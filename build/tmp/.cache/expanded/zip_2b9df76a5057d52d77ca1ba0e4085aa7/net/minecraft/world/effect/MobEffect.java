package net.minecraft.world.effect;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class MobEffect implements FeatureElement, net.minecraftforge.common.extensions.IForgeMobEffect {
    private static final int f_314568_ = Mth.floor(38.25F);
    private final Map<Holder<Attribute>, MobEffect.AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();
    private final MobEffectCategory category;
    private final int color;
    private final Function<MobEffectInstance, ParticleOptions> f_314870_;
    @Nullable
    private String descriptionId;
    private int f_315239_;
    private Optional<SoundEvent> f_316316_ = Optional.empty();
    private FeatureFlagSet f_316645_ = FeatureFlags.VANILLA_SET;

    protected MobEffect(MobEffectCategory pCategory, int pColor) {
        this.category = pCategory;
        this.color = pColor;
        this.f_314870_ = p_326747_ -> {
            int i = p_326747_.isAmbient() ? f_314568_ : 255;
            return ColorParticleOption.m_318840_(ParticleTypes.ENTITY_EFFECT, FastColor.ARGB32.m_320289_(i, pColor));
        };
        initClient();
    }

    protected MobEffect(MobEffectCategory p_335432_, int p_334901_, ParticleOptions p_331136_) {
        this.category = p_335432_;
        this.color = p_334901_;
        this.f_314870_ = p_326745_ -> p_331136_;
        initClient();
    }

    public int m_323916_() {
        return this.f_315239_;
    }

    public boolean m_294194_(LivingEntity p_333541_, int p_333570_) {
        return true;
    }

    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity pLivingEntity, int pAmplifier, double pHealth) {
        this.m_294194_(pLivingEntity, pAmplifier);
    }

    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return false;
    }

    public void onEffectStarted(LivingEntity pLivingEntity, int pAmplifier) {
    }

    public void m_325074_(LivingEntity p_335100_, int p_336309_) {
        this.f_316316_
            .ifPresent(
                p_326753_ -> p_335100_.level()
                        .playSound(null, p_335100_.getX(), p_335100_.getY(), p_335100_.getZ(), p_326753_, p_335100_.getSoundSource(), 1.0F, 1.0F)
            );
    }

    public void m_319157_(LivingEntity p_335815_, int p_328980_, Entity.RemovalReason p_328413_) {
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier, DamageSource p_334111_, float p_330556_) {
    }

    public boolean isInstantenous() {
        return false;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("effect", BuiltInRegistries.MOB_EFFECT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getDisplayName() {
        return Component.translatable(this.getDescriptionId());
    }

    public MobEffectCategory getCategory() {
        return this.category;
    }

    public int getColor() {
        return this.color;
    }

    public MobEffect addAttributeModifier(Holder<Attribute> p_332101_, String pUuid, double pAmount, AttributeModifier.Operation pOperation) {
        this.attributeModifiers.put(p_332101_, new MobEffect.AttributeTemplate(UUID.fromString(pUuid), pAmount, pOperation));
        return this;
    }

    public MobEffect m_321800_(int p_328727_) {
        this.f_315239_ = p_328727_;
        return this;
    }

    public void m_320407_(int p_334564_, BiConsumer<Holder<Attribute>, AttributeModifier> p_333602_) {
        this.attributeModifiers.forEach((p_326750_, p_326751_) -> p_333602_.accept((Holder<Attribute>)p_326750_, p_326751_.m_324395_(this.getDescriptionId(), p_334564_)));
    }

    public void removeAttributeModifiers(AttributeMap pAttributeMap) {
        for (Entry<Holder<Attribute>, MobEffect.AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeinstance = pAttributeMap.getInstance(entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier(entry.getValue().f_314788_());
            }
        }
    }

    public void addAttributeModifiers(AttributeMap pAttributeMap, int pAmplifier) {
        for (Entry<Holder<Attribute>, MobEffect.AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeinstance = pAttributeMap.getInstance(entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier(entry.getValue().f_314788_());
                attributeinstance.addPermanentModifier(entry.getValue().m_324395_(this.getDescriptionId(), pAmplifier));
            }
        }
    }

    public boolean isBeneficial() {
        return this.category == MobEffectCategory.BENEFICIAL;
    }

    public ParticleOptions m_321363_(MobEffectInstance p_332465_) {
        return this.f_314870_.apply(p_332465_);
    }

    public MobEffect m_320304_(SoundEvent p_329951_) {
        this.f_316316_ = Optional.of(p_329951_);
        return this;
    }

    public MobEffect m_320974_(FeatureFlag... p_329270_) {
        this.f_316645_ = FeatureFlags.REGISTRY.subset(p_329270_);
        return this;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.f_316645_;
    }

    static record AttributeTemplate(UUID f_314788_, double f_316035_, AttributeModifier.Operation f_315228_) {
        public AttributeModifier m_324395_(String p_331481_, int p_332230_) {
            return new AttributeModifier(this.f_314788_, p_331481_ + " " + p_332230_, this.f_316035_ * (double)(p_332230_ + 1), this.f_315228_);
        }
    }

    // FORGE START
    private Object effectRenderer;

    /*
       DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
       Call RenderProperties.getEffectRenderer instead
     */
    public Object getEffectRendererInternal() {
        return effectRenderer;
    }

    private void initClient() {
        // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
            initializeClient(properties -> {
                this.effectRenderer = properties;
            });
        }
    }

    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientMobEffectExtensions> consumer) {
    }
    // END FORGE
}
