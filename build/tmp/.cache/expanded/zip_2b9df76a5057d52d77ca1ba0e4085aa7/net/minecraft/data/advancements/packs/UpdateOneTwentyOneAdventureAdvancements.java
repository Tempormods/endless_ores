package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FallAfterExplosionTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class UpdateOneTwentyOneAdventureAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider p_312808_, Consumer<AdvancementHolder> p_313044_) {
        AdvancementHolder advancementholder = AdvancementSubProvider.m_306985_("adventure/root");
        VanillaAdventureAdvancements.m_305193_(
            advancementholder,
            p_313044_,
            Stream.concat(VanillaAdventureAdvancements.MOBS_TO_KILL.stream(), Stream.of(EntityType.f_302782_, EntityType.f_316281_)).collect(Collectors.toList())
        );
        AdvancementHolder advancementholder1 = Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Blocks.f_302743_,
                Component.translatable("advancements.adventure.minecraft_trials_edition.title"),
                Component.translatable("advancements.adventure.minecraft_trials_edition.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "minecraft_trials_edition",
                PlayerTrigger.TriggerInstance.located(
                    LocationPredicate.Builder.inStructure(p_312808_.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.f_303057_))
                )
            )
            .save(p_313044_, "adventure/minecraft_trials_edition");
        Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.f_302606_,
                Component.translatable("advancements.adventure.lighten_up.title"),
                Component.translatable("advancements.adventure.lighten_up.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "lighten_up",
                ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                    LocationPredicate.Builder.location()
                        .setBlock(
                            BlockPredicate.Builder.block()
                                .of(Blocks.f_302668_, Blocks.f_303674_, Blocks.f_303271_, Blocks.f_302347_, Blocks.f_302556_, Blocks.f_303797_)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CopperBulbBlock.f_302965_, true))
                        ),
                    ItemPredicate.Builder.item().of(VanillaHusbandryAdvancements.WAX_SCRAPING_TOOLS)
                )
            )
            .save(p_313044_, "adventure/lighten_up");
        AdvancementHolder advancementholder2 = Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.f_302928_,
                Component.translatable("advancements.adventure.under_lock_and_key.title"),
                Component.translatable("advancements.adventure.under_lock_and_key.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "under_lock_and_key",
                ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                    LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.f_316985_)),
                    ItemPredicate.Builder.item().of(Items.f_302928_)
                )
            )
            .save(p_313044_, "adventure/under_lock_and_key");
        Advancement.Builder.advancement()
            .parent(advancementholder2)
            .display(
                Items.f_314905_,
                Component.translatable("advancements.adventure.revaulting.title"),
                Component.translatable("advancements.adventure.revaulting.description"),
                null,
                AdvancementType.GOAL,
                true,
                true,
                false
            )
            .addCriterion(
                "revaulting",
                ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
                    LocationPredicate.Builder.location()
                        .setBlock(
                            BlockPredicate.Builder.block()
                                .of(Blocks.f_316985_)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VaultBlock.f_317007_, true))
                        ),
                    ItemPredicate.Builder.item().of(Items.f_314905_)
                )
            )
            .save(p_313044_, "adventure/revaulting");
        Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.f_315945_,
                Component.translatable("advancements.adventure.blowback.title"),
                Component.translatable("advancements.adventure.blowback.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(40))
            .addCriterion(
                "blowback",
                KilledTrigger.TriggerInstance.playerKilledEntity(
                    EntityPredicate.Builder.entity().of(EntityType.f_302782_),
                    DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                        .direct(EntityPredicate.Builder.entity().of(EntityType.f_315936_))
                )
            )
            .save(p_313044_, "adventure/blowback");
        Advancement.Builder.advancement()
            .parent(advancementholder)
            .display(
                Items.f_303457_,
                Component.translatable("advancements.adventure.crafters_crafting_crafters.title"),
                Component.translatable("advancements.adventure.crafters_crafting_crafters.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion("crafter_crafted_crafter", RecipeCraftedTrigger.TriggerInstance.m_321438_(new ResourceLocation("minecraft:crafter")))
            .save(p_313044_, "adventure/crafters_crafting_crafters");
        Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.f_315945_,
                Component.translatable("advancements.adventure.who_needs_rockets.title"),
                Component.translatable("advancements.adventure.who_needs_rockets.description"),
                null,
                AdvancementType.TASK,
                true,
                true,
                false
            )
            .addCriterion(
                "who_needs_rockets",
                FallAfterExplosionTrigger.TriggerInstance.m_324889_(
                    DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(7.0)), EntityPredicate.Builder.entity().of(EntityType.f_303421_)
                )
            )
            .save(p_313044_, "adventure/who_needs_rockets");
        Advancement.Builder.advancement()
            .parent(advancementholder1)
            .display(
                Items.f_314862_,
                Component.translatable("advancements.adventure.overoverkill.title"),
                Component.translatable("advancements.adventure.overoverkill.description"),
                null,
                AdvancementType.CHALLENGE,
                true,
                true,
                false
            )
            .rewards(AdvancementRewards.Builder.experience(50))
            .addCriterion(
                "overoverkill",
                PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
                    DamagePredicate.Builder.damageInstance()
                        .dealtDamage(MinMaxBounds.Doubles.atLeast(100.0))
                        .type(
                            DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(DamageTypeTags.f_314599_))
                                .direct(
                                    EntityPredicate.Builder.entity()
                                        .of(EntityType.PLAYER)
                                        .equipment(
                                            EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(Items.f_314862_))
                                        )
                                )
                        )
                )
            )
            .save(p_313044_, "adventure/overoverkill");
    }
}