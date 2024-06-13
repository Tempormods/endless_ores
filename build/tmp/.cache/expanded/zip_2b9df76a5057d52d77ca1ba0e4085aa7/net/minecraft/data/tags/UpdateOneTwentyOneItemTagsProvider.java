package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class UpdateOneTwentyOneItemTagsProvider extends ItemTagsProvider {
    public UpdateOneTwentyOneItemTagsProvider(
        PackOutput p_309895_,
        CompletableFuture<HolderLookup.Provider> p_312150_,
        CompletableFuture<TagsProvider.TagLookup<Item>> p_311361_,
        CompletableFuture<TagsProvider.TagLookup<Block>> p_310346_
    ) {
        super(p_309895_, p_312150_, p_311361_, p_310346_);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_312918_) {
        this.tag(ItemTags.STAIRS).add(Items.f_303287_, Items.f_303855_, Items.f_303722_);
        this.tag(ItemTags.SLABS).add(Items.f_303544_, Items.f_303752_, Items.f_303552_);
        this.tag(ItemTags.WALLS).add(Items.f_303825_, Items.f_302841_, Items.f_303248_);
        this.tag(ItemTags.DOORS)
            .add(Items.f_302707_, Items.f_302845_, Items.f_302562_, Items.f_303662_, Items.f_302639_, Items.f_302952_, Items.f_303664_, Items.f_303526_);
        this.tag(ItemTags.TRAPDOORS)
            .add(Items.f_303741_, Items.f_303245_, Items.f_303628_, Items.f_302523_, Items.f_302804_, Items.f_303349_, Items.f_302780_, Items.f_302421_);
        this.tag(ItemTags.f_314471_).add(Items.f_314862_);
        this.tag(ItemTags.DECORATED_POT_SHERDS).add(Items.f_314824_, Items.f_316059_, Items.f_315489_);
        this.tag(ItemTags.DECORATED_POT_INGREDIENTS).add(Items.f_314824_).add(Items.f_316059_).add(Items.f_315489_);
        this.tag(ItemTags.TRIM_TEMPLATES).add(Items.f_316167_).add(Items.f_314806_);
        this.tag(ItemTags.f_314809_).add(Items.f_314862_);
        this.tag(ItemTags.f_316107_).add(Items.f_314862_);
        this.tag(ItemTags.f_314461_).add(Items.f_314862_);
        this.tag(ItemTags.f_314986_).add(Items.f_314862_);
        this.tag(ItemTags.BREAKS_DECORATED_POTS).add(Items.f_314862_);
    }
}