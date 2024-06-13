package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface LoggedChatMessage extends LoggedChatEvent {
    static LoggedChatMessage.Player player(GameProfile pProfile, PlayerChatMessage pMessage, ChatTrustLevel pTrustLevel) {
        return new LoggedChatMessage.Player(pProfile, pMessage, pTrustLevel);
    }

    static LoggedChatMessage.System system(Component pMessage, Instant pTimestamp) {
        return new LoggedChatMessage.System(pMessage, pTimestamp);
    }

    Component toContentComponent();

    default Component toNarrationComponent() {
        return this.toContentComponent();
    }

    boolean canReport(UUID pUuid);

    @OnlyIn(Dist.CLIENT)
    public static record Player(GameProfile profile, PlayerChatMessage message, ChatTrustLevel trustLevel) implements LoggedChatMessage {
        public static final MapCodec<LoggedChatMessage.Player> CODEC = RecordCodecBuilder.mapCodec(
            p_261382_ -> p_261382_.group(
                        ExtraCodecs.GAME_PROFILE.fieldOf("profile").forGetter(LoggedChatMessage.Player::profile),
                        PlayerChatMessage.MAP_CODEC.forGetter(LoggedChatMessage.Player::message),
                        ChatTrustLevel.CODEC.optionalFieldOf("trust_level", ChatTrustLevel.SECURE).forGetter(LoggedChatMessage.Player::trustLevel)
                    )
                    .apply(p_261382_, LoggedChatMessage.Player::new)
        );
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        @Override
        public Component toContentComponent() {
            if (!this.message.filterMask().isEmpty()) {
                Component component = this.message.filterMask().applyWithFormatting(this.message.signedContent());
                return (Component)(component != null ? component : Component.empty());
            } else {
                return this.message.decoratedContent();
            }
        }

        @Override
        public Component toNarrationComponent() {
            Component component = this.toContentComponent();
            Component component1 = this.getTimeComponent();
            return Component.translatable("gui.chatSelection.message.narrate", this.profile.getName(), component, component1);
        }

        public Component toHeadingComponent() {
            Component component = this.getTimeComponent();
            return Component.translatable("gui.chatSelection.heading", this.profile.getName(), component);
        }

        private Component getTimeComponent() {
            LocalDateTime localdatetime = LocalDateTime.ofInstant(this.message.timeStamp(), ZoneOffset.systemDefault());
            return Component.literal(localdatetime.format(TIME_FORMATTER)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
        }

        @Override
        public boolean canReport(UUID p_242210_) {
            return this.message.hasSignatureFrom(p_242210_);
        }

        public UUID profileId() {
            return this.profile.getId();
        }

        @Override
        public LoggedChatEvent.Type type() {
            return LoggedChatEvent.Type.PLAYER;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record System(Component message, Instant timeStamp) implements LoggedChatMessage {
        public static final MapCodec<LoggedChatMessage.System> CODEC = RecordCodecBuilder.mapCodec(
            p_308279_ -> p_308279_.group(
                        ComponentSerialization.f_303288_.fieldOf("message").forGetter(LoggedChatMessage.System::message),
                        ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(LoggedChatMessage.System::timeStamp)
                    )
                    .apply(p_308279_, LoggedChatMessage.System::new)
        );

        @Override
        public Component toContentComponent() {
            return this.message;
        }

        @Override
        public boolean canReport(UUID p_242173_) {
            return false;
        }

        @Override
        public LoggedChatEvent.Type type() {
            return LoggedChatEvent.Type.SYSTEM;
        }
    }
}