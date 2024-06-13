package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public interface EntityTypeTags {
    TagKey<EntityType<?>> SKELETONS = create("skeletons");
    TagKey<EntityType<?>> f_303793_ = create("zombies");
    TagKey<EntityType<?>> RAIDERS = create("raiders");
    TagKey<EntityType<?>> f_303412_ = create("undead");
    TagKey<EntityType<?>> BEEHIVE_INHABITORS = create("beehive_inhabitors");
    TagKey<EntityType<?>> ARROWS = create("arrows");
    TagKey<EntityType<?>> IMPACT_PROJECTILES = create("impact_projectiles");
    TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = create("powder_snow_walkable_mobs");
    TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = create("axolotl_always_hostiles");
    TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = create("axolotl_hunt_targets");
    TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = create("freeze_immune_entity_types");
    TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = create("freeze_hurts_extra_types");
    TagKey<EntityType<?>> f_303534_ = create("can_breathe_under_water");
    TagKey<EntityType<?>> FROG_FOOD = create("frog_food");
    TagKey<EntityType<?>> FALL_DAMAGE_IMMUNE = create("fall_damage_immune");
    TagKey<EntityType<?>> DISMOUNTS_UNDERWATER = create("dismounts_underwater");
    TagKey<EntityType<?>> NON_CONTROLLING_RIDER = create("non_controlling_rider");
    TagKey<EntityType<?>> f_316998_ = create("deflects_projectiles");
    TagKey<EntityType<?>> f_302423_ = create("can_turn_in_boats");
    TagKey<EntityType<?>> f_314047_ = create("illager");
    TagKey<EntityType<?>> f_316007_ = create("aquatic");
    TagKey<EntityType<?>> f_315418_ = create("arthropod");
    TagKey<EntityType<?>> f_314231_ = create("ignores_poison_and_regen");
    TagKey<EntityType<?>> f_316912_ = create("inverted_healing_and_harm");
    TagKey<EntityType<?>> f_314802_ = create("wither_friends");
    TagKey<EntityType<?>> f_314612_ = create("illager_friends");
    TagKey<EntityType<?>> f_314707_ = create("not_scary_for_pufferfish");
    TagKey<EntityType<?>> f_316359_ = create("sensitive_to_impaling");
    TagKey<EntityType<?>> f_314167_ = create("sensitive_to_bane_of_arthropods");
    TagKey<EntityType<?>> f_314378_ = create("sensitive_to_smite");
    TagKey<EntityType<?>> f_315192_ = create("no_anger_from_wind_charge");
    TagKey<EntityType<?>> f_315487_ = create("immune_to_oozing");
    TagKey<EntityType<?>> f_314026_ = create("immune_to_infested");
    TagKey<EntityType<?>> f_314896_ = create("redirectable_projectile");

    private static TagKey<EntityType<?>> create(String pName) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(pName));
    }
}