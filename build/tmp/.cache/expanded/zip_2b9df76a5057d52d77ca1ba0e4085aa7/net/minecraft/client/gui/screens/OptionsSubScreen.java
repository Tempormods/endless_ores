package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsSubScreen extends Screen {
    protected final Screen lastScreen;
    protected final Options options;
    public final HeaderAndFooterLayout f_314621_ = new HeaderAndFooterLayout(this);

    public OptionsSubScreen(Screen pLastScreen, Options pOptions, Component pTitle) {
        super(pTitle);
        this.lastScreen = pLastScreen;
        this.options = pOptions;
    }

    @Override
    protected void init() {
        this.m_323887_();
        this.m_319570_();
        this.f_314621_.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    protected void m_323887_() {
        this.f_314621_.m_324480_(this.title, this.font);
    }

    protected void m_319570_() {
        this.f_314621_.addToFooter(Button.builder(CommonComponents.GUI_DONE, p_333159_ -> this.onClose()).width(200).build());
    }

    @Override
    protected void repositionElements() {
        this.f_314621_.arrangeElements();
    }

    @Override
    public void removed() {
        this.minecraft.options.save();
    }

    @Override
    public void onClose() {
        for (GuiEventListener guieventlistener : this.children()) {
            if (guieventlistener instanceof OptionsList optionslist) {
                optionslist.m_323432_();
            }
        }

        this.minecraft.setScreen(this.lastScreen);
    }
}