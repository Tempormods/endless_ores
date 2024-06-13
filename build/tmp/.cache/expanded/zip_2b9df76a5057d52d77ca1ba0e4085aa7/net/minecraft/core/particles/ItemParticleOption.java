package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
    private static final Codec<ItemStack> f_314946_ = Codec.withAlternative(ItemStack.f_302992_, ItemStack.f_303113_, ItemStack::new);
    private final ParticleType<ItemParticleOption> type;
    private final ItemStack itemStack;

    public static MapCodec<ItemParticleOption> codec(ParticleType<ItemParticleOption> pType) {
        return f_314946_.xmap(p_123714_ -> new ItemParticleOption(pType, p_123714_), p_123709_ -> p_123709_.itemStack).fieldOf("item");
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> m_322965_(ParticleType<ItemParticleOption> p_332819_) {
        return ItemStack.f_315801_.m_323038_(p_325801_ -> new ItemParticleOption(p_332819_, p_325801_), p_325802_ -> p_325802_.itemStack);
    }

    public ItemParticleOption(ParticleType<ItemParticleOption> pType, ItemStack pItemStack) {
        if (pItemStack.isEmpty()) {
            throw new IllegalArgumentException("Empty stacks are not allowed");
        } else {
            this.type = pType;
            this.itemStack = pItemStack.copy(); // Forge: Fix stack updating after the fact causing particle changes.
        }
    }

    @Override
    public ParticleType<ItemParticleOption> getType() {
        return this.type;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }
}
