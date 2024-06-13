package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class BeaconBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private static final int MAX_LEVELS = 4;
    public static final List<List<Holder<MobEffect>>> BEACON_EFFECTS = List.of(
        List.of(MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED),
        List.of(MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP),
        List.of(MobEffects.DAMAGE_BOOST),
        List.of(MobEffects.REGENERATION)
    );
    private static final Set<Holder<MobEffect>> VALID_EFFECTS = BEACON_EFFECTS.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_SECONDARY = 2;
    public static final int NUM_DATA_VALUES = 3;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final Component DEFAULT_NAME = Component.translatable("container.beacon");
    private static final String TAG_PRIMARY = "primary_effect";
    private static final String TAG_SECONDARY = "secondary_effect";
    List<BeaconBlockEntity.BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    int levels;
    private int lastCheckY;
    @Nullable
    Holder<MobEffect> primaryPower;
    @Nullable
    Holder<MobEffect> secondaryPower;
    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int p_58711_) {
            return switch (p_58711_) {
                case 0 -> BeaconBlockEntity.this.levels;
                case 1 -> BeaconMenu.encodeEffect(BeaconBlockEntity.this.primaryPower);
                case 2 -> BeaconMenu.encodeEffect(BeaconBlockEntity.this.secondaryPower);
                default -> 0;
            };
        }

        @Override
        public void set(int p_58713_, int p_58714_) {
            switch (p_58713_) {
                case 0:
                    BeaconBlockEntity.this.levels = p_58714_;
                    break;
                case 1:
                    if (!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
                        BeaconBlockEntity.playSound(BeaconBlockEntity.this.level, BeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
                    }

                    BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.filterEffect(BeaconMenu.decodeEffect(p_58714_));
                    break;
                case 2:
                    BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.filterEffect(BeaconMenu.decodeEffect(p_58714_));
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    @Nullable
    static Holder<MobEffect> filterEffect(@Nullable Holder<MobEffect> p_330198_) {
        return VALID_EFFECTS.contains(p_330198_) ? p_330198_ : null;
    }

    public BeaconBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.BEACON, pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, BeaconBlockEntity pBlockEntity) {
        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();
        BlockPos blockpos;
        if (pBlockEntity.lastCheckY < j) {
            blockpos = pPos;
            pBlockEntity.checkingBeamSections = Lists.newArrayList();
            pBlockEntity.lastCheckY = pPos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, pBlockEntity.lastCheckY + 1, k);
        }

        BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = pBlockEntity.checkingBeamSections.isEmpty()
            ? null
            : pBlockEntity.checkingBeamSections.get(pBlockEntity.checkingBeamSections.size() - 1);
        int l = pLevel.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        for (int i1 = 0; i1 < 10 && blockpos.getY() <= l; i1++) {
            BlockState blockstate = pLevel.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            float[] afloat = blockstate.getBeaconColorMultiplier(pLevel, blockpos, pPos);
            if (afloat != null) {
                if (pBlockEntity.checkingBeamSections.size() <= 1) {
                    beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(afloat);
                    pBlockEntity.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                } else if (beaconblockentity$beaconbeamsection != null) {
                    if (Arrays.equals(afloat, beaconblockentity$beaconbeamsection.color)) {
                        beaconblockentity$beaconbeamsection.increaseHeight();
                    } else {
                        beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(
                            new float[]{
                                (beaconblockentity$beaconbeamsection.color[0] + afloat[0]) / 2.0F,
                                (beaconblockentity$beaconbeamsection.color[1] + afloat[1]) / 2.0F,
                                (beaconblockentity$beaconbeamsection.color[2] + afloat[2]) / 2.0F
                            }
                        );
                        pBlockEntity.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                    }
                }
            } else {
                if (beaconblockentity$beaconbeamsection == null || blockstate.getLightBlock(pLevel, blockpos) >= 15 && !blockstate.is(Blocks.BEDROCK)) {
                    pBlockEntity.checkingBeamSections.clear();
                    pBlockEntity.lastCheckY = l;
                    break;
                }

                beaconblockentity$beaconbeamsection.increaseHeight();
            }

            blockpos = blockpos.above();
            pBlockEntity.lastCheckY++;
        }

        int j1 = pBlockEntity.levels;
        if (pLevel.getGameTime() % 80L == 0L) {
            if (!pBlockEntity.beamSections.isEmpty()) {
                pBlockEntity.levels = updateBase(pLevel, i, j, k);
            }

            if (pBlockEntity.levels > 0 && !pBlockEntity.beamSections.isEmpty()) {
                applyEffects(pLevel, pPos, pBlockEntity.levels, pBlockEntity.primaryPower, pBlockEntity.secondaryPower);
                playSound(pLevel, pPos, SoundEvents.BEACON_AMBIENT);
            }
        }

        if (pBlockEntity.lastCheckY >= l) {
            pBlockEntity.lastCheckY = pLevel.getMinBuildHeight() - 1;
            boolean flag = j1 > 0;
            pBlockEntity.beamSections = pBlockEntity.checkingBeamSections;
            if (!pLevel.isClientSide) {
                boolean flag1 = pBlockEntity.levels > 0;
                if (!flag && flag1) {
                    playSound(pLevel, pPos, SoundEvents.BEACON_ACTIVATE);

                    for (ServerPlayer serverplayer : pLevel.getEntitiesOfClass(
                        ServerPlayer.class, new AABB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).inflate(10.0, 5.0, 10.0)
                    )) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayer, pBlockEntity.levels);
                    }
                } else if (flag && !flag1) {
                    playSound(pLevel, pPos, SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }
    }

    private static int updateBase(Level pLevel, int pX, int pY, int pZ) {
        int i = 0;

        for (int j = 1; j <= 4; i = j++) {
            int k = pY - j;
            if (k < pLevel.getMinBuildHeight()) {
                break;
            }

            boolean flag = true;

            for (int l = pX - j; l <= pX + j && flag; l++) {
                for (int i1 = pZ - j; i1 <= pZ + j; i1++) {
                    if (!pLevel.getBlockState(new BlockPos(l, k, i1)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                break;
            }
        }

        return i;
    }

    @Override
    public void setRemoved() {
        playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private static void applyEffects(
        Level pLevel, BlockPos pPos, int pLevels, @Nullable Holder<MobEffect> p_329363_, @Nullable Holder<MobEffect> p_332048_
    ) {
        if (!pLevel.isClientSide && p_329363_ != null) {
            double d0 = (double)(pLevels * 10 + 10);
            int i = 0;
            if (pLevels >= 4 && Objects.equals(p_329363_, p_332048_)) {
                i = 1;
            }

            int j = (9 + pLevels * 2) * 20;
            AABB aabb = new AABB(pPos).inflate(d0).expandTowards(0.0, (double)pLevel.getHeight(), 0.0);
            List<Player> list = pLevel.getEntitiesOfClass(Player.class, aabb);

            for (Player player : list) {
                player.addEffect(new MobEffectInstance(p_329363_, j, i, true, true));
            }

            if (pLevels >= 4 && !Objects.equals(p_329363_, p_332048_) && p_332048_ != null) {
                for (Player player1 : list) {
                    player1.addEffect(new MobEffectInstance(p_332048_, j, 0, true, true));
                }
            }
        }
    }

    public static void playSound(Level pLevel, BlockPos pPos, SoundEvent pSound) {
        pLevel.playSound(null, pPos, pSound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public List<BeaconBlockEntity.BeaconBeamSection> getBeamSections() {
        return (List<BeaconBlockEntity.BeaconBeamSection>)(this.levels == 0 ? ImmutableList.of() : this.beamSections);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_333588_) {
        return this.m_320696_(p_333588_);
    }

    private static void storeEffect(CompoundTag pTag, String pKey, @Nullable Holder<MobEffect> p_329692_) {
        if (p_329692_ != null) {
            p_329692_.unwrapKey().ifPresent(p_333727_ -> pTag.putString(pKey, p_333727_.location().toString()));
        }
    }

    @Nullable
    private static Holder<MobEffect> loadEffect(CompoundTag pTag, String pKey) {
        if (pTag.contains(pKey, 8)) {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(pTag.getString(pKey));
            return resourcelocation == null ? null : BuiltInRegistries.MOB_EFFECT.m_320017_(resourcelocation).map(BeaconBlockEntity::filterEffect).orElse(null);
        } else {
            return null;
        }
    }

    @Override
    protected void m_318667_(CompoundTag p_333194_, HolderLookup.Provider p_333691_) {
        super.m_318667_(p_333194_, p_333691_);
        this.primaryPower = loadEffect(p_333194_, "primary_effect");
        this.secondaryPower = loadEffect(p_333194_, "secondary_effect");
        if (p_333194_.contains("CustomName", 8)) {
            this.name = m_336414_(p_333194_.getString("CustomName"), p_333691_);
        }

        this.lockKey = LockCode.fromTag(p_333194_);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_330516_) {
        super.saveAdditional(pTag, p_330516_);
        storeEffect(pTag, "primary_effect", this.primaryPower);
        storeEffect(pTag, "secondary_effect", this.secondaryPower);
        pTag.putInt("Levels", this.levels);
        if (this.name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name, p_330516_));
        }

        this.lockKey.addToTag(pTag);
    }

    public void setCustomName(@Nullable Component pName) {
        this.name = pName;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return BaseContainerBlockEntity.canUnlock(pPlayer, this.lockKey, this.getDisplayName())
            ? new BeaconMenu(pContainerId, pPlayerInventory, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()))
            : null;
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_328078_) {
        super.m_318741_(p_328078_);
        this.name = p_328078_.m_319293_(DataComponents.f_316016_);
        this.lockKey = p_328078_.m_319031_(DataComponents.f_315242_, LockCode.NO_LOCK);
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_329382_) {
        super.m_318837_(p_329382_);
        p_329382_.m_322739_(DataComponents.f_316016_, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            p_329382_.m_322739_(DataComponents.f_315242_, this.lockKey);
        }
    }

    @Override
    public void m_318942_(CompoundTag p_331794_) {
        p_331794_.remove("CustomName");
        p_331794_.remove("Lock");
    }

    @Override
    public void setLevel(Level pLevel) {
        super.setLevel(pLevel);
        this.lastCheckY = pLevel.getMinBuildHeight() - 1;
    }

    public static class BeaconBeamSection {
        final float[] color;
        private int height;

        public BeaconBeamSection(float[] pColor) {
            this.color = pColor;
            this.height = 1;
        }

        protected void increaseHeight() {
            this.height++;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
