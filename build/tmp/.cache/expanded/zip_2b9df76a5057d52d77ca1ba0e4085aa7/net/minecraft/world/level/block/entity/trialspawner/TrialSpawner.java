package net.minecraft.world.level.block.entity.trialspawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public final class TrialSpawner {
    public static final String f_316473_ = "normal_config";
    public static final String f_314681_ = "ominous_config";
    public static final int f_302637_ = 40;
    private static final int f_316421_ = 36000;
    private static final int f_315941_ = 14;
    private static final int f_302891_ = 47;
    private static final int f_303092_ = Mth.square(47);
    private static final float f_302617_ = 0.02F;
    private final TrialSpawnerConfig f_314388_;
    private final TrialSpawnerConfig f_314347_;
    private final TrialSpawnerData f_302910_;
    private final int f_315564_;
    private final int f_316987_;
    private final TrialSpawner.StateAccessor f_302785_;
    private PlayerDetector f_302944_;
    private final PlayerDetector.EntitySelector f_314166_;
    private boolean f_302722_;
    private boolean f_315367_;

    public Codec<TrialSpawner> m_307687_() {
        return RecordCodecBuilder.create(
            p_327363_ -> p_327363_.group(
                        TrialSpawnerConfig.f_314242_.optionalFieldOf("normal_config", TrialSpawnerConfig.f_303284_).forGetter(TrialSpawner::m_319548_),
                        TrialSpawnerConfig.f_314242_.optionalFieldOf("ominous_config", TrialSpawnerConfig.f_303284_).forGetter(TrialSpawner::m_324376_),
                        TrialSpawnerData.f_302600_.forGetter(TrialSpawner::m_305472_),
                        Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("target_cooldown_length", 36000).forGetter(TrialSpawner::m_320388_),
                        Codec.intRange(1, 128).optionalFieldOf("required_player_range", 14).forGetter(TrialSpawner::m_321887_)
                    )
                    .apply(
                        p_327363_,
                        (p_327358_, p_327359_, p_327360_, p_327361_, p_327362_) -> new TrialSpawner(
                                p_327358_, p_327359_, p_327360_, p_327361_, p_327362_, this.f_302785_, this.f_302944_, this.f_314166_
                            )
                    )
        );
    }

    public TrialSpawner(TrialSpawner.StateAccessor p_310216_, PlayerDetector p_309626_, PlayerDetector.EntitySelector p_328170_) {
        this(TrialSpawnerConfig.f_303284_, TrialSpawnerConfig.f_303284_, new TrialSpawnerData(), 36000, 14, p_310216_, p_309626_, p_328170_);
    }

    public TrialSpawner(
        TrialSpawnerConfig p_327983_,
        TrialSpawnerConfig p_327832_,
        TrialSpawnerData p_330822_,
        int p_330441_,
        int p_335693_,
        TrialSpawner.StateAccessor p_310539_,
        PlayerDetector p_312974_,
        PlayerDetector.EntitySelector p_333634_
    ) {
        this.f_314388_ = p_327983_;
        this.f_314347_ = p_327832_;
        this.f_302910_ = p_330822_;
        this.f_316987_ = p_330441_;
        this.f_315564_ = p_335693_;
        this.f_302785_ = p_310539_;
        this.f_302944_ = p_312974_;
        this.f_314166_ = p_333634_;
    }

    public TrialSpawnerConfig m_306177_() {
        return this.f_315367_ ? this.f_314347_ : this.f_314388_;
    }

    @VisibleForTesting
    public TrialSpawnerConfig m_319548_() {
        return this.f_314388_;
    }

    @VisibleForTesting
    public TrialSpawnerConfig m_320710_() {
        return this.f_314347_;
    }

    private TrialSpawnerConfig m_324376_() {
        return !this.f_314347_.equals(this.f_314388_) ? this.f_314347_ : TrialSpawnerConfig.f_303284_;
    }

    public void m_324951_(ServerLevel p_334207_, BlockPos p_327778_) {
        p_334207_.setBlock(p_327778_, p_334207_.getBlockState(p_327778_).setValue(TrialSpawnerBlock.f_314407_, Boolean.valueOf(true)), 3);
        p_334207_.levelEvent(3020, p_327778_, 1);
        this.f_315367_ = true;
        this.f_302910_.m_321694_(this, p_334207_);
    }

    public void m_324138_(ServerLevel p_336080_, BlockPos p_328593_) {
        p_336080_.setBlock(p_328593_, p_336080_.getBlockState(p_328593_).setValue(TrialSpawnerBlock.f_314407_, Boolean.valueOf(false)), 3);
        this.f_315367_ = false;
    }

    public boolean m_322987_() {
        return this.f_315367_;
    }

    public TrialSpawnerData m_305472_() {
        return this.f_302910_;
    }

    public int m_320388_() {
        return this.f_316987_;
    }

    public int m_321887_() {
        return this.f_315564_;
    }

    public TrialSpawnerState m_305684_() {
        return this.f_302785_.m_306453_();
    }

    public void m_304838_(Level p_310153_, TrialSpawnerState p_312484_) {
        this.f_302785_.m_305970_(p_310153_, p_312484_);
    }

    public void m_306727_() {
        this.f_302785_.m_306374_();
    }

    public PlayerDetector m_305791_() {
        return this.f_302944_;
    }

    public PlayerDetector.EntitySelector m_323449_() {
        return this.f_314166_;
    }

    public boolean m_305592_(Level p_312209_) {
        if (this.f_302722_) {
            return true;
        } else {
            return p_312209_.getDifficulty() == Difficulty.PEACEFUL ? false : p_312209_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        }
    }

    public Optional<UUID> m_305361_(ServerLevel p_312690_, BlockPos p_313108_) {
        RandomSource randomsource = p_312690_.getRandom();
        SpawnData spawndata = this.f_302910_.m_306716_(this, p_312690_.getRandom());
        CompoundTag compoundtag = spawndata.entityToSpawn();
        ListTag listtag = compoundtag.getList("Pos", 6);
        Optional<EntityType<?>> optional = EntityType.by(compoundtag);
        if (optional.isEmpty()) {
            return Optional.empty();
        } else {
            int i = listtag.size();
            double d0 = i >= 1
                ? listtag.getDouble(0)
                : (double)p_313108_.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.m_306177_().f_302636_() + 0.5;
            double d1 = i >= 2 ? listtag.getDouble(1) : (double)(p_313108_.getY() + randomsource.nextInt(3) - 1);
            double d2 = i >= 3
                ? listtag.getDouble(2)
                : (double)p_313108_.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.m_306177_().f_302636_() + 0.5;
            if (!p_312690_.noCollision(optional.get().m_319702_(d0, d1, d2))) {
                return Optional.empty();
            } else {
                Vec3 vec3 = new Vec3(d0, d1, d2);
                if (!m_306844_(p_312690_, p_313108_.getCenter(), vec3)) {
                    return Optional.empty();
                } else {
                    BlockPos blockpos = BlockPos.containing(vec3);
                    if (!SpawnPlacements.checkSpawnRules(optional.get(), p_312690_, MobSpawnType.TRIAL_SPAWNER, blockpos, p_312690_.getRandom())) {
                        return Optional.empty();
                    } else {
                        if (spawndata.getCustomSpawnRules().isPresent()) {
                            SpawnData.CustomSpawnRules spawndata$customspawnrules = spawndata.getCustomSpawnRules().get();
                            if (!spawndata$customspawnrules.m_320581_(blockpos, p_312690_)) {
                                return Optional.empty();
                            }
                        }

                        Entity entity = EntityType.loadEntityRecursive(compoundtag, p_312690_, p_312166_ -> {
                            p_312166_.moveTo(d0, d1, d2, randomsource.nextFloat() * 360.0F, 0.0F);
                            return p_312166_;
                        });
                        if (entity == null) {
                            return Optional.empty();
                        } else {
                            if (entity instanceof Mob mob) {
                                if (!mob.checkSpawnObstruction(p_312690_)) {
                                    return Optional.empty();
                                }

                                boolean flag = spawndata.getEntityToSpawn().size() == 1 && spawndata.getEntityToSpawn().contains("id", 8);
                                if (flag) {
                                    mob.finalizeSpawn(p_312690_, p_312690_.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.TRIAL_SPAWNER, null);
                                }

                                mob.setPersistenceRequired();
                                spawndata.m_318950_().ifPresent(mob::m_319416_);
                            }

                            if (!p_312690_.tryAddFreshEntityWithPassengers(entity)) {
                                return Optional.empty();
                            } else {
                                TrialSpawner.FlameParticle trialspawner$flameparticle = this.f_315367_
                                    ? TrialSpawner.FlameParticle.OMINOUS
                                    : TrialSpawner.FlameParticle.NORMAL;
                                p_312690_.levelEvent(3011, p_313108_, trialspawner$flameparticle.m_324816_());
                                p_312690_.levelEvent(3012, blockpos, trialspawner$flameparticle.m_324816_());
                                p_312690_.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
                                return Optional.of(entity.getUUID());
                            }
                        }
                    }
                }
            }
        }
    }

    public void m_306218_(ServerLevel p_310080_, BlockPos p_311547_, ResourceKey<LootTable> p_330647_) {
        LootTable loottable = p_310080_.getServer().m_323018_().m_321428_(p_330647_);
        LootParams lootparams = new LootParams.Builder(p_310080_).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> objectarraylist = loottable.getRandomItems(lootparams);
        if (!objectarraylist.isEmpty()) {
            for (ItemStack itemstack : objectarraylist) {
                DefaultDispenseItemBehavior.spawnItem(p_310080_, itemstack, 2, Direction.UP, Vec3.atBottomCenterOf(p_311547_).relative(Direction.UP, 1.2));
            }

            p_310080_.levelEvent(3014, p_311547_, 0);
        }
    }

    public void m_304902_(Level p_309627_, BlockPos p_311485_, boolean p_332221_) {
        if (!this.m_305592_(p_309627_)) {
            this.f_302910_.f_303293_ = this.f_302910_.f_302632_;
        } else {
            TrialSpawnerState trialspawnerstate = this.m_305684_();
            trialspawnerstate.m_306816_(p_309627_, p_311485_, p_332221_);
            if (trialspawnerstate.m_307384_()) {
                double d0 = (double)Math.max(0L, this.f_302910_.f_302458_ - p_309627_.getGameTime());
                this.f_302910_.f_303293_ = this.f_302910_.f_302632_;
                this.f_302910_.f_302632_ = (this.f_302910_.f_302632_ + trialspawnerstate.m_305120_() / (d0 + 200.0)) % 360.0;
            }

            if (trialspawnerstate.m_306216_()) {
                RandomSource randomsource = p_309627_.getRandom();
                if (randomsource.nextFloat() <= 0.02F) {
                    SoundEvent soundevent = p_332221_ ? SoundEvents.f_315855_ : SoundEvents.f_302711_;
                    p_309627_.playLocalSound(
                        p_311485_, soundevent, SoundSource.BLOCKS, randomsource.nextFloat() * 0.25F + 0.75F, randomsource.nextFloat() + 0.5F, false
                    );
                }
            }
        }
    }

    public void m_306335_(ServerLevel p_310996_, BlockPos p_312836_, boolean p_332881_) {
        this.f_315367_ = p_332881_;
        TrialSpawnerState trialspawnerstate = this.m_305684_();
        if (!this.m_305592_(p_310996_)) {
            if (trialspawnerstate.m_306216_()) {
                this.f_302910_.m_305301_();
                this.m_304838_(p_310996_, TrialSpawnerState.INACTIVE);
            }
        } else {
            if (this.f_302910_.f_302440_.removeIf(p_309715_ -> m_306734_(p_310996_, p_312836_, p_309715_))) {
                this.f_302910_.f_302458_ = p_310996_.getGameTime() + (long)this.m_306177_().f_303452_();
            }

            TrialSpawnerState trialspawnerstate1 = trialspawnerstate.m_308008_(p_312836_, this, p_310996_);
            if (trialspawnerstate1 != trialspawnerstate) {
                this.m_304838_(p_310996_, trialspawnerstate1);
            }
        }
    }

    private static boolean m_306734_(ServerLevel p_312275_, BlockPos p_310158_, UUID p_312011_) {
        Entity entity = p_312275_.getEntity(p_312011_);
        return entity == null
            || !entity.isAlive()
            || !entity.level().dimension().equals(p_312275_.dimension())
            || entity.blockPosition().distSqr(p_310158_) > (double)f_303092_;
    }

    private static boolean m_306844_(Level p_311873_, Vec3 p_311845_, Vec3 p_312229_) {
        BlockHitResult blockhitresult = p_311873_.clip(
            new ClipContext(p_312229_, p_311845_, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty())
        );
        return blockhitresult.getBlockPos().equals(BlockPos.containing(p_311845_)) || blockhitresult.getType() == HitResult.Type.MISS;
    }

    public static void m_320714_(Level p_333032_, BlockPos p_328008_, RandomSource p_330922_, SimpleParticleType p_331431_) {
        for (int i = 0; i < 20; i++) {
            double d0 = (double)p_328008_.getX() + 0.5 + (p_330922_.nextDouble() - 0.5) * 2.0;
            double d1 = (double)p_328008_.getY() + 0.5 + (p_330922_.nextDouble() - 0.5) * 2.0;
            double d2 = (double)p_328008_.getZ() + 0.5 + (p_330922_.nextDouble() - 0.5) * 2.0;
            p_333032_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
            p_333032_.addParticle(p_331431_, d0, d1, d2, 0.0, 0.0, 0.0);
        }
    }

    public static void m_307155_(Level p_312837_, BlockPos p_311261_, RandomSource p_312356_) {
        for (int i = 0; i < 20; i++) {
            double d0 = (double)p_311261_.getX() + 0.5 + (p_312356_.nextDouble() - 0.5) * 2.0;
            double d1 = (double)p_311261_.getY() + 0.5 + (p_312356_.nextDouble() - 0.5) * 2.0;
            double d2 = (double)p_311261_.getZ() + 0.5 + (p_312356_.nextDouble() - 0.5) * 2.0;
            double d3 = p_312356_.nextGaussian() * 0.02;
            double d4 = p_312356_.nextGaussian() * 0.02;
            double d5 = p_312356_.nextGaussian() * 0.02;
            p_312837_.addParticle(ParticleTypes.f_317125_, d0, d1, d2, d3, d4, d5);
            p_312837_.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, d3, d4, d5);
        }
    }

    public static void m_306813_(Level p_309415_, BlockPos p_309941_, RandomSource p_310263_, int p_310988_, ParticleOptions p_331085_) {
        for (int i = 0; i < 30 + Math.min(p_310988_, 10) * 5; i++) {
            double d0 = (double)(2.0F * p_310263_.nextFloat() - 1.0F) * 0.65;
            double d1 = (double)(2.0F * p_310263_.nextFloat() - 1.0F) * 0.65;
            double d2 = (double)p_309941_.getX() + 0.5 + d0;
            double d3 = (double)p_309941_.getY() + 0.1 + (double)p_310263_.nextFloat() * 0.8;
            double d4 = (double)p_309941_.getZ() + 0.5 + d1;
            p_309415_.addParticle(p_331085_, d2, d3, d4, 0.0, 0.0, 0.0);
        }
    }

    public static void m_306726_(Level p_311170_, BlockPos p_309958_, RandomSource p_309409_) {
        for (int i = 0; i < 20; i++) {
            double d0 = (double)p_309958_.getX() + 0.4 + p_309409_.nextDouble() * 0.2;
            double d1 = (double)p_309958_.getY() + 0.4 + p_309409_.nextDouble() * 0.2;
            double d2 = (double)p_309958_.getZ() + 0.4 + p_309409_.nextDouble() * 0.2;
            double d3 = p_309409_.nextGaussian() * 0.02;
            double d4 = p_309409_.nextGaussian() * 0.02;
            double d5 = p_309409_.nextGaussian() * 0.02;
            p_311170_.addParticle(ParticleTypes.SMALL_FLAME, d0, d1, d2, d3, d4, d5 * 0.25);
            p_311170_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, d3, d4, d5);
        }
    }

    @Deprecated(
        forRemoval = true
    )
    @VisibleForTesting
    public void m_305658_(PlayerDetector p_311472_) {
        this.f_302944_ = p_311472_;
    }

    @Deprecated(
        forRemoval = true
    )
    @VisibleForTesting
    public void m_306621_() {
        this.f_302722_ = true;
    }

    public static enum FlameParticle {
        NORMAL(ParticleTypes.FLAME),
        OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

        public final SimpleParticleType f_316337_;

        private FlameParticle(final SimpleParticleType p_332977_) {
            this.f_316337_ = p_332977_;
        }

        public static TrialSpawner.FlameParticle m_319943_(int p_333274_) {
            TrialSpawner.FlameParticle[] atrialspawner$flameparticle = values();
            return p_333274_ <= atrialspawner$flameparticle.length && p_333274_ >= 0 ? atrialspawner$flameparticle[p_333274_] : NORMAL;
        }

        public int m_324816_() {
            return this.ordinal();
        }
    }

    public interface StateAccessor {
        void m_305970_(Level p_309383_, TrialSpawnerState p_310563_);

        TrialSpawnerState m_306453_();

        void m_306374_();
    }
}