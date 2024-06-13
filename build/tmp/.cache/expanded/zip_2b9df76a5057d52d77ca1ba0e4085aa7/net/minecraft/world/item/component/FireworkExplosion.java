package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record FireworkExplosion(FireworkExplosion.Shape f_316547_, IntList f_316201_, IntList f_314743_, boolean f_316522_, boolean f_316285_)
    implements TooltipProvider {
    public static final FireworkExplosion f_316800_ = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
    public static final Codec<IntList> f_314508_ = Codec.INT.listOf().xmap(IntArrayList::new, ArrayList::new);
    public static final Codec<FireworkExplosion> f_315661_ = RecordCodecBuilder.create(
        p_332691_ -> p_332691_.group(
                    FireworkExplosion.Shape.f_314578_.fieldOf("shape").forGetter(FireworkExplosion::f_316547_),
                    f_314508_.optionalFieldOf("colors", IntList.of()).forGetter(FireworkExplosion::f_316201_),
                    f_314508_.optionalFieldOf("fade_colors", IntList.of()).forGetter(FireworkExplosion::f_314743_),
                    Codec.BOOL.optionalFieldOf("has_trail", Boolean.valueOf(false)).forGetter(FireworkExplosion::f_316522_),
                    Codec.BOOL.optionalFieldOf("has_twinkle", Boolean.valueOf(false)).forGetter(FireworkExplosion::f_316285_)
                )
                .apply(p_332691_, FireworkExplosion::new)
    );
    private static final StreamCodec<ByteBuf, IntList> f_316103_ = ByteBufCodecs.f_316612_
        .m_321801_(ByteBufCodecs.m_324765_())
        .m_323038_(IntArrayList::new, ArrayList::new);
    public static final StreamCodec<ByteBuf, FireworkExplosion> f_316358_ = StreamCodec.m_319894_(
        FireworkExplosion.Shape.f_315490_,
        FireworkExplosion::f_316547_,
        f_316103_,
        FireworkExplosion::f_316201_,
        f_316103_,
        FireworkExplosion::f_314743_,
        ByteBufCodecs.f_315514_,
        FireworkExplosion::f_316522_,
        ByteBufCodecs.f_315514_,
        FireworkExplosion::f_316285_,
        FireworkExplosion::new
    );
    private static final Component f_315548_ = Component.translatable("item.minecraft.firework_star.custom_color");

    @Override
    public void m_319025_(Item.TooltipContext p_328877_, Consumer<Component> p_333224_, TooltipFlag p_335960_) {
        this.m_321829_(p_333224_);
        this.m_323234_(p_333224_);
    }

    public void m_321829_(Consumer<Component> p_331419_) {
        p_331419_.accept(this.f_316547_.m_321671_().withStyle(ChatFormatting.GRAY));
    }

    public void m_323234_(Consumer<Component> p_331797_) {
        if (!this.f_316201_.isEmpty()) {
            p_331797_.accept(m_322644_(Component.empty().withStyle(ChatFormatting.GRAY), this.f_316201_));
        }

        if (!this.f_314743_.isEmpty()) {
            p_331797_.accept(
                m_322644_(
                    Component.translatable("item.minecraft.firework_star.fade_to").append(CommonComponents.SPACE).withStyle(ChatFormatting.GRAY),
                    this.f_314743_
                )
            );
        }

        if (this.f_316522_) {
            p_331797_.accept(Component.translatable("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY));
        }

        if (this.f_316285_) {
            p_331797_.accept(Component.translatable("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component m_322644_(MutableComponent p_333538_, IntList p_333652_) {
        for (int i = 0; i < p_333652_.size(); i++) {
            if (i > 0) {
                p_333538_.append(", ");
            }

            p_333538_.append(m_319213_(p_333652_.getInt(i)));
        }

        return p_333538_;
    }

    private static Component m_319213_(int p_333961_) {
        DyeColor dyecolor = DyeColor.byFireworkColor(p_333961_);
        return (Component)(dyecolor == null ? f_315548_ : Component.translatable("item.minecraft.firework_star." + dyecolor.getName()));
    }

    public FireworkExplosion m_319637_(IntList p_330299_) {
        return new FireworkExplosion(this.f_316547_, this.f_316201_, new IntArrayList(p_330299_), this.f_316522_, this.f_316285_);
    }

    public static enum Shape implements StringRepresentable {
        SMALL_BALL(0, "small_ball"),
        LARGE_BALL(1, "large_ball"),
        STAR(2, "star"),
        CREEPER(3, "creeper"),
        BURST(4, "burst");

        private static final IntFunction<FireworkExplosion.Shape> f_315493_ = ByIdMap.continuous(
            FireworkExplosion.Shape::m_323337_, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
        public static final StreamCodec<ByteBuf, FireworkExplosion.Shape> f_315490_ = ByteBufCodecs.m_321301_(f_315493_, FireworkExplosion.Shape::m_323337_);
        public static final Codec<FireworkExplosion.Shape> f_314578_ = StringRepresentable.m_306774_(FireworkExplosion.Shape::values);
        private final int f_314641_;
        private final String f_314912_;

        private Shape(final int p_330815_, final String p_329574_) {
            this.f_314641_ = p_330815_;
            this.f_314912_ = p_329574_;
        }

        public MutableComponent m_321671_() {
            return Component.translatable("item.minecraft.firework_star.shape." + this.f_314912_);
        }

        public int m_323337_() {
            return this.f_314641_;
        }

        public static FireworkExplosion.Shape m_319551_(int p_330413_) {
            return f_315493_.apply(p_330413_);
        }

        @Override
        public String getSerializedName() {
            return this.f_314912_;
        }
    }
}