package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestListener {
    private int attempts = 0;
    private int successes = 0;

    public ReportGameListener() {
    }

    @Override
    public void testStructureLoaded(GameTestInfo pTestInfo) {
        spawnBeacon(pTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
        this.attempts++;
    }

    private void m_321376_(GameTestInfo p_333394_, GameTestRunner p_328423_, boolean p_328930_) {
        RetryOptions retryoptions = p_333394_.m_324328_();
        String s = String.format("[Run: %4d, Ok: %4d, Fail: %4d", this.attempts, this.successes, this.attempts - this.successes);
        if (!retryoptions.m_319667_()) {
            s = s + String.format(", Left: %4d", retryoptions.f_316969_() - this.attempts);
        }

        s = s + "]";
        String s1 = p_333394_.getTestName() + " " + (p_328930_ ? "passed" : "failed") + "! " + p_333394_.getRunTime() + "ms";
        String s2 = String.format("%-53s%s", s, s1);
        if (p_328930_) {
            reportPassed(p_333394_, s2);
        } else {
            say(p_333394_.getLevel(), ChatFormatting.RED, s2);
        }

        if (retryoptions.m_320775_(this.attempts, this.successes)) {
            p_328423_.m_321090_(p_333394_);
        }
    }

    @Override
    public void testPassed(GameTestInfo pTestInfo, GameTestRunner p_331098_) {
        this.successes++;
        if (pTestInfo.m_324328_().m_319078_()) {
            this.m_321376_(pTestInfo, p_331098_, true);
        } else if (!pTestInfo.isFlaky()) {
            reportPassed(pTestInfo, pTestInfo.getTestName() + " passed! (" + pTestInfo.getRunTime() + "ms)");
        } else {
            if (this.successes >= pTestInfo.requiredSuccesses()) {
                reportPassed(pTestInfo, pTestInfo + " passed " + this.successes + " times of " + this.attempts + " attempts.");
            } else {
                say(
                    pTestInfo.getLevel(),
                    ChatFormatting.GREEN,
                    "Flaky test " + pTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes
                );
                p_331098_.m_321090_(pTestInfo);
            }
        }
    }

    @Override
    public void testFailed(GameTestInfo pTestInfo, GameTestRunner p_330024_) {
        if (!pTestInfo.isFlaky()) {
            reportFailure(pTestInfo, pTestInfo.getError());
            if (pTestInfo.m_324328_().m_319078_()) {
                this.m_321376_(pTestInfo, p_330024_, false);
            }
        } else {
            TestFunction testfunction = pTestInfo.getTestFunction();
            String s = "Flaky test " + pTestInfo + " failed, attempt: " + this.attempts + "/" + testfunction.maxAttempts();
            if (testfunction.requiredSuccesses() > 1) {
                s = s + ", successes: " + this.successes + " (" + testfunction.requiredSuccesses() + " required)";
            }

            say(pTestInfo.getLevel(), ChatFormatting.YELLOW, s);
            if (pTestInfo.maxAttempts() - this.attempts + this.successes >= pTestInfo.requiredSuccesses()) {
                p_330024_.m_321090_(pTestInfo);
            } else {
                reportFailure(pTestInfo, new ExhaustedAttemptsException(this.attempts, this.successes, pTestInfo));
            }
        }
    }

    @Override
    public void m_177684_(GameTestInfo p_330084_, GameTestInfo p_327991_, GameTestRunner p_334385_) {
        p_327991_.addListener(this);
    }

    public static void reportPassed(GameTestInfo pTestInfo, String pMessage) {
        spawnBeacon(pTestInfo, Blocks.LIME_STAINED_GLASS);
        visualizePassedTest(pTestInfo, pMessage);
    }

    private static void visualizePassedTest(GameTestInfo pTestInfo, String pMessage) {
        say(pTestInfo.getLevel(), ChatFormatting.GREEN, pMessage);
        GlobalTestReporter.onTestSuccess(pTestInfo);
    }

    protected static void reportFailure(GameTestInfo pTestInfo, Throwable pError) {
        spawnBeacon(pTestInfo, pTestInfo.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
        spawnLectern(pTestInfo, Util.describeError(pError));
        visualizeFailedTest(pTestInfo, pError);
    }

    protected static void visualizeFailedTest(GameTestInfo pTestInfo, Throwable pError) {
        String s = pError.getMessage() + (pError.getCause() == null ? "" : " cause: " + Util.describeError(pError.getCause()));
        String s1 = (pTestInfo.isRequired() ? "" : "(optional) ") + pTestInfo.getTestName() + " failed! " + s;
        say(pTestInfo.getLevel(), pTestInfo.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, s1);
        Throwable throwable = MoreObjects.firstNonNull(ExceptionUtils.getRootCause(pError), pError);
        if (throwable instanceof GameTestAssertPosException gametestassertposexception) {
            showRedBox(pTestInfo.getLevel(), gametestassertposexception.getAbsolutePos(), gametestassertposexception.getMessageToShowAtBlock());
        }

        GlobalTestReporter.onTestFailed(pTestInfo);
    }

    protected static void spawnBeacon(GameTestInfo pTestInfo, Block pBlock) {
        ServerLevel serverlevel = pTestInfo.getLevel();
        BlockPos blockpos = pTestInfo.getStructureBlockPos();
        BlockPos blockpos1 = new BlockPos(-1, -2, -1);
        BlockPos blockpos2 = StructureTemplate.transform(blockpos.offset(blockpos1), Mirror.NONE, pTestInfo.getRotation(), blockpos);
        serverlevel.setBlockAndUpdate(blockpos2, Blocks.BEACON.defaultBlockState().rotate(pTestInfo.getRotation()));
        BlockPos blockpos3 = blockpos2.offset(0, 1, 0);
        serverlevel.setBlockAndUpdate(blockpos3, pBlock.defaultBlockState());

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos blockpos4 = blockpos2.offset(i, -1, j);
                serverlevel.setBlockAndUpdate(blockpos4, Blocks.IRON_BLOCK.defaultBlockState());
            }
        }
    }

    private static void spawnLectern(GameTestInfo pTestInfo, String pMessage) {
        ServerLevel serverlevel = pTestInfo.getLevel();
        BlockPos blockpos = pTestInfo.getStructureBlockPos();
        BlockPos blockpos1 = new BlockPos(-1, 0, -1);
        BlockPos blockpos2 = StructureTemplate.transform(blockpos.offset(blockpos1), Mirror.NONE, pTestInfo.getRotation(), blockpos);
        serverlevel.setBlockAndUpdate(blockpos2, Blocks.LECTERN.defaultBlockState().rotate(pTestInfo.getRotation()));
        BlockState blockstate = serverlevel.getBlockState(blockpos2);
        ItemStack itemstack = createBook(pTestInfo.getTestName(), pTestInfo.isRequired(), pMessage);
        LecternBlock.tryPlaceBook(null, serverlevel, blockpos2, blockstate, itemstack);
    }

    private static ItemStack createBook(String pTestName, boolean pRequired, String pMessage) {
        StringBuffer stringbuffer = new StringBuffer();
        Arrays.stream(pTestName.split("\\.")).forEach(p_177716_ -> stringbuffer.append(p_177716_).append('\n'));
        if (!pRequired) {
            stringbuffer.append("(optional)\n");
        }

        stringbuffer.append("-------------------\n");
        ItemStack itemstack = new ItemStack(Items.WRITABLE_BOOK);
        itemstack.m_322496_(DataComponents.f_314472_, new WritableBookContent(List.of(Filterable.m_323001_(stringbuffer + pMessage))));
        return itemstack;
    }

    protected static void say(ServerLevel pServerLevel, ChatFormatting pFormatting, String pMessage) {
        pServerLevel.getPlayers(p_177705_ -> true).forEach(p_177709_ -> p_177709_.sendSystemMessage(Component.literal(pMessage).withStyle(pFormatting)));
    }

    private static void showRedBox(ServerLevel pServerLevel, BlockPos pPos, String pDisplayMessage) {
        DebugPackets.sendGameTestAddMarker(pServerLevel, pPos, pDisplayMessage, -2130771968, Integer.MAX_VALUE);
    }
}