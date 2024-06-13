package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class Pack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackLocationInfo f_316989_;
    private final Pack.ResourcesSupplier resources;
    private final Pack.Metadata f_315327_;
    private final PackSelectionConfig f_314425_;
    private final boolean hidden; // Forge: Allow packs to be hidden from the UI entirely

    @Nullable
    public static Pack readMetaAndCreate(PackLocationInfo p_333251_, Pack.ResourcesSupplier pResources, PackType pPackType, PackSelectionConfig p_334202_) {
        int i = SharedConstants.getCurrentVersion().getPackVersion(pPackType);
        Pack.Metadata pack$metadata = m_324832_(p_333251_, pResources, i);
        return pack$metadata != null ? new Pack(p_333251_, pResources, pack$metadata, p_334202_) : null;
    }

    public Pack(PackLocationInfo p_330003_, Pack.ResourcesSupplier pResources, Pack.Metadata p_330761_, PackSelectionConfig p_334769_) {
        this.f_316989_ = p_330003_;
        this.resources = pResources;
        this.f_315327_ = p_330761_;
        this.f_314425_ = p_334769_;
        this.hidden = p_330761_.isHidden();
    }

    @Nullable
    public static Pack.Metadata m_324832_(PackLocationInfo p_330799_, Pack.ResourcesSupplier p_331172_, int p_333544_) {
        try {
            Pack.Metadata pack$metadata;
            try (PackResources packresources = p_331172_.openPrimary(p_330799_)) {
                PackMetadataSection packmetadatasection = packresources.getMetadataSection(PackMetadataSection.TYPE);
                if (packmetadatasection == null) {
                    LOGGER.warn("Missing metadata in pack {}", p_330799_.f_316372_());
                    return null;
                }

                FeatureFlagsMetadataSection featureflagsmetadatasection = packresources.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
                FeatureFlagSet featureflagset = featureflagsmetadatasection != null ? featureflagsmetadatasection.flags() : FeatureFlagSet.of();
                InclusiveRange<Integer> inclusiverange = getDeclaredPackVersions(p_330799_.f_316372_(), packmetadatasection);
                PackCompatibility packcompatibility = PackCompatibility.forVersion(inclusiverange, p_333544_);
                OverlayMetadataSection overlaymetadatasection = packresources.getMetadataSection(OverlayMetadataSection.TYPE);
                List<String> list = overlaymetadatasection != null ? overlaymetadatasection.overlaysForVersion(p_333544_) : List.of();
                pack$metadata = new Pack.Metadata(packmetadatasection.description(), packcompatibility, featureflagset, list, packresources.isHidden());
            }

            return pack$metadata;
        } catch (Exception exception) {
            LOGGER.warn("Failed to read pack {} metadata", p_330799_.f_316372_(), exception);
            return null;
        }
    }

    private static InclusiveRange<Integer> getDeclaredPackVersions(String pId, PackMetadataSection pMetadata) {
        int i = pMetadata.packFormat();
        if (pMetadata.supportedFormats().isEmpty()) {
            return new InclusiveRange<>(i);
        } else {
            InclusiveRange<Integer> inclusiverange = pMetadata.supportedFormats().get();
            if (!inclusiverange.isValueInRange(i)) {
                LOGGER.warn("Pack {} declared support for versions {} but declared main format is {}, defaulting to {}", pId, inclusiverange, i, i);
                return new InclusiveRange<>(i);
            } else {
                return inclusiverange;
            }
        }
    }

    public PackLocationInfo m_320537_() {
        return this.f_316989_;
    }

    public Component getTitle() {
        return this.f_316989_.f_316378_();
    }

    public Component getDescription() {
        return this.f_315327_.f_315370_();
    }

    public Component getChatLink(boolean pGreen) {
        return this.f_316989_.m_320992_(pGreen, this.f_315327_.f_315370_);
    }

    public PackCompatibility getCompatibility() {
        return this.f_315327_.f_314013_();
    }

    public FeatureFlagSet getRequestedFeatures() {
        return this.f_315327_.f_316794_();
    }

    public PackResources open() {
        return this.resources.openFull(this.f_316989_, this.f_315327_);
    }

    public String getId() {
        return this.f_316989_.f_316372_();
    }

    public PackSelectionConfig m_319332_() {
        return this.f_314425_;
    }

    public boolean isRequired() {
        return this.f_314425_.f_314129_();
    }

    public boolean isFixedPosition() {
        return this.f_314425_.f_315456_();
    }

    public Pack.Position getDefaultPosition() {
        return this.f_314425_.f_314230_();
    }

    public PackSource getPackSource() {
        return this.f_316989_.f_316564_();
    }

    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            return !(pOther instanceof Pack pack) ? false : this.f_316989_.equals(pack.f_316989_);
        }
    }

    @Override
    public int hashCode() {
        return this.f_316989_.hashCode();
    }

    public static record Metadata(Component f_315370_, PackCompatibility f_314013_, FeatureFlagSet f_316794_, List<String> f_316499_, boolean isHidden) {
        public Metadata(Component description, PackCompatibility compatibility, FeatureFlagSet requestedFeatures, List<String> overlays) {
            this(description, compatibility, requestedFeatures, overlays, false);
        }
    }

    public static enum Position {
        TOP,
        BOTTOM;

        public <T> int insert(List<T> pList, T pElement, Function<T, PackSelectionConfig> pPackFactory, boolean pFlipPosition) {
            Pack.Position pack$position = pFlipPosition ? this.opposite() : this;
            if (pack$position == BOTTOM) {
                int j;
                for (j = 0; j < pList.size(); j++) {
                    PackSelectionConfig packselectionconfig1 = pPackFactory.apply(pList.get(j));
                    if (!packselectionconfig1.f_315456_() || packselectionconfig1.f_314230_() != this) {
                        break;
                    }
                }

                pList.add(j, pElement);
                return j;
            } else {
                int i;
                for (i = pList.size() - 1; i >= 0; i--) {
                    PackSelectionConfig packselectionconfig = pPackFactory.apply(pList.get(i));
                    if (!packselectionconfig.f_315456_() || packselectionconfig.f_314230_() != this) {
                        break;
                    }
                }

                pList.add(i + 1, pElement);
                return i + 1;
            }
        }

        public Pack.Position opposite() {
            return this == TOP ? BOTTOM : TOP;
        }
    }

    public interface ResourcesSupplier {
        PackResources openPrimary(PackLocationInfo p_332103_);

        PackResources openFull(PackLocationInfo p_330351_, Pack.Metadata p_333429_);
    }
}
