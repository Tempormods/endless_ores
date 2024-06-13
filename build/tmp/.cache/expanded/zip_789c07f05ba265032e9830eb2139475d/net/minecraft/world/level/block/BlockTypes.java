package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;

public class BlockTypes {
    public static final MapCodec<Block> CODEC = BuiltInRegistries.BLOCK_TYPE.byNameCodec().dispatchMap(Block::codec, Function.identity());

    public static MapCodec<? extends Block> bootstrap(Registry<MapCodec<? extends Block>> p_310746_) {
        Registry.register(p_310746_, "block", Block.CODEC);
        Registry.register(p_310746_, "air", AirBlock.CODEC);
        Registry.register(p_310746_, "amethyst", AmethystBlock.CODEC);
        Registry.register(p_310746_, "amethyst_cluster", AmethystClusterBlock.CODEC);
        Registry.register(p_310746_, "anvil", AnvilBlock.CODEC);
        Registry.register(p_310746_, "attached_stem", AttachedStemBlock.CODEC);
        Registry.register(p_310746_, "azalea", AzaleaBlock.CODEC);
        Registry.register(p_310746_, "bamboo_sapling", BambooSaplingBlock.CODEC);
        Registry.register(p_310746_, "bamboo_stalk", BambooStalkBlock.CODEC);
        Registry.register(p_310746_, "banner", BannerBlock.CODEC);
        Registry.register(p_310746_, "barrel", BarrelBlock.CODEC);
        Registry.register(p_310746_, "barrier", BarrierBlock.CODEC);
        Registry.register(p_310746_, "base_coral_fan", BaseCoralFanBlock.CODEC);
        Registry.register(p_310746_, "base_coral_plant", BaseCoralPlantBlock.CODEC);
        Registry.register(p_310746_, "base_coral_wall_fan", BaseCoralWallFanBlock.CODEC);
        Registry.register(p_310746_, "beacon", BeaconBlock.CODEC);
        Registry.register(p_310746_, "bed", BedBlock.CODEC);
        Registry.register(p_310746_, "beehive", BeehiveBlock.CODEC);
        Registry.register(p_310746_, "beetroot", BeetrootBlock.CODEC);
        Registry.register(p_310746_, "bell", BellBlock.CODEC);
        Registry.register(p_310746_, "big_dripleaf", BigDripleafBlock.CODEC);
        Registry.register(p_310746_, "big_dripleaf_stem", BigDripleafStemBlock.CODEC);
        Registry.register(p_310746_, "blast_furnace", BlastFurnaceBlock.CODEC);
        Registry.register(p_310746_, "brewing_stand", BrewingStandBlock.CODEC);
        Registry.register(p_310746_, "brushable", BrushableBlock.CODEC);
        Registry.register(p_310746_, "bubble_column", BubbleColumnBlock.CODEC);
        Registry.register(p_310746_, "budding_amethyst", BuddingAmethystBlock.CODEC);
        Registry.register(p_310746_, "button", ButtonBlock.CODEC);
        Registry.register(p_310746_, "cactus", CactusBlock.CODEC);
        Registry.register(p_310746_, "cake", CakeBlock.CODEC);
        Registry.register(p_310746_, "calibrated_sculk_sensor", CalibratedSculkSensorBlock.CODEC);
        Registry.register(p_310746_, "campfire", CampfireBlock.CODEC);
        Registry.register(p_310746_, "candle_cake", CandleCakeBlock.CODEC);
        Registry.register(p_310746_, "candle", CandleBlock.CODEC);
        Registry.register(p_310746_, "carpet", CarpetBlock.CODEC);
        Registry.register(p_310746_, "carrot", CarrotBlock.CODEC);
        Registry.register(p_310746_, "cartography_table", CartographyTableBlock.CODEC);
        Registry.register(p_310746_, "carved_pumpkin", EquipableCarvedPumpkinBlock.CODEC);
        Registry.register(p_310746_, "cauldron", CauldronBlock.CODEC);
        Registry.register(p_310746_, "cave_vines", CaveVinesBlock.CODEC);
        Registry.register(p_310746_, "cave_vines_plant", CaveVinesPlantBlock.CODEC);
        Registry.register(p_310746_, "ceiling_hanging_sign", CeilingHangingSignBlock.CODEC);
        Registry.register(p_310746_, "chain", ChainBlock.CODEC);
        Registry.register(p_310746_, "cherry_leaves", CherryLeavesBlock.CODEC);
        Registry.register(p_310746_, "chest", ChestBlock.CODEC);
        Registry.register(p_310746_, "chiseled_book_shelf", ChiseledBookShelfBlock.CODEC);
        Registry.register(p_310746_, "chorus_flower", ChorusFlowerBlock.CODEC);
        Registry.register(p_310746_, "chorus_plant", ChorusPlantBlock.CODEC);
        Registry.register(p_310746_, "cocoa", CocoaBlock.CODEC);
        Registry.register(p_310746_, "colored_falling", ColoredFallingBlock.CODEC);
        Registry.register(p_310746_, "command", CommandBlock.CODEC);
        Registry.register(p_310746_, "comparator", ComparatorBlock.CODEC);
        Registry.register(p_310746_, "composter", ComposterBlock.CODEC);
        Registry.register(p_310746_, "concrete_powder", ConcretePowderBlock.CODEC);
        Registry.register(p_310746_, "conduit", ConduitBlock.CODEC);
        Registry.register(p_310746_, "copper_bulb_block", CopperBulbBlock.CODEC);
        Registry.register(p_310746_, "coral", CoralBlock.CODEC);
        Registry.register(p_310746_, "coral_fan", CoralFanBlock.CODEC);
        Registry.register(p_310746_, "coral_plant", CoralPlantBlock.CODEC);
        Registry.register(p_310746_, "coral_wall_fan", CoralWallFanBlock.CODEC);
        Registry.register(p_310746_, "crafter", CrafterBlock.CODEC);
        Registry.register(p_310746_, "crafting_table", CraftingTableBlock.CODEC);
        Registry.register(p_310746_, "crop", CropBlock.CODEC);
        Registry.register(p_310746_, "crying_obsidian", CryingObsidianBlock.CODEC);
        Registry.register(p_310746_, "daylight_detector", DaylightDetectorBlock.CODEC);
        Registry.register(p_310746_, "dead_bush", DeadBushBlock.CODEC);
        Registry.register(p_310746_, "decorated_pot", DecoratedPotBlock.CODEC);
        Registry.register(p_310746_, "detector_rail", DetectorRailBlock.CODEC);
        Registry.register(p_310746_, "dirt_path", DirtPathBlock.CODEC);
        Registry.register(p_310746_, "dispenser", DispenserBlock.CODEC);
        Registry.register(p_310746_, "door", DoorBlock.CODEC);
        Registry.register(p_310746_, "double_plant", DoublePlantBlock.CODEC);
        Registry.register(p_310746_, "dragon_egg", DragonEggBlock.CODEC);
        Registry.register(p_310746_, "drop_experience", DropExperienceBlock.CODEC);
        Registry.register(p_310746_, "dropper", DropperBlock.CODEC);
        Registry.register(p_310746_, "enchantment_table", EnchantingTableBlock.CODEC);
        Registry.register(p_310746_, "ender_chest", EnderChestBlock.CODEC);
        Registry.register(p_310746_, "end_gateway", EndGatewayBlock.CODEC);
        Registry.register(p_310746_, "end_portal", EndPortalBlock.CODEC);
        Registry.register(p_310746_, "end_portal_frame", EndPortalFrameBlock.CODEC);
        Registry.register(p_310746_, "end_rod", EndRodBlock.CODEC);
        Registry.register(p_310746_, "farm", FarmBlock.CODEC);
        Registry.register(p_310746_, "fence", FenceBlock.CODEC);
        Registry.register(p_310746_, "fence_gate", FenceGateBlock.CODEC);
        Registry.register(p_310746_, "fire", FireBlock.CODEC);
        Registry.register(p_310746_, "fletching_table", FletchingTableBlock.CODEC);
        Registry.register(p_310746_, "flower", FlowerBlock.CODEC);
        Registry.register(p_310746_, "flower_pot", FlowerPotBlock.CODEC);
        Registry.register(p_310746_, "frogspawn", FrogspawnBlock.CODEC);
        Registry.register(p_310746_, "frosted_ice", FrostedIceBlock.CODEC);
        Registry.register(p_310746_, "fungus", FungusBlock.CODEC);
        Registry.register(p_310746_, "furnace", FurnaceBlock.CODEC);
        Registry.register(p_310746_, "glazed_terracotta", GlazedTerracottaBlock.CODEC);
        Registry.register(p_310746_, "glow_lichen", GlowLichenBlock.CODEC);
        Registry.register(p_310746_, "grass", GrassBlock.CODEC);
        Registry.register(p_310746_, "grindstone", GrindstoneBlock.CODEC);
        Registry.register(p_310746_, "half_transparent", HalfTransparentBlock.CODEC);
        Registry.register(p_310746_, "hanging_roots", HangingRootsBlock.CODEC);
        Registry.register(p_310746_, "hay", HayBlock.CODEC);
        Registry.register(p_310746_, "heavy_core", HeavyCoreBlock.CODEC);
        Registry.register(p_310746_, "honey", HoneyBlock.CODEC);
        Registry.register(p_310746_, "hopper", HopperBlock.CODEC);
        Registry.register(p_310746_, "huge_mushroom", HugeMushroomBlock.CODEC);
        Registry.register(p_310746_, "ice", IceBlock.CODEC);
        Registry.register(p_310746_, "infested", InfestedBlock.CODEC);
        Registry.register(p_310746_, "infested_rotated_pillar", InfestedRotatedPillarBlock.CODEC);
        Registry.register(p_310746_, "iron_bars", IronBarsBlock.CODEC);
        Registry.register(p_310746_, "jack_o_lantern", CarvedPumpkinBlock.CODEC);
        Registry.register(p_310746_, "jigsaw", JigsawBlock.CODEC);
        Registry.register(p_310746_, "jukebox", JukeboxBlock.CODEC);
        Registry.register(p_310746_, "kelp", KelpBlock.CODEC);
        Registry.register(p_310746_, "kelp_plant", KelpPlantBlock.CODEC);
        Registry.register(p_310746_, "ladder", LadderBlock.CODEC);
        Registry.register(p_310746_, "lantern", LanternBlock.CODEC);
        Registry.register(p_310746_, "lava_cauldron", LavaCauldronBlock.CODEC);
        Registry.register(p_310746_, "layered_cauldron", LayeredCauldronBlock.CODEC);
        Registry.register(p_310746_, "leaves", LeavesBlock.CODEC);
        Registry.register(p_310746_, "lectern", LecternBlock.CODEC);
        Registry.register(p_310746_, "lever", LeverBlock.CODEC);
        Registry.register(p_310746_, "light", LightBlock.CODEC);
        Registry.register(p_310746_, "lightning_rod", LightningRodBlock.CODEC);
        Registry.register(p_310746_, "liquid", LiquidBlock.CODEC);
        Registry.register(p_310746_, "loom", LoomBlock.CODEC);
        Registry.register(p_310746_, "magma", MagmaBlock.CODEC);
        Registry.register(p_310746_, "mangrove_leaves", MangroveLeavesBlock.CODEC);
        Registry.register(p_310746_, "mangrove_propagule", MangrovePropaguleBlock.CODEC);
        Registry.register(p_310746_, "mangrove_roots", MangroveRootsBlock.CODEC);
        Registry.register(p_310746_, "moss", MossBlock.CODEC);
        Registry.register(p_310746_, "moving_piston", MovingPistonBlock.CODEC);
        Registry.register(p_310746_, "mud", MudBlock.CODEC);
        Registry.register(p_310746_, "mushroom", MushroomBlock.CODEC);
        Registry.register(p_310746_, "mycelium", MyceliumBlock.CODEC);
        Registry.register(p_310746_, "nether_portal", NetherPortalBlock.CODEC);
        Registry.register(p_310746_, "netherrack", NetherrackBlock.CODEC);
        Registry.register(p_310746_, "nether_sprouts", NetherSproutsBlock.CODEC);
        Registry.register(p_310746_, "nether_wart", NetherWartBlock.CODEC);
        Registry.register(p_310746_, "note", NoteBlock.CODEC);
        Registry.register(p_310746_, "nylium", NyliumBlock.CODEC);
        Registry.register(p_310746_, "observer", ObserverBlock.CODEC);
        Registry.register(p_310746_, "piglinwallskull", PiglinWallSkullBlock.CODEC);
        Registry.register(p_310746_, "pink_petals", PinkPetalsBlock.CODEC);
        Registry.register(p_310746_, "piston_base", PistonBaseBlock.CODEC);
        Registry.register(p_310746_, "piston_head", PistonHeadBlock.CODEC);
        Registry.register(p_310746_, "pitcher_crop", PitcherCropBlock.CODEC);
        Registry.register(p_310746_, "player_head", PlayerHeadBlock.CODEC);
        Registry.register(p_310746_, "player_wall_head", PlayerWallHeadBlock.CODEC);
        Registry.register(p_310746_, "pointed_dripstone", PointedDripstoneBlock.CODEC);
        Registry.register(p_310746_, "potato", PotatoBlock.CODEC);
        Registry.register(p_310746_, "powder_snow", PowderSnowBlock.CODEC);
        Registry.register(p_310746_, "powered", PoweredBlock.CODEC);
        Registry.register(p_310746_, "powered_rail", PoweredRailBlock.CODEC);
        Registry.register(p_310746_, "pressure_plate", PressurePlateBlock.CODEC);
        Registry.register(p_310746_, "pumpkin", PumpkinBlock.CODEC);
        Registry.register(p_310746_, "rail", RailBlock.CODEC);
        Registry.register(p_310746_, "redstone_lamp", RedstoneLampBlock.CODEC);
        Registry.register(p_310746_, "redstone_ore", RedStoneOreBlock.CODEC);
        Registry.register(p_310746_, "redstone_torch", RedstoneTorchBlock.CODEC);
        Registry.register(p_310746_, "redstone_wall_torch", RedstoneWallTorchBlock.CODEC);
        Registry.register(p_310746_, "redstone_wire", RedStoneWireBlock.CODEC);
        Registry.register(p_310746_, "repeater", RepeaterBlock.CODEC);
        Registry.register(p_310746_, "respawn_anchor", RespawnAnchorBlock.CODEC);
        Registry.register(p_310746_, "rooted_dirt", RootedDirtBlock.CODEC);
        Registry.register(p_310746_, "roots", RootsBlock.CODEC);
        Registry.register(p_310746_, "rotated_pillar", RotatedPillarBlock.CODEC);
        Registry.register(p_310746_, "sapling", SaplingBlock.CODEC);
        Registry.register(p_310746_, "scaffolding", ScaffoldingBlock.CODEC);
        Registry.register(p_310746_, "sculk_catalyst", SculkCatalystBlock.CODEC);
        Registry.register(p_310746_, "sculk", SculkBlock.CODEC);
        Registry.register(p_310746_, "sculk_sensor", SculkSensorBlock.CODEC);
        Registry.register(p_310746_, "sculk_shrieker", SculkShriekerBlock.CODEC);
        Registry.register(p_310746_, "sculk_vein", SculkVeinBlock.CODEC);
        Registry.register(p_310746_, "seagrass", SeagrassBlock.CODEC);
        Registry.register(p_310746_, "sea_pickle", SeaPickleBlock.CODEC);
        Registry.register(p_310746_, "shulker_box", ShulkerBoxBlock.CODEC);
        Registry.register(p_310746_, "skull", SkullBlock.CODEC);
        Registry.register(p_310746_, "slab", SlabBlock.CODEC);
        Registry.register(p_310746_, "slime", SlimeBlock.CODEC);
        Registry.register(p_310746_, "small_dripleaf", SmallDripleafBlock.CODEC);
        Registry.register(p_310746_, "smithing_table", SmithingTableBlock.CODEC);
        Registry.register(p_310746_, "smoker", SmokerBlock.CODEC);
        Registry.register(p_310746_, "sniffer_egg", SnifferEggBlock.CODEC);
        Registry.register(p_310746_, "snow_layer", SnowLayerBlock.CODEC);
        Registry.register(p_310746_, "snowy_dirt", SnowyDirtBlock.CODEC);
        Registry.register(p_310746_, "soul_fire", SoulFireBlock.CODEC);
        Registry.register(p_310746_, "soul_sand", SoulSandBlock.CODEC);
        Registry.register(p_310746_, "spawner", SpawnerBlock.CODEC);
        Registry.register(p_310746_, "sponge", SpongeBlock.CODEC);
        Registry.register(p_310746_, "spore_blossom", SporeBlossomBlock.CODEC);
        Registry.register(p_310746_, "stained_glass_pane", StainedGlassPaneBlock.CODEC);
        Registry.register(p_310746_, "stained_glass", StainedGlassBlock.CODEC);
        Registry.register(p_310746_, "stair", StairBlock.CODEC);
        Registry.register(p_310746_, "standing_sign", StandingSignBlock.CODEC);
        Registry.register(p_310746_, "stem", StemBlock.CODEC);
        Registry.register(p_310746_, "stonecutter", StonecutterBlock.CODEC);
        Registry.register(p_310746_, "structure", StructureBlock.CODEC);
        Registry.register(p_310746_, "structure_void", StructureVoidBlock.CODEC);
        Registry.register(p_310746_, "sugar_cane", SugarCaneBlock.CODEC);
        Registry.register(p_310746_, "sweet_berry_bush", SweetBerryBushBlock.CODEC);
        Registry.register(p_310746_, "tall_flower", TallFlowerBlock.CODEC);
        Registry.register(p_310746_, "tall_grass", TallGrassBlock.CODEC);
        Registry.register(p_310746_, "tall_seagrass", TallSeagrassBlock.CODEC);
        Registry.register(p_310746_, "target", TargetBlock.CODEC);
        Registry.register(p_310746_, "tinted_glass", TintedGlassBlock.CODEC);
        Registry.register(p_310746_, "tnt", TntBlock.CODEC);
        Registry.register(p_310746_, "torchflower_crop", TorchflowerCropBlock.CODEC);
        Registry.register(p_310746_, "torch", TorchBlock.CODEC);
        Registry.register(p_310746_, "transparent", TransparentBlock.CODEC);
        Registry.register(p_310746_, "trapdoor", TrapDoorBlock.CODEC);
        Registry.register(p_310746_, "trapped_chest", TrappedChestBlock.CODEC);
        Registry.register(p_310746_, "trial_spawner", TrialSpawnerBlock.CODEC);
        Registry.register(p_310746_, "trip_wire_hook", TripWireHookBlock.CODEC);
        Registry.register(p_310746_, "tripwire", TripWireBlock.CODEC);
        Registry.register(p_310746_, "turtle_egg", TurtleEggBlock.CODEC);
        Registry.register(p_310746_, "twisting_vines_plant", TwistingVinesPlantBlock.CODEC);
        Registry.register(p_310746_, "twisting_vines", TwistingVinesBlock.CODEC);
        Registry.register(p_310746_, "vault", VaultBlock.CODEC);
        Registry.register(p_310746_, "vine", VineBlock.CODEC);
        Registry.register(p_310746_, "wall_banner", WallBannerBlock.CODEC);
        Registry.register(p_310746_, "wall_hanging_sign", WallHangingSignBlock.CODEC);
        Registry.register(p_310746_, "wall_sign", WallSignBlock.CODEC);
        Registry.register(p_310746_, "wall_skull", WallSkullBlock.CODEC);
        Registry.register(p_310746_, "wall_torch", WallTorchBlock.CODEC);
        Registry.register(p_310746_, "wall", WallBlock.CODEC);
        Registry.register(p_310746_, "waterlily", WaterlilyBlock.CODEC);
        Registry.register(p_310746_, "waterlogged_transparent", WaterloggedTransparentBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_bulb", WeatheringCopperBulbBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_door", WeatheringCopperDoorBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_full", WeatheringCopperFullBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_grate", WeatheringCopperGrateBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_slab", WeatheringCopperSlabBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_stair", WeatheringCopperStairBlock.CODEC);
        Registry.register(p_310746_, "weathering_copper_trap_door", WeatheringCopperTrapDoorBlock.CODEC);
        Registry.register(p_310746_, "web", WebBlock.CODEC);
        Registry.register(p_310746_, "weeping_vines_plant", WeepingVinesPlantBlock.CODEC);
        Registry.register(p_310746_, "weeping_vines", WeepingVinesBlock.CODEC);
        Registry.register(p_310746_, "weighted_pressure_plate", WeightedPressurePlateBlock.CODEC);
        Registry.register(p_310746_, "wet_sponge", WetSpongeBlock.CODEC);
        Registry.register(p_310746_, "wither_rose", WitherRoseBlock.CODEC);
        Registry.register(p_310746_, "wither_skull", WitherSkullBlock.CODEC);
        Registry.register(p_310746_, "wither_wall_skull", WitherWallSkullBlock.CODEC);
        return Registry.register(p_310746_, "wool_carpet", WoolCarpetBlock.CODEC);
    }
}