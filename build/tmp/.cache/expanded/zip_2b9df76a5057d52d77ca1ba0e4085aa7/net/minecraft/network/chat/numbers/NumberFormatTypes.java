package net.minecraft.network.chat.numbers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class NumberFormatTypes {
    public static final MapCodec<NumberFormat> f_302721_ = BuiltInRegistries.f_303198_
        .byNameCodec()
        .dispatchMap(NumberFormat::m_307395_, NumberFormatType::m_305346_);
    public static final Codec<NumberFormat> f_303316_ = f_302721_.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, NumberFormat> f_314583_ = ByteBufCodecs.m_320159_(Registries.f_302869_)
        .m_321818_(NumberFormat::m_307395_, NumberFormatType::m_321453_);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<NumberFormat>> f_316603_ = f_314583_.m_321801_(ByteBufCodecs::m_319027_);

    public static NumberFormatType<?> m_305447_(Registry<NumberFormatType<?>> p_310229_) {
        Registry.register(p_310229_, "blank", BlankFormat.f_303499_);
        Registry.register(p_310229_, "styled", StyledFormat.f_302955_);
        return Registry.register(p_310229_, "fixed", FixedFormat.f_302513_);
    }
}