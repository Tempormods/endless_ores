package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record Tool(List<Tool.Rule> f_316959_, float f_314531_, int f_315325_) {
    public static final Codec<Tool> f_314592_ = RecordCodecBuilder.create(
        p_335351_ -> p_335351_.group(
                    Tool.Rule.f_314178_.listOf().fieldOf("rules").forGetter(Tool::f_316959_),
                    Codec.FLOAT.optionalFieldOf("default_mining_speed", Float.valueOf(1.0F)).forGetter(Tool::f_314531_),
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_block", 1).forGetter(Tool::f_315325_)
                )
                .apply(p_335351_, Tool::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Tool> f_314459_ = StreamCodec.m_321516_(
        Tool.Rule.f_314031_.m_321801_(ByteBufCodecs.m_324765_()),
        Tool::f_316959_,
        ByteBufCodecs.f_314734_,
        Tool::f_314531_,
        ByteBufCodecs.f_316730_,
        Tool::f_315325_,
        Tool::new
    );

    public float m_325036_(BlockState p_330264_) {
        for (Tool.Rule tool$rule : this.f_316959_) {
            if (tool$rule.f_314550_.isPresent() && p_330264_.is(tool$rule.f_316953_)) {
                return tool$rule.f_314550_.get();
            }
        }

        return this.f_314531_;
    }

    public boolean m_322492_(BlockState p_332652_) {
        for (Tool.Rule tool$rule : this.f_316959_) {
            if (tool$rule.f_315140_.isPresent() && p_332652_.is(tool$rule.f_316953_)) {
                return tool$rule.f_315140_.get();
            }
        }

        return false;
    }

    public static record Rule(HolderSet<Block> f_316953_, Optional<Float> f_314550_, Optional<Boolean> f_315140_) {
        public static final Codec<Tool.Rule> f_314178_ = RecordCodecBuilder.create(
            p_329479_ -> p_329479_.group(
                        RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(Tool.Rule::f_316953_),
                        ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Tool.Rule::f_314550_),
                        Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Tool.Rule::f_315140_)
                    )
                    .apply(p_329479_, Tool.Rule::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Tool.Rule> f_314031_ = StreamCodec.m_321516_(
            ByteBufCodecs.m_319169_(Registries.BLOCK),
            Tool.Rule::f_316953_,
            ByteBufCodecs.f_314734_.m_321801_(ByteBufCodecs::m_319027_),
            Tool.Rule::f_314550_,
            ByteBufCodecs.f_315514_.m_321801_(ByteBufCodecs::m_319027_),
            Tool.Rule::f_315140_,
            Tool.Rule::new
        );

        public static Tool.Rule m_321859_(List<Block> p_335835_, float p_329194_) {
            return m_322181_(p_335835_, Optional.of(p_329194_), Optional.of(true));
        }

        public static Tool.Rule m_321972_(TagKey<Block> p_331729_, float p_328288_) {
            return m_320355_(p_331729_, Optional.of(p_328288_), Optional.of(true));
        }

        public static Tool.Rule m_323695_(TagKey<Block> p_330234_) {
            return m_320355_(p_330234_, Optional.empty(), Optional.of(false));
        }

        public static Tool.Rule m_319062_(TagKey<Block> p_331960_, float p_329347_) {
            return m_320355_(p_331960_, Optional.of(p_329347_), Optional.empty());
        }

        public static Tool.Rule m_322924_(List<Block> p_330791_, float p_328067_) {
            return m_322181_(p_330791_, Optional.of(p_328067_), Optional.empty());
        }

        private static Tool.Rule m_320355_(TagKey<Block> p_330425_, Optional<Float> p_328628_, Optional<Boolean> p_332485_) {
            return new Tool.Rule(BuiltInRegistries.BLOCK.getOrCreateTag(p_330425_), p_328628_, p_332485_);
        }

        private static Tool.Rule m_322181_(List<Block> p_330965_, Optional<Float> p_333293_, Optional<Boolean> p_332888_) {
            return new Tool.Rule(HolderSet.direct(p_330965_.stream().map(Block::builtInRegistryHolder).collect(Collectors.toList())), p_333293_, p_332888_);
        }
    }
}