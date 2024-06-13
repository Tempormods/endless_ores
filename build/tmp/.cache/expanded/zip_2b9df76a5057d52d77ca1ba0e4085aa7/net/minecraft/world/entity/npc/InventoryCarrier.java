package net.minecraft.world.entity.npc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface InventoryCarrier {
    String TAG_INVENTORY = "Inventory";

    SimpleContainer getInventory();

    static void pickUpItem(Mob pMob, InventoryCarrier pCarrier, ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (pMob.wantsToPickUp(itemstack)) {
            SimpleContainer simplecontainer = pCarrier.getInventory();
            boolean flag = simplecontainer.canAddItem(itemstack);
            if (!flag) {
                return;
            }

            pMob.onItemPickup(pItemEntity);
            int i = itemstack.getCount();
            ItemStack itemstack1 = simplecontainer.addItem(itemstack);
            pMob.take(pItemEntity, i - itemstack1.getCount());
            if (itemstack1.isEmpty()) {
                pItemEntity.discard();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }
    }

    default void readInventoryFromTag(CompoundTag pTag, HolderLookup.Provider p_331899_) {
        if (pTag.contains("Inventory", 9)) {
            this.getInventory().fromTag(pTag.getList("Inventory", 10), p_331899_);
        }
    }

    default void writeInventoryToTag(CompoundTag pTag, HolderLookup.Provider p_328974_) {
        pTag.put("Inventory", this.getInventory().createTag(p_328974_));
    }
}