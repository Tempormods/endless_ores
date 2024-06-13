package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class NestedLootTable extends LootPoolSingletonContainer {
    public static final MapCodec<NestedLootTable> f_315961_ = RecordCodecBuilder.mapCodec(
        p_331721_ -> p_331721_.group(
                    Codec.either(ResourceKey.codec(Registries.f_314309_), LootTable.f_315527_).fieldOf("value").forGetter(p_331624_ -> p_331624_.f_314988_)
                )
                .and(singletonFields(p_331721_))
                .apply(p_331721_, NestedLootTable::new)
    );
    private final Either<ResourceKey<LootTable>, LootTable> f_314988_;

    private NestedLootTable(
        Either<ResourceKey<LootTable>, LootTable> p_335218_, int p_332597_, int p_330218_, List<LootItemCondition> p_335913_, List<LootItemFunction> p_331388_
    ) {
        super(p_332597_, p_330218_, p_335913_, p_331388_);
        this.f_314988_ = p_335218_;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.f_314057_;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> p_329435_, LootContext p_332786_) {
        this.f_314988_
            .map(
                p_333276_ -> p_332786_.getResolver()
                        .m_318772_(Registries.f_314309_, (ResourceKey<LootTable>)p_333276_)
                        .map(Holder::value)
                        .orElse(LootTable.EMPTY),
                p_328175_ -> (LootTable)p_328175_
            )
            .getRandomItemsRaw(p_332786_, p_329435_);
    }

    @Override
    public void validate(ValidationContext p_331194_) {
        Optional<ResourceKey<LootTable>> optional = this.f_314988_.left();
        if (optional.isPresent()) {
            ResourceKey<LootTable> resourcekey = optional.get();
            if (p_331194_.hasVisitedElement(resourcekey)) {
                p_331194_.reportProblem("Table " + resourcekey.location() + " is recursively called");
                return;
            }
        }

        super.validate(p_331194_);
        this.f_314988_
            .ifLeft(
                p_334373_ -> p_331194_.resolver()
                        .m_318772_(Registries.f_314309_, (ResourceKey<LootTable>)p_334373_)
                        .ifPresentOrElse(
                            p_329102_ -> p_329102_.value().validate(p_331194_.enterElement("->{" + p_334373_.location() + "}", (ResourceKey<?>)p_334373_)),
                            () -> p_331194_.reportProblem("Unknown loot table called " + p_334373_.location())
                        )
            )
            .ifRight(p_333644_ -> p_333644_.validate(p_331194_.forChild("->{inline}")));
    }

    public static LootPoolSingletonContainer.Builder<?> m_320126_(ResourceKey<LootTable> p_332425_) {
        return simpleBuilder((p_331287_, p_328654_, p_335079_, p_330542_) -> new NestedLootTable(Either.left(p_332425_), p_331287_, p_328654_, p_335079_, p_330542_));
    }

    public static LootPoolSingletonContainer.Builder<?> m_325034_(LootTable p_336216_) {
        return simpleBuilder(
            (p_327921_, p_332453_, p_332156_, p_328257_) -> new NestedLootTable(Either.right(p_336216_), p_327921_, p_332453_, p_332156_, p_328257_)
        );
    }
}