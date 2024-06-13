package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnPlacements {
    private static final Map<EntityType<?>, SpawnPlacements.Data> DATA_BY_TYPE = Maps.newHashMap();

    /** @deprecated FORGE: use SpawnPlacementRegisterEvent to register and modify spawn placements */
    private static <T extends Mob> void register(
        EntityType<T> pEntityType, SpawnPlacementType p_331557_, Heightmap.Types pHeightMapType, SpawnPlacements.SpawnPredicate<T> pDecoratorPredicate
    ) {
        SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.put(pEntityType, new SpawnPlacements.Data(pHeightMapType, p_331557_, pDecoratorPredicate));
        if (spawnplacements$data != null) {
            throw new IllegalStateException("Duplicate registration for type " + BuiltInRegistries.ENTITY_TYPE.getKey(pEntityType));
        }
    }

    public static SpawnPlacementType getPlacementType(EntityType<?> pEntityType) {
        SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.get(pEntityType);
        return spawnplacements$data == null ? SpawnPlacementTypes.f_315673_ : spawnplacements$data.placement;
    }

    public static boolean m_323908_(EntityType<?> p_331487_, LevelReader p_329941_, BlockPos p_327899_) {
        return getPlacementType(p_331487_).m_322997_(p_329941_, p_327899_, p_331487_);
    }

    public static Heightmap.Types getHeightmapType(@Nullable EntityType<?> pEntityType) {
        SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.get(pEntityType);
        return spawnplacements$data == null ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : spawnplacements$data.heightMap;
    }

    public static <T extends Entity> boolean checkSpawnRules(
        EntityType<T> pEntityType, ServerLevelAccessor pServerLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom
    ) {
        SpawnPlacements.Data spawnplacements$data = DATA_BY_TYPE.get(pEntityType);
        boolean vanillaResult = spawnplacements$data == null || spawnplacements$data.predicate.test((EntityType)pEntityType, pServerLevel, pSpawnType, pPos, pRandom);
        return net.minecraftforge.event.ForgeEventFactory.checkSpawnPlacements(pEntityType, pServerLevel, pSpawnType, pPos, pRandom, vanillaResult);
    }

    static {
        register(EntityType.AXOLOTL, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Axolotl::checkAxolotlSpawnRules);
        register(EntityType.COD, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.DOLPHIN, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.DROWNED, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Drowned::checkDrownedSpawnRules);
        register(EntityType.GUARDIAN, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Guardian::checkGuardianSpawnRules);
        register(EntityType.PUFFERFISH, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.SALMON, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.SQUID, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.TROPICAL_FISH, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TropicalFish::checkTropicalFishSpawnRules);
        register(EntityType.f_316265_, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Armadillo::m_319845_);
        register(EntityType.BAT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Bat::checkBatSpawnRules);
        register(EntityType.BLAZE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        register(EntityType.f_316281_, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.CAVE_SPIDER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.CHICKEN, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.COW, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.CREEPER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.DONKEY, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.ENDERMAN, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.ENDERMITE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Endermite::checkEndermiteSpawnRules);
        register(EntityType.ENDER_DRAGON, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.FROG, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Frog::checkFrogSpawnRules);
        register(EntityType.GHAST, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Ghast::checkGhastSpawnRules);
        register(EntityType.GIANT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.GLOW_SQUID, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GlowSquid::checkGlowSquidSpawnRules);
        register(EntityType.GOAT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Goat::checkGoatSpawnRules);
        register(EntityType.HORSE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.HUSK, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Husk::checkHuskSpawnRules);
        register(EntityType.IRON_GOLEM, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.LLAMA, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.MAGMA_CUBE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MagmaCube::checkMagmaCubeSpawnRules);
        register(EntityType.MOOSHROOM, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MushroomCow::checkMushroomSpawnRules);
        register(EntityType.MULE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.OCELOT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING, Ocelot::checkOcelotSpawnRules);
        register(EntityType.PARROT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING, Parrot::checkParrotSpawnRules);
        register(EntityType.PIG, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.HOGLIN, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Hoglin::checkHoglinSpawnRules);
        register(EntityType.PIGLIN, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Piglin::checkPiglinSpawnRules);
        register(EntityType.PILLAGER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
        register(EntityType.POLAR_BEAR, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PolarBear::checkPolarBearSpawnRules);
        register(EntityType.RABBIT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Rabbit::checkRabbitSpawnRules);
        register(EntityType.SHEEP, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.SILVERFISH, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Silverfish::checkSilverfishSpawnRules);
        register(EntityType.SKELETON, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.SKELETON_HORSE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SkeletonHorse::m_305737_);
        register(EntityType.SLIME, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Slime::checkSlimeSpawnRules);
        register(EntityType.SNOW_GOLEM, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.SPIDER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.STRAY, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Stray::checkStraySpawnRules);
        register(EntityType.STRIDER, SpawnPlacementTypes.f_315430_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Strider::checkStriderSpawnRules);
        register(EntityType.TURTLE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Turtle::checkTurtleSpawnRules);
        register(EntityType.VILLAGER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.WITCH, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WITHER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WITHER_SKELETON, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WOLF, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wolf::checkWolfSpawnRules);
        register(EntityType.ZOMBIE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.ZOMBIE_HORSE, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ZombieHorse::m_305739_);
        register(EntityType.ZOMBIFIED_PIGLIN, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ZombifiedPiglin::checkZombifiedPiglinSpawnRules);
        register(EntityType.ZOMBIE_VILLAGER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.CAT, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.ELDER_GUARDIAN, SpawnPlacementTypes.f_317066_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Guardian::checkGuardianSpawnRules);
        register(EntityType.EVOKER, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.FOX, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fox::checkFoxSpawnRules);
        register(EntityType.ILLUSIONER, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.PANDA, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.PHANTOM, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.RAVAGER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.SHULKER, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.TRADER_LLAMA, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.VEX, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.VINDICATOR, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WANDERING_TRADER, SpawnPlacementTypes.f_314433_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.WARDEN, SpawnPlacementTypes.f_315673_, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    static record Data(Heightmap.Types heightMap, SpawnPlacementType placement, SpawnPlacements.SpawnPredicate<?> predicate) {
    }

    @FunctionalInterface
    public interface SpawnPredicate<T extends Entity> {
        boolean test(EntityType<T> pEntityType, ServerLevelAccessor pServerLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom);
    }

    // ******* FORGE START. INTERNAL USE ONLY! ****** //
    public static void fireSpawnPlacementEvent() {
        Map<EntityType<?>, net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.MergedSpawnPredicate<?>> map = Maps.newHashMap();
        DATA_BY_TYPE.forEach((type, data) -> map.put(type, new net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.MergedSpawnPredicate<>(data.predicate, data.placement, data.heightMap)));
        net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.event.entity.SpawnPlacementRegisterEvent(map));
        map.forEach(((entityType, merged) -> DATA_BY_TYPE.put(entityType, new SpawnPlacements.Data(merged.getHeightmapType(), merged.getSpawnType(), merged.build()))));
    }
}
