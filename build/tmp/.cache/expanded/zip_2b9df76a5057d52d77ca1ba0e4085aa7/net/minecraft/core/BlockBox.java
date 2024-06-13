package net.minecraft.core;

import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;

public record BlockBox(BlockPos f_315022_, BlockPos f_315343_) implements Iterable<BlockPos> {
    public static final StreamCodec<ByteBuf, BlockBox> f_314337_ = new StreamCodec<ByteBuf, BlockBox>() {
        public BlockBox m_318688_(ByteBuf p_328358_) {
            return new BlockBox(FriendlyByteBuf.m_319748_(p_328358_), FriendlyByteBuf.m_319748_(p_328358_));
        }

        public void m_318638_(ByteBuf p_335006_, BlockBox p_331887_) {
            FriendlyByteBuf.m_323314_(p_335006_, p_331887_.f_315022_());
            FriendlyByteBuf.m_323314_(p_335006_, p_331887_.f_315343_());
        }
    };

    public BlockBox(final BlockPos f_315022_, final BlockPos f_315343_) {
        this.f_315022_ = BlockPos.m_319889_(f_315022_, f_315343_);
        this.f_315343_ = BlockPos.m_323725_(f_315022_, f_315343_);
    }

    public static BlockBox m_321337_(BlockPos p_333581_) {
        return new BlockBox(p_333581_, p_333581_);
    }

    public static BlockBox m_318760_(BlockPos p_333861_, BlockPos p_330004_) {
        return new BlockBox(p_333861_, p_330004_);
    }

    public BlockBox m_322874_(BlockPos p_330504_) {
        return new BlockBox(BlockPos.m_319889_(this.f_315022_, p_330504_), BlockPos.m_323725_(this.f_315343_, p_330504_));
    }

    public boolean m_320598_() {
        return this.f_315022_.equals(this.f_315343_);
    }

    public boolean m_323467_(BlockPos p_327940_) {
        return p_327940_.getX() >= this.f_315022_.getX()
            && p_327940_.getY() >= this.f_315022_.getY()
            && p_327940_.getZ() >= this.f_315022_.getZ()
            && p_327940_.getX() <= this.f_315343_.getX()
            && p_327940_.getY() <= this.f_315343_.getY()
            && p_327940_.getZ() <= this.f_315343_.getZ();
    }

    public AABB m_322580_() {
        return AABB.m_307411_(this.f_315022_, this.f_315343_);
    }

    @Override
    public Iterator<BlockPos> iterator() {
        return BlockPos.betweenClosed(this.f_315022_, this.f_315343_).iterator();
    }

    public int m_323830_() {
        return this.f_315343_.getX() - this.f_315022_.getX() + 1;
    }

    public int m_322159_() {
        return this.f_315343_.getY() - this.f_315022_.getY() + 1;
    }

    public int m_321501_() {
        return this.f_315343_.getZ() - this.f_315022_.getZ() + 1;
    }

    public BlockBox m_320451_(Direction p_336349_, int p_329831_) {
        if (p_329831_ == 0) {
            return this;
        } else {
            return p_336349_.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? m_318760_(this.f_315022_, BlockPos.m_323725_(this.f_315022_, this.f_315343_.relative(p_336349_, p_329831_)))
                : m_318760_(BlockPos.m_319889_(this.f_315022_.relative(p_336349_, p_329831_), this.f_315343_), this.f_315343_);
        }
    }

    public BlockBox m_320499_(Direction p_335445_, int p_328653_) {
        return p_328653_ == 0 ? this : new BlockBox(this.f_315022_.relative(p_335445_, p_328653_), this.f_315343_.relative(p_335445_, p_328653_));
    }

    public BlockBox m_322256_(Vec3i p_327763_) {
        return new BlockBox(this.f_315022_.offset(p_327763_), this.f_315343_.offset(p_327763_));
    }
}