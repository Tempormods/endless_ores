package net.minecraft.network.syncher;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Rotations;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Registry for {@link EntityDataSerializer}.
 */
public class EntityDataSerializers {
    private static final CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> SERIALIZERS = CrudeIncrementalIntIdentityHashBiMap.create(16);
    public static final EntityDataSerializer<Byte> BYTE = EntityDataSerializer.m_322970_(ByteBufCodecs.f_313954_);
    public static final EntityDataSerializer<Integer> INT = EntityDataSerializer.m_322970_(ByteBufCodecs.f_316730_);
    public static final EntityDataSerializer<Long> LONG = EntityDataSerializer.m_322970_(ByteBufCodecs.f_315478_);
    public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializer.m_322970_(ByteBufCodecs.f_314734_);
    public static final EntityDataSerializer<String> STRING = EntityDataSerializer.m_322970_(ByteBufCodecs.f_315450_);
    public static final EntityDataSerializer<Component> COMPONENT = EntityDataSerializer.m_322970_(ComponentSerialization.f_316335_);
    public static final EntityDataSerializer<Optional<Component>> OPTIONAL_COMPONENT = EntityDataSerializer.m_322970_(ComponentSerialization.f_316844_);
    public static final EntityDataSerializer<ItemStack> ITEM_STACK = new EntityDataSerializer<ItemStack>() {
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ItemStack> m_321181_() {
            return ItemStack.f_314979_;
        }

        public ItemStack copy(ItemStack p_238121_) {
            return p_238121_.copy();
        }
    };
    public static final EntityDataSerializer<BlockState> BLOCK_STATE = EntityDataSerializer.m_322970_(ByteBufCodecs.m_323411_(Block.BLOCK_STATE_REGISTRY));
    private static final StreamCodec<ByteBuf, Optional<BlockState>> f_316598_ = new StreamCodec<ByteBuf, Optional<BlockState>>() {
        public void m_318638_(ByteBuf p_329740_, Optional<BlockState> p_331636_) {
            if (p_331636_.isPresent()) {
                VarInt.write(p_329740_, Block.getId(p_331636_.get()));
            } else {
                VarInt.write(p_329740_, 0);
            }
        }

        public Optional<BlockState> m_318688_(ByteBuf p_334256_) {
            int i = VarInt.read(p_334256_);
            return i == 0 ? Optional.empty() : Optional.of(Block.stateById(i));
        }
    };
    public static final EntityDataSerializer<Optional<BlockState>> OPTIONAL_BLOCK_STATE = EntityDataSerializer.m_322970_(f_316598_);
    public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializer.m_322970_(ByteBufCodecs.f_315514_);
    public static final EntityDataSerializer<ParticleOptions> PARTICLE = EntityDataSerializer.m_322970_(ParticleTypes.f_314250_);
    public static final EntityDataSerializer<List<ParticleOptions>> f_315472_ = EntityDataSerializer.m_322970_(
        ParticleTypes.f_314250_.m_321801_(ByteBufCodecs.m_324765_())
    );
    public static final EntityDataSerializer<Rotations> ROTATIONS = EntityDataSerializer.m_322970_(Rotations.f_316585_);
    public static final EntityDataSerializer<BlockPos> BLOCK_POS = EntityDataSerializer.m_322970_(BlockPos.f_316462_);
    public static final EntityDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = EntityDataSerializer.m_322970_(
        BlockPos.f_316462_.m_321801_(ByteBufCodecs::m_319027_)
    );
    public static final EntityDataSerializer<Direction> DIRECTION = EntityDataSerializer.m_322970_(Direction.f_315582_);
    public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = EntityDataSerializer.m_322970_(UUIDUtil.f_315346_.m_321801_(ByteBufCodecs::m_319027_));
    public static final EntityDataSerializer<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = EntityDataSerializer.m_322970_(
        GlobalPos.f_314491_.m_321801_(ByteBufCodecs::m_319027_)
    );
    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = new EntityDataSerializer<CompoundTag>() {
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, CompoundTag> m_321181_() {
            return ByteBufCodecs.f_315964_;
        }

        public CompoundTag copy(CompoundTag p_331518_) {
            return p_331518_.copy();
        }
    };
    public static final EntityDataSerializer<VillagerData> VILLAGER_DATA = EntityDataSerializer.m_322970_(VillagerData.f_314537_);
    private static final StreamCodec<ByteBuf, OptionalInt> f_316634_ = new StreamCodec<ByteBuf, OptionalInt>() {
        public OptionalInt m_318688_(ByteBuf p_334577_) {
            int i = VarInt.read(p_334577_);
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        public void m_318638_(ByteBuf p_330497_, OptionalInt p_332106_) {
            VarInt.write(p_330497_, p_332106_.orElse(-1) + 1);
        }
    };
    public static final EntityDataSerializer<OptionalInt> OPTIONAL_UNSIGNED_INT = EntityDataSerializer.m_322970_(f_316634_);
    public static final EntityDataSerializer<Pose> POSE = EntityDataSerializer.m_322970_(Pose.f_315890_);
    public static final EntityDataSerializer<Holder<CatVariant>> CAT_VARIANT = EntityDataSerializer.m_322970_(ByteBufCodecs.m_322636_(Registries.CAT_VARIANT));
    public static final EntityDataSerializer<Holder<WolfVariant>> f_314299_ = EntityDataSerializer.m_322970_(ByteBufCodecs.m_322636_(Registries.f_317086_));
    public static final EntityDataSerializer<Holder<FrogVariant>> FROG_VARIANT = EntityDataSerializer.m_322970_(ByteBufCodecs.m_322636_(Registries.FROG_VARIANT));
    public static final EntityDataSerializer<Holder<PaintingVariant>> PAINTING_VARIANT = EntityDataSerializer.m_322970_(ByteBufCodecs.m_322636_(Registries.PAINTING_VARIANT));
    public static final EntityDataSerializer<Armadillo.ArmadilloState> f_315197_ = EntityDataSerializer.m_322970_(Armadillo.ArmadilloState.f_315535_);
    public static final EntityDataSerializer<Sniffer.State> SNIFFER_STATE = EntityDataSerializer.m_322970_(Sniffer.State.f_313997_);
    public static final EntityDataSerializer<Vector3f> VECTOR3 = EntityDataSerializer.m_322970_(ByteBufCodecs.f_314483_);
    public static final EntityDataSerializer<Quaternionf> QUATERNION = EntityDataSerializer.m_322970_(ByteBufCodecs.f_313943_);

    public static void registerSerializer(EntityDataSerializer<?> pSerializer) {
        int id =
        SERIALIZERS.add(pSerializer);
        if (id >= 256) throw new RuntimeException("Vanilla DataSerializer ID limit exceeded");
    }

    @Nullable
    public static EntityDataSerializer<?> getSerializer(int pId) {
        return net.minecraftforge.common.ForgeHooks.getSerializer(pId, SERIALIZERS);
    }

    public static int getSerializedId(EntityDataSerializer<?> pSerializer) {
        return net.minecraftforge.common.ForgeHooks.getSerializerId(pSerializer, SERIALIZERS);
    }

    private EntityDataSerializers() {
    }

    static {
        registerSerializer(BYTE);
        registerSerializer(INT);
        registerSerializer(LONG);
        registerSerializer(FLOAT);
        registerSerializer(STRING);
        registerSerializer(COMPONENT);
        registerSerializer(OPTIONAL_COMPONENT);
        registerSerializer(ITEM_STACK);
        registerSerializer(BOOLEAN);
        registerSerializer(ROTATIONS);
        registerSerializer(BLOCK_POS);
        registerSerializer(OPTIONAL_BLOCK_POS);
        registerSerializer(DIRECTION);
        registerSerializer(OPTIONAL_UUID);
        registerSerializer(BLOCK_STATE);
        registerSerializer(OPTIONAL_BLOCK_STATE);
        registerSerializer(COMPOUND_TAG);
        registerSerializer(PARTICLE);
        registerSerializer(f_315472_);
        registerSerializer(VILLAGER_DATA);
        registerSerializer(OPTIONAL_UNSIGNED_INT);
        registerSerializer(POSE);
        registerSerializer(CAT_VARIANT);
        registerSerializer(f_314299_);
        registerSerializer(FROG_VARIANT);
        registerSerializer(OPTIONAL_GLOBAL_POS);
        registerSerializer(PAINTING_VARIANT);
        registerSerializer(SNIFFER_STATE);
        registerSerializer(f_315197_);
        registerSerializer(VECTOR3);
        registerSerializer(QUATERNION);
    }
}
