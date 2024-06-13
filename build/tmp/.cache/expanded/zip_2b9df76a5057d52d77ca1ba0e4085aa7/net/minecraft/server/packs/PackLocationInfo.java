package net.minecraft.server.packs;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackSource;

public record PackLocationInfo(String f_316372_, Component f_316378_, PackSource f_316564_, Optional<KnownPack> f_314017_) {
    public Component m_320992_(boolean p_333920_, Component p_329432_) {
        return ComponentUtils.wrapInSquareBrackets(this.f_316564_.decorate(Component.literal(this.f_316372_)))
            .withStyle(
                p_333907_ -> p_333907_.withColor(p_333920_ ? ChatFormatting.GREEN : ChatFormatting.RED)
                        .withInsertion(StringArgumentType.escapeIfRequired(this.f_316372_))
                        .withHoverEvent(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(this.f_316378_).append("\n").append(p_329432_))
                        )
            );
    }
}