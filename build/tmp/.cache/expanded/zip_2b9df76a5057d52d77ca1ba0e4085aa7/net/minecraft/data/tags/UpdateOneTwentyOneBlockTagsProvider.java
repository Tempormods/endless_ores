package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyOneBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
    public UpdateOneTwentyOneBlockTagsProvider(
        PackOutput p_311091_, CompletableFuture<HolderLookup.Provider> p_312495_, CompletableFuture<TagsProvider.TagLookup<Block>> p_312696_
    ) {
        super(p_311091_, Registries.BLOCK, p_312495_, p_312696_, p_313025_ -> p_313025_.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider p_311271_) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(
                Blocks.f_303044_,
                Blocks.f_303571_,
                Blocks.f_303426_,
                Blocks.f_302213_,
                Blocks.f_302743_,
                Blocks.f_303652_,
                Blocks.f_302914_,
                Blocks.f_302449_,
                Blocks.f_302818_,
                Blocks.f_303547_,
                Blocks.f_303545_,
                Blocks.f_303371_,
                Blocks.f_303237_,
                Blocks.f_302382_,
                Blocks.f_302689_,
                Blocks.f_303448_,
                Blocks.f_302507_,
                Blocks.f_303811_,
                Blocks.f_303363_,
                Blocks.f_302554_,
                Blocks.f_302612_,
                Blocks.f_303118_,
                Blocks.f_303215_,
                Blocks.f_302995_,
                Blocks.f_303236_,
                Blocks.f_303549_,
                Blocks.f_303013_,
                Blocks.f_303410_,
                Blocks.f_302872_,
                Blocks.f_303373_,
                Blocks.f_302358_,
                Blocks.f_303271_,
                Blocks.f_303674_,
                Blocks.f_302668_,
                Blocks.f_302439_,
                Blocks.f_303797_,
                Blocks.f_302556_,
                Blocks.f_302347_,
                Blocks.f_302565_,
                Blocks.f_303201_,
                Blocks.f_303010_,
                Blocks.f_303016_,
                Blocks.f_302634_,
                Blocks.f_303222_,
                Blocks.f_302853_,
                Blocks.f_303692_,
                Blocks.f_303635_,
                Blocks.f_303609_,
                Blocks.f_302627_,
                Blocks.f_302247_,
                Blocks.f_302395_,
                Blocks.f_302272_,
                Blocks.f_303663_,
                Blocks.f_303101_,
                Blocks.f_314894_
            );
        this.tag(BlockTags.STAIRS).add(Blocks.f_303426_, Blocks.f_302449_, Blocks.f_303371_);
        this.tag(BlockTags.SLABS).add(Blocks.f_303571_, Blocks.f_302914_, Blocks.f_303545_);
        this.tag(BlockTags.WALLS).add(Blocks.f_302213_, Blocks.f_302818_, Blocks.f_303237_);
        this.tag(BlockTags.NEEDS_STONE_TOOL)
            .add(
                Blocks.f_303044_,
                Blocks.f_302689_,
                Blocks.f_303448_,
                Blocks.f_302507_,
                Blocks.f_303811_,
                Blocks.f_303363_,
                Blocks.f_302554_,
                Blocks.f_302612_,
                Blocks.f_303118_,
                Blocks.f_303215_,
                Blocks.f_302995_,
                Blocks.f_303236_,
                Blocks.f_303549_,
                Blocks.f_303013_,
                Blocks.f_303410_,
                Blocks.f_302872_,
                Blocks.f_303373_,
                Blocks.f_302358_,
                Blocks.f_303271_,
                Blocks.f_303674_,
                Blocks.f_302668_,
                Blocks.f_302439_,
                Blocks.f_303797_,
                Blocks.f_302556_,
                Blocks.f_302347_,
                Blocks.f_303635_,
                Blocks.f_303609_,
                Blocks.f_302627_,
                Blocks.f_302247_,
                Blocks.f_302395_,
                Blocks.f_302272_,
                Blocks.f_303663_,
                Blocks.f_303101_
            );
        this.tag(BlockTags.WOODEN_DOORS)
            .add(
                Blocks.f_302565_, Blocks.f_303201_, Blocks.f_303010_, Blocks.f_303016_, Blocks.f_302634_, Blocks.f_303222_, Blocks.f_302853_, Blocks.f_303692_
            );
        this.tag(BlockTags.FEATURES_CANNOT_REPLACE).add(Blocks.f_302964_).add(Blocks.f_316985_);
        this.tag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE).addTag(BlockTags.FEATURES_CANNOT_REPLACE);
        this.tag(BlockTags.TRAPDOORS)
            .add(
                Blocks.f_303635_, Blocks.f_303609_, Blocks.f_302627_, Blocks.f_302247_, Blocks.f_302395_, Blocks.f_302272_, Blocks.f_303663_, Blocks.f_303101_
            );
        this.tag(BlockTags.DOORS)
            .add(
                Blocks.f_302565_, Blocks.f_303201_, Blocks.f_303010_, Blocks.f_303016_, Blocks.f_302634_, Blocks.f_303222_, Blocks.f_302853_, Blocks.f_303692_
            );
        this.tag(BlockTags.f_314652_).add(Blocks.BARRIER, Blocks.BEDROCK);
    }
}