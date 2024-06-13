package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public class BlankFormat implements NumberFormat {
    public static final BlankFormat f_302787_ = new BlankFormat();
    public static final NumberFormatType<BlankFormat> f_303499_ = new NumberFormatType<BlankFormat>() {
        private static final MapCodec<BlankFormat> f_303700_ = MapCodec.unit(BlankFormat.f_302787_);
        private static final StreamCodec<RegistryFriendlyByteBuf, BlankFormat> f_314823_ = StreamCodec.m_323136_(BlankFormat.f_302787_);

        @Override
        public MapCodec<BlankFormat> m_305346_() {
            return f_303700_;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlankFormat> m_321453_() {
            return f_314823_;
        }
    };

    @Override
    public MutableComponent m_305266_(int p_310442_) {
        return Component.empty();
    }

    @Override
    public NumberFormatType<BlankFormat> m_307395_() {
        return f_303499_;
    }
}