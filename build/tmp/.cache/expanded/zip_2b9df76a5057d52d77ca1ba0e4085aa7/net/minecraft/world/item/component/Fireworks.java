package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record Fireworks(int f_317050_, List<FireworkExplosion> f_314926_) implements TooltipProvider {
    public static final int f_317122_ = 256;
    public static final Codec<Fireworks> f_316251_ = RecordCodecBuilder.create(
        p_329042_ -> p_329042_.group(
                    ExtraCodecs.f_316863_.optionalFieldOf("flight_duration", 0).forGetter(Fireworks::f_317050_),
                    FireworkExplosion.f_315661_.sizeLimitedListOf(256).optionalFieldOf("explosions", List.of()).forGetter(Fireworks::f_314926_)
                )
                .apply(p_329042_, Fireworks::new)
    );
    public static final StreamCodec<ByteBuf, Fireworks> f_314560_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_316730_,
        Fireworks::f_317050_,
        FireworkExplosion.f_316358_.m_321801_(ByteBufCodecs.m_319259_(256)),
        Fireworks::f_314926_,
        Fireworks::new
    );

    public Fireworks(int f_317050_, List<FireworkExplosion> f_314926_) {
        if (f_314926_.size() > 256) {
            throw new IllegalArgumentException("Got " + f_314926_.size() + " explosions, but maximum is 256");
        } else {
            this.f_317050_ = f_317050_;
            this.f_314926_ = f_314926_;
        }
    }

    @Override
    public void m_319025_(Item.TooltipContext p_328344_, Consumer<Component> p_335967_, TooltipFlag p_328360_) {
        if (this.f_317050_ > 0) {
            p_335967_.accept(
                Component.translatable("item.minecraft.firework_rocket.flight")
                    .append(CommonComponents.SPACE)
                    .append(String.valueOf(this.f_317050_))
                    .withStyle(ChatFormatting.GRAY)
            );
        }

        for (FireworkExplosion fireworkexplosion : this.f_314926_) {
            fireworkexplosion.m_321829_(p_335967_);
            fireworkexplosion.m_323234_(p_329354_ -> p_335967_.accept(Component.literal("  ").append(p_329354_)));
        }
    }
}