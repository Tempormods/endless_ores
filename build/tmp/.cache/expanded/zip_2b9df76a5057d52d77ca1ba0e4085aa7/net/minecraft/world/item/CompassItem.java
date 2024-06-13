package net.minecraft.world.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CompassItem extends Item {
    public CompassItem(Item.Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    public static GlobalPos getSpawnPosition(Level pLevel) {
        return pLevel.dimensionType().natural() ? GlobalPos.of(pLevel.dimension(), pLevel.getSharedSpawnPos()) : null;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.m_319951_(DataComponents.f_314784_) || super.isFoil(pStack);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pItemSlot, boolean pIsSelected) {
        if (pLevel instanceof ServerLevel serverlevel) {
            LodestoneTracker lodestonetracker = pStack.m_323252_(DataComponents.f_314784_);
            if (lodestonetracker != null) {
                LodestoneTracker lodestonetracker1 = lodestonetracker.m_321939_(serverlevel);
                if (lodestonetracker1 != lodestonetracker) {
                    pStack.m_322496_(DataComponents.f_314784_, lodestonetracker1);
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if (!level.getBlockState(blockpos).is(Blocks.LODESTONE)) {
            return super.useOn(pContext);
        } else {
            level.playSound(null, blockpos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
            Player player = pContext.getPlayer();
            ItemStack itemstack = pContext.getItemInHand();
            boolean flag = !player.m_322042_() && itemstack.getCount() == 1;
            LodestoneTracker lodestonetracker = new LodestoneTracker(Optional.of(GlobalPos.of(level.dimension(), blockpos)), true);
            if (flag) {
                itemstack.m_322496_(DataComponents.f_314784_, lodestonetracker);
            } else {
                ItemStack itemstack1 = itemstack.m_319323_(Items.COMPASS, 1);
                itemstack.m_321439_(1, player);
                itemstack1.m_322496_(DataComponents.f_314784_, lodestonetracker);
                if (!player.getInventory().add(itemstack1)) {
                    player.drop(itemstack1, false);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        return pStack.m_319951_(DataComponents.f_314784_) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(pStack);
    }
}