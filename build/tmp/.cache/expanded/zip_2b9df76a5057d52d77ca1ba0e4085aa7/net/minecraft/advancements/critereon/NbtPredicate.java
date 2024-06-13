package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record NbtPredicate(CompoundTag tag) {
    public static final Codec<NbtPredicate> CODEC = TagParser.f_316526_.xmap(NbtPredicate::new, NbtPredicate::tag);
    public static final StreamCodec<ByteBuf, NbtPredicate> f_314061_ = ByteBufCodecs.f_314933_.m_323038_(NbtPredicate::new, NbtPredicate::tag);

    public boolean matches(ItemStack pStack) {
        CustomData customdata = pStack.m_322304_(DataComponents.f_316665_, CustomData.f_317060_);
        return customdata.m_324111_(this.tag);
    }

    public boolean matches(Entity pEntity) {
        return this.matches(getEntityTagToCompare(pEntity));
    }

    public boolean matches(@Nullable Tag pTag) {
        return pTag != null && NbtUtils.compareNbt(this.tag, pTag, true);
    }

    public static CompoundTag getEntityTagToCompare(Entity pEntity) {
        CompoundTag compoundtag = pEntity.saveWithoutId(new CompoundTag());
        if (pEntity instanceof Player) {
            ItemStack itemstack = ((Player)pEntity).getInventory().getSelected();
            if (!itemstack.isEmpty()) {
                compoundtag.put("SelectedItem", itemstack.save(pEntity.m_321891_()));
            }
        }

        return compoundtag;
    }
}