package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AttachedStemBlock extends BushBlock {
    public static final MapCodec<AttachedStemBlock> f_303034_ = RecordCodecBuilder.mapCodec(
        p_310408_ -> p_310408_.group(
                    ResourceKey.codec(Registries.BLOCK).fieldOf("fruit").forGetter(p_309932_ -> p_309932_.fruit),
                    ResourceKey.codec(Registries.BLOCK).fieldOf("stem").forGetter(p_312475_ -> p_312475_.f_303819_),
                    ResourceKey.codec(Registries.ITEM).fieldOf("seed").forGetter(p_312517_ -> p_312517_.f_303597_),
                    m_305607_()
                )
                .apply(p_310408_, AttachedStemBlock::new)
    );
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final float AABB_OFFSET = 2.0F;
    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
        ImmutableMap.of(
            Direction.SOUTH,
            Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
            Direction.WEST,
            Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
            Direction.NORTH,
            Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
            Direction.EAST,
            Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
        )
    );
    private final ResourceKey<Block> fruit;
    private final ResourceKey<Block> f_303819_;
    private final ResourceKey<Item> f_303597_;

    @Override
    public MapCodec<AttachedStemBlock> m_304657_() {
        return f_303034_;
    }

    public AttachedStemBlock(ResourceKey<Block> p_309773_, ResourceKey<Block> p_312687_, ResourceKey<Item> p_310792_, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.f_303819_ = p_309773_;
        this.fruit = p_312687_;
        this.f_303597_ = p_310792_;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AABBS.get(pState.getValue(FACING));
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!pFacingState.m_305717_(this.fruit) && pFacing == pState.getValue(FACING)) {
            Optional<Block> optional = pLevel.registryAccess().registryOrThrow(Registries.BLOCK).getOptional(this.f_303819_);
            if (optional.isPresent()) {
                return optional.get().defaultBlockState().trySetValue(StemBlock.AGE, Integer.valueOf(7));
            }
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(Blocks.FARMLAND);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader p_313034_, BlockPos pPos, BlockState pState) {
        return new ItemStack(DataFixUtils.orElse(p_313034_.registryAccess().registryOrThrow(Registries.ITEM).getOptional(this.f_303597_), this));
    }

    @Override
    protected BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}