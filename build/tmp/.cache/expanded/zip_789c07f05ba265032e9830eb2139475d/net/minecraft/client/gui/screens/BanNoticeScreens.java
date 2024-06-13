package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.time.Duration;
import java.time.Instant;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.chat.report.BanReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class BanNoticeScreens {
    private static final Component TEMPORARY_BAN_TITLE = Component.translatable("gui.banned.title.temporary").withStyle(ChatFormatting.BOLD);
    private static final Component PERMANENT_BAN_TITLE = Component.translatable("gui.banned.title.permanent").withStyle(ChatFormatting.BOLD);
    public static final Component NAME_BAN_TITLE = Component.translatable("gui.banned.name.title").withStyle(ChatFormatting.BOLD);
    private static final Component SKIN_BAN_TITLE = Component.translatable("gui.banned.skin.title").withStyle(ChatFormatting.BOLD);
    private static final Component SKIN_BAN_DESCRIPTION = Component.translatable("gui.banned.skin.description", Component.literal("https://aka.ms/mcjavamoderation"));

    public static ConfirmLinkScreen create(BooleanConsumer p_299994_, BanDetails p_297408_) {
        return new ConfirmLinkScreen(p_299994_, getBannedTitle(p_297408_), getBannedScreenText(p_297408_), "https://aka.ms/mcjavamoderation", CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createSkinBan(Runnable p_300032_) {
        String s = "https://aka.ms/mcjavamoderation";
        return new ConfirmLinkScreen(p_300188_ -> {
            if (p_300188_) {
                Util.getPlatform().openUri("https://aka.ms/mcjavamoderation");
            }

            p_300032_.run();
        }, SKIN_BAN_TITLE, SKIN_BAN_DESCRIPTION, "https://aka.ms/mcjavamoderation", CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    public static ConfirmLinkScreen createNameBan(String p_300838_, Runnable p_297249_) {
        String s = "https://aka.ms/mcjavamoderation";
        return new ConfirmLinkScreen(
            p_299238_ -> {
                if (p_299238_) {
                    Util.getPlatform().openUri("https://aka.ms/mcjavamoderation");
                }

                p_297249_.run();
            },
            NAME_BAN_TITLE,
            Component.translatable(
                "gui.banned.name.description", Component.literal(p_300838_).withStyle(ChatFormatting.YELLOW), "https://aka.ms/mcjavamoderation"
            ),
            "https://aka.ms/mcjavamoderation",
            CommonComponents.GUI_ACKNOWLEDGE,
            true
        );
    }

    private static Component getBannedTitle(BanDetails p_299452_) {
        return isTemporaryBan(p_299452_) ? TEMPORARY_BAN_TITLE : PERMANENT_BAN_TITLE;
    }

    private static Component getBannedScreenText(BanDetails p_299903_) {
        return Component.translatable("gui.banned.description", getBanReasonText(p_299903_), getBanStatusText(p_299903_), Component.literal("https://aka.ms/mcjavamoderation"));
    }

    private static Component getBanReasonText(BanDetails p_298548_) {
        String s = p_298548_.reason();
        String s1 = p_298548_.reasonMessage();
        if (StringUtils.isNumeric(s)) {
            int i = Integer.parseInt(s);
            BanReason banreason = BanReason.byId(i);
            Component component;
            if (banreason != null) {
                component = ComponentUtils.mergeStyles(banreason.title().copy(), Style.EMPTY.withBold(true));
            } else if (s1 != null) {
                component = Component.translatable("gui.banned.description.reason_id_message", i, s1).withStyle(ChatFormatting.BOLD);
            } else {
                component = Component.translatable("gui.banned.description.reason_id", i).withStyle(ChatFormatting.BOLD);
            }

            return Component.translatable("gui.banned.description.reason", component);
        } else {
            return Component.translatable("gui.banned.description.unknownreason");
        }
    }

    private static Component getBanStatusText(BanDetails p_298190_) {
        if (isTemporaryBan(p_298190_)) {
            Component component = getBanDurationText(p_298190_);
            return Component.translatable(
                "gui.banned.description.temporary", Component.translatable("gui.banned.description.temporary.duration", component).withStyle(ChatFormatting.BOLD)
            );
        } else {
            return Component.translatable("gui.banned.description.permanent").withStyle(ChatFormatting.BOLD);
        }
    }

    private static Component getBanDurationText(BanDetails p_300603_) {
        Duration duration = Duration.between(Instant.now(), p_300603_.expires());
        long i = duration.toHours();
        if (i > 72L) {
            return CommonComponents.days(duration.toDays());
        } else {
            return i < 1L ? CommonComponents.minutes(duration.toMinutes()) : CommonComponents.hours(duration.toHours());
        }
    }

    private static boolean isTemporaryBan(BanDetails p_300637_) {
        return p_300637_.expires() != null;
    }
}