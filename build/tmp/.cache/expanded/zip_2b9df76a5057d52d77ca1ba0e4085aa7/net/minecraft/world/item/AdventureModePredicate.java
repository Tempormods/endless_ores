package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModePredicate {
    private static final Codec<AdventureModePredicate> f_316464_ = BlockPredicate.CODEC
        .flatComapMap(p_334782_ -> new AdventureModePredicate(List.of(p_334782_), true), p_329449_ -> DataResult.error(() -> "Cannot encode"));
    private static final Codec<AdventureModePredicate> f_316127_ = RecordCodecBuilder.create(
        p_329746_ -> p_329746_.group(
                    ExtraCodecs.nonEmptyList(BlockPredicate.CODEC.listOf()).fieldOf("predicates").forGetter(p_329117_ -> p_329117_.f_316891_),
                    Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.valueOf(true)).forGetter(AdventureModePredicate::m_324667_)
                )
                .apply(p_329746_, AdventureModePredicate::new)
    );
    public static final Codec<AdventureModePredicate> f_314196_ = Codec.withAlternative(f_316127_, f_316464_);
    public static final StreamCodec<RegistryFriendlyByteBuf, AdventureModePredicate> f_315519_ = StreamCodec.m_320349_(
        BlockPredicate.f_315415_.m_321801_(ByteBufCodecs.m_324765_()),
        p_333442_ -> p_333442_.f_316891_,
        ByteBufCodecs.f_315514_,
        AdventureModePredicate::m_324667_,
        AdventureModePredicate::new
    );
    public static final Component f_315565_ = Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY);
    public static final Component f_314797_ = Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY);
    private static final Component f_314193_ = Component.translatable("item.canUse.unknown").withStyle(ChatFormatting.GRAY);
    private final List<BlockPredicate> f_316891_;
    private final boolean f_316745_;
    private final List<Component> f_314142_;
    @Nullable
    private BlockInWorld f_314808_;
    private boolean f_316657_;
    private boolean f_314757_;

    private AdventureModePredicate(List<BlockPredicate> p_330484_, boolean p_331616_, List<Component> p_329534_) {
        this.f_316891_ = p_330484_;
        this.f_316745_ = p_331616_;
        this.f_314142_ = p_329534_;
    }

    public AdventureModePredicate(List<BlockPredicate> p_336068_, boolean p_330877_) {
        this.f_316891_ = p_336068_;
        this.f_316745_ = p_330877_;
        this.f_314142_ = m_322824_(p_336068_);
    }

    private static boolean m_323601_(BlockInWorld p_330769_, @Nullable BlockInWorld p_330025_, boolean p_331117_) {
        if (p_330025_ == null || p_330769_.getState() != p_330025_.getState()) {
            return false;
        } else if (!p_331117_) {
            return true;
        } else if (p_330769_.getEntity() == null && p_330025_.getEntity() == null) {
            return true;
        } else if (p_330769_.getEntity() != null && p_330025_.getEntity() != null) {
            RegistryAccess registryaccess = p_330769_.getLevel().registryAccess();
            return Objects.equals(p_330769_.getEntity().saveWithId(registryaccess), p_330025_.getEntity().saveWithId(registryaccess));
        } else {
            return false;
        }
    }

    public boolean m_322201_(BlockInWorld p_333716_) {
        if (m_323601_(p_333716_, this.f_314808_, this.f_314757_)) {
            return this.f_316657_;
        } else {
            this.f_314808_ = p_333716_;
            this.f_314757_ = false;

            for (BlockPredicate blockpredicate : this.f_316891_) {
                if (blockpredicate.m_321461_(p_333716_)) {
                    this.f_314757_ = this.f_314757_ | blockpredicate.m_324452_();
                    this.f_316657_ = true;
                    return true;
                }
            }

            this.f_316657_ = false;
            return false;
        }
    }

    public void m_318685_(Consumer<Component> p_334654_) {
        this.f_314142_.forEach(p_334654_);
    }

    public AdventureModePredicate m_322095_(boolean p_335029_) {
        return new AdventureModePredicate(this.f_316891_, p_335029_, this.f_314142_);
    }

    private static List<Component> m_322824_(List<BlockPredicate> p_328947_) {
        for (BlockPredicate blockpredicate : p_328947_) {
            if (blockpredicate.blocks().isEmpty()) {
                return List.of(f_314193_);
            }
        }

        return p_328947_.stream()
            .flatMap(p_333785_ -> p_333785_.blocks().orElseThrow().stream())
            .distinct()
            .map(p_335858_ -> (Component)p_335858_.value().getName().withStyle(ChatFormatting.DARK_GRAY))
            .toList();
    }

    public boolean m_324667_() {
        return this.f_316745_;
    }

    @Override
    public boolean equals(Object p_331232_) {
        if (this == p_331232_) {
            return true;
        } else {
            return !(p_331232_ instanceof AdventureModePredicate adventuremodepredicate)
                ? false
                : this.f_316891_.equals(adventuremodepredicate.f_316891_) && this.f_316745_ == adventuremodepredicate.f_316745_;
        }
    }

    @Override
    public int hashCode() {
        return this.f_316891_.hashCode() * 31 + (this.f_316745_ ? 1 : 0);
    }

    @Override
    public String toString() {
        return "AdventureModePredicate{predicates=" + this.f_316891_ + ", showInTooltip=" + this.f_316745_ + "}";
    }
}