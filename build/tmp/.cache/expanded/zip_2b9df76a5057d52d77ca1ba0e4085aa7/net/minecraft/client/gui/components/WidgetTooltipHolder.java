package net.minecraft.client.gui.components;

import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WidgetTooltipHolder {
    @Nullable
    private Tooltip f_314197_;
    private Duration f_316434_ = Duration.ZERO;
    private long f_316664_;
    private boolean f_316484_;

    public void m_320201_(Duration p_334379_) {
        this.f_316434_ = p_334379_;
    }

    public void m_321872_(@Nullable Tooltip p_327883_) {
        this.f_314197_ = p_327883_;
    }

    @Nullable
    public Tooltip m_323637_() {
        return this.f_314197_;
    }

    public void m_323585_(boolean p_330612_, boolean p_330175_, ScreenRectangle p_331953_) {
        if (this.f_314197_ == null) {
            this.f_316484_ = false;
        } else {
            boolean flag = p_330612_ || p_330175_ && Minecraft.getInstance().getLastInputType().isKeyboard();
            if (flag != this.f_316484_) {
                if (flag) {
                    this.f_316664_ = Util.getMillis();
                }

                this.f_316484_ = flag;
            }

            if (flag && Util.getMillis() - this.f_316664_ > this.f_316434_.toMillis()) {
                Screen screen = Minecraft.getInstance().screen;
                if (screen != null) {
                    screen.setTooltipForNextRenderPass(this.f_314197_, this.m_323619_(p_331953_, p_330612_, p_330175_), p_330175_);
                }
            }
        }
    }

    private ClientTooltipPositioner m_323619_(ScreenRectangle p_328060_, boolean p_329268_, boolean p_336280_) {
        return (ClientTooltipPositioner)(!p_329268_ && p_336280_ && Minecraft.getInstance().getLastInputType().isKeyboard()
            ? new BelowOrAboveWidgetTooltipPositioner(p_328060_)
            : new MenuTooltipPositioner(p_328060_));
    }

    public void m_322101_(NarrationElementOutput p_329365_) {
        if (this.f_314197_ != null) {
            this.f_314197_.updateNarration(p_329365_);
        }
    }
}