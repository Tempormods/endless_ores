package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName").withStyle(ChatFormatting.GRAY);
    private static final Component f_302417_ = Component.translatable("selectWorld.edit.resetIcon");
    private static final Component f_303595_ = Component.translatable("selectWorld.edit.openFolder");
    private static final Component f_303784_ = Component.translatable("selectWorld.edit.backup");
    private static final Component f_303177_ = Component.translatable("selectWorld.edit.backupFolder");
    private static final Component f_303852_ = Component.translatable("selectWorld.edit.optimize");
    private static final Component f_303218_ = Component.translatable("optimizeWorld.confirm.title");
    private static final Component f_302499_ = Component.translatable("optimizeWorld.confirm.description");
    private static final Component f_302402_ = Component.translatable("selectWorld.edit.save");
    private static final int f_303512_ = 200;
    private static final int f_303690_ = 4;
    private static final int f_303096_ = 98;
    private final LinearLayout f_303364_ = LinearLayout.vertical().spacing(5);
    private final BooleanConsumer callback;
    private final LevelStorageSource.LevelStorageAccess levelAccess;
    private final EditBox f_316278_;

    public static EditWorldScreen m_306966_(Minecraft p_312937_, LevelStorageSource.LevelStorageAccess p_310908_, BooleanConsumer p_311675_) throws IOException {
        LevelSummary levelsummary = p_310908_.getSummary(p_310908_.m_307464_());
        return new EditWorldScreen(p_312937_, p_310908_, levelsummary.getLevelName(), p_311675_);
    }

    private EditWorldScreen(Minecraft p_309397_, LevelStorageSource.LevelStorageAccess pLevelAccess, String p_312996_, BooleanConsumer pCallback) {
        super(Component.translatable("selectWorld.edit.title"));
        this.callback = pCallback;
        this.levelAccess = pLevelAccess;
        Font font = p_309397_.font;
        this.f_303364_.addChild(new SpacerElement(200, 20));
        this.f_303364_.addChild(new StringWidget(NAME_LABEL, font));
        this.f_316278_ = this.f_303364_.addChild(new EditBox(font, 200, 20, NAME_LABEL));
        this.f_316278_.setValue(p_312996_);
        LinearLayout linearlayout = LinearLayout.horizontal().spacing(4);
        Button button = linearlayout.addChild(Button.builder(f_302402_, p_325437_ -> this.onRename(this.f_316278_.getValue())).width(98).build());
        linearlayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, p_308233_ -> this.onClose()).width(98).build());
        this.f_316278_.setResponder(p_325436_ -> button.active = !StringUtil.m_320314_(p_325436_));
        this.f_303364_.addChild(Button.builder(f_302417_, p_308218_ -> {
            pLevelAccess.getIconFile().ifPresent(p_182594_ -> FileUtils.deleteQuietly(p_182594_.toFile()));
            p_308218_.active = false;
        }).width(200).build()).active = pLevelAccess.getIconFile().filter(p_182587_ -> Files.isRegularFile(p_182587_)).isPresent();
        this.f_303364_
            .addChild(
                Button.builder(f_303595_, p_308223_ -> Util.getPlatform().openFile(pLevelAccess.getLevelPath(LevelResource.ROOT).toFile()))
                    .width(200)
                    .build()
            );
        this.f_303364_.addChild(Button.builder(f_303784_, p_308216_ -> {
            boolean flag = makeBackupAndShowToast(pLevelAccess);
            this.callback.accept(!flag);
        }).width(200).build());
        this.f_303364_.addChild(Button.builder(f_303177_, p_308237_ -> {
            LevelStorageSource levelstoragesource = p_309397_.getLevelSource();
            Path path = levelstoragesource.getBackupPath();

            try {
                FileUtil.createDirectoriesSafe(path);
            } catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }

            Util.getPlatform().openFile(path.toFile());
        }).width(200).build());
        this.f_303364_
            .addChild(
                Button.builder(f_303852_, p_308221_ -> p_309397_.setScreen(new BackupConfirmScreen(() -> p_309397_.setScreen(this), (p_308228_, p_308229_) -> {
                        if (p_308228_) {
                            makeBackupAndShowToast(pLevelAccess);
                        }

                        p_309397_.setScreen(OptimizeWorldScreen.create(p_309397_, this.callback, p_309397_.getFixerUpper(), pLevelAccess, p_308229_));
                    }, f_303218_, f_302499_, true))).width(200).build()
            );
        this.f_303364_.addChild(new SpacerElement(200, 20));
        this.f_303364_.addChild(linearlayout);
        this.f_303364_.visitWidgets(p_325434_ -> {
            AbstractWidget abstractwidget = this.addRenderableWidget(p_325434_);
        });
    }

    @Override
    protected void m_318615_() {
        this.setInitialFocus(this.f_316278_);
    }

    @Override
    protected void init() {
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.f_303364_.arrangeElements();
        FrameLayout.centerInRectangle(this.f_303364_, this.getRectangle());
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    private void onRename(String p_312476_) {
        try {
            this.levelAccess.renameLevel(p_312476_);
        } catch (NbtException | ReportedNbtException | IOException ioexception) {
            LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), ioexception);
            SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
        }

        this.callback.accept(true);
    }

    public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess pLevelAccess) {
        long i = 0L;
        IOException ioexception = null;

        try {
            i = pLevelAccess.makeWorldBackup();
        } catch (IOException ioexception1) {
            ioexception = ioexception1;
        }

        if (ioexception != null) {
            Component component2 = Component.translatable("selectWorld.edit.backupFailed");
            Component component3 = Component.literal(ioexception.getMessage());
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.f_302937_, component2, component3));
            return false;
        } else {
            Component component = Component.translatable("selectWorld.edit.backupCreated", pLevelAccess.getLevelId());
            Component component1 = Component.translatable("selectWorld.edit.backupSize", Mth.ceil((double)i / 1048576.0));
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.f_302937_, component, component1));
            return true;
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
    }
}