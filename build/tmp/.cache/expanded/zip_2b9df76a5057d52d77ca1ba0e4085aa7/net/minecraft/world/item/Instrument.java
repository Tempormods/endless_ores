package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;

public record Instrument(Holder<SoundEvent> soundEvent, int useDuration, float range) {
    public static final Codec<Instrument> f_314982_ = RecordCodecBuilder.create(
        p_220089_ -> p_220089_.group(
                    SoundEvent.CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent),
                    ExtraCodecs.POSITIVE_INT.fieldOf("use_duration").forGetter(Instrument::useDuration),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range)
                )
                .apply(p_220089_, Instrument::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Instrument> f_316019_ = StreamCodec.m_321516_(
        SoundEvent.f_314687_,
        Instrument::soundEvent,
        ByteBufCodecs.f_316730_,
        Instrument::useDuration,
        ByteBufCodecs.f_314734_,
        Instrument::range,
        Instrument::new
    );
    public static final Codec<Holder<Instrument>> CODEC = RegistryFileCodec.create(Registries.INSTRUMENT, f_314982_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Instrument>> f_315407_ = ByteBufCodecs.m_321333_(Registries.INSTRUMENT, f_316019_);
}