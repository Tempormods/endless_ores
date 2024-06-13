package net.minecraft.client.gui.screens.controls;

import javax.annotation.Nullable;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends OptionsSubScreen {
    private static final Component f_315727_ = Component.translatable("controls.title");
    @Nullable
    private OptionsList f_313952_;

    private static OptionInstance<?>[] m_324390_(Options p_330213_) {
        return new OptionInstance[]{p_330213_.toggleCrouch(), p_330213_.toggleSprint(), p_330213_.autoJump(), p_330213_.operatorItemsTab()};
    }

    public ControlsScreen(Screen pLastScreen, Options pOptions) {
        super(pLastScreen, pOptions, f_315727_);
    }

    @Override
    protected void init() {
        this.f_313952_ = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height, this));
        this.f_313952_
            .addSmall(
                Button.builder(
                        Component.translatable("options.mouse_settings"), p_280846_ -> this.minecraft.setScreen(new MouseSettingsScreen(this, this.options))
                    )
                    .build(),
                Button.builder(Component.translatable("controls.keybinds"), p_280844_ -> this.minecraft.setScreen(new KeyBindsScreen(this, this.options)))
                    .build()
            );
        this.f_313952_.addSmall(m_324390_(this.options));
        super.init();
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (this.f_313952_ != null) {
            this.f_313952_.m_319425_(this.width, this.f_314621_);
        }
    }
}