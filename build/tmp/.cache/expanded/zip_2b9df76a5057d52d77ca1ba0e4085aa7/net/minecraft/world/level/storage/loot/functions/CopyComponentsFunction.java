package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
    public static final MapCodec<CopyComponentsFunction> f_315830_ = RecordCodecBuilder.mapCodec(
        p_334725_ -> commonFields(p_334725_)
                .and(
                    p_334725_.group(
                        CopyComponentsFunction.Source.f_314521_.fieldOf("source").forGetter(p_329984_ -> p_329984_.f_315071_),
                        DataComponentType.f_314889_.listOf().optionalFieldOf("include").forGetter(p_330902_ -> p_330902_.f_316589_),
                        DataComponentType.f_314889_.listOf().optionalFieldOf("exclude").forGetter(p_331318_ -> p_331318_.f_314754_)
                    )
                )
                .apply(p_334725_, CopyComponentsFunction::new)
    );
    private final CopyComponentsFunction.Source f_315071_;
    private final Optional<List<DataComponentType<?>>> f_316589_;
    private final Optional<List<DataComponentType<?>>> f_314754_;
    private final Predicate<DataComponentType<?>> f_315946_;

    CopyComponentsFunction(
        List<LootItemCondition> p_332739_,
        CopyComponentsFunction.Source p_333486_,
        Optional<List<DataComponentType<?>>> p_332029_,
        Optional<List<DataComponentType<?>>> p_329656_
    ) {
        super(p_332739_);
        this.f_315071_ = p_333486_;
        this.f_316589_ = p_332029_.map(List::copyOf);
        this.f_314754_ = p_329656_.map(List::copyOf);
        List<Predicate<DataComponentType<?>>> list = new ArrayList<>(2);
        p_329656_.ifPresent(p_329848_ -> list.add(p_331276_ -> !p_329848_.contains(p_331276_)));
        p_332029_.ifPresent(p_331486_ -> list.add(p_331486_::contains));
        this.f_315946_ = Util.m_322468_(list);
    }

    @Override
    public LootItemFunctionType<CopyComponentsFunction> getType() {
        return LootItemFunctions.f_315087_;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.f_315071_.m_319407_();
    }

    @Override
    public ItemStack run(ItemStack p_329465_, LootContext p_328771_) {
        DataComponentMap datacomponentmap = this.f_315071_.m_319887_(p_328771_);
        p_329465_.m_323474_(datacomponentmap.m_322426_(this.f_315946_));
        return p_329465_;
    }

    public static CopyComponentsFunction.Builder m_324952_(CopyComponentsFunction.Source p_331082_) {
        return new CopyComponentsFunction.Builder(p_331082_);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyComponentsFunction.Builder> {
        private final CopyComponentsFunction.Source f_314605_;
        private Optional<ImmutableList.Builder<DataComponentType<?>>> f_316886_ = Optional.empty();
        private Optional<ImmutableList.Builder<DataComponentType<?>>> f_316291_ = Optional.empty();

        Builder(CopyComponentsFunction.Source p_336396_) {
            this.f_314605_ = p_336396_;
        }

        public CopyComponentsFunction.Builder m_323761_(DataComponentType<?> p_329871_) {
            if (this.f_316886_.isEmpty()) {
                this.f_316886_ = Optional.of(ImmutableList.builder());
            }

            this.f_316886_.get().add(p_329871_);
            return this;
        }

        public CopyComponentsFunction.Builder m_319935_(DataComponentType<?> p_332922_) {
            if (this.f_316291_.isEmpty()) {
                this.f_316291_ = Optional.of(ImmutableList.builder());
            }

            this.f_316291_.get().add(p_332922_);
            return this;
        }

        protected CopyComponentsFunction.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyComponentsFunction(
                this.getConditions(), this.f_314605_, this.f_316886_.map(ImmutableList.Builder::build), this.f_316291_.map(ImmutableList.Builder::build)
            );
        }
    }

    public static enum Source implements StringRepresentable {
        BLOCK_ENTITY("block_entity");

        public static final Codec<CopyComponentsFunction.Source> f_314521_ = StringRepresentable.m_306774_(CopyComponentsFunction.Source::values);
        private final String f_314041_;

        private Source(final String p_331847_) {
            this.f_314041_ = p_331847_;
        }

        public DataComponentMap m_319887_(LootContext p_331544_) {
            switch (this) {
                case BLOCK_ENTITY:
                    BlockEntity blockentity = p_331544_.getParamOrNull(LootContextParams.BLOCK_ENTITY);
                    return blockentity != null ? blockentity.m_321843_() : DataComponentMap.f_314291_;
                default:
                    throw new MatchException(null, null);
            }
        }

        public Set<LootContextParam<?>> m_319407_() {
            switch (this) {
                case BLOCK_ENTITY:
                    return Set.of(LootContextParams.BLOCK_ENTITY);
                default:
                    throw new MatchException(null, null);
            }
        }

        @Override
        public String getSerializedName() {
            return this.f_314041_;
        }
    }
}