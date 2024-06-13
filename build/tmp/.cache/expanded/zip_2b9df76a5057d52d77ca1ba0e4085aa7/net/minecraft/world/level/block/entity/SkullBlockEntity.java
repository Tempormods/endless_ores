package net.minecraft.world.level.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class SkullBlockEntity extends BlockEntity {
    private static final String f_314169_ = "profile";
    private static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
    private static final String f_314338_ = "custom_name";
    private static final Logger f_314011_ = LogUtils.getLogger();
    @Nullable
    private static Executor mainThreadExecutor;
    @Nullable
    private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> f_315372_;
    @Nullable
    private static LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> f_315920_;
    public static final Executor CHECKED_MAIN_THREAD_EXECUTOR = p_296964_ -> {
        Executor executor = mainThreadExecutor;
        if (executor != null) {
            executor.execute(p_296964_);
        }
    };
    @Nullable
    private ResolvableProfile owner;
    @Nullable
    private ResourceLocation noteBlockSound;
    private int animationTickCount;
    private boolean isAnimating;
    @Nullable
    private Component f_314450_;

    public SkullBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.SKULL, pPos, pBlockState);
    }

    public static void setup(final Services pServices, Executor pMainThreadExecutor) {
        mainThreadExecutor = pMainThreadExecutor;
        final BooleanSupplier booleansupplier = () -> f_315920_ == null;
        f_315372_ = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10L))
            .maximumSize(256L)
            .build(new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>() {
                public CompletableFuture<Optional<GameProfile>> load(String p_312380_) {
                    return SkullBlockEntity.m_319852_(p_312380_, pServices);
                }
            });
        f_315920_ = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10L))
            .maximumSize(256L)
            .build(new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>() {
                public CompletableFuture<Optional<GameProfile>> load(UUID p_330530_) {
                    return SkullBlockEntity.m_321119_(p_330530_, pServices, booleansupplier);
                }
            });
    }

    static CompletableFuture<Optional<GameProfile>> m_319852_(String p_333451_, Services p_332839_) {
        return p_332839_.profileCache()
            .getAsync(p_333451_)
            .thenCompose(
                p_327322_ -> {
                    LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingcache = f_315920_;
                    return loadingcache != null && !p_327322_.isEmpty()
                        ? loadingcache.getUnchecked(p_327322_.get().getId()).thenApply(p_327317_ -> p_327317_.or(() -> p_327322_))
                        : CompletableFuture.completedFuture(Optional.empty());
                }
            );
    }

    static CompletableFuture<Optional<GameProfile>> m_321119_(UUID p_332548_, Services p_336268_, BooleanSupplier p_335205_) {
        return CompletableFuture.supplyAsync(() -> {
            if (p_335205_.getAsBoolean()) {
                return Optional.empty();
            } else {
                ProfileResult profileresult = p_336268_.sessionService().fetchProfile(p_332548_, true);
                return Optional.ofNullable(profileresult).map(ProfileResult::profile);
            }
        }, Util.backgroundExecutor());
    }

    public static void clear() {
        mainThreadExecutor = null;
        f_315372_ = null;
        f_315920_ = null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_329143_) {
        super.saveAdditional(pTag, p_329143_);
        if (this.owner != null) {
            pTag.put("profile", ResolvableProfile.f_315352_.encodeStart(NbtOps.INSTANCE, this.owner).getOrThrow());
        }

        if (this.noteBlockSound != null) {
            pTag.putString("note_block_sound", this.noteBlockSound.toString());
        }

        if (this.f_314450_ != null) {
            pTag.putString("custom_name", Component.Serializer.toJson(this.f_314450_, p_329143_));
        }
    }

    @Override
    protected void m_318667_(CompoundTag p_335831_, HolderLookup.Provider p_329643_) {
        super.m_318667_(p_335831_, p_329643_);
        if (p_335831_.contains("profile")) {
            ResolvableProfile.f_315352_
                .parse(NbtOps.INSTANCE, p_335831_.get("profile"))
                .resultOrPartial(p_327318_ -> f_314011_.error("Failed to load profile from player head: {}", p_327318_))
                .ifPresent(this::setOwner);
        }

        if (p_335831_.contains("note_block_sound", 8)) {
            this.noteBlockSound = ResourceLocation.tryParse(p_335831_.getString("note_block_sound"));
        }

        if (p_335831_.contains("custom_name", 8)) {
            this.f_314450_ = Component.Serializer.fromJson(p_335831_.getString("custom_name"), p_329643_);
        } else {
            this.f_314450_ = null;
        }
    }

    public static void animation(Level pLevel, BlockPos pPos, BlockState pState, SkullBlockEntity pBlockEntity) {
        if (pState.hasProperty(SkullBlock.POWERED) && pState.getValue(SkullBlock.POWERED)) {
            pBlockEntity.isAnimating = true;
            pBlockEntity.animationTickCount++;
        } else {
            pBlockEntity.isAnimating = false;
        }
    }

    public float getAnimation(float pPartialTick) {
        return this.isAnimating ? (float)this.animationTickCount + pPartialTick : (float)this.animationTickCount;
    }

    @Nullable
    public ResolvableProfile getOwnerProfile() {
        return this.owner;
    }

    @Nullable
    public ResourceLocation getNoteBlockSound() {
        return this.noteBlockSound;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_335540_) {
        return this.m_320696_(p_335540_);
    }

    public void setOwner(@Nullable ResolvableProfile p_328553_) {
        synchronized (this) {
            this.owner = p_328553_;
        }

        this.updateOwnerProfile();
    }

    private void updateOwnerProfile() {
        if (this.owner != null && !this.owner.m_320408_()) {
            this.owner.m_322305_().thenAcceptAsync(p_327314_ -> {
                this.owner = p_327314_;
                this.setChanged();
            }, CHECKED_MAIN_THREAD_EXECUTOR);
        } else {
            this.setChanged();
        }
    }

    public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String pProfileName) {
        LoadingCache<String, CompletableFuture<Optional<GameProfile>>> loadingcache = f_315372_;
        return loadingcache != null && StringUtil.m_319148_(pProfileName)
            ? loadingcache.getUnchecked(pProfileName)
            : CompletableFuture.completedFuture(Optional.empty());
    }

    public static CompletableFuture<Optional<GameProfile>> m_319014_(UUID p_331248_) {
        LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> loadingcache = f_315920_;
        return loadingcache != null ? loadingcache.getUnchecked(p_331248_) : CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_334905_) {
        super.m_318741_(p_334905_);
        this.setOwner(p_334905_.m_319293_(DataComponents.f_315901_));
        this.noteBlockSound = p_334905_.m_319293_(DataComponents.f_315959_);
        this.f_314450_ = p_334905_.m_319293_(DataComponents.f_316016_);
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_335245_) {
        super.m_318837_(p_335245_);
        p_335245_.m_322739_(DataComponents.f_315901_, this.owner);
        p_335245_.m_322739_(DataComponents.f_315959_, this.noteBlockSound);
        p_335245_.m_322739_(DataComponents.f_316016_, this.f_314450_);
    }

    @Override
    public void m_318942_(CompoundTag p_332333_) {
        super.m_318942_(p_332333_);
        p_332333_.remove("profile");
        p_332333_.remove("note_block_sound");
        p_332333_.remove("custom_name");
    }
}