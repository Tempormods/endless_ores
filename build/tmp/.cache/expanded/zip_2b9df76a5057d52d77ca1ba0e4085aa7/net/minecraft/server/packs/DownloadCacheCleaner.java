package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

public class DownloadCacheCleaner {
    private static final Logger f_303196_ = LogUtils.getLogger();

    public static void m_307501_(Path p_311487_, int p_312653_) {
        try {
            List<DownloadCacheCleaner.PathAndTime> list = m_305409_(p_311487_);
            int i = list.size() - p_312653_;
            if (i <= 0) {
                return;
            }

            list.sort(DownloadCacheCleaner.PathAndTime.f_302218_);
            List<DownloadCacheCleaner.PathAndPriority> list1 = m_305891_(list);
            Collections.reverse(list1);
            list1.sort(DownloadCacheCleaner.PathAndPriority.f_302746_);
            Set<Path> set = new HashSet<>();

            for (int j = 0; j < i; j++) {
                DownloadCacheCleaner.PathAndPriority downloadcachecleaner$pathandpriority = list1.get(j);
                Path path = downloadcachecleaner$pathandpriority.f_302336_;

                try {
                    Files.delete(path);
                    if (downloadcachecleaner$pathandpriority.f_303116_ == 0) {
                        set.add(path.getParent());
                    }
                } catch (IOException ioexception1) {
                    f_303196_.warn("Failed to delete cache file {}", path, ioexception1);
                }
            }

            set.remove(p_311487_);

            for (Path path1 : set) {
                try {
                    Files.delete(path1);
                } catch (DirectoryNotEmptyException directorynotemptyexception) {
                } catch (IOException ioexception) {
                    f_303196_.warn("Failed to delete empty(?) cache directory {}", path1, ioexception);
                }
            }
        } catch (UncheckedIOException | IOException ioexception2) {
            f_303196_.error("Failed to vacuum cache dir {}", p_311487_, ioexception2);
        }
    }

    private static List<DownloadCacheCleaner.PathAndTime> m_305409_(final Path p_311706_) throws IOException {
        try {
            final List<DownloadCacheCleaner.PathAndTime> list = new ArrayList<>();
            Files.walkFileTree(p_311706_, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path p_312027_, BasicFileAttributes p_309596_) {
                    if (p_309596_.isRegularFile() && !p_312027_.getParent().equals(p_311706_)) {
                        FileTime filetime = p_309596_.lastModifiedTime();
                        list.add(new DownloadCacheCleaner.PathAndTime(p_312027_, filetime));
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
            return list;
        } catch (NoSuchFileException nosuchfileexception) {
            return List.of();
        }
    }

    private static List<DownloadCacheCleaner.PathAndPriority> m_305891_(List<DownloadCacheCleaner.PathAndTime> p_312641_) {
        List<DownloadCacheCleaner.PathAndPriority> list = new ArrayList<>();
        Object2IntOpenHashMap<Path> object2intopenhashmap = new Object2IntOpenHashMap<>();

        for (DownloadCacheCleaner.PathAndTime downloadcachecleaner$pathandtime : p_312641_) {
            int i = object2intopenhashmap.addTo(downloadcachecleaner$pathandtime.f_303548_.getParent(), 1);
            list.add(new DownloadCacheCleaner.PathAndPriority(downloadcachecleaner$pathandtime.f_303548_, i));
        }

        return list;
    }

    static record PathAndPriority(Path f_302336_, int f_303116_) {
        public static final Comparator<DownloadCacheCleaner.PathAndPriority> f_302746_ = Comparator.comparing(DownloadCacheCleaner.PathAndPriority::f_303116_)
            .reversed();
    }

    static record PathAndTime(Path f_303548_, FileTime f_303326_) {
        public static final Comparator<DownloadCacheCleaner.PathAndTime> f_302218_ = Comparator.comparing(DownloadCacheCleaner.PathAndTime::f_303326_)
            .reversed();
    }
}