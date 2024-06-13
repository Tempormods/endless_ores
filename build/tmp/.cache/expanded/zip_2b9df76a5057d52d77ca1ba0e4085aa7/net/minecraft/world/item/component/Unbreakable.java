package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record Unbreakable(boolean f_316777_) implements TooltipProvider {
    public static final Codec<Unbreakable> f_316475_ = RecordCodecBuilder.create(
        p_330402_ -> p_330402_.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(Unbreakable::f_316777_))
                .apply(p_330402_, Unbreakable::new)
    );
    public static final StreamCodec<ByteBuf, Unbreakable> f_315219_ = ByteBufCodecs.f_315514_.m_323038_(Unbreakable::new, Unbreakable::f_316777_);
    private static final Component f_317030_ = Component.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE);

    @Override
    public void m_319025_(Item.TooltipContext p_331499_, Consumer<Component> p_335134_, TooltipFlag p_331046_) {
        if (this.f_316777_) {
            p_335134_.accept(f_317030_);
        }
    }

    public Unbreakable m_320618_(boolean p_334168_) {
        return new Unbreakable(p_334168_);
    }
}