package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SkullBlock extends AbstractSkullBlock {
    public static final MapCodec<SkullBlock> f_303836_ = RecordCodecBuilder.mapCodec(
        p_311084_ -> p_311084_.group(SkullBlock.Type.f_302410_.fieldOf("kind").forGetter(AbstractSkullBlock::getType), m_305607_())
                .apply(p_311084_, SkullBlock::new)
    );
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    protected static final VoxelShape PIGLIN_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

    @Override
    public MapCodec<? extends SkullBlock> m_304657_() {
        return f_303836_;
    }

    public SkullBlock(SkullBlock.Type pType, BlockBehaviour.Properties pProperties) {
        super(pType, pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(ROTATION, Integer.valueOf(0)));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.getType() == SkullBlock.Types.PIGLIN ? PIGLIN_SHAPE : SHAPE;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(pContext.getRotation())));
    }

    @Override
    protected BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(ROTATION, Integer.valueOf(pRotation.rotate(pState.getValue(ROTATION), ROTATIONS)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(ROTATION, Integer.valueOf(pMirror.mirror(pState.getValue(ROTATION), ROTATIONS)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(ROTATION);
    }

    public interface Type extends StringRepresentable {
        Map<String, SkullBlock.Type> f_303019_ = new Object2ObjectArrayMap<>();
        Codec<SkullBlock.Type> f_302410_ = Codec.stringResolver(StringRepresentable::getSerializedName, f_303019_::get);
    }

    public static enum Types implements SkullBlock.Type {
        SKELETON("skeleton"),
        WITHER_SKELETON("wither_skeleton"),
        PLAYER("player"),
        ZOMBIE("zombie"),
        CREEPER("creeper"),
        PIGLIN("piglin"),
        DRAGON("dragon");

        private final String f_303610_;

        private Types(final String p_310892_) {
            this.f_303610_ = p_310892_;
            f_303019_.put(p_310892_, this);
        }

        @Override
        public String getSerializedName() {
            return this.f_303610_;
        }
    }
}