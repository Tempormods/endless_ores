package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CrafterSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrafterScreen extends AbstractContainerScreen<CrafterMenu> {
    private static final ResourceLocation f_303203_ = new ResourceLocation("container/crafter/disabled_slot");
    private static final ResourceLocation f_303403_ = new ResourceLocation("container/crafter/powered_redstone");
    private static final ResourceLocation f_302677_ = new ResourceLocation("container/crafter/unpowered_redstone");
    private static final ResourceLocation f_302889_ = new ResourceLocation("textures/gui/container/crafter.png");
    private static final Component f_303407_ = Component.translatable("gui.togglable_slot");
    private final Player f_303763_;

    public CrafterScreen(CrafterMenu p_310211_, Inventory p_312788_, Component p_312962_) {
        super(p_310211_, p_312788_, p_312962_);
        this.f_303763_ = p_312788_.player;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void slotClicked(Slot p_310794_, int p_309597_, int p_311886_, ClickType p_312328_) {
        if (p_310794_ instanceof CrafterSlot && !p_310794_.hasItem() && !this.f_303763_.isSpectator()) {
            switch (p_312328_) {
                case PICKUP:
                    if (this.menu.m_305638_(p_309597_)) {
                        this.m_307561_(p_309597_);
                    } else if (this.menu.getCarried().isEmpty()) {
                        this.m_307046_(p_309597_);
                    }
                    break;
                case SWAP:
                    ItemStack itemstack = this.f_303763_.getInventory().getItem(p_311886_);
                    if (this.menu.m_305638_(p_309597_) && !itemstack.isEmpty()) {
                        this.m_307561_(p_309597_);
                    }
            }
        }

        super.slotClicked(p_310794_, p_309597_, p_311886_, p_312328_);
    }

    private void m_307561_(int p_309894_) {
        this.m_306960_(p_309894_, true);
    }

    private void m_307046_(int p_309649_) {
        this.m_306960_(p_309649_, false);
    }

    private void m_306960_(int p_309759_, boolean p_311308_) {
        this.menu.m_305921_(p_309759_, p_311308_);
        super.m_305068_(p_309759_, this.menu.containerId, p_311308_);
        float f = p_311308_ ? 1.0F : 0.75F;
        this.f_303763_.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, f);
    }

    @Override
    public void renderSlot(GuiGraphics p_310399_, Slot p_312178_) {
        if (p_312178_ instanceof CrafterSlot crafterslot && this.menu.m_305638_(p_312178_.index)) {
            this.m_307778_(p_310399_, crafterslot);
            return;
        }

        super.renderSlot(p_310399_, p_312178_);
    }

    private void m_307778_(GuiGraphics p_310437_, CrafterSlot p_309818_) {
        p_310437_.blitSprite(f_303203_, p_309818_.x - 1, p_309818_.y - 1, 18, 18);
    }

    @Override
    public void render(GuiGraphics p_313170_, int p_311302_, int p_309565_, float p_311210_) {
        super.render(p_313170_, p_311302_, p_309565_, p_311210_);
        this.m_306510_(p_313170_);
        this.renderTooltip(p_313170_, p_311302_, p_309565_);
        if (this.hoveredSlot instanceof CrafterSlot
            && !this.menu.m_305638_(this.hoveredSlot.index)
            && this.menu.getCarried().isEmpty()
            && !this.hoveredSlot.hasItem()
            && !this.f_303763_.isSpectator()) {
            p_313170_.renderTooltip(this.font, f_303407_, p_311302_, p_309565_);
        }
    }

    private void m_306510_(GuiGraphics p_311767_) {
        int i = this.width / 2 + 9;
        int j = this.height / 2 - 48;
        ResourceLocation resourcelocation;
        if (this.menu.m_305504_()) {
            resourcelocation = f_303403_;
        } else {
            resourcelocation = f_302677_;
        }

        p_311767_.blitSprite(resourcelocation, i, j, 16, 16);
    }

    @Override
    protected void renderBg(GuiGraphics p_309628_, float p_312032_, int p_310627_, int p_311751_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_309628_.blit(f_302889_, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}