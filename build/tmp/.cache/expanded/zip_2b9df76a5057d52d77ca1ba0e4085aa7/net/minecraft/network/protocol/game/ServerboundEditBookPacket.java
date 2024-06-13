package net.minecraft.network.protocol.game;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundEditBookPacket(int slot, List<String> pages, Optional<String> title) implements Packet<ServerGamePacketListener> {
    public static final int MAX_BYTES_PER_CHAR = 4;
    private static final int TITLE_MAX_CHARS = 128;
    private static final int PAGE_MAX_CHARS = 8192;
    private static final int MAX_PAGES_COUNT = 200;
    public static final StreamCodec<FriendlyByteBuf, ServerboundEditBookPacket> f_316818_ = StreamCodec.m_321516_(
        ByteBufCodecs.f_316730_,
        ServerboundEditBookPacket::slot,
        ByteBufCodecs.m_319534_(8192).m_321801_(ByteBufCodecs.m_319259_(200)),
        ServerboundEditBookPacket::pages,
        ByteBufCodecs.m_319534_(128).m_321801_(ByteBufCodecs::m_319027_),
        ServerboundEditBookPacket::title,
        ServerboundEditBookPacket::new
    );

    public ServerboundEditBookPacket(int slot, List<String> pages, Optional<String> title) {
        pages = List.copyOf(pages);
        this.slot = slot;
        this.pages = pages;
        this.title = title;
    }

    @Override
    public PacketType<ServerboundEditBookPacket> write() {
        return GamePacketTypes.f_316788_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleEditBook(this);
    }
}