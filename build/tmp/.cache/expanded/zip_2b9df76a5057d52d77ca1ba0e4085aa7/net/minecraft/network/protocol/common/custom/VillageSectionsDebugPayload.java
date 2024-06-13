package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record VillageSectionsDebugPayload(Set<SectionPos> villageChunks, Set<SectionPos> notVillageChunks) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, VillageSectionsDebugPayload> f_314252_ = CustomPacketPayload.m_320054_(
        VillageSectionsDebugPayload::m_293632_, VillageSectionsDebugPayload::new
    );
    public static final CustomPacketPayload.Type<VillageSectionsDebugPayload> f_315276_ = CustomPacketPayload.m_319865_("debug/village_sections");

    private VillageSectionsDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos), pBuffer.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos));
    }

    private void m_293632_(FriendlyByteBuf pBuffer) {
        pBuffer.writeCollection(this.villageChunks, FriendlyByteBuf::writeSectionPos);
        pBuffer.writeCollection(this.notVillageChunks, FriendlyByteBuf::writeSectionPos);
    }

    @Override
    public CustomPacketPayload.Type<VillageSectionsDebugPayload> m_293297_() {
        return f_315276_;
    }
}