package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput {
    private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType(
        (p_308404_, p_308405_) -> Component.m_307043_("arguments.item.overstacked", p_308404_, p_308405_)
    );
    private final Holder<Item> item;
    private final DataComponentMap f_316689_;

    public ItemInput(Holder<Item> pItem, DataComponentMap p_335048_) {
        this.item = pItem;
        this.f_316689_ = p_335048_;
    }

    public Item getItem() {
        return this.item.value();
    }

    public ItemStack createItemStack(int pCount, boolean pAllowOversizedStacks) throws CommandSyntaxException {
        ItemStack itemstack = new ItemStack(this.item, pCount);
        itemstack.m_323474_(this.f_316689_);
        if (pAllowOversizedStacks && pCount > itemstack.getMaxStackSize()) {
            throw ERROR_STACK_TOO_BIG.create(this.getItemName(), itemstack.getMaxStackSize());
        } else {
            return itemstack;
        }
    }

    public String serialize(HolderLookup.Provider p_331128_) {
        StringBuilder stringbuilder = new StringBuilder(this.getItemName());
        String s = this.m_319662_(p_331128_);
        if (!s.isEmpty()) {
            stringbuilder.append('[');
            stringbuilder.append(s);
            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    private String m_319662_(HolderLookup.Provider p_332272_) {
        DynamicOps<Tag> dynamicops = p_332272_.m_318927_(NbtOps.INSTANCE);
        return this.f_316689_.m_322172_().flatMap(p_325610_ -> {
            DataComponentType<?> datacomponenttype = p_325610_.f_316611_();
            ResourceLocation resourcelocation = BuiltInRegistries.f_315333_.getKey(datacomponenttype);
            Optional<Tag> optional = p_325610_.m_318908_(dynamicops).result();
            return resourcelocation != null && !optional.isEmpty() ? Stream.of(resourcelocation.toString() + "=" + optional.get()) : Stream.empty();
        }).collect(Collectors.joining(String.valueOf(',')));
    }

    private String getItemName() {
        return this.item.unwrapKey().<Object>map(ResourceKey::location).orElseGet(() -> "unknown[" + this.item + "]").toString();
    }
}