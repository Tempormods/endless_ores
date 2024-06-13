package net.minecraft.data.loot.packs;

import java.util.Set;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyOneBlockLoot extends BlockLootSubProvider {
    protected UpdateOneTwentyOneBlockLoot() {
        super(Set.of(), FeatureFlagSet.of(FeatureFlags.f_302467_));
    }

    @Override
    protected void generate() {
        this.dropSelf(Blocks.f_303044_);
        this.dropSelf(Blocks.f_302743_);
        this.dropSelf(Blocks.f_303426_);
        this.dropSelf(Blocks.f_302213_);
        this.dropSelf(Blocks.f_303652_);
        this.dropSelf(Blocks.f_302449_);
        this.dropSelf(Blocks.f_302818_);
        this.dropSelf(Blocks.f_303547_);
        this.dropSelf(Blocks.f_303371_);
        this.dropSelf(Blocks.f_303237_);
        this.dropSelf(Blocks.f_302382_);
        this.add(Blocks.f_303571_, p_309884_ -> this.createSlabItemTable(p_309884_));
        this.add(Blocks.f_303545_, p_312067_ -> this.createSlabItemTable(p_312067_));
        this.add(Blocks.f_302914_, p_312759_ -> this.createSlabItemTable(p_312759_));
        this.dropSelf(Blocks.f_302689_);
        this.dropSelf(Blocks.f_303448_);
        this.dropSelf(Blocks.f_302507_);
        this.dropSelf(Blocks.f_303811_);
        this.dropSelf(Blocks.f_303363_);
        this.dropSelf(Blocks.f_302554_);
        this.dropSelf(Blocks.f_302612_);
        this.dropSelf(Blocks.f_303118_);
        this.add(Blocks.f_302565_, p_310248_ -> this.createDoorTable(p_310248_));
        this.add(Blocks.f_303201_, p_310936_ -> this.createDoorTable(p_310936_));
        this.add(Blocks.f_303010_, p_311974_ -> this.createDoorTable(p_311974_));
        this.add(Blocks.f_303016_, p_312652_ -> this.createDoorTable(p_312652_));
        this.add(Blocks.f_302634_, p_311328_ -> this.createDoorTable(p_311328_));
        this.add(Blocks.f_303222_, p_310922_ -> this.createDoorTable(p_310922_));
        this.add(Blocks.f_302853_, p_310816_ -> this.createDoorTable(p_310816_));
        this.add(Blocks.f_303692_, p_310706_ -> this.createDoorTable(p_310706_));
        this.dropSelf(Blocks.f_303635_);
        this.dropSelf(Blocks.f_303609_);
        this.dropSelf(Blocks.f_302627_);
        this.dropSelf(Blocks.f_302247_);
        this.dropSelf(Blocks.f_302395_);
        this.dropSelf(Blocks.f_302272_);
        this.dropSelf(Blocks.f_303663_);
        this.dropSelf(Blocks.f_303101_);
        this.dropSelf(Blocks.f_303215_);
        this.dropSelf(Blocks.f_302995_);
        this.dropSelf(Blocks.f_303236_);
        this.dropSelf(Blocks.f_303549_);
        this.dropSelf(Blocks.f_303013_);
        this.dropSelf(Blocks.f_303410_);
        this.dropSelf(Blocks.f_302872_);
        this.dropSelf(Blocks.f_303373_);
        this.dropSelf(Blocks.f_302358_);
        this.dropSelf(Blocks.f_303271_);
        this.dropSelf(Blocks.f_303674_);
        this.dropSelf(Blocks.f_302668_);
        this.dropSelf(Blocks.f_302439_);
        this.dropSelf(Blocks.f_303797_);
        this.dropSelf(Blocks.f_302556_);
        this.dropSelf(Blocks.f_302347_);
        this.add(Blocks.f_302964_, noDrop());
        this.add(Blocks.f_316985_, noDrop());
        this.dropSelf(Blocks.f_314894_);
    }
}