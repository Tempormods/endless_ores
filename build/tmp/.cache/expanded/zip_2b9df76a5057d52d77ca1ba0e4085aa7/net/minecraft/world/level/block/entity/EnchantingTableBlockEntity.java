package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable {
    public int f_315885_;
    public float f_315289_;
    public float f_316058_;
    public float f_315152_;
    public float f_316618_;
    public float f_316041_;
    public float f_314967_;
    public float f_313927_;
    public float f_317009_;
    public float f_316197_;
    private static final RandomSource f_315765_ = RandomSource.create();
    @Nullable
    private Component f_314480_;

    public EnchantingTableBlockEntity(BlockPos p_329912_, BlockState p_331662_) {
        super(BlockEntityType.ENCHANTING_TABLE, p_329912_, p_331662_);
    }

    @Override
    protected void saveAdditional(CompoundTag p_329203_, HolderLookup.Provider p_335261_) {
        super.saveAdditional(p_329203_, p_335261_);
        if (this.hasCustomName()) {
            p_329203_.putString("CustomName", Component.Serializer.toJson(this.f_314480_, p_335261_));
        }
    }

    @Override
    protected void m_318667_(CompoundTag p_333729_, HolderLookup.Provider p_333480_) {
        super.m_318667_(p_333729_, p_333480_);
        if (p_333729_.contains("CustomName", 8)) {
            this.f_314480_ = m_336414_(p_333729_.getString("CustomName"), p_333480_);
        }
    }

    public static void m_320089_(Level p_334676_, BlockPos p_332815_, BlockState p_332072_, EnchantingTableBlockEntity p_333258_) {
        p_333258_.f_314967_ = p_333258_.f_316041_;
        p_333258_.f_317009_ = p_333258_.f_313927_;
        Player player = p_334676_.getNearestPlayer(
            (double)p_332815_.getX() + 0.5, (double)p_332815_.getY() + 0.5, (double)p_332815_.getZ() + 0.5, 3.0, false
        );
        if (player != null) {
            double d0 = player.getX() - ((double)p_332815_.getX() + 0.5);
            double d1 = player.getZ() - ((double)p_332815_.getZ() + 0.5);
            p_333258_.f_316197_ = (float)Mth.atan2(d1, d0);
            p_333258_.f_316041_ += 0.1F;
            if (p_333258_.f_316041_ < 0.5F || f_315765_.nextInt(40) == 0) {
                float f1 = p_333258_.f_315152_;

                do {
                    p_333258_.f_315152_ = p_333258_.f_315152_ + (float)(f_315765_.nextInt(4) - f_315765_.nextInt(4));
                } while (f1 == p_333258_.f_315152_);
            }
        } else {
            p_333258_.f_316197_ += 0.02F;
            p_333258_.f_316041_ -= 0.1F;
        }

        while (p_333258_.f_313927_ >= (float) Math.PI) {
            p_333258_.f_313927_ -= (float) (Math.PI * 2);
        }

        while (p_333258_.f_313927_ < (float) -Math.PI) {
            p_333258_.f_313927_ += (float) (Math.PI * 2);
        }

        while (p_333258_.f_316197_ >= (float) Math.PI) {
            p_333258_.f_316197_ -= (float) (Math.PI * 2);
        }

        while (p_333258_.f_316197_ < (float) -Math.PI) {
            p_333258_.f_316197_ += (float) (Math.PI * 2);
        }

        float f2 = p_333258_.f_316197_ - p_333258_.f_313927_;

        while (f2 >= (float) Math.PI) {
            f2 -= (float) (Math.PI * 2);
        }

        while (f2 < (float) -Math.PI) {
            f2 += (float) (Math.PI * 2);
        }

        p_333258_.f_313927_ += f2 * 0.4F;
        p_333258_.f_316041_ = Mth.clamp(p_333258_.f_316041_, 0.0F, 1.0F);
        p_333258_.f_315885_++;
        p_333258_.f_316058_ = p_333258_.f_315289_;
        float f = (p_333258_.f_315152_ - p_333258_.f_315289_) * 0.4F;
        float f3 = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        p_333258_.f_316618_ = p_333258_.f_316618_ + (f - p_333258_.f_316618_) * 0.9F;
        p_333258_.f_315289_ = p_333258_.f_315289_ + p_333258_.f_316618_;
    }

    @Override
    public Component getName() {
        return (Component)(this.f_314480_ != null ? this.f_314480_ : Component.translatable("container.enchant"));
    }

    public void m_321991_(@Nullable Component p_330108_) {
        this.f_314480_ = p_330108_;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.f_314480_;
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_333936_) {
        super.m_318741_(p_333936_);
        this.f_314480_ = p_333936_.m_319293_(DataComponents.f_316016_);
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_334287_) {
        super.m_318837_(p_334287_);
        p_334287_.m_322739_(DataComponents.f_316016_, this.f_314480_);
    }

    @Override
    public void m_318942_(CompoundTag p_330630_) {
        p_330630_.remove("CustomName");
    }
}