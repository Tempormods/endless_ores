package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BreezeDebugRenderer {
    private static final int f_302700_ = FastColor.ARGB32.color(255, 255, 100, 255);
    private static final int f_303590_ = FastColor.ARGB32.color(255, 100, 255, 255);
    private static final int f_303283_ = FastColor.ARGB32.color(255, 0, 255, 0);
    private static final int f_303307_ = FastColor.ARGB32.color(255, 255, 165, 0);
    private static final int f_303863_ = FastColor.ARGB32.color(255, 255, 0, 0);
    private static final int f_303865_ = 20;
    private static final float f_302238_ = (float) (Math.PI / 10);
    private final Minecraft f_303199_;
    private final Map<Integer, BreezeDebugPayload.BreezeInfo> f_303736_ = new HashMap<>();

    public BreezeDebugRenderer(Minecraft p_312673_) {
        this.f_303199_ = p_312673_;
    }

    public void m_306567_(PoseStack p_311387_, MultiBufferSource p_310722_, double p_312623_, double p_310151_, double p_312438_) {
        LocalPlayer localplayer = this.f_303199_.player;
        localplayer.level()
            .getEntities(EntityType.f_302782_, localplayer.getBoundingBox().inflate(100.0), p_312249_ -> true)
            .forEach(
                p_325541_ -> {
                    Optional<BreezeDebugPayload.BreezeInfo> optional = Optional.ofNullable(this.f_303736_.get(p_325541_.getId()));
                    optional.map(BreezeDebugPayload.BreezeInfo::f_303058_)
                        .map(p_325543_ -> localplayer.level().getEntity(p_325543_))
                        .map(p_311009_ -> p_311009_.getPosition(this.f_303199_.getFrameTime()))
                        .ifPresent(
                            p_310972_ -> {
                                m_304788_(p_311387_, p_310722_, p_312623_, p_310151_, p_312438_, p_325541_.position(), p_310972_, f_303590_);
                                Vec3 vec3 = p_310972_.add(0.0, 0.01F, 0.0);
                                m_307508_(
                                    p_311387_.last().pose(),
                                    p_312623_,
                                    p_310151_,
                                    p_312438_,
                                    p_310722_.getBuffer(RenderType.debugLineStrip(2.0)),
                                    vec3,
                                    4.0F,
                                    f_303283_
                                );
                                m_307508_(
                                    p_311387_.last().pose(),
                                    p_312623_,
                                    p_310151_,
                                    p_312438_,
                                    p_310722_.getBuffer(RenderType.debugLineStrip(2.0)),
                                    vec3,
                                    8.0F,
                                    f_303307_
                                );
                                m_307508_(
                                    p_311387_.last().pose(),
                                    p_312623_,
                                    p_310151_,
                                    p_312438_,
                                    p_310722_.getBuffer(RenderType.debugLineStrip(2.0)),
                                    vec3,
                                    20.0F,
                                    f_303863_
                                );
                            }
                        );
                    optional.map(BreezeDebugPayload.BreezeInfo::f_302733_)
                        .ifPresent(
                            p_325534_ -> {
                                m_304788_(p_311387_, p_310722_, p_312623_, p_310151_, p_312438_, p_325541_.position(), p_325534_.getCenter(), f_302700_);
                                DebugRenderer.renderFilledBox(
                                    p_311387_,
                                    p_310722_,
                                    AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(p_325534_)).move(-p_312623_, -p_310151_, -p_312438_),
                                    1.0F,
                                    0.0F,
                                    0.0F,
                                    1.0F
                                );
                            }
                        );
                }
            );
    }

    private static void m_304788_(
        PoseStack p_310860_, MultiBufferSource p_311050_, double p_312740_, double p_310856_, double p_311669_, Vec3 p_309935_, Vec3 p_311298_, int p_312664_
    ) {
        VertexConsumer vertexconsumer = p_311050_.getBuffer(RenderType.debugLineStrip(2.0));
        vertexconsumer.m_320578_(
                p_310860_.last(), (float)(p_309935_.x - p_312740_), (float)(p_309935_.y - p_310856_), (float)(p_309935_.z - p_311669_)
            )
            .color(p_312664_)
            .endVertex();
        vertexconsumer.m_320578_(
                p_310860_.last(), (float)(p_311298_.x - p_312740_), (float)(p_311298_.y - p_310856_), (float)(p_311298_.z - p_311669_)
            )
            .color(p_312664_)
            .endVertex();
    }

    private static void m_307508_(
        Matrix4f p_309536_, double p_312264_, double p_310099_, double p_311317_, VertexConsumer p_310217_, Vec3 p_311990_, float p_311488_, int p_309735_
    ) {
        for (int i = 0; i < 20; i++) {
            m_305544_(i, p_309536_, p_312264_, p_310099_, p_311317_, p_310217_, p_311990_, p_311488_, p_309735_);
        }

        m_305544_(0, p_309536_, p_312264_, p_310099_, p_311317_, p_310217_, p_311990_, p_311488_, p_309735_);
    }

    private static void m_305544_(
        int p_313136_,
        Matrix4f p_311552_,
        double p_312433_,
        double p_309912_,
        double p_312340_,
        VertexConsumer p_311728_,
        Vec3 p_312252_,
        float p_311583_,
        int p_312406_
    ) {
        float f = (float)p_313136_ * (float) (Math.PI / 10);
        Vec3 vec3 = p_312252_.add((double)p_311583_ * Math.cos((double)f), 0.0, (double)p_311583_ * Math.sin((double)f));
        p_311728_.vertex(p_311552_, (float)(vec3.x - p_312433_), (float)(vec3.y - p_309912_), (float)(vec3.z - p_312340_))
            .color(p_312406_)
            .endVertex();
    }

    public void m_307053_() {
        this.f_303736_.clear();
    }

    public void m_306647_(BreezeDebugPayload.BreezeInfo p_313013_) {
        this.f_303736_.put(p_313013_.f_302477_(), p_313013_);
    }
}