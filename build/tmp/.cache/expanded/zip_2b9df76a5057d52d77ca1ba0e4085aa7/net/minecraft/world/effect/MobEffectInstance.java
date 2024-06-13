package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class MobEffectInstance implements Comparable<MobEffectInstance> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int INFINITE_DURATION = -1;
    public static final int f_316862_ = 0;
    public static final int f_316220_ = 255;
    public static final Codec<MobEffectInstance> f_316026_ = RecordCodecBuilder.create(
        p_326754_ -> p_326754_.group(
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("id").forGetter(MobEffectInstance::getEffect),
                    MobEffectInstance.Details.f_316395_.forGetter(MobEffectInstance::m_324991_)
                )
                .apply(p_326754_, MobEffectInstance::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectInstance> f_315755_ = StreamCodec.m_320349_(
        ByteBufCodecs.m_322636_(Registries.MOB_EFFECT),
        MobEffectInstance::getEffect,
        MobEffectInstance.Details.f_315344_,
        MobEffectInstance::m_324991_,
        MobEffectInstance::new
    );
    private final Holder<MobEffect> effect;
    private int duration;
    private int amplifier;
    private boolean ambient;
    private boolean visible;
    private boolean showIcon;
    @Nullable
    private MobEffectInstance hiddenEffect;
    private final MobEffectInstance.BlendState f_317081_ = new MobEffectInstance.BlendState();

    public MobEffectInstance(Holder<MobEffect> p_333937_) {
        this(p_333937_, 0, 0);
    }

    public MobEffectInstance(Holder<MobEffect> p_332556_, int pDuration) {
        this(p_332556_, pDuration, 0);
    }

    public MobEffectInstance(Holder<MobEffect> p_334453_, int p_328066_, int p_330997_) {
        this(p_334453_, p_328066_, p_330997_, false, true);
    }

    public MobEffectInstance(Holder<MobEffect> p_327781_, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible) {
        this(p_327781_, pDuration, pAmplifier, pAmbient, pVisible, pVisible);
    }

    public MobEffectInstance(Holder<MobEffect> p_333122_, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon) {
        this(p_333122_, pDuration, pAmplifier, pAmbient, pVisible, pShowIcon, null);
    }

    public MobEffectInstance(
        Holder<MobEffect> p_334558_, int pDuration, int pAmplifier, boolean p_332448_, boolean p_327855_, boolean p_334281_, @Nullable MobEffectInstance p_332569_
    ) {
        this.effect = p_334558_;
        this.duration = pDuration;
        this.amplifier = Mth.clamp(pAmplifier, 0, 255);
        this.ambient = p_332448_;
        this.visible = p_327855_;
        this.showIcon = p_334281_;
        this.hiddenEffect = p_332569_;
    }

    public MobEffectInstance(MobEffectInstance pOther) {
        this.effect = pOther.effect;
        this.setDetailsFrom(pOther);
    }

    private MobEffectInstance(Holder<MobEffect> p_330051_, MobEffectInstance.Details p_332322_) {
        this(
            p_330051_,
            p_332322_.f_316574_(),
            p_332322_.f_316534_(),
            p_332322_.f_314727_(),
            p_332322_.f_314365_(),
            p_332322_.f_314718_(),
            p_332322_.f_315812_().map(p_326756_ -> new MobEffectInstance(p_330051_, p_326756_)).orElse(null)
        );
    }

    private MobEffectInstance.Details m_324991_() {
        return new MobEffectInstance.Details(
            this.getAmplifier(),
            this.getDuration(),
            this.isAmbient(),
            this.isVisible(),
            this.showIcon(),
            Optional.ofNullable(this.hiddenEffect).map(MobEffectInstance::m_324991_)
        );
    }

    public float m_318631_(LivingEntity p_333473_, float p_327866_) {
        return this.f_317081_.m_322860_(p_333473_, p_327866_);
    }

    public ParticleOptions m_319016_() {
        return this.effect.value().m_321363_(this);
    }

    void setDetailsFrom(MobEffectInstance pEffectInstance) {
        this.duration = pEffectInstance.duration;
        this.amplifier = pEffectInstance.amplifier;
        this.ambient = pEffectInstance.ambient;
        this.visible = pEffectInstance.visible;
        this.showIcon = pEffectInstance.showIcon;
    }

    public boolean update(MobEffectInstance pOther) {
        if (!this.effect.equals(pOther.effect)) {
            LOGGER.warn("This method should only be called for matching effects!");
        }

        boolean flag = false;
        if (pOther.amplifier > this.amplifier) {
            if (pOther.isShorterDurationThan(this)) {
                MobEffectInstance mobeffectinstance = this.hiddenEffect;
                this.hiddenEffect = new MobEffectInstance(this);
                this.hiddenEffect.hiddenEffect = mobeffectinstance;
            }

            this.amplifier = pOther.amplifier;
            this.duration = pOther.duration;
            flag = true;
        } else if (this.isShorterDurationThan(pOther)) {
            if (pOther.amplifier == this.amplifier) {
                this.duration = pOther.duration;
                flag = true;
            } else if (this.hiddenEffect == null) {
                this.hiddenEffect = new MobEffectInstance(pOther);
            } else {
                this.hiddenEffect.update(pOther);
            }
        }

        if (!pOther.ambient && this.ambient || flag) {
            this.ambient = pOther.ambient;
            flag = true;
        }

        if (pOther.visible != this.visible) {
            this.visible = pOther.visible;
            flag = true;
        }

        if (pOther.showIcon != this.showIcon) {
            this.showIcon = pOther.showIcon;
            flag = true;
        }

        return flag;
    }

    private boolean isShorterDurationThan(MobEffectInstance pOther) {
        return !this.isInfiniteDuration() && (this.duration < pOther.duration || pOther.isInfiniteDuration());
    }

    public boolean isInfiniteDuration() {
        return this.duration == -1;
    }

    public boolean endsWithin(int pDuration) {
        return !this.isInfiniteDuration() && this.duration <= pDuration;
    }

    public int mapDuration(Int2IntFunction pMapper) {
        return !this.isInfiniteDuration() && this.duration != 0 ? pMapper.applyAsInt(this.duration) : this.duration;
    }

    public Holder<MobEffect> getEffect() {
        return this.effect;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public boolean isAmbient() {
        return this.ambient;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean showIcon() {
        return this.showIcon;
    }

    public boolean tick(LivingEntity pEntity, Runnable pOnExpirationRunnable) {
        if (this.hasRemainingDuration()) {
            int i = this.isInfiniteDuration() ? pEntity.tickCount : this.duration;
            if (this.effect.value().shouldApplyEffectTickThisTick(i, this.amplifier) && !this.effect.value().m_294194_(pEntity, this.amplifier)) {
                pEntity.removeEffect(this.effect);
            }

            this.tickDownDuration();
            if (this.duration == 0 && this.hiddenEffect != null) {
                this.setDetailsFrom(this.hiddenEffect);
                this.hiddenEffect = this.hiddenEffect.hiddenEffect;
                pOnExpirationRunnable.run();
            }
        }

        this.f_317081_.m_319519_(this);
        return this.hasRemainingDuration();
    }

    private boolean hasRemainingDuration() {
        return this.isInfiniteDuration() || this.duration > 0;
    }

    private int tickDownDuration() {
        if (this.hiddenEffect != null) {
            this.hiddenEffect.tickDownDuration();
        }

        return this.duration = this.mapDuration(p_267916_ -> p_267916_ - 1);
    }

    public void onEffectStarted(LivingEntity pEntity) {
        this.effect.value().onEffectStarted(pEntity, this.amplifier);
    }

    public void m_325044_(LivingEntity p_329318_, Entity.RemovalReason p_333232_) {
        this.effect.value().m_319157_(p_329318_, this.amplifier, p_333232_);
    }

    public void m_321810_(LivingEntity p_327684_, DamageSource p_328403_, float p_331463_) {
        this.effect.value().applyEffectTick(p_327684_, this.amplifier, p_328403_, p_331463_);
    }

    public String getDescriptionId() {
        return this.effect.value().getDescriptionId();
    }

    @Override
    public String toString() {
        String s;
        if (this.amplifier > 0) {
            s = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.describeDuration();
        } else {
            s = this.getDescriptionId() + ", Duration: " + this.describeDuration();
        }

        if (!this.visible) {
            s = s + ", Particles: false";
        }

        if (!this.showIcon) {
            s = s + ", Show Icon: false";
        }

        return s;
    }

    private String describeDuration() {
        return this.isInfiniteDuration() ? "infinite" : Integer.toString(this.duration);
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            return !(pOther instanceof MobEffectInstance mobeffectinstance)
                ? false
                : this.duration == mobeffectinstance.duration
                    && this.amplifier == mobeffectinstance.amplifier
                    && this.ambient == mobeffectinstance.ambient
                    && this.effect.equals(mobeffectinstance.effect);
        }
    }

    @Override
    public int hashCode() {
        int i = this.effect.hashCode();
        i = 31 * i + this.duration;
        i = 31 * i + this.amplifier;
        return 31 * i + (this.ambient ? 1 : 0);
    }

    public Tag save() {
        return f_316026_.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    @Nullable
    public static MobEffectInstance load(CompoundTag pNbt) {
        return f_316026_.parse(NbtOps.INSTANCE, pNbt).resultOrPartial(LOGGER::error).orElse(null);
    }

    public int compareTo(MobEffectInstance pOther) {
        int i = 32147;
        return (this.getDuration() <= 32147 || pOther.getDuration() <= 32147) && (!this.isAmbient() || !pOther.isAmbient())
            ? ComparisonChain.start()
                .compareFalseFirst(this.isAmbient(), pOther.isAmbient())
                .compareFalseFirst(this.isInfiniteDuration(), pOther.isInfiniteDuration())
                .compare(this.getDuration(), pOther.getDuration())
                .compare(this.getEffect().value().getColor(), pOther.getEffect().value().getColor())
                .result()
            : ComparisonChain.start()
                .compare(this.isAmbient(), pOther.isAmbient())
                .compare(this.getEffect().value().getColor(), pOther.getEffect().value().getColor())
                .result();
    }

    public void m_322321_(LivingEntity p_334348_) {
        this.effect.value().m_325074_(p_334348_, this.amplifier);
    }

    public boolean m_323663_(Holder<MobEffect> p_329529_) {
        return this.effect.equals(p_329529_);
    }

    public void m_324193_(MobEffectInstance p_335404_) {
        this.f_317081_.m_324074_(p_335404_.f_317081_);
    }

    public void m_320166_() {
        this.f_317081_.m_318781_(this);
    }

    static class BlendState {
        private float f_315287_;
        private float f_315595_;

        public void m_318781_(MobEffectInstance p_333918_) {
            this.f_315287_ = m_323207_(p_333918_);
            this.f_315595_ = this.f_315287_;
        }

        public void m_324074_(MobEffectInstance.BlendState p_327821_) {
            this.f_315287_ = p_327821_.f_315287_;
            this.f_315595_ = p_327821_.f_315595_;
        }

        public void m_319519_(MobEffectInstance p_330345_) {
            this.f_315595_ = this.f_315287_;
            int i = m_324372_(p_330345_);
            if (i == 0) {
                this.f_315287_ = 1.0F;
            } else {
                float f = m_323207_(p_330345_);
                if (this.f_315287_ != f) {
                    float f1 = 1.0F / (float)i;
                    this.f_315287_ = this.f_315287_ + Mth.clamp(f - this.f_315287_, -f1, f1);
                }
            }
        }

        private static float m_323207_(MobEffectInstance p_334116_) {
            boolean flag = !p_334116_.endsWithin(m_324372_(p_334116_));
            return flag ? 1.0F : 0.0F;
        }

        private static int m_324372_(MobEffectInstance p_335826_) {
            return p_335826_.getEffect().value().m_323916_();
        }

        public float m_322860_(LivingEntity p_333208_, float p_330792_) {
            if (p_333208_.isRemoved()) {
                this.f_315595_ = this.f_315287_;
            }

            return Mth.lerp(p_330792_, this.f_315595_, this.f_315287_);
        }
    }

    static record Details(int f_316534_, int f_316574_, boolean f_314727_, boolean f_314365_, boolean f_314718_, Optional<MobEffectInstance.Details> f_315812_) {
        public static final MapCodec<MobEffectInstance.Details> f_316395_ = MapCodec.recursive(
            "MobEffectInstance.Details",
            p_332855_ -> RecordCodecBuilder.mapCodec(
                    p_327980_ -> p_327980_.group(
                                ExtraCodecs.f_316863_.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance.Details::f_316534_),
                                Codec.INT.optionalFieldOf("duration", Integer.valueOf(0)).forGetter(MobEffectInstance.Details::f_316574_),
                                Codec.BOOL.optionalFieldOf("ambient", Boolean.valueOf(false)).forGetter(MobEffectInstance.Details::f_314727_),
                                Codec.BOOL.optionalFieldOf("show_particles", Boolean.valueOf(true)).forGetter(MobEffectInstance.Details::f_314365_),
                                Codec.BOOL.optionalFieldOf("show_icon").forGetter(p_330483_ -> Optional.of(p_330483_.f_314718_())),
                                p_332855_.optionalFieldOf("hidden_effect").forGetter(MobEffectInstance.Details::f_315812_)
                            )
                            .apply(p_327980_, MobEffectInstance.Details::m_323128_)
                )
        );
        public static final StreamCodec<ByteBuf, MobEffectInstance.Details> f_315344_ = StreamCodec.m_320869_(
            p_333279_ -> StreamCodec.m_322230_(
                    ByteBufCodecs.f_316730_,
                    MobEffectInstance.Details::f_316534_,
                    ByteBufCodecs.f_316730_,
                    MobEffectInstance.Details::f_316574_,
                    ByteBufCodecs.f_315514_,
                    MobEffectInstance.Details::f_314727_,
                    ByteBufCodecs.f_315514_,
                    MobEffectInstance.Details::f_314365_,
                    ByteBufCodecs.f_315514_,
                    MobEffectInstance.Details::f_314718_,
                    p_333279_.m_321801_(ByteBufCodecs::m_319027_),
                    MobEffectInstance.Details::f_315812_,
                    MobEffectInstance.Details::new
                )
        );

        private static MobEffectInstance.Details m_323128_(
            int p_334251_, int p_332882_, boolean p_330487_, boolean p_334607_, Optional<Boolean> p_329280_, Optional<MobEffectInstance.Details> p_330477_
        ) {
            return new MobEffectInstance.Details(p_334251_, p_332882_, p_330487_, p_334607_, p_329280_.orElse(p_334607_), p_330477_);
        }
    }
}