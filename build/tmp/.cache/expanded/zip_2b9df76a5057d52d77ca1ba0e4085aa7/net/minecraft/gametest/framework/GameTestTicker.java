package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;

public class GameTestTicker {
    public static final GameTestTicker SINGLETON = new GameTestTicker();
    private final Collection<GameTestInfo> testInfos = Lists.newCopyOnWriteArrayList();
    @Nullable
    private GameTestRunner f_316743_;

    private GameTestTicker() {
    }

    public void add(GameTestInfo pTestInfo) {
        this.testInfos.add(pTestInfo);
    }

    public void clear() {
        this.testInfos.clear();
        if (this.f_316743_ != null) {
            this.f_316743_.m_321148_();
            this.f_316743_ = null;
        }
    }

    public void m_325071_(GameTestRunner p_328613_) {
        if (this.f_316743_ != null) {
            Util.logAndPauseIfInIde("The runner was already set in GameTestTicker");
        }

        this.f_316743_ = p_328613_;
    }

    public void tick() {
        if (this.f_316743_ != null) {
            this.testInfos.forEach(p_328686_ -> p_328686_.tick(this.f_316743_));
            this.testInfos.removeIf(GameTestInfo::isDone);
        }
    }
}