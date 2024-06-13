package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * LootItemFunction that sets the contents of a container such as a chest by setting the {@code BlocKEntityTag} of the
 * stacks.
 * The contents are based on a list of loot pools.
 */
public class SetContainerContents extends LootItemConditionalFunction {
    public static final MapCodec<SetContainerContents> CODEC = RecordCodecBuilder.mapCodec(
        p_327591_ -> commonFields(p_327591_)
                .and(
                    p_327591_.group(
                        ContainerComponentManipulators.f_315037_.fieldOf("component").forGetter(p_327590_ -> p_327590_.f_315714_),
                        LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter(p_297115_ -> p_297115_.entries)
                    )
                )
                .apply(p_327591_, SetContainerContents::new)
    );
    private final ContainerComponentManipulator<?> f_315714_;
    private final List<LootPoolEntryContainer> entries;

    SetContainerContents(List<LootItemCondition> p_193035_, ContainerComponentManipulator<?> p_329803_, List<LootPoolEntryContainer> p_298786_) {
        super(p_193035_);
        this.f_315714_ = p_329803_;
        this.entries = List.copyOf(p_298786_);
    }

    @Override
    public LootItemFunctionType<SetContainerContents> getType() {
        return LootItemFunctions.SET_CONTENTS;
    }

    @Override
    public ItemStack run(ItemStack pStack, LootContext pContext) {
        if (pStack.isEmpty()) {
            return pStack;
        } else {
            Stream.Builder<ItemStack> builder = Stream.builder();
            this.entries
                .forEach(
                    p_80916_ -> p_80916_.expand(pContext, p_287573_ -> p_287573_.createItemStack(LootTable.createStackSplitter(pContext.getLevel(), builder::add), pContext))
                );
            this.f_315714_.m_321924_(pStack, builder.build());
            return pStack;
        }
    }

    @Override
    public void validate(ValidationContext pContext) {
        super.validate(pContext);

        for (int i = 0; i < this.entries.size(); i++) {
            this.entries.get(i).validate(pContext.forChild(".entry[" + i + "]"));
        }
    }

    public static SetContainerContents.Builder setContents(ContainerComponentManipulator<?> p_328808_) {
        return new SetContainerContents.Builder(p_328808_);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<SetContainerContents.Builder> {
        private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
        private final ContainerComponentManipulator<?> f_315663_;

        public Builder(ContainerComponentManipulator<?> p_332521_) {
            this.f_315663_ = p_332521_;
        }

        protected SetContainerContents.Builder getThis() {
            return this;
        }

        public SetContainerContents.Builder withEntry(LootPoolEntryContainer.Builder<?> pLootEntryBuilder) {
            this.entries.add(pLootEntryBuilder.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetContainerContents(this.getConditions(), this.f_315663_, this.entries.build());
        }
    }
}