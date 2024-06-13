package net.minecraft.server.packs.repository;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;

public class ServerPacksSource extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(
        Component.translatable("dataPack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA), Optional.empty()
    );
    private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(
        PackMetadataSection.TYPE, VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, FEATURE_FLAGS_METADATA_SECTION
    );
    private static final PackLocationInfo f_316407_ = new PackLocationInfo(
        "vanilla", Component.translatable("dataPack.vanilla.name"), PackSource.BUILT_IN, Optional.of(f_315573_)
    );
    private static final PackSelectionConfig f_316252_ = new PackSelectionConfig(false, Pack.Position.BOTTOM, false);
    private static final PackSelectionConfig f_315861_ = new PackSelectionConfig(false, Pack.Position.TOP, false);
    private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "datapacks");

    public ServerPacksSource(DirectoryValidator pValidator) {
        super(PackType.SERVER_DATA, createVanillaPackSource(), PACKS_DIR, pValidator);
    }

    private static PackLocationInfo m_321450_(String p_330867_, Component p_330785_) {
        return new PackLocationInfo(p_330867_, p_330785_, PackSource.FEATURE, Optional.of(KnownPack.m_321609_(p_330867_)));
    }

    @VisibleForTesting
    public static VanillaPackResources createVanillaPackSource() {
        return new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft").applyDevelopmentConfig().pushJarResources().build(f_316407_);
    }

    @Override
    protected Component getPackTitle(String pId) {
        return Component.literal(pId);
    }

    @Nullable
    @Override
    protected Pack createVanillaPack(PackResources pResources) {
        return Pack.readMetaAndCreate(f_316407_, fixedResources(pResources), PackType.SERVER_DATA, f_316252_);
    }

    @Nullable
    @Override
    protected Pack createBuiltinPack(String pId, Pack.ResourcesSupplier pResources, Component pTitle) {
        return Pack.readMetaAndCreate(m_321450_(pId, pTitle), pResources, PackType.SERVER_DATA, f_315861_);
    }

    public static PackRepository createPackRepository(Path pFolder, DirectoryValidator pValidator) {
        var ret = new PackRepository(new ServerPacksSource(pValidator), new FolderRepositorySource(pFolder, PackType.SERVER_DATA, PackSource.WORLD, pValidator));
        net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.event.AddPackFindersEvent(PackType.SERVER_DATA, ret::addPackFinder));
        return ret;
    }

    public static PackRepository createVanillaTrustedRepository() {
        return new PackRepository(new ServerPacksSource(new DirectoryValidator(p_296600_ -> true)));
    }

    public static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess pLevel) {
        return createPackRepository(pLevel.getLevelPath(LevelResource.DATAPACK_DIR), pLevel.parent().getWorldDirValidator());
    }
}
