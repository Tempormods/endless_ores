package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
import net.minecraft.Util;

public abstract class NamedEntityWriteReadFix extends DataFix {
    private final String f_302800_;
    private final String f_302274_;
    private final TypeReference f_302623_;

    public NamedEntityWriteReadFix(Schema p_310297_, boolean p_312818_, String p_313129_, TypeReference p_311108_, String p_313092_) {
        super(p_310297_, p_312818_);
        this.f_302800_ = p_313129_;
        this.f_302623_ = p_311108_;
        this.f_302274_ = p_313092_;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(this.f_302623_);
        Type<?> type1 = this.getInputSchema().getChoiceType(this.f_302623_, this.f_302274_);
        Type<?> type2 = this.getOutputSchema().getType(this.f_302623_);
        Type<?> type3 = this.getOutputSchema().getChoiceType(this.f_302623_, this.f_302274_);
        OpticFinder<?> opticfinder = DSL.namedChoice(this.f_302274_, type1);
        Type<?> type4 = type1.all(m_324768_(type, type2), true, false).view().newType();
        return this.m_320011_(type, type2, opticfinder, type3, type4);
    }

    private <S, T, A, B> TypeRewriteRule m_320011_(Type<S> p_334263_, Type<T> p_329342_, OpticFinder<A> p_329193_, Type<B> p_333979_, Type<?> p_327956_) {
        return this.fixTypeEverywhere(this.f_302800_, p_334263_, p_329342_, p_326626_ -> p_326621_ -> {
                Typed<S> typed = new Typed<>(p_334263_, p_326626_, p_326621_);
                return (T)typed.update(p_329193_, p_333979_, p_326631_ -> {
                    Typed<A> typed1 = new Typed<>((Type<A>)p_327956_, p_326626_, p_326631_);
                    return Util.m_306942_(typed1, p_333979_, this::m_305448_).getValue();
                }).getValue();
            });
    }

    private static <A, B> TypeRewriteRule m_324768_(Type<A> p_333790_, Type<B> p_334198_) {
        RewriteResult<A, B> rewriteresult = RewriteResult.create(View.create("Patcher", p_333790_, p_334198_, p_326627_ -> p_326615_ -> {
                throw new UnsupportedOperationException();
            }), new BitSet());
        return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(p_333790_, rewriteresult), PointFreeRule.nop(), true, true);
    }

    protected abstract <T> Dynamic<T> m_305448_(Dynamic<T> p_310304_);
}