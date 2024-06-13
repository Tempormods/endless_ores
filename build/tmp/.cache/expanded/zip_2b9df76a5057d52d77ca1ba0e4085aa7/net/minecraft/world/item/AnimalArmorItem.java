package net.minecraft.world.item;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class AnimalArmorItem extends ArmorItem {
    private final ResourceLocation f_313937_;
    @Nullable
    private final ResourceLocation f_314825_;
    private final AnimalArmorItem.BodyType f_314349_;

    public AnimalArmorItem(Holder<ArmorMaterial> p_329749_, AnimalArmorItem.BodyType p_330915_, boolean p_329552_, Item.Properties p_333708_) {
        super(p_329749_, ArmorItem.Type.BODY, p_333708_);
        this.f_314349_ = p_330915_;
        ResourceLocation resourcelocation = p_330915_.f_315425_.apply(p_329749_.unwrapKey().orElseThrow().location());
        this.f_313937_ = resourcelocation.withSuffix(".png");
        if (p_329552_) {
            this.f_314825_ = resourcelocation.withSuffix("_overlay.png");
        } else {
            this.f_314825_ = null;
        }
    }

    public ResourceLocation m_320881_() {
        return this.f_313937_;
    }

    @Nullable
    public ResourceLocation m_323746_() {
        return this.f_314825_;
    }

    public AnimalArmorItem.BodyType m_319458_() {
        return this.f_314349_;
    }

    @Override
    public SoundEvent m_318629_() {
        return this.f_314349_.f_315049_;
    }

    @Override
    public boolean isEnchantable(ItemStack p_329133_) {
        return false;
    }

    public static enum BodyType implements net.minecraftforge.common.IExtensibleEnum {
        EQUESTRIAN(p_331659_ -> p_331659_.withPath(p_329177_ -> "textures/entity/horse/armor/horse_armor_" + p_329177_), SoundEvents.ITEM_BREAK),
        CANINE(p_333424_ -> p_333424_.withPath("textures/entity/wolf/wolf_armor"), SoundEvents.f_315250_);

        final Function<ResourceLocation, ResourceLocation> f_315425_;
        final SoundEvent f_315049_;

        private BodyType(final Function<ResourceLocation, ResourceLocation> p_332420_, final SoundEvent p_335661_) {
            this.f_315425_ = p_332420_;
            this.f_315049_ = p_335661_;
        }

        public static BodyType create(String name, final Function<ResourceLocation, ResourceLocation> path, SoundEvent sound) {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
