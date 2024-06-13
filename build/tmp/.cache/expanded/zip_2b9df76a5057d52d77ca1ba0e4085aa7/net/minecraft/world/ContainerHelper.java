package net.minecraft.world;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class ContainerHelper {
    public static final String f_314637_ = "Items";

    public static ItemStack removeItem(List<ItemStack> pStacks, int pIndex, int pAmount) {
        return pIndex >= 0 && pIndex < pStacks.size() && !pStacks.get(pIndex).isEmpty() && pAmount > 0
            ? pStacks.get(pIndex).split(pAmount)
            : ItemStack.EMPTY;
    }

    public static ItemStack takeItem(List<ItemStack> pStacks, int pIndex) {
        return pIndex >= 0 && pIndex < pStacks.size() ? pStacks.set(pIndex, ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public static CompoundTag saveAllItems(CompoundTag pTag, NonNullList<ItemStack> pList, HolderLookup.Provider p_333891_) {
        return saveAllItems(pTag, pList, true, p_333891_);
    }

    public static CompoundTag saveAllItems(CompoundTag pTag, NonNullList<ItemStack> pList, boolean p_336339_, HolderLookup.Provider p_329730_) {
        ListTag listtag = new ListTag();

        for (int i = 0; i < pList.size(); i++) {
            ItemStack itemstack = pList.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                listtag.add(itemstack.m_321167_(p_329730_, compoundtag));
            }
        }

        if (!listtag.isEmpty() || p_336339_) {
            pTag.put("Items", listtag);
        }

        return pTag;
    }

    public static void loadAllItems(CompoundTag pTag, NonNullList<ItemStack> pList, HolderLookup.Provider p_334892_) {
        ListTag listtag = pTag.getList("Items", 10);

        for (int i = 0; i < listtag.size(); i++) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < pList.size()) {
                pList.set(j, ItemStack.m_323951_(p_334892_, compoundtag).orElse(ItemStack.EMPTY));
            }
        }
    }

    public static int clearOrCountMatchingItems(Container pContainer, Predicate<ItemStack> pItemPredicate, int pMaxItems, boolean pSimulate) {
        int i = 0;

        for (int j = 0; j < pContainer.getContainerSize(); j++) {
            ItemStack itemstack = pContainer.getItem(j);
            int k = clearOrCountMatchingItems(itemstack, pItemPredicate, pMaxItems - i, pSimulate);
            if (k > 0 && !pSimulate && itemstack.isEmpty()) {
                pContainer.setItem(j, ItemStack.EMPTY);
            }

            i += k;
        }

        return i;
    }

    public static int clearOrCountMatchingItems(ItemStack pStack, Predicate<ItemStack> pItemPredicate, int pMaxItems, boolean pSimulate) {
        if (pStack.isEmpty() || !pItemPredicate.test(pStack)) {
            return 0;
        } else if (pSimulate) {
            return pStack.getCount();
        } else {
            int i = pMaxItems < 0 ? pStack.getCount() : Math.min(pMaxItems, pStack.getCount());
            pStack.shrink(i);
            return i;
        }
    }
}