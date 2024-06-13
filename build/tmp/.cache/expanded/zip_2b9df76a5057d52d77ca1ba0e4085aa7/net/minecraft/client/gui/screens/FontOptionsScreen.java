package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontOptionsScreen extends SimpleOptionsSubScreen {
    private static OptionInstance<?>[] m_324360_(Options p_331766_) {
        return new OptionInstance[]{p_331766_.forceUnicodeFont(), p_331766_.m_321442_()};
    }

    public FontOptionsScreen(Screen p_332705_, Options p_330216_) {
        super(p_332705_, p_330216_, Component.translatable("options.font.title"), m_324360_(p_330216_));
    }
}