package net.minecraft.world.item.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment extends Enchantment {
    public FrostWalkerEnchantment(Enchantment.EnchantmentDefinition p_330164_) {
        super(p_330164_);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    public static void onEntityMoved(LivingEntity pLiving, Level pLevel, BlockPos pPos, int pLevelConflicting) {
        if (pLiving.onGround()) {
            BlockState blockstate = Blocks.FROSTED_ICE.defaultBlockState();
            int i = Math.min(16, 2 + pLevelConflicting);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-i, -1, -i), pPos.offset(i, -1, i))) {
                if (blockpos.closerToCenterThan(pLiving.position(), (double)i)) {
                    blockpos$mutableblockpos.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                    BlockState blockstate1 = pLevel.getBlockState(blockpos$mutableblockpos);
                    if (blockstate1.isAir()) {
                        BlockState blockstate2 = pLevel.getBlockState(blockpos);
                        if (blockstate2 == FrostedIceBlock.meltsInto()
                                && blockstate.canSurvive(pLevel, blockpos)
                                && pLevel.isUnobstructed(blockstate, blockpos, CollisionContext.empty())
                                && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(pLiving, net.minecraftforge.common.util.BlockSnapshot.create(pLevel.dimension(), pLevel, blockpos), net.minecraft.core.Direction.UP)) {
                            pLevel.setBlockAndUpdate(blockpos, blockstate);
                            pLevel.scheduleTick(blockpos, Blocks.FROSTED_ICE, Mth.nextInt(pLiving.getRandom(), 60, 120));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean checkCompatibility(Enchantment pEnch) {
        return super.checkCompatibility(pEnch) && pEnch != Enchantments.DEPTH_STRIDER;
    }
}
