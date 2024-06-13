package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;

public record AttributeModifier(UUID id, String f_303575_, double amount, AttributeModifier.Operation operation) {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<AttributeModifier> f_316599_ = RecordCodecBuilder.mapCodec(
        p_309016_ -> p_309016_.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(AttributeModifier::id),
                    Codec.STRING.fieldOf("name").forGetter(p_309017_ -> p_309017_.f_303575_),
                    Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::amount),
                    AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifier::operation)
                )
                .apply(p_309016_, AttributeModifier::new)
    );
    public static final Codec<AttributeModifier> f_303618_ = f_316599_.codec();
    public static final StreamCodec<ByteBuf, AttributeModifier> f_315334_ = StreamCodec.m_319980_(
        UUIDUtil.f_315346_,
        AttributeModifier::id,
        ByteBufCodecs.f_315450_,
        p_326798_ -> p_326798_.f_303575_,
        ByteBufCodecs.f_315477_,
        AttributeModifier::amount,
        AttributeModifier.Operation.f_314272_,
        AttributeModifier::operation,
        AttributeModifier::new
    );

    public AttributeModifier(String pName, double pAmount, AttributeModifier.Operation pOperation) {
        this(Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), pName, pAmount, pOperation);
    }

    public CompoundTag save() {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", this.f_303575_);
        compoundtag.putDouble("Amount", this.amount);
        compoundtag.putInt("Operation", this.operation.m_324661_());
        compoundtag.putUUID("UUID", this.id);
        return compoundtag;
    }

    @Nullable
    public static AttributeModifier load(CompoundTag pNbt) {
        try {
            UUID uuid = pNbt.getUUID("UUID");
            AttributeModifier.Operation attributemodifier$operation = AttributeModifier.Operation.f_314520_.apply(pNbt.getInt("Operation"));
            return new AttributeModifier(uuid, pNbt.getString("Name"), pNbt.getDouble("Amount"), attributemodifier$operation);
        } catch (Exception exception) {
            LOGGER.warn("Unable to create attribute: {}", exception.getMessage());
            return null;
        }
    }

    public static enum Operation implements StringRepresentable {
        ADD_VALUE("add_value", 0),
        ADD_MULTIPLIED_BASE("add_multiplied_base", 1),
        ADD_MULTIPLIED_TOTAL("add_multiplied_total", 2);

        public static final IntFunction<AttributeModifier.Operation> f_314520_ = ByIdMap.continuous(
            AttributeModifier.Operation::m_324661_, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
        public static final StreamCodec<ByteBuf, AttributeModifier.Operation> f_314272_ = ByteBufCodecs.m_321301_(
            f_314520_, AttributeModifier.Operation::m_324661_
        );
        public static final Codec<AttributeModifier.Operation> CODEC = StringRepresentable.fromEnum(AttributeModifier.Operation::values);
        private final String name;
        private final int f_316785_;

        private Operation(final String pName, final int pValue) {
            this.name = pName;
            this.f_316785_ = pValue;
        }

        public int m_324661_() {
            return this.f_316785_;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}