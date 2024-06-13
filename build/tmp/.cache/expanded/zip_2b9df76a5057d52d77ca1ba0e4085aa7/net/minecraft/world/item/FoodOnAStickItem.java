package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FoodOnAStickItem<T extends Entity & ItemSteerable> extends Item {
    private final EntityType<T> canInteractWith;
    private final int consumeItemDamage;

    public FoodOnAStickItem(Item.Properties pProperties, EntityType<T> pCanInteractWith, int pConsumeItemDamage) {
        super(pProperties);
        this.canInteractWith = pCanInteractWith;
        this.consumeItemDamage = pConsumeItemDamage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Entity entity = pPlayer.getControlledVehicle();
            if (pPlayer.isPassenger() && entity instanceof ItemSteerable itemsteerable && entity.getType() == this.canInteractWith && itemsteerable.boost()) {
                itemstack.hurtAndBreak(this.consumeItemDamage, pPlayer, LivingEntity.m_322775_(pHand));
                if (itemstack.isEmpty()) {
                    ItemStack itemstack1 = itemstack.m_320013_(Items.FISHING_ROD, 1);
                    return InteractionResultHolder.success(itemstack1);
                }

                return InteractionResultHolder.success(itemstack);
            }

            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.pass(itemstack);
        }
    }
}