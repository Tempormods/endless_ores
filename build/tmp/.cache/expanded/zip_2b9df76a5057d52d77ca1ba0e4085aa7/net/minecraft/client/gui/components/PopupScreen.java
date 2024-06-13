package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PopupScreen extends Screen {
    private static final ResourceLocation f_302308_ = new ResourceLocation("popup/background");
    private static final int f_303098_ = 12;
    private static final int f_303750_ = 18;
    private static final int f_303197_ = 6;
    private static final int f_302379_ = 130;
    private static final int f_303011_ = 64;
    private static final int f_302717_ = 250;
    private final Screen f_302750_;
    @Nullable
    private final ResourceLocation f_303685_;
    private final Component f_302929_;
    private final List<PopupScreen.ButtonOption> f_303229_;
    @Nullable
    private final Runnable f_302877_;
    private final int f_303828_;
    private final LinearLayout f_303064_ = LinearLayout.vertical();

    PopupScreen(
        Screen p_311716_,
        int p_312972_,
        @Nullable ResourceLocation p_312263_,
        Component p_311243_,
        Component p_313078_,
        List<PopupScreen.ButtonOption> p_312924_,
        @Nullable Runnable p_309530_
    ) {
        super(p_311243_);
        this.f_302750_ = p_311716_;
        this.f_303685_ = p_312263_;
        this.f_302929_ = p_313078_;
        this.f_303229_ = p_312924_;
        this.f_302877_ = p_309530_;
        this.f_303828_ = p_312972_ - 36;
    }

    @Override
    public void added() {
        super.added();
        this.f_302750_.clearFocus();
    }

    @Override
    protected void init() {
        this.f_303064_.spacing(12).defaultCellSetting().alignHorizontallyCenter();
        this.f_303064_
            .addChild(new MultiLineTextWidget(this.title.copy().withStyle(ChatFormatting.BOLD), this.font).setMaxWidth(this.f_303828_).setCentered(true));
        if (this.f_303685_ != null) {
            this.f_303064_.addChild(ImageWidget.texture(130, 64, this.f_303685_, 130, 64));
        }

        this.f_303064_.addChild(new MultiLineTextWidget(this.f_302929_, this.font).setMaxWidth(this.f_303828_).setCentered(true));
        this.f_303064_.addChild(this.m_306027_());
        this.f_303064_.visitWidgets(p_325330_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325330_);
        });
        this.repositionElements();
    }

    private LinearLayout m_306027_() {
        int i = 6 * (this.f_303229_.size() - 1);
        int j = Math.min((this.f_303828_ - i) / this.f_303229_.size(), 150);
        LinearLayout linearlayout = LinearLayout.horizontal();
        linearlayout.spacing(6);

        for (PopupScreen.ButtonOption popupscreen$buttonoption : this.f_303229_) {
            linearlayout.addChild(
                Button.builder(popupscreen$buttonoption.f_302383_(), p_310515_ -> popupscreen$buttonoption.f_303633_().accept(this)).width(j).build()
            );
        }

        return linearlayout;
    }

    @Override
    protected void repositionElements() {
        this.f_302750_.resize(this.minecraft, this.width, this.height);
        this.f_303064_.arrangeElements();
        FrameLayout.centerInRectangle(this.f_303064_, this.getRectangle());
    }

    @Override
    public void renderBackground(GuiGraphics p_312654_, int p_312824_, int p_310533_, float p_313128_) {
        this.f_302750_.render(p_312654_, -1, -1, p_313128_);
        p_312654_.flush();
        RenderSystem.clear(256, Minecraft.ON_OSX);
        this.renderTransparentBackground(p_312654_);
        p_312654_.blitSprite(
            f_302308_, this.f_303064_.getX() - 18, this.f_303064_.getY() - 18, this.f_303064_.getWidth() + 36, this.f_303064_.getHeight() + 36
        );
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(this.title, this.f_302929_);
    }

    @Override
    public void onClose() {
        if (this.f_302877_ != null) {
            this.f_302877_.run();
        }

        this.minecraft.setScreen(this.f_302750_);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Screen f_303166_;
        private final Component f_302854_;
        private Component f_302432_ = CommonComponents.EMPTY;
        private int f_303769_ = 250;
        @Nullable
        private ResourceLocation f_302994_;
        private final List<PopupScreen.ButtonOption> f_302895_ = new ArrayList<>();
        @Nullable
        private Runnable f_303530_ = null;

        public Builder(Screen p_311941_, Component p_309447_) {
            this.f_303166_ = p_311941_;
            this.f_302854_ = p_309447_;
        }

        public PopupScreen.Builder m_305535_(int p_311856_) {
            this.f_303769_ = p_311856_;
            return this;
        }

        public PopupScreen.Builder m_307765_(ResourceLocation p_309878_) {
            this.f_302994_ = p_309878_;
            return this;
        }

        public PopupScreen.Builder m_307758_(Component p_309841_) {
            this.f_302432_ = p_309841_;
            return this;
        }

        public PopupScreen.Builder m_305980_(Component p_309455_, Consumer<PopupScreen> p_311142_) {
            this.f_302895_.add(new PopupScreen.ButtonOption(p_309455_, p_311142_));
            return this;
        }

        public PopupScreen.Builder m_304891_(Runnable p_311998_) {
            this.f_303530_ = p_311998_;
            return this;
        }

        public PopupScreen m_307029_() {
            if (this.f_302895_.isEmpty()) {
                throw new IllegalStateException("Popup must have at least one button");
            } else {
                return new PopupScreen(
                    this.f_303166_, this.f_303769_, this.f_302994_, this.f_302854_, this.f_302432_, List.copyOf(this.f_302895_), this.f_303530_
                );
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record ButtonOption(Component f_302383_, Consumer<PopupScreen> f_303633_) {
    }
}