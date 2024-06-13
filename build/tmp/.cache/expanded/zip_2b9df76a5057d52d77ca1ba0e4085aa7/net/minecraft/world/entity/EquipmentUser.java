package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

public interface EquipmentUser {
    void m_21035_(EquipmentSlot p_333752_, ItemStack p_331668_);

    ItemStack getItemBySlot(EquipmentSlot p_329199_);

    void setDropChance(EquipmentSlot p_331517_, float p_334697_);

    default void m_321661_(EquipmentTable p_331159_, LootParams p_332346_) {
        this.m_319719_(p_331159_.f_316700_(), p_332346_, p_331159_.f_315505_());
    }

    default void m_319719_(ResourceKey<LootTable> p_329232_, LootParams p_330675_, Map<EquipmentSlot, Float> p_328003_) {
        this.m_320583_(p_329232_, p_330675_, 0L, p_328003_);
    }

    default void m_320583_(ResourceKey<LootTable> p_331471_, LootParams p_333826_, long p_331881_, Map<EquipmentSlot, Float> p_328541_) {
        if (!p_331471_.equals(BuiltInLootTables.EMPTY)) {
            LootTable loottable = p_333826_.getLevel().getServer().m_323018_().m_321428_(p_331471_);
            if (loottable != LootTable.EMPTY) {
                List<ItemStack> list = loottable.getRandomItems(p_333826_, p_331881_);
                List<EquipmentSlot> list1 = new ArrayList<>();

                for (ItemStack itemstack : list) {
                    EquipmentSlot equipmentslot = this.m_320137_(itemstack, list1);
                    if (equipmentslot != null) {
                        ItemStack itemstack1 = equipmentslot.isArmor() ? itemstack.copyWithCount(1) : itemstack;
                        this.m_21035_(equipmentslot, itemstack1);
                        Float f = p_328541_.get(equipmentslot);
                        if (f != null) {
                            this.setDropChance(equipmentslot, f);
                        }

                        list1.add(equipmentslot);
                    }
                }
            }
        }
    }

    @Nullable
    default EquipmentSlot m_320137_(ItemStack p_329649_, List<EquipmentSlot> p_334449_) {
        if (p_329649_.isEmpty()) {
            return null;
        } else {
            Equipable equipable = Equipable.get(p_329649_);
            if (equipable != null) {
                EquipmentSlot equipmentslot = equipable.getEquipmentSlot();
                if (!p_334449_.contains(equipmentslot)) {
                    return equipmentslot;
                }
            } else if (!p_334449_.contains(EquipmentSlot.MAINHAND)) {
                return EquipmentSlot.MAINHAND;
            }

            return null;
        }
    }
}