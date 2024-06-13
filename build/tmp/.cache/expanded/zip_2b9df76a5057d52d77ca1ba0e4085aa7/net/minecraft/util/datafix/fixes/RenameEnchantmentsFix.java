package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DataResult.Error;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class RenameEnchantmentsFix extends DataFix {
    final String f_316310_;
    final Map<String, String> f_315960_;

    public RenameEnchantmentsFix(Schema p_330639_, String p_335402_, Map<String, String> p_335538_) {
        super(p_330639_, false);
        this.f_316310_ = p_335402_;
        this.f_315960_ = p_335538_;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("tag");
        return this.fixTypeEverywhereTyped(
            this.f_316310_, type, p_327788_ -> p_327788_.updateTyped(opticfinder, p_330012_ -> p_330012_.update(DSL.remainderFinder(), this::m_321421_))
        );
    }

    private Dynamic<?> m_321421_(Dynamic<?> p_331261_) {
        p_331261_ = this.m_321435_(p_331261_, "Enchantments");
        return this.m_321435_(p_331261_, "StoredEnchantments");
    }

    private Dynamic<?> m_321435_(Dynamic<?> p_329694_, String p_331585_) {
        return p_329694_.update(
            p_331585_,
            p_335338_ -> p_335338_.asStreamOpt()
                    .map(
                        p_329289_ -> p_329289_.map(
                                p_333784_ -> p_333784_.update(
                                        "id",
                                        p_336146_ -> p_336146_.asString()
                                                .map(p_334096_ -> p_333784_.createString(this.f_315960_.getOrDefault(p_334096_, p_334096_)))
                                                .mapOrElse(Function.identity(), p_332784_ -> p_336146_)
                                    )
                            )
                    )
                    .map(p_335338_::createList)
                    .mapOrElse(Function.identity(), p_334640_ -> p_335338_)
        );
    }
}