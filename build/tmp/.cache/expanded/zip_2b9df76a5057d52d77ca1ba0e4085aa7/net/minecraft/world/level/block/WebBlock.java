package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WebBlock extends Block implements net.minecraftforge.common.IForgeShearable {
    public static final MapCodec<WebBlock> f_303327_ = m_306223_(WebBlock::new);

    @Override
    public MapCodec<WebBlock> m_304657_() {
        return f_303327_;
    }

    public WebBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        Vec3 vec3 = new Vec3(0.25, 0.05F, 0.25);
        if (pEntity instanceof LivingEntity livingentity && livingentity.hasEffect(MobEffects.f_315811_)) {
            vec3 = new Vec3(0.5, 0.25, 0.5);
        }

        pEntity.makeStuckInBlock(pState, vec3);
    }
}
