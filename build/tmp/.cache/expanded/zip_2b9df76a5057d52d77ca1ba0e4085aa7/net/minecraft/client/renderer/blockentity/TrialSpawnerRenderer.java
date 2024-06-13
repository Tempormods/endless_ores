package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
    private final EntityRenderDispatcher f_302933_;

    public TrialSpawnerRenderer(BlockEntityRendererProvider.Context p_311333_) {
        this.f_302933_ = p_311333_.getEntityRenderer();
    }

    public void render(TrialSpawnerBlockEntity p_311991_, float p_312826_, PoseStack p_310994_, MultiBufferSource p_310042_, int p_311268_, int p_312508_) {
        Level level = p_311991_.getLevel();
        if (level != null) {
            TrialSpawner trialspawner = p_311991_.m_307437_();
            TrialSpawnerData trialspawnerdata = trialspawner.m_305472_();
            Entity entity = trialspawnerdata.m_307031_(trialspawner, level, trialspawner.m_305684_());
            if (entity != null) {
                SpawnerRenderer.m_305419_(
                    p_312826_, p_310994_, p_310042_, p_311268_, entity, this.f_302933_, trialspawnerdata.m_305098_(), trialspawnerdata.m_306486_()
                );
            }
        }
    }
}