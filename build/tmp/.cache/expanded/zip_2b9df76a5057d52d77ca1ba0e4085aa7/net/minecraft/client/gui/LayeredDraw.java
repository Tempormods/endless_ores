package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayeredDraw {
    public static final float f_314628_ = 200.0F;
    private final List<LayeredDraw.Layer> f_314699_ = new ArrayList<>();

    public LayeredDraw m_322513_(LayeredDraw.Layer p_332264_) {
        this.f_314699_.add(p_332264_);
        return this;
    }

    public LayeredDraw m_323151_(LayeredDraw p_328749_, BooleanSupplier p_332055_) {
        return this.m_322513_((p_331839_, p_333777_) -> {
            if (p_332055_.getAsBoolean()) {
                p_328749_.m_321084_(p_331839_, p_333777_);
            }
        });
    }

    public void m_322951_(GuiGraphics p_335429_, float p_332136_) {
        p_335429_.pose().pushPose();
        this.m_321084_(p_335429_, p_332136_);
        p_335429_.pose().popPose();
    }

    private void m_321084_(GuiGraphics p_333655_, float p_331829_) {
        for (LayeredDraw.Layer layereddraw$layer : this.f_314699_) {
            layereddraw$layer.m_324219_(p_333655_, p_331829_);
            p_333655_.pose().translate(0.0F, 0.0F, 200.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface Layer {
        void m_324219_(GuiGraphics p_328217_, float p_327998_);
    }
}