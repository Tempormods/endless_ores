package net.minecraft.util;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;
import java.util.OptionalLong;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HttpUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    private HttpUtil() {
    }

    public static Path m_305661_(
        Path p_312337_,
        URL p_309490_,
        Map<String, String> p_311545_,
        HashFunction p_312368_,
        @Nullable HashCode p_309569_,
        int p_312993_,
        Proxy p_311636_,
        HttpUtil.DownloadProgressListener p_310347_
    ) {
        HttpURLConnection httpurlconnection = null;
        InputStream inputstream = null;
        p_310347_.m_305410_();
        Path path;
        if (p_309569_ != null) {
            path = m_306073_(p_312337_, p_309569_);

            try {
                if (m_306783_(path, p_312368_, p_309569_)) {
                    LOGGER.info("Returning cached file since actual hash matches requested");
                    p_310347_.m_304846_(true);
                    m_305114_(path);
                    return path;
                }
            } catch (IOException ioexception1) {
                LOGGER.warn("Failed to check cached file {}", path, ioexception1);
            }

            try {
                LOGGER.warn("Existing file {} not found or had mismatched hash", path);
                Files.deleteIfExists(path);
            } catch (IOException ioexception) {
                p_310347_.m_304846_(false);
                throw new UncheckedIOException("Failed to remove existing file " + path, ioexception);
            }
        } else {
            path = null;
        }

        Path $$18;
        try {
            httpurlconnection = (HttpURLConnection)p_309490_.openConnection(p_311636_);
            httpurlconnection.setInstanceFollowRedirects(true);
            p_311545_.forEach(httpurlconnection::setRequestProperty);
            inputstream = httpurlconnection.getInputStream();
            long i = httpurlconnection.getContentLengthLong();
            OptionalLong optionallong = i != -1L ? OptionalLong.of(i) : OptionalLong.empty();
            FileUtil.createDirectoriesSafe(p_312337_);
            p_310347_.m_306050_(optionallong);
            if (optionallong.isPresent() && optionallong.getAsLong() > (long)p_312993_) {
                throw new IOException("Filesize is bigger than maximum allowed (file is " + optionallong + ", limit is " + p_312993_ + ")");
            }

            if (path == null) {
                Path path3 = Files.createTempFile(p_312337_, "download", ".tmp");

                try {
                    HashCode hashcode1 = m_306496_(p_312368_, p_312993_, p_310347_, inputstream, path3);
                    Path path2 = m_306073_(p_312337_, hashcode1);
                    if (!m_306783_(path2, p_312368_, hashcode1)) {
                        Files.move(path3, path2, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        m_305114_(path2);
                    }

                    p_310347_.m_304846_(true);
                    return path2;
                } finally {
                    Files.deleteIfExists(path3);
                }
            }

            HashCode hashcode = m_306496_(p_312368_, p_312993_, p_310347_, inputstream, path);
            if (!hashcode.equals(p_309569_)) {
                throw new IOException("Hash of downloaded file (" + hashcode + ") did not match requested (" + p_309569_ + ")");
            }

            p_310347_.m_304846_(true);
            $$18 = path;
        } catch (Throwable throwable) {
            if (httpurlconnection != null) {
                InputStream inputstream1 = httpurlconnection.getErrorStream();
                if (inputstream1 != null) {
                    try {
                        LOGGER.error("HTTP response error: {}", IOUtils.toString(inputstream1, StandardCharsets.UTF_8));
                    } catch (Exception exception) {
                        LOGGER.error("Failed to read response from server");
                    }
                }
            }

            p_310347_.m_304846_(false);
            throw new IllegalStateException("Failed to download file " + p_309490_, throwable);
        } finally {
            IOUtils.closeQuietly(inputstream);
        }

        return $$18;
    }

    private static void m_305114_(Path p_311353_) {
        try {
            Files.setLastModifiedTime(p_311353_, FileTime.from(Instant.now()));
        } catch (IOException ioexception) {
            LOGGER.warn("Failed to update modification time of {}", p_311353_, ioexception);
        }
    }

    private static HashCode m_307527_(Path p_310985_, HashFunction p_312320_) throws IOException {
        Hasher hasher = p_312320_.newHasher();

        try (
            OutputStream outputstream = Funnels.asOutputStream(hasher);
            InputStream inputstream = Files.newInputStream(p_310985_);
        ) {
            inputstream.transferTo(outputstream);
        }

        return hasher.hash();
    }

    private static boolean m_306783_(Path p_309713_, HashFunction p_311423_, HashCode p_312149_) throws IOException {
        if (Files.exists(p_309713_)) {
            HashCode hashcode = m_307527_(p_309713_, p_311423_);
            if (hashcode.equals(p_312149_)) {
                return true;
            }

            LOGGER.warn("Mismatched hash of file {}, expected {} but found {}", p_309713_, p_312149_, hashcode);
        }

        return false;
    }

    private static Path m_306073_(Path p_310769_, HashCode p_311855_) {
        return p_310769_.resolve(p_311855_.toString());
    }

    private static HashCode m_306496_(HashFunction p_312168_, int p_311506_, HttpUtil.DownloadProgressListener p_311732_, InputStream p_312120_, Path p_310124_) throws IOException {
        HashCode hashcode;
        try (OutputStream outputstream = Files.newOutputStream(p_310124_, StandardOpenOption.CREATE)) {
            Hasher hasher = p_312168_.newHasher();
            byte[] abyte = new byte[8196];
            long j = 0L;

            int i;
            while ((i = p_312120_.read(abyte)) >= 0) {
                j += (long)i;
                p_311732_.m_305341_(j);
                if (j > (long)p_311506_) {
                    throw new IOException("Filesize was bigger than maximum allowed (got >= " + j + ", limit was " + p_311506_ + ")");
                }

                if (Thread.interrupted()) {
                    LOGGER.error("INTERRUPTED");
                    throw new IOException("Download interrupted");
                }

                outputstream.write(abyte, 0, i);
                hasher.putBytes(abyte, 0, i);
            }

            hashcode = hasher.hash();
        }

        return hashcode;
    }

    public static int getAvailablePort() {
        try {
            int i;
            try (ServerSocket serversocket = new ServerSocket(0)) {
                i = serversocket.getLocalPort();
            }

            return i;
        } catch (IOException ioexception) {
            return 25564;
        }
    }

    public static boolean isPortAvailable(int pPort) {
        if (pPort >= 0 && pPort <= 65535) {
            try {
                boolean flag;
                try (ServerSocket serversocket = new ServerSocket(pPort)) {
                    flag = serversocket.getLocalPort() == pPort;
                }

                return flag;
            } catch (IOException ioexception) {
                return false;
            }
        } else {
            return false;
        }
    }

    public interface DownloadProgressListener {
        void m_305410_();

        void m_306050_(OptionalLong p_311723_);

        void m_305341_(long p_309797_);

        void m_304846_(boolean p_311898_);
    }
}