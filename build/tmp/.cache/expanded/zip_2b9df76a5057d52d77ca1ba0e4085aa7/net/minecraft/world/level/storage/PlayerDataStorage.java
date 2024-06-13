package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class PlayerDataStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final File playerDir;
    protected final DataFixer fixerUpper;
    private static final DateTimeFormatter f_315551_ = FileNameDateFormatter.m_323179_();

    public PlayerDataStorage(LevelStorageSource.LevelStorageAccess pLevelStorageAccess, DataFixer pFixerUpper) {
        this.fixerUpper = pFixerUpper;
        this.playerDir = pLevelStorageAccess.getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
        this.playerDir.mkdirs();
    }

    public void save(Player pPlayer) {
        try {
            CompoundTag compoundtag = pPlayer.saveWithoutId(new CompoundTag());
            Path path = this.playerDir.toPath();
            Path path1 = Files.createTempFile(path, pPlayer.getStringUUID() + "-", ".dat");
            NbtIo.writeCompressed(compoundtag, path1);
            Path path2 = path.resolve(pPlayer.getStringUUID() + ".dat");
            Path path3 = path.resolve(pPlayer.getStringUUID() + ".dat_old");
            Util.safeReplaceFile(path2, path1, path3);
            net.minecraftforge.event.ForgeEventFactory.firePlayerSavingEvent(pPlayer, playerDir, pPlayer.getStringUUID());
        } catch (Exception exception) {
            LOGGER.warn("Failed to save player data for {}", pPlayer.getName().getString());
        }
    }

    private void m_323775_(Player p_331737_, String p_336359_) {
        Path path = this.playerDir.toPath();
        Path path1 = path.resolve(p_331737_.getStringUUID() + p_336359_);
        Path path2 = path.resolve(p_331737_.getStringUUID() + "_corrupted_" + LocalDateTime.now().format(f_315551_) + p_336359_);
        if (Files.isRegularFile(path1)) {
            try {
                Files.copy(path1, path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            } catch (Exception exception) {
                LOGGER.warn("Failed to copy the player.dat file for {}", p_331737_.getName().getString(), exception);
            }
        }
    }

    private Optional<CompoundTag> m_323516_(Player p_329651_, String p_330353_) {
        File file1 = new File(this.playerDir, p_329651_.getStringUUID() + p_330353_);
        if (file1.exists() && file1.isFile()) {
            try {
                var ret = Optional.of(NbtIo.readCompressed(file1.toPath(), NbtAccounter.unlimitedHeap()));
                net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(p_329651_, playerDir, p_329651_.getStringUUID());
                return ret;
            } catch (Exception exception) {
                LOGGER.warn("Failed to load player data for {}", p_329651_.getName().getString());
            }
        }

        return Optional.empty();
    }

    public Optional<CompoundTag> load(Player pPlayer) {
        Optional<CompoundTag> optional = this.m_323516_(pPlayer, ".dat");
        if (optional.isEmpty()) {
            this.m_323775_(pPlayer, ".dat");
        }

        return optional.or(() -> this.m_323516_(pPlayer, ".dat_old")).map(p_328937_ -> {
            int i = NbtUtils.getDataVersion(p_328937_, -1);
            p_328937_ = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, p_328937_, i);
            pPlayer.load(p_328937_);
            return p_328937_;
        });
    }

    public File getPlayerDataFolder() {
        return playerDir;
    }
}
