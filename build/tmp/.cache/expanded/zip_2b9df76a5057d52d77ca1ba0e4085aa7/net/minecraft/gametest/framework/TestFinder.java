package net.minecraft.gametest.framework;

import com.mojang.brigadier.context.CommandContext;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;

public class TestFinder<T> implements StructureBlockPosFinder, TestFunctionFinder {
    static final TestFunctionFinder f_315355_ = Stream::empty;
    static final StructureBlockPosFinder f_315670_ = Stream::empty;
    private final TestFunctionFinder f_315412_;
    private final StructureBlockPosFinder f_315198_;
    private final CommandSourceStack f_315253_;
    private final Function<TestFinder<T>, T> f_314837_;

    @Override
    public Stream<BlockPos> m_319645_() {
        return this.f_315198_.m_319645_();
    }

    TestFinder(CommandSourceStack p_332130_, Function<TestFinder<T>, T> p_332876_, TestFunctionFinder p_330000_, StructureBlockPosFinder p_332515_) {
        this.f_315253_ = p_332130_;
        this.f_314837_ = p_332876_;
        this.f_315412_ = p_330000_;
        this.f_315198_ = p_332515_;
    }

    T m_323683_() {
        return this.f_314837_.apply(this);
    }

    public CommandSourceStack m_324045_() {
        return this.f_315253_;
    }

    @Override
    public Stream<TestFunction> m_318842_() {
        return this.f_315412_.m_318842_();
    }

    public static class Builder<T> {
        private final Function<TestFinder<T>, T> f_314579_;
        private final UnaryOperator<Supplier<Stream<TestFunction>>> f_314868_;
        private final UnaryOperator<Supplier<Stream<BlockPos>>> f_316257_;

        public Builder(Function<TestFinder<T>, T> p_329391_) {
            this.f_314579_ = p_329391_;
            this.f_314868_ = p_333647_ -> p_333647_;
            this.f_316257_ = p_327811_ -> p_327811_;
        }

        private Builder(
            Function<TestFinder<T>, T> p_329078_, UnaryOperator<Supplier<Stream<TestFunction>>> p_334250_, UnaryOperator<Supplier<Stream<BlockPos>>> p_334300_
        ) {
            this.f_314579_ = p_329078_;
            this.f_314868_ = p_334250_;
            this.f_316257_ = p_334300_;
        }

        public TestFinder.Builder<T> m_319044_(int p_329806_) {
            return new TestFinder.Builder<>(this.f_314579_, m_321615_(p_329806_), m_321615_(p_329806_));
        }

        private static <Q> UnaryOperator<Supplier<Stream<Q>>> m_321615_(int p_334571_) {
            return p_333976_ -> {
                List<Q> list = new LinkedList<>();
                List<Q> list1 = ((Stream)p_333976_.get()).toList();

                for (int i = 0; i < p_334571_; i++) {
                    list.addAll(list1);
                }

                return list::stream;
            };
        }

        private T m_323262_(CommandSourceStack p_334153_, TestFunctionFinder p_330203_, StructureBlockPosFinder p_328202_) {
            return new TestFinder<>(p_334153_, this.f_314579_, this.f_314868_.apply(p_330203_::m_318842_)::get, this.f_316257_.apply(p_328202_::m_319645_)::get)
                .m_323683_();
        }

        public T m_320082_(CommandContext<CommandSourceStack> p_330481_, int p_334173_) {
            CommandSourceStack commandsourcestack = p_330481_.getSource();
            BlockPos blockpos = BlockPos.containing(commandsourcestack.getPosition());
            return this.m_323262_(commandsourcestack, TestFinder.f_315355_, () -> StructureUtils.findStructureBlocks(blockpos, p_334173_, commandsourcestack.getLevel()));
        }

        public T m_321677_(CommandContext<CommandSourceStack> p_332654_) {
            CommandSourceStack commandsourcestack = p_332654_.getSource();
            BlockPos blockpos = BlockPos.containing(commandsourcestack.getPosition());
            return this.m_323262_(
                commandsourcestack, TestFinder.f_315355_, () -> StructureUtils.findNearestStructureBlock(blockpos, 15, commandsourcestack.getLevel()).stream()
            );
        }

        public T m_321386_(CommandContext<CommandSourceStack> p_335428_) {
            CommandSourceStack commandsourcestack = p_335428_.getSource();
            BlockPos blockpos = BlockPos.containing(commandsourcestack.getPosition());
            return this.m_323262_(commandsourcestack, TestFinder.f_315355_, () -> StructureUtils.findStructureBlocks(blockpos, 200, commandsourcestack.getLevel()));
        }

        public T m_322936_(CommandContext<CommandSourceStack> p_328071_) {
            CommandSourceStack commandsourcestack = p_328071_.getSource();
            return this.m_323262_(
                commandsourcestack,
                TestFinder.f_315355_,
                () -> StructureUtils.m_320514_(
                        BlockPos.containing(commandsourcestack.getPosition()), commandsourcestack.getPlayer().getCamera(), commandsourcestack.getLevel()
                    )
            );
        }

        public T m_320211_(CommandContext<CommandSourceStack> p_331369_) {
            return this.m_323262_(
                p_331369_.getSource(), () -> GameTestRegistry.getAllTestFunctions().stream().filter(p_334467_ -> !p_334467_.f_315754_()), TestFinder.f_315670_
            );
        }

        public T m_319649_(CommandContext<CommandSourceStack> p_333766_, String p_332600_) {
            return this.m_323262_(
                p_333766_.getSource(), () -> GameTestRegistry.getTestFunctionsForClassName(p_332600_).filter(p_328668_ -> !p_328668_.f_315754_()), TestFinder.f_315670_
            );
        }

        public T m_321527_(CommandContext<CommandSourceStack> p_332736_, boolean p_336399_) {
            return this.m_323262_(
                p_332736_.getSource(), () -> GameTestRegistry.getLastFailedTests().filter(p_328598_ -> !p_336399_ || p_328598_.required()), TestFinder.f_315670_
            );
        }

        public T m_318690_(CommandContext<CommandSourceStack> p_329167_, String p_334913_) {
            return this.m_323262_(p_329167_.getSource(), () -> Stream.of(TestFunctionArgument.getTestFunction(p_329167_, p_334913_)), TestFinder.f_315670_);
        }

        public T m_320452_(CommandContext<CommandSourceStack> p_330730_, String p_336390_) {
            CommandSourceStack commandsourcestack = p_330730_.getSource();
            BlockPos blockpos = BlockPos.containing(commandsourcestack.getPosition());
            return this.m_323262_(
                commandsourcestack, TestFinder.f_315355_, () -> StructureUtils.m_322152_(blockpos, 1024, commandsourcestack.getLevel(), p_336390_)
            );
        }

        public T m_319961_(CommandContext<CommandSourceStack> p_331687_) {
            return this.m_321527_(p_331687_, false);
        }
    }
}