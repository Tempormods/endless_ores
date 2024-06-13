package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackupConfirmScreen extends Screen {
    private final Runnable f_303694_;
    protected final BackupConfirmScreen.Listener f_303055_;
    private final Component description;
    private final boolean promptForCacheErase;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    protected int id;
    private Checkbox eraseCache;

    public BackupConfirmScreen(Runnable p_309995_, BackupConfirmScreen.Listener pListener, Component pTitle, Component pDescription, boolean pPromptForCacheErase) {
        super(pTitle);
        this.f_303694_ = p_309995_;
        this.f_303055_ = pListener;
        this.description = pDescription;
        this.promptForCacheErase = pPromptForCacheErase;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, this.description, this.width - 50);
        int i = (this.message.getLineCount() + 1) * 9;
        this.addRenderableWidget(
            Button.builder(Component.translatable("selectWorld.backupJoinConfirmButton"), p_308190_ -> this.f_303055_.proceed(true, this.eraseCache.selected()))
                .bounds(this.width / 2 - 155, 100 + i, 150, 20)
                .build()
        );
        this.addRenderableWidget(
            Button.builder(Component.translatable("selectWorld.backupJoinSkipButton"), p_308188_ -> this.f_303055_.proceed(false, this.eraseCache.selected()))
                .bounds(this.width / 2 - 155 + 160, 100 + i, 150, 20)
                .build()
        );
        this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_CANCEL, p_308189_ -> this.f_303694_.run())
                .bounds(this.width / 2 - 155 + 80, 124 + i, 150, 20)
                .build()
        );
        this.eraseCache = Checkbox.m_306644_(Component.translatable("selectWorld.backupEraseCache"), this.font)
            .m_307310_(this.width / 2 - 155 + 80, 76 + i)
            .m_307240_();
        if (this.promptForCacheErase) {
            this.addRenderableWidget(this.eraseCache);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 50, 16777215);
        this.message.renderCentered(pGuiGraphics, this.width / 2, 70);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) {
            this.f_303694_.run();
            return true;
        } else {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface Listener {
        void proceed(boolean pConfirmed, boolean pEraseCache);
    }
}