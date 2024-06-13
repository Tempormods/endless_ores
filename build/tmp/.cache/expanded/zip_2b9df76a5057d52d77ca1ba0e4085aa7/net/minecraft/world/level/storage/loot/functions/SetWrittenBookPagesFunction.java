package net.minecraft.world.level.storage.loot.functions;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetWrittenBookPagesFunction extends LootItemConditionalFunction {
    public static final Codec<Component> f_317055_ = ComponentSerialization.f_303288_
        .validate(p_335313_ -> WrittenBookContent.f_315642_.encodeStart(JavaOps.INSTANCE, p_335313_).map(p_336204_ -> p_335313_));
    public static final MapCodec<SetWrittenBookPagesFunction> f_315235_ = RecordCodecBuilder.mapCodec(
        p_332217_ -> commonFields(p_332217_)
                .and(
                    p_332217_.group(
                        WrittenBookContent.m_322748_(f_317055_).fieldOf("pages").forGetter(p_336220_ -> p_336220_.f_316083_),
                        ListOperation.f_317149_.forGetter(p_335675_ -> p_335675_.f_314064_)
                    )
                )
                .apply(p_332217_, SetWrittenBookPagesFunction::new)
    );
    private final List<Filterable<Component>> f_316083_;
    private final ListOperation f_314064_;

    protected SetWrittenBookPagesFunction(List<LootItemCondition> p_334314_, List<Filterable<Component>> p_336337_, ListOperation p_336120_) {
        super(p_334314_);
        this.f_316083_ = p_336337_;
        this.f_314064_ = p_336120_;
    }

    @Override
    protected ItemStack run(ItemStack p_328011_, LootContext p_329576_) {
        p_328011_.m_322591_(DataComponents.f_315840_, WrittenBookContent.f_315751_, this::m_321326_);
        return p_328011_;
    }

    @VisibleForTesting
    public WrittenBookContent m_321326_(WrittenBookContent p_335075_) {
        List<Filterable<Component>> list = this.f_314064_.m_323335_(p_335075_.m_319402_(), this.f_316083_);
        return p_335075_.m_319955_(list);
    }

    @Override
    public LootItemFunctionType<SetWrittenBookPagesFunction> getType() {
        return LootItemFunctions.f_316039_;
    }
}