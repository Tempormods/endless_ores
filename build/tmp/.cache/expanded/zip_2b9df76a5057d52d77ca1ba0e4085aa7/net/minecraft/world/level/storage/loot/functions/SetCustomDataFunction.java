package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetCustomDataFunction extends LootItemConditionalFunction {
    public static final MapCodec<SetCustomDataFunction> f_314300_ = RecordCodecBuilder.mapCodec(
        p_336302_ -> commonFields(p_336302_)
                .and(TagParser.f_316526_.fieldOf("tag").forGetter(p_328670_ -> p_328670_.f_314948_))
                .apply(p_336302_, SetCustomDataFunction::new)
    );
    private final CompoundTag f_314948_;

    private SetCustomDataFunction(List<LootItemCondition> p_334383_, CompoundTag p_334528_) {
        super(p_334383_);
        this.f_314948_ = p_334528_;
    }

    @Override
    public LootItemFunctionType<SetCustomDataFunction> getType() {
        return LootItemFunctions.f_316637_;
    }

    @Override
    public ItemStack run(ItemStack p_328195_, LootContext p_331034_) {
        CustomData.m_322978_(DataComponents.f_316665_, p_328195_, p_335000_ -> p_335000_.merge(this.f_314948_));
        return p_328195_;
    }

    @Deprecated
    public static LootItemConditionalFunction.Builder<?> m_323013_(CompoundTag p_328660_) {
        return simpleBuilder(p_332883_ -> new SetCustomDataFunction(p_332883_, p_328660_));
    }
}