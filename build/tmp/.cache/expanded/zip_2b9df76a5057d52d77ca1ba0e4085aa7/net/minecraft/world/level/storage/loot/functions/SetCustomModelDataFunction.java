package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetCustomModelDataFunction extends LootItemConditionalFunction {
    static final MapCodec<SetCustomModelDataFunction> f_315713_ = RecordCodecBuilder.mapCodec(
        p_332110_ -> commonFields(p_332110_)
                .and(NumberProviders.CODEC.fieldOf("value").forGetter(p_334766_ -> p_334766_.f_316443_))
                .apply(p_332110_, SetCustomModelDataFunction::new)
    );
    private final NumberProvider f_316443_;

    private SetCustomModelDataFunction(List<LootItemCondition> p_335890_, NumberProvider p_333004_) {
        super(p_335890_);
        this.f_316443_ = p_333004_;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.f_316443_.getReferencedContextParams();
    }

    @Override
    public LootItemFunctionType<SetCustomModelDataFunction> getType() {
        return LootItemFunctions.f_316565_;
    }

    @Override
    public ItemStack run(ItemStack p_328099_, LootContext p_333702_) {
        p_328099_.m_322496_(DataComponents.f_315513_, new CustomModelData(this.f_316443_.getInt(p_333702_)));
        return p_328099_;
    }
}