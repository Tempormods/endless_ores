package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetItemFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetItemFunction> f_316876_ = RecordCodecBuilder.mapCodec(
        p_335262_ -> commonFields(p_335262_)
                .and(RegistryFixedCodec.create(Registries.ITEM).fieldOf("item").forGetter(p_334713_ -> p_334713_.f_313907_))
                .apply(p_335262_, SetItemFunction::new)
    );
    private final Holder<Item> f_313907_;

    private SetItemFunction(List<LootItemCondition> p_334628_, Holder<Item> p_334791_) {
        super(p_334628_);
        this.f_313907_ = p_334791_;
    }

    @Override
    public LootItemFunctionType<SetItemFunction> getType() {
        return LootItemFunctions.f_314595_;
    }

    @Override
    public ItemStack run(ItemStack p_330993_, LootContext p_332197_) {
        return p_330993_.m_319323_(this.f_313907_.value(), p_330993_.getCount());
    }
}