package net.minecraft.world.scores;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public interface ScoreHolder {
    String f_303112_ = "*";
    ScoreHolder f_303742_ = new ScoreHolder() {
        @Override
        public String getScoreboardName() {
            return "*";
        }
    };

    String getScoreboardName();

    @Nullable
    default Component getDisplayName() {
        return null;
    }

    default Component m_305099_() {
        Component component = this.getDisplayName();
        return component != null
            ? component.copy().withStyle(p_312428_ -> p_312428_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.getScoreboardName()))))
            : Component.literal(this.getScoreboardName());
    }

    static ScoreHolder m_306660_(final String p_312707_) {
        if (p_312707_.equals("*")) {
            return f_303742_;
        } else {
            final Component component = Component.literal(p_312707_);
            return new ScoreHolder() {
                @Override
                public String getScoreboardName() {
                    return p_312707_;
                }

                @Override
                public Component m_305099_() {
                    return component;
                }
            };
        }
    }

    static ScoreHolder m_305011_(GameProfile p_311927_) {
        final String s = p_311927_.getName();
        return new ScoreHolder() {
            /**
             * Returns a String to use as this entity's name in the scoreboard/entity selector systems
             */
            @Override
            public String getScoreboardName() {
                return s;
            }
        };
    }
}