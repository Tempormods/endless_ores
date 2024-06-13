package net.minecraft.client.multiplayer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LevelLoadStatusManager {
    private final LocalPlayer f_303813_;
    private final ClientLevel f_302732_;
    private final LevelRenderer f_303268_;
    private LevelLoadStatusManager.Status f_302326_ = LevelLoadStatusManager.Status.WAITING_FOR_SERVER;

    public LevelLoadStatusManager(LocalPlayer p_312813_, ClientLevel p_310113_, LevelRenderer p_311686_) {
        this.f_303813_ = p_312813_;
        this.f_302732_ = p_310113_;
        this.f_303268_ = p_311686_;
    }

    public void m_304812_() {
        switch (this.f_302326_) {
            case WAITING_FOR_PLAYER_CHUNK:
                BlockPos blockpos = this.f_303813_.blockPosition();
                boolean flag = this.f_302732_.isOutsideBuildHeight(blockpos.getY());
                if (flag || this.f_303268_.isSectionCompiled(blockpos) || this.f_303813_.isSpectator() || !this.f_303813_.isAlive()) {
                    this.f_302326_ = LevelLoadStatusManager.Status.LEVEL_READY;
                }
            case WAITING_FOR_SERVER:
            case LEVEL_READY:
        }
    }

    public boolean m_305903_() {
        return this.f_302326_ == LevelLoadStatusManager.Status.LEVEL_READY;
    }

    public void m_304720_() {
        if (this.f_302326_ == LevelLoadStatusManager.Status.WAITING_FOR_SERVER) {
            this.f_302326_ = LevelLoadStatusManager.Status.WAITING_FOR_PLAYER_CHUNK;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum Status {
        WAITING_FOR_SERVER,
        WAITING_FOR_PLAYER_CHUNK,
        LEVEL_READY;
    }
}