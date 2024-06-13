package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockPredicate(Optional<HolderSet<Block>> blocks, Optional<StatePropertiesPredicate> properties, Optional<NbtPredicate> nbt) {
    public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(
        p_325191_ -> p_325191_.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(BlockPredicate::blocks),
                    StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(BlockPredicate::properties),
                    NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(BlockPredicate::nbt)
                )
                .apply(p_325191_, BlockPredicate::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPredicate> f_315415_ = StreamCodec.m_321516_(
        ByteBufCodecs.m_319027_(ByteBufCodecs.m_319169_(Registries.BLOCK)),
        BlockPredicate::blocks,
        ByteBufCodecs.m_319027_(StatePropertiesPredicate.f_316592_),
        BlockPredicate::properties,
        ByteBufCodecs.m_319027_(NbtPredicate.f_314061_),
        BlockPredicate::nbt,
        BlockPredicate::new
    );

    public boolean matches(ServerLevel pLevel, BlockPos pPos) {
        if (!pLevel.isLoaded(pPos)) {
            return false;
        } else {
            return !this.m_320876_(pLevel.getBlockState(pPos))
                ? false
                : !this.nbt.isPresent() || m_320500_(pLevel, pLevel.getBlockEntity(pPos), this.nbt.get());
        }
    }

    public boolean m_321461_(BlockInWorld p_335665_) {
        return !this.m_320876_(p_335665_.getState())
            ? false
            : !this.nbt.isPresent() || m_320500_(p_335665_.getLevel(), p_335665_.getEntity(), this.nbt.get());
    }

    private boolean m_320876_(BlockState p_334077_) {
        return this.blocks.isPresent() && !p_334077_.is(this.blocks.get())
            ? false
            : !this.properties.isPresent() || this.properties.get().matches(p_334077_);
    }

    private static boolean m_320500_(LevelReader p_330206_, @Nullable BlockEntity p_327732_, NbtPredicate p_335422_) {
        return p_327732_ != null && p_335422_.matches(p_327732_.saveWithFullMetadata(p_330206_.registryAccess()));
    }

    public boolean m_324452_() {
        return this.nbt.isPresent();
    }

    public static class Builder {
        private Optional<HolderSet<Block>> blocks = Optional.empty();
        private Optional<StatePropertiesPredicate> properties = Optional.empty();
        private Optional<NbtPredicate> nbt = Optional.empty();

        private Builder() {
        }

        public static BlockPredicate.Builder block() {
            return new BlockPredicate.Builder();
        }

        public BlockPredicate.Builder of(Block... pBlocks) {
            this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, pBlocks));
            return this;
        }

        public BlockPredicate.Builder of(Collection<Block> p_298036_) {
            this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, p_298036_));
            return this;
        }

        public BlockPredicate.Builder of(TagKey<Block> pTag) {
            this.blocks = Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(pTag));
            return this;
        }

        public BlockPredicate.Builder hasNbt(CompoundTag pNbt) {
            this.nbt = Optional.of(new NbtPredicate(pNbt));
            return this;
        }

        public BlockPredicate.Builder setProperties(StatePropertiesPredicate.Builder p_299418_) {
            this.properties = p_299418_.build();
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(this.blocks, this.properties, this.nbt);
        }
    }
}