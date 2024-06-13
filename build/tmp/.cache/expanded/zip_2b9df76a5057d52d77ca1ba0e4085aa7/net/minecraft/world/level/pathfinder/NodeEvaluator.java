package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class NodeEvaluator {
    protected PathfindingContext f_314620_;
    protected Mob mob;
    protected final Int2ObjectMap<Node> nodes = new Int2ObjectOpenHashMap<>();
    protected int entityWidth;
    protected int entityHeight;
    protected int entityDepth;
    protected boolean canPassDoors;
    protected boolean canOpenDoors;
    protected boolean canFloat;
    protected boolean canWalkOverFences;

    public void prepare(PathNavigationRegion pLevel, Mob pMob) {
        this.f_314620_ = new PathfindingContext(pLevel, pMob);
        this.mob = pMob;
        this.nodes.clear();
        this.entityWidth = Mth.floor(pMob.getBbWidth() + 1.0F);
        this.entityHeight = Mth.floor(pMob.getBbHeight() + 1.0F);
        this.entityDepth = Mth.floor(pMob.getBbWidth() + 1.0F);
    }

    public void done() {
        this.f_314620_ = null;
        this.mob = null;
    }

    protected Node getNode(BlockPos pPos) {
        return this.getNode(pPos.getX(), pPos.getY(), pPos.getZ());
    }

    protected Node getNode(int pX, int pY, int pZ) {
        return this.nodes.computeIfAbsent(Node.createHash(pX, pY, pZ), p_77332_ -> new Node(pX, pY, pZ));
    }

    public abstract Node getStart();

    public abstract Target m_319819_(double p_336317_, double p_334044_, double p_334139_);

    protected Target m_322518_(double p_328825_, double p_331532_, double p_333874_) {
        return new Target(this.getNode(Mth.floor(p_328825_), Mth.floor(p_331532_), Mth.floor(p_333874_)));
    }

    public abstract int getNeighbors(Node[] pOutputArray, Node pNode);

    public abstract PathType m_319854_(PathfindingContext p_335888_, int p_331986_, int p_331764_, int p_335132_, Mob p_329853_);

    public abstract PathType m_320240_(PathfindingContext p_334172_, int p_335319_, int p_333029_, int p_332756_);

    public PathType m_319718_(Mob p_330121_, BlockPos p_332460_) {
        return this.m_320240_(new PathfindingContext(p_330121_.level(), p_330121_), p_332460_.getX(), p_332460_.getY(), p_332460_.getZ());
    }

    public void setCanPassDoors(boolean pCanEnterDoors) {
        this.canPassDoors = pCanEnterDoors;
    }

    public void setCanOpenDoors(boolean pCanOpenDoors) {
        this.canOpenDoors = pCanOpenDoors;
    }

    public void setCanFloat(boolean pCanFloat) {
        this.canFloat = pCanFloat;
    }

    public void setCanWalkOverFences(boolean pCanWalkOverFences) {
        this.canWalkOverFences = pCanWalkOverFences;
    }

    public boolean canPassDoors() {
        return this.canPassDoors;
    }

    public boolean canOpenDoors() {
        return this.canOpenDoors;
    }

    public boolean canFloat() {
        return this.canFloat;
    }

    public boolean canWalkOverFences() {
        return this.canWalkOverFences;
    }

    public static boolean m_321676_(BlockState p_329628_) {
        return p_329628_.is(BlockTags.FIRE)
            || p_329628_.is(Blocks.LAVA)
            || p_329628_.is(Blocks.MAGMA_BLOCK)
            || CampfireBlock.isLitCampfire(p_329628_)
            || p_329628_.is(Blocks.LAVA_CAULDRON);
    }
}