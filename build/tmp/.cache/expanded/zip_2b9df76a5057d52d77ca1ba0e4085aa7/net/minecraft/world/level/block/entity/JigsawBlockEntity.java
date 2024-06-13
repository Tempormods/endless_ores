package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class JigsawBlockEntity extends BlockEntity {
    public static final String TARGET = "target";
    public static final String POOL = "pool";
    public static final String JOINT = "joint";
    public static final String f_303847_ = "placement_priority";
    public static final String f_302719_ = "selection_priority";
    public static final String NAME = "name";
    public static final String FINAL_STATE = "final_state";
    private ResourceLocation name = new ResourceLocation("empty");
    private ResourceLocation target = new ResourceLocation("empty");
    private ResourceKey<StructureTemplatePool> pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"));
    private JigsawBlockEntity.JointType joint = JigsawBlockEntity.JointType.ROLLABLE;
    private String finalState = "minecraft:air";
    private int f_303759_;
    private int f_302960_;

    public JigsawBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.JIGSAW, pPos, pBlockState);
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public ResourceLocation getTarget() {
        return this.target;
    }

    public ResourceKey<StructureTemplatePool> getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public JigsawBlockEntity.JointType getJoint() {
        return this.joint;
    }

    public int m_307662_() {
        return this.f_303759_;
    }

    public int m_304756_() {
        return this.f_302960_;
    }

    public void setName(ResourceLocation pName) {
        this.name = pName;
    }

    public void setTarget(ResourceLocation pTarget) {
        this.target = pTarget;
    }

    public void setPool(ResourceKey<StructureTemplatePool> pPool) {
        this.pool = pPool;
    }

    public void setFinalState(String pFinalState) {
        this.finalState = pFinalState;
    }

    public void setJoint(JigsawBlockEntity.JointType pJoint) {
        this.joint = pJoint;
    }

    public void m_307282_(int p_312425_) {
        this.f_303759_ = p_312425_;
    }

    public void m_305981_(int p_309491_) {
        this.f_302960_ = p_309491_;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_335581_) {
        super.saveAdditional(pTag, p_335581_);
        pTag.putString("name", this.name.toString());
        pTag.putString("target", this.target.toString());
        pTag.putString("pool", this.pool.location().toString());
        pTag.putString("final_state", this.finalState);
        pTag.putString("joint", this.joint.getSerializedName());
        pTag.putInt("placement_priority", this.f_303759_);
        pTag.putInt("selection_priority", this.f_302960_);
    }

    @Override
    protected void m_318667_(CompoundTag p_331375_, HolderLookup.Provider p_332374_) {
        super.m_318667_(p_331375_, p_332374_);
        this.name = new ResourceLocation(p_331375_.getString("name"));
        this.target = new ResourceLocation(p_331375_.getString("target"));
        this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(p_331375_.getString("pool")));
        this.finalState = p_331375_.getString("final_state");
        this.joint = JigsawBlockEntity.JointType.byName(p_331375_.getString("joint"))
            .orElseGet(
                () -> JigsawBlock.getFrontFacing(this.getBlockState()).getAxis().isHorizontal()
                        ? JigsawBlockEntity.JointType.ALIGNED
                        : JigsawBlockEntity.JointType.ROLLABLE
            );
        this.f_303759_ = p_331375_.getInt("placement_priority");
        this.f_302960_ = p_331375_.getInt("selection_priority");
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_333585_) {
        return this.m_320696_(p_333585_);
    }

    public void generate(ServerLevel pLevel, int pMaxDepth, boolean pKeepJigsaws) {
        BlockPos blockpos = this.getBlockPos().relative(this.getBlockState().getValue(JigsawBlock.ORIENTATION).front());
        Registry<StructureTemplatePool> registry = pLevel.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder = registry.getHolderOrThrow(this.pool);
        JigsawPlacement.generateJigsaw(pLevel, holder, this.target, pMaxDepth, blockpos, pKeepJigsaws);
    }

    public static enum JointType implements StringRepresentable {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private final String name;

        private JointType(final String pName) {
            this.name = pName;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Optional<JigsawBlockEntity.JointType> byName(String pName) {
            return Arrays.stream(values()).filter(p_59461_ -> p_59461_.getSerializedName().equals(pName)).findFirst();
        }

        public Component getTranslatedName() {
            return Component.translatable("jigsaw_block.joint." + this.name);
        }
    }
}