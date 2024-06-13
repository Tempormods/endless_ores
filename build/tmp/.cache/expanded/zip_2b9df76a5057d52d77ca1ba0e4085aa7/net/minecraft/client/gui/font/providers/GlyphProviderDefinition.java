package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.io.IOException;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GlyphProviderDefinition {
    MapCodec<GlyphProviderDefinition> f_316723_ = GlyphProviderType.CODEC.dispatchMap(GlyphProviderDefinition::type, GlyphProviderType::mapCodec);

    GlyphProviderType type();

    Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack();

    @OnlyIn(Dist.CLIENT)
    public static record Conditional(GlyphProviderDefinition f_315443_, FontOption.Filter f_317087_) {
        public static final Codec<GlyphProviderDefinition.Conditional> f_316293_ = RecordCodecBuilder.create(
            p_330851_ -> p_330851_.group(
                        GlyphProviderDefinition.f_316723_.forGetter(GlyphProviderDefinition.Conditional::f_315443_),
                        FontOption.Filter.f_314128_
                            .optionalFieldOf("filter", FontOption.Filter.f_315854_)
                            .forGetter(GlyphProviderDefinition.Conditional::f_317087_)
                    )
                    .apply(p_330851_, GlyphProviderDefinition.Conditional::new)
        );
    }

    @OnlyIn(Dist.CLIENT)
    public interface Loader {
        GlyphProvider load(ResourceManager pResourceManager) throws IOException;
    }

    @OnlyIn(Dist.CLIENT)
    public static record Reference(ResourceLocation id) {
    }
}