package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RecoverWorldDataScreen extends Screen {
    private static final Logger f_303328_ = LogUtils.getLogger();
    private static final int f_303518_ = 25;
    private static final Component f_303026_ = Component.translatable("recover_world.title").withStyle(ChatFormatting.BOLD);
    private static final Component f_302299_ = Component.translatable("recover_world.bug_tracker");
    private static final Component f_303641_ = Component.translatable("recover_world.restore");
    private static final Component f_303235_ = Component.translatable("recover_world.no_fallback");
    private static final Component f_303243_ = Component.translatable("recover_world.done.title");
    private static final Component f_303559_ = Component.translatable("recover_world.done.success");
    private static final Component f_303054_ = Component.translatable("recover_world.done.failed");
    private static final Component f_302441_ = Component.translatable("recover_world.issue.none").withStyle(ChatFormatting.GREEN);
    private static final Component f_302831_ = Component.translatable("recover_world.issue.missing_file").withStyle(ChatFormatting.RED);
    private final BooleanConsumer f_303785_;
    private final LinearLayout f_303826_ = LinearLayout.vertical().spacing(8);
    private final Component f_302472_;
    private final MultiLineTextWidget f_302943_;
    private final MultiLineTextWidget f_302287_;
    private final LevelStorageSource.LevelStorageAccess f_302221_;

    public RecoverWorldDataScreen(Minecraft p_310416_, BooleanConsumer p_312140_, LevelStorageSource.LevelStorageAccess p_310102_) {
        super(f_303026_);
        this.f_303785_ = p_312140_;
        this.f_302472_ = Component.translatable("recover_world.message", Component.literal(p_310102_.getLevelId()).withStyle(ChatFormatting.GRAY));
        this.f_302943_ = new MultiLineTextWidget(this.f_302472_, p_310416_.font);
        this.f_302221_ = p_310102_;
        Exception exception = this.m_307260_(p_310102_, false);
        Exception exception1 = this.m_307260_(p_310102_, true);
        Component component = Component.empty()
            .append(this.m_306235_(p_310102_, false, exception))
            .append("\n")
            .append(this.m_306235_(p_310102_, true, exception1));
        this.f_302287_ = new MultiLineTextWidget(component, p_310416_.font);
        boolean flag = exception != null && exception1 == null;
        this.f_303826_.defaultCellSetting().alignHorizontallyCenter();
        this.f_303826_.addChild(new StringWidget(this.title, p_310416_.font));
        this.f_303826_.addChild(this.f_302943_.setCentered(true));
        this.f_303826_.addChild(this.f_302287_);
        LinearLayout linearlayout = LinearLayout.horizontal().spacing(5);
        linearlayout.addChild(
            Button.builder(f_302299_, ConfirmLinkScreen.confirmLink(this, "https://aka.ms/snapshotbugs?ref=game")).size(120, 20).build()
        );
        linearlayout.addChild(
                Button.builder(f_303641_, p_311022_ -> this.m_307474_(p_310416_))
                    .size(120, 20)
                    .tooltip(flag ? null : Tooltip.create(f_303235_))
                    .build()
            )
            .active = flag;
        this.f_303826_.addChild(linearlayout);
        this.f_303826_.addChild(Button.builder(CommonComponents.GUI_BACK, p_311773_ -> this.onClose()).size(120, 20).build());
        this.f_303826_.visitWidgets(this::addRenderableWidget);
    }

    private void m_307474_(Minecraft p_311355_) {
        Exception exception = this.m_307260_(this.f_302221_, false);
        Exception exception1 = this.m_307260_(this.f_302221_, true);
        if (exception != null && exception1 == null) {
            p_311355_.forceSetScreen(new GenericMessageScreen(Component.translatable("recover_world.restoring")));
            EditWorldScreen.makeBackupAndShowToast(this.f_302221_);
            if (this.f_302221_.m_305486_()) {
                p_311355_.setScreen(new ConfirmScreen(this.f_303785_, f_303243_, f_303559_, CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
            } else {
                p_311355_.setScreen(new AlertScreen(() -> this.f_303785_.accept(false), f_303243_, f_303054_));
            }
        } else {
            f_303328_.error(
                "Failed to recover world, files not as expected. level.dat: {}, level.dat_old: {}",
                exception != null ? exception.getMessage() : "no issues",
                exception1 != null ? exception1.getMessage() : "no issues"
            );
            p_311355_.setScreen(new AlertScreen(() -> this.f_303785_.accept(false), f_303243_, f_303054_));
        }
    }

    private Component m_306235_(LevelStorageSource.LevelStorageAccess p_311955_, boolean p_311169_, @Nullable Exception p_312117_) {
        if (p_311169_ && p_312117_ instanceof FileNotFoundException) {
            return Component.empty();
        } else {
            MutableComponent mutablecomponent = Component.empty();
            Instant instant = p_311955_.m_306206_(p_311169_);
            MutableComponent mutablecomponent1 = instant != null
                ? Component.literal(WorldSelectionList.DATE_FORMAT.format(instant))
                : Component.translatable("recover_world.state_entry.unknown");
            mutablecomponent.append(Component.translatable("recover_world.state_entry", mutablecomponent1.withStyle(ChatFormatting.GRAY)));
            if (p_312117_ == null) {
                mutablecomponent.append(f_302441_);
            } else if (p_312117_ instanceof FileNotFoundException) {
                mutablecomponent.append(f_302831_);
            } else if (p_312117_ instanceof ReportedNbtException) {
                mutablecomponent.append(Component.literal(p_312117_.getCause().toString()).withStyle(ChatFormatting.RED));
            } else {
                mutablecomponent.append(Component.literal(p_312117_.toString()).withStyle(ChatFormatting.RED));
            }

            return mutablecomponent;
        }
    }

    @Nullable
    private Exception m_307260_(LevelStorageSource.LevelStorageAccess p_311404_, boolean p_311931_) {
        try {
            if (!p_311931_) {
                p_311404_.getSummary(p_311404_.m_307464_());
            } else {
                p_311404_.getSummary(p_311404_.m_305112_());
            }

            return null;
        } catch (NbtException | ReportedNbtException | IOException ioexception) {
            return ioexception;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.f_302287_.setMaxWidth(this.width - 50);
        this.f_302943_.setMaxWidth(this.width - 50);
        this.f_303826_.arrangeElements();
        FrameLayout.centerInRectangle(this.f_303826_, this.getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.f_302472_);
    }

    @Override
    public void onClose() {
        this.f_303785_.accept(false);
    }
}