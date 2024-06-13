package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
    private static final int f_314638_ = 310;
    private static final int f_314690_ = 25;
    private final OptionsSubScreen f_316801_;

    public OptionsList(Minecraft pMinecraft, int pWidth, int pHeight, OptionsSubScreen p_333471_) {
        super(pMinecraft, pWidth, p_333471_.f_314621_.m_319781_(), p_333471_.f_314621_.getHeaderHeight(), 25);
        this.centerListVertically = false;
        this.f_316801_ = p_333471_;
    }

    public void addBig(OptionInstance<?> pOption) {
        this.addEntry(OptionsList.OptionEntry.m_324394_(this.minecraft.options, pOption, this.f_316801_));
    }

    public void addSmall(OptionInstance<?>... pOptions) {
        for (int i = 0; i < pOptions.length; i += 2) {
            OptionInstance<?> optioninstance = i < pOptions.length - 1 ? pOptions[i + 1] : null;
            this.addEntry(OptionsList.OptionEntry.m_319849_(this.minecraft.options, pOptions[i], optioninstance, this.f_316801_));
        }
    }

    public void m_324569_(List<AbstractWidget> p_334237_) {
        for (int i = 0; i < p_334237_.size(); i += 2) {
            this.addSmall(p_334237_.get(i), i < p_334237_.size() - 1 ? p_334237_.get(i + 1) : null);
        }
    }

    public void addSmall(AbstractWidget p_330860_, @Nullable AbstractWidget p_333864_) {
        this.addEntry(OptionsList.Entry.small(p_330860_, p_333864_, this.f_316801_));
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Nullable
    public AbstractWidget findOption(OptionInstance<?> pOption) {
        for (OptionsList.Entry optionslist$entry : this.children()) {
            if (optionslist$entry instanceof OptionsList.OptionEntry optionslist$optionentry) {
                AbstractWidget abstractwidget = optionslist$optionentry.f_315275_.get(pOption);
                if (abstractwidget != null) {
                    return abstractwidget;
                }
            }
        }

        return null;
    }

    public void m_323432_() {
        for (OptionsList.Entry optionslist$entry : this.children()) {
            if (optionslist$entry instanceof OptionsList.OptionEntry) {
                OptionsList.OptionEntry optionslist$optionentry = (OptionsList.OptionEntry)optionslist$entry;

                for (AbstractWidget abstractwidget : optionslist$optionentry.f_315275_.values()) {
                    if (abstractwidget instanceof OptionInstance.OptionInstanceSliderButton<?> optioninstancesliderbutton) {
                        optioninstancesliderbutton.m_323527_();
                    }
                }
            }
        }
    }

    public Optional<GuiEventListener> getMouseOver(double pMouseX, double pMouseY) {
        for (OptionsList.Entry optionslist$entry : this.children()) {
            for (GuiEventListener guieventlistener : optionslist$entry.children()) {
                if (guieventlistener.isMouseOver(pMouseX, pMouseY)) {
                    return Optional.of(guieventlistener);
                }
            }
        }

        return Optional.empty();
    }

    @OnlyIn(Dist.CLIENT)
    protected static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
        private final List<AbstractWidget> children;
        private final Screen f_316121_;
        private static final int f_314006_ = 160;

        Entry(List<AbstractWidget> p_328739_, Screen p_332963_) {
            this.children = ImmutableList.copyOf(p_328739_);
            this.f_316121_ = p_332963_;
        }

        public static OptionsList.Entry big(List<AbstractWidget> p_331607_, Screen p_332678_) {
            return new OptionsList.Entry(p_331607_, p_332678_);
        }

        public static OptionsList.Entry small(AbstractWidget p_332778_, @Nullable AbstractWidget p_330638_, Screen p_328012_) {
            return p_330638_ == null
                ? new OptionsList.Entry(ImmutableList.of(p_332778_), p_328012_)
                : new OptionsList.Entry(ImmutableList.of(p_332778_, p_330638_), p_328012_);
        }

        @Override
        public void render(
            GuiGraphics pGuiGraphics,
            int pIndex,
            int pTop,
            int pLeft,
            int pWidth,
            int pHeight,
            int pMouseX,
            int pMouseY,
            boolean pHovering,
            float pPartialTick
        ) {
            int i = 0;
            int j = this.f_316121_.width / 2 - 155;

            for (AbstractWidget abstractwidget : this.children) {
                abstractwidget.setPosition(j + i, pTop);
                abstractwidget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                i += 160;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class OptionEntry extends OptionsList.Entry {
        final Map<OptionInstance<?>, AbstractWidget> f_315275_;

        private OptionEntry(Map<OptionInstance<?>, AbstractWidget> p_331348_, OptionsSubScreen p_335221_) {
            super(ImmutableList.copyOf(p_331348_.values()), p_335221_);
            this.f_315275_ = p_331348_;
        }

        public static OptionsList.OptionEntry m_324394_(Options p_335438_, OptionInstance<?> p_329713_, OptionsSubScreen p_334802_) {
            return new OptionsList.OptionEntry(ImmutableMap.of(p_329713_, p_329713_.createButton(p_335438_, 0, 0, 310)), p_334802_);
        }

        public static OptionsList.OptionEntry m_319849_(
            Options p_330617_, OptionInstance<?> p_330233_, @Nullable OptionInstance<?> p_331704_, OptionsSubScreen p_334257_
        ) {
            AbstractWidget abstractwidget = p_330233_.m_324463_(p_330617_);
            return p_331704_ == null
                ? new OptionsList.OptionEntry(ImmutableMap.of(p_330233_, abstractwidget), p_334257_)
                : new OptionsList.OptionEntry(ImmutableMap.of(p_330233_, abstractwidget, p_331704_, p_331704_.m_324463_(p_330617_)), p_334257_);
        }
    }
}