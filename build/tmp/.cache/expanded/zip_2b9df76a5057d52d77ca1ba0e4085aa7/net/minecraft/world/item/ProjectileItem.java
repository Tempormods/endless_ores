package net.minecraft.world.item;

import java.util.OptionalInt;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public interface ProjectileItem {
    Projectile m_319847_(Level p_329689_, Position p_329462_, ItemStack p_328976_, Direction p_329211_);

    default ProjectileItem.DispenseConfig m_320420_() {
        return ProjectileItem.DispenseConfig.f_316643_;
    }

    default void m_319015_(Projectile p_328685_, double p_328692_, double p_328907_, double p_334180_, float p_333007_, float p_331671_) {
        p_328685_.shoot(p_328692_, p_328907_, p_334180_, p_333007_, p_331671_);
    }

    public static record DispenseConfig(ProjectileItem.PositionFunction f_316313_, float f_315383_, float f_317028_, OptionalInt f_314791_) {
        public static final ProjectileItem.DispenseConfig f_316643_ = m_321505_().m_321407_();

        public static ProjectileItem.DispenseConfig.Builder m_321505_() {
            return new ProjectileItem.DispenseConfig.Builder();
        }

        public static class Builder {
            private ProjectileItem.PositionFunction f_316772_ = (p_331972_, p_327694_) -> DispenserBlock.m_321992_(p_331972_, 0.7, new Vec3(0.0, 0.1, 0.0));
            private float f_315860_ = 6.0F;
            private float f_317002_ = 1.1F;
            private OptionalInt f_313955_ = OptionalInt.empty();

            public ProjectileItem.DispenseConfig.Builder m_321513_(ProjectileItem.PositionFunction p_328427_) {
                this.f_316772_ = p_328427_;
                return this;
            }

            public ProjectileItem.DispenseConfig.Builder m_324742_(float p_328001_) {
                this.f_315860_ = p_328001_;
                return this;
            }

            public ProjectileItem.DispenseConfig.Builder m_318910_(float p_334376_) {
                this.f_317002_ = p_334376_;
                return this;
            }

            public ProjectileItem.DispenseConfig.Builder m_323513_(int p_331932_) {
                this.f_313955_ = OptionalInt.of(p_331932_);
                return this;
            }

            public ProjectileItem.DispenseConfig m_321407_() {
                return new ProjectileItem.DispenseConfig(this.f_316772_, this.f_315860_, this.f_317002_, this.f_313955_);
            }
        }
    }

    @FunctionalInterface
    public interface PositionFunction {
        Position m_323176_(BlockSource p_332931_, Direction p_333506_);
    }
}