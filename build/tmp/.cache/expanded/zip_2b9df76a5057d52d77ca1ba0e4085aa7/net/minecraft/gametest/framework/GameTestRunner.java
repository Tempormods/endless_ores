package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class GameTestRunner {
    public static final int DEFAULT_TESTS_PER_ROW = 8;
    private static final Logger f_314662_ = LogUtils.getLogger();
    final ServerLevel f_315849_;
    private final GameTestTicker f_315775_;
    private final List<GameTestInfo> f_315746_;
    private ImmutableList<GameTestBatch> f_315154_;
    final List<GameTestBatchListener> f_315622_ = Lists.newArrayList();
    private final List<GameTestInfo> f_316198_ = Lists.newArrayList();
    private final GameTestRunner.GameTestBatcher f_316465_;
    private boolean f_315783_ = true;
    @Nullable
    GameTestBatch f_313970_;
    private final GameTestRunner.StructureSpawner f_316496_;
    private final GameTestRunner.StructureSpawner f_315308_;

    protected GameTestRunner(
        GameTestRunner.GameTestBatcher p_332546_,
        Collection<GameTestBatch> p_332555_,
        ServerLevel p_331060_,
        GameTestTicker p_335326_,
        GameTestRunner.StructureSpawner p_336219_,
        GameTestRunner.StructureSpawner p_330306_
    ) {
        this.f_315849_ = p_331060_;
        this.f_315775_ = p_335326_;
        this.f_316465_ = p_332546_;
        this.f_316496_ = p_336219_;
        this.f_315308_ = p_330306_;
        this.f_315154_ = ImmutableList.copyOf(p_332555_);
        this.f_315746_ = this.f_315154_.stream().flatMap(p_325950_ -> p_325950_.f_314676_().stream()).collect(Util.m_323807_());
        p_335326_.m_325071_(this);
        this.f_315746_.forEach(p_325945_ -> p_325945_.addListener(new ReportGameListener()));
    }

    public List<GameTestInfo> m_320202_() {
        return this.f_315746_;
    }

    public void m_323089_() {
        this.f_315783_ = false;
        this.m_324502_(0);
    }

    public void m_321148_() {
        this.f_315783_ = true;
        if (this.f_313970_ != null) {
            this.f_313970_.afterBatchFunction().accept(this.f_315849_);
        }
    }

    public void m_321090_(GameTestInfo p_335947_) {
        GameTestInfo gametestinfo = p_335947_.m_325037_();
        p_335947_.m_324070_().forEach(p_325948_ -> p_325948_.m_177684_(p_335947_, gametestinfo, this));
        this.f_315746_.add(gametestinfo);
        this.f_316198_.add(gametestinfo);
        if (this.f_315783_) {
            this.m_319427_();
        }
    }

    void m_324502_(final int p_336310_) {
        if (p_336310_ >= this.f_315154_.size()) {
            this.m_319427_();
        } else {
            this.f_313970_ = this.f_315154_.get(p_336310_);
            Collection<GameTestInfo> collection = this.m_320995_(this.f_313970_.f_314676_());
            String s = this.f_313970_.name();
            f_314662_.info("Running test batch '{}' ({} tests)...", s, collection.size());
            this.f_313970_.beforeBatchFunction().accept(this.f_315849_);
            this.f_315622_.forEach(p_325951_ -> p_325951_.m_318675_(this.f_313970_));
            final MultipleTestTracker multipletesttracker = new MultipleTestTracker();
            collection.forEach(multipletesttracker::addTestToTrack);
            multipletesttracker.addListener(new GameTestListener() {
                private void m_324014_() {
                    if (multipletesttracker.isDone()) {
                        GameTestRunner.this.f_313970_.afterBatchFunction().accept(GameTestRunner.this.f_315849_);
                        GameTestRunner.this.f_315622_.forEach(p_329497_ -> p_329497_.m_320803_(GameTestRunner.this.f_313970_));
                        LongSet longset = new LongArraySet(GameTestRunner.this.f_315849_.getForcedChunks());
                        longset.forEach(p_328493_ -> GameTestRunner.this.f_315849_.setChunkForced(ChunkPos.getX(p_328493_), ChunkPos.getZ(p_328493_), false));
                        GameTestRunner.this.m_324502_(p_336310_ + 1);
                    }
                }

                @Override
                public void testStructureLoaded(GameTestInfo p_336002_) {
                }

                @Override
                public void testPassed(GameTestInfo p_334410_, GameTestRunner p_329201_) {
                    this.m_324014_();
                }

                @Override
                public void testFailed(GameTestInfo p_335430_, GameTestRunner p_330830_) {
                    this.m_324014_();
                }

                @Override
                public void m_177684_(GameTestInfo p_329460_, GameTestInfo p_328079_, GameTestRunner p_334962_) {
                }
            });
            collection.forEach(this.f_315775_::add);
        }
    }

    private void m_319427_() {
        if (!this.f_316198_.isEmpty()) {
            f_314662_.info(
                "Starting re-run of tests: {}", this.f_316198_.stream().map(p_325949_ -> p_325949_.getTestFunction().testName()).collect(Collectors.joining(", "))
            );
            this.f_315154_ = ImmutableList.copyOf(this.f_316465_.m_322478_(this.f_316198_));
            this.f_316198_.clear();
            this.f_315783_ = false;
            this.m_324502_(0);
        } else {
            this.f_315154_ = ImmutableList.of();
            this.f_315783_ = true;
        }
    }

    public void m_324189_(GameTestBatchListener p_329355_) {
        this.f_315622_.add(p_329355_);
    }

    private Collection<GameTestInfo> m_320995_(Collection<GameTestInfo> p_335557_) {
        return p_335557_.stream().map(this::m_319278_).flatMap(Optional::stream).toList();
    }

    private Optional<GameTestInfo> m_319278_(GameTestInfo p_330408_) {
        return p_330408_.getStructureBlockPos() == null ? this.f_315308_.m_321592_(p_330408_) : this.f_316496_.m_321592_(p_330408_);
    }

    public static void clearMarkers(ServerLevel pServerLevel) {
        DebugPackets.sendGameTestClearPacket(pServerLevel);
    }

    public static class Builder {
        private final ServerLevel f_313924_;
        private final GameTestTicker f_314246_ = GameTestTicker.SINGLETON;
        private final GameTestRunner.GameTestBatcher f_314509_ = GameTestBatchFactory.m_322110_();
        private final GameTestRunner.StructureSpawner f_314482_ = GameTestRunner.StructureSpawner.f_314859_;
        private GameTestRunner.StructureSpawner f_316948_ = GameTestRunner.StructureSpawner.f_314095_;
        private final Collection<GameTestBatch> f_315759_;

        private Builder(Collection<GameTestBatch> p_329875_, ServerLevel p_329864_) {
            this.f_315759_ = p_329875_;
            this.f_313924_ = p_329864_;
        }

        public static GameTestRunner.Builder m_321232_(Collection<GameTestBatch> p_329486_, ServerLevel p_336096_) {
            return new GameTestRunner.Builder(p_329486_, p_336096_);
        }

        public static GameTestRunner.Builder m_319523_(Collection<GameTestInfo> p_335004_, ServerLevel p_328084_) {
            return m_321232_(GameTestBatchFactory.m_322110_().m_322478_(p_335004_), p_328084_);
        }

        public GameTestRunner.Builder m_322147_(GameTestRunner.StructureSpawner p_329789_) {
            this.f_316948_ = p_329789_;
            return this;
        }

        public GameTestRunner m_322128_() {
            return new GameTestRunner(this.f_314509_, this.f_315759_, this.f_313924_, this.f_314246_, this.f_314482_, this.f_316948_);
        }
    }

    public interface GameTestBatcher {
        Collection<GameTestBatch> m_322478_(Collection<GameTestInfo> p_335979_);
    }

    public interface StructureSpawner {
        GameTestRunner.StructureSpawner f_314859_ = p_329780_ -> Optional.of(p_329780_.m_306517_().m_322668_().startExecution(1));
        GameTestRunner.StructureSpawner f_314095_ = p_329287_ -> Optional.empty();

        Optional<GameTestInfo> m_321592_(GameTestInfo p_334318_);
    }
}