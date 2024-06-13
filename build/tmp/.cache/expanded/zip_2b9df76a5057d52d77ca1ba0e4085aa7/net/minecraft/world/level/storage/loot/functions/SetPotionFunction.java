package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetPotionFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_327629_ -> commonFields(p_327629_)
                .and(BuiltInRegistries.POTION.holderByNameCodec().fieldOf("id").forGetter(p_297173_ -> p_297173_.potion))
                .apply(p_327629_, SetPotionFunction::new)
    );
    private final Holder<Potion> potion;

    private SetPotionFunction(List<LootItemCondition> p_297236_, Holder<Potion> p_300134_) {
        super(p_297236_);
        this.potion = p_300134_;
    }

    @Override
    public LootItemFunctionType<SetPotionFunction> getType() {
        return LootItemFunctions.SET_POTION;
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        pStack.m_324919_(DataComponents.f_314188_, PotionContents.f_313984_, this.potion, PotionContents::m_324670_);
        return pStack;
    }

    public static LootItemConditionalFunction.Builder<?> setPotion(Holder<Potion> p_329541_) {
        return simpleBuilder(p_327628_ -> new SetPotionFunction(p_327628_, p_329541_));
    }
}