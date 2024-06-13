package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

public enum AdvancementType implements StringRepresentable {
    TASK("task", ChatFormatting.GREEN),
    CHALLENGE("challenge", ChatFormatting.DARK_PURPLE),
    GOAL("goal", ChatFormatting.GREEN);

    public static final Codec<AdvancementType> f_303602_ = StringRepresentable.fromEnum(AdvancementType::values);
    private final String f_303261_;
    private final ChatFormatting f_303078_;
    private final Component f_302214_;

    private AdvancementType(final String p_309637_, final ChatFormatting p_312188_) {
        this.f_303261_ = p_309637_;
        this.f_303078_ = p_312188_;
        this.f_302214_ = Component.translatable("advancements.toast." + p_309637_);
    }

    public ChatFormatting m_305069_() {
        return this.f_303078_;
    }

    public Component m_306854_() {
        return this.f_302214_;
    }

    @Override
    public String getSerializedName() {
        return this.f_303261_;
    }

    public MutableComponent m_305571_(AdvancementHolder p_311620_, ServerPlayer p_311407_) {
        return Component.translatable("chat.type.advancement." + this.f_303261_, p_311407_.getDisplayName(), Advancement.name(p_311620_));
    }
}