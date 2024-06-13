package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public abstract class ItemStackComponentRemainderFix extends DataFix {
    private final String f_314829_;
    private final String f_316508_;
    private final String f_315232_;

    public ItemStackComponentRemainderFix(Schema p_334140_, String p_330429_, String p_335879_) {
        this(p_334140_, p_330429_, p_335879_, p_335879_);
    }

    public ItemStackComponentRemainderFix(Schema p_330360_, String p_329958_, String p_335490_, String p_335902_) {
        super(p_330360_, false);
        this.f_314829_ = p_329958_;
        this.f_316508_ = p_335490_;
        this.f_315232_ = p_335902_;
    }

    @Override
    public final TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("components");
        return this.fixTypeEverywhereTyped(
            this.f_314829_,
            type,
            p_329992_ -> p_329992_.updateTyped(
                    opticfinder,
                    p_332858_ -> p_332858_.update(
                            DSL.remainderFinder(), p_330335_ -> p_330335_.renameAndFixField(this.f_316508_, this.f_315232_, this::m_321335_)
                        )
                )
        );
    }

    protected abstract <T> Dynamic<T> m_321335_(Dynamic<T> p_330625_);
}