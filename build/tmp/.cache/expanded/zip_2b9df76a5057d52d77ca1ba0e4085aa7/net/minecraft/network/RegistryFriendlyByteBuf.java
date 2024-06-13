package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.core.RegistryAccess;

public class RegistryFriendlyByteBuf extends FriendlyByteBuf {
    private final RegistryAccess f_316842_;

    public RegistryFriendlyByteBuf(ByteBuf p_333796_, RegistryAccess p_330009_) {
        super(p_333796_);
        this.f_316842_ = p_330009_;
    }

    public RegistryAccess m_319626_() {
        return this.f_316842_;
    }

    @Override
    public RegistryFriendlyByteBuf wrap(ByteBuf data) {
        return new RegistryFriendlyByteBuf(data, this.f_316842_);
    }

    public static Function<ByteBuf, RegistryFriendlyByteBuf> m_324635_(RegistryAccess p_336066_) {
        return p_328649_ -> new RegistryFriendlyByteBuf(p_328649_, p_336066_);
    }
}
