package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class TrialSpawnerData {
    public static final String f_302545_ = "spawn_data";
    private static final String f_303495_ = "next_mob_spawns_at";
    private static final int f_315937_ = 20;
    private static final int f_314589_ = 18000;
    public static MapCodec<TrialSpawnerData> f_302600_ = RecordCodecBuilder.mapCodec(
        p_313188_ -> p_313188_.group(
                    UUIDUtil.f_302298_.lenientOptionalFieldOf("registered_players", Sets.newHashSet()).forGetter(p_309580_ -> p_309580_.f_303462_),
                    UUIDUtil.f_302298_.lenientOptionalFieldOf("current_mobs", Sets.newHashSet()).forGetter(p_311034_ -> p_311034_.f_302440_),
                    Codec.LONG.lenientOptionalFieldOf("cooldown_ends_at", Long.valueOf(0L)).forGetter(p_309685_ -> p_309685_.f_303712_),
                    Codec.LONG.lenientOptionalFieldOf("next_mob_spawns_at", Long.valueOf(0L)).forGetter(p_310876_ -> p_310876_.f_302458_),
                    Codec.intRange(0, Integer.MAX_VALUE).lenientOptionalFieldOf("total_mobs_spawned", 0).forGetter(p_309745_ -> p_309745_.f_302930_),
                    SpawnData.CODEC.lenientOptionalFieldOf("spawn_data").forGetter(p_312904_ -> p_312904_.f_303191_),
                    ResourceKey.codec(Registries.f_314309_).lenientOptionalFieldOf("ejecting_loot_table").forGetter(p_310765_ -> p_310765_.f_303012_)
                )
                .apply(p_313188_, TrialSpawnerData::new)
    );
    protected final Set<UUID> f_303462_ = new HashSet<>();
    protected final Set<UUID> f_302440_ = new HashSet<>();
    protected long f_303712_;
    protected long f_302458_;
    protected int f_302930_;
    protected Optional<SpawnData> f_303191_;
    protected Optional<ResourceKey<LootTable>> f_303012_;
    @Nullable
    protected Entity f_302195_;
    @Nullable
    private SimpleWeightedRandomList<ItemStack> f_314593_;
    protected double f_302632_;
    protected double f_303293_;

    public TrialSpawnerData() {
        this(Collections.emptySet(), Collections.emptySet(), 0L, 0L, 0, Optional.empty(), Optional.empty());
    }

    public TrialSpawnerData(
        Set<UUID> p_312543_,
        Set<UUID> p_311274_,
        long p_312908_,
        long p_311373_,
        int p_311452_,
        Optional<SpawnData> p_311258_,
        Optional<ResourceKey<LootTable>> p_312612_
    ) {
        this.f_303462_.addAll(p_312543_);
        this.f_302440_.addAll(p_311274_);
        this.f_303712_ = p_312908_;
        this.f_302458_ = p_311373_;
        this.f_302930_ = p_311452_;
        this.f_303191_ = p_311258_;
        this.f_303012_ = p_312612_;
    }

    public void m_305301_() {
        this.f_303462_.clear();
        this.f_302930_ = 0;
        this.f_302458_ = 0L;
        this.f_303712_ = 0L;
        this.f_302440_.clear();
    }

    public boolean m_306286_(TrialSpawner p_328530_, RandomSource p_333493_) {
        boolean flag = this.m_306716_(p_328530_, p_333493_).getEntityToSpawn().contains("id", 8);
        return flag || !p_328530_.m_306177_().f_303733_().isEmpty();
    }

    public boolean m_305025_(TrialSpawnerConfig p_310871_, int p_313160_) {
        return this.f_302930_ >= p_310871_.m_306590_(p_313160_);
    }

    public boolean m_307352_() {
        return this.f_302440_.isEmpty();
    }

    public boolean m_305681_(ServerLevel p_312376_, TrialSpawnerConfig p_313089_, int p_311969_) {
        return p_312376_.getGameTime() >= this.f_302458_ && this.f_302440_.size() < p_313089_.m_306918_(p_311969_);
    }

    public int m_305480_(BlockPos p_310055_) {
        if (this.f_303462_.isEmpty()) {
            Util.logAndPauseIfInIde("Trial Spawner at " + p_310055_ + " has no detected players");
        }

        return Math.max(0, this.f_303462_.size() - 1);
    }

    public void m_304942_(ServerLevel p_313049_, BlockPos p_310981_, TrialSpawner p_331326_) {
        boolean flag = (p_310981_.asLong() + p_313049_.getGameTime()) % 20L != 0L;
        if (!flag) {
            if (!p_331326_.m_305684_().equals(TrialSpawnerState.COOLDOWN) || !p_331326_.m_322987_()) {
                List<UUID> list = p_331326_.m_305791_().m_305839_(p_313049_, p_331326_.m_323449_(), p_310981_, (double)p_331326_.m_321887_(), true);
                Player player = null;

                for (UUID uuid : list) {
                    Player player1 = p_313049_.getPlayerByUUID(uuid);
                    if (player1 != null) {
                        if (player1.hasEffect(MobEffects.BAD_OMEN)) {
                            this.m_319436_(player1, player1.getEffect(MobEffects.BAD_OMEN));
                            player = player1;
                        } else if (player1.hasEffect(MobEffects.f_316051_)) {
                            player = player1;
                        }
                    }
                }

                boolean flag1 = !p_331326_.m_322987_() && player != null;
                if (!p_331326_.m_305684_().equals(TrialSpawnerState.COOLDOWN) || flag1) {
                    if (flag1) {
                        p_313049_.levelEvent(3020, BlockPos.containing(player.getEyePosition()), 0);
                        p_331326_.m_324951_(p_313049_, p_310981_);
                    }

                    boolean flag2 = p_331326_.m_305472_().f_303462_.isEmpty();
                    List<UUID> list1 = flag2
                        ? list
                        : p_331326_.m_305791_().m_305839_(p_313049_, p_331326_.m_323449_(), p_310981_, (double)p_331326_.m_321887_(), false);
                    if (this.f_303462_.addAll(list1)) {
                        this.f_302458_ = Math.max(p_313049_.getGameTime() + 40L, this.f_302458_);
                        if (!flag1) {
                            int i = p_331326_.m_322987_() ? 3019 : 3013;
                            p_313049_.levelEvent(i, p_310981_, this.f_303462_.size());
                        }
                    }
                }
            }
        }
    }

    public void m_321694_(TrialSpawner p_330837_, ServerLevel p_328172_) {
        this.f_302440_.stream().map(p_328172_::getEntity).forEach(p_327368_ -> {
            if (p_327368_ != null) {
                p_328172_.levelEvent(3012, p_327368_.blockPosition(), TrialSpawner.FlameParticle.NORMAL.m_324816_());
                p_327368_.remove(Entity.RemovalReason.DISCARDED);
            }
        });
        if (!p_330837_.m_320710_().f_303733_().isEmpty()) {
            this.f_303191_ = Optional.empty();
        }

        this.f_302930_ = 0;
        this.f_302440_.clear();
        this.f_302458_ = p_328172_.getGameTime() + (long)p_330837_.m_320710_().f_303452_();
        p_330837_.m_306727_();
        this.f_303712_ = p_328172_.getGameTime() + p_330837_.m_320710_().m_324829_();
    }

    private void m_319436_(Player p_327801_, MobEffectInstance p_329932_) {
        int i = p_329932_.getAmplifier() + 1;
        int j = 18000 * i;
        p_327801_.removeEffect(MobEffects.BAD_OMEN);
        p_327801_.addEffect(new MobEffectInstance(MobEffects.f_316051_, j, 0));
    }

    public boolean m_305761_(ServerLevel p_311936_, float p_312381_, int p_334019_) {
        long i = this.f_303712_ - (long)p_334019_;
        return (float)p_311936_.getGameTime() >= (float)i + p_312381_;
    }

    public boolean m_305594_(ServerLevel p_309478_, float p_310189_, int p_330888_) {
        long i = this.f_303712_ - (long)p_330888_;
        return (float)(p_309478_.getGameTime() - i) % p_310189_ == 0.0F;
    }

    public boolean m_305171_(ServerLevel p_312277_) {
        return p_312277_.getGameTime() >= this.f_303712_;
    }

    public void m_307184_(TrialSpawner p_311233_, RandomSource p_312395_, EntityType<?> p_311226_) {
        this.m_306716_(p_311233_, p_312395_).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(p_311226_).toString());
    }

    protected SpawnData m_306716_(TrialSpawner p_311810_, RandomSource p_311692_) {
        if (this.f_303191_.isPresent()) {
            return this.f_303191_.get();
        } else {
            SimpleWeightedRandomList<SpawnData> simpleweightedrandomlist = p_311810_.m_306177_().f_303733_();
            Optional<SpawnData> optional = simpleweightedrandomlist.isEmpty()
                ? this.f_303191_
                : simpleweightedrandomlist.getRandom(p_311692_).map(WeightedEntry.Wrapper::data);
            this.f_303191_ = Optional.of(optional.orElseGet(SpawnData::new));
            p_311810_.m_306727_();
            return this.f_303191_.get();
        }
    }

    @Nullable
    public Entity m_307031_(TrialSpawner p_310895_, Level p_310374_, TrialSpawnerState p_310556_) {
        if (p_310895_.m_305592_(p_310374_) && p_310556_.m_307384_()) {
            if (this.f_302195_ == null) {
                CompoundTag compoundtag = this.m_306716_(p_310895_, p_310374_.getRandom()).getEntityToSpawn();
                if (compoundtag.contains("id", 8)) {
                    this.f_302195_ = EntityType.loadEntityRecursive(compoundtag, p_310374_, Function.identity());
                }
            }

            return this.f_302195_;
        } else {
            return null;
        }
    }

    public CompoundTag m_307504_(TrialSpawnerState p_310015_) {
        CompoundTag compoundtag = new CompoundTag();
        if (p_310015_ == TrialSpawnerState.ACTIVE) {
            compoundtag.putLong("next_mob_spawns_at", this.f_302458_);
        }

        this.f_303191_
            .ifPresent(
                p_327366_ -> compoundtag.put(
                        "spawn_data",
                        SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, p_327366_).result().orElseThrow(() -> new IllegalStateException("Invalid SpawnData"))
                    )
            );
        return compoundtag;
    }

    public double m_306486_() {
        return this.f_302632_;
    }

    public double m_305098_() {
        return this.f_303293_;
    }

    SimpleWeightedRandomList<ItemStack> m_319751_(ServerLevel p_335070_, TrialSpawnerConfig p_328688_, BlockPos p_329742_) {
        if (this.f_314593_ != null) {
            return this.f_314593_;
        } else {
            LootTable loottable = p_335070_.getServer().m_323018_().m_321428_(p_328688_.f_315702_());
            LootParams lootparams = new LootParams.Builder(p_335070_).create(LootContextParamSets.EMPTY);
            long i = m_324844_(p_335070_, p_329742_);
            ObjectArrayList<ItemStack> objectarraylist = loottable.getRandomItems(lootparams, i);
            if (objectarraylist.isEmpty()) {
                return SimpleWeightedRandomList.empty();
            } else {
                SimpleWeightedRandomList.Builder<ItemStack> builder = new SimpleWeightedRandomList.Builder<>();

                for (ItemStack itemstack : objectarraylist) {
                    builder.add(itemstack.copyWithCount(1), itemstack.getCount());
                }

                this.f_314593_ = builder.build();
                return this.f_314593_;
            }
        }
    }

    private static long m_324844_(ServerLevel p_332486_, BlockPos p_332719_) {
        BlockPos blockpos = new BlockPos(
            Mth.floor((float)p_332719_.getX() / 30.0F),
            Mth.floor((float)p_332719_.getY() / 20.0F),
            Mth.floor((float)p_332719_.getZ() / 30.0F)
        );
        return p_332486_.getSeed() + blockpos.asLong();
    }
}