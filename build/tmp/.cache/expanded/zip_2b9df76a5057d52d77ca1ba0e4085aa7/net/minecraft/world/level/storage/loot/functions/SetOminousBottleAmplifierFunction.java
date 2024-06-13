package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetOminousBottleAmplifierFunction extends LootItemConditionalFunction {
    static final MapCodec<SetOminousBottleAmplifierFunction> f_315471_ = RecordCodecBuilder.mapCodec(
        p_330680_ -> commonFields(p_330680_)
                .and(NumberProviders.CODEC.fieldOf("amplifier").forGetter(p_328335_ -> p_328335_.f_316180_))
                .apply(p_330680_, SetOminousBottleAmplifierFunction::new)
    );
    private final NumberProvider f_316180_;

    private SetOminousBottleAmplifierFunction(List<LootItemCondition> p_328459_, NumberProvider p_330589_) {
        super(p_328459_);
        this.f_316180_ = p_330589_;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.f_316180_.getReferencedContextParams();
    }

    @Override
    public LootItemFunctionType<SetOminousBottleAmplifierFunction> getType() {
        return LootItemFunctions.f_316756_;
    }

    @Override
    public ItemStack run(ItemStack p_327902_, LootContext p_335574_) {
        int i = Mth.clamp(this.f_316180_.getInt(p_335574_), 0, 4);
        p_327902_.m_322496_(DataComponents.f_314953_, i);
        return p_327902_;
    }

    public NumberProvider m_324345_() {
        return this.f_316180_;
    }

    public static LootItemConditionalFunction.Builder<?> m_323203_(NumberProvider p_329950_) {
        return simpleBuilder(p_334301_ -> new SetOminousBottleAmplifierFunction(p_334301_, p_329950_));
    }
}