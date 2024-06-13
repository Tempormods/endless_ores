package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AccessibilityOptionsScreen extends SimpleOptionsSubScreen {
    public static final Component f_316258_ = Component.translatable("options.accessibility.title");

    private static OptionInstance<?>[] options(Options pOptions) {
        return new OptionInstance[]{
            pOptions.narrator(),
            pOptions.showSubtitles(),
            pOptions.highContrast(),
            pOptions.autoJump(),
            pOptions.m_323040_(),
            pOptions.textBackgroundOpacity(),
            pOptions.backgroundForChatOnly(),
            pOptions.chatOpacity(),
            pOptions.chatLineSpacing(),
            pOptions.chatDelay(),
            pOptions.notificationDisplayTime(),
            pOptions.toggleCrouch(),
            pOptions.toggleSprint(),
            pOptions.screenEffectScale(),
            pOptions.fovEffectScale(),
            pOptions.darknessEffectScale(),
            pOptions.damageTiltStrength(),
            pOptions.glintSpeed(),
            pOptions.glintStrength(),
            pOptions.hideLightningFlash(),
            pOptions.darkMojangStudiosBackground(),
            pOptions.panoramaSpeed(),
            pOptions.m_307023_(),
            pOptions.narratorHotkey()
        };
    }

    public AccessibilityOptionsScreen(Screen pLastScreen, Options pOptions) {
        super(pLastScreen, pOptions, f_316258_, options(pOptions));
    }

    @Override
    protected void init() {
        super.init();
        AbstractWidget abstractwidget = this.list.findOption(this.options.highContrast());
        if (abstractwidget != null && !this.minecraft.getResourcePackRepository().getAvailableIds().contains("high_contrast")) {
            abstractwidget.active = false;
            abstractwidget.setTooltip(Tooltip.create(Component.translatable("options.accessibility.high_contrast.error.tooltip")));
        }
    }

    @Override
    protected void m_319570_() {
        LinearLayout linearlayout = this.f_314621_.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout.addChild(
            Button.builder(Component.translatable("options.accessibility.link"), ConfirmLinkScreen.confirmLink(this, "https://aka.ms/MinecraftJavaAccessibility"))
                .build()
        );
        linearlayout.addChild(Button.builder(CommonComponents.GUI_DONE, p_280785_ -> this.minecraft.setScreen(this.lastScreen)).build());
    }
}