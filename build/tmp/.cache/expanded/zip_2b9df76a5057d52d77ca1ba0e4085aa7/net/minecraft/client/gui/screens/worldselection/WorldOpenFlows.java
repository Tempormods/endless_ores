package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.RecoverWorldDataScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.util.MemoryReserve;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldOpenFlows {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final UUID f_303497_ = UUID.fromString("640a6a92-b6cb-48a0-b391-831586500359");
    private final Minecraft minecraft;
    private final LevelStorageSource levelSource;

    public WorldOpenFlows(Minecraft pMinecraft, LevelStorageSource pLevelSource) {
        this.minecraft = pMinecraft;
        this.levelSource = pLevelSource;
    }

    public void createFreshLevel(
        String pLevelName, LevelSettings pLevelSettings, WorldOptions pWorldOptions, Function<RegistryAccess, WorldDimensions> pDimensionsGetter, Screen p_310233_
    ) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.createWorldAccess(pLevelName);
        if (levelstoragesource$levelstorageaccess != null) {
            PackRepository packrepository = ServerPacksSource.createPackRepository(levelstoragesource$levelstorageaccess);
            WorldDataConfiguration worlddataconfiguration = pLevelSettings.getDataConfiguration();

            try {
                WorldLoader.PackConfig worldloader$packconfig = new WorldLoader.PackConfig(packrepository, worlddataconfiguration, false, false);
                WorldStem worldstem = this.loadWorldDataBlocking(
                    worldloader$packconfig,
                    p_258145_ -> {
                        WorldDimensions.Complete worlddimensions$complete = pDimensionsGetter.apply(p_258145_.datapackWorldgen())
                            .bake(p_258145_.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM));
                        return new WorldLoader.DataLoadOutput<>(
                            new PrimaryLevelData(pLevelSettings, pWorldOptions, worlddimensions$complete.specialWorldProperty(), worlddimensions$complete.lifecycle()),
                            worlddimensions$complete.dimensionsRegistryAccess()
                        );
                    },
                    WorldStem::new
                );
                this.minecraft.doWorldLoad(levelstoragesource$levelstorageaccess, packrepository, worldstem, true);
            } catch (Exception exception) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)exception);
                levelstoragesource$levelstorageaccess.m_306156_();
                this.minecraft.setScreen(p_310233_);
            }
        }
    }

    @Nullable
    private LevelStorageSource.LevelStorageAccess createWorldAccess(String pLevelName) {
        try {
            return this.levelSource.validateAndCreateAccess(pLevelName);
        } catch (IOException ioexception) {
            LOGGER.warn("Failed to read level {} data", pLevelName, ioexception);
            SystemToast.onWorldAccessFailure(this.minecraft, pLevelName);
            this.minecraft.setScreen(null);
            return null;
        } catch (ContentValidationException contentvalidationexception) {
            LOGGER.warn("{}", contentvalidationexception.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(null)));
            return null;
        }
    }

    public void createLevelFromExistingSettings(
        LevelStorageSource.LevelStorageAccess pLevelStorage,
        ReloadableServerResources pResources,
        LayeredRegistryAccess<RegistryLayer> pRegistries,
        WorldData pWorldData
    ) {
        PackRepository packrepository = ServerPacksSource.createPackRepository(pLevelStorage);
        CloseableResourceManager closeableresourcemanager = new WorldLoader.PackConfig(packrepository, pWorldData.getDataConfiguration(), false, false)
            .createResourceManager()
            .getSecond();
        this.minecraft.doWorldLoad(pLevelStorage, packrepository, new WorldStem(closeableresourcemanager, pResources, pRegistries, pWorldData), true);
    }

    public WorldStem loadWorldStem(Dynamic<?> p_312184_, boolean pSafeMode, PackRepository pPackRepository) throws Exception {
        WorldLoader.PackConfig worldloader$packconfig = LevelStorageSource.m_305246_(p_312184_, pPackRepository, pSafeMode);
        return this.loadWorldDataBlocking(worldloader$packconfig, p_308270_ -> {
            Registry<LevelStem> registry = p_308270_.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
            LevelDataAndDimensions leveldataanddimensions = LevelStorageSource.m_306102_(p_312184_, p_308270_.dataConfiguration(), registry, p_308270_.datapackWorldgen());
            return new WorldLoader.DataLoadOutput<>(leveldataanddimensions.f_303671_(), leveldataanddimensions.f_303409_().dimensionsRegistryAccess());
        }, WorldStem::new);
    }

    public Pair<LevelSettings, WorldCreationContext> recreateWorldData(LevelStorageSource.LevelStorageAccess pLevelStorage) throws Exception {
        PackRepository packrepository = ServerPacksSource.createPackRepository(pLevelStorage);
        Dynamic<?> dynamic = pLevelStorage.m_307464_();
        WorldLoader.PackConfig worldloader$packconfig = LevelStorageSource.m_305246_(dynamic, packrepository, false);

        @OnlyIn(Dist.CLIENT)
        record Data(LevelSettings levelSettings, WorldOptions options, Registry<LevelStem> existingDimensions) {
        }

        return this.<Data, Pair<LevelSettings, WorldCreationContext>>loadWorldDataBlocking(
            worldloader$packconfig,
            p_308268_ -> {
                Registry<LevelStem> registry = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();
                LevelDataAndDimensions leveldataanddimensions = LevelStorageSource.m_306102_(dynamic, p_308268_.dataConfiguration(), registry, p_308268_.datapackWorldgen());
                return new WorldLoader.DataLoadOutput<>(
                    new Data(
                        leveldataanddimensions.f_303671_().getLevelSettings(),
                        leveldataanddimensions.f_303671_().worldGenOptions(),
                        leveldataanddimensions.f_303409_().dimensions()
                    ),
                    p_308268_.datapackDimensions()
                );
            },
            (p_247840_, p_247841_, p_247842_, p_247843_) -> {
                p_247840_.close();
                return Pair.of(
                    p_247843_.levelSettings,
                    new WorldCreationContext(
                        p_247843_.options, new WorldDimensions(p_247843_.existingDimensions), p_247842_, p_247841_, p_247843_.levelSettings.getDataConfiguration()
                    )
                );
            }
        );
    }

    private <D, R> R loadWorldDataBlocking(WorldLoader.PackConfig pPackConfig, WorldLoader.WorldDataSupplier<D> pWorldDataSupplier, WorldLoader.ResultFactory<D, R> pResultFactory) throws Exception {
        WorldLoader.InitConfig worldloader$initconfig = new WorldLoader.InitConfig(pPackConfig, Commands.CommandSelection.INTEGRATED, 2);
        CompletableFuture<R> completablefuture = WorldLoader.load(worldloader$initconfig, pWorldDataSupplier, pResultFactory, Util.backgroundExecutor(), this.minecraft);
        this.minecraft.managedBlock(completablefuture::isDone);
        return completablefuture.get();
    }

    private void askForBackup(LevelStorageSource.LevelStorageAccess p_312560_, boolean pCustomized, Runnable pLoadLevel, Runnable p_312163_) {
        Component component;
        Component component1;
        if (pCustomized) {
            component = Component.translatable("selectWorld.backupQuestion.customized");
            component1 = Component.translatable("selectWorld.backupWarning.customized");
        } else {
            component = Component.translatable("selectWorld.backupQuestion.experimental");
            component1 = Component.translatable("selectWorld.backupWarning.experimental");
        }

        this.minecraft.setScreen(new BackupConfirmScreen(p_312163_, (p_308273_, p_308274_) -> {
            if (p_308273_) {
                EditWorldScreen.makeBackupAndShowToast(p_312560_);
            }

            pLoadLevel.run();
        }, component, component1, false));
    }

    public static void confirmWorldCreation(Minecraft pMinecraft, CreateWorldScreen pScreen, Lifecycle pLifecycle, Runnable pLoadWorld, boolean pSkipWarnings) {
        BooleanConsumer booleanconsumer = p_233154_ -> {
            if (p_233154_) {
                pLoadWorld.run();
            } else {
                pMinecraft.setScreen(pScreen);
            }
        };
        if (pSkipWarnings || pLifecycle == Lifecycle.stable()) {
            pLoadWorld.run();
        } else if (pLifecycle == Lifecycle.experimental()) {
            pMinecraft.setScreen(
                new ConfirmScreen(
                    booleanconsumer,
                    Component.translatable("selectWorld.warning.experimental.title"),
                    Component.translatable("selectWorld.warning.experimental.question")
                )
            );
        } else {
            pMinecraft.setScreen(
                new ConfirmScreen(
                    booleanconsumer,
                    Component.translatable("selectWorld.warning.deprecated.title"),
                    Component.translatable("selectWorld.warning.deprecated.question")
                )
            );
        }
    }

    public void m_320872_(String p_332907_, Runnable p_332472_) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
        LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = this.createWorldAccess(p_332907_);
        if (levelstoragesource$levelstorageaccess != null) {
            this.m_320544_(levelstoragesource$levelstorageaccess, p_332472_);
        }
    }

    private void m_320544_(LevelStorageSource.LevelStorageAccess p_330142_, Runnable p_335478_) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));

        Dynamic<?> dynamic;
        LevelSummary levelsummary;
        try {
            dynamic = p_330142_.m_307464_();
            levelsummary = p_330142_.getSummary(dynamic);
        } catch (NbtException | ReportedNbtException | IOException ioexception) {
            this.minecraft.setScreen(new RecoverWorldDataScreen(this.minecraft, p_325454_ -> {
                if (p_325454_) {
                    this.m_320544_(p_330142_, p_335478_);
                } else {
                    p_330142_.m_306156_();
                    p_335478_.run();
                }
            }, p_330142_));
            return;
        } catch (OutOfMemoryError outofmemoryerror1) {
            MemoryReserve.release();
            System.gc();
            String s = "Ran out of memory trying to read level data of world folder \"" + p_330142_.getLevelId() + "\"";
            LOGGER.error(LogUtils.FATAL_MARKER, s);
            OutOfMemoryError outofmemoryerror = new OutOfMemoryError("Ran out of memory reading level data");
            outofmemoryerror.initCause(outofmemoryerror1);
            CrashReport crashreport = CrashReport.forThrowable(outofmemoryerror, s);
            CrashReportCategory crashreportcategory = crashreport.addCategory("World details");
            crashreportcategory.setDetail("World folder", p_330142_.getLevelId());
            throw new ReportedException(crashreport);
        }

        this.m_321507_(p_330142_, levelsummary, dynamic, p_335478_);
    }

    private void m_321507_(LevelStorageSource.LevelStorageAccess p_335405_, LevelSummary p_331961_, Dynamic<?> p_333467_, Runnable p_328023_) {
        if (!p_331961_.isCompatible()) {
            p_335405_.m_306156_();
            this.minecraft
                .setScreen(
                    new AlertScreen(
                        p_328023_,
                        Component.translatable("selectWorld.incompatible.title").m_306658_(-65536),
                        Component.translatable("selectWorld.incompatible.description", p_331961_.getWorldVersionName())
                    )
                );
        } else {
            LevelSummary.BackupStatus levelsummary$backupstatus = p_331961_.backupStatus();
            if (levelsummary$backupstatus.shouldBackup()) {
                String s = "selectWorld.backupQuestion." + levelsummary$backupstatus.getTranslationKey();
                String s1 = "selectWorld.backupWarning." + levelsummary$backupstatus.getTranslationKey();
                MutableComponent mutablecomponent = Component.translatable(s);
                if (levelsummary$backupstatus.isSevere()) {
                    mutablecomponent.m_306658_(-2142128);
                }

                Component component = Component.translatable(s1, p_331961_.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
                this.minecraft.setScreen(new BackupConfirmScreen(() -> {
                    p_335405_.m_306156_();
                    p_328023_.run();
                }, (p_325458_, p_325459_) -> {
                    if (p_325458_) {
                        EditWorldScreen.makeBackupAndShowToast(p_335405_);
                    }

                    this.m_320597_(p_335405_, p_333467_, false, p_328023_);
                }, mutablecomponent, component, false));
            } else {
                this.m_320597_(p_335405_, p_333467_, false, p_328023_);
            }
        }
    }

    private void m_320597_(LevelStorageSource.LevelStorageAccess p_333651_, Dynamic<?> p_332568_, boolean p_334192_, Runnable p_332843_) {
        this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.resource_load")));
        PackRepository packrepository = ServerPacksSource.createPackRepository(p_333651_);

        net.minecraftforge.common.ForgeHooks.readAdditionalLevelSaveData(p_333651_, p_333651_.m_306248_());

        WorldStem worldstem;
        try {
            worldstem = this.loadWorldStem(p_332568_, p_334192_, packrepository);

            for (LevelStem levelstem : worldstem.registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM)) {
                levelstem.generator().m_321960_();
            }
        } catch (Exception exception) {
            LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", (Throwable)exception);
            if (!p_334192_) {
                this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
                    p_333651_.m_306156_();
                    p_332843_.run();
                }, () -> this.m_320597_(p_333651_, p_332568_, true, p_332843_)));
            } else {
                p_333651_.m_306156_();
                this.minecraft
                    .setScreen(
                        new AlertScreen(
                            p_332843_,
                            Component.translatable("datapackFailure.safeMode.failed.title"),
                            Component.translatable("datapackFailure.safeMode.failed.description"),
                            CommonComponents.GUI_BACK,
                            true
                        )
                    );
            }

            return;
        }

        this.m_324520_(p_333651_, worldstem, packrepository, p_332843_);
    }

    private void m_324520_(LevelStorageSource.LevelStorageAccess p_329946_, WorldStem p_331923_, PackRepository p_329592_, Runnable p_331882_) {
        WorldData worlddata = p_331923_.worldData();
        boolean flag = worlddata.worldGenOptions().isOldCustomizedWorld();
        boolean flag1 = worlddata.worldGenSettingsLifecycle() != Lifecycle.stable();
        if (!flag && !flag1) {
            this.m_323928_(p_329946_, p_331923_, p_329592_, p_331882_);
        } else {
            this.askForBackup(p_329946_, flag, () -> this.m_323928_(p_329946_, p_331923_, p_329592_, p_331882_), () -> {
                p_331923_.close();
                p_329946_.m_306156_();
                p_331882_.run();
            });
        }
    }

    private void m_323928_(LevelStorageSource.LevelStorageAccess p_332203_, WorldStem p_333813_, PackRepository p_328830_, Runnable p_331357_) {
        DownloadedPackSource downloadedpacksource = this.minecraft.getDownloadedPackSource();
        this.m_306257_(downloadedpacksource, p_332203_).thenApply(p_233177_ -> true).exceptionallyComposeAsync(p_233183_ -> {
            LOGGER.warn("Failed to load pack: ", p_233183_);
            return this.promptBundledPackLoadFailure();
        }, this.minecraft).thenAcceptAsync(p_325451_ -> {
            if (p_325451_) {
                this.m_323759_(p_332203_, p_333813_, downloadedpacksource, p_328830_, p_331357_);
            } else {
                downloadedpacksource.m_304654_();
                p_333813_.close();
                p_332203_.m_306156_();
                p_331357_.run();
            }
        }, this.minecraft).exceptionally(p_233175_ -> {
            this.minecraft.delayCrash(CrashReport.forThrowable(p_233175_, "Load world"));
            return null;
        });
    }

    private void m_323759_(
        LevelStorageSource.LevelStorageAccess p_332115_, WorldStem p_329606_, DownloadedPackSource p_331698_, PackRepository p_334521_, Runnable p_330770_
    ) {
        if (p_332115_.m_323802_()) {
            this.minecraft
                .setScreen(
                    new ConfirmScreen(
                        p_325469_ -> {
                            if (p_325469_) {
                                this.m_324083_(p_332115_, p_329606_, p_334521_);
                            } else {
                                p_331698_.m_304654_();
                                p_329606_.close();
                                p_332115_.m_306156_();
                                p_330770_.run();
                            }
                        },
                        Component.translatable("selectWorld.warning.lowDiskSpace.title").withStyle(ChatFormatting.RED),
                        Component.translatable("selectWorld.warning.lowDiskSpace.description"),
                        CommonComponents.GUI_CONTINUE,
                        CommonComponents.GUI_BACK
                    )
                );
        } else {
            this.m_324083_(p_332115_, p_329606_, p_334521_);
        }
    }

    private void m_324083_(LevelStorageSource.LevelStorageAccess p_329495_, WorldStem p_329186_, PackRepository p_331916_) {
        this.minecraft.doWorldLoad(p_329495_, p_331916_, p_329186_, false);
    }

    private CompletableFuture<Void> m_306257_(DownloadedPackSource p_312230_, LevelStorageSource.LevelStorageAccess p_310544_) {
        Path path = p_310544_.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            p_312230_.m_306738_();
            CompletableFuture<Void> completablefuture = p_312230_.m_305490_(f_303497_);
            p_312230_.m_306353_(f_303497_, path);
            return completablefuture;
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
        CompletableFuture<Boolean> completablefuture = new CompletableFuture<>();
        this.minecraft
            .setScreen(
                new ConfirmScreen(
                    completablefuture::complete,
                    Component.translatable("multiplayer.texturePrompt.failure.line1"),
                    Component.translatable("multiplayer.texturePrompt.failure.line2"),
                    CommonComponents.GUI_PROCEED,
                    CommonComponents.GUI_CANCEL
                )
            );
        return completablefuture;
    }
}
