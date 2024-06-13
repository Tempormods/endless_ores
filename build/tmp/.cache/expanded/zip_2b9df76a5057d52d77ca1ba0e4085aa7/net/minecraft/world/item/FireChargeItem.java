package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FireChargeItem extends Item implements ProjectileItem {
    public FireChargeItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        boolean flag = false;
        if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
            blockpos = blockpos.relative(pContext.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(level, blockpos, pContext.getHorizontalDirection())) {
                this.playSound(level, blockpos);
                level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(level, blockpos));
                level.gameEvent(pContext.getPlayer(), GameEvent.BLOCK_PLACE, blockpos);
                flag = true;
            }
        } else {
            this.playSound(level, blockpos);
            level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
            level.gameEvent(pContext.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
            flag = true;
        }

        if (flag) {
            pContext.getItemInHand().shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }

    private void playSound(Level pLevel, BlockPos pPos) {
        RandomSource randomsource = pLevel.getRandom();
        pLevel.playSound(null, pPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomsource.nextFloat() - randomsource.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Projectile m_319847_(Level p_333696_, Position p_332623_, ItemStack p_335300_, Direction p_332824_) {
        RandomSource randomsource = p_333696_.getRandom();
        double d0 = randomsource.triangle((double)p_332824_.getStepX(), 0.11485000000000001);
        double d1 = randomsource.triangle((double)p_332824_.getStepY(), 0.11485000000000001);
        double d2 = randomsource.triangle((double)p_332824_.getStepZ(), 0.11485000000000001);
        SmallFireball smallfireball = new SmallFireball(p_333696_, p_332623_.x(), p_332623_.y(), p_332623_.z(), d0, d1, d2);
        smallfireball.setItem(p_335300_);
        return smallfireball;
    }

    @Override
    public void m_319015_(Projectile p_333684_, double p_331158_, double p_330156_, double p_328098_, float p_334367_, float p_329865_) {
    }

    @Override
    public ProjectileItem.DispenseConfig m_320420_() {
        return ProjectileItem.DispenseConfig.m_321505_()
            .m_321513_((p_334997_, p_333408_) -> DispenserBlock.m_321992_(p_334997_, 1.0, Vec3.ZERO))
            .m_324742_(6.6666665F)
            .m_318910_(1.0F)
            .m_323513_(1018)
            .m_321407_();
    }
}