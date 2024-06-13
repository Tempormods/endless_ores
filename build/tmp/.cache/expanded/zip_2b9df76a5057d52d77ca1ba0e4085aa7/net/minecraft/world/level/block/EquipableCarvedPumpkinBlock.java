package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EquipableCarvedPumpkinBlock extends CarvedPumpkinBlock implements Equipable {
    public static final MapCodec<EquipableCarvedPumpkinBlock> f_302913_ = m_306223_(EquipableCarvedPumpkinBlock::new);

    @Override
    public MapCodec<EquipableCarvedPumpkinBlock> m_304657_() {
        return f_302913_;
    }

    public EquipableCarvedPumpkinBlock(BlockBehaviour.Properties p_289677_) {
        super(p_289677_);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}