package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyOneEntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public UpdateOneTwentyOneEntityTypeTagsProvider(PackOutput p_312388_, CompletableFuture<HolderLookup.Provider> p_311973_) {
        super(p_312388_, Registries.ENTITY_TYPE, p_311973_, p_312136_ -> p_312136_.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider p_312640_) {
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(EntityType.f_302782_);
        this.tag(EntityTypeTags.f_316998_).add(EntityType.f_302782_);
        this.tag(EntityTypeTags.f_302423_).add(EntityType.f_302782_);
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(EntityType.f_303421_, EntityType.f_315936_);
        this.tag(EntityTypeTags.f_315192_)
            .add(
                EntityType.f_302782_,
                EntityType.SKELETON,
                EntityType.f_316281_,
                EntityType.STRAY,
                EntityType.ZOMBIE,
                EntityType.HUSK,
                EntityType.SPIDER,
                EntityType.CAVE_SPIDER,
                EntityType.SLIME
            );
        this.tag(EntityTypeTags.SKELETONS).add(EntityType.f_316281_);
        this.tag(EntityTypeTags.f_314026_).add(EntityType.SILVERFISH);
        this.tag(EntityTypeTags.f_315487_).add(EntityType.SLIME);
        this.tag(EntityTypeTags.f_314896_).add(EntityType.f_303421_, EntityType.f_315936_);
    }
}