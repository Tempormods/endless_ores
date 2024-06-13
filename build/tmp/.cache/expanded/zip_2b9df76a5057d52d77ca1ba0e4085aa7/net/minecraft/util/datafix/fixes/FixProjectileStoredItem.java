package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import net.minecraft.Util;

public class FixProjectileStoredItem extends DataFix {
    private static final String f_302840_ = "minecraft:empty";

    public FixProjectileStoredItem(Schema p_310923_) {
        super(p_310923_, true);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.ENTITY);
        Type<?> type1 = this.getOutputSchema().getType(References.ENTITY);
        return this.fixTypeEverywhereTyped(
            "Fix AbstractArrow item type",
            type,
            type1,
            this.m_306393_(
                this.m_305303_("minecraft:trident", FixProjectileStoredItem::m_306325_),
                this.m_305303_("minecraft:arrow", FixProjectileStoredItem::m_305306_),
                this.m_305303_("minecraft:spectral_arrow", FixProjectileStoredItem::m_305622_)
            )
        );
    }

    @SafeVarargs
    private <T> Function<Typed<?>, Typed<?>> m_306393_(Function<Typed<?>, Typed<?>>... p_312048_) {
        return p_310414_ -> {
            for (Function<Typed<?>, Typed<?>> function : p_312048_) {
                p_310414_ = function.apply(p_310414_);
            }

            return p_310414_;
        };
    }

    private Function<Typed<?>, Typed<?>> m_305303_(String p_312752_, FixProjectileStoredItem.SubFixer<?> p_310778_) {
        Type<?> type = this.getInputSchema().getChoiceType(References.ENTITY, p_312752_);
        Type<?> type1 = this.getOutputSchema().getChoiceType(References.ENTITY, p_312752_);
        return m_307175_(p_312752_, p_310778_, type, type1);
    }

    private static <T> Function<Typed<?>, Typed<?>> m_307175_(
        String p_312294_, FixProjectileStoredItem.SubFixer<?> p_310164_, Type<?> p_310703_, Type<T> p_312528_
    ) {
        OpticFinder<?> opticfinder = DSL.namedChoice(p_312294_, p_310703_);
        return p_313205_ -> p_313205_.updateTyped(opticfinder, p_312528_, p_312567_ -> p_310164_.m_307591_((Typed)p_312567_, (Type)p_312528_));
    }

    private static <T> Typed<T> m_305306_(Typed<?> p_312190_, Type<T> p_311775_) {
        return Util.m_306942_(p_312190_, p_311775_, p_312479_ -> p_312479_.set("item", m_307895_(p_312479_, m_305619_(p_312479_))));
    }

    private static String m_305619_(Dynamic<?> p_311918_) {
        return p_311918_.get("Potion").asString("minecraft:empty").equals("minecraft:empty") ? "minecraft:arrow" : "minecraft:tipped_arrow";
    }

    private static <T> Typed<T> m_305622_(Typed<?> p_311496_, Type<T> p_311551_) {
        return Util.m_306942_(p_311496_, p_311551_, p_310800_ -> p_310800_.set("item", m_307895_(p_310800_, "minecraft:spectral_arrow")));
    }

    private static Dynamic<?> m_307895_(Dynamic<?> p_310249_, String p_312956_) {
        return p_310249_.createMap(
            ImmutableMap.of(p_310249_.createString("id"), p_310249_.createString(p_312956_), p_310249_.createString("Count"), p_310249_.createInt(1))
        );
    }

    private static <T> Typed<T> m_306325_(Typed<?> p_310006_, Type<T> p_312989_) {
        return new Typed<>(p_312989_, p_310006_.getOps(), (T)p_310006_.getValue());
    }

    interface SubFixer<F> {
        Typed<F> m_307591_(Typed<?> p_309643_, Type<F> p_311884_);
    }
}