package net.minecraft.client.gui.screens;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinCustomizationScreen extends OptionsSubScreen {
    private static final Component f_315091_ = Component.translatable("options.skinCustomisation.title");
    @Nullable
    private OptionsList f_316272_;

    public SkinCustomizationScreen(Screen pLastScreen, Options pOptions) {
        super(pLastScreen, pOptions, f_315091_);
    }

    @Override
    protected void init() {
        this.f_316272_ = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height, this));
        List<AbstractWidget> list = new ArrayList<>();

        for (PlayerModelPart playermodelpart : PlayerModelPart.values()) {
            list.add(
                CycleButton.onOffBuilder(this.options.isModelPartEnabled(playermodelpart))
                    .m_323445_(playermodelpart.getName(), (p_169436_, p_169437_) -> this.options.toggleModelPart(playermodelpart, p_169437_))
            );
        }

        list.add(this.options.mainHand().m_324463_(this.options));
        this.f_316272_.m_324569_(list);
        super.init();
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (this.f_316272_ != null) {
            this.f_316272_.m_319425_(this.width, this.f_314621_);
        }
    }
}