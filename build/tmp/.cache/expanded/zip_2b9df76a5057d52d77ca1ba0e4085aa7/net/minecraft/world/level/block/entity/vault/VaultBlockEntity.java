package net.minecraft.world.level.block.entity.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class VaultBlockEntity extends BlockEntity {
    private static final Logger f_316405_ = LogUtils.getLogger();
    private final VaultServerData f_315874_ = new VaultServerData();
    private final VaultSharedData f_314451_ = new VaultSharedData();
    private final VaultClientData f_315237_ = new VaultClientData();
    private VaultConfig f_314356_ = VaultConfig.f_314544_;

    public VaultBlockEntity(BlockPos p_329814_, BlockState p_335937_) {
        super(BlockEntityType.f_316672_, p_329814_, p_335937_);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_335952_) {
        return Util.make(
            new CompoundTag(), p_331371_ -> p_331371_.put("shared_data", m_319292_(VaultSharedData.f_314733_, this.f_314451_, p_335952_))
        );
    }

    @Override
    protected void saveAdditional(CompoundTag p_335237_, HolderLookup.Provider p_332605_) {
        super.saveAdditional(p_335237_, p_332605_);
        p_335237_.put("config", m_319292_(VaultConfig.f_316227_, this.f_314356_, p_332605_));
        p_335237_.put("shared_data", m_319292_(VaultSharedData.f_314733_, this.f_314451_, p_332605_));
        p_335237_.put("server_data", m_319292_(VaultServerData.f_315361_, this.f_315874_, p_332605_));
    }

    private static <T> Tag m_319292_(Codec<T> p_328379_, T p_331958_, HolderLookup.Provider p_334758_) {
        return p_328379_.encodeStart(p_334758_.m_318927_(NbtOps.INSTANCE), p_331958_).getOrThrow();
    }

    @Override
    protected void m_318667_(CompoundTag p_329069_, HolderLookup.Provider p_335999_) {
        super.m_318667_(p_329069_, p_335999_);
        DynamicOps<Tag> dynamicops = p_335999_.m_318927_(NbtOps.INSTANCE);
        if (p_329069_.contains("server_data")) {
            VaultServerData.f_315361_
                .parse(dynamicops, p_329069_.get("server_data"))
                .resultOrPartial(f_316405_::error)
                .ifPresent(this.f_315874_::m_323813_);
        }

        if (p_329069_.contains("config")) {
            VaultConfig.f_316227_
                .parse(dynamicops, p_329069_.get("config"))
                .resultOrPartial(f_316405_::error)
                .ifPresent(p_335308_ -> this.f_314356_ = p_335308_);
        }

        if (p_329069_.contains("shared_data")) {
            VaultSharedData.f_314733_
                .parse(dynamicops, p_329069_.get("shared_data"))
                .resultOrPartial(f_316405_::error)
                .ifPresent(this.f_314451_::m_319383_);
        }
    }

    @Nullable
    public VaultServerData m_324418_() {
        return this.level != null && !this.level.isClientSide ? this.f_315874_ : null;
    }

    public VaultSharedData m_318941_() {
        return this.f_314451_;
    }

    public VaultClientData m_320550_() {
        return this.f_315237_;
    }

    public VaultConfig m_321918_() {
        return this.f_314356_;
    }

    @VisibleForTesting
    public void m_319417_(VaultConfig p_332483_) {
        this.f_314356_ = p_332483_;
    }

    public static final class Client {
        private static final int f_316347_ = 20;
        private static final float f_314781_ = 0.5F;
        private static final float f_314689_ = 0.02F;
        private static final int f_316230_ = 20;
        private static final int f_315483_ = 20;

        public static void m_322772_(Level p_331255_, BlockPos p_335715_, BlockState p_330773_, VaultClientData p_335986_, VaultSharedData p_333339_) {
            p_335986_.m_320699_();
            if (p_331255_.getGameTime() % 20L == 0L) {
                m_319797_(p_331255_, p_335715_, p_330773_, p_333339_);
            }

            m_322132_(p_331255_, p_335715_, p_333339_, p_330773_.getValue(VaultBlock.f_317007_) ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME);
            m_322416_(p_331255_, p_335715_, p_333339_);
        }

        public static void m_322037_(Level p_329048_, BlockPos p_334504_, BlockState p_328465_, VaultSharedData p_331322_, ParticleOptions p_332937_) {
            m_319797_(p_329048_, p_334504_, p_328465_, p_331322_);
            RandomSource randomsource = p_329048_.random;

            for (int i = 0; i < 20; i++) {
                Vec3 vec3 = m_320709_(p_334504_, randomsource);
                p_329048_.addParticle(ParticleTypes.SMOKE, vec3.x(), vec3.y(), vec3.z(), 0.0, 0.0, 0.0);
                p_329048_.addParticle(p_332937_, vec3.x(), vec3.y(), vec3.z(), 0.0, 0.0, 0.0);
            }
        }

        public static void m_319825_(Level p_330549_, BlockPos p_334754_, ParticleOptions p_335199_) {
            RandomSource randomsource = p_330549_.random;

            for (int i = 0; i < 20; i++) {
                Vec3 vec3 = m_321013_(p_334754_, randomsource);
                Vec3 vec31 = new Vec3(randomsource.nextGaussian() * 0.02, randomsource.nextGaussian() * 0.02, randomsource.nextGaussian() * 0.02);
                p_330549_.addParticle(p_335199_, vec3.x(), vec3.y(), vec3.z(), vec31.x(), vec31.y(), vec31.z());
            }
        }

        private static void m_322132_(Level p_329901_, BlockPos p_330744_, VaultSharedData p_332348_, ParticleOptions p_333563_) {
            RandomSource randomsource = p_329901_.getRandom();
            if (randomsource.nextFloat() <= 0.5F) {
                Vec3 vec3 = m_320709_(p_330744_, randomsource);
                p_329901_.addParticle(ParticleTypes.SMOKE, vec3.x(), vec3.y(), vec3.z(), 0.0, 0.0, 0.0);
                if (m_321174_(p_332348_)) {
                    p_329901_.addParticle(p_333563_, vec3.x(), vec3.y(), vec3.z(), 0.0, 0.0, 0.0);
                }
            }
        }

        private static void m_320814_(Level p_327765_, Vec3 p_335116_, Player p_333131_) {
            RandomSource randomsource = p_327765_.random;
            Vec3 vec3 = p_335116_.vectorTo(p_333131_.position().add(0.0, (double)(p_333131_.getBbHeight() / 2.0F), 0.0));
            int i = Mth.nextInt(randomsource, 2, 5);

            for (int j = 0; j < i; j++) {
                Vec3 vec31 = vec3.offsetRandom(randomsource, 1.0F);
                p_327765_.addParticle(
                    ParticleTypes.f_314380_, p_335116_.x(), p_335116_.y(), p_335116_.z(), vec31.x(), vec31.y(), vec31.z()
                );
            }
        }

        private static void m_319797_(Level p_329933_, BlockPos p_335364_, BlockState p_330110_, VaultSharedData p_332177_) {
            Set<UUID> set = p_332177_.m_324860_();
            if (!set.isEmpty()) {
                Vec3 vec3 = m_322516_(p_335364_, p_330110_.getValue(VaultBlock.f_315389_));

                for (UUID uuid : set) {
                    Player player = p_329933_.getPlayerByUUID(uuid);
                    if (player != null && m_324184_(p_335364_, p_332177_, player)) {
                        m_320814_(p_329933_, vec3, player);
                    }
                }
            }
        }

        private static boolean m_324184_(BlockPos p_334746_, VaultSharedData p_334927_, Player p_333038_) {
            return p_333038_.blockPosition().distSqr(p_334746_) <= Mth.square(p_334927_.m_322015_());
        }

        private static void m_322416_(Level p_329850_, BlockPos p_333501_, VaultSharedData p_332082_) {
            if (m_321174_(p_332082_)) {
                RandomSource randomsource = p_329850_.getRandom();
                if (randomsource.nextFloat() <= 0.02F) {
                    p_329850_.playLocalSound(
                        p_333501_, SoundEvents.f_315555_, SoundSource.BLOCKS, randomsource.nextFloat() * 0.25F + 0.75F, randomsource.nextFloat() + 0.5F, false
                    );
                }
            }
        }

        public static boolean m_321174_(VaultSharedData p_329617_) {
            return p_329617_.m_323977_();
        }

        private static Vec3 m_321013_(BlockPos p_329856_, RandomSource p_333945_) {
            return Vec3.atLowerCornerOf(p_329856_)
                .add(Mth.nextDouble(p_333945_, 0.4, 0.6), Mth.nextDouble(p_333945_, 0.4, 0.6), Mth.nextDouble(p_333945_, 0.4, 0.6));
        }

        private static Vec3 m_320709_(BlockPos p_327884_, RandomSource p_332986_) {
            return Vec3.atLowerCornerOf(p_327884_)
                .add(Mth.nextDouble(p_332986_, 0.1, 0.9), Mth.nextDouble(p_332986_, 0.25, 0.75), Mth.nextDouble(p_332986_, 0.1, 0.9));
        }

        private static Vec3 m_322516_(BlockPos p_331540_, Direction p_333034_) {
            return Vec3.atBottomCenterOf(p_331540_).add((double)p_333034_.getStepX() * 0.5, 1.75, (double)p_333034_.getStepZ() * 0.5);
        }
    }

    public static final class Server {
        private static final int f_314328_ = 14;
        private static final int f_315248_ = 20;
        private static final int f_316099_ = 15;

        public static void m_318871_(
            ServerLevel p_327862_, BlockPos p_334036_, BlockState p_336094_, VaultConfig p_332912_, VaultServerData p_332613_, VaultSharedData p_336360_
        ) {
            VaultState vaultstate = p_336094_.getValue(VaultBlock.f_314947_);
            if (m_320131_(p_327862_.getGameTime(), vaultstate)) {
                m_321879_(p_327862_, vaultstate, p_332912_, p_336360_, p_334036_);
            }

            BlockState blockstate = p_336094_;
            if (p_327862_.getGameTime() >= p_332613_.m_318811_()) {
                blockstate = p_336094_.setValue(VaultBlock.f_314947_, vaultstate.m_321927_(p_327862_, p_334036_, p_332912_, p_332613_, p_336360_));
                if (!p_336094_.equals(blockstate)) {
                    m_318999_(p_327862_, p_334036_, p_336094_, blockstate, p_332912_, p_336360_);
                }
            }

            if (p_332613_.f_316353_ || p_336360_.f_315421_) {
                VaultBlockEntity.setChanged(p_327862_, p_334036_, p_336094_);
                if (p_336360_.f_315421_) {
                    p_327862_.sendBlockUpdated(p_334036_, p_336094_, blockstate, 2);
                }

                p_332613_.f_316353_ = false;
                p_336360_.f_315421_ = false;
            }
        }

        public static void m_321007_(
            ServerLevel p_330813_,
            BlockPos p_333223_,
            BlockState p_331301_,
            VaultConfig p_333877_,
            VaultServerData p_334388_,
            VaultSharedData p_330336_,
            Player p_332764_,
            ItemStack p_329896_
        ) {
            VaultState vaultstate = p_331301_.getValue(VaultBlock.f_314947_);
            if (m_322366_(p_333877_, vaultstate)) {
                if (!m_320541_(p_333877_, p_329896_)) {
                    m_324764_(p_330813_, p_334388_, p_333223_);
                } else if (p_334388_.m_324059_(p_332764_)) {
                    m_324764_(p_330813_, p_334388_, p_333223_);
                } else {
                    List<ItemStack> list = m_323202_(p_330813_, p_333877_, p_333223_, p_332764_);
                    if (!list.isEmpty()) {
                        p_332764_.awardStat(Stats.ITEM_USED.get(p_329896_.getItem()));
                        if (!p_332764_.isCreative()) {
                            p_329896_.shrink(p_333877_.f_313949_().getCount());
                        }

                        m_320999_(p_330813_, p_331301_, p_333223_, p_333877_, p_334388_, p_330336_, list);
                        p_334388_.m_320315_(p_332764_);
                        p_330336_.m_321245_(p_330813_, p_333223_, p_334388_, p_333877_, p_333877_.f_314383_());
                    }
                }
            }
        }

        static void m_318999_(
            ServerLevel p_327709_, BlockPos p_330897_, BlockState p_333801_, BlockState p_336357_, VaultConfig p_332945_, VaultSharedData p_328872_
        ) {
            VaultState vaultstate = p_333801_.getValue(VaultBlock.f_314947_);
            VaultState vaultstate1 = p_336357_.getValue(VaultBlock.f_314947_);
            p_327709_.setBlock(p_330897_, p_336357_, 3);
            vaultstate.m_320883_(p_327709_, p_330897_, vaultstate1, p_332945_, p_328872_, p_336357_.getValue(VaultBlock.f_317007_));
        }

        static void m_321879_(ServerLevel p_328186_, VaultState p_335064_, VaultConfig p_329242_, VaultSharedData p_336318_, BlockPos p_327920_) {
            if (!m_322366_(p_329242_, p_335064_)) {
                p_336318_.m_319450_(ItemStack.EMPTY);
            } else {
                ItemStack itemstack = m_321020_(p_328186_, p_327920_, p_329242_.f_314838_().orElse(p_329242_.f_314615_()));
                p_336318_.m_319450_(itemstack);
            }
        }

        private static ItemStack m_321020_(ServerLevel p_329309_, BlockPos p_331772_, ResourceKey<LootTable> p_327947_) {
            LootTable loottable = p_329309_.getServer().m_323018_().m_321428_(p_327947_);
            LootParams lootparams = new LootParams.Builder(p_329309_)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_331772_))
                .create(LootContextParamSets.f_317143_);
            List<ItemStack> list = loottable.getRandomItems(lootparams);
            return list.isEmpty() ? ItemStack.EMPTY : Util.getRandom(list, p_329309_.getRandom());
        }

        private static void m_320999_(
            ServerLevel p_329025_,
            BlockState p_334542_,
            BlockPos p_331457_,
            VaultConfig p_328759_,
            VaultServerData p_329258_,
            VaultSharedData p_328090_,
            List<ItemStack> p_328105_
        ) {
            p_329258_.m_322800_(p_328105_);
            p_328090_.m_319450_(p_329258_.m_320646_());
            p_329258_.m_319712_(p_329025_.getGameTime() + 14L);
            m_318999_(p_329025_, p_331457_, p_334542_, p_334542_.setValue(VaultBlock.f_314947_, VaultState.UNLOCKING), p_328759_, p_328090_);
        }

        private static List<ItemStack> m_323202_(ServerLevel p_332295_, VaultConfig p_329503_, BlockPos p_333443_, Player p_334837_) {
            LootTable loottable = p_332295_.getServer().m_323018_().m_321428_(p_329503_.f_314615_());
            LootParams lootparams = new LootParams.Builder(p_332295_)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_333443_))
                .withLuck(p_334837_.getLuck())
                .withParameter(LootContextParams.THIS_ENTITY, p_334837_)
                .create(LootContextParamSets.f_317143_);
            return loottable.getRandomItems(lootparams);
        }

        private static boolean m_322366_(VaultConfig p_333220_, VaultState p_335172_) {
            return p_333220_.f_314615_() != BuiltInLootTables.EMPTY && !p_333220_.f_313949_().isEmpty() && p_335172_ != VaultState.INACTIVE;
        }

        private static boolean m_320541_(VaultConfig p_334332_, ItemStack p_335056_) {
            return ItemStack.m_322370_(p_335056_, p_334332_.f_313949_()) && p_335056_.getCount() >= p_334332_.f_313949_().getCount();
        }

        private static boolean m_320131_(long p_334702_, VaultState p_332761_) {
            return p_334702_ % 20L == 0L && p_332761_ == VaultState.ACTIVE;
        }

        private static void m_324764_(ServerLevel p_334677_, VaultServerData p_330421_, BlockPos p_330460_) {
            if (p_334677_.getGameTime() >= p_330421_.m_322332_() + 15L) {
                p_334677_.playSound(null, p_330460_, SoundEvents.f_315921_, SoundSource.BLOCKS);
                p_330421_.m_319976_(p_334677_.getGameTime());
            }
        }
    }
}