package com.mojang.realmsclient.gui.screens;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
    private static final Component INCOMPATIBLE_TITLE = Component.translatable("mco.client.incompatible.title").m_306658_(-65536);
    private static final Component f_315351_ = Component.literal(SharedConstants.getCurrentVersion().getName()).m_306658_(-65536);
    private static final Component f_314165_ = Component.translatable("mco.client.unsupported.snapshot.version", f_315351_);
    private static final Component f_316169_ = Component.translatable("mco.client.outdated.stable.version", f_315351_);
    private final Screen lastScreen;
    private final HeaderAndFooterLayout f_314768_ = new HeaderAndFooterLayout(this);

    public RealmsClientOutdatedScreen(Screen pLastScreen) {
        super(INCOMPATIBLE_TITLE);
        this.lastScreen = pLastScreen;
    }

    @Override
    public void init() {
        this.f_314768_.m_324480_(INCOMPATIBLE_TITLE, this.font);
        this.f_314768_.addToContents(new MultiLineTextWidget(this.m_324674_(), this.font).setCentered(true));
        this.f_314768_.addToFooter(Button.builder(CommonComponents.GUI_BACK, p_325116_ -> this.onClose()).width(200).build());
        this.f_314768_.visitWidgets(p_325118_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325118_);
        });
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.f_314768_.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private Component m_324674_() {
        return SharedConstants.getCurrentVersion().isStable() ? f_316169_ : f_314165_;
    }
}