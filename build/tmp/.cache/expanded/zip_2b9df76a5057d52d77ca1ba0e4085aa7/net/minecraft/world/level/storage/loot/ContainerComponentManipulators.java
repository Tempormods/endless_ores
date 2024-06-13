package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemContainerContents;

public interface ContainerComponentManipulators {
    ContainerComponentManipulator<ItemContainerContents> f_316176_ = new ContainerComponentManipulator<ItemContainerContents>() {
        @Override
        public DataComponentType<ItemContainerContents> m_319799_() {
            return DataComponents.f_316065_;
        }

        public Stream<ItemStack> m_321528_(ItemContainerContents p_327822_) {
            return p_327822_.m_324244_();
        }

        public ItemContainerContents m_320702_() {
            return ItemContainerContents.f_316619_;
        }

        public ItemContainerContents m_318985_(ItemContainerContents p_332953_, Stream<ItemStack> p_328345_) {
            return ItemContainerContents.m_320241_(p_328345_.toList());
        }
    };
    ContainerComponentManipulator<BundleContents> f_315613_ = new ContainerComponentManipulator<BundleContents>() {
        @Override
        public DataComponentType<BundleContents> m_319799_() {
            return DataComponents.f_315394_;
        }

        public BundleContents m_320702_() {
            return BundleContents.f_316266_;
        }

        public Stream<ItemStack> m_321528_(BundleContents p_330782_) {
            return p_330782_.m_324878_();
        }

        public BundleContents m_318985_(BundleContents p_331239_, Stream<ItemStack> p_331370_) {
            BundleContents.Mutable bundlecontents$mutable = new BundleContents.Mutable(p_331239_).m_321086_();
            p_331370_.forEach(bundlecontents$mutable::m_319811_);
            return bundlecontents$mutable.m_322369_();
        }
    };
    ContainerComponentManipulator<ChargedProjectiles> f_314938_ = new ContainerComponentManipulator<ChargedProjectiles>() {
        @Override
        public DataComponentType<ChargedProjectiles> m_319799_() {
            return DataComponents.f_314625_;
        }

        public ChargedProjectiles m_320702_() {
            return ChargedProjectiles.f_316210_;
        }

        public Stream<ItemStack> m_321528_(ChargedProjectiles p_328278_) {
            return p_328278_.m_321623_().stream();
        }

        public ChargedProjectiles m_318985_(ChargedProjectiles p_329938_, Stream<ItemStack> p_330328_) {
            return ChargedProjectiles.m_322388_(p_330328_.toList());
        }
    };
    Map<DataComponentType<?>, ContainerComponentManipulator<?>> f_317071_ = Stream.of(f_316176_, f_315613_, f_314938_)
        .collect(Collectors.toMap(ContainerComponentManipulator::m_319799_, p_329998_ -> (ContainerComponentManipulator<?>)p_329998_));
    Codec<ContainerComponentManipulator<?>> f_315037_ = BuiltInRegistries.f_315333_.byNameCodec().comapFlatMap(p_328982_ -> {
        ContainerComponentManipulator<?> containercomponentmanipulator = f_317071_.get(p_328982_);
        return containercomponentmanipulator != null ? DataResult.success(containercomponentmanipulator) : DataResult.error(() -> "No items in component");
    }, ContainerComponentManipulator::m_319799_);
}