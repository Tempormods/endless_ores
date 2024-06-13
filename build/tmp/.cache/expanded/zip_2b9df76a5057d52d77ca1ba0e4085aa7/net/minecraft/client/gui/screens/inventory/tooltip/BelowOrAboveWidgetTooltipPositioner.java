package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@OnlyIn(Dist.CLIENT)
public class BelowOrAboveWidgetTooltipPositioner implements ClientTooltipPositioner {
    private final ScreenRectangle f_302734_;

    public BelowOrAboveWidgetTooltipPositioner(ScreenRectangle p_312932_) {
        this.f_302734_ = p_312932_;
    }

    @Override
    public Vector2ic positionTooltip(int pScreenWidth, int pScreenHeight, int pMouseX, int pMouseY, int pTooltipWidth, int pTooltipHeight) {
        Vector2i vector2i = new Vector2i();
        vector2i.x = this.f_302734_.left() + 3;
        vector2i.y = this.f_302734_.bottom() + 3 + 1;
        if (vector2i.y + pTooltipHeight + 3 > pScreenHeight) {
            vector2i.y = this.f_302734_.top() - pTooltipHeight - 3 - 1;
        }

        if (vector2i.x + pTooltipWidth > pScreenWidth) {
            vector2i.x = Math.max(this.f_302734_.right() - pTooltipWidth - 3, 4);
        }

        return vector2i;
    }
}