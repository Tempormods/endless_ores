package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.ArmadilloModel;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.CamelModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.SnifferModel;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.TadpoleModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.WindChargeModel;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerDefinitions {
    private static final CubeDeformation FISH_PATTERN_DEFORMATION = new CubeDeformation(0.008F);
    public static final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0F);
    public static final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5F);

    public static Map<ModelLayerLocation, LayerDefinition> createRoots() {
        Builder<ModelLayerLocation, LayerDefinition> builder = ImmutableMap.builder();
        LayerDefinition layerdefinition = LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
        LayerDefinition layerdefinition1 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32);
        LayerDefinition layerdefinition2 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.02F)), 64, 32);
        LayerDefinition layerdefinition3 = LayerDefinition.create(HumanoidArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION), 64, 32);
        LayerDefinition layerdefinition4 = MinecartModel.createBodyLayer();
        LayerDefinition layerdefinition5 = SkullModel.createMobHeadLayer();
        LayerDefinition layerdefinition6 = LayerDefinition.create(HorseModel.createBodyMesh(CubeDeformation.NONE), 64, 64);
        LayerDefinition layerdefinition7 = IllagerModel.createBodyLayer();
        LayerDefinition layerdefinition8 = CowModel.createBodyLayer();
        LayerDefinition layerdefinition9 = LayerDefinition.create(OcelotModel.createBodyMesh(CubeDeformation.NONE), 64, 32);
        LayerDefinition layerdefinition10 = LayerDefinition.create(PiglinModel.createMesh(CubeDeformation.NONE), 64, 64);
        LayerDefinition layerdefinition11 = LayerDefinition.create(PiglinHeadModel.createHeadModel(), 64, 64);
        LayerDefinition layerdefinition12 = SkullModel.createHumanoidHeadLayer();
        LayerDefinition layerdefinition13 = LlamaModel.createBodyLayer(CubeDeformation.NONE);
        LayerDefinition layerdefinition14 = StriderModel.createBodyLayer();
        LayerDefinition layerdefinition15 = HoglinModel.createBodyLayer();
        LayerDefinition layerdefinition16 = SkeletonModel.createBodyLayer();
        LayerDefinition layerdefinition17 = LayerDefinition.create(VillagerModel.createBodyModel(), 64, 64);
        LayerDefinition layerdefinition18 = SpiderModel.createSpiderBodyLayer();
        builder.put(ModelLayers.ALLAY, AllayModel.createBodyLayer());
        builder.put(ModelLayers.ARMADILLO, ArmadilloModel.createBodyLayer());
        builder.put(ModelLayers.ARMOR_STAND, ArmorStandModel.createBodyLayer());
        builder.put(ModelLayers.ARMOR_STAND_INNER_ARMOR, ArmorStandArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
        builder.put(ModelLayers.ARMOR_STAND_OUTER_ARMOR, ArmorStandArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION));
        builder.put(ModelLayers.AXOLOTL, AxolotlModel.createBodyLayer());
        builder.put(ModelLayers.BANNER, BannerRenderer.createBodyLayer());
        builder.put(ModelLayers.BAT, BatModel.createBodyLayer());
        builder.put(ModelLayers.BED_FOOT, BedRenderer.createFootLayer());
        builder.put(ModelLayers.BED_HEAD, BedRenderer.createHeadLayer());
        builder.put(ModelLayers.BEE, BeeModel.createBodyLayer());
        builder.put(ModelLayers.BELL, BellRenderer.createBodyLayer());
        builder.put(ModelLayers.BLAZE, BlazeModel.createBodyLayer());
        builder.put(ModelLayers.BOGGED, BoggedModel.createBodyLayer());
        builder.put(ModelLayers.BOGGED_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.BOGGED_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.BOGGED_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.2F), 0.0F), 64, 32));
        builder.put(ModelLayers.BOOK, BookModel.createBodyLayer());
        builder.put(ModelLayers.BREEZE, BreezeModel.createBodyLayer(32, 32));
        builder.put(ModelLayers.CAT, layerdefinition9);
        builder.put(ModelLayers.CAT_COLLAR, LayerDefinition.create(OcelotModel.createBodyMesh(new CubeDeformation(0.01F)), 64, 32));
        builder.put(ModelLayers.CAMEL, CamelModel.createBodyLayer());
        builder.put(ModelLayers.CAVE_SPIDER, layerdefinition18);
        builder.put(ModelLayers.CHEST, ChestRenderer.createSingleBodyLayer());
        builder.put(ModelLayers.CHEST_MINECART, layerdefinition4);
        builder.put(ModelLayers.CHICKEN, ChickenModel.createBodyLayer());
        builder.put(ModelLayers.COD, CodModel.createBodyLayer());
        builder.put(ModelLayers.COMMAND_BLOCK_MINECART, layerdefinition4);
        builder.put(ModelLayers.CONDUIT_EYE, ConduitRenderer.createEyeLayer());
        builder.put(ModelLayers.CONDUIT_WIND, ConduitRenderer.createWindLayer());
        builder.put(ModelLayers.CONDUIT_SHELL, ConduitRenderer.createShellLayer());
        builder.put(ModelLayers.CONDUIT_CAGE, ConduitRenderer.createCageLayer());
        builder.put(ModelLayers.COW, layerdefinition8);
        builder.put(ModelLayers.CREEPER, CreeperModel.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.CREEPER_ARMOR, CreeperModel.createBodyLayer(new CubeDeformation(2.0F)));
        builder.put(ModelLayers.CREEPER_HEAD, layerdefinition5);
        builder.put(ModelLayers.DECORATED_POT_BASE, DecoratedPotRenderer.createBaseLayer());
        builder.put(ModelLayers.DECORATED_POT_SIDES, DecoratedPotRenderer.createSidesLayer());
        builder.put(ModelLayers.DOLPHIN, DolphinModel.createBodyLayer());
        builder.put(ModelLayers.DONKEY, ChestedHorseModel.createBodyLayer());
        builder.put(ModelLayers.DOUBLE_CHEST_LEFT, ChestRenderer.createDoubleBodyLeftLayer());
        builder.put(ModelLayers.DOUBLE_CHEST_RIGHT, ChestRenderer.createDoubleBodyRightLayer());
        builder.put(ModelLayers.DRAGON_SKULL, DragonHeadModel.createHeadLayer());
        builder.put(ModelLayers.DROWNED, DrownedModel.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.DROWNED_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.DROWNED_OUTER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.DROWNED_OUTER_LAYER, DrownedModel.createBodyLayer(new CubeDeformation(0.25F)));
        builder.put(ModelLayers.ELDER_GUARDIAN, GuardianModel.createBodyLayer());
        builder.put(ModelLayers.ELYTRA, ElytraModel.createLayer());
        builder.put(ModelLayers.ENDERMAN, EndermanModel.createBodyLayer());
        builder.put(ModelLayers.ENDERMITE, EndermiteModel.createBodyLayer());
        builder.put(ModelLayers.ENDER_DRAGON, EnderDragonRenderer.createBodyLayer());
        builder.put(ModelLayers.END_CRYSTAL, EndCrystalRenderer.createBodyLayer());
        builder.put(ModelLayers.EVOKER, layerdefinition7);
        builder.put(ModelLayers.EVOKER_FANGS, EvokerFangsModel.createBodyLayer());
        builder.put(ModelLayers.FOX, FoxModel.createBodyLayer());
        builder.put(ModelLayers.FROG, FrogModel.createBodyLayer());
        builder.put(ModelLayers.FURNACE_MINECART, layerdefinition4);
        builder.put(ModelLayers.GHAST, GhastModel.createBodyLayer());
        builder.put(ModelLayers.GIANT, layerdefinition);
        builder.put(ModelLayers.GIANT_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.GIANT_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.GLOW_SQUID, SquidModel.createBodyLayer());
        builder.put(ModelLayers.GOAT, GoatModel.createBodyLayer());
        builder.put(ModelLayers.GUARDIAN, GuardianModel.createBodyLayer());
        builder.put(ModelLayers.HOGLIN, layerdefinition15);
        builder.put(ModelLayers.HOPPER_MINECART, layerdefinition4);
        builder.put(ModelLayers.HORSE, layerdefinition6);
        builder.put(ModelLayers.HORSE_ARMOR, LayerDefinition.create(HorseModel.createBodyMesh(new CubeDeformation(0.1F)), 64, 64));
        builder.put(ModelLayers.HUSK, layerdefinition);
        builder.put(ModelLayers.HUSK_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.HUSK_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.ILLUSIONER, layerdefinition7);
        builder.put(ModelLayers.IRON_GOLEM, IronGolemModel.createBodyLayer());
        builder.put(ModelLayers.LEASH_KNOT, LeashKnotModel.createBodyLayer());
        builder.put(ModelLayers.LLAMA, layerdefinition13);
        builder.put(ModelLayers.LLAMA_DECOR, LlamaModel.createBodyLayer(new CubeDeformation(0.5F)));
        builder.put(ModelLayers.LLAMA_SPIT, LlamaSpitModel.createBodyLayer());
        builder.put(ModelLayers.MAGMA_CUBE, LavaSlimeModel.createBodyLayer());
        builder.put(ModelLayers.MINECART, layerdefinition4);
        builder.put(ModelLayers.MOOSHROOM, layerdefinition8);
        builder.put(ModelLayers.MULE, ChestedHorseModel.createBodyLayer());
        builder.put(ModelLayers.OCELOT, layerdefinition9);
        builder.put(ModelLayers.PANDA, PandaModel.createBodyLayer());
        builder.put(ModelLayers.PARROT, ParrotModel.createBodyLayer());
        builder.put(ModelLayers.PHANTOM, PhantomModel.createBodyLayer());
        builder.put(ModelLayers.PIG, PigModel.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.PIG_SADDLE, PigModel.createBodyLayer(new CubeDeformation(0.5F)));
        builder.put(ModelLayers.PIGLIN, layerdefinition10);
        builder.put(ModelLayers.PIGLIN_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.PIGLIN_OUTER_ARMOR, layerdefinition2);
        builder.put(ModelLayers.PIGLIN_BRUTE, layerdefinition10);
        builder.put(ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, layerdefinition2);
        builder.put(ModelLayers.PIGLIN_HEAD, layerdefinition11);
        builder.put(ModelLayers.PILLAGER, layerdefinition7);
        builder.put(ModelLayers.PLAYER, LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));
        builder.put(ModelLayers.PLAYER_HEAD, layerdefinition12);
        builder.put(ModelLayers.PLAYER_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.PLAYER_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.PLAYER_SLIM, LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, true), 64, 64));
        builder.put(ModelLayers.PLAYER_SLIM_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.PLAYER_SLIM_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.PLAYER_SPIN_ATTACK, SpinAttackEffectLayer.createLayer());
        builder.put(ModelLayers.POLAR_BEAR, PolarBearModel.createBodyLayer());
        builder.put(ModelLayers.PUFFERFISH_BIG, PufferfishBigModel.createBodyLayer());
        builder.put(ModelLayers.PUFFERFISH_MEDIUM, PufferfishMidModel.createBodyLayer());
        builder.put(ModelLayers.PUFFERFISH_SMALL, PufferfishSmallModel.createBodyLayer());
        builder.put(ModelLayers.RABBIT, RabbitModel.createBodyLayer());
        builder.put(ModelLayers.RAVAGER, RavagerModel.createBodyLayer());
        builder.put(ModelLayers.SALMON, SalmonModel.createBodyLayer());
        builder.put(ModelLayers.SHEEP, SheepModel.createBodyLayer());
        builder.put(ModelLayers.SHEEP_FUR, SheepFurModel.createFurLayer());
        builder.put(ModelLayers.SHIELD, ShieldModel.createLayer());
        builder.put(ModelLayers.SHULKER, ShulkerModel.createBodyLayer());
        builder.put(ModelLayers.SHULKER_BULLET, ShulkerBulletModel.createBodyLayer());
        builder.put(ModelLayers.SILVERFISH, SilverfishModel.createBodyLayer());
        builder.put(ModelLayers.SKELETON, layerdefinition16);
        builder.put(ModelLayers.SKELETON_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.SKELETON_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.SKELETON_HORSE, layerdefinition6);
        builder.put(ModelLayers.SKELETON_SKULL, layerdefinition5);
        builder.put(ModelLayers.SLIME, SlimeModel.createInnerBodyLayer());
        builder.put(ModelLayers.SLIME_OUTER, SlimeModel.createOuterBodyLayer());
        builder.put(ModelLayers.SNIFFER, SnifferModel.createBodyLayer());
        builder.put(ModelLayers.SNOW_GOLEM, SnowGolemModel.createBodyLayer());
        builder.put(ModelLayers.SPAWNER_MINECART, layerdefinition4);
        builder.put(ModelLayers.SPIDER, layerdefinition18);
        builder.put(ModelLayers.SQUID, SquidModel.createBodyLayer());
        builder.put(ModelLayers.STRAY, layerdefinition16);
        builder.put(ModelLayers.STRAY_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.STRAY_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.STRAY_OUTER_LAYER, LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F), 64, 32));
        builder.put(ModelLayers.STRIDER, layerdefinition14);
        builder.put(ModelLayers.STRIDER_SADDLE, layerdefinition14);
        builder.put(ModelLayers.TADPOLE, TadpoleModel.createBodyLayer());
        builder.put(ModelLayers.TNT_MINECART, layerdefinition4);
        builder.put(ModelLayers.TRADER_LLAMA, layerdefinition13);
        builder.put(ModelLayers.TRIDENT, TridentModel.createLayer());
        builder.put(ModelLayers.TROPICAL_FISH_LARGE, TropicalFishModelB.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.TROPICAL_FISH_LARGE_PATTERN, TropicalFishModelB.createBodyLayer(FISH_PATTERN_DEFORMATION));
        builder.put(ModelLayers.TROPICAL_FISH_SMALL, TropicalFishModelA.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.TROPICAL_FISH_SMALL_PATTERN, TropicalFishModelA.createBodyLayer(FISH_PATTERN_DEFORMATION));
        builder.put(ModelLayers.TURTLE, TurtleModel.createBodyLayer());
        builder.put(ModelLayers.VEX, VexModel.createBodyLayer());
        builder.put(ModelLayers.VILLAGER, layerdefinition17);
        builder.put(ModelLayers.VINDICATOR, layerdefinition7);
        builder.put(ModelLayers.WARDEN, WardenModel.createBodyLayer());
        builder.put(ModelLayers.WANDERING_TRADER, layerdefinition17);
        builder.put(ModelLayers.WIND_CHARGE, WindChargeModel.createBodyLayer());
        builder.put(ModelLayers.WITCH, WitchModel.createBodyLayer());
        builder.put(ModelLayers.WITHER, WitherBossModel.createBodyLayer(CubeDeformation.NONE));
        builder.put(ModelLayers.WITHER_ARMOR, WitherBossModel.createBodyLayer(INNER_ARMOR_DEFORMATION));
        builder.put(ModelLayers.WITHER_SKULL, WitherSkullRenderer.createSkullLayer());
        builder.put(ModelLayers.WITHER_SKELETON, layerdefinition16);
        builder.put(ModelLayers.WITHER_SKELETON_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.WITHER_SKELETON_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.WITHER_SKELETON_SKULL, layerdefinition5);
        builder.put(ModelLayers.WOLF, LayerDefinition.create(WolfModel.createMeshDefinition(CubeDeformation.NONE), 64, 32));
        builder.put(ModelLayers.WOLF_ARMOR, LayerDefinition.create(WolfModel.createMeshDefinition(new CubeDeformation(0.2F)), 64, 32));
        builder.put(ModelLayers.ZOGLIN, layerdefinition15);
        builder.put(ModelLayers.ZOMBIE, layerdefinition);
        builder.put(ModelLayers.ZOMBIE_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.ZOMBIE_OUTER_ARMOR, layerdefinition1);
        builder.put(ModelLayers.ZOMBIE_HEAD, layerdefinition12);
        builder.put(ModelLayers.ZOMBIE_HORSE, layerdefinition6);
        builder.put(ModelLayers.ZOMBIE_VILLAGER, ZombieVillagerModel.createBodyLayer());
        builder.put(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR, ZombieVillagerModel.createArmorLayer(INNER_ARMOR_DEFORMATION));
        builder.put(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, ZombieVillagerModel.createArmorLayer(OUTER_ARMOR_DEFORMATION));
        builder.put(ModelLayers.ZOMBIFIED_PIGLIN, layerdefinition10);
        builder.put(ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, layerdefinition3);
        builder.put(ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, layerdefinition2);
        LayerDefinition layerdefinition19 = BoatModel.createBodyModel();
        LayerDefinition layerdefinition20 = ChestBoatModel.createBodyModel();
        LayerDefinition layerdefinition21 = RaftModel.createBodyModel();
        LayerDefinition layerdefinition22 = ChestRaftModel.createBodyModel();

        for (Boat.Type boat$type : Boat.Type.values()) {
            if (boat$type == Boat.Type.BAMBOO) {
                builder.put(ModelLayers.createBoatModelName(boat$type), layerdefinition21);
                builder.put(ModelLayers.createChestBoatModelName(boat$type), layerdefinition22);
            } else {
                builder.put(ModelLayers.createBoatModelName(boat$type), layerdefinition19);
                builder.put(ModelLayers.createChestBoatModelName(boat$type), layerdefinition20);
            }
        }

        LayerDefinition layerdefinition23 = SignRenderer.createSignLayer();
        WoodType.values().forEach(p_171114_ -> builder.put(ModelLayers.createSignModelName(p_171114_), layerdefinition23));
        LayerDefinition layerdefinition24 = HangingSignRenderer.createHangingSignLayer();
        WoodType.values().forEach(p_247864_ -> builder.put(ModelLayers.createHangingSignModelName(p_247864_), layerdefinition24));
        net.minecraftforge.client.ForgeHooksClient.loadLayerDefinitions(builder);
        ImmutableMap<ModelLayerLocation, LayerDefinition> immutablemap = builder.build();
        List<ModelLayerLocation> list = ModelLayers.getKnownLocations().filter(p_171117_ -> !immutablemap.containsKey(p_171117_)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing layer definitions: " + list);
        } else {
            return immutablemap;
        }
    }
}
