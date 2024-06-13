package net.minecraft.data.advancements.packs;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LightningBoltPredicate;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.TargetBlockTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class VanillaAdventureAdvancements implements AdvancementSubProvider {
    private static final int DISTANCE_FROM_BOTTOM_TO_TOP = 384;
    private static final int Y_COORDINATE_AT_TOP = 320;
    private static final int Y_COORDINATE_AT_BOTTOM = -64;
    private static final int BEDROCK_THICKNESS = 5;
    protected static final List<EntityType<?>> MOBS_TO_KILL = Arrays.asList(
        EntityType.BLAZE,
        EntityType.CAVE_SPIDER,
        EntityType.CREEPER,
        EntityType.DROWNED,
        EntityType.ELDER_GUARDIAN,
        EntityType.ENDER_DRAGON,
        EntityType.ENDERMAN,
        EntityType.ENDERMITE,
        EntityType.EVOKER,
        EntityType.GHAST,
        EntityType.GUARDIAN,
        EntityType.HOGLIN,
        EntityType.HUSK,
        EntityType.MAGMA_CUBE,
        EntityType.PHANTOM,
        EntityType.PIGLIN,
        EntityType.PIGLIN_BRUTE,
        EntityType.PILLAGER,
        EntityType.RAVAGER,
        EntityType.SHULKER,
        EntityType.SILVERFISH,
        EntityType.SKELETON,
        EntityType.SLIME,
        EntityType.SPIDER,
        EntityType.STRAY,
        EntityType.VEX,
        EntityType.VINDICATOR,
        EntityType.WITCH,
        EntityType.WITHER_SKELETON,
        EntityType.WITHER,
        EntityType.ZOGLIN,
        EntityType.ZOMBIE_VILLAGER,
        EntityType.ZOMBIE,
        EntityType.ZOMBIFIED_PIGLIN
    );

    private static Criterion<LightningStrikeTrigger.TriggerInstance> fireCountAndBystander(MinMaxBounds.Ints pFireCount, Optional<EntityPredicate> pBystander) {
        return LightningStrikeTrigger.TriggerInstance.lightningStrike(
            Optional.of(
                EntityPredicate.Builder.entity()
                    .distance(DistancePredicate.absolute(MinMaxBounds.Doubles.atMost(30.0)))
                    .subPredicate(LightningBoltPredicate.blockSetOnFire(pFireCount))
                    .build()
            ),
            pBystander
        );
    }

    private static Criterion<UsingItemTrigger.TriggerInstance> lookAtThroughItem(EntityType<?> pEntity, Item pItem) {
        return UsingItemTrigger.TriggerInstance.lookingAt(
            EntityPredicate.Builder.entity()
                .subPredicate(PlayerPredicate.Builder.player().setLookingAt(EntityPredicate.Builder.entity().of(pEntity)).build()),
            ItemPredicate.Builder.item().of(pItem)
        );
    }

    @Override
    public void generate(HolderLookup.Provider pRegistries, Consumer<AdvancementHolder> pWriter) {
        AdvancementHolder advancementholder = Advancement.Builder.advancement()
            .display(
                Items.MAP,
                Component.translatable("advancements.adventure.root.title"),
                Component.translatable("advancements.adventure.root.description"),
                new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"),
                AdvancementType.TASK,
                false,
                false,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion("killed_something", KilledTrigger.TriggerInstance.playerKilledEntity())
            .addCriterion("killed_by_something", KilledTrigger.TriggerInstance.entityKilledPlayer())
            .save(pWriter, "adventure/root");
        AdvancementHolder advancementholder1 = Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Blocks.RED_BED,
                Component.translatable("advancements.adventure.sleep_in_bed.title"),
                Component.translatable("advancements.adventure.sleep_in_bed.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("slept_in_bed", PlayerTrigger.TriggerInstance.sleptInBed())
            .save(pWriter, "adventure/sleep_in_bed");
        createAdventuringTime(pRegistries, pWriter, advancementholder1, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
        AdvancementHolder advancementholder2 = Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.EMERALD,
                Component.translatable("advancements.adventure.trade.title"),
                Component.translatable("advancements.adventure.trade.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("traded", TradeTrigger.TriggerInstance.tradedWithVillager())
            .save(pWriter, "adventure/trade");
        Advancement.Builder.advancement()
            .parent(advancementholder2)
            .display(
                Items.EMERALD,
                Component.translatable("advancements.adventure.trade_at_world_height.title"),
                Component.translatable("advancements.adventure.trade_at_world_height.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "trade_at_world_height",
                TradeTrigger.TriggerInstance.tradedWithVillager(
                    EntityPredicate.Builder.entity().located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(319.0)))
                )
            )
            .save(pWriter, "adventure/trade_at_world_height");
        AdvancementHolder advancementholder3 = m_305193_(advancementholder, pWriter, MOBS_TO_KILL);
        AdvancementHolder advancementholder4 = Advancement.Builder.advancement()
            .parent(advancementholder3)
            .display(
                Items.BOW,
                Component.translatable("advancements.adventure.shoot_arrow.title"),
                Component.translatable("advancements.adventure.shoot_arrow.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "shot_arrow",
                PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
                    DamagePredicate.Builder.damageInstance()
                        .type(
                            DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                                .direct(EntityPredicate.Builder.entity().of(EntityTypeTags.ARROWS))
                        )
                )
            )
            .save(pWriter, "adventure/shoot_arrow");
        AdvancementHolder advancementholder5 = Advancement.Builder.advancement()
            .parent(advancementholder3)
            .display(
                Items.TRIDENT,
                Component.translatable("advancements.adventure.throw_trident.title"),
                Component.translatable("advancements.adventure.throw_trident.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "shot_trident",
                PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
                    DamagePredicate.Builder.damageInstance()
                        .type(
                            DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                                .direct(EntityPredicate.Builder.entity().of(EntityType.TRIDENT))
                        )
                )
            )
            .save(pWriter, "adventure/throw_trident");
        Advancement.Builder.advancement()
            .parent(advancementholder5)
            .display(
                Items.TRIDENT,
                Component.translatable("advancements.adventure.very_very_frightening.title"),
                Component.translatable("advancements.adventure.very_very_frightening.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("struck_villager", ChanneledLightningTrigger.TriggerInstance.channeledLightning(EntityPredicate.Builder.entity().of(EntityType.VILLAGER)))
            .save(pWriter, "adventure/very_very_frightening");
        Advancement.Builder.advancement()
            .parent(advancementholder2)
            .display(
                Blocks.CARVED_PUMPKIN,
                Component.translatable("advancements.adventure.summon_iron_golem.title"),
                Component.translatable("advancements.adventure.summon_iron_golem.description"),
                null,
                AdvancementType.GOAL,
                true,
                true,
                false
            )
            .addCriterion("summoned_golem", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.IRON_GOLEM)))
            .save(pWriter, "adventure/summon_iron_golem");
        Advancement.Builder.advancement()
            .parent(advancementholder4)
            .display(
                Items.ARROW,
                Component.translatable("advancements.adventure.sniper_duel.title"),
                Component.translatable("advancements.adventure.sniper_duel.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(50))
            .addCriterion(
                "killed_skeleton",
                KilledTrigger.TriggerInstance.playerKilledEntity(
                    EntityPredicate.Builder.entity()
                        .of(EntityType.SKELETON)
                        .distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(50.0))),
                    DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                )
            )
            .save(pWriter, "adventure/sniper_duel");
        Advancement.Builder.advancement()
            .parent(advancementholder3)
            .display(
                Items.TOTEM_OF_UNDYING,
                Component.translatable("advancements.adventure.totem_of_undying.title"),
                Component.translatable("advancements.adventure.totem_of_undying.description"),
                null,
                AdvancementType.GOAL,
                true,
                true,
                false
            )
            .addCriterion("used_totem", UsedTotemTrigger.TriggerInstance.usedTotem(Items.TOTEM_OF_UNDYING))
            .save(pWriter, "adventure/totem_of_undying");
        AdvancementHolder advancementholder6 = Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.CROSSBOW,
                Component.translatable("advancements.adventure.ol_betsy.title"),
                Component.translatable("advancements.adventure.ol_betsy.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("shot_crossbow", ShotCrossbowTrigger.TriggerInstance.shotCrossbow(Items.CROSSBOW))
            .save(pWriter, "adventure/ol_betsy");
        Advancement.Builder.advancement()
            .parent(advancementholder6)
            .display(
                Items.CROSSBOW,
                Component.translatable("advancements.adventure.whos_the_pillager_now.title"),
                Component.translatable("advancements.adventure.whos_the_pillager_now.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("kill_pillager", KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().of(EntityType.PILLAGER)))
            .save(pWriter, "adventure/whos_the_pillager_now");
        Advancement.Builder.advancement()
            .parent(advancementholder6)
            .display(
                Items.CROSSBOW,
                Component.translatable("advancements.adventure.two_birds_one_arrow.title"),
                Component.translatable("advancements.adventure.two_birds_one_arrow.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(65))
            .addCriterion(
                "two_birds",
                KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(
                    EntityPredicate.Builder.entity().of(EntityType.PHANTOM), EntityPredicate.Builder.entity().of(EntityType.PHANTOM)
                )
            )
            .save(pWriter, "adventure/two_birds_one_arrow");
        Advancement.Builder.advancement()
            .parent(advancementholder6)
            .display(
                Items.CROSSBOW,
                Component.translatable("advancements.adventure.arbalistic.title"),
                Component.translatable("advancements.adventure.arbalistic.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                true
            )
            .rewards(AdvancementRewards.Builder.experience(85))
            .addCriterion("arbalistic", KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(MinMaxBounds.Ints.exactly(5)))
            .save(pWriter, "adventure/arbalistic");
        HolderLookup.RegistryLookup<BannerPattern> registrylookup = pRegistries.lookupOrThrow(Registries.BANNER_PATTERN);
        AdvancementHolder advancementholder7 = Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Raid.getLeaderBannerInstance(registrylookup),
                Component.translatable("advancements.adventure.voluntary_exile.title"),
                Component.translatable("advancements.adventure.voluntary_exile.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                true
            )
            .addCriterion(
                "voluntary_exile",
                KilledTrigger.TriggerInstance.playerKilledEntity(
                    EntityPredicate.Builder.entity().of(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.m_322698_(registrylookup))
                )
            )
            .save(pWriter, "adventure/voluntary_exile");
        Advancement.Builder.advancement()
            .parent(advancementholder7)
            .display(
                Raid.getLeaderBannerInstance(registrylookup),
                Component.translatable("advancements.adventure.hero_of_the_village.title"),
                Component.translatable("advancements.adventure.hero_of_the_village.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                true
            )
            .rewards(AdvancementRewards.Builder.experience(100))
            .addCriterion("hero_of_the_village", PlayerTrigger.TriggerInstance.raidWon())
            .save(pWriter, "adventure/hero_of_the_village");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Blocks.HONEY_BLOCK.asItem(),
                Component.translatable("advancements.adventure.honey_block_slide.title"),
                Component.translatable("advancements.adventure.honey_block_slide.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("honey_block_slide", SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(Blocks.HONEY_BLOCK))
            .save(pWriter, "adventure/honey_block_slide");
        Advancement.Builder.advancement()
            .parent(advancementholder4)
            .display(
                Blocks.TARGET.asItem(),
                Component.translatable("advancements.adventure.bullseye.title"),
                Component.translatable("advancements.adventure.bullseye.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(50))
            .addCriterion(
                "bullseye",
                TargetBlockTrigger.TriggerInstance.targetHit(
                    MinMaxBounds.Ints.exactly(15),
                    Optional.of(
                        EntityPredicate.wrap(
                            EntityPredicate.Builder.entity().distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(30.0)))
                        )
                    )
                )
            )
            .save(pWriter, "adventure/bullseye");
        Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.LEATHER_BOOTS,
                Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"),
                Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("walk_on_powder_snow_with_leather_boots", PlayerTrigger.TriggerInstance.walkOnBlockWithEquipment(Blocks.POWDER_SNOW, Items.LEATHER_BOOTS))
            .save(pWriter, "adventure/walk_on_powder_snow_with_leather_boots");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.LIGHTNING_ROD,
                Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.title"),
                Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "lightning_rod_with_villager_no_fire",
                fireCountAndBystander(MinMaxBounds.Ints.exactly(0), Optional.of(EntityPredicate.Builder.entity().of(EntityType.VILLAGER).build()))
            )
            .save(pWriter, "adventure/lightning_rod_with_villager_no_fire");
        AdvancementHolder advancementholder8 = Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.SPYGLASS,
                Component.translatable("advancements.adventure.spyglass_at_parrot.title"),
                Component.translatable("advancements.adventure.spyglass_at_parrot.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("spyglass_at_parrot", lookAtThroughItem(EntityType.PARROT, Items.SPYGLASS))
            .save(pWriter, "adventure/spyglass_at_parrot");
        AdvancementHolder advancementholder9 = Advancement.Builder.advancement()
            .parent(advancementholder8)
            .display(
                Items.SPYGLASS,
                Component.translatable("advancements.adventure.spyglass_at_ghast.title"),
                Component.translatable("advancements.adventure.spyglass_at_ghast.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("spyglass_at_ghast", lookAtThroughItem(EntityType.GHAST, Items.SPYGLASS))
            .save(pWriter, "adventure/spyglass_at_ghast");
        Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.JUKEBOX,
                Component.translatable("advancements.adventure.play_jukebox_in_meadows.title"),
                Component.translatable("advancements.adventure.play_jukebox_in_meadows.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "play_jukebox_in_meadows",
                ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                    LocationPredicate.Builder.location()
                        .m_320551_(HolderSet.direct(pRegistries.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.MEADOW)))
                        .setBlock(BlockPredicate.Builder.block().of(Blocks.JUKEBOX)),
                    ItemPredicate.Builder.item().of(ItemTags.MUSIC_DISCS)
                )
            )
            .save(pWriter, "adventure/play_jukebox_in_meadows");
        Advancement.Builder.advancement()
            .parent(advancementholder9)
            .display(
                Items.SPYGLASS,
                Component.translatable("advancements.adventure.spyglass_at_dragon.title"),
                Component.translatable("advancements.adventure.spyglass_at_dragon.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("spyglass_at_dragon", lookAtThroughItem(EntityType.ENDER_DRAGON, Items.SPYGLASS))
            .save(pWriter, "adventure/spyglass_at_dragon");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.WATER_BUCKET,
                Component.translatable("advancements.adventure.fall_from_world_height.title"),
                Component.translatable("advancements.adventure.fall_from_world_height.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "fall_from_world_height",
                DistanceTrigger.TriggerInstance.fallFromHeight(
                    EntityPredicate.Builder.entity().located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atMost(-59.0))),
                    DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(379.0)),
                    LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(319.0))
                )
            )
            .save(pWriter, "adventure/fall_from_world_height");
        Advancement.Builder.advancement()
            .parent(advancementholder3)
            .display(
                Blocks.SCULK_CATALYST,
                Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.title"),
                Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .addCriterion("kill_mob_near_sculk_catalyst", KilledTrigger.TriggerInstance.playerKilledEntityNearSculkCatalyst())
            .save(pWriter, "adventure/kill_mob_near_sculk_catalyst");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Blocks.SCULK_SENSOR,
                Component.translatable("advancements.adventure.avoid_vibration.title"),
                Component.translatable("advancements.adventure.avoid_vibration.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("avoid_vibration", PlayerTrigger.TriggerInstance.avoidVibration())
            .save(pWriter, "adventure/avoid_vibration");
        AdvancementHolder advancementholder10 = respectingTheRemnantsCriterions(Advancement.Builder.advancement())
            .parent(advancementholder)
            .display(
                Items.BRUSH,
                Component.translatable("advancements.adventure.salvage_sherd.title"),
                Component.translatable("advancements.adventure.salvage_sherd.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(pWriter, "adventure/salvage_sherd");
        Advancement.Builder.advancement()
            .parent(advancementholder10)
            .display(
                DecoratedPotBlockEntity.createDecoratedPotItem(
                    new PotDecorations(Optional.empty(), Optional.of(Items.HEART_POTTERY_SHERD), Optional.empty(), Optional.of(Items.EXPLORER_POTTERY_SHERD))
                ),
                Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.title"),
                Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "pot_crafted_using_only_sherds",
                RecipeCraftedTrigger.TriggerInstance.craftedItem(
                    new ResourceLocation("minecraft:decorated_pot"),
                    List.of(
                        ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS),
                        ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS),
                        ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS),
                        ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS)
                    )
                )
            )
            .save(pWriter, "adventure/craft_decorated_pot_using_only_sherds");
        AdvancementHolder advancementholder11 = craftingANewLook(Advancement.Builder.advancement())
            .parent(advancementholder)
            .display(
                new ItemStack(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE),
                Component.translatable("advancements.adventure.trim_with_any_armor_pattern.title"),
                Component.translatable("advancements.adventure.trim_with_any_armor_pattern.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .save(pWriter, "adventure/trim_with_any_armor_pattern");
        smithingWithStyle(Advancement.Builder.advancement())
            .parent(advancementholder11)
            .display(
                new ItemStack(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE),
                Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.title"),
                Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(150))
            .save(pWriter, "adventure/trim_with_all_exclusive_armor_patterns");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.CHISELED_BOOKSHELF,
                Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.title"),
                Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .addCriterion("chiseled_bookshelf", placedBlockReadByComparator(Blocks.CHISELED_BOOKSHELF))
            .addCriterion("comparator", placedComparatorReadingBlock(Blocks.CHISELED_BOOKSHELF))
            .save(pWriter, "adventure/read_power_of_chiseled_bookshelf");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.f_314244_,
                Component.translatable("advancements.adventure.brush_armadillo.title"),
                Component.translatable("advancements.adventure.brush_armadillo.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "brush_armadillo",
                PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                    ItemPredicate.Builder.item().of(Items.BRUSH),
                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.f_316265_)))
                )
            )
            .save(pWriter, "adventure/brush_armadillo");
    }

    public static AdvancementHolder m_305193_(AdvancementHolder p_309635_, Consumer<AdvancementHolder> p_309544_, List<EntityType<?>> p_310276_) {
        AdvancementHolder advancementholder = addMobsToKill(Advancement.Builder.advancement(), p_310276_)
            .parent(p_309635_)
            .display(
                Items.IRON_SWORD,
                Component.translatable("advancements.adventure.kill_a_mob.title"),
                Component.translatable("advancements.adventure.kill_a_mob.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .requirements(AdvancementRequirements.Strategy.OR)
            .save(p_309544_, "adventure/kill_a_mob");
        addMobsToKill(Advancement.Builder.advancement(), p_310276_)
            .parent(advancementholder)
            .display(
                Items.DIAMOND_SWORD,
                Component.translatable("advancements.adventure.kill_all_mobs.title"),
                Component.translatable("advancements.adventure.kill_all_mobs.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(100))
            .save(p_309544_, "adventure/kill_all_mobs");
        return advancementholder;
    }

    private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlockReadByComparator(Block pBlock) {
        LootItemCondition.Builder[] alootitemcondition$builder = ComparatorBlock.FACING
            .getPossibleValues()
            .stream()
            .map(
                p_296341_ -> {
                    StatePropertiesPredicate.Builder statepropertiespredicate$builder = StatePropertiesPredicate.Builder.properties()
                        .hasProperty(ComparatorBlock.FACING, p_296341_);
                    BlockPredicate.Builder blockpredicate$builder = BlockPredicate.Builder.block()
                        .of(Blocks.COMPARATOR)
                        .setProperties(statepropertiespredicate$builder);
                    return LocationCheck.checkLocation(
                        LocationPredicate.Builder.location().setBlock(blockpredicate$builder), new BlockPos(p_296341_.getOpposite().getNormal())
                    );
                }
            )
            .toArray(LootItemCondition.Builder[]::new);
        return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(
            LootItemBlockStatePropertyCondition.hasBlockStateProperties(pBlock), AnyOfCondition.anyOf(alootitemcondition$builder)
        );
    }

    private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedComparatorReadingBlock(Block pBlock) {
        LootItemCondition.Builder[] alootitemcondition$builder = ComparatorBlock.FACING
            .getPossibleValues()
            .stream()
            .map(
                p_286190_ -> {
                    StatePropertiesPredicate.Builder statepropertiespredicate$builder = StatePropertiesPredicate.Builder.properties()
                        .hasProperty(ComparatorBlock.FACING, p_286190_);
                    LootItemBlockStatePropertyCondition.Builder lootitemblockstatepropertycondition$builder = new LootItemBlockStatePropertyCondition.Builder(
                            Blocks.COMPARATOR
                        )
                        .setProperties(statepropertiespredicate$builder);
                    LootItemCondition.Builder lootitemcondition$builder = LocationCheck.checkLocation(
                        LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(pBlock)),
                        new BlockPos(p_286190_.getNormal())
                    );
                    return AllOfCondition.allOf(lootitemblockstatepropertycondition$builder, lootitemcondition$builder);
                }
            )
            .toArray(LootItemCondition.Builder[]::new);
        return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(AnyOfCondition.anyOf(alootitemcondition$builder));
    }

    private static Advancement.Builder smithingWithStyle(Advancement.Builder pBuilder) {
        pBuilder.requirements(AdvancementRequirements.Strategy.AND);
        Set<Item> set = Set.of(
            Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE
        );
        VanillaRecipeProvider.smithingTrims()
            .filter(p_308497_ -> set.contains(p_308497_.f_303140_()))
            .forEach(
                p_325838_ -> pBuilder.addCriterion(
                        "armor_trimmed_" + p_325838_.f_302799_(), RecipeCraftedTrigger.TriggerInstance.craftedItem(p_325838_.f_302799_())
                    )
            );
        return pBuilder;
    }

    private static Advancement.Builder craftingANewLook(Advancement.Builder pBuilder) {
        pBuilder.requirements(AdvancementRequirements.Strategy.OR);
        VanillaRecipeProvider.smithingTrims()
            .map(VanillaRecipeProvider.TrimTemplate::f_302799_)
            .forEach(p_325836_ -> pBuilder.addCriterion("armor_trimmed_" + p_325836_, RecipeCraftedTrigger.TriggerInstance.craftedItem(p_325836_)));
        return pBuilder;
    }

    private static Advancement.Builder respectingTheRemnantsCriterions(Advancement.Builder pBuilder) {
        List<Pair<String, Criterion<LootTableTrigger.TriggerInstance>>> list = List.of(
            Pair.of("desert_pyramid", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY)),
            Pair.of("desert_well", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY)),
            Pair.of("ocean_ruin_cold", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY)),
            Pair.of("ocean_ruin_warm", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY)),
            Pair.of("trail_ruins_rare", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE)),
            Pair.of("trail_ruins_common", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON))
        );
        list.forEach(p_308495_ -> pBuilder.addCriterion(p_308495_.getFirst(), p_308495_.getSecond()));
        String s = "has_sherd";
        pBuilder.addCriterion("has_sherd", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.DECORATED_POT_SHERDS)));
        pBuilder.requirements(new AdvancementRequirements(List.of(list.stream().map(Pair::getFirst).toList(), List.of("has_sherd"))));
        return pBuilder;
    }

    protected static void createAdventuringTime(
        HolderLookup.Provider p_334518_,
        Consumer<AdvancementHolder> pConsumer,
        AdvancementHolder pAdvancement,
        MultiNoiseBiomeSourceParameterList.Preset pPreset
    ) {
        addBiomes(Advancement.Builder.advancement(), p_334518_, pPreset.usedBiomes().toList())
            .parent(pAdvancement)
            .display(
                Items.DIAMOND_BOOTS,
                Component.translatable("advancements.adventure.adventuring_time.title"),
                Component.translatable("advancements.adventure.adventuring_time.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(500))
            .save(pConsumer, "adventure/adventuring_time");
    }

    private static Advancement.Builder addMobsToKill(Advancement.Builder pBuilder, List<EntityType<?>> p_309412_) {
        p_309412_.forEach(
            p_308489_ -> pBuilder.addCriterion(
                    BuiltInRegistries.ENTITY_TYPE.getKey((EntityType<?>)p_308489_).toString(),
                    KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of((EntityType<?>)p_308489_))
                )
        );
        return pBuilder;
    }

    protected static Advancement.Builder addBiomes(Advancement.Builder pBuilder, HolderLookup.Provider p_334548_, List<ResourceKey<Biome>> pBiomes) {
        HolderGetter<Biome> holdergetter = p_334548_.lookupOrThrow(Registries.BIOME);

        for (ResourceKey<Biome> resourcekey : pBiomes) {
            pBuilder.addCriterion(
                resourcekey.location().toString(),
                PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(holdergetter.getOrThrow(resourcekey)))
            );
        }

        return pBuilder;
    }
}