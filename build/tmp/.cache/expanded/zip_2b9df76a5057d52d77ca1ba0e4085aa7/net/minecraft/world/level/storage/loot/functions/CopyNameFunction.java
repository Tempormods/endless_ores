package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * LootItemFunction that sets the stack's name by copying it from somewhere else, such as the killing player.
 */
public class CopyNameFunction extends LootItemConditionalFunction {
    public static final MapCodec<CopyNameFunction> CODEC = RecordCodecBuilder.mapCodec(
        p_297078_ -> commonFields(p_297078_)
                .and(CopyNameFunction.NameSource.CODEC.fieldOf("source").forGetter(p_297077_ -> p_297077_.source))
                .apply(p_297078_, CopyNameFunction::new)
    );
    private final CopyNameFunction.NameSource source;

    private CopyNameFunction(List<LootItemCondition> p_300985_, CopyNameFunction.NameSource p_80178_) {
        super(p_300985_);
        this.source = p_80178_;
    }

    @Override
    public LootItemFunctionType<CopyNameFunction> getType() {
        return LootItemFunctions.COPY_NAME;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.source.param);
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        if (pContext.getParamOrNull(this.source.param) instanceof Nameable nameable) {
            pStack.m_322496_(DataComponents.f_316016_, nameable.getCustomName());
        }

        return pStack;
    }

    public static LootItemConditionalFunction.Builder<?> copyName(CopyNameFunction.NameSource pSource) {
        return simpleBuilder(p_297080_ -> new CopyNameFunction(p_297080_, pSource));
    }

    public static enum NameSource implements StringRepresentable {
        THIS("this", LootContextParams.THIS_ENTITY),
        KILLER("killer", LootContextParams.KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

        public static final Codec<CopyNameFunction.NameSource> CODEC = StringRepresentable.fromEnum(CopyNameFunction.NameSource::values);
        private final String name;
        final LootContextParam<?> param;

        private NameSource(final String pName, final LootContextParam<?> pParam) {
            this.name = pName;
            this.param = pParam;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}