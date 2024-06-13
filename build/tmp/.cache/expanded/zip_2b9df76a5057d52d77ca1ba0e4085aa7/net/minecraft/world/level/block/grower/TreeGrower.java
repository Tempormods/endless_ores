package net.minecraft.world.level.block.grower;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class TreeGrower {
    private static final Map<String, TreeGrower> f_302769_ = new Object2ObjectArrayMap<>();
    public static final Codec<TreeGrower> f_302786_ = Codec.stringResolver(p_310196_ -> p_310196_.f_303132_, f_302769_::get);
    public static final TreeGrower f_303425_ = new TreeGrower(
        "oak",
        0.1F,
        Optional.empty(),
        Optional.empty(),
        Optional.of(TreeFeatures.OAK),
        Optional.of(TreeFeatures.FANCY_OAK),
        Optional.of(TreeFeatures.OAK_BEES_005),
        Optional.of(TreeFeatures.FANCY_OAK_BEES_005)
    );
    public static final TreeGrower f_302563_ = new TreeGrower(
        "spruce",
        0.5F,
        Optional.of(TreeFeatures.MEGA_SPRUCE),
        Optional.of(TreeFeatures.MEGA_PINE),
        Optional.of(TreeFeatures.SPRUCE),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );
    public static final TreeGrower f_302923_ = new TreeGrower(
        "mangrove",
        0.85F,
        Optional.empty(),
        Optional.empty(),
        Optional.of(TreeFeatures.MANGROVE),
        Optional.of(TreeFeatures.TALL_MANGROVE),
        Optional.empty(),
        Optional.empty()
    );
    public static final TreeGrower f_303862_ = new TreeGrower("azalea", Optional.empty(), Optional.of(TreeFeatures.AZALEA_TREE), Optional.empty());
    public static final TreeGrower f_303209_ = new TreeGrower(
        "birch", Optional.empty(), Optional.of(TreeFeatures.BIRCH), Optional.of(TreeFeatures.BIRCH_BEES_005)
    );
    public static final TreeGrower f_303040_ = new TreeGrower(
        "jungle", Optional.of(TreeFeatures.MEGA_JUNGLE_TREE), Optional.of(TreeFeatures.JUNGLE_TREE_NO_VINE), Optional.empty()
    );
    public static final TreeGrower f_303438_ = new TreeGrower("acacia", Optional.empty(), Optional.of(TreeFeatures.ACACIA), Optional.empty());
    public static final TreeGrower f_302972_ = new TreeGrower(
        "cherry", Optional.empty(), Optional.of(TreeFeatures.CHERRY), Optional.of(TreeFeatures.CHERRY_BEES_005)
    );
    public static final TreeGrower f_302400_ = new TreeGrower("dark_oak", Optional.of(TreeFeatures.DARK_OAK), Optional.empty(), Optional.empty());
    private final String f_303132_;
    private final float f_303193_;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> f_302394_;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> f_303060_;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> f_302290_;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> f_303523_;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> f_303258_;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> f_303546_;

    public TreeGrower(
        String p_311110_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_309803_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_311829_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_310077_
    ) {
        this(p_311110_, 0.0F, p_309803_, Optional.empty(), p_311829_, Optional.empty(), p_310077_, Optional.empty());
    }

    public TreeGrower(
        String p_310538_,
        float p_312608_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_311356_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_309855_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_312520_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_310394_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_309623_,
        Optional<ResourceKey<ConfiguredFeature<?, ?>>> p_310708_
    ) {
        this.f_303132_ = p_310538_;
        this.f_303193_ = p_312608_;
        this.f_302394_ = p_311356_;
        this.f_303060_ = p_309855_;
        this.f_302290_ = p_312520_;
        this.f_303523_ = p_310394_;
        this.f_303258_ = p_309623_;
        this.f_303546_ = p_310708_;
        f_302769_.put(p_310538_, this);
    }

    @Nullable
    private ResourceKey<ConfiguredFeature<?, ?>> m_307922_(RandomSource p_312729_, boolean p_311061_) {
        if (p_312729_.nextFloat() < this.f_303193_) {
            if (p_311061_ && this.f_303546_.isPresent()) {
                return this.f_303546_.get();
            }

            if (this.f_303523_.isPresent()) {
                return this.f_303523_.get();
            }
        }

        return p_311061_ && this.f_303258_.isPresent() ? this.f_303258_.get() : this.f_302290_.orElse(null);
    }

    @Nullable
    private ResourceKey<ConfiguredFeature<?, ?>> m_304937_(RandomSource p_309400_) {
        return this.f_303060_.isPresent() && p_309400_.nextFloat() < this.f_303193_ ? this.f_303060_.get() : this.f_302394_.orElse(null);
    }

    public boolean m_307294_(ServerLevel p_309830_, ChunkGenerator p_311976_, BlockPos p_310327_, BlockState p_312382_, RandomSource p_309951_) {
        ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.m_304937_(p_309951_);
        if (resourcekey != null) {
            Holder<ConfiguredFeature<?, ?>> holder = p_309830_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse(null);
            var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(p_309830_, p_309951_, p_310327_, holder);
            holder = event.getFeature();
            if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
            if (holder != null) {
                for (int i = 0; i >= -1; i--) {
                    for (int j = 0; j >= -1; j--) {
                        if (m_305963_(p_312382_, p_309830_, p_310327_, i, j)) {
                            ConfiguredFeature<?, ?> configuredfeature = holder.value();
                            BlockState blockstate = Blocks.AIR.defaultBlockState();
                            p_309830_.setBlock(p_310327_.offset(i, 0, j), blockstate, 4);
                            p_309830_.setBlock(p_310327_.offset(i + 1, 0, j), blockstate, 4);
                            p_309830_.setBlock(p_310327_.offset(i, 0, j + 1), blockstate, 4);
                            p_309830_.setBlock(p_310327_.offset(i + 1, 0, j + 1), blockstate, 4);
                            if (configuredfeature.place(p_309830_, p_311976_, p_309951_, p_310327_.offset(i, 0, j))) {
                                return true;
                            }

                            p_309830_.setBlock(p_310327_.offset(i, 0, j), p_312382_, 4);
                            p_309830_.setBlock(p_310327_.offset(i + 1, 0, j), p_312382_, 4);
                            p_309830_.setBlock(p_310327_.offset(i, 0, j + 1), p_312382_, 4);
                            p_309830_.setBlock(p_310327_.offset(i + 1, 0, j + 1), p_312382_, 4);
                            return false;
                        }
                    }
                }
            }
        }

        ResourceKey<ConfiguredFeature<?, ?>> resourcekey1 = this.m_307922_(p_309951_, this.m_306742_(p_309830_, p_310327_));
        if (resourcekey1 == null) {
            return false;
        } else {
            Holder<ConfiguredFeature<?, ?>> holder1 = p_309830_.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey1).orElse(null);
            if (holder1 == null) {
                return false;
            } else {
                ConfiguredFeature<?, ?> configuredfeature1 = holder1.value();
                BlockState blockstate1 = p_309830_.getFluidState(p_310327_).createLegacyBlock();
                p_309830_.setBlock(p_310327_, blockstate1, 4);
                if (configuredfeature1.place(p_309830_, p_311976_, p_309951_, p_310327_)) {
                    if (p_309830_.getBlockState(p_310327_) == blockstate1) {
                        p_309830_.sendBlockUpdated(p_310327_, p_312382_, blockstate1, 2);
                    }

                    return true;
                } else {
                    p_309830_.setBlock(p_310327_, p_312382_, 4);
                    return false;
                }
            }
        }
    }

    private static boolean m_305963_(BlockState p_310256_, BlockGetter p_311754_, BlockPos p_312442_, int p_310725_, int p_310118_) {
        Block block = p_310256_.getBlock();
        return p_311754_.getBlockState(p_312442_.offset(p_310725_, 0, p_310118_)).is(block)
            && p_311754_.getBlockState(p_312442_.offset(p_310725_ + 1, 0, p_310118_)).is(block)
            && p_311754_.getBlockState(p_312442_.offset(p_310725_, 0, p_310118_ + 1)).is(block)
            && p_311754_.getBlockState(p_312442_.offset(p_310725_ + 1, 0, p_310118_ + 1)).is(block);
    }

    private boolean m_306742_(LevelAccessor p_312531_, BlockPos p_312326_) {
        for (BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(
            p_312326_.below().north(2).west(2), p_312326_.above().south(2).east(2)
        )) {
            if (p_312531_.getBlockState(blockpos).is(BlockTags.FLOWERS)) {
                return true;
            }
        }

        return false;
    }
}
