package net.minecraft.client.gui.screens;

import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AccessibilityOnboardingScreen extends Screen {
    private static final Component f_317152_ = Component.translatable("accessibility.onboarding.screen.title");
    private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
    private static final int PADDING = 4;
    private static final int TITLE_PADDING = 16;
    private final LogoRenderer logoRenderer;
    private final Options options;
    private final boolean narratorAvailable;
    private boolean hasNarrated;
    private float timer;
    private final Runnable onClose;
    @Nullable
    private FocusableTextWidget textWidget;
    @Nullable
    private AbstractWidget f_316442_;
    private final HeaderAndFooterLayout f_316821_ = new HeaderAndFooterLayout(this, this.initTitleYPos(), 33);

    public AccessibilityOnboardingScreen(Options pOptions, Runnable pOnClose) {
        super(f_317152_);
        this.options = pOptions;
        this.onClose = pOnClose;
        this.logoRenderer = new LogoRenderer(true);
        this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
    }

    @Override
    public void init() {
        LinearLayout linearlayout = this.f_316821_.addToContents(LinearLayout.vertical());
        linearlayout.defaultCellSetting().alignHorizontallyCenter().padding(4);
        this.textWidget = linearlayout.addChild(new FocusableTextWidget(this.width, this.title, this.font), p_325362_ -> p_325362_.padding(8));
        this.f_316442_ = this.options.narrator().m_324463_(this.options);
        this.f_316442_.active = this.narratorAvailable;
        linearlayout.addChild(this.f_316442_);
        linearlayout.addChild(CommonButtons.accessibility(150, p_280782_ -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), false));
        linearlayout.addChild(
            CommonButtons.language(150, p_280781_ -> this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), false)
        );
        this.f_316821_.addToFooter(Button.builder(CommonComponents.GUI_CONTINUE, p_267841_ -> this.onClose()).build());
        this.f_316821_.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (this.textWidget != null) {
            this.textWidget.m_319387_(this.width);
        }

        this.f_316821_.arrangeElements();
    }

    @Override
    protected void m_318615_() {
        if (this.narratorAvailable && this.f_316442_ != null) {
            this.setInitialFocus(this.f_316442_);
        } else {
            super.m_318615_();
        }
    }

    private int initTitleYPos() {
        return 90;
    }

    @Override
    public void onClose() {
        this.close(this.onClose);
    }

    private void closeAndSetScreen(Screen pScreen) {
        this.close(() -> this.minecraft.setScreen(pScreen));
    }

    private void close(Runnable pOnClose) {
        this.options.onboardAccessibility = false;
        this.options.save();
        Narrator.getNarrator().clear();
        pOnClose.run();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.handleInitialNarrationDelay();
        this.logoRenderer.renderLogo(pGuiGraphics, this.width, 1.0F);
    }

    @Override
    protected void m_318720_(GuiGraphics p_336323_, float p_332027_) {
        f_317031_.render(p_336323_, this.width, this.height, 1.0F, 0.0F);
    }

    private void handleInitialNarrationDelay() {
        if (!this.hasNarrated && this.narratorAvailable) {
            if (this.timer < 40.0F) {
                this.timer++;
            } else if (this.minecraft.isWindowActive()) {
                Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true);
                this.hasNarrated = true;
            }
        }
    }
}