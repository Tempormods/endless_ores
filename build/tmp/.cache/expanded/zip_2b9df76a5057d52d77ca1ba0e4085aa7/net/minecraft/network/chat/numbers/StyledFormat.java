package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public class StyledFormat implements NumberFormat {
    public static final NumberFormatType<StyledFormat> f_302955_ = new NumberFormatType<StyledFormat>() {
        private static final MapCodec<StyledFormat> f_303186_ = Style.Serializer.f_303391_.xmap(StyledFormat::new, p_311299_ -> p_311299_.f_302982_);
        private static final StreamCodec<RegistryFriendlyByteBuf, StyledFormat> f_316732_ = StreamCodec.m_322204_(
            Style.Serializer.f_314849_, p_326089_ -> p_326089_.f_302982_, StyledFormat::new
        );

        @Override
        public MapCodec<StyledFormat> m_305346_() {
            return f_303186_;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StyledFormat> m_321453_() {
            return f_316732_;
        }
    };
    public static final StyledFormat f_302669_ = new StyledFormat(Style.EMPTY);
    public static final StyledFormat f_303088_ = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.RED));
    public static final StyledFormat f_302354_ = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    final Style f_302982_;

    public StyledFormat(Style p_311279_) {
        this.f_302982_ = p_311279_;
    }

    @Override
    public MutableComponent m_305266_(int p_312267_) {
        return Component.literal(Integer.toString(p_312267_)).withStyle(this.f_302982_);
    }

    @Override
    public NumberFormatType<StyledFormat> m_307395_() {
        return f_302955_;
    }
}