package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map.Entry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TypedDataComponent<T>(DataComponentType<T> f_316611_, T f_314804_) {
    public static final StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>> f_315130_ = new StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>>() {
        public TypedDataComponent<?> m_318688_(RegistryFriendlyByteBuf p_333264_) {
            DataComponentType<?> datacomponenttype = DataComponentType.f_315368_.m_318688_(p_333264_);
            return m_319771_(p_333264_, (DataComponentType)datacomponenttype);
        }

        private static <T> TypedDataComponent<T> m_319771_(RegistryFriendlyByteBuf p_329132_, DataComponentType<T> p_330664_) {
            return new TypedDataComponent<>(p_330664_, p_330664_.m_318786_().m_318688_(p_329132_));
        }

        public void m_318638_(RegistryFriendlyByteBuf p_334022_, TypedDataComponent<?> p_331938_) {
            m_324725_(p_334022_, (TypedDataComponent)p_331938_);
        }

        private static <T> void m_324725_(RegistryFriendlyByteBuf p_331689_, TypedDataComponent<T> p_331096_) {
            DataComponentType.f_315368_.m_318638_(p_331689_, p_331096_.f_316611_());
            p_331096_.f_316611_().m_318786_().m_318638_(p_331689_, p_331096_.f_314804_());
        }
    };

    static TypedDataComponent<?> m_322622_(Entry<DataComponentType<?>, Object> p_335332_) {
        return m_321971_(p_335332_.getKey(), p_335332_.getValue());
    }

    static <T> TypedDataComponent<T> m_321971_(DataComponentType<T> p_332647_, Object p_330924_) {
        return new TypedDataComponent<>(p_332647_, (T)p_330924_);
    }

    public void m_324030_(PatchedDataComponentMap p_334157_) {
        p_334157_.m_322371_(this.f_316611_, this.f_314804_);
    }

    public <D> DataResult<D> m_318908_(DynamicOps<D> p_331110_) {
        Codec<T> codec = this.f_316611_.m_319878_();
        return codec == null
            ? DataResult.error(() -> "Component of type " + this.f_316611_ + " is not encodable")
            : codec.encodeStart(p_331110_, this.f_314804_);
    }

    @Override
    public String toString() {
        return this.f_316611_ + "=>" + this.f_314804_;
    }
}