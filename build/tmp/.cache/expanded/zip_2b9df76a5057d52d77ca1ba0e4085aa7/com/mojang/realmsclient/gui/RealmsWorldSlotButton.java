package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldSlotButton extends Button {
    private static final ResourceLocation SLOT_FRAME_SPRITE = new ResourceLocation("widget/slot_frame");
    private static final ResourceLocation CHECKMARK_SPRITE = new ResourceLocation("icon/checkmark");
    public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("textures/gui/realms/empty_frame.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
    public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
    private static final Component SLOT_ACTIVE_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.active");
    private static final Component SWITCH_TO_MINIGAME_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip.minigame");
    private static final Component SWITCH_TO_WORLD_SLOT_TOOLTIP = Component.translatable("mco.configure.world.slot.tooltip");
    static final Component MINIGAME = Component.translatable("mco.worldSlot.minigame");
    private final int slotIndex;
    @Nullable
    private RealmsWorldSlotButton.State state;

    public RealmsWorldSlotButton(int pX, int pY, int pWidth, int pHeight, int pSlotIndex, Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
        this.slotIndex = pSlotIndex;
    }

    @Nullable
    public RealmsWorldSlotButton.State getState() {
        return this.state;
    }

    public void m_306833_(RealmsServer p_310623_) {
        this.state = new RealmsWorldSlotButton.State(p_310623_, this.slotIndex);
        this.m_307605_(this.state, p_310623_.minigameName);
    }

    private void m_307605_(RealmsWorldSlotButton.State p_312604_, @Nullable String p_310582_) {
        Component component = switch (p_312604_.action) {
            case SWITCH_SLOT -> p_312604_.minigame ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
            case JOIN -> SLOT_ACTIVE_TOOLTIP;
            default -> null;
        };
        if (component != null) {
            this.setTooltip(Tooltip.create(component));
        }

        MutableComponent mutablecomponent = Component.literal(p_312604_.slotName);
        if (p_312604_.minigame && p_310582_ != null) {
            mutablecomponent = mutablecomponent.append(CommonComponents.SPACE).append(p_310582_);
        }

        this.setMessage(mutablecomponent);
    }

    static RealmsWorldSlotButton.Action getAction(RealmsServer pRealmsServer, boolean pIsCurrentlyActiveSlot, boolean pMinigame) {
        if (pIsCurrentlyActiveSlot && !pRealmsServer.expired && pRealmsServer.state != RealmsServer.State.UNINITIALIZED) {
            return RealmsWorldSlotButton.Action.JOIN;
        } else {
            return pIsCurrentlyActiveSlot || pMinigame && pRealmsServer.expired ? RealmsWorldSlotButton.Action.NOTHING : RealmsWorldSlotButton.Action.SWITCH_SLOT;
        }
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.state != null) {
            int i = this.getX();
            int j = this.getY();
            boolean flag = this.isHoveredOrFocused();
            ResourceLocation resourcelocation;
            if (this.state.minigame) {
                resourcelocation = RealmsTextureManager.worldTemplate(String.valueOf(this.state.imageId), this.state.image);
            } else if (this.state.empty) {
                resourcelocation = EMPTY_SLOT_LOCATION;
            } else if (this.state.image != null && this.state.imageId != -1L) {
                resourcelocation = RealmsTextureManager.worldTemplate(String.valueOf(this.state.imageId), this.state.image);
            } else if (this.slotIndex == 1) {
                resourcelocation = DEFAULT_WORLD_SLOT_1;
            } else if (this.slotIndex == 2) {
                resourcelocation = DEFAULT_WORLD_SLOT_2;
            } else if (this.slotIndex == 3) {
                resourcelocation = DEFAULT_WORLD_SLOT_3;
            } else {
                resourcelocation = EMPTY_SLOT_LOCATION;
            }

            if (this.state.isCurrentlyActiveSlot) {
                pGuiGraphics.setColor(0.56F, 0.56F, 0.56F, 1.0F);
            }

            pGuiGraphics.blit(resourcelocation, i + 3, j + 3, 0.0F, 0.0F, 74, 74, 74, 74);
            boolean flag1 = flag && this.state.action != RealmsWorldSlotButton.Action.NOTHING;
            if (flag1) {
                pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else if (this.state.isCurrentlyActiveSlot) {
                pGuiGraphics.setColor(0.8F, 0.8F, 0.8F, 1.0F);
            } else {
                pGuiGraphics.setColor(0.56F, 0.56F, 0.56F, 1.0F);
            }

            pGuiGraphics.blitSprite(SLOT_FRAME_SPRITE, i, j, 80, 80);
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.state.isCurrentlyActiveSlot) {
                RenderSystem.enableBlend();
                pGuiGraphics.blitSprite(CHECKMARK_SPRITE, i + 67, j + 4, 9, 8);
                RenderSystem.disableBlend();
            }

            Font font = Minecraft.getInstance().font;
            pGuiGraphics.drawCenteredString(font, this.state.slotName, i + 40, j + 66, -1);
            pGuiGraphics.drawCenteredString(font, RealmsMainScreen.m_305395_(this.state.f_302212_, this.state.f_303774_.m_307186_()), i + 40, j + 80 + 2, -1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Action {
        NOTHING,
        SWITCH_SLOT,
        JOIN;
    }

    @OnlyIn(Dist.CLIENT)
    public static class State {
        final boolean isCurrentlyActiveSlot;
        final String slotName;
        final String f_302212_;
        final RealmsServer.Compatibility f_303774_;
        final long imageId;
        @Nullable
        final String image;
        public final boolean empty;
        public final boolean minigame;
        public final RealmsWorldSlotButton.Action action;

        public State(RealmsServer p_309960_, int p_309979_) {
            this.minigame = p_309979_ == 4;
            if (this.minigame) {
                this.isCurrentlyActiveSlot = p_309960_.worldType == RealmsServer.WorldType.MINIGAME;
                this.slotName = RealmsWorldSlotButton.MINIGAME.getString();
                this.imageId = (long)p_309960_.minigameId;
                this.image = p_309960_.minigameImage;
                this.empty = p_309960_.minigameId == -1;
                this.f_302212_ = "";
                this.f_303774_ = RealmsServer.Compatibility.UNVERIFIABLE;
            } else {
                RealmsWorldOptions realmsworldoptions = p_309960_.slots.get(p_309979_);
                this.isCurrentlyActiveSlot = p_309960_.activeSlot == p_309979_ && p_309960_.worldType != RealmsServer.WorldType.MINIGAME;
                this.slotName = realmsworldoptions.getSlotName(p_309979_);
                this.imageId = realmsworldoptions.templateId;
                this.image = realmsworldoptions.templateImage;
                this.empty = realmsworldoptions.empty;
                this.f_302212_ = realmsworldoptions.f_303798_;
                this.f_303774_ = realmsworldoptions.f_302886_;
            }

            this.action = RealmsWorldSlotButton.getAction(p_309960_, this.isCurrentlyActiveSlot, this.minigame);
        }
    }
}