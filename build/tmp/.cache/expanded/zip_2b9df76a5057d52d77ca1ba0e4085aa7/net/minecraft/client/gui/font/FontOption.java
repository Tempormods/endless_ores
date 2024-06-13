package net.minecraft.client.gui.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum FontOption implements StringRepresentable {
    UNIFORM("uniform"),
    JAPANESE_VARIANTS("jp");

    public static final Codec<FontOption> f_316018_ = StringRepresentable.fromEnum(FontOption::values);
    private final String f_316271_;

    private FontOption(final String p_334824_) {
        this.f_316271_ = p_334824_;
    }

    @Override
    public String getSerializedName() {
        return this.f_316271_;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Filter {
        private final Map<FontOption, Boolean> f_316067_;
        public static final Codec<FontOption.Filter> f_314128_ = Codec.unboundedMap(FontOption.f_316018_, Codec.BOOL)
            .xmap(FontOption.Filter::new, p_329501_ -> p_329501_.f_316067_);
        public static final FontOption.Filter f_315854_ = new FontOption.Filter(Map.of());

        public Filter(Map<FontOption, Boolean> p_332258_) {
            this.f_316067_ = p_332258_;
        }

        public boolean m_319512_(Set<FontOption> p_334823_) {
            for (Entry<FontOption, Boolean> entry : this.f_316067_.entrySet()) {
                if (p_334823_.contains(entry.getKey()) != entry.getValue()) {
                    return false;
                }
            }

            return true;
        }

        public FontOption.Filter m_323896_(FontOption.Filter p_331605_) {
            Map<FontOption, Boolean> map = new HashMap<>(p_331605_.f_316067_);
            map.putAll(this.f_316067_);
            return new FontOption.Filter(Map.copyOf(map));
        }
    }
}