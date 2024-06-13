package net.minecraft.data.loot.packs;

import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SetOminousBottleAmplifierFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class UpdateOneTwentyOneChestLoot implements LootTableSubProvider {
    @Override
    public void generate(HolderLookup.Provider p_335293_, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_309902_) {
        p_309902_.accept(
            BuiltInLootTables.f_302850_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_303173_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.WATER_BUCKET).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_303584_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.WATER_BUCKET).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))).setWeight(4))
                        .add(
                            LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))).setWeight(4)
                        )
                        .add(
                            LootItem.lootTableItem(Items.SNOWBALL).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))).setWeight(6)
                        )
                        .add(
                            LootItem.lootTableItem(Items.EGG).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.FIRE_CHARGE).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F))).setWeight(6)
                        )
                        .add(
                            LootItem.lootTableItem(Items.SPLASH_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.SLOWNESS))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.SPLASH_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.POISON))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.SPLASH_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.WEAKNESS))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.SLOWNESS))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.POISON))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.WEAKNESS))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetPotionFunction.setPotion(Potions.HEALING))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .setWeight(1)
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_302648_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))).setWeight(100)
                        )
                        .add(
                            LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(100)
                        )
                        .add(
                            LootItem.lootTableItem(Items.AMETHYST_SHARD).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(100)
                        )
                        .add(
                            LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(100)
                        )
                        .add(
                            LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))).setWeight(50)
                        )
                        .add(
                            LootItem.lootTableItem(Items.COPPER_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))).setWeight(50)
                        )
                        .add(
                            LootItem.lootTableItem(Items.f_302928_).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))).setWeight(20)
                        )
                        .add(
                            LootItem.lootTableItem(Items.GOLD_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 6.0F))).setWeight(20)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(5)
                        )
                        .add(
                            LootItem.lootTableItem(Items.EMERALD_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))).setWeight(5)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(1)
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_303341_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(UniformGenerator.between(3.0F, 5.0F))
                        .add(
                            LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 14.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.TIPPED_ARROW)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.POISON))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.TIPPED_ARROW)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.SLOWNESS))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAKED_POTATO).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.GLOW_BERRIES).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.ACACIA_PLANKS).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.MOSS_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BONE_MEAL).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.TUFF).apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 10.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.REGENERATION))
                        )
                        .add(
                            LootItem.lootTableItem(Items.POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.STRENGTH))
                        )
                        .add(
                            LootItem.lootTableItem(Items.STONE_PICKAXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(2)
                        )
                        .add(LootItem.lootTableItem(Items.MILK_BUCKET).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_303376_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(UniformGenerator.between(2.0F, 3.0F))
                        .add(LootItem.lootTableItem(Items.f_302928_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))).setWeight(1))
                        .add(
                            LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).setWeight(5)
                        )
                        .add(LootItem.lootTableItem(Items.WOODEN_AXE).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))).setWeight(10))
                        .add(
                            LootItem.lootTableItem(Items.HONEYCOMB).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F))).setWeight(10)
                        )
                        .add(
                            LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 10.0F))).setWeight(10)
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_302884_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 3.0F))
                        .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))).setWeight(1))
                        .add(
                            LootItem.lootTableItem(Items.EMERALD_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))).setWeight(5)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_AXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.5F)))
                                .setWeight(5)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.1F, 0.5F)))
                                .setWeight(5)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(10)
                        )
                        .add(
                            LootItem.lootTableItem(Items.CAKE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(20)
                        )
                        .add(
                            LootItem.lootTableItem(Items.AMETHYST_SHARD).apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0F, 20.0F))).setWeight(20)
                        )
                        .add(
                            LootItem.lootTableItem(Items.IRON_BLOCK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(20)
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_302208_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 3.0F))
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_AXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.4F, 0.9F)))
                                .apply(EnchantRandomlyFunction.randomApplicableEnchantment())
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.COMPASS)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BUCKET).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.GOLDEN_AXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(4)
                        )
                        .add(
                            LootItem.lootTableItem(Items.GOLDEN_PICKAXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(4)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAMBOO_PLANKS).apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 15.0F))).setWeight(10)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAKED_POTATO).apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 10.0F))).setWeight(10)
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_302226_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1.0F, 3.0F))
                        .add(
                            LootItem.lootTableItem(Items.IRON_AXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.4F, 0.9F)))
                                .apply(EnchantRandomlyFunction.randomApplicableEnchantment())
                                .setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.HONEYCOMB).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F))).setWeight(1)
                        )
                        .add(
                            LootItem.lootTableItem(Items.STONE_AXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.STONE_PICKAXE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.15F, 0.8F)))
                                .setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.ENDER_PEARL).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAMBOO_HANGING_SIGN).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAMBOO_PLANKS).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.SCAFFOLDING).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 10.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.TORCH).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F))).setWeight(2)
                        )
                        .add(
                            LootItem.lootTableItem(Items.TUFF).apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0F, 20.0F))).setWeight(3)
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315354_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.EMERALD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.SHIELD).setWeight(3).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.5F, 1.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOW)
                                .setWeight(3)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(5.0F, 15.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.CROSSBOW)
                                .setWeight(2)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(5.0F, 20.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.IRON_AXE)
                                .setWeight(2)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(0.0F, 10.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.IRON_CHESTPLATE)
                                .setWeight(2)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(0.0F, 10.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.GOLDEN_CARROT).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOOK)
                                .setWeight(2)
                                .apply(
                                    new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.SHARPNESS)
                                        .withEnchantment(Enchantments.BANE_OF_ARTHROPODS)
                                        .withEnchantment(Enchantments.f_316758_)
                                        .withEnchantment(Enchantments.f_316753_)
                                        .withEnchantment(Enchantments.SILK_TOUCH)
                                        .withEnchantment(Enchantments.f_315602_)
                                )
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOOK)
                                .setWeight(2)
                                .apply(
                                    new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.RIPTIDE)
                                        .withEnchantment(Enchantments.LOYALTY)
                                        .withEnchantment(Enchantments.CHANNELING)
                                        .withEnchantment(Enchantments.IMPALING)
                                        .withEnchantment(Enchantments.MENDING)
                                )
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE)
                                .setWeight(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(5.0F, 15.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_AXE)
                                .setWeight(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(5.0F, 15.0F)).allowTreasure())
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315207_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.ARROW).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.TIPPED_ARROW)
                                .setWeight(4)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.POISON))
                        )
                        .add(
                            LootItem.lootTableItem(Items.EMERALD).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.f_315945_).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.IRON_INGOT).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.HONEY_BOTTLE).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.f_316650_)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetOminousBottleAmplifierFunction.m_323203_(UniformGenerator.between(0.0F, 1.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.f_315945_).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 12.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND).setWeight(1).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_314271_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(6))
                        .add(LootItem.lootTableItem(Items.f_314806_).setWeight(3))
                        .add(LootItem.lootTableItem(Items.f_316480_).setWeight(2))
                        .add(LootItem.lootTableItem(Items.TRIDENT).setWeight(1))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_303517_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(NestedLootTable.m_320126_(BuiltInLootTables.f_315354_).setWeight(8))
                        .add(NestedLootTable.m_320126_(BuiltInLootTables.f_315207_).setWeight(2))
                )
                .withPool(
                    LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F)).add(NestedLootTable.m_320126_(BuiltInLootTables.f_315207_))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemRandomChanceCondition.randomChance(0.25F))
                        .add(NestedLootTable.m_320126_(BuiltInLootTables.f_314271_))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315822_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.EMERALD_BLOCK).setWeight(6))
                        .add(LootItem.lootTableItem(Items.IRON_BLOCK).setWeight(4))
                        .add(
                            LootItem.lootTableItem(Items.CROSSBOW)
                                .setWeight(4)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(5.0F, 20.0F)).allowTreasure())
                        )
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(4))
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_AXE)
                                .setWeight(3)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(10.0F, 20.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND_CHESTPLATE)
                                .setWeight(3)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(UniformGenerator.between(10.0F, 20.0F)).allowTreasure())
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOOK)
                                .setWeight(2)
                                .apply(
                                    new EnchantRandomlyFunction.Builder()
                                        .withEnchantment(Enchantments.KNOCKBACK)
                                        .withEnchantment(Enchantments.f_316860_)
                                        .withEnchantment(Enchantments.SMITE)
                                        .withEnchantment(Enchantments.f_316023_)
                                        .withEnchantment(Enchantments.MULTISHOT)
                                )
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOOK)
                                .setWeight(2)
                                .apply(new EnchantRandomlyFunction.Builder().withEnchantment(Enchantments.f_316771_).withEnchantment(Enchantments.f_314294_))
                        )
                        .add(
                            LootItem.lootTableItem(Items.BOOK).setWeight(1).apply(new EnchantRandomlyFunction.Builder().withEnchantment(Enchantments.f_316259_))
                        )
                        .add(LootItem.lootTableItem(Items.DIAMOND_BLOCK).setWeight(1))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315306_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.EMERALD).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 10.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.f_315945_).setWeight(4).apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0F, 12.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.TIPPED_ARROW)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 12.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.STRONG_SLOWNESS))
                        )
                        .add(
                            LootItem.lootTableItem(Items.DIAMOND).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.f_316650_)
                                .setWeight(1)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetOminousBottleAmplifierFunction.m_323203_(UniformGenerator.between(2.0F, 4.0F)))
                        )
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315133_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(3))
                        .add(LootItem.lootTableItem(Items.f_316167_).setWeight(3))
                        .add(LootItem.lootTableItem(Items.f_316582_).setWeight(2))
                        .add(LootItem.lootTableItem(Items.f_314540_).setWeight(1))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315697_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(NestedLootTable.m_320126_(BuiltInLootTables.f_315822_).setWeight(8))
                        .add(NestedLootTable.m_320126_(BuiltInLootTables.f_315306_).setWeight(2))
                )
                .withPool(
                    LootPool.lootPool().setRolls(UniformGenerator.between(1.0F, 3.0F)).add(NestedLootTable.m_320126_(BuiltInLootTables.f_315306_))
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemRandomChanceCondition.randomChance(0.75F))
                        .add(NestedLootTable.m_320126_(BuiltInLootTables.f_315133_))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_303815_,
            LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.f_302928_)))
        );
        p_309902_.accept(
            BuiltInLootTables.f_303325_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.COOKED_CHICKEN).setWeight(3).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                        .add(
                            LootItem.lootTableItem(Items.BREAD).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAKED_POTATO).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.REGENERATION))
                        )
                        .add(
                            LootItem.lootTableItem(Items.POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.SWIFTNESS))
                        )
                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_316043_,
            LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.f_314905_)))
        );
        p_309902_.accept(
            BuiltInLootTables.f_316831_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.COOKED_BEEF).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.BAKED_POTATO).setWeight(3).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.GOLDEN_CARROT).setWeight(2).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                        )
                        .add(
                            LootItem.lootTableItem(Items.POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.REGENERATION))
                        )
                        .add(
                            LootItem.lootTableItem(Items.POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.STRENGTH))
                        )
                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
                )
        );
        p_309902_.accept(
            BuiltInLootTables.f_315958_,
            LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.f_313979_))
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.f_316406_))
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.f_314032_))
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.f_315215_))
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.STRENGTH))
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.SWIFTNESS))
                        )
                        .add(
                            LootItem.lootTableItem(Items.LINGERING_POTION)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.SLOW_FALLING))
                        )
                )
                .withPool(
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))))
                        .add(
                            LootItem.lootTableItem(Items.ARROW)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.POISON))
                        )
                        .add(
                            LootItem.lootTableItem(Items.ARROW)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(SetPotionFunction.setPotion(Potions.STRONG_SLOWNESS))
                        )
                        .add(LootItem.lootTableItem(Items.FIRE_CHARGE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.f_315945_).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
                )
        );
    }
}