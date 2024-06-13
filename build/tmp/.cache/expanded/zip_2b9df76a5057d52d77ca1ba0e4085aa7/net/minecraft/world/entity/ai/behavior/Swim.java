package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;

public class Swim extends Behavior<Mob> {
    private final float chance;

    public Swim(float pChance) {
        super(ImmutableMap.of());
        this.chance = pChance;
    }

    public static boolean m_319678_(Mob p_327994_) {
        return p_327994_.isInWater() &&p_327994_.getFluidHeight(FluidTags.WATER) > p_327994_.getFluidJumpThreshold() || p_327994_.isInLava() || p_327994_.isInFluidType((fluidType, height) -> p_327994_.canSwimInFluidType(fluidType) && height > p_327994_.getFluidJumpThreshold());
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, Mob pOwner) {
        return m_319678_(pOwner);
    }

    protected boolean canStillUse(ServerLevel pLevel, Mob pEntity, long pGameTime) {
        return this.checkExtraStartConditions(pLevel, pEntity);
    }

    protected void tick(ServerLevel pLevel, Mob pOwner, long pGameTime) {
        if (pOwner.getRandom().nextFloat() < this.chance) {
            pOwner.getJumpControl().jump();
        }
    }
}
