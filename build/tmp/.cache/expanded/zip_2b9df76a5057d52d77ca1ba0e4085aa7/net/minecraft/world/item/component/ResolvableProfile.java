package net.minecraft.world.item.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public record ResolvableProfile(Optional<String> f_316631_, Optional<UUID> f_314446_, PropertyMap f_316692_, GameProfile f_316880_) {
    private static final Codec<ResolvableProfile> f_315962_ = RecordCodecBuilder.create(
        p_333384_ -> p_333384_.group(
                    ExtraCodecs.f_316749_.optionalFieldOf("name").forGetter(ResolvableProfile::f_316631_),
                    UUIDUtil.CODEC.optionalFieldOf("id").forGetter(ResolvableProfile::f_314446_),
                    ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(ResolvableProfile::f_316692_)
                )
                .apply(p_333384_, ResolvableProfile::new)
    );
    public static final Codec<ResolvableProfile> f_315352_ = Codec.withAlternative(
        f_315962_, ExtraCodecs.f_316749_, p_329676_ -> new ResolvableProfile(Optional.of(p_329676_), Optional.empty(), new PropertyMap())
    );
    public static final StreamCodec<ByteBuf, ResolvableProfile> f_316630_ = StreamCodec.m_321516_(
        ByteBufCodecs.m_319534_(16).m_321801_(ByteBufCodecs::m_319027_),
        ResolvableProfile::f_316631_,
        UUIDUtil.f_315346_.m_321801_(ByteBufCodecs::m_319027_),
        ResolvableProfile::f_314446_,
        ByteBufCodecs.f_315576_,
        ResolvableProfile::f_316692_,
        ResolvableProfile::new
    );

    public ResolvableProfile(Optional<String> p_328556_, Optional<UUID> p_331819_, PropertyMap p_329390_) {
        this(p_328556_, p_331819_, p_329390_, m_322027_(p_328556_, p_331819_, p_329390_));
    }

    public ResolvableProfile(GameProfile p_332940_) {
        this(Optional.of(p_332940_.getName()), Optional.of(p_332940_.getId()), p_332940_.getProperties(), p_332940_);
    }

    public CompletableFuture<ResolvableProfile> m_322305_() {
        if (this.m_320408_()) {
            return CompletableFuture.completedFuture(this);
        } else {
            return this.f_314446_.isPresent() ? SkullBlockEntity.m_319014_(this.f_314446_.get()).thenApply(p_332213_ -> {
                GameProfile gameprofile = p_332213_.orElseGet(() -> new GameProfile(this.f_314446_.get(), this.f_316631_.orElse("")));
                return new ResolvableProfile(gameprofile);
            }) : SkullBlockEntity.fetchGameProfile(this.f_316631_.orElseThrow()).thenApply(p_331268_ -> {
                GameProfile gameprofile = p_331268_.orElseGet(() -> new GameProfile(Util.NIL_UUID, this.f_316631_.get()));
                return new ResolvableProfile(gameprofile);
            });
        }
    }

    private static GameProfile m_322027_(Optional<String> p_329472_, Optional<UUID> p_333643_, PropertyMap p_330035_) {
        GameProfile gameprofile = new GameProfile(p_333643_.orElse(Util.NIL_UUID), p_329472_.orElse(""));
        gameprofile.getProperties().putAll(p_330035_);
        return gameprofile;
    }

    public boolean m_320408_() {
        return !this.f_316692_.isEmpty() ? true : this.f_314446_.isPresent() == this.f_316631_.isPresent();
    }
}