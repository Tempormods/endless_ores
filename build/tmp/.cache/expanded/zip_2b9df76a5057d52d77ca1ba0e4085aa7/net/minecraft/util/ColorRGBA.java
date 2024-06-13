package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Locale;

public record ColorRGBA(int f_303724_) {
    private static final String f_303874_ = "#";
    public static final Codec<ColorRGBA> f_303313_ = Codec.STRING.comapFlatMap(p_311033_ -> {
        if (!p_311033_.startsWith("#")) {
            return DataResult.error(() -> "Not a color code: " + p_311033_);
        } else {
            try {
                int i = (int)Long.parseLong(p_311033_.substring(1), 16);
                return DataResult.success(new ColorRGBA(i));
            } catch (NumberFormatException numberformatexception) {
                return DataResult.error(() -> "Exception parsing color code: " + numberformatexception.getMessage());
            }
        }
    }, ColorRGBA::m_305093_);

    private String m_305093_() {
        return String.format(Locale.ROOT, "#%08X", this.f_303724_);
    }

    @Override
    public String toString() {
        return this.m_305093_();
    }
}