package net.minecraft.client;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import java.nio.file.Path;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class HotbarManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int NUM_HOTBAR_GROUPS = 9;
    private final Path optionsFile;
    private final DataFixer fixerUpper;
    private final Hotbar[] hotbars = new Hotbar[9];
    private boolean loaded;

    public HotbarManager(Path p_311778_, DataFixer pFixerUpper) {
        this.optionsFile = p_311778_.resolve("hotbar.nbt");
        this.fixerUpper = pFixerUpper;

        for (int i = 0; i < 9; i++) {
            this.hotbars[i] = new Hotbar();
        }
    }

    private void load() {
        try {
            CompoundTag compoundtag = NbtIo.read(this.optionsFile);
            if (compoundtag == null) {
                return;
            }

            int i = NbtUtils.getDataVersion(compoundtag, 1343);
            compoundtag = DataFixTypes.HOTBAR.updateToCurrentVersion(this.fixerUpper, compoundtag, i);

            for (int j = 0; j < 9; j++) {
                this.hotbars[j] = Hotbar.f_316903_
                    .parse(NbtOps.INSTANCE, compoundtag.get(String.valueOf(j)))
                    .resultOrPartial(p_329426_ -> LOGGER.warn("Failed to parse hotbar: {}", p_329426_))
                    .orElseGet(Hotbar::new);
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to load creative mode options", (Throwable)exception);
        }
    }

    public void save() {
        try {
            CompoundTag compoundtag = NbtUtils.addCurrentDataVersion(new CompoundTag());

            for (int i = 0; i < 9; i++) {
                Hotbar hotbar = this.get(i);
                DataResult<Tag> dataresult = Hotbar.f_316903_.encodeStart(NbtOps.INSTANCE, hotbar);
                compoundtag.put(String.valueOf(i), dataresult.getOrThrow());
            }

            NbtIo.write(compoundtag, this.optionsFile);
        } catch (Exception exception) {
            LOGGER.error("Failed to save creative mode options", (Throwable)exception);
        }
    }

    public Hotbar get(int pIndex) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }

        return this.hotbars[pIndex];
    }
}