package net.minecraft.client.gui.components.debugchart;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debugchart.SampleStorage;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TpsDebugChart extends AbstractDebugChart {
    private static final int RED = -65536;
    private static final int YELLOW = -256;
    private static final int GREEN = -16711936;
    private static final int f_316482_ = -6745839;
    private static final int f_315584_ = -4548257;
    private static final int f_316843_ = -10547572;
    private final Supplier<Float> f_303738_;

    public TpsDebugChart(Font pFont, SampleStorage p_332350_, Supplier<Float> p_309657_) {
        super(pFont, p_332350_);
        this.f_303738_ = p_309657_;
    }

    @Override
    protected void renderAdditionalLinesAndLabels(GuiGraphics pGuiGraphics, int pX, int pWidth, int pHeight) {
        float f = (float)TimeUtil.f_302812_ / this.f_303738_.get();
        this.drawStringWithShade(pGuiGraphics, String.format("%.1f TPS", f), pX + 1, pHeight - 60 + 1);
    }

    @Override
    protected void m_321123_(GuiGraphics p_330453_, int p_332124_, int p_334033_, int p_330538_) {
        long i = this.f_316855_.m_320960_(p_330538_, TpsDebugDimensions.TICK_SERVER_METHOD.ordinal());
        int j = this.getSampleHeight((double)i);
        p_330453_.fill(RenderType.guiOverlay(), p_334033_, p_332124_ - j, p_334033_ + 1, p_332124_, -6745839);
        long k = this.f_316855_.m_320960_(p_330538_, TpsDebugDimensions.SCHEDULED_TASKS.ordinal());
        int l = this.getSampleHeight((double)k);
        p_330453_.fill(RenderType.guiOverlay(), p_334033_, p_332124_ - j - l, p_334033_ + 1, p_332124_ - j, -4548257);
        long i1 = this.f_316855_.m_318870_(p_330538_) - this.f_316855_.m_320960_(p_330538_, TpsDebugDimensions.IDLE.ordinal()) - i - k;
        int j1 = this.getSampleHeight((double)i1);
        p_330453_.fill(RenderType.guiOverlay(), p_334033_, p_332124_ - j1 - l - j, p_334033_ + 1, p_332124_ - l - j, -10547572);
    }

    @Override
    protected long m_320595_(int p_335820_) {
        return this.f_316855_.m_318870_(p_335820_) - this.f_316855_.m_320960_(p_335820_, TpsDebugDimensions.IDLE.ordinal());
    }

    @Override
    protected String toDisplayString(double pValue) {
        return String.format(Locale.ROOT, "%d ms", (int)Math.round(toMilliseconds(pValue)));
    }

    @Override
    protected int getSampleHeight(double pValue) {
        return (int)Math.round(toMilliseconds(pValue) * 60.0 / (double)this.f_303738_.get().floatValue());
    }

    @Override
    protected int getSampleColor(long pValue) {
        float f = this.f_303738_.get();
        return this.getSampleColor(toMilliseconds((double)pValue), (double)f, -16711936, (double)f * 1.125, -256, (double)f * 1.25, -65536);
    }

    private static double toMilliseconds(double pValue) {
        return pValue / 1000000.0;
    }
}