package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public interface PlainTextContents extends ComponentContents {
    MapCodec<PlainTextContents> f_302508_ = RecordCodecBuilder.mapCodec(
        p_310759_ -> p_310759_.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContents::m_305315_)).apply(p_310759_, PlainTextContents::m_307377_)
    );
    ComponentContents.Type<PlainTextContents> f_302384_ = new ComponentContents.Type<>(f_302508_, "text");
    PlainTextContents f_302486_ = new PlainTextContents() {
        @Override
        public String toString() {
            return "empty";
        }

        @Override
        public String m_305315_() {
            return "";
        }
    };

    static PlainTextContents m_307377_(String p_310243_) {
        return (PlainTextContents)(p_310243_.isEmpty() ? f_302486_ : new PlainTextContents.LiteralContents(p_310243_));
    }

    String m_305315_();

    @Override
    default ComponentContents.Type<?> m_304650_() {
        return f_302384_;
    }

    public static record LiteralContents(String f_302414_) implements PlainTextContents {
        @Override
        public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_312000_) {
            return p_312000_.accept(this.f_302414_);
        }

        @Override
        public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_313135_, Style p_310796_) {
            return p_313135_.accept(p_310796_, this.f_302414_);
        }

        @Override
        public String toString() {
            return "literal{" + this.f_302414_ + "}";
        }

        @Override
        public String m_305315_() {
            return this.f_302414_;
        }
    }
}