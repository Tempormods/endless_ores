package net.minecraft.world.level.storage;

import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import org.apache.commons.lang3.StringUtils;

public class LevelSummary implements Comparable<LevelSummary> {
    public static final Component f_303340_ = Component.translatable("selectWorld.select");
    private final LevelSettings settings;
    private final LevelVersion levelVersion;
    private final String levelId;
    private final boolean requiresManualConversion;
    private final boolean locked;
    private final boolean experimental;
    private final Path icon;
    @Nullable
    private Component info;

    public LevelSummary(
        LevelSettings pSettings, LevelVersion pLevelVersion, String pLevelId, boolean pRequiresManualConversion, boolean pLocked, boolean pExperimental, Path pIcon
    ) {
        this.settings = pSettings;
        this.levelVersion = pLevelVersion;
        this.levelId = pLevelId;
        this.locked = pLocked;
        this.experimental = pExperimental;
        this.icon = pIcon;
        this.requiresManualConversion = pRequiresManualConversion;
    }

    public String getLevelId() {
        return this.levelId;
    }

    public String getLevelName() {
        return StringUtils.isEmpty(this.settings.levelName()) ? this.levelId : this.settings.levelName();
    }

    public Path getIcon() {
        return this.icon;
    }

    public boolean requiresManualConversion() {
        return this.requiresManualConversion;
    }

    public boolean isExperimental() {
        return this.experimental;
    }

    public long getLastPlayed() {
        return this.levelVersion.lastPlayed();
    }

    public int compareTo(LevelSummary pOther) {
        if (this.getLastPlayed() < pOther.getLastPlayed()) {
            return 1;
        } else {
            return this.getLastPlayed() > pOther.getLastPlayed() ? -1 : this.levelId.compareTo(pOther.levelId);
        }
    }

    public LevelSettings getSettings() {
        return this.settings;
    }

    public GameType getGameMode() {
        return this.settings.gameType();
    }

    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    public boolean hasCheats() {
        return this.settings.allowCommands();
    }

    public MutableComponent getWorldVersionName() {
        return StringUtil.isNullOrEmpty(this.levelVersion.minecraftVersionName())
            ? Component.translatable("selectWorld.versionUnknown")
            : Component.literal(this.levelVersion.minecraftVersionName());
    }

    public LevelVersion levelVersion() {
        return this.levelVersion;
    }

    public boolean m_306713_() {
        return this.backupStatus().shouldBackup();
    }

    public boolean m_305825_() {
        return this.backupStatus() == LevelSummary.BackupStatus.DOWNGRADE;
    }

    public LevelSummary.BackupStatus backupStatus() {
        WorldVersion worldversion = SharedConstants.getCurrentVersion();
        int i = worldversion.getDataVersion().getVersion();
        int j = this.levelVersion.minecraftVersion().getVersion();
        if (!worldversion.isStable() && j < i) {
            return LevelSummary.BackupStatus.UPGRADE_TO_SNAPSHOT;
        } else {
            return j > i ? LevelSummary.BackupStatus.DOWNGRADE : LevelSummary.BackupStatus.NONE;
        }
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isDisabled() {
        return !this.isLocked() && !this.requiresManualConversion() ? !this.isCompatible() : true;
    }

    public boolean isCompatible() {
        return SharedConstants.getCurrentVersion().getDataVersion().isCompatible(this.levelVersion.minecraftVersion());
    }

    public Component getInfo() {
        if (this.info == null) {
            this.info = this.createInfo();
        }

        return this.info;
    }

    private Component createInfo() {
        if (this.isLocked()) {
            return Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
        } else if (this.requiresManualConversion()) {
            return Component.translatable("selectWorld.conversion").withStyle(ChatFormatting.RED);
        } else if (!this.isCompatible()) {
            return Component.translatable("selectWorld.incompatible.info", this.getWorldVersionName()).withStyle(ChatFormatting.RED);
        } else {
            MutableComponent mutablecomponent = this.isHardcore()
                ? Component.empty().append(Component.translatable("gameMode.hardcore").m_306658_(-65536))
                : Component.translatable("gameMode." + this.getGameMode().getName());
            if (this.hasCheats()) {
                mutablecomponent.append(", ").append(Component.translatable("selectWorld.commands"));
            }

            if (this.isExperimental()) {
                mutablecomponent.append(", ").append(Component.translatable("selectWorld.experimental").withStyle(ChatFormatting.YELLOW));
            }

            MutableComponent mutablecomponent1 = this.getWorldVersionName();
            MutableComponent mutablecomponent2 = Component.literal(", ")
                .append(Component.translatable("selectWorld.version"))
                .append(CommonComponents.SPACE);
            if (this.m_306713_()) {
                mutablecomponent2.append(mutablecomponent1.withStyle(this.m_305825_() ? ChatFormatting.RED : ChatFormatting.ITALIC));
            } else {
                mutablecomponent2.append(mutablecomponent1);
            }

            mutablecomponent.append(mutablecomponent2);
            return mutablecomponent;
        }
    }

    public Component m_304777_() {
        return f_303340_;
    }

    public boolean m_305960_() {
        return !this.isDisabled();
    }

    public boolean m_322465_() {
        return !this.requiresManualConversion() && !this.isLocked();
    }

    public boolean m_305680_() {
        return !this.isDisabled();
    }

    public boolean m_306795_() {
        return !this.isDisabled();
    }

    public boolean m_305615_() {
        return true;
    }

    public static enum BackupStatus {
        NONE(false, false, ""),
        DOWNGRADE(true, true, "downgrade"),
        UPGRADE_TO_SNAPSHOT(true, false, "snapshot");

        private final boolean shouldBackup;
        private final boolean severe;
        private final String translationKey;

        private BackupStatus(final boolean pShouldBackup, final boolean pSevere, final String pTranslationKey) {
            this.shouldBackup = pShouldBackup;
            this.severe = pSevere;
            this.translationKey = pTranslationKey;
        }

        public boolean shouldBackup() {
            return this.shouldBackup;
        }

        public boolean isSevere() {
            return this.severe;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }
    }

    public static class CorruptedLevelSummary extends LevelSummary {
        private static final Component f_303814_ = Component.translatable("recover_world.warning").withStyle(p_309622_ -> p_309622_.withColor(-65536));
        private static final Component f_302348_ = Component.translatable("recover_world.button");
        private final long f_302803_;

        public CorruptedLevelSummary(String p_313183_, Path p_310684_, long p_312803_) {
            super(null, null, p_313183_, false, false, false, p_310684_);
            this.f_302803_ = p_312803_;
        }

        @Override
        public String getLevelName() {
            return this.getLevelId();
        }

        @Override
        public Component getInfo() {
            return f_303814_;
        }

        @Override
        public long getLastPlayed() {
            return this.f_302803_;
        }

        @Override
        public boolean isDisabled() {
            return false;
        }

        @Override
        public Component m_304777_() {
            return f_302348_;
        }

        @Override
        public boolean m_305960_() {
            return true;
        }

        @Override
        public boolean m_322465_() {
            return false;
        }

        @Override
        public boolean m_305680_() {
            return false;
        }

        @Override
        public boolean m_306795_() {
            return false;
        }
    }

    public boolean isLifecycleExperimental() {
        return this.settings.getLifecycle().equals(com.mojang.serialization.Lifecycle.experimental());
    }

    public static class SymlinkLevelSummary extends LevelSummary {
        private static final Component f_303689_ = Component.translatable("symlink_warning.more_info");
        private static final Component f_302262_ = Component.translatable("symlink_warning.title").m_306658_(-65536);

        public SymlinkLevelSummary(String pLevelId, Path pIcon) {
            super(null, null, pLevelId, false, false, false, pIcon);
        }

        @Override
        public String getLevelName() {
            return this.getLevelId();
        }

        @Override
        public Component getInfo() {
            return f_302262_;
        }

        @Override
        public long getLastPlayed() {
            return -1L;
        }

        @Override
        public boolean isDisabled() {
            return false;
        }

        @Override
        public Component m_304777_() {
            return f_303689_;
        }

        @Override
        public boolean m_305960_() {
            return true;
        }

        @Override
        public boolean m_322465_() {
            return false;
        }

        @Override
        public boolean m_305680_() {
            return false;
        }

        @Override
        public boolean m_306795_() {
            return false;
        }
    }
}
