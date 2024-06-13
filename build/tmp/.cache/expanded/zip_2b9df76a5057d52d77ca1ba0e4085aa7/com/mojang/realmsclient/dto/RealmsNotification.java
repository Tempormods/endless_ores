package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsNotification {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String NOTIFICATION_UUID = "notificationUuid";
    private static final String DISMISSABLE = "dismissable";
    private static final String SEEN = "seen";
    private static final String TYPE = "type";
    private static final String VISIT_URL = "visitUrl";
    private static final String f_303133_ = "infoPopup";
    static final Component f_302673_ = Component.translatable("mco.notification.visitUrl.buttonText.default");
    final UUID uuid;
    final boolean dismissable;
    final boolean seen;
    final String type;

    RealmsNotification(UUID pUuid, boolean pDismissable, boolean pSeen, String pType) {
        this.uuid = pUuid;
        this.dismissable = pDismissable;
        this.seen = pSeen;
        this.type = pType;
    }

    public boolean seen() {
        return this.seen;
    }

    public boolean dismissable() {
        return this.dismissable;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public static List<RealmsNotification> parseList(String pJson) {
        List<RealmsNotification> list = new ArrayList<>();

        try {
            for (JsonElement jsonelement : JsonParser.parseString(pJson).getAsJsonObject().get("notifications").getAsJsonArray()) {
                list.add(parse(jsonelement.getAsJsonObject()));
            }
        } catch (Exception exception) {
            LOGGER.error("Could not parse list of RealmsNotifications", (Throwable)exception);
        }

        return list;
    }

    private static RealmsNotification parse(JsonObject pJson) {
        UUID uuid = JsonUtils.getUuidOr("notificationUuid", pJson, null);
        if (uuid == null) {
            throw new IllegalStateException("Missing required property notificationUuid");
        } else {
            boolean flag = JsonUtils.getBooleanOr("dismissable", pJson, true);
            boolean flag1 = JsonUtils.getBooleanOr("seen", pJson, false);
            String s = JsonUtils.getRequiredString("type", pJson);
            RealmsNotification realmsnotification = new RealmsNotification(uuid, flag, flag1, s);

            return (RealmsNotification)(switch (s) {
                case "visitUrl" -> RealmsNotification.VisitUrl.parse(realmsnotification, pJson);
                case "infoPopup" -> RealmsNotification.InfoPopup.m_306041_(realmsnotification, pJson);
                default -> realmsnotification;
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class InfoPopup extends RealmsNotification {
        private static final String f_302768_ = "title";
        private static final String f_302899_ = "message";
        private static final String f_302984_ = "image";
        private static final String f_302866_ = "urlButton";
        private final RealmsText f_302312_;
        private final RealmsText f_303866_;
        private final ResourceLocation f_303001_;
        @Nullable
        private final RealmsNotification.UrlButton f_302575_;

        private InfoPopup(
            RealmsNotification p_311557_,
            RealmsText p_310281_,
            RealmsText p_312392_,
            ResourceLocation p_312062_,
            @Nullable RealmsNotification.UrlButton p_309599_
        ) {
            super(p_311557_.uuid, p_311557_.dismissable, p_311557_.seen, p_311557_.type);
            this.f_302312_ = p_310281_;
            this.f_303866_ = p_312392_;
            this.f_303001_ = p_312062_;
            this.f_302575_ = p_309599_;
        }

        public static RealmsNotification.InfoPopup m_306041_(RealmsNotification p_311623_, JsonObject p_309477_) {
            RealmsText realmstext = JsonUtils.getRequired("title", p_309477_, RealmsText::parse);
            RealmsText realmstext1 = JsonUtils.getRequired("message", p_309477_, RealmsText::parse);
            ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getRequiredString("image", p_309477_));
            RealmsNotification.UrlButton realmsnotification$urlbutton = JsonUtils.m_304933_("urlButton", p_309477_, RealmsNotification.UrlButton::m_305558_);
            return new RealmsNotification.InfoPopup(p_311623_, realmstext, realmstext1, resourcelocation, realmsnotification$urlbutton);
        }

        @Nullable
        public PopupScreen m_304898_(Screen p_312232_, Consumer<UUID> p_310624_) {
            Component component = this.f_302312_.m_307258_();
            if (component == null) {
                RealmsNotification.LOGGER.warn("Realms info popup had title with no available translation: {}", this.f_302312_);
                return null;
            } else {
                PopupScreen.Builder popupscreen$builder = new PopupScreen.Builder(p_312232_, component)
                    .m_307765_(this.f_303001_)
                    .m_307758_(this.f_303866_.createComponent(CommonComponents.EMPTY));
                if (this.f_302575_ != null) {
                    popupscreen$builder.m_305980_(this.f_302575_.f_302705_.createComponent(RealmsNotification.f_302673_), p_310971_ -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        minecraft.setScreen(new ConfirmLinkScreen(p_311599_ -> {
                            if (p_311599_) {
                                Util.getPlatform().openUri(this.f_302575_.f_303755_);
                                minecraft.setScreen(p_312232_);
                            } else {
                                minecraft.setScreen(p_310971_);
                            }
                        }, this.f_302575_.f_303755_, true));
                        p_310624_.accept(this.uuid());
                    });
                }

                popupscreen$builder.m_305980_(CommonComponents.GUI_OK, p_311029_ -> {
                    p_311029_.onClose();
                    p_310624_.accept(this.uuid());
                });
                popupscreen$builder.m_304891_(() -> p_310624_.accept(this.uuid()));
                return popupscreen$builder.m_307029_();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record UrlButton(String f_303755_, RealmsText f_302705_) {
        private static final String f_303560_ = "url";
        private static final String f_303091_ = "urlText";

        public static RealmsNotification.UrlButton m_305558_(JsonObject p_310888_) {
            String s = JsonUtils.getRequiredString("url", p_310888_);
            RealmsText realmstext = JsonUtils.getRequired("urlText", p_310888_, RealmsText::parse);
            return new RealmsNotification.UrlButton(s, realmstext);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class VisitUrl extends RealmsNotification {
        private static final String URL = "url";
        private static final String BUTTON_TEXT = "buttonText";
        private static final String MESSAGE = "message";
        private final String url;
        private final RealmsText buttonText;
        private final RealmsText message;

        private VisitUrl(RealmsNotification pNotification, String pUrl, RealmsText pButtonText, RealmsText pMessage) {
            super(pNotification.uuid, pNotification.dismissable, pNotification.seen, pNotification.type);
            this.url = pUrl;
            this.buttonText = pButtonText;
            this.message = pMessage;
        }

        public static RealmsNotification.VisitUrl parse(RealmsNotification pNotification, JsonObject pJson) {
            String s = JsonUtils.getRequiredString("url", pJson);
            RealmsText realmstext = JsonUtils.getRequired("buttonText", pJson, RealmsText::parse);
            RealmsText realmstext1 = JsonUtils.getRequired("message", pJson, RealmsText::parse);
            return new RealmsNotification.VisitUrl(pNotification, s, realmstext, realmstext1);
        }

        public Component getMessage() {
            return this.message.createComponent(Component.translatable("mco.notification.visitUrl.message.default"));
        }

        public Button buildOpenLinkButton(Screen pLastScreen) {
            Component component = this.buttonText.createComponent(RealmsNotification.f_302673_);
            return Button.builder(component, ConfirmLinkScreen.confirmLink(pLastScreen, this.url)).build();
        }
    }
}