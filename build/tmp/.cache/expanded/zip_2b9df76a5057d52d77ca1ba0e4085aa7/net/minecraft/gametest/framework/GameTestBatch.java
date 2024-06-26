package net.minecraft.gametest.framework;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;

public record GameTestBatch(String name, Collection<GameTestInfo> f_314676_, Consumer<ServerLevel> beforeBatchFunction, Consumer<ServerLevel> afterBatchFunction) {
    public static final String DEFAULT_BATCH_NAME = "defaultBatch";

    public GameTestBatch(String name, Collection<GameTestInfo> f_314676_, Consumer<ServerLevel> beforeBatchFunction, Consumer<ServerLevel> afterBatchFunction) {
        if (f_314676_.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one GameTestInfo!");
        } else {
            this.name = name;
            this.f_314676_ = f_314676_;
            this.beforeBatchFunction = beforeBatchFunction;
            this.afterBatchFunction = afterBatchFunction;
        }
    }
}