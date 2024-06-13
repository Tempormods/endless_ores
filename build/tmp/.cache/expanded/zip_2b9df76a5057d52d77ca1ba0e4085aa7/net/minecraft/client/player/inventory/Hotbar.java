package net.minecraft.client.player.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Hotbar {
    private static final Logger f_315832_ = LogUtils.getLogger();
    private static final int f_317035_ = Inventory.getSelectionSize();
    public static final Codec<Hotbar> f_316903_ = Codec.PASSTHROUGH
        .listOf()
        .validate(p_335942_ -> Util.fixedSize(p_335942_, f_317035_))
        .xmap(Hotbar::new, p_334316_ -> p_334316_.items);
    private static final DynamicOps<Tag> f_314316_ = NbtOps.INSTANCE;
    private static final Dynamic<?> f_315257_ = new Dynamic<>(f_314316_, ItemStack.f_316315_.encodeStart(f_314316_, ItemStack.EMPTY).getOrThrow());
    private List<Dynamic<?>> items;

    private Hotbar(List<Dynamic<?>> p_336192_) {
        this.items = p_336192_;
    }

    public Hotbar() {
        this(Collections.nCopies(f_317035_, f_315257_));
    }

    public List<ItemStack> m_318641_(HolderLookup.Provider p_331400_) {
        return this.items
            .stream()
            .map(
                p_334847_ -> ItemStack.f_316315_
                        .parse(RegistryOps.m_321059_((Dynamic<?>)p_334847_, p_331400_))
                        .resultOrPartial(p_332209_ -> f_315832_.warn("Could not parse hotbar item: {}", p_332209_))
                        .orElse(ItemStack.EMPTY)
            )
            .toList();
    }

    public void m_324536_(Inventory p_335728_, RegistryAccess p_328533_) {
        RegistryOps<Tag> registryops = p_328533_.m_318927_(f_314316_);
        Builder<Dynamic<?>> builder = ImmutableList.builderWithExpectedSize(f_317035_);

        for (int i = 0; i < f_317035_; i++) {
            ItemStack itemstack = p_335728_.getItem(i);
            Optional<Dynamic<?>> optional = ItemStack.f_316315_
                .encodeStart(registryops, itemstack)
                .resultOrPartial(p_332599_ -> f_315832_.warn("Could not encode hotbar item: {}", p_332599_))
                .map(p_331427_ -> new Dynamic<>(f_314316_, p_331427_));
            builder.add(optional.orElse(f_315257_));
        }

        this.items = builder.build();
    }

    public boolean m_108788_() {
        for (Dynamic<?> dynamic : this.items) {
            if (!m_323331_(dynamic)) {
                return false;
            }
        }

        return true;
    }

    private static boolean m_323331_(Dynamic<?> p_331706_) {
        return f_315257_.equals(p_331706_);
    }
}