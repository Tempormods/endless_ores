package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PanoramaRenderer {
    public static final ResourceLocation f_314014_ = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private final Minecraft minecraft;
    private final CubeMap cubeMap;
    private float spin;
    private float bob;

    public PanoramaRenderer(CubeMap pCubeMap) {
        this.cubeMap = pCubeMap;
        this.minecraft = Minecraft.getInstance();
    }

    public void render(GuiGraphics p_331913_, int p_332706_, int p_333201_, float pDeltaT, float pAlpha) {
        float f = (float)((double)pAlpha * this.minecraft.options.panoramaSpeed().get());
        this.spin = wrap(this.spin + f * 0.1F, 360.0F);
        this.bob = wrap(this.bob + f * 0.001F, (float) (Math.PI * 2));
        this.cubeMap.render(this.minecraft, 10.0F, -this.spin, pDeltaT);
        RenderSystem.enableBlend();
        p_331913_.setColor(1.0F, 1.0F, 1.0F, pDeltaT);
        p_331913_.blit(f_314014_, 0, 0, p_332706_, p_333201_, 0.0F, 0.0F, 16, 128, 16, 128);
        p_331913_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    private static float wrap(float pValue, float pMax) {
        return pValue > pMax ? pValue - pMax : pValue;
    }
}