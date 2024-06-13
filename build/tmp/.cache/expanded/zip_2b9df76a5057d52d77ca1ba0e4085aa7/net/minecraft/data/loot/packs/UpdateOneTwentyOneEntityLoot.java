package net.minecraft.data.loot.packs;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.RaiderPredicate;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetOminousBottleAmplifierFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class UpdateOneTwentyOneEntityLoot extends EntityLootSubProvider {
    protected UpdateOneTwentyOneEntityLoot() {
        super(FeatureFlagSet.of(FeatureFlags.VANILLA, FeatureFlags.f_302467_), FeatureFlagSet.of(FeatureFlags.f_302467_));
    }

    @Override
    public void generate() {
        this.add(
            EntityType.f_302782_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.f_315544_)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(1.0F, 2.0F)))
                        )
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                )
        );
        this.add(
            EntityType.f_316281_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.ARROW)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.TIPPED_ARROW)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)).setLimit(1))
                                .apply(SetPotionFunction.setPotion(Potions.POISON))
                        )
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                )
        );
        this.add(
            EntityType.PILLAGER,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.f_316650_)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetOminousBottleAmplifierFunction.m_323203_(UniformGenerator.between(0.0F, 4.0F)))
                        )
                        .when(
                            LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(RaiderPredicate.f_316933_)
                            )
                        )
                )
        );
    }
}