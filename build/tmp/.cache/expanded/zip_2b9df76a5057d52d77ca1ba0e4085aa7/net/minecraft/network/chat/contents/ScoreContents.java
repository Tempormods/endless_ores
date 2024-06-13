package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

public class ScoreContents implements ComponentContents {
    public static final MapCodec<ScoreContents> f_303021_ = RecordCodecBuilder.mapCodec(
        p_310432_ -> p_310432_.group(
                    Codec.STRING.fieldOf("name").forGetter(ScoreContents::getName), Codec.STRING.fieldOf("objective").forGetter(ScoreContents::getObjective)
                )
                .apply(p_310432_, ScoreContents::new)
    );
    public static final MapCodec<ScoreContents> f_303494_ = f_303021_.fieldOf("score");
    public static final ComponentContents.Type<ScoreContents> f_303411_ = new ComponentContents.Type<>(f_303494_, "score");
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseSelector(String pSelector) {
        try {
            return new EntitySelectorParser(new StringReader(pSelector)).parse();
        } catch (CommandSyntaxException commandsyntaxexception) {
            return null;
        }
    }

    public ScoreContents(String pName, String pObjective) {
        this.name = pName;
        this.selector = parseSelector(pName);
        this.objective = pObjective;
    }

    @Override
    public ComponentContents.Type<?> m_304650_() {
        return f_303411_;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public String getObjective() {
        return this.objective;
    }

    private ScoreHolder findTargetName(CommandSourceStack pSource) throws CommandSyntaxException {
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.findEntities(pSource);
            if (!list.isEmpty()) {
                if (list.size() != 1) {
                    throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
                }

                return list.get(0);
            }
        }

        return ScoreHolder.m_306660_(this.name);
    }

    private MutableComponent getScore(ScoreHolder p_312678_, CommandSourceStack pSource) {
        MinecraftServer minecraftserver = pSource.getServer();
        if (minecraftserver != null) {
            Scoreboard scoreboard = minecraftserver.getScoreboard();
            Objective objective = scoreboard.getObjective(this.objective);
            if (objective != null) {
                ReadOnlyScoreInfo readonlyscoreinfo = scoreboard.m_305759_(p_312678_, objective);
                if (readonlyscoreinfo != null) {
                    return readonlyscoreinfo.m_307457_(objective.m_305063_(StyledFormat.f_302669_));
                }
            }
        }

        return Component.empty();
    }

    @Override
    public MutableComponent resolve(@Nullable CommandSourceStack pNbtPathPattern, @Nullable Entity pEntity, int pRecursionDepth) throws CommandSyntaxException {
        if (pNbtPathPattern == null) {
            return Component.empty();
        } else {
            ScoreHolder scoreholder = this.findTargetName(pNbtPathPattern);
            ScoreHolder scoreholder1 = (ScoreHolder)(pEntity != null && scoreholder.equals(ScoreHolder.f_303742_) ? pEntity : scoreholder);
            return this.getScore(scoreholder1, pNbtPathPattern);
        }
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            if (pOther instanceof ScoreContents scorecontents
                && this.name.equals(scorecontents.name)
                && this.objective.equals(scorecontents.objective)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = this.name.hashCode();
        return 31 * i + this.objective.hashCode();
    }

    @Override
    public String toString() {
        return "score{name='" + this.name + "', objective='" + this.objective + "'}";
    }
}