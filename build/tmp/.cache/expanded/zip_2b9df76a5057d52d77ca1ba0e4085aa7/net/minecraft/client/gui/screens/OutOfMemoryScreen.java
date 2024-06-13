package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OutOfMemoryScreen extends Screen {
    private static final Component f_315821_ = Component.translatable("outOfMemory.title");
    private static final Component f_315125_ = Component.translatable("outOfMemory.message");
    private static final int f_316414_ = 300;
    private final HeaderAndFooterLayout f_316501_ = new HeaderAndFooterLayout(this);

    public OutOfMemoryScreen() {
        super(f_315821_);
    }

    @Override
    protected void init() {
        this.f_316501_.m_324480_(f_315821_, this.font);
        this.f_316501_.addToContents(new FocusableTextWidget(300, f_315125_, this.font));
        LinearLayout linearlayout = this.f_316501_.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout.addChild(Button.builder(CommonComponents.GUI_TO_TITLE, p_280810_ -> this.minecraft.setScreen(new TitleScreen())).build());
        linearlayout.addChild(Button.builder(Component.translatable("menu.quit"), p_280811_ -> this.minecraft.stop()).build());
        this.f_316501_.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.f_316501_.arrangeElements();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}