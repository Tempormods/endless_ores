package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public class FixedFormat implements NumberFormat {
    public static final NumberFormatType<FixedFormat> f_302513_ = new NumberFormatType<FixedFormat>() {
        private static final MapCodec<FixedFormat> f_302209_ = ComponentSerialization.f_303288_
            .fieldOf("value")
            .xmap(FixedFormat::new, p_311625_ -> p_311625_.f_303777_);
        private static final StreamCodec<RegistryFriendlyByteBuf, FixedFormat> f_316913_ = StreamCodec.m_322204_(
            ComponentSerialization.f_316335_, p_326088_ -> p_326088_.f_303777_, FixedFormat::new
        );

        @Override
        public MapCodec<FixedFormat> m_305346_() {
            return f_302209_;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FixedFormat> m_321453_() {
            return f_316913_;
        }
    };
    final Component f_303777_;

    public FixedFormat(Component p_309670_) {
        this.f_303777_ = p_309670_;
    }

    @Override
    public MutableComponent m_305266_(int p_311204_) {
        return this.f_303777_.copy();
    }

    @Override
    public NumberFormatType<FixedFormat> m_307395_() {
        return f_302513_;
    }
}