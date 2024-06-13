package net.minecraft.world.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.StringRepresentable;

public interface SlotRange extends StringRepresentable {
    IntList m_319501_();

    default int m_319620_() {
        return this.m_319501_().size();
    }

    static SlotRange m_324344_(final String p_332630_, final IntList p_330943_) {
        return new SlotRange() {
            @Override
            public IntList m_319501_() {
                return p_330943_;
            }

            @Override
            public String getSerializedName() {
                return p_332630_;
            }

            @Override
            public String toString() {
                return p_332630_;
            }
        };
    }
}