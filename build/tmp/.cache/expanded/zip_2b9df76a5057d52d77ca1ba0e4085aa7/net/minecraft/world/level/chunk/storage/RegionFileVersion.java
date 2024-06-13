package net.minecraft.world.level.chunk.storage;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.minecraft.util.FastBufferedInputStream;
import org.slf4j.Logger;

/**
 * A decorator for input and output streams used to read and write the chunk data from region files. This exists as
 * there are different ways of compressing the chunk data inside a region file.
 * @see net.minecraft.world.level.chunk.storage.RegionFileVersion#VERSION_GZIP
 * @see net.minecraft.world.level.chunk.storage.RegionFileVersion#VERSION_DEFLATE
 * @see net.minecraft.world.level.chunk.storage.RegionFileVersion#VERSION_NONE
 */
public class RegionFileVersion {
    private static final Logger f_316092_ = LogUtils.getLogger();
    private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<String, RegionFileVersion> f_316236_ = new Object2ObjectOpenHashMap<>();
    public static final RegionFileVersion VERSION_GZIP = register(
        new RegionFileVersion(
            1,
            null,
            p_63767_ -> new FastBufferedInputStream(new GZIPInputStream(p_63767_)),
            p_63769_ -> new BufferedOutputStream(new GZIPOutputStream(p_63769_))
        )
    );
    public static final RegionFileVersion VERSION_DEFLATE = register(
        new RegionFileVersion(
            2,
            "deflate",
            p_196964_ -> new FastBufferedInputStream(new InflaterInputStream(p_196964_)),
            p_196966_ -> new BufferedOutputStream(new DeflaterOutputStream(p_196966_))
        )
    );
    public static final RegionFileVersion VERSION_NONE = register(new RegionFileVersion(3, "none", FastBufferedInputStream::new, BufferedOutputStream::new));
    public static final RegionFileVersion f_316033_ = register(
        new RegionFileVersion(
            4,
            "lz4",
            p_327422_ -> new FastBufferedInputStream(new LZ4BlockInputStream(p_327422_)),
            p_327421_ -> new BufferedOutputStream(new LZ4BlockOutputStream(p_327421_))
        )
    );
    public static final RegionFileVersion f_315662_ = register(new RegionFileVersion(127, null, p_327423_ -> {
        throw new UnsupportedOperationException();
    }, p_327424_ -> {
        throw new UnsupportedOperationException();
    }));
    public static final RegionFileVersion f_315183_ = VERSION_DEFLATE;
    private static volatile RegionFileVersion f_314639_ = f_315183_;
    private final int id;
    @Nullable
    private final String f_316088_;
    private final RegionFileVersion.StreamWrapper<InputStream> inputWrapper;
    private final RegionFileVersion.StreamWrapper<OutputStream> outputWrapper;

    private RegionFileVersion(
        int pId, @Nullable String p_336103_, RegionFileVersion.StreamWrapper<InputStream> pInputWrapper, RegionFileVersion.StreamWrapper<OutputStream> pOutputWrapper
    ) {
        this.id = pId;
        this.f_316088_ = p_336103_;
        this.inputWrapper = pInputWrapper;
        this.outputWrapper = pOutputWrapper;
    }

    private static RegionFileVersion register(RegionFileVersion pFileVersion) {
        VERSIONS.put(pFileVersion.id, pFileVersion);
        if (pFileVersion.f_316088_ != null) {
            f_316236_.put(pFileVersion.f_316088_, pFileVersion);
        }

        return pFileVersion;
    }

    @Nullable
    public static RegionFileVersion fromId(int pId) {
        return VERSIONS.get(pId);
    }

    public static void m_324113_(String p_335730_) {
        RegionFileVersion regionfileversion = f_316236_.get(p_335730_);
        if (regionfileversion != null) {
            f_314639_ = regionfileversion;
        } else {
            f_316092_.error(
                "Invalid `region-file-compression` value `{}` in server.properties. Please use one of: {}", p_335730_, String.join(", ", f_316236_.keySet())
            );
        }
    }

    public static RegionFileVersion m_321044_() {
        return f_314639_;
    }

    public static boolean isValidVersion(int pId) {
        return VERSIONS.containsKey(pId);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream pOutputStream) throws IOException {
        return this.outputWrapper.wrap(pOutputStream);
    }

    public InputStream wrap(InputStream pInputStream) throws IOException {
        return this.inputWrapper.wrap(pInputStream);
    }

    @FunctionalInterface
    interface StreamWrapper<O> {
        O wrap(O pStream) throws IOException;
    }
}