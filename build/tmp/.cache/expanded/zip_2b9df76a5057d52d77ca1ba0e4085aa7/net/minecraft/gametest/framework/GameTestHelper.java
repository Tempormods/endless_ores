package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GameTestHelper implements net.minecraftforge.common.extensions.IForgeGameTestHelper {
    private final GameTestInfo testInfo;
    private boolean finalCheckAdded;

    public GameTestHelper(GameTestInfo pTestInfo) {
        this.testInfo = pTestInfo;
    }

    public ServerLevel getLevel() {
        return this.testInfo.getLevel();
    }

    public BlockState getBlockState(BlockPos pPos) {
        return this.getLevel().getBlockState(this.absolutePos(pPos));
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pPos) {
        return this.getLevel().getBlockEntity(this.absolutePos(pPos));
    }

    public void killAllEntities() {
        this.killAllEntitiesOfClass(Entity.class);
    }

    public void killAllEntitiesOfClass(Class pEntityClass) {
        AABB aabb = this.getBounds();
        List<Entity> list = this.getLevel().getEntitiesOfClass(pEntityClass, aabb.inflate(1.0), p_177131_ -> !(p_177131_ instanceof Player));
        list.forEach(Entity::kill);
    }

    public ItemEntity m_324662_(Item p_329778_, Vec3 p_334689_) {
        ServerLevel serverlevel = this.getLevel();
        Vec3 vec3 = this.absoluteVec(p_334689_);
        ItemEntity itementity = new ItemEntity(serverlevel, vec3.x, vec3.y, vec3.z, new ItemStack(p_329778_, 1));
        itementity.setDeltaMovement(0.0, 0.0, 0.0);
        serverlevel.addFreshEntity(itementity);
        return itementity;
    }

    public ItemEntity spawnItem(Item pItem, float pX, float pY, float pZ) {
        return this.m_324662_(pItem, new Vec3((double)pX, (double)pY, (double)pZ));
    }

    public ItemEntity spawnItem(Item pItem, BlockPos pPos) {
        return this.spawnItem(pItem, (float)pPos.getX(), (float)pPos.getY(), (float)pPos.getZ());
    }

    public <E extends Entity> E spawn(EntityType<E> pType, BlockPos pPos) {
        return this.spawn(pType, Vec3.atBottomCenterOf(pPos));
    }

    public <E extends Entity> E spawn(EntityType<E> pType, Vec3 pPos) {
        ServerLevel serverlevel = this.getLevel();
        E e = pType.create(serverlevel);
        if (e == null) {
            throw new NullPointerException("Failed to create entity " + pType.builtInRegistryHolder().key().location());
        } else {
            if (e instanceof Mob mob) {
                mob.setPersistenceRequired();
            }

            Vec3 vec3 = this.absoluteVec(pPos);
            e.moveTo(vec3.x, vec3.y, vec3.z, e.getYRot(), e.getXRot());
            serverlevel.addFreshEntity(e);
            return e;
        }
    }

    public <E extends Entity> E m_324009_(EntityType<E> p_333077_) {
        return this.m_322182_(p_333077_, 0, 0, 0, 2.147483647E9);
    }

    public <E extends Entity> E m_322182_(EntityType<E> p_335109_, int p_329434_, int p_334603_, int p_333149_, double p_331586_) {
        List<E> list = this.m_321062_(p_335109_, p_329434_, p_334603_, p_333149_, p_331586_);
        if (list.isEmpty()) {
            throw new GameTestAssertException("Expected " + p_335109_.toShortString() + " to exist around " + p_329434_ + "," + p_334603_ + "," + p_333149_);
        } else if (list.size() > 1) {
            throw new GameTestAssertException(
                "Expected only one "
                    + p_335109_.toShortString()
                    + " to exist around "
                    + p_329434_
                    + ","
                    + p_334603_
                    + ","
                    + p_333149_
                    + ", but found "
                    + list.size()
            );
        } else {
            Vec3 vec3 = this.absoluteVec(new Vec3((double)p_329434_, (double)p_334603_, (double)p_333149_));
            list.sort((p_325933_, p_325934_) -> {
                double d0 = p_325933_.position().distanceTo(vec3);
                double d1 = p_325934_.position().distanceTo(vec3);
                return Double.compare(d0, d1);
            });
            return list.get(0);
        }
    }

    public <E extends Entity> List<E> m_321062_(EntityType<E> p_327745_, int p_330471_, int p_329385_, int p_328777_, double p_336258_) {
        return this.m_319921_(p_327745_, Vec3.atBottomCenterOf(new BlockPos(p_330471_, p_329385_, p_328777_)), p_336258_);
    }

    public <E extends Entity> List<E> m_319921_(EntityType<E> p_327849_, Vec3 p_331515_, double p_330795_) {
        ServerLevel serverlevel = this.getLevel();
        Vec3 vec3 = this.absoluteVec(p_331515_);
        AABB aabb = this.testInfo.getStructureBounds();
        AABB aabb1 = new AABB(vec3.add(-p_330795_, -p_330795_, -p_330795_), vec3.add(p_330795_, p_330795_, p_330795_));
        return serverlevel.getEntities(p_327849_, aabb, p_325936_ -> p_325936_.getBoundingBox().intersects(aabb1) && p_325936_.isAlive());
    }

    public <E extends Entity> E spawn(EntityType<E> pType, int pX, int pY, int pZ) {
        return this.spawn(pType, new BlockPos(pX, pY, pZ));
    }

    public <E extends Entity> E spawn(EntityType<E> pType, float pX, float pY, float pZ) {
        return this.spawn(pType, new Vec3((double)pX, (double)pY, (double)pZ));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> pType, BlockPos pPos) {
        E e = (E)this.spawn(pType, pPos);
        e.removeFreeWill();
        return e;
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> pType, int pX, int pY, int pZ) {
        return this.spawnWithNoFreeWill(pType, new BlockPos(pX, pY, pZ));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> pType, Vec3 pPos) {
        E e = (E)this.spawn(pType, pPos);
        e.removeFreeWill();
        return e;
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> pType, float pX, float pY, float pZ) {
        return this.spawnWithNoFreeWill(pType, new Vec3((double)pX, (double)pY, (double)pZ));
    }

    public void m_318926_(Mob p_335410_, float p_330841_, float p_334132_, float p_332530_) {
        Vec3 vec3 = this.absoluteVec(new Vec3((double)p_330841_, (double)p_334132_, (double)p_332530_));
        p_335410_.moveTo(vec3.x, vec3.y, vec3.z, p_335410_.getYRot(), p_335410_.getXRot());
    }

    public GameTestSequence walkTo(Mob pMob, BlockPos pPos, float pSpeed) {
        return this.startSequence().thenExecuteAfter(2, () -> {
            Path path = pMob.getNavigation().createPath(this.absolutePos(pPos), 0);
            pMob.getNavigation().moveTo(path, (double)pSpeed);
        });
    }

    public void pressButton(int pX, int pY, int pZ) {
        this.pressButton(new BlockPos(pX, pY, pZ));
    }

    public void pressButton(BlockPos pPos) {
        this.assertBlockState(pPos, p_177212_ -> p_177212_.is(BlockTags.BUTTONS), () -> "Expected button");
        BlockPos blockpos = this.absolutePos(pPos);
        BlockState blockstate = this.getLevel().getBlockState(blockpos);
        ButtonBlock buttonblock = (ButtonBlock)blockstate.getBlock();
        buttonblock.press(blockstate, this.getLevel(), blockpos);
    }

    public void useBlock(BlockPos pPos) {
        this.useBlock(pPos, this.makeMockPlayer(GameType.CREATIVE));
    }

    public void useBlock(BlockPos pPos, Player pPlayer) {
        BlockPos blockpos = this.absolutePos(pPos);
        this.useBlock(pPos, pPlayer, new BlockHitResult(Vec3.atCenterOf(blockpos), Direction.NORTH, blockpos, true));
    }

    public void useBlock(BlockPos pPos, Player pPlayer, BlockHitResult pResult) {
        BlockPos blockpos = this.absolutePos(pPos);
        BlockState blockstate = this.getLevel().getBlockState(blockpos);
        InteractionHand interactionhand = InteractionHand.MAIN_HAND;
        ItemInteractionResult iteminteractionresult = blockstate.m_318730_(
            pPlayer.getItemInHand(interactionhand), this.getLevel(), pPlayer, interactionhand, pResult
        );
        if (!iteminteractionresult.m_321211_()) {
            if (iteminteractionresult != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
                || !blockstate.m_324126_(this.getLevel(), pPlayer, pResult).consumesAction()) {
                UseOnContext useoncontext = new UseOnContext(pPlayer, interactionhand, pResult);
                pPlayer.getItemInHand(interactionhand).useOn(useoncontext);
            }
        }
    }

    public LivingEntity makeAboutToDrown(LivingEntity pEntity) {
        pEntity.setAirSupply(0);
        pEntity.setHealth(0.25F);
        return pEntity;
    }

    public LivingEntity withLowHealth(LivingEntity pEntity) {
        pEntity.setHealth(0.25F);
        return pEntity;
    }

    public Player makeMockPlayer(final GameType p_333981_) {
        return new Player(this.getLevel(), BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
            /**
             * Returns {@code true} if the player is in spectator mode.
             */
            @Override
            public boolean isSpectator() {
                return p_333981_ == GameType.SPECTATOR;
            }

            @Override
            public boolean isCreative() {
                return p_333981_.isCreative();
            }

            /**
             * Returns whether this is a {@link net.minecraft.client.player.LocalPlayer}.
             */
            @Override
            public boolean isLocalPlayer() {
                return true;
            }
        };
    }

    @Deprecated(
        forRemoval = true
    )
    public ServerPlayer makeMockServerPlayerInLevel() {
        CommonListenerCookie commonlistenercookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        ServerPlayer serverplayer = new ServerPlayer(
            this.getLevel().getServer(), this.getLevel(), commonlistenercookie.gameProfile(), commonlistenercookie.clientInformation()
        ) {
            /**
             * Returns {@code true} if the player is in spectator mode.
             */
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        this.getLevel().getServer().getPlayerList().placeNewPlayer(connection, serverplayer, commonlistenercookie);
        return serverplayer;
    }

    public void pullLever(int pX, int pY, int pZ) {
        this.pullLever(new BlockPos(pX, pY, pZ));
    }

    public void pullLever(BlockPos pPos) {
        this.assertBlockPresent(Blocks.LEVER, pPos);
        BlockPos blockpos = this.absolutePos(pPos);
        BlockState blockstate = this.getLevel().getBlockState(blockpos);
        LeverBlock leverblock = (LeverBlock)blockstate.getBlock();
        leverblock.pull(blockstate, this.getLevel(), blockpos);
    }

    public void pulseRedstone(BlockPos pPos, long pDelay) {
        this.setBlock(pPos, Blocks.REDSTONE_BLOCK);
        this.runAfterDelay(pDelay, () -> this.setBlock(pPos, Blocks.AIR));
    }

    public void destroyBlock(BlockPos pPos) {
        this.getLevel().destroyBlock(this.absolutePos(pPos), false, null);
    }

    public void setBlock(int pX, int pY, int pZ, Block pBlock) {
        this.setBlock(new BlockPos(pX, pY, pZ), pBlock);
    }

    public void setBlock(int pX, int pY, int pZ, BlockState pState) {
        this.setBlock(new BlockPos(pX, pY, pZ), pState);
    }

    public void setBlock(BlockPos pPos, Block pBlock) {
        this.setBlock(pPos, pBlock.defaultBlockState());
    }

    public void setBlock(BlockPos pPos, BlockState pState) {
        this.getLevel().setBlock(this.absolutePos(pPos), pState, 3);
    }

    public void setNight() {
        this.setDayTime(13000);
    }

    public void setDayTime(int pTime) {
        this.getLevel().setDayTime((long)pTime);
    }

    public void assertBlockPresent(Block pBlock, int pX, int pY, int pZ) {
        this.assertBlockPresent(pBlock, new BlockPos(pX, pY, pZ));
    }

    public void assertBlockPresent(Block pBlock, BlockPos pPos) {
        BlockState blockstate = this.getBlockState(pPos);
        this.assertBlock(
            pPos,
            p_177216_ -> blockstate.is(pBlock),
            "Expected " + pBlock.getName().getString() + ", got " + blockstate.getBlock().getName().getString()
        );
    }

    public void assertBlockNotPresent(Block pBlock, int pX, int pY, int pZ) {
        this.assertBlockNotPresent(pBlock, new BlockPos(pX, pY, pZ));
    }

    public void assertBlockNotPresent(Block pBlock, BlockPos pPos) {
        this.assertBlock(pPos, p_177251_ -> !this.getBlockState(pPos).is(pBlock), "Did not expect " + pBlock.getName().getString());
    }

    public void succeedWhenBlockPresent(Block pBlock, int pX, int pY, int pZ) {
        this.succeedWhenBlockPresent(pBlock, new BlockPos(pX, pY, pZ));
    }

    public void succeedWhenBlockPresent(Block pBlock, BlockPos pPos) {
        this.succeedWhen(() -> this.assertBlockPresent(pBlock, pPos));
    }

    public void assertBlock(BlockPos pPos, Predicate<Block> pPredicate, String pExceptionMessage) {
        this.assertBlock(pPos, pPredicate, () -> pExceptionMessage);
    }

    public void assertBlock(BlockPos pPos, Predicate<Block> pPredicate, Supplier<String> pExceptionMessage) {
        this.assertBlockState(pPos, p_177296_ -> pPredicate.test(p_177296_.getBlock()), pExceptionMessage);
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPos pPos, Property<T> pProperty, T pValue) {
        BlockState blockstate = this.getBlockState(pPos);
        boolean flag = blockstate.hasProperty(pProperty);
        if (!flag || !blockstate.<T>getValue(pProperty).equals(pValue)) {
            String s = flag ? "was " + blockstate.getValue(pProperty) : "property " + pProperty.getName() + " is missing";
            String s1 = String.format(Locale.ROOT, "Expected property %s to be %s, %s", pProperty.getName(), pValue, s);
            throw new GameTestAssertPosException(s1, this.absolutePos(pPos), pPos, this.testInfo.getTick());
        }
    }

    public <T extends Comparable<T>> void assertBlockProperty(BlockPos pPos, Property<T> pProperty, Predicate<T> pPredicate, String pExceptionMessage) {
        this.assertBlockState(pPos, p_277264_ -> {
            if (!p_277264_.hasProperty(pProperty)) {
                return false;
            } else {
                T t = p_277264_.getValue(pProperty);
                return pPredicate.test(t);
            }
        }, () -> pExceptionMessage);
    }

    public void assertBlockState(BlockPos pPos, Predicate<BlockState> pPredicate, Supplier<String> pExceptionMessage) {
        BlockState blockstate = this.getBlockState(pPos);
        if (!pPredicate.test(blockstate)) {
            throw new GameTestAssertPosException(pExceptionMessage.get(), this.absolutePos(pPos), pPos, this.testInfo.getTick());
        }
    }

    public void assertRedstoneSignal(BlockPos pPos, Direction pDirection, IntPredicate pSignalStrengthPredicate, Supplier<String> pExceptionMessage) {
        BlockPos blockpos = this.absolutePos(pPos);
        ServerLevel serverlevel = this.getLevel();
        BlockState blockstate = serverlevel.getBlockState(blockpos);
        int i = blockstate.getSignal(serverlevel, blockpos, pDirection);
        if (!pSignalStrengthPredicate.test(i)) {
            throw new GameTestAssertPosException(pExceptionMessage.get(), blockpos, pPos, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> pType) {
        List<? extends Entity> list = this.getLevel().getEntities(pType, this.getBounds(), Entity::isAlive);
        if (list.isEmpty()) {
            throw new GameTestAssertException("Expected " + pType.toShortString() + " to exist");
        }
    }

    public void assertEntityPresent(EntityType<?> pType, int pX, int pY, int pZ) {
        this.assertEntityPresent(pType, new BlockPos(pX, pY, pZ));
    }

    public void assertEntityPresent(EntityType<?> pType, BlockPos pPos) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<? extends Entity> list = this.getLevel().getEntities(pType, new AABB(blockpos), Entity::isAlive);
        if (list.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + pType.toShortString(), blockpos, pPos, this.testInfo.getTick());
        }
    }

    public void assertEntityPresent(EntityType<?> pEntityType, Vec3 pStartPos, Vec3 pEndPos) {
        List<? extends Entity> list = this.getLevel().getEntities(pEntityType, new AABB(pStartPos, pEndPos), Entity::isAlive);
        if (list.isEmpty()) {
            throw new GameTestAssertPosException(
                "Expected " + pEntityType.toShortString() + " between ", BlockPos.containing(pStartPos), BlockPos.containing(pEndPos), this.testInfo.getTick()
            );
        }
    }

    public void m_304701_(EntityType<?> p_313026_, int p_310037_) {
        List<? extends Entity> list = this.getLevel().getEntities(p_313026_, this.getBounds(), Entity::isAlive);
        if (list.size() != p_310037_) {
            throw new GameTestAssertException("Expected " + p_310037_ + " of type " + p_313026_.toShortString() + " to exist, found " + list.size());
        }
    }

    public void assertEntitiesPresent(EntityType<?> pEntityType, BlockPos pPos, int pCount, double pRadius) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<? extends Entity> list = this.getEntities((EntityType<? extends Entity>)pEntityType, pPos, pRadius);
        if (list.size() != pCount) {
            throw new GameTestAssertPosException(
                "Expected " + pCount + " entities of type " + pEntityType.toShortString() + ", actual number of entities found=" + list.size(),
                blockpos,
                pPos,
                this.testInfo.getTick()
            );
        }
    }

    public void assertEntityPresent(EntityType<?> pType, BlockPos pPos, double pExpansionAmount) {
        List<? extends Entity> list = this.getEntities((EntityType<? extends Entity>)pType, pPos, pExpansionAmount);
        if (list.isEmpty()) {
            BlockPos blockpos = this.absolutePos(pPos);
            throw new GameTestAssertPosException("Expected " + pType.toShortString(), blockpos, pPos, this.testInfo.getTick());
        }
    }

    public <T extends Entity> List<T> getEntities(EntityType<T> pEntityType, BlockPos pPos, double pRadius) {
        BlockPos blockpos = this.absolutePos(pPos);
        return this.getLevel().getEntities(pEntityType, new AABB(blockpos).inflate(pRadius), Entity::isAlive);
    }

    public <T extends Entity> List<T> m_318662_(EntityType<T> p_330219_) {
        return this.getLevel().getEntities(p_330219_, this.getBounds(), Entity::isAlive);
    }

    public void assertEntityInstancePresent(Entity pEntity, int pX, int pY, int pZ) {
        this.assertEntityInstancePresent(pEntity, new BlockPos(pX, pY, pZ));
    }

    public void assertEntityInstancePresent(Entity pEntity, BlockPos pPos) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<? extends Entity> list = this.getLevel().getEntities(pEntity.getType(), new AABB(blockpos), Entity::isAlive);
        list.stream()
            .filter(p_177139_ -> p_177139_ == pEntity)
            .findFirst()
            .orElseThrow(() -> new GameTestAssertPosException("Expected " + pEntity.getType().toShortString(), blockpos, pPos, this.testInfo.getTick()));
    }

    public void assertItemEntityCountIs(Item pItem, BlockPos pPos, double pExpansionAmount, int pCount) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<ItemEntity> list = this.getLevel().getEntities(EntityType.ITEM, new AABB(blockpos).inflate(pExpansionAmount), Entity::isAlive);
        int i = 0;

        for (ItemEntity itementity : list) {
            ItemStack itemstack = itementity.getItem();
            if (itemstack.is(pItem)) {
                i += itemstack.getCount();
            }
        }

        if (i != pCount) {
            throw new GameTestAssertPosException(
                "Expected " + pCount + " " + pItem.getDescription().getString() + " items to exist (found " + i + ")",
                blockpos,
                pPos,
                this.testInfo.getTick()
            );
        }
    }

    public void assertItemEntityPresent(Item pItem, BlockPos pPos, double pExpansionAmount) {
        BlockPos blockpos = this.absolutePos(pPos);

        for (Entity entity : this.getLevel().getEntities(EntityType.ITEM, new AABB(blockpos).inflate(pExpansionAmount), Entity::isAlive)) {
            ItemEntity itementity = (ItemEntity)entity;
            if (itementity.getItem().getItem().equals(pItem)) {
                return;
            }
        }

        throw new GameTestAssertPosException("Expected " + pItem.getDescription().getString() + " item", blockpos, pPos, this.testInfo.getTick());
    }

    public void assertItemEntityNotPresent(Item pItem, BlockPos pPos, double pRadius) {
        BlockPos blockpos = this.absolutePos(pPos);

        for (Entity entity : this.getLevel().getEntities(EntityType.ITEM, new AABB(blockpos).inflate(pRadius), Entity::isAlive)) {
            ItemEntity itementity = (ItemEntity)entity;
            if (itementity.getItem().getItem().equals(pItem)) {
                throw new GameTestAssertPosException(
                    "Did not expect " + pItem.getDescription().getString() + " item", blockpos, pPos, this.testInfo.getTick()
                );
            }
        }
    }

    public void m_307970_(Item p_310630_) {
        for (Entity entity : this.getLevel().getEntities(EntityType.ITEM, this.getBounds(), Entity::isAlive)) {
            ItemEntity itementity = (ItemEntity)entity;
            if (itementity.getItem().getItem().equals(p_310630_)) {
                return;
            }
        }

        throw new GameTestAssertException("Expected " + p_310630_.getDescription().getString() + " item");
    }

    public void m_304884_(Item p_312600_) {
        for (Entity entity : this.getLevel().getEntities(EntityType.ITEM, this.getBounds(), Entity::isAlive)) {
            ItemEntity itementity = (ItemEntity)entity;
            if (itementity.getItem().getItem().equals(p_312600_)) {
                throw new GameTestAssertException("Did not expect " + p_312600_.getDescription().getString() + " item");
            }
        }
    }

    public void assertEntityNotPresent(EntityType<?> pType) {
        List<? extends Entity> list = this.getLevel().getEntities(pType, this.getBounds(), Entity::isAlive);
        if (!list.isEmpty()) {
            throw new GameTestAssertException("Did not expect " + pType.toShortString() + " to exist");
        }
    }

    public void assertEntityNotPresent(EntityType<?> pType, int pX, int pY, int pZ) {
        this.assertEntityNotPresent(pType, new BlockPos(pX, pY, pZ));
    }

    public void assertEntityNotPresent(EntityType<?> pType, BlockPos pPos) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<? extends Entity> list = this.getLevel().getEntities(pType, new AABB(blockpos), Entity::isAlive);
        if (!list.isEmpty()) {
            throw new GameTestAssertPosException("Did not expect " + pType.toShortString(), blockpos, pPos, this.testInfo.getTick());
        }
    }

    public void m_319826_(EntityType<?> p_328558_, Vec3 p_334197_, Vec3 p_335309_) {
        List<? extends Entity> list = this.getLevel().getEntities(p_328558_, new AABB(p_334197_, p_335309_), Entity::isAlive);
        if (!list.isEmpty()) {
            throw new GameTestAssertPosException(
                "Did not expect " + p_328558_.toShortString() + " between ",
                BlockPos.containing(p_334197_),
                BlockPos.containing(p_335309_),
                this.testInfo.getTick()
            );
        }
    }

    public void assertEntityTouching(EntityType<?> pType, double pX, double pY, double pZ) {
        Vec3 vec3 = new Vec3(pX, pY, pZ);
        Vec3 vec31 = this.absoluteVec(vec3);
        Predicate<? super Entity> predicate = p_177346_ -> p_177346_.getBoundingBox().intersects(vec31, vec31);
        List<? extends Entity> list = this.getLevel().getEntities(pType, this.getBounds(), predicate);
        if (list.isEmpty()) {
            throw new GameTestAssertException("Expected " + pType.toShortString() + " to touch " + vec31 + " (relative " + vec3 + ")");
        }
    }

    public void assertEntityNotTouching(EntityType<?> pType, double pX, double pY, double pZ) {
        Vec3 vec3 = new Vec3(pX, pY, pZ);
        Vec3 vec31 = this.absoluteVec(vec3);
        Predicate<? super Entity> predicate = p_177231_ -> !p_177231_.getBoundingBox().intersects(vec31, vec31);
        List<? extends Entity> list = this.getLevel().getEntities(pType, this.getBounds(), predicate);
        if (list.isEmpty()) {
            throw new GameTestAssertException("Did not expect " + pType.toShortString() + " to touch " + vec31 + " (relative " + vec3 + ")");
        }
    }

    public <E extends Entity, T> void assertEntityData(BlockPos pPos, EntityType<E> pType, Function<? super E, T> pEntityDataGetter, @Nullable T pTestEntityData) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<E> list = this.getLevel().getEntities(pType, new AABB(blockpos), Entity::isAlive);
        if (list.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + pType.toShortString(), blockpos, pPos, this.testInfo.getTick());
        } else {
            for (E e : list) {
                T t = pEntityDataGetter.apply(e);
                if (t == null) {
                    if (pTestEntityData != null) {
                        throw new GameTestAssertException("Expected entity data to be: " + pTestEntityData + ", but was: " + t);
                    }
                } else if (!t.equals(pTestEntityData)) {
                    throw new GameTestAssertException("Expected entity data to be: " + pTestEntityData + ", but was: " + t);
                }
            }
        }
    }

    public <E extends LivingEntity> void assertEntityIsHolding(BlockPos pPos, EntityType<E> pEntityType, Item pItem) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<E> list = this.getLevel().getEntities(pEntityType, new AABB(blockpos), Entity::isAlive);
        if (list.isEmpty()) {
            throw new GameTestAssertPosException("Expected entity of type: " + pEntityType, blockpos, pPos, this.getTick());
        } else {
            for (E e : list) {
                if (e.isHolding(pItem)) {
                    return;
                }
            }

            throw new GameTestAssertPosException("Entity should be holding: " + pItem, blockpos, pPos, this.getTick());
        }
    }

    public <E extends Entity & InventoryCarrier> void assertEntityInventoryContains(BlockPos pPos, EntityType<E> pEntityType, Item pItem) {
        BlockPos blockpos = this.absolutePos(pPos);
        List<E> list = this.getLevel().getEntities(pEntityType, new AABB(blockpos), p_263479_ -> p_263479_.isAlive());
        if (list.isEmpty()) {
            throw new GameTestAssertPosException("Expected " + pEntityType.toShortString() + " to exist", blockpos, pPos, this.getTick());
        } else {
            for (E e : list) {
                if (e.getInventory().hasAnyMatching(p_263481_ -> p_263481_.is(pItem))) {
                    return;
                }
            }

            throw new GameTestAssertPosException("Entity inventory should contain: " + pItem, blockpos, pPos, this.getTick());
        }
    }

    public void assertContainerEmpty(BlockPos pPos) {
        BlockPos blockpos = this.absolutePos(pPos);
        BlockEntity blockentity = this.getLevel().getBlockEntity(blockpos);
        if (blockentity instanceof BaseContainerBlockEntity && !((BaseContainerBlockEntity)blockentity).isEmpty()) {
            throw new GameTestAssertException("Container should be empty");
        }
    }

    public void assertContainerContains(BlockPos pPos, Item pItem) {
        BlockPos blockpos = this.absolutePos(pPos);
        BlockEntity blockentity = this.getLevel().getBlockEntity(blockpos);
        if (!(blockentity instanceof BaseContainerBlockEntity)) {
            throw new GameTestAssertException("Expected a container at " + pPos + ", found " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockentity.getType()));
        } else if (((BaseContainerBlockEntity)blockentity).countItem(pItem) != 1) {
            throw new GameTestAssertException("Container should contain: " + pItem);
        }
    }

    public void assertSameBlockStates(BoundingBox pBoundingBox, BlockPos pPos) {
        BlockPos.betweenClosedStream(pBoundingBox)
            .forEach(
                p_177267_ -> {
                    BlockPos blockpos = pPos.offset(
                        p_177267_.getX() - pBoundingBox.minX(),
                        p_177267_.getY() - pBoundingBox.minY(),
                        p_177267_.getZ() - pBoundingBox.minZ()
                    );
                    this.assertSameBlockState(p_177267_, blockpos);
                }
            );
    }

    public void assertSameBlockState(BlockPos pTestPos, BlockPos pComparisonPos) {
        BlockState blockstate = this.getBlockState(pTestPos);
        BlockState blockstate1 = this.getBlockState(pComparisonPos);
        if (blockstate != blockstate1) {
            this.fail("Incorrect state. Expected " + blockstate1 + ", got " + blockstate, pTestPos);
        }
    }

    public void assertAtTickTimeContainerContains(long pTickTime, BlockPos pPos, Item pItem) {
        this.runAtTickTime(pTickTime, () -> this.assertContainerContains(pPos, pItem));
    }

    public void assertAtTickTimeContainerEmpty(long pTickTime, BlockPos pPos) {
        this.runAtTickTime(pTickTime, () -> this.assertContainerEmpty(pPos));
    }

    public <E extends Entity, T> void succeedWhenEntityData(BlockPos pPos, EntityType<E> pType, Function<E, T> pEntityDataGetter, T pTestEntityData) {
        this.succeedWhen(() -> this.assertEntityData(pPos, pType, pEntityDataGetter, pTestEntityData));
    }

    public <E extends Entity> void assertEntityProperty(E pEntity, Predicate<E> pPredicate, String pName) {
        if (!pPredicate.test(pEntity)) {
            throw new GameTestAssertException("Entity " + pEntity + " failed " + pName + " test");
        }
    }

    public <E extends Entity, T> void assertEntityProperty(E pEntity, Function<E, T> pEntityPropertyGetter, String pValueName, T pTestEntityProperty) {
        T t = pEntityPropertyGetter.apply(pEntity);
        if (!t.equals(pTestEntityProperty)) {
            throw new GameTestAssertException("Entity " + pEntity + " value " + pValueName + "=" + t + " is not equal to expected " + pTestEntityProperty);
        }
    }

    public void assertLivingEntityHasMobEffect(LivingEntity pEntity, Holder<MobEffect> p_331754_, int pAmplifier) {
        MobEffectInstance mobeffectinstance = pEntity.getEffect(p_331754_);
        if (mobeffectinstance == null || mobeffectinstance.getAmplifier() != pAmplifier) {
            int i = pAmplifier + 1;
            throw new GameTestAssertException("Entity " + pEntity + " failed has " + p_331754_.value().getDescriptionId() + " x " + i + " test");
        }
    }

    public void succeedWhenEntityPresent(EntityType<?> pType, int pX, int pY, int pZ) {
        this.succeedWhenEntityPresent(pType, new BlockPos(pX, pY, pZ));
    }

    public void succeedWhenEntityPresent(EntityType<?> pType, BlockPos pPos) {
        this.succeedWhen(() -> this.assertEntityPresent(pType, pPos));
    }

    public void succeedWhenEntityNotPresent(EntityType<?> pType, int pX, int pY, int pZ) {
        this.succeedWhenEntityNotPresent(pType, new BlockPos(pX, pY, pZ));
    }

    public void succeedWhenEntityNotPresent(EntityType<?> pType, BlockPos pPos) {
        this.succeedWhen(() -> this.assertEntityNotPresent(pType, pPos));
    }

    public void succeed() {
        this.testInfo.succeed();
    }

    private void ensureSingleFinalCheck() {
        if (this.finalCheckAdded) {
            throw new IllegalStateException("This test already has final clause");
        } else {
            this.finalCheckAdded = true;
        }
    }

    public void succeedIf(Runnable pCriterion) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(0L, pCriterion).thenSucceed();
    }

    public void succeedWhen(Runnable pCriterion) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil(pCriterion).thenSucceed();
    }

    public void succeedOnTickWhen(int pTick, Runnable pCriterion) {
        this.ensureSingleFinalCheck();
        this.testInfo.createSequence().thenWaitUntil((long)pTick, pCriterion).thenSucceed();
    }

    public void runAtTickTime(long pTickTime, Runnable pTask) {
        this.testInfo.setRunAtTickTime(pTickTime, pTask);
    }

    public void runAfterDelay(long pDelay, Runnable pTask) {
        this.runAtTickTime(this.testInfo.getTick() + pDelay, pTask);
    }

    public void randomTick(BlockPos pPos) {
        BlockPos blockpos = this.absolutePos(pPos);
        ServerLevel serverlevel = this.getLevel();
        serverlevel.getBlockState(blockpos).randomTick(serverlevel, blockpos, serverlevel.random);
    }

    public void m_305517_(BlockPos p_311105_) {
        BlockPos blockpos = this.absolutePos(p_311105_);
        ServerLevel serverlevel = this.getLevel();
        serverlevel.tickIceAndSnow(blockpos);
    }

    public void m_305614_() {
        AABB aabb = this.getRelativeBounds();
        int i = (int)Math.floor(aabb.maxX);
        int j = (int)Math.floor(aabb.maxZ);
        int k = (int)Math.floor(aabb.maxY);

        for (int l = (int)Math.floor(aabb.minX); l < i; l++) {
            for (int i1 = (int)Math.floor(aabb.minZ); i1 < j; i1++) {
                this.m_305517_(new BlockPos(l, k, i1));
            }
        }
    }

    public int getHeight(Heightmap.Types pHeightmapType, int pX, int pZ) {
        BlockPos blockpos = this.absolutePos(new BlockPos(pX, 0, pZ));
        return this.relativePos(this.getLevel().getHeightmapPos(pHeightmapType, blockpos)).getY();
    }

    public void fail(String pExceptionMessage, BlockPos pPos) {
        throw new GameTestAssertPosException(pExceptionMessage, this.absolutePos(pPos), pPos, this.getTick());
    }

    public void fail(String pExceptionMessage, Entity pEntity) {
        throw new GameTestAssertPosException(pExceptionMessage, pEntity.blockPosition(), this.relativePos(pEntity.blockPosition()), this.getTick());
    }

    public void fail(String pExceptionMessage) {
        throw new GameTestAssertException(pExceptionMessage);
    }

    public void failIf(Runnable pCriterion) {
        this.testInfo.createSequence().thenWaitUntil(pCriterion).thenFail(() -> new GameTestAssertException("Fail conditions met"));
    }

    public void failIfEver(Runnable pCriterion) {
        LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks())
            .forEach(p_177365_ -> this.testInfo.setRunAtTickTime(p_177365_, pCriterion::run));
    }

    public GameTestSequence startSequence() {
        return this.testInfo.createSequence();
    }

    public BlockPos absolutePos(BlockPos pPos) {
        BlockPos blockpos = this.testInfo.getStructureBlockPos();
        BlockPos blockpos1 = blockpos.offset(pPos);
        return StructureTemplate.transform(blockpos1, Mirror.NONE, this.testInfo.getRotation(), blockpos);
    }

    public BlockPos relativePos(BlockPos pPos) {
        BlockPos blockpos = this.testInfo.getStructureBlockPos();
        Rotation rotation = this.testInfo.getRotation().getRotated(Rotation.CLOCKWISE_180);
        BlockPos blockpos1 = StructureTemplate.transform(pPos, Mirror.NONE, rotation, blockpos);
        return blockpos1.subtract(blockpos);
    }

    public Vec3 absoluteVec(Vec3 pRelativeVec3) {
        Vec3 vec3 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
        return StructureTemplate.transform(vec3.add(pRelativeVec3), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public Vec3 relativeVec(Vec3 pAbsoluteVec3) {
        Vec3 vec3 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
        return StructureTemplate.transform(pAbsoluteVec3.subtract(vec3), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
    }

    public void assertTrue(boolean pCondition, String pFailureMessage) {
        if (!pCondition) {
            throw new GameTestAssertException(pFailureMessage);
        }
    }

    public <N> void m_322431_(N p_328559_, N p_332683_, String p_336245_) {
        if (!p_328559_.equals(p_332683_)) {
            throw new GameTestAssertException("Expected " + p_336245_ + " to be " + p_332683_ + ", but was " + p_328559_);
        }
    }

    public void assertFalse(boolean pCondition, String pFailureMessage) {
        if (pCondition) {
            throw new GameTestAssertException(pFailureMessage);
        }
    }

    public long getTick() {
        return this.testInfo.getTick();
    }

    public AABB getBounds() {
        return this.testInfo.getStructureBounds();
    }

    private AABB getRelativeBounds() {
        AABB aabb = this.testInfo.getStructureBounds();
        return aabb.move(BlockPos.ZERO.subtract(this.absolutePos(BlockPos.ZERO)));
    }

    public void forEveryBlockInStructure(Consumer<BlockPos> pConsumer) {
        AABB aabb = this.getRelativeBounds().contract(1.0, 1.0, 1.0);
        BlockPos.MutableBlockPos.betweenClosedStream(aabb).forEach(pConsumer);
    }

    public void onEachTick(Runnable pTask) {
        LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks())
            .forEach(p_177283_ -> this.testInfo.setRunAtTickTime(p_177283_, pTask::run));
    }

    public void placeAt(Player pPlayer, ItemStack pStack, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = this.absolutePos(pPos.relative(pDirection));
        BlockHitResult blockhitresult = new BlockHitResult(Vec3.atCenterOf(blockpos), pDirection, blockpos, false);
        UseOnContext useoncontext = new UseOnContext(pPlayer, InteractionHand.MAIN_HAND, blockhitresult);
        pStack.useOn(useoncontext);
    }

    public void m_304814_(ResourceKey<Biome> p_312755_) {
        AABB aabb = this.getBounds();
        BlockPos blockpos = BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ);
        BlockPos blockpos1 = BlockPos.containing(aabb.maxX, aabb.maxY, aabb.maxZ);
        Either<Integer, CommandSyntaxException> either = FillBiomeCommand.m_307048_(
            this.getLevel(), blockpos, blockpos1, this.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(p_312755_)
        );
        if (either.right().isPresent()) {
            this.fail("Failed to set biome for test");
        }
    }
}
