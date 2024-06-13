package net.minecraft.client.resources;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPackSource extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(
        Component.translatable("resourcePack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES), Optional.empty()
    );
    private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION);
    public static final String HIGH_CONTRAST_PACK = "high_contrast";
    private static final Map<String, Component> SPECIAL_PACK_NAMES = Map.of(
        "programmer_art", Component.translatable("resourcePack.programmer_art.name"), "high_contrast", Component.translatable("resourcePack.high_contrast.name")
    );
    private static final PackLocationInfo f_315949_ = new PackLocationInfo(
        "vanilla", Component.translatable("resourcePack.vanilla.name"), PackSource.BUILT_IN, Optional.of(f_315573_)
    );
    private static final PackSelectionConfig f_316211_ = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
    private static final PackSelectionConfig f_314704_ = new PackSelectionConfig(false, Pack.Position.TOP, false);
    private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "resourcepacks");
    @Nullable
    private final Path externalAssetDir;

    public ClientPackSource(Path p_249324_, DirectoryValidator p_299963_) {
        super(PackType.CLIENT_RESOURCES, createVanillaPackSource(p_249324_), PACKS_DIR, p_299963_);
        this.externalAssetDir = this.findExplodedAssetPacks(p_249324_);
    }

    private static PackLocationInfo m_322194_(String p_331520_, Component p_335955_) {
        return new PackLocationInfo(p_331520_, p_335955_, PackSource.BUILT_IN, Optional.of(KnownPack.m_321609_(p_331520_)));
    }

    @Nullable
    private Path findExplodedAssetPacks(Path pAssetIndex) {
        if (SharedConstants.IS_RUNNING_IN_IDE && pAssetIndex.getFileSystem() == FileSystems.getDefault()) {
            Path path = pAssetIndex.getParent().resolve("resourcepacks");
            if (Files.isDirectory(path)) {
                return path;
            }
        }

        return null;
    }

    public static VanillaPackResources createVanillaPackSource(Path pAssetIndex) {
        VanillaPackResourcesBuilder vanillapackresourcesbuilder = new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft", "realms");
        return vanillapackresourcesbuilder.applyDevelopmentConfig().pushJarResources().pushAssetPath(PackType.CLIENT_RESOURCES, pAssetIndex).build(f_315949_);
    }

    @Override
    protected Component getPackTitle(String pId) {
        Component component = SPECIAL_PACK_NAMES.get(pId);
        return (Component)(component != null ? component : Component.literal(pId));
    }

    @Nullable
    @Override
    protected Pack createVanillaPack(PackResources pResources) {
        return Pack.readMetaAndCreate(f_315949_, fixedResources(pResources), PackType.CLIENT_RESOURCES, f_316211_);
    }

    @Nullable
    @Override
    protected Pack createBuiltinPack(String pId, Pack.ResourcesSupplier pResources, Component pTitle) {
        return Pack.readMetaAndCreate(m_322194_(pId, pTitle), pResources, PackType.CLIENT_RESOURCES, f_314704_);
    }

    @Override
    protected void populatePackList(BiConsumer<String, Function<String, Pack>> pPopulator) {
        super.populatePackList(pPopulator);
        if (this.externalAssetDir != null) {
            this.discoverPacksInPath(this.externalAssetDir, pPopulator);
        }
    }
}