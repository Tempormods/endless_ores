package net.minecraft.network.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;

public class ComponentSerialization {
    public static final Codec<Component> f_303288_ = Codec.recursive("Component", ComponentSerialization::m_305392_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Component> f_315335_ = ByteBufCodecs.m_319284_(f_303288_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> f_315970_ = f_315335_.m_321801_(ByteBufCodecs::m_319027_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Component> f_316335_ = ByteBufCodecs.m_322295_(f_303288_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> f_316844_ = f_316335_.m_321801_(ByteBufCodecs::m_319027_);
    public static final StreamCodec<ByteBuf, Component> f_314039_ = ByteBufCodecs.m_319482_(f_303288_);
    public static final Codec<Component> f_303675_ = m_324597_(Integer.MAX_VALUE);

    public static Codec<Component> m_324597_(int p_330224_) {
        final Codec<String> codec = Codec.string(0, p_330224_);
        return new Codec<Component>() {
            @Override
            public <T> DataResult<Pair<Component, T>> decode(DynamicOps<T> p_334494_, T p_334478_) {
                DynamicOps<JsonElement> dynamicops = m_322966_(p_334494_);
                return codec.decode(p_334494_, p_334478_).flatMap(p_328894_ -> {
                    try {
                        JsonElement jsonelement = JsonParser.parseString(p_328894_.getFirst());
                        return ComponentSerialization.f_303288_.parse(dynamicops, jsonelement).map(p_334008_ -> Pair.of(p_334008_, (T)p_328894_.getSecond()));
                    } catch (JsonParseException jsonparseexception) {
                        return DataResult.error(jsonparseexception::getMessage);
                    }
                });
            }

            public <T> DataResult<T> encode(Component p_330654_, DynamicOps<T> p_330879_, T p_336296_) {
                DynamicOps<JsonElement> dynamicops = m_322966_(p_330879_);
                return ComponentSerialization.f_303288_.encodeStart(dynamicops, p_330654_).flatMap(p_332275_ -> {
                    try {
                        return codec.encodeStart(p_330879_, GsonHelper.toStableString(p_332275_));
                    } catch (IllegalArgumentException illegalargumentexception) {
                        return DataResult.error(illegalargumentexception::getMessage);
                    }
                });
            }

            private static <T> DynamicOps<JsonElement> m_322966_(DynamicOps<T> p_331374_) {
                return (DynamicOps<JsonElement>)(p_331374_ instanceof RegistryOps<T> registryops ? registryops.m_322470_(JsonOps.INSTANCE) : JsonOps.INSTANCE);
            }
        };
    }

    private static MutableComponent m_304932_(List<Component> p_312708_) {
        MutableComponent mutablecomponent = p_312708_.get(0).copy();

        for (int i = 1; i < p_312708_.size(); i++) {
            mutablecomponent.append(p_312708_.get(i));
        }

        return mutablecomponent;
    }

    public static <T extends StringRepresentable, E> MapCodec<E> m_306757_(
        T[] p_312620_, Function<T, MapCodec<? extends E>> p_312447_, Function<E, T> p_309774_, String p_311665_
    ) {
        MapCodec<E> mapcodec = new ComponentSerialization.FuzzyCodec<>(
            Stream.<T>of(p_312620_).map(p_312447_).toList(), p_312251_ -> p_312447_.apply(p_309774_.apply(p_312251_))
        );
        Codec<T> codec = StringRepresentable.m_306774_((Supplier<T[]>)(() -> p_312620_));
        MapCodec<E> mapcodec1 = codec.dispatchMap(p_311665_, p_309774_, p_312447_);
        MapCodec<E> mapcodec2 = new ComponentSerialization.StrictEither<>(p_311665_, mapcodec1, mapcodec);
        return ExtraCodecs.m_307667_(mapcodec2, mapcodec1);
    }

    private static Codec<Component> m_305392_(Codec<Component> p_310353_) {
        ComponentContents.Type<?>[] type = new ComponentContents.Type[]{
            PlainTextContents.f_302384_,
            TranslatableContents.f_303657_,
            KeybindContents.f_302328_,
            ScoreContents.f_303411_,
            SelectorContents.f_302445_,
            NbtContents.f_303372_
        };
        MapCodec<ComponentContents> mapcodec = m_306757_(type, ComponentContents.Type::f_302903_, ComponentContents::m_304650_, "type");
        Codec<Component> codec = RecordCodecBuilder.create(
            p_326064_ -> p_326064_.group(
                        mapcodec.forGetter(Component::getContents),
                        ExtraCodecs.nonEmptyList(p_310353_.listOf()).optionalFieldOf("extra", List.of()).forGetter(Component::getSiblings),
                        Style.Serializer.f_303391_.forGetter(Component::getStyle)
                    )
                    .apply(p_326064_, MutableComponent::new)
        );
        return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(p_310353_.listOf())), codec)
            .xmap(
                p_312362_ -> p_312362_.map(
                        p_310114_ -> p_310114_.map(Component::literal, ComponentSerialization::m_304932_), p_310523_ -> (Component)p_310523_
                    ),
                p_312558_ -> {
                    String s = p_312558_.m_306448_();
                    return s != null ? Either.left(Either.left(s)) : Either.right(p_312558_);
                }
            );
    }

    static class FuzzyCodec<T> extends MapCodec<T> {
        private final List<MapCodec<? extends T>> f_303286_;
        private final Function<T, MapEncoder<? extends T>> f_303269_;

        public FuzzyCodec(List<MapCodec<? extends T>> p_313195_, Function<T, MapEncoder<? extends T>> p_313105_) {
            this.f_303286_ = p_313195_;
            this.f_303269_ = p_313105_;
        }

        @Override
        public <S> DataResult<T> decode(DynamicOps<S> p_311662_, MapLike<S> p_310979_) {
            for (MapDecoder<? extends T> mapdecoder : this.f_303286_) {
                DataResult<? extends T> dataresult = mapdecoder.decode(p_311662_, p_310979_);
                if (dataresult.result().isPresent()) {
                    return (DataResult<T>)dataresult;
                }
            }

            return DataResult.error(() -> "No matching codec found");
        }

        @Override
        public <S> RecordBuilder<S> encode(T p_310202_, DynamicOps<S> p_312954_, RecordBuilder<S> p_312771_) {
            MapEncoder<T> mapencoder = (MapEncoder<T>)this.f_303269_.apply(p_310202_);
            return mapencoder.encode(p_310202_, p_312954_, p_312771_);
        }

        @Override
        public <S> Stream<S> keys(DynamicOps<S> p_311118_) {
            return this.f_303286_.stream().flatMap(p_310919_ -> p_310919_.keys(p_311118_)).distinct();
        }

        @Override
        public String toString() {
            return "FuzzyCodec[" + this.f_303286_ + "]";
        }
    }

    static class StrictEither<T> extends MapCodec<T> {
        private final String f_303015_;
        private final MapCodec<T> f_303318_;
        private final MapCodec<T> f_303354_;

        public StrictEither(String p_310206_, MapCodec<T> p_312028_, MapCodec<T> p_312603_) {
            this.f_303015_ = p_310206_;
            this.f_303318_ = p_312028_;
            this.f_303354_ = p_312603_;
        }

        @Override
        public <O> DataResult<T> decode(DynamicOps<O> p_310941_, MapLike<O> p_311041_) {
            return p_311041_.get(this.f_303015_) != null ? this.f_303318_.decode(p_310941_, p_311041_) : this.f_303354_.decode(p_310941_, p_311041_);
        }

        @Override
        public <O> RecordBuilder<O> encode(T p_310960_, DynamicOps<O> p_310726_, RecordBuilder<O> p_310170_) {
            return this.f_303354_.encode(p_310960_, p_310726_, p_310170_);
        }

        @Override
        public <T1> Stream<T1> keys(DynamicOps<T1> p_310134_) {
            return Stream.concat(this.f_303318_.keys(p_310134_), this.f_303354_.keys(p_310134_)).distinct();
        }
    }
}