package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.IoSupplier;

public interface PackResources extends AutoCloseable, net.minecraftforge.common.extensions.IForgePackResources {
    String METADATA_EXTENSION = ".mcmeta";
    String PACK_META = "pack.mcmeta";

    @Nullable
    IoSupplier<InputStream> getRootResource(String... pElements);

    @Nullable
    IoSupplier<InputStream> getResource(PackType pPackType, ResourceLocation pLocation);

    void listResources(PackType pPackType, String pNamespace, String pPath, PackResources.ResourceOutput pResourceOutput);

    Set<String> getNamespaces(PackType pType);

    @Nullable
    <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) throws IOException;

    PackLocationInfo m_318586_();

    default String packId() {
        return this.m_318586_().f_316372_();
    }

    default Optional<KnownPack> m_323505_() {
        return this.m_318586_().f_314017_();
    }

    @Override
    void close();

    @FunctionalInterface
    public interface ResourceOutput extends BiConsumer<ResourceLocation, IoSupplier<InputStream>> {
    }
}