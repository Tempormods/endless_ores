package net.minecraft.world.scores;

import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;

public class Score implements ReadOnlyScoreInfo {
    private static final String f_303143_ = "Score";
    private static final String f_303167_ = "Locked";
    private static final String f_303163_ = "display";
    private static final String f_303536_ = "format";
    private int f_303612_;
    private boolean locked = true;
    @Nullable
    private Component f_303295_;
    @Nullable
    private NumberFormat f_303570_;

    @Override
    public int m_305685_() {
        return this.f_303612_;
    }

    public void m_307037_(int p_313056_) {
        this.f_303612_ = p_313056_;
    }

    @Override
    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean pLocked) {
        this.locked = pLocked;
    }

    @Nullable
    public Component m_307077_() {
        return this.f_303295_;
    }

    public void m_306495_(@Nullable Component p_312952_) {
        this.f_303295_ = p_312952_;
    }

    @Nullable
    @Override
    public NumberFormat m_305750_() {
        return this.f_303570_;
    }

    public void m_306820_(@Nullable NumberFormat p_310093_) {
        this.f_303570_ = p_310093_;
    }

    public CompoundTag m_305101_(HolderLookup.Provider p_334001_) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putInt("Score", this.f_303612_);
        compoundtag.putBoolean("Locked", this.locked);
        if (this.f_303295_ != null) {
            compoundtag.putString("display", Component.Serializer.toJson(this.f_303295_, p_334001_));
        }

        if (this.f_303570_ != null) {
            NumberFormatTypes.f_303316_
                .encodeStart(p_334001_.m_318927_(NbtOps.INSTANCE), this.f_303570_)
                .ifSuccess(p_309357_ -> compoundtag.put("format", p_309357_));
        }

        return compoundtag;
    }

    public static Score m_306631_(CompoundTag p_313199_, HolderLookup.Provider p_329343_) {
        Score score = new Score();
        score.f_303612_ = p_313199_.getInt("Score");
        score.locked = p_313199_.getBoolean("Locked");
        if (p_313199_.contains("display", 8)) {
            score.f_303295_ = Component.Serializer.fromJson(p_313199_.getString("display"), p_329343_);
        }

        if (p_313199_.contains("format", 10)) {
            NumberFormatTypes.f_303316_
                .parse(p_329343_.m_318927_(NbtOps.INSTANCE), p_313199_.get("format"))
                .ifSuccess(p_309359_ -> score.f_303570_ = p_309359_);
        }

        return score;
    }
}