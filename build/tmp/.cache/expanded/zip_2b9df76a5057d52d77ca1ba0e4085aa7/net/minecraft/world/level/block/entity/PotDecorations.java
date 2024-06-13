package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record PotDecorations(Optional<Item> f_316149_, Optional<Item> f_315094_, Optional<Item> f_315756_, Optional<Item> f_314229_) {
    public static final PotDecorations f_316418_ = new PotDecorations(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    public static final Codec<PotDecorations> f_314944_ = BuiltInRegistries.ITEM
        .byNameCodec()
        .sizeLimitedListOf(4)
        .xmap(PotDecorations::new, PotDecorations::m_324979_);
    public static final StreamCodec<RegistryFriendlyByteBuf, PotDecorations> f_315461_ = ByteBufCodecs.m_320159_(Registries.ITEM)
        .m_321801_(ByteBufCodecs.m_319259_(4))
        .m_323038_(PotDecorations::new, PotDecorations::m_324979_);

    private PotDecorations(List<Item> p_331996_) {
        this(m_320791_(p_331996_, 0), m_320791_(p_331996_, 1), m_320791_(p_331996_, 2), m_320791_(p_331996_, 3));
    }

    public PotDecorations(Item p_335624_, Item p_333843_, Item p_334423_, Item p_332271_) {
        this(List.of(p_335624_, p_333843_, p_334423_, p_332271_));
    }

    private static Optional<Item> m_320791_(List<Item> p_329359_, int p_331055_) {
        if (p_331055_ >= p_329359_.size()) {
            return Optional.empty();
        } else {
            Item item = p_329359_.get(p_331055_);
            return item == Items.BRICK ? Optional.empty() : Optional.of(item);
        }
    }

    public CompoundTag m_319081_(CompoundTag p_332345_) {
        if (this.equals(f_316418_)) {
            return p_332345_;
        } else {
            p_332345_.put("sherds", f_314944_.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
            return p_332345_;
        }
    }

    public List<Item> m_324979_() {
        return Stream.of(this.f_316149_, this.f_315094_, this.f_315756_, this.f_314229_).map(p_330456_ -> p_330456_.orElse(Items.BRICK)).toList();
    }

    public static PotDecorations m_319296_(@Nullable CompoundTag p_334784_) {
        return p_334784_ != null && p_334784_.contains("sherds")
            ? f_314944_.parse(NbtOps.INSTANCE, p_334784_.get("sherds")).result().orElse(f_316418_)
            : f_316418_;
    }
}