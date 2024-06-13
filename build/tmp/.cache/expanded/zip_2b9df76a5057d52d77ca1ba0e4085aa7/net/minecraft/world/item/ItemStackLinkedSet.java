package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class ItemStackLinkedSet {
    public static final Strategy<? super ItemStack> TYPE_AND_TAG = new Strategy<ItemStack>() {
        public int hashCode(@Nullable ItemStack p_251266_) {
            return ItemStack.m_322198_(p_251266_);
        }

        public boolean equals(@Nullable ItemStack p_250623_, @Nullable ItemStack p_251135_) {
            return p_250623_ == p_251135_
                || p_250623_ != null && p_251135_ != null && p_250623_.isEmpty() == p_251135_.isEmpty() && ItemStack.m_322370_(p_250623_, p_251135_);
        }
    };

    public static Set<ItemStack> createTypeAndTagSet() {
        return new ObjectLinkedOpenCustomHashSet<>(TYPE_AND_TAG);
    }
}