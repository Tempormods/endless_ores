package net.minecraft.world.item.component;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public final class CustomData {
    private static final Logger f_336410_ = LogUtils.getLogger();
    public static final CustomData f_317060_ = new CustomData(new CompoundTag());
    public static final Codec<CustomData> f_314012_ = CompoundTag.CODEC.xmap(CustomData::new, p_327962_ -> p_327962_.f_316331_);
    public static final Codec<CustomData> f_316298_ = f_314012_.validate(
        p_332921_ -> p_332921_.m_323459_().contains("id", 8)
                ? DataResult.success(p_332921_)
                : DataResult.error(() -> "Missing id for entity in: " + p_332921_)
    );
    @Deprecated
    public static final StreamCodec<ByteBuf, CustomData> f_316654_ = ByteBufCodecs.f_314933_.m_323038_(CustomData::new, p_329964_ -> p_329964_.f_316331_);
    private final CompoundTag f_316331_;

    private CustomData(CompoundTag p_331981_) {
        this.f_316331_ = p_331981_;
    }

    public static CustomData m_321102_(CompoundTag p_334177_) {
        return new CustomData(p_334177_.copy());
    }

    public static Predicate<ItemStack> m_321708_(DataComponentType<CustomData> p_329049_, CompoundTag p_330570_) {
        return p_334391_ -> {
            CustomData customdata = p_334391_.m_322304_(p_329049_, f_317060_);
            return customdata.m_324111_(p_330570_);
        };
    }

    public boolean m_324111_(CompoundTag p_328523_) {
        return NbtUtils.compareNbt(p_328523_, this.f_316331_, true);
    }

    public static void m_322978_(DataComponentType<CustomData> p_336008_, ItemStack p_335562_, Consumer<CompoundTag> p_332401_) {
        CustomData customdata = p_335562_.m_322304_(p_336008_, f_317060_).m_320944_(p_332401_);
        if (customdata.f_316331_.isEmpty()) {
            p_335562_.m_319322_(p_336008_);
        } else {
            p_335562_.m_322496_(p_336008_, customdata);
        }
    }

    public static void m_323150_(DataComponentType<CustomData> p_327973_, ItemStack p_332195_, CompoundTag p_330130_) {
        if (!p_330130_.isEmpty()) {
            p_332195_.m_322496_(p_327973_, m_321102_(p_330130_));
        } else {
            p_332195_.m_319322_(p_327973_);
        }
    }

    public CustomData m_320944_(Consumer<CompoundTag> p_336344_) {
        CompoundTag compoundtag = this.f_316331_.copy();
        p_336344_.accept(compoundtag);
        return new CustomData(compoundtag);
    }

    public void m_322510_(Entity p_328148_) {
        CompoundTag compoundtag = p_328148_.saveWithoutId(new CompoundTag());
        UUID uuid = p_328148_.getUUID();
        compoundtag.merge(this.f_316331_);
        p_328148_.load(compoundtag);
        p_328148_.setUUID(uuid);
    }

    public boolean m_323254_(BlockEntity p_335855_, HolderLookup.Provider p_331192_) {
        CompoundTag compoundtag = p_335855_.m_320696_(p_331192_);
        CompoundTag compoundtag1 = compoundtag.copy();
        compoundtag.merge(this.f_316331_);
        if (!compoundtag.equals(compoundtag1)) {
            try {
                p_335855_.m_324273_(compoundtag, p_331192_);
                p_335855_.setChanged();
                return true;
            } catch (Exception exception1) {
                f_336410_.warn("Failed to apply custom data to block entity at {}", p_335855_.getBlockPos(), exception1);

                try {
                    p_335855_.m_324273_(compoundtag1, p_331192_);
                } catch (Exception exception) {
                    f_336410_.warn("Failed to rollback block entity at {} after failure", p_335855_.getBlockPos(), exception);
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public <T> DataResult<CustomData> m_323216_(MapEncoder<T> p_328479_, T p_328689_) {
        return p_328479_.encode(p_328689_, NbtOps.INSTANCE, NbtOps.INSTANCE.mapBuilder())
            .build(this.f_316331_)
            .map(p_327948_ -> new CustomData((CompoundTag)p_327948_));
    }

    public <T> DataResult<T> m_322213_(MapDecoder<T> p_333786_) {
        MapLike<Tag> maplike = NbtOps.INSTANCE.getMap((Tag)this.f_316331_).getOrThrow();
        return p_333786_.decode(NbtOps.INSTANCE, maplike);
    }

    public int m_322577_() {
        return this.f_316331_.size();
    }

    public boolean m_318976_() {
        return this.f_316331_.isEmpty();
    }

    public CompoundTag m_323330_() {
        return this.f_316331_.copy();
    }

    public boolean m_323290_(String p_331160_) {
        return this.f_316331_.contains(p_331160_);
    }

    @Override
    public boolean equals(Object p_335284_) {
        if (p_335284_ == this) {
            return true;
        } else {
            return p_335284_ instanceof CustomData customdata ? this.f_316331_.equals(customdata.f_316331_) : false;
        }
    }

    @Override
    public int hashCode() {
        return this.f_316331_.hashCode();
    }

    @Override
    public String toString() {
        return this.f_316331_.toString();
    }

    @Deprecated
    public CompoundTag m_323459_() {
        return this.f_316331_;
    }
}