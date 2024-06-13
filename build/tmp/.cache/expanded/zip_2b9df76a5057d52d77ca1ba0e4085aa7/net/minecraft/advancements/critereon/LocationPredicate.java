package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.levelgen.structure.Structure;

public record LocationPredicate(
    Optional<LocationPredicate.PositionPredicate> position,
    Optional<HolderSet<Biome>> f_314588_,
    Optional<HolderSet<Structure>> f_316568_,
    Optional<ResourceKey<Level>> dimension,
    Optional<Boolean> smokey,
    Optional<LightPredicate> light,
    Optional<BlockPredicate> block,
    Optional<FluidPredicate> fluid
) {
    public static final Codec<LocationPredicate> CODEC = RecordCodecBuilder.create(
        p_296137_ -> p_296137_.group(
                    LocationPredicate.PositionPredicate.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position),
                    RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::f_314588_),
                    RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::f_316568_),
                    ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension),
                    Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey),
                    LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light),
                    BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block),
                    FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid)
                )
                .apply(p_296137_, LocationPredicate::new)
    );

    public boolean matches(ServerLevel pLevel, double pX, double pY, double pZ) {
        if (this.position.isPresent() && !this.position.get().matches(pX, pY, pZ)) {
            return false;
        } else if (this.dimension.isPresent() && this.dimension.get() != pLevel.dimension()) {
            return false;
        } else {
            BlockPos blockpos = BlockPos.containing(pX, pY, pZ);
            boolean flag = pLevel.isLoaded(blockpos);
            if (!this.f_314588_.isPresent() || flag && this.f_314588_.get().contains(pLevel.getBiome(blockpos))) {
                if (!this.f_316568_.isPresent() || flag && pLevel.structureManager().getStructureWithPieceAt(blockpos, this.f_316568_.get()).isValid()) {
                    if (!this.smokey.isPresent() || flag && this.smokey.get() == CampfireBlock.isSmokeyPos(pLevel, blockpos)) {
                        if (this.light.isPresent() && !this.light.get().matches(pLevel, blockpos)) {
                            return false;
                        } else {
                            return this.block.isPresent() && !this.block.get().matches(pLevel, blockpos)
                                ? false
                                : !this.fluid.isPresent() || this.fluid.get().matches(pLevel, blockpos);
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public static class Builder {
        private MinMaxBounds.Doubles x = MinMaxBounds.Doubles.ANY;
        private MinMaxBounds.Doubles y = MinMaxBounds.Doubles.ANY;
        private MinMaxBounds.Doubles z = MinMaxBounds.Doubles.ANY;
        private Optional<HolderSet<Biome>> f_315213_ = Optional.empty();
        private Optional<HolderSet<Structure>> f_316280_ = Optional.empty();
        private Optional<ResourceKey<Level>> dimension = Optional.empty();
        private Optional<Boolean> smokey = Optional.empty();
        private Optional<LightPredicate> light = Optional.empty();
        private Optional<BlockPredicate> block = Optional.empty();
        private Optional<FluidPredicate> fluid = Optional.empty();

        public static LocationPredicate.Builder location() {
            return new LocationPredicate.Builder();
        }

        public static LocationPredicate.Builder inBiome(Holder<Biome> p_334208_) {
            return location().m_320551_(HolderSet.direct(p_334208_));
        }

        public static LocationPredicate.Builder inDimension(ResourceKey<Level> pDimension) {
            return location().setDimension(pDimension);
        }

        public static LocationPredicate.Builder inStructure(Holder<Structure> p_333866_) {
            return location().m_319558_(HolderSet.direct(p_333866_));
        }

        public static LocationPredicate.Builder atYLocation(MinMaxBounds.Doubles pY) {
            return location().setY(pY);
        }

        public LocationPredicate.Builder setX(MinMaxBounds.Doubles pX) {
            this.x = pX;
            return this;
        }

        public LocationPredicate.Builder setY(MinMaxBounds.Doubles pY) {
            this.y = pY;
            return this;
        }

        public LocationPredicate.Builder setZ(MinMaxBounds.Doubles pZ) {
            this.z = pZ;
            return this;
        }

        public LocationPredicate.Builder m_320551_(HolderSet<Biome> p_330531_) {
            this.f_315213_ = Optional.of(p_330531_);
            return this;
        }

        public LocationPredicate.Builder m_319558_(HolderSet<Structure> p_330147_) {
            this.f_316280_ = Optional.of(p_330147_);
            return this;
        }

        public LocationPredicate.Builder setDimension(ResourceKey<Level> pDimension) {
            this.dimension = Optional.of(pDimension);
            return this;
        }

        public LocationPredicate.Builder setLight(LightPredicate.Builder pLight) {
            this.light = Optional.of(pLight.build());
            return this;
        }

        public LocationPredicate.Builder setBlock(BlockPredicate.Builder pBlock) {
            this.block = Optional.of(pBlock.build());
            return this;
        }

        public LocationPredicate.Builder setFluid(FluidPredicate.Builder pFluid) {
            this.fluid = Optional.of(pFluid.build());
            return this;
        }

        public LocationPredicate.Builder setSmokey(boolean pSmokey) {
            this.smokey = Optional.of(pSmokey);
            return this;
        }

        public LocationPredicate build() {
            Optional<LocationPredicate.PositionPredicate> optional = LocationPredicate.PositionPredicate.of(this.x, this.y, this.z);
            return new LocationPredicate(optional, this.f_315213_, this.f_316280_, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }

    static record PositionPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z) {
        public static final Codec<LocationPredicate.PositionPredicate> CODEC = RecordCodecBuilder.create(
            p_325229_ -> p_325229_.group(
                        MinMaxBounds.Doubles.CODEC
                            .optionalFieldOf("x", MinMaxBounds.Doubles.ANY)
                            .forGetter(LocationPredicate.PositionPredicate::x),
                        MinMaxBounds.Doubles.CODEC
                            .optionalFieldOf("y", MinMaxBounds.Doubles.ANY)
                            .forGetter(LocationPredicate.PositionPredicate::y),
                        MinMaxBounds.Doubles.CODEC
                            .optionalFieldOf("z", MinMaxBounds.Doubles.ANY)
                            .forGetter(LocationPredicate.PositionPredicate::z)
                    )
                    .apply(p_325229_, LocationPredicate.PositionPredicate::new)
        );

        static Optional<LocationPredicate.PositionPredicate> of(
            MinMaxBounds.Doubles pX, MinMaxBounds.Doubles pY, MinMaxBounds.Doubles pZ
        ) {
            return pX.isAny() && pY.isAny() && pZ.isAny()
                ? Optional.empty()
                : Optional.of(new LocationPredicate.PositionPredicate(pX, pY, pZ));
        }

        public boolean matches(double pX, double pY, double pZ) {
            return this.x.matches(pX) && this.y.matches(pY) && this.z.matches(pZ);
        }
    }
}