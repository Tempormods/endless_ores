package net.minecraft.world.item.alchemy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record PotionContents(Optional<Holder<Potion>> f_317059_, Optional<Integer> f_314927_, List<MobEffectInstance> f_315369_) {
    public static final PotionContents f_313984_ = new PotionContents(Optional.empty(), Optional.empty(), List.of());
    private static final Component f_315730_ = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
    private static final int f_315229_ = -524040;
    private static final int f_315879_ = -13083194;
    private static final Codec<PotionContents> f_316625_ = RecordCodecBuilder.create(
        p_330959_ -> p_330959_.group(
                    BuiltInRegistries.POTION.holderByNameCodec().optionalFieldOf("potion").forGetter(PotionContents::f_317059_),
                    Codec.INT.optionalFieldOf("custom_color").forGetter(PotionContents::f_314927_),
                    MobEffectInstance.f_316026_.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(PotionContents::m_321614_)
                )
                .apply(p_330959_, PotionContents::new)
    );
    public static final Codec<PotionContents> f_315880_ = Codec.withAlternative(f_316625_, BuiltInRegistries.POTION.holderByNameCodec(), PotionContents::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, PotionContents> f_315823_ = StreamCodec.m_321516_(
        ByteBufCodecs.m_322636_(Registries.POTION).m_321801_(ByteBufCodecs::m_319027_),
        PotionContents::f_317059_,
        ByteBufCodecs.f_316612_.m_321801_(ByteBufCodecs::m_319027_),
        PotionContents::f_314927_,
        MobEffectInstance.f_315755_.m_321801_(ByteBufCodecs.m_324765_()),
        PotionContents::m_321614_,
        PotionContents::new
    );

    public PotionContents(Holder<Potion> p_335062_) {
        this(Optional.of(p_335062_), Optional.empty(), List.of());
    }

    public static ItemStack m_324840_(Item p_328254_, Holder<Potion> p_334269_) {
        ItemStack itemstack = new ItemStack(p_328254_);
        itemstack.m_322496_(DataComponents.f_314188_, new PotionContents(p_334269_));
        return itemstack;
    }

    public boolean m_323649_(Holder<Potion> p_329141_) {
        return this.f_317059_.isPresent() && this.f_317059_.get().m_318604_(p_329141_) && this.f_315369_.isEmpty();
    }

    public Iterable<MobEffectInstance> m_319638_() {
        if (this.f_317059_.isEmpty()) {
            return this.f_315369_;
        } else {
            return (Iterable<MobEffectInstance>)(this.f_315369_.isEmpty()
                ? this.f_317059_.get().value().getEffects()
                : Iterables.concat(this.f_317059_.get().value().getEffects(), this.f_315369_));
        }
    }

    public void m_325077_(Consumer<MobEffectInstance> p_335805_) {
        if (this.f_317059_.isPresent()) {
            for (MobEffectInstance mobeffectinstance : this.f_317059_.get().value().getEffects()) {
                p_335805_.accept(new MobEffectInstance(mobeffectinstance));
            }
        }

        for (MobEffectInstance mobeffectinstance1 : this.f_315369_) {
            p_335805_.accept(new MobEffectInstance(mobeffectinstance1));
        }
    }

    public PotionContents m_324670_(Holder<Potion> p_333654_) {
        return new PotionContents(Optional.of(p_333654_), this.f_314927_, this.f_315369_);
    }

    public PotionContents m_324984_(MobEffectInstance p_328742_) {
        return new PotionContents(this.f_317059_, this.f_314927_, Util.m_324319_(this.f_315369_, p_328742_));
    }

    public int m_318943_() {
        return this.f_314927_.isPresent() ? this.f_314927_.get() : m_324402_(this.m_319638_());
    }

    public static int m_318851_(Holder<Potion> p_332484_) {
        return m_324402_(p_332484_.value().getEffects());
    }

    public static int m_324402_(Iterable<MobEffectInstance> p_328528_) {
        return m_320220_(p_328528_).orElse(-13083194);
    }

    public static OptionalInt m_320220_(Iterable<MobEffectInstance> p_331345_) {
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;

        for (MobEffectInstance mobeffectinstance : p_331345_) {
            if (mobeffectinstance.isVisible()) {
                int i1 = mobeffectinstance.getEffect().value().getColor();
                int j1 = mobeffectinstance.getAmplifier() + 1;
                i += j1 * FastColor.ARGB32.red(i1);
                j += j1 * FastColor.ARGB32.green(i1);
                k += j1 * FastColor.ARGB32.blue(i1);
                l += j1;
            }
        }

        return l == 0 ? OptionalInt.empty() : OptionalInt.of(FastColor.ARGB32.m_322882_(i / l, j / l, k / l));
    }

    public boolean m_323528_() {
        return !this.f_315369_.isEmpty() ? true : this.f_317059_.isPresent() && !this.f_317059_.get().value().getEffects().isEmpty();
    }

    public List<MobEffectInstance> m_321614_() {
        return Lists.transform(this.f_315369_, MobEffectInstance::new);
    }

    public void m_324933_(Consumer<Component> p_334042_, float p_336314_, float p_328696_) {
        m_319937_(this.m_319638_(), p_334042_, p_336314_, p_328696_);
    }

    public static void m_319937_(Iterable<MobEffectInstance> p_328255_, Consumer<Component> p_336197_, float p_333725_, float p_333963_) {
        List<Pair<Holder<Attribute>, AttributeModifier>> list = Lists.newArrayList();
        boolean flag = true;

        for (MobEffectInstance mobeffectinstance : p_328255_) {
            flag = false;
            MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
            Holder<MobEffect> holder = mobeffectinstance.getEffect();
            holder.value().m_320407_(mobeffectinstance.getAmplifier(), (p_329075_, p_331827_) -> list.add(new Pair<>(p_329075_, p_331827_)));
            if (mobeffectinstance.getAmplifier() > 0) {
                mutablecomponent = Component.translatable(
                    "potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier())
                );
            }

            if (!mobeffectinstance.endsWithin(20)) {
                mutablecomponent = Component.translatable(
                    "potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, p_333725_, p_333963_)
                );
            }

            p_336197_.accept(mutablecomponent.withStyle(holder.value().getCategory().getTooltipFormatting()));
        }

        if (flag) {
            p_336197_.accept(f_315730_);
        }

        if (!list.isEmpty()) {
            p_336197_.accept(CommonComponents.EMPTY);
            p_336197_.accept(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            for (Pair<Holder<Attribute>, AttributeModifier> pair : list) {
                AttributeModifier attributemodifier = pair.getSecond();
                double d1 = attributemodifier.amount();
                double d0;
                if (attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    && attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    d0 = attributemodifier.amount();
                } else {
                    d0 = attributemodifier.amount() * 100.0;
                }

                if (d1 > 0.0) {
                    p_336197_.accept(
                        Component.translatable(
                                "attribute.modifier.plus." + attributemodifier.operation().m_324661_(),
                                ItemAttributeModifiers.f_315079_.format(d0),
                                Component.translatable(pair.getFirst().value().getDescriptionId())
                            )
                            .withStyle(ChatFormatting.BLUE)
                    );
                } else if (d1 < 0.0) {
                    d0 *= -1.0;
                    p_336197_.accept(
                        Component.translatable(
                                "attribute.modifier.take." + attributemodifier.operation().m_324661_(),
                                ItemAttributeModifiers.f_315079_.format(d0),
                                Component.translatable(pair.getFirst().value().getDescriptionId())
                            )
                            .withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }
}