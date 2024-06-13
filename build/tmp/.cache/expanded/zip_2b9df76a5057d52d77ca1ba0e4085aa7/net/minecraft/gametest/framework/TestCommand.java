package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class TestCommand {
    public static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
    public static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_CLEAR_RADIUS = 200;
    private static final int MAX_CLEAR_RADIUS = 1024;
    private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final int DEFAULT_X_SIZE = 5;
    private static final int DEFAULT_Y_SIZE = 5;
    private static final int DEFAULT_Z_SIZE = 5;
    private static final String f_316045_ = "Structure block entity could not be found";
    private static final TestFinder.Builder<TestCommand.Runner> f_316814_ = new TestFinder.Builder<>(TestCommand.Runner::new);

    private static ArgumentBuilder<CommandSourceStack, ?> m_319788_(
        ArgumentBuilder<CommandSourceStack, ?> p_331571_,
        Function<CommandContext<CommandSourceStack>, TestCommand.Runner> p_335923_,
        Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> p_333739_
    ) {
        return p_331571_.executes(p_325991_ -> p_335923_.apply(p_325991_).m_322798_())
            .then(
                Commands.argument("numberOfTimes", IntegerArgumentType.integer(0))
                    .executes(
                        p_325975_ -> p_335923_.apply(p_325975_).m_324651_(new RetryOptions(IntegerArgumentType.getInteger(p_325975_, "numberOfTimes"), false))
                    )
                    .then(
                        p_333739_.apply(
                            Commands.argument("untilFailed", BoolArgumentType.bool())
                                .executes(
                                    p_325980_ -> p_335923_.apply(p_325980_)
                                            .m_324651_(
                                                new RetryOptions(
                                                    IntegerArgumentType.getInteger(p_325980_, "numberOfTimes"),
                                                    BoolArgumentType.getBool(p_325980_, "untilFailed")
                                                )
                                            )
                                )
                        )
                    )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> m_324887_(
        ArgumentBuilder<CommandSourceStack, ?> p_335642_, Function<CommandContext<CommandSourceStack>, TestCommand.Runner> p_330546_
    ) {
        return m_319788_(p_335642_, p_330546_, p_325997_ -> p_325997_);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> m_322812_(
        ArgumentBuilder<CommandSourceStack, ?> p_328748_, Function<CommandContext<CommandSourceStack>, TestCommand.Runner> p_328595_
    ) {
        return m_319788_(
            p_328748_,
            p_328595_,
            p_325993_ -> p_325993_.then(
                    Commands.argument("rotationSteps", IntegerArgumentType.integer())
                        .executes(
                            p_326001_ -> p_328595_.apply(p_326001_)
                                    .m_324097_(
                                        new RetryOptions(
                                            IntegerArgumentType.getInteger(p_326001_, "numberOfTimes"), BoolArgumentType.getBool(p_326001_, "untilFailed")
                                        ),
                                        IntegerArgumentType.getInteger(p_326001_, "rotationSteps")
                                    )
                        )
                        .then(
                            Commands.argument("testsPerRow", IntegerArgumentType.integer())
                                .executes(
                                    p_325977_ -> p_328595_.apply(p_325977_)
                                            .m_322161_(
                                                new RetryOptions(
                                                    IntegerArgumentType.getInteger(p_325977_, "numberOfTimes"),
                                                    BoolArgumentType.getBool(p_325977_, "untilFailed")
                                                ),
                                                IntegerArgumentType.getInteger(p_325977_, "rotationSteps"),
                                                IntegerArgumentType.getInteger(p_325977_, "testsPerRow")
                                            )
                                )
                        )
                )
        );
    }

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        ArgumentBuilder<CommandSourceStack, ?> argumentbuilder = m_322812_(
            Commands.argument("onlyRequiredTests", BoolArgumentType.bool()),
            p_326015_ -> f_316814_.m_321527_(p_326015_, BoolArgumentType.getBool(p_326015_, "onlyRequiredTests"))
        );
        ArgumentBuilder<CommandSourceStack, ?> argumentbuilder1 = m_322812_(
            Commands.argument("testClassName", TestClassNameArgument.testClassName()),
            p_325999_ -> f_316814_.m_319649_(p_325999_, TestClassNameArgument.getTestClassName(p_325999_, "testClassName"))
        );
        pDispatcher.register(
            Commands.literal("test")
                .then(
                    Commands.literal("run")
                        .then(
                            m_322812_(Commands.argument("testName", TestFunctionArgument.testFunctionArgument()), p_325988_ -> f_316814_.m_318690_(p_325988_, "testName"))
                        )
                )
                .then(
                    Commands.literal("runmultiple")
                        .then(
                            Commands.argument("testName", TestFunctionArgument.testFunctionArgument())
                                .executes(p_325973_ -> f_316814_.m_318690_(p_325973_, "testName").m_322798_())
                                .then(
                                    Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(
                                            p_325995_ -> f_316814_.m_319044_(IntegerArgumentType.getInteger(p_325995_, "amount"))
                                                    .m_318690_(p_325995_, "testName")
                                                    .m_322798_()
                                        )
                                )
                        )
                )
                .then(m_322812_(Commands.literal("runall").then(argumentbuilder1), f_316814_::m_320211_))
                .then(m_324887_(Commands.literal("runthese"), f_316814_::m_321386_))
                .then(m_324887_(Commands.literal("runclosest"), f_316814_::m_321677_))
                .then(m_324887_(Commands.literal("runthat"), f_316814_::m_322936_))
                .then(m_322812_(Commands.literal("runfailed").then(argumentbuilder), f_316814_::m_319961_))
                .then(
                    Commands.literal("locate")
                        .then(
                            Commands.argument("testName", TestFunctionArgument.testFunctionArgument())
                                .executes(
                                    p_325985_ -> f_316814_.m_320452_(
                                                p_325985_, "minecraft:" + TestFunctionArgument.getTestFunction(p_325985_, "testName").structureName()
                                            )
                                            .m_324809_()
                                )
                        )
                )
                .then(Commands.literal("resetclosest").executes(p_325984_ -> f_316814_.m_321677_(p_325984_).m_324404_()))
                .then(Commands.literal("resetthese").executes(p_325994_ -> f_316814_.m_321386_(p_325994_).m_324404_()))
                .then(Commands.literal("resetthat").executes(p_325983_ -> f_316814_.m_322936_(p_325983_).m_324404_()))
                .then(
                    Commands.literal("export")
                        .then(
                            Commands.argument("testName", StringArgumentType.word())
                                .executes(p_325998_ -> exportTestStructure(p_325998_.getSource(), "minecraft:" + StringArgumentType.getString(p_325998_, "testName")))
                        )
                )
                .then(Commands.literal("exportclosest").executes(p_326009_ -> f_316814_.m_321677_(p_326009_).m_318776_()))
                .then(Commands.literal("exportthese").executes(p_326010_ -> f_316814_.m_321386_(p_326010_).m_318776_()))
                .then(Commands.literal("exportthat").executes(p_326011_ -> f_316814_.m_322936_(p_326011_).m_318776_()))
                .then(Commands.literal("clearthat").executes(p_325987_ -> f_316814_.m_322936_(p_325987_).m_321437_()))
                .then(Commands.literal("clearthese").executes(p_325978_ -> f_316814_.m_321386_(p_325978_).m_321437_()))
                .then(
                    Commands.literal("clearall")
                        .executes(p_325986_ -> f_316814_.m_320082_(p_325986_, 200).m_321437_())
                        .then(
                            Commands.argument("radius", IntegerArgumentType.integer())
                                .executes(
                                    p_325996_ -> f_316814_.m_320082_(p_325996_, Mth.clamp(IntegerArgumentType.getInteger(p_325996_, "radius"), 0, 1024))
                                            .m_321437_()
                                )
                        )
                )
                .then(
                    Commands.literal("import")
                        .then(
                            Commands.argument("testName", StringArgumentType.word())
                                .executes(p_128025_ -> importTestStructure(p_128025_.getSource(), StringArgumentType.getString(p_128025_, "testName")))
                        )
                )
                .then(Commands.literal("stop").executes(p_326006_ -> m_320848_()))
                .then(
                    Commands.literal("pos")
                        .executes(p_128023_ -> showPos(p_128023_.getSource(), "pos"))
                        .then(
                            Commands.argument("var", StringArgumentType.word())
                                .executes(p_128021_ -> showPos(p_128021_.getSource(), StringArgumentType.getString(p_128021_, "var")))
                        )
                )
                .then(
                    Commands.literal("create")
                        .then(
                            Commands.argument("testName", StringArgumentType.word())
                                .suggests(TestFunctionArgument::m_324783_)
                                .executes(p_128019_ -> createNewStructure(p_128019_.getSource(), StringArgumentType.getString(p_128019_, "testName"), 5, 5, 5))
                                .then(
                                    Commands.argument("width", IntegerArgumentType.integer())
                                        .executes(
                                            p_128014_ -> createNewStructure(
                                                    p_128014_.getSource(),
                                                    StringArgumentType.getString(p_128014_, "testName"),
                                                    IntegerArgumentType.getInteger(p_128014_, "width"),
                                                    IntegerArgumentType.getInteger(p_128014_, "width"),
                                                    IntegerArgumentType.getInteger(p_128014_, "width")
                                                )
                                        )
                                        .then(
                                            Commands.argument("height", IntegerArgumentType.integer())
                                                .then(
                                                    Commands.argument("depth", IntegerArgumentType.integer())
                                                        .executes(
                                                            p_128007_ -> createNewStructure(
                                                                    p_128007_.getSource(),
                                                                    StringArgumentType.getString(p_128007_, "testName"),
                                                                    IntegerArgumentType.getInteger(p_128007_, "width"),
                                                                    IntegerArgumentType.getInteger(p_128007_, "height"),
                                                                    IntegerArgumentType.getInteger(p_128007_, "depth")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int m_324923_(GameTestInfo p_331593_) {
        p_331593_.getLevel().getEntities(null, p_331593_.getStructureBounds()).stream().forEach(p_325989_ -> p_325989_.remove(Entity.RemovalReason.DISCARDED));
        p_331593_.getStructureBlockEntity().m_305508_(p_331593_.getLevel());
        StructureUtils.m_319541_(p_331593_.getStructureBounds(), p_331593_.getLevel());
        say(p_331593_.getLevel(), "Reset succeded for: " + p_331593_.getTestName(), ChatFormatting.GREEN);
        return 1;
    }

    static Stream<GameTestInfo> m_322751_(CommandSourceStack p_329247_, RetryOptions p_336246_, StructureBlockPosFinder p_334897_) {
        return p_334897_.m_319645_().map(p_326014_ -> m_324715_(p_326014_, p_329247_.getLevel(), p_336246_)).flatMap(Optional::stream);
    }

    static Stream<GameTestInfo> m_325000_(CommandSourceStack p_330917_, RetryOptions p_332428_, TestFunctionFinder p_328880_, int p_327985_) {
        return p_328880_.m_318842_()
            .filter(p_326008_ -> m_306765_(p_330917_.getLevel(), p_326008_.structureName()))
            .map(p_326005_ -> new GameTestInfo(p_326005_, StructureUtils.getRotationForRotationSteps(p_327985_), p_330917_.getLevel(), p_332428_));
    }

    private static Optional<GameTestInfo> m_324715_(BlockPos p_332856_, ServerLevel p_328153_, RetryOptions p_330368_) {
        StructureBlockEntity structureblockentity = (StructureBlockEntity)p_328153_.getBlockEntity(p_332856_);
        if (structureblockentity == null) {
            say(p_328153_, "Structure block entity could not be found", ChatFormatting.RED);
            return Optional.empty();
        } else {
            String s = structureblockentity.getMetaData();
            Optional<TestFunction> optional = GameTestRegistry.findTestFunction(s);
            if (optional.isEmpty()) {
                say(p_328153_, "Test function for test " + s + " could not be found", ChatFormatting.RED);
                return Optional.empty();
            } else {
                TestFunction testfunction = optional.get();
                GameTestInfo gametestinfo = new GameTestInfo(testfunction, structureblockentity.getRotation(), p_328153_, p_330368_);
                gametestinfo.setStructureBlockPos(p_332856_);
                return !m_306765_(p_328153_, gametestinfo.getStructureName()) ? Optional.empty() : Optional.of(gametestinfo);
            }
        }
    }

    private static int createNewStructure(CommandSourceStack pSource, String pStructureName, int pX, int pY, int pZ) {
        if (pX <= 48 && pY <= 48 && pZ <= 48) {
            ServerLevel serverlevel = pSource.getLevel();
            BlockPos blockpos = m_307920_(pSource).below();
            StructureUtils.createNewEmptyStructureBlock(pStructureName.toLowerCase(), blockpos, new Vec3i(pX, pY, pZ), Rotation.NONE, serverlevel);
            BlockPos blockpos1 = blockpos.above();
            BlockPos blockpos2 = blockpos1.offset(pX - 1, 0, pZ - 1);
            BlockPos.betweenClosedStream(blockpos1, blockpos2).forEach(p_325982_ -> serverlevel.setBlockAndUpdate(p_325982_, Blocks.BEDROCK.defaultBlockState()));
            StructureUtils.addCommandBlockAndButtonToStartTest(blockpos, new BlockPos(1, 0, -1), Rotation.NONE, serverlevel);
            return 0;
        } else {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
    }

    private static int showPos(CommandSourceStack pSource, String pVariableName) throws CommandSyntaxException {
        BlockHitResult blockhitresult = (BlockHitResult)pSource.getPlayerOrException().pick(10.0, 1.0F, false);
        BlockPos blockpos = blockhitresult.getBlockPos();
        ServerLevel serverlevel = pSource.getLevel();
        Optional<BlockPos> optional = StructureUtils.findStructureBlockContainingPos(blockpos, 15, serverlevel);
        if (optional.isEmpty()) {
            optional = StructureUtils.findStructureBlockContainingPos(blockpos, 200, serverlevel);
        }

        if (optional.isEmpty()) {
            pSource.sendFailure(Component.literal("Can't find a structure block that contains the targeted pos " + blockpos));
            return 0;
        } else {
            StructureBlockEntity structureblockentity = (StructureBlockEntity)serverlevel.getBlockEntity(optional.get());
            if (structureblockentity == null) {
                say(serverlevel, "Structure block entity could not be found", ChatFormatting.RED);
                return 0;
            } else {
                BlockPos blockpos1 = blockpos.subtract(optional.get());
                String s = blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ();
                String s1 = structureblockentity.getMetaData();
                Component component = Component.literal(s)
                    .setStyle(
                        Style.EMPTY
                            .withBold(true)
                            .withColor(ChatFormatting.GREEN)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + pVariableName + " = new BlockPos(" + s + ");"))
                    );
                pSource.sendSuccess(() -> Component.literal("Position relative to " + s1 + ": ").append(component), false);
                DebugPackets.sendGameTestAddMarker(serverlevel, new BlockPos(blockpos), s, -2147418368, 10000);
                return 1;
            }
        }
    }

    static int m_320848_() {
        GameTestTicker.SINGLETON.clear();
        return 1;
    }

    static int m_324481_(CommandSourceStack p_333535_, ServerLevel p_333108_, GameTestRunner p_333430_) {
        p_333430_.m_324189_(new TestCommand.TestBatchSummaryDisplayer(p_333535_));
        MultipleTestTracker multipletesttracker = new MultipleTestTracker(p_333430_.m_320202_());
        multipletesttracker.addListener(new TestCommand.TestSummaryDisplayer(p_333108_, multipletesttracker));
        multipletesttracker.addFailureListener(p_127992_ -> GameTestRegistry.rememberFailedTest(p_127992_.getTestFunction()));
        p_333430_.m_323089_();
        return 1;
    }

    static int m_306022_(CommandSourceStack p_309467_, StructureBlockEntity p_310131_) {
        String s = p_310131_.getStructureName();
        if (!p_310131_.saveStructure(true)) {
            say(p_309467_, "Failed to save structure " + s);
        }

        return exportTestStructure(p_309467_, s);
    }

    private static int exportTestStructure(CommandSourceStack pSource, String pStructurePath) {
        Path path = Paths.get(StructureUtils.testStructuresDir);
        ResourceLocation resourcelocation = new ResourceLocation(pStructurePath);
        Path path1 = pSource.getLevel().getStructureManager().getPathToGeneratedStructure(resourcelocation, ".nbt");
        Path path2 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, path1, resourcelocation.getPath(), path);
        if (path2 == null) {
            say(pSource, "Failed to export " + path1);
            return 1;
        } else {
            try {
                FileUtil.createDirectoriesSafe(path2.getParent());
            } catch (IOException ioexception) {
                say(pSource, "Could not create folder " + path2.getParent());
                LOGGER.error("Could not create export folder", (Throwable)ioexception);
                return 1;
            }

            say(pSource, "Exported " + pStructurePath + " to " + path2.toAbsolutePath());
            return 0;
        }
    }

    private static boolean m_306765_(ServerLevel p_310841_, String p_330426_) {
        if (p_310841_.getStructureManager().get(new ResourceLocation(p_330426_)).isEmpty()) {
            say(p_310841_, "Test structure " + p_330426_ + " could not be found", ChatFormatting.RED);
            return false;
        } else {
            return true;
        }
    }

    static BlockPos m_307920_(CommandSourceStack p_313084_) {
        BlockPos blockpos = BlockPos.containing(p_313084_.getPosition());
        int i = p_313084_.getLevel().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockpos).getY();
        return new BlockPos(blockpos.getX(), i + 1, blockpos.getZ() + 3);
    }

    static void say(CommandSourceStack pSource, String pMessage) {
        pSource.sendSuccess(() -> Component.literal(pMessage), false);
    }

    private static int importTestStructure(CommandSourceStack pSource, String pStructurePath) {
        Path path = Paths.get(StructureUtils.testStructuresDir, pStructurePath + ".snbt");
        ResourceLocation resourcelocation = new ResourceLocation("minecraft", pStructurePath);
        Path path1 = pSource.getLevel().getStructureManager().getPathToGeneratedStructure(resourcelocation, ".nbt");

        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);
            String s = IOUtils.toString(bufferedreader);
            Files.createDirectories(path1.getParent());

            try (OutputStream outputstream = Files.newOutputStream(path1)) {
                NbtIo.writeCompressed(NbtUtils.snbtToStructure(s), outputstream);
            }

            pSource.getLevel().getStructureManager().remove(resourcelocation);
            say(pSource, "Imported to " + path1.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException ioexception) {
            LOGGER.error("Failed to load structure {}", pStructurePath, ioexception);
            return 1;
        }
    }

    static void say(ServerLevel pServerLevel, String pMessage, ChatFormatting pFormatting) {
        pServerLevel.getPlayers(p_127945_ -> true).forEach(p_308546_ -> p_308546_.sendSystemMessage(Component.literal(pMessage).withStyle(pFormatting)));
    }

    public static class Runner {
        private final TestFinder<TestCommand.Runner> f_314747_;

        public Runner(TestFinder<TestCommand.Runner> p_330629_) {
            this.f_314747_ = p_330629_;
        }

        public int m_324404_() {
            TestCommand.m_320848_();
            return TestCommand.m_322751_(this.f_314747_.m_324045_(), RetryOptions.m_321305_(), this.f_314747_).map(TestCommand::m_324923_).toList().isEmpty()
                ? 0
                : 1;
        }

        private <T> void m_321203_(Stream<T> p_331509_, ToIntFunction<T> p_328365_, Runnable p_334945_, Consumer<Integer> p_335243_) {
            int i = p_331509_.mapToInt(p_328365_).sum();
            if (i == 0) {
                p_334945_.run();
            } else {
                p_335243_.accept(i);
            }
        }

        public int m_321437_() {
            TestCommand.m_320848_();
            CommandSourceStack commandsourcestack = this.f_314747_.m_324045_();
            ServerLevel serverlevel = commandsourcestack.getLevel();
            GameTestRunner.clearMarkers(serverlevel);
            this.m_321203_(
                this.f_314747_.m_319645_(),
                p_330831_ -> {
                    StructureBlockEntity structureblockentity = (StructureBlockEntity)serverlevel.getBlockEntity(p_330831_);
                    if (structureblockentity == null) {
                        return 0;
                    } else {
                        BoundingBox boundingbox = StructureUtils.getStructureBoundingBox(structureblockentity);
                        StructureUtils.clearSpaceForStructure(boundingbox, serverlevel);
                        return 1;
                    }
                },
                () -> TestCommand.say(serverlevel, "Could not find any structures to clear", ChatFormatting.RED),
                p_330244_ -> TestCommand.say(commandsourcestack, "Cleared " + p_330244_ + " structures")
            );
            return 1;
        }

        public int m_318776_() {
            MutableBoolean mutableboolean = new MutableBoolean(true);
            CommandSourceStack commandsourcestack = this.f_314747_.m_324045_();
            ServerLevel serverlevel = commandsourcestack.getLevel();
            this.m_321203_(
                this.f_314747_.m_319645_(),
                p_331429_ -> {
                    StructureBlockEntity structureblockentity = (StructureBlockEntity)serverlevel.getBlockEntity(p_331429_);
                    if (structureblockentity == null) {
                        TestCommand.say(serverlevel, "Structure block entity could not be found", ChatFormatting.RED);
                        mutableboolean.setFalse();
                        return 0;
                    } else {
                        if (TestCommand.m_306022_(commandsourcestack, structureblockentity) != 0) {
                            mutableboolean.setFalse();
                        }

                        return 1;
                    }
                },
                () -> TestCommand.say(serverlevel, "Could not find any structures to export", ChatFormatting.RED),
                p_333553_ -> TestCommand.say(commandsourcestack, "Exported " + p_333553_ + " structures")
            );
            return mutableboolean.getValue() ? 0 : 1;
        }

        public int m_322161_(RetryOptions p_334797_, int p_327669_, int p_333611_) {
            TestCommand.m_320848_();
            CommandSourceStack commandsourcestack = this.f_314747_.m_324045_();
            ServerLevel serverlevel = commandsourcestack.getLevel();
            BlockPos blockpos = TestCommand.m_307920_(commandsourcestack);
            Collection<GameTestInfo> collection = Stream.concat(
                    TestCommand.m_322751_(commandsourcestack, p_334797_, this.f_314747_),
                    TestCommand.m_325000_(commandsourcestack, p_334797_, this.f_314747_, p_327669_)
                )
                .toList();
            if (collection.isEmpty()) {
                TestCommand.say(commandsourcestack, "No tests found");
                return 0;
            } else {
                GameTestRunner.clearMarkers(serverlevel);
                GameTestRegistry.forgetFailedTests();
                TestCommand.say(commandsourcestack, "Running " + collection.size() + " tests...");
                GameTestRunner gametestrunner = GameTestRunner.Builder.m_319523_(collection, serverlevel)
                    .m_322147_(new StructureGridSpawner(blockpos, p_333611_))
                    .m_322128_();
                return TestCommand.m_324481_(commandsourcestack, serverlevel, gametestrunner);
            }
        }

        public int m_320717_(int p_333354_, int p_329165_) {
            return this.m_322161_(RetryOptions.m_321305_(), p_333354_, p_329165_);
        }

        public int m_324466_(int p_333969_) {
            return this.m_322161_(RetryOptions.m_321305_(), p_333969_, 8);
        }

        public int m_324097_(RetryOptions p_328161_, int p_330365_) {
            return this.m_322161_(p_328161_, p_330365_, 8);
        }

        public int m_324651_(RetryOptions p_329766_) {
            return this.m_322161_(p_329766_, 0, 8);
        }

        public int m_322798_() {
            return this.m_324651_(RetryOptions.m_321305_());
        }

        public int m_324809_() {
            TestCommand.say(this.f_314747_.m_324045_(), "Started locating test structures, this might take a while..");
            MutableInt mutableint = new MutableInt(0);
            BlockPos blockpos = BlockPos.containing(this.f_314747_.m_324045_().getPosition());
            this.f_314747_
                .m_319645_()
                .forEach(
                    p_327721_ -> {
                        StructureBlockEntity structureblockentity = (StructureBlockEntity)this.f_314747_.m_324045_().getLevel().getBlockEntity(p_327721_);
                        if (structureblockentity != null) {
                            Direction direction = structureblockentity.getRotation().rotate(Direction.NORTH);
                            BlockPos blockpos1 = structureblockentity.getBlockPos().relative(direction, 2);
                            int j = (int)direction.getOpposite().toYRot();
                            String s = String.format("/tp @s %d %d %d %d 0", blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), j);
                            int k = blockpos.getX() - p_327721_.getX();
                            int l = blockpos.getZ() - p_327721_.getZ();
                            int i1 = Mth.floor(Mth.sqrt((float)(k * k + l * l)));
                            Component component = ComponentUtils.wrapInSquareBrackets(
                                    Component.translatable("chat.coordinates", p_327721_.getX(), p_327721_.getY(), p_327721_.getZ())
                                )
                                .withStyle(
                                    p_332540_ -> p_332540_.withColor(ChatFormatting.GREEN)
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                                );
                            Component component1 = Component.literal("Found structure at: ").append(component).append(" (distance: " + i1 + ")");
                            this.f_314747_.m_324045_().sendSuccess(() -> component1, false);
                            mutableint.increment();
                        }
                    }
                );
            int i = mutableint.intValue();
            if (i == 0) {
                TestCommand.say(this.f_314747_.m_324045_().getLevel(), "No such test structure found", ChatFormatting.RED);
                return 0;
            } else {
                TestCommand.say(this.f_314747_.m_324045_().getLevel(), "Finished locating, found " + i + " structure(s)", ChatFormatting.GREEN);
                return 1;
            }
        }
    }

    static record TestBatchSummaryDisplayer(CommandSourceStack f_316456_) implements GameTestBatchListener {
        @Override
        public void m_318675_(GameTestBatch p_327831_) {
            TestCommand.say(this.f_316456_, "Starting batch: " + p_327831_.name());
        }

        @Override
        public void m_320803_(GameTestBatch p_335734_) {
        }
    }

    public static record TestSummaryDisplayer(ServerLevel level, MultipleTestTracker tracker) implements GameTestListener {
        @Override
        public void testStructureLoaded(GameTestInfo pTestInfo) {
        }

        @Override
        public void testPassed(GameTestInfo pTestInfo, GameTestRunner p_333026_) {
            m_323141_(this.level, this.tracker);
        }

        @Override
        public void testFailed(GameTestInfo pTestInfo, GameTestRunner p_333809_) {
            m_323141_(this.level, this.tracker);
        }

        @Override
        public void m_177684_(GameTestInfo p_328539_, GameTestInfo p_335500_, GameTestRunner p_328503_) {
            this.tracker.addTestToTrack(p_335500_);
        }

        private static void m_323141_(ServerLevel p_329959_, MultipleTestTracker p_331168_) {
            if (p_331168_.isDone()) {
                TestCommand.say(p_329959_, "GameTest done! " + p_331168_.getTotalCount() + " tests were run", ChatFormatting.WHITE);
                if (p_331168_.hasFailedRequired()) {
                    TestCommand.say(p_329959_, p_331168_.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
                } else {
                    TestCommand.say(p_329959_, "All required tests passed :)", ChatFormatting.GREEN);
                }

                if (p_331168_.hasFailedOptional()) {
                    TestCommand.say(p_329959_, p_331168_.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
                }
            }
        }
    }
}