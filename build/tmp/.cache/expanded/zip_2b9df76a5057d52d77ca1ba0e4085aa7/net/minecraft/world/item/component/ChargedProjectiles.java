package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ChargedProjectiles {
    public static final ChargedProjectiles f_316210_ = new ChargedProjectiles(List.of());
    public static final Codec<ChargedProjectiles> f_316545_ = ItemStack.CODEC.listOf().xmap(ChargedProjectiles::new, p_333238_ -> p_333238_.f_316057_);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> f_316708_ = ItemStack.f_315801_
        .m_321801_(ByteBufCodecs.m_324765_())
        .m_323038_(ChargedProjectiles::new, p_330449_ -> p_330449_.f_316057_);
    private final List<ItemStack> f_316057_;

    private ChargedProjectiles(List<ItemStack> p_328441_) {
        this.f_316057_ = p_328441_;
    }

    public static ChargedProjectiles m_324021_(ItemStack p_330424_) {
        return new ChargedProjectiles(List.of(p_330424_.copy()));
    }

    public static ChargedProjectiles m_322388_(List<ItemStack> p_334351_) {
        return new ChargedProjectiles(List.copyOf(Lists.transform(p_334351_, ItemStack::copy)));
    }

    public boolean m_319117_(Item p_329513_) {
        for (ItemStack itemstack : this.f_316057_) {
            if (itemstack.is(p_329513_)) {
                return true;
            }
        }

        return false;
    }

    public List<ItemStack> m_321623_() {
        return Lists.transform(this.f_316057_, ItemStack::copy);
    }

    public boolean m_324666_() {
        return this.f_316057_.isEmpty();
    }

    @Override
    public boolean equals(Object p_332122_) {
        if (this == p_332122_) {
            return true;
        } else {
            if (p_332122_ instanceof ChargedProjectiles chargedprojectiles && ItemStack.m_319597_(this.f_316057_, chargedprojectiles.f_316057_)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.m_318747_(this.f_316057_);
    }

    @Override
    public String toString() {
        return "ChargedProjectiles[items=" + this.f_316057_ + "]";
    }
}