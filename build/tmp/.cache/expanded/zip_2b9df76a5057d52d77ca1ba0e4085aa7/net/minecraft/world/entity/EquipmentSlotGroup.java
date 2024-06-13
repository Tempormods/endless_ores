package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum EquipmentSlotGroup implements StringRepresentable {
    ANY(0, "any", p_335585_ -> true),
    MAINHAND(1, "mainhand", EquipmentSlot.MAINHAND),
    OFFHAND(2, "offhand", EquipmentSlot.OFFHAND),
    HAND(3, "hand", p_330375_ -> p_330375_.getType() == EquipmentSlot.Type.HAND),
    FEET(4, "feet", EquipmentSlot.FEET),
    LEGS(5, "legs", EquipmentSlot.LEGS),
    CHEST(6, "chest", EquipmentSlot.CHEST),
    HEAD(7, "head", EquipmentSlot.HEAD),
    ARMOR(8, "armor", EquipmentSlot::isArmor),
    BODY(9, "body", EquipmentSlot.BODY);

    public static final IntFunction<EquipmentSlotGroup> f_315025_ = ByIdMap.continuous(
        p_331450_ -> p_331450_.f_315618_, values(), ByIdMap.OutOfBoundsStrategy.ZERO
    );
    public static final Codec<EquipmentSlotGroup> f_315768_ = StringRepresentable.fromEnum(EquipmentSlotGroup::values);
    public static final StreamCodec<ByteBuf, EquipmentSlotGroup> f_316872_ = ByteBufCodecs.m_321301_(f_315025_, p_330886_ -> p_330886_.f_315618_);
    private final int f_315618_;
    private final String f_317095_;
    private final Predicate<EquipmentSlot> f_316897_;

    private EquipmentSlotGroup(final int p_335419_, final String p_332223_, final Predicate<EquipmentSlot> p_333500_) {
        this.f_315618_ = p_335419_;
        this.f_317095_ = p_332223_;
        this.f_316897_ = p_333500_;
    }

    private EquipmentSlotGroup(final int p_334344_, final String p_328996_, final EquipmentSlot p_332147_) {
        this(p_334344_, p_328996_, p_330757_ -> p_330757_ == p_332147_);
    }

    public static EquipmentSlotGroup m_320511_(EquipmentSlot p_331051_) {
        return switch (p_331051_) {
            case MAINHAND -> MAINHAND;
            case OFFHAND -> OFFHAND;
            case FEET -> FEET;
            case LEGS -> LEGS;
            case CHEST -> CHEST;
            case HEAD -> HEAD;
            case BODY -> BODY;
        };
    }

    @Override
    public String getSerializedName() {
        return this.f_317095_;
    }

    public boolean m_318881_(EquipmentSlot p_328114_) {
        return this.f_316897_.test(p_328114_);
    }
}