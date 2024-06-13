package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<SpawnerBlock> f_303471_ = m_306223_(SpawnerBlock::new);

    @Override
    public MapCodec<SpawnerBlock> m_304657_() {
        return f_303471_;
    }

    public SpawnerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SpawnerBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityType.MOB_SPAWNER, pLevel.isClientSide ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick);
    }

    @Override
    protected void spawnAfterBreak(BlockState pState, ServerLevel pLevel, BlockPos pPos, ItemStack pStack, boolean pDropExperience) {
        super.spawnAfterBreak(pState, pLevel, pPos, pStack, pDropExperience);
        if (false && pDropExperience) { // Forge: moved to getExpDrop
            int i = 15 + pLevel.random.nextInt(15) + pLevel.random.nextInt(15);
            this.popExperience(pLevel, pPos, i);
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext p_331610_, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, p_331610_, pTooltip, pFlag);
        Spawner.m_304703_(pStack, pTooltip, "SpawnData");
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
        return 15 + randomSource.nextInt(15) + randomSource.nextInt(15);
    }
}
