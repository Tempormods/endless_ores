package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyBindsScreen extends OptionsSubScreen {
    private static final Component f_314160_ = Component.translatable("controls.keybinds.title");
    @Nullable
    public KeyMapping selectedKey;
    public long lastKeySelection;
    private KeyBindsList keyBindsList;
    private Button resetButton;

    public KeyBindsScreen(Screen pLastScreen, Options pOptions) {
        super(pLastScreen, pOptions, f_314160_);
    }

    @Override
    protected void init() {
        this.keyBindsList = this.addRenderableWidget(new KeyBindsList(this, this.minecraft));
        this.resetButton = Button.builder(Component.translatable("controls.resetAll"), p_269619_ -> {
            for (KeyMapping keymapping : this.options.keyMappings) {
                keymapping.setToDefault();
            }

            this.keyBindsList.resetMappingAndUpdateButtons();
        }).build();
        super.init();
    }

    @Override
    protected void m_319570_() {
        LinearLayout linearlayout = this.f_314621_.addToFooter(LinearLayout.horizontal().spacing(8));
        linearlayout.addChild(this.resetButton);
        linearlayout.addChild(Button.builder(CommonComponents.GUI_DONE, p_325377_ -> this.onClose()).build());
    }

    @Override
    protected void repositionElements() {
        this.f_314621_.arrangeElements();
        this.keyBindsList.m_319425_(this.width, this.f_314621_);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.selectedKey != null) {
            this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(pButton));
            this.selectedKey = null;
            this.keyBindsList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.selectedKey != null) {
            if (pKeyCode == 256) {
                this.selectedKey.setKeyModifierAndCode(null, InputConstants.UNKNOWN);
                this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
            } else {
                this.selectedKey.setKeyModifierAndCode(null, InputConstants.getKey(pKeyCode, pScanCode));
                this.options.setKey(this.selectedKey, InputConstants.getKey(pKeyCode, pScanCode));
            }

            if (pKeyCode == 256 || !net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.selectedKey.getKey()))
            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            this.keyBindsList.resetMappingAndUpdateButtons();
            return true;
        } else {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
       // Forge: We wait for a second key above if the first press is a modifier
       // but if they release the modifier then set it explicitly.
       var key = InputConstants.getKey(keyCode, scanCode);
       if (this.selectedKey != null && this.selectedKey.getKey() == key) {
          this.selectedKey = null;
          this.lastKeySelection = Util.getMillis();
          this.keyBindsList.resetMappingAndUpdateButtons();
       }
       return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        boolean flag = false;

        for (KeyMapping keymapping : this.options.keyMappings) {
            if (!keymapping.isDefault()) {
                flag = true;
                break;
            }
        }

        this.resetButton.active = flag;
    }
}
