package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetComponentsFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetComponentsFunction> f_315423_ = RecordCodecBuilder.mapCodec(
        p_327903_ -> commonFields(p_327903_)
                .and(DataComponentPatch.f_315187_.fieldOf("components").forGetter(p_331439_ -> p_331439_.f_316848_))
                .apply(p_327903_, SetComponentsFunction::new)
    );
    private final DataComponentPatch f_316848_;

    private SetComponentsFunction(List<LootItemCondition> p_334087_, DataComponentPatch p_331768_) {
        super(p_334087_);
        this.f_316848_ = p_331768_;
    }

    @Override
    public LootItemFunctionType<SetComponentsFunction> getType() {
        return LootItemFunctions.f_316980_;
    }

    @Override
    public ItemStack run(ItemStack p_336175_, LootContext p_333804_) {
        p_336175_.m_320623_(this.f_316848_);
        return p_336175_;
    }

    public static <T> LootItemConditionalFunction.Builder<?> m_320963_(DataComponentType<T> p_334396_, T p_330070_) {
        return simpleBuilder(p_328648_ -> new SetComponentsFunction(p_328648_, DataComponentPatch.m_322543_().m_323566_(p_334396_, p_330070_).m_323652_()));
    }
}