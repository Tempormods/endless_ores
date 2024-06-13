package net.minecraft.network.protocol.game;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundCommandSuggestionsPacket(int id, int f_316300_, int f_316678_, List<ClientboundCommandSuggestionsPacket.Entry> suggestions)
    implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandSuggestionsPacket> f_315522_ = StreamCodec.m_319980_(
        ByteBufCodecs.f_316730_,
        ClientboundCommandSuggestionsPacket::id,
        ByteBufCodecs.f_316730_,
        ClientboundCommandSuggestionsPacket::f_316300_,
        ByteBufCodecs.f_316730_,
        ClientboundCommandSuggestionsPacket::f_316678_,
        ClientboundCommandSuggestionsPacket.Entry.f_316684_.m_321801_(ByteBufCodecs.m_324765_()),
        ClientboundCommandSuggestionsPacket::suggestions,
        ClientboundCommandSuggestionsPacket::new
    );

    public ClientboundCommandSuggestionsPacket(int pId, Suggestions pSuggestions) {
        this(
            pId,
            pSuggestions.getRange().getStart(),
            pSuggestions.getRange().getLength(),
            pSuggestions.getList()
                .stream()
                .map(
                    p_326097_ -> new ClientboundCommandSuggestionsPacket.Entry(
                            p_326097_.getText(), Optional.ofNullable(p_326097_.getTooltip()).map(ComponentUtils::fromMessage)
                        )
                )
                .toList()
        );
    }

    @Override
    public PacketType<ClientboundCommandSuggestionsPacket> write() {
        return GamePacketTypes.f_314280_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleCommandSuggestions(this);
    }

    public Suggestions m_319734_() {
        StringRange stringrange = StringRange.between(this.f_316300_, this.f_316300_ + this.f_316678_);
        return new Suggestions(
            stringrange,
            this.suggestions.stream().map(p_326096_ -> new Suggestion(stringrange, p_326096_.f_315395_(), p_326096_.f_314923_().orElse(null))).toList()
        );
    }

    public static record Entry(String f_315395_, Optional<Component> f_314923_) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCommandSuggestionsPacket.Entry> f_316684_ = StreamCodec.m_320349_(
            ByteBufCodecs.f_315450_,
            ClientboundCommandSuggestionsPacket.Entry::f_315395_,
            ComponentSerialization.f_316844_,
            ClientboundCommandSuggestionsPacket.Entry::f_314923_,
            ClientboundCommandSuggestionsPacket.Entry::new
        );
    }
}