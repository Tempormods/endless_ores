package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record DyedItemColor(int f_314370_, boolean f_314022_) implements TooltipProvider {
    private static final Codec<DyedItemColor> f_315345_ = RecordCodecBuilder.create(
        p_332588_ -> p_332588_.group(
                    Codec.INT.fieldOf("rgb").forGetter(DyedItemColor::f_314370_),
                    Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(DyedItemColor::f_314022_)
                )
                .apply(p_332588_, DyedItemColor::new)
    );
    public static final Codec<DyedItemColor> f_316765_ = Codec.withAlternative(f_315345_, Codec.INT, p_330172_ -> new DyedItemColor(p_330172_, true));
    public static final StreamCodec<ByteBuf, DyedItemColor> f_314233_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_316612_, DyedItemColor::f_314370_, ByteBufCodecs.f_315514_, DyedItemColor::f_314022_, DyedItemColor::new
    );
    public static final int f_315887_ = -6265536;

    public static int m_322889_(ItemStack p_327803_, int p_334743_) {
        DyedItemColor dyeditemcolor = p_327803_.m_323252_(DataComponents.f_315011_);
        return dyeditemcolor != null ? FastColor.ARGB32.m_321570_(dyeditemcolor.f_314370_()) : p_334743_;
    }

    public static ItemStack m_323436_(ItemStack p_333863_, List<DyeItem> p_329585_) {
        if (!p_333863_.is(ItemTags.f_314020_)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = p_333863_.copyWithCount(1);
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int i1 = 0;
            DyedItemColor dyeditemcolor = itemstack.m_323252_(DataComponents.f_315011_);
            if (dyeditemcolor != null) {
                int j1 = FastColor.ARGB32.red(dyeditemcolor.f_314370_());
                int k1 = FastColor.ARGB32.green(dyeditemcolor.f_314370_());
                int l1 = FastColor.ARGB32.blue(dyeditemcolor.f_314370_());
                l += Math.max(j1, Math.max(k1, l1));
                i += j1;
                j += k1;
                k += l1;
                i1++;
            }

            for (DyeItem dyeitem : p_329585_) {
                float[] afloat = dyeitem.getDyeColor().getTextureDiffuseColors();
                int i2 = (int)(afloat[0] * 255.0F);
                int j2 = (int)(afloat[1] * 255.0F);
                int k2 = (int)(afloat[2] * 255.0F);
                l += Math.max(i2, Math.max(j2, k2));
                i += i2;
                j += j2;
                k += k2;
                i1++;
            }

            int l2 = i / i1;
            int i3 = j / i1;
            int j3 = k / i1;
            float f = (float)l / (float)i1;
            float f1 = (float)Math.max(l2, Math.max(i3, j3));
            l2 = (int)((float)l2 * f / f1);
            i3 = (int)((float)i3 * f / f1);
            j3 = (int)((float)j3 * f / f1);
            int k3 = FastColor.ARGB32.color(0, l2, i3, j3);
            boolean flag = dyeditemcolor == null || dyeditemcolor.f_314022_();
            itemstack.m_322496_(DataComponents.f_315011_, new DyedItemColor(k3, flag));
            return itemstack;
        }
    }

    @Override
    public void m_319025_(Item.TooltipContext p_332585_, Consumer<Component> p_332053_, TooltipFlag p_329372_) {
        if (this.f_314022_) {
            if (p_329372_.isAdvanced()) {
                p_332053_.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.f_314370_)).withStyle(ChatFormatting.GRAY));
            } else {
                p_332053_.accept(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }

    public DyedItemColor m_320147_(boolean p_328256_) {
        return new DyedItemColor(this.f_314370_, p_328256_);
    }
}