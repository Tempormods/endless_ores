package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemUtils {
    public static InteractionResultHolder<ItemStack> startUsingInstantly(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
    }

    public static ItemStack createFilledResult(ItemStack pEmptyStack, Player pPlayer, ItemStack pFilledStack, boolean pPreventDuplicates) {
        boolean flag = pPlayer.m_322042_();
        if (pPreventDuplicates && flag) {
            if (!pPlayer.getInventory().contains(pFilledStack)) {
                pPlayer.getInventory().add(pFilledStack);
            }

            return pEmptyStack;
        } else {
            pEmptyStack.m_321439_(1, pPlayer);
            if (pEmptyStack.isEmpty()) {
                return pFilledStack;
            } else {
                if (!pPlayer.getInventory().add(pFilledStack)) {
                    pPlayer.drop(pFilledStack, false);
                }

                return pEmptyStack;
            }
        }
    }

    public static ItemStack createFilledResult(ItemStack pEmptyStack, Player pPlayer, ItemStack pFilledStack) {
        return createFilledResult(pEmptyStack, pPlayer, pFilledStack, true);
    }

    public static void onContainerDestroyed(ItemEntity pItemEntity, Iterable<ItemStack> p_330873_) {
        Level level = pItemEntity.level();
        if (!level.isClientSide) {
            p_330873_.forEach(p_327181_ -> level.addFreshEntity(new ItemEntity(level, pItemEntity.getX(), pItemEntity.getY(), pItemEntity.getZ(), p_327181_)));
        }
    }
}