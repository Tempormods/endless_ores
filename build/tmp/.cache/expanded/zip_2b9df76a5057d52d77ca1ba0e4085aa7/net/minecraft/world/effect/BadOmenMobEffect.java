package net.minecraft.world.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.flag.FeatureFlags;

class BadOmenMobEffect extends MobEffect {
    protected BadOmenMobEffect(MobEffectCategory p_298574_, int p_301000_) {
        super(p_298574_, p_301000_);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int p_297444_, int p_300866_) {
        return true;
    }

    @Override
    public boolean m_294194_(LivingEntity p_299568_, int p_299125_) {
        if (p_299568_ instanceof ServerPlayer serverplayer && !serverplayer.isSpectator()) {
            ServerLevel serverlevel = serverplayer.serverLevel();
            if (!serverlevel.enabledFeatures().contains(FeatureFlags.f_302467_)) {
                return this.m_320456_(serverplayer, serverlevel);
            }

            if (serverlevel.getDifficulty() != Difficulty.PEACEFUL && serverlevel.isVillage(serverplayer.blockPosition())) {
                Raid raid = serverlevel.getRaidAt(serverplayer.blockPosition());
                if (raid == null || raid.m_322744_() < raid.getMaxBadOmenLevel()) {
                    serverplayer.addEffect(new MobEffectInstance(MobEffects.f_314945_, 600, p_299125_));
                    serverplayer.m_322512_(serverplayer.blockPosition());
                    return false;
                }
            }
        }

        return true;
    }

    private boolean m_320456_(ServerPlayer p_335869_, ServerLevel p_336391_) {
        BlockPos blockpos = p_335869_.blockPosition();
        return p_336391_.getDifficulty() != Difficulty.PEACEFUL && p_336391_.isVillage(blockpos) ? p_336391_.getRaids().createOrExtendRaid(p_335869_, blockpos) == null : true;
    }
}