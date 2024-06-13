package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface WeatheringCopper extends ChangeOverTimeBlock<WeatheringCopper.WeatherState> {
    Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(
        () -> ImmutableBiMap.<Block, Block>builder()
                .put(Blocks.COPPER_BLOCK, Blocks.EXPOSED_COPPER)
                .put(Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER)
                .put(Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER)
                .put(Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER)
                .put(Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER)
                .put(Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER)
                .put(Blocks.f_302689_, Blocks.f_303448_)
                .put(Blocks.f_303448_, Blocks.f_302507_)
                .put(Blocks.f_302507_, Blocks.f_303811_)
                .put(Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB)
                .put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB)
                .put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB)
                .put(Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS)
                .put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS)
                .put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS)
                .put(Blocks.f_302565_, Blocks.f_303201_)
                .put(Blocks.f_303201_, Blocks.f_303010_)
                .put(Blocks.f_303010_, Blocks.f_303016_)
                .put(Blocks.f_303635_, Blocks.f_303609_)
                .put(Blocks.f_303609_, Blocks.f_302627_)
                .put(Blocks.f_302627_, Blocks.f_302247_)
                .put(Blocks.f_303215_, Blocks.f_302995_)
                .put(Blocks.f_302995_, Blocks.f_303236_)
                .put(Blocks.f_303236_, Blocks.f_303549_)
                .put(Blocks.f_302358_, Blocks.f_303271_)
                .put(Blocks.f_303271_, Blocks.f_303674_)
                .put(Blocks.f_303674_, Blocks.f_302668_)
                .build()
    );
    Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());

    static Optional<Block> getPrevious(Block pBlock) {
        return Optional.ofNullable(PREVIOUS_BY_BLOCK.get().get(pBlock));
    }

    static Block getFirst(Block pBlock) {
        Block block = pBlock;

        for (Block block1 = PREVIOUS_BY_BLOCK.get().get(pBlock); block1 != null; block1 = PREVIOUS_BY_BLOCK.get().get(block1)) {
            block = block1;
        }

        return block;
    }

    static Optional<BlockState> getPrevious(BlockState pState) {
        return getPrevious(pState.getBlock()).map(p_154903_ -> p_154903_.withPropertiesOf(pState));
    }

    static Optional<Block> getNext(Block pBlock) {
        return Optional.ofNullable(NEXT_BY_BLOCK.get().get(pBlock));
    }

    static BlockState getFirst(BlockState pState) {
        return getFirst(pState.getBlock()).withPropertiesOf(pState);
    }

    @Override
    default Optional<BlockState> getNext(BlockState pState) {
        return getNext(pState.getBlock()).map(p_154896_ -> p_154896_.withPropertiesOf(pState));
    }

    @Override
    default float getChanceModifier() {
        return this.getAge() == WeatheringCopper.WeatherState.UNAFFECTED ? 0.75F : 1.0F;
    }

    public static enum WeatherState implements StringRepresentable {
        UNAFFECTED("unaffected"),
        EXPOSED("exposed"),
        WEATHERED("weathered"),
        OXIDIZED("oxidized");

        public static final Codec<WeatheringCopper.WeatherState> f_302372_ = StringRepresentable.fromEnum(WeatheringCopper.WeatherState::values);
        private final String f_303684_;

        private WeatherState(final String p_309663_) {
            this.f_303684_ = p_309663_;
        }

        @Override
        public String getSerializedName() {
            return this.f_303684_;
        }
    }
}