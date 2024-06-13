package net.minecraft.world.scores;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Objective {
    private final Scoreboard scoreboard;
    private final String name;
    private final ObjectiveCriteria criteria;
    private Component displayName;
    private Component formattedDisplayName;
    private ObjectiveCriteria.RenderType renderType;
    private boolean f_302739_;
    @Nullable
    private NumberFormat f_302905_;

    public Objective(
        Scoreboard pScoreboard,
        String pName,
        ObjectiveCriteria pCriteria,
        Component pDisplayName,
        ObjectiveCriteria.RenderType pRenderType,
        boolean p_311052_,
        @Nullable NumberFormat p_309864_
    ) {
        this.scoreboard = pScoreboard;
        this.name = pName;
        this.criteria = pCriteria;
        this.displayName = pDisplayName;
        this.formattedDisplayName = this.createFormattedDisplayName();
        this.renderType = pRenderType;
        this.f_302739_ = p_311052_;
        this.f_302905_ = p_309864_;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public ObjectiveCriteria getCriteria() {
        return this.criteria;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public boolean m_305930_() {
        return this.f_302739_;
    }

    @Nullable
    public NumberFormat m_306659_() {
        return this.f_302905_;
    }

    public NumberFormat m_305063_(NumberFormat p_309891_) {
        return Objects.requireNonNullElse(this.f_302905_, p_309891_);
    }

    private Component createFormattedDisplayName() {
        return ComponentUtils.wrapInSquareBrackets(
            this.displayName.copy().withStyle(p_83319_ -> p_83319_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.name))))
        );
    }

    public Component getFormattedDisplayName() {
        return this.formattedDisplayName;
    }

    public void setDisplayName(Component pDisplayName) {
        this.displayName = pDisplayName;
        this.formattedDisplayName = this.createFormattedDisplayName();
        this.scoreboard.onObjectiveChanged(this);
    }

    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }

    public void setRenderType(ObjectiveCriteria.RenderType pRenderType) {
        this.renderType = pRenderType;
        this.scoreboard.onObjectiveChanged(this);
    }

    public void m_307898_(boolean p_309636_) {
        this.f_302739_ = p_309636_;
        this.scoreboard.onObjectiveChanged(this);
    }

    public void m_305060_(@Nullable NumberFormat p_311380_) {
        this.f_302905_ = p_311380_;
        this.scoreboard.onObjectiveChanged(this);
    }
}