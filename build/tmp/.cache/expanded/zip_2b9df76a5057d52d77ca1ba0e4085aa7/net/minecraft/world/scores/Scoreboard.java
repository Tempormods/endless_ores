package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class Scoreboard {
    public static final String f_303532_ = "#";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Object2ObjectMap<String, Objective> objectivesByName = new Object2ObjectOpenHashMap<>(16, 0.5F);
    private final Reference2ObjectMap<ObjectiveCriteria, List<Objective>> objectivesByCriteria = new Reference2ObjectOpenHashMap<>();
    private final Map<String, PlayerScores> playerScores = new Object2ObjectOpenHashMap<>(16, 0.5F);
    private final Map<DisplaySlot, Objective> displayObjectives = new EnumMap<>(DisplaySlot.class);
    private final Object2ObjectMap<String, PlayerTeam> teamsByName = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<String, PlayerTeam> teamsByPlayer = new Object2ObjectOpenHashMap<>();

    @Nullable
    public Objective getObjective(@Nullable String pName) {
        return this.objectivesByName.get(pName);
    }

    public Objective addObjective(
        String pName,
        ObjectiveCriteria pCriteria,
        Component pDisplayName,
        ObjectiveCriteria.RenderType pRenderType,
        boolean p_311367_,
        @Nullable NumberFormat p_311959_
    ) {
        if (this.objectivesByName.containsKey(pName)) {
            throw new IllegalArgumentException("An objective with the name '" + pName + "' already exists!");
        } else {
            Objective objective = new Objective(this, pName, pCriteria, pDisplayName, pRenderType, p_311367_, p_311959_);
            this.objectivesByCriteria.computeIfAbsent(pCriteria, p_310953_ -> Lists.newArrayList()).add(objective);
            this.objectivesByName.put(pName, objective);
            this.onObjectiveAdded(objective);
            return objective;
        }
    }

    public final void forAllObjectives(ObjectiveCriteria pCriteria, ScoreHolder p_310719_, Consumer<ScoreAccess> pAction) {
        this.objectivesByCriteria.getOrDefault(pCriteria, Collections.emptyList()).forEach(p_309370_ -> pAction.accept(this.m_307672_(p_310719_, p_309370_, true)));
    }

    private PlayerScores m_307682_(String p_311117_) {
        return this.playerScores.computeIfAbsent(p_311117_, p_309376_ -> new PlayerScores());
    }

    public ScoreAccess getOrCreatePlayerScore(ScoreHolder p_309688_, Objective pObjective) {
        return this.m_307672_(p_309688_, pObjective, false);
    }

    public ScoreAccess m_307672_(final ScoreHolder p_310827_, final Objective p_312875_, boolean p_310024_) {
        final boolean flag = p_310024_ || !p_312875_.getCriteria().isReadOnly();
        PlayerScores playerscores = this.m_307682_(p_310827_.getScoreboardName());
        final MutableBoolean mutableboolean = new MutableBoolean();
        final Score score = playerscores.m_306863_(p_312875_, p_309375_ -> mutableboolean.setTrue());
        return new ScoreAccess() {
            @Override
            public int m_306505_() {
                return score.m_305685_();
            }

            @Override
            public void m_305183_(int p_312858_) {
                if (!flag) {
                    throw new IllegalStateException("Cannot modify read-only score");
                } else {
                    boolean flag1 = mutableboolean.isTrue();
                    if (p_312875_.m_305930_()) {
                        Component component = p_310827_.getDisplayName();
                        if (component != null && !component.equals(score.m_307077_())) {
                            score.m_306495_(component);
                            flag1 = true;
                        }
                    }

                    if (p_312858_ != score.m_305685_()) {
                        score.m_307037_(p_312858_);
                        flag1 = true;
                    }

                    if (flag1) {
                        this.m_304734_();
                    }
                }
            }

            @Nullable
            @Override
            public Component m_305613_() {
                return score.m_307077_();
            }

            @Override
            public void m_306789_(@Nullable Component p_309551_) {
                if (mutableboolean.isTrue() || !Objects.equals(p_309551_, score.m_307077_())) {
                    score.m_306495_(p_309551_);
                    this.m_304734_();
                }
            }

            @Override
            public void m_304839_(@Nullable NumberFormat p_312257_) {
                score.m_306820_(p_312257_);
                this.m_304734_();
            }

            @Override
            public boolean m_304717_() {
                return score.isLocked();
            }

            @Override
            public void m_305539_() {
                this.m_305235_(false);
            }

            @Override
            public void m_305263_() {
                this.m_305235_(true);
            }

            private void m_305235_(boolean p_311228_) {
                score.setLocked(p_311228_);
                if (mutableboolean.isTrue()) {
                    this.m_304734_();
                }

                Scoreboard.this.m_304975_(p_310827_, p_312875_);
            }

            private void m_304734_() {
                Scoreboard.this.onScoreChanged(p_310827_, p_312875_, score);
                mutableboolean.setFalse();
            }
        };
    }

    @Nullable
    public ReadOnlyScoreInfo m_305759_(ScoreHolder p_309394_, Objective p_310266_) {
        PlayerScores playerscores = this.playerScores.get(p_309394_.getScoreboardName());
        return playerscores != null ? playerscores.m_307163_(p_310266_) : null;
    }

    public Collection<PlayerScoreEntry> m_306706_(Objective p_312530_) {
        List<PlayerScoreEntry> list = new ArrayList<>();
        this.playerScores.forEach((p_309362_, p_309363_) -> {
            Score score = p_309363_.m_307163_(p_312530_);
            if (score != null) {
                list.add(new PlayerScoreEntry(p_309362_, score.m_305685_(), score.m_307077_(), score.m_305750_()));
            }
        });
        return list;
    }

    public Collection<Objective> getObjectives() {
        return this.objectivesByName.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.objectivesByName.keySet();
    }

    public Collection<ScoreHolder> getTrackedPlayers() {
        return this.playerScores.keySet().stream().map(ScoreHolder::m_306660_).toList();
    }

    public void m_307153_(ScoreHolder p_311535_) {
        PlayerScores playerscores = this.playerScores.remove(p_311535_.getScoreboardName());
        if (playerscores != null) {
            this.onPlayerRemoved(p_311535_);
        }
    }

    public void m_305788_(ScoreHolder p_312886_, Objective p_311508_) {
        PlayerScores playerscores = this.playerScores.get(p_312886_.getScoreboardName());
        if (playerscores != null) {
            boolean flag = playerscores.m_305067_(p_311508_);
            if (!playerscores.m_307156_()) {
                PlayerScores playerscores1 = this.playerScores.remove(p_312886_.getScoreboardName());
                if (playerscores1 != null) {
                    this.onPlayerRemoved(p_312886_);
                }
            } else if (flag) {
                this.onPlayerScoreRemoved(p_312886_, p_311508_);
            }
        }
    }

    public Object2IntMap<Objective> m_307827_(ScoreHolder p_312742_) {
        PlayerScores playerscores = this.playerScores.get(p_312742_.getScoreboardName());
        return playerscores != null ? playerscores.m_306675_() : Object2IntMaps.emptyMap();
    }

    public void removeObjective(Objective pObjective) {
        this.objectivesByName.remove(pObjective.getName());

        for (DisplaySlot displayslot : DisplaySlot.values()) {
            if (this.getDisplayObjective(displayslot) == pObjective) {
                this.setDisplayObjective(displayslot, null);
            }
        }

        List<Objective> list = this.objectivesByCriteria.get(pObjective.getCriteria());
        if (list != null) {
            list.remove(pObjective);
        }

        for (PlayerScores playerscores : this.playerScores.values()) {
            playerscores.m_305067_(pObjective);
        }

        this.onObjectiveRemoved(pObjective);
    }

    public void setDisplayObjective(DisplaySlot pSlot, @Nullable Objective pObjective) {
        this.displayObjectives.put(pSlot, pObjective);
    }

    @Nullable
    public Objective getDisplayObjective(DisplaySlot pSlot) {
        return this.displayObjectives.get(pSlot);
    }

    @Nullable
    public PlayerTeam getPlayerTeam(String pTeamName) {
        return this.teamsByName.get(pTeamName);
    }

    public PlayerTeam addPlayerTeam(String pName) {
        PlayerTeam playerteam = this.getPlayerTeam(pName);
        if (playerteam != null) {
            LOGGER.warn("Requested creation of existing team '{}'", pName);
            return playerteam;
        } else {
            playerteam = new PlayerTeam(this, pName);
            this.teamsByName.put(pName, playerteam);
            this.onTeamAdded(playerteam);
            return playerteam;
        }
    }

    public void removePlayerTeam(PlayerTeam pPlayerTeam) {
        this.teamsByName.remove(pPlayerTeam.getName());

        for (String s : pPlayerTeam.getPlayers()) {
            this.teamsByPlayer.remove(s);
        }

        this.onTeamRemoved(pPlayerTeam);
    }

    public boolean addPlayerToTeam(String pPlayerName, PlayerTeam pTeam) {
        if (this.getPlayersTeam(pPlayerName) != null) {
            this.removePlayerFromTeam(pPlayerName);
        }

        this.teamsByPlayer.put(pPlayerName, pTeam);
        return pTeam.getPlayers().add(pPlayerName);
    }

    public boolean removePlayerFromTeam(String pPlayerName) {
        PlayerTeam playerteam = this.getPlayersTeam(pPlayerName);
        if (playerteam != null) {
            this.removePlayerFromTeam(pPlayerName, playerteam);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String pUsername, PlayerTeam pPlayerTeam) {
        if (this.getPlayersTeam(pUsername) != pPlayerTeam) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + pPlayerTeam.getName() + "'.");
        } else {
            this.teamsByPlayer.remove(pUsername);
            pPlayerTeam.getPlayers().remove(pUsername);
        }
    }

    public Collection<String> getTeamNames() {
        return this.teamsByName.keySet();
    }

    public Collection<PlayerTeam> getPlayerTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public PlayerTeam getPlayersTeam(String pUsername) {
        return this.teamsByPlayer.get(pUsername);
    }

    public void onObjectiveAdded(Objective pObjective) {
    }

    public void onObjectiveChanged(Objective pObjective) {
    }

    public void onObjectiveRemoved(Objective pObjective) {
    }

    protected void onScoreChanged(ScoreHolder p_312923_, Objective p_311972_, Score pScore) {
    }

    protected void m_304975_(ScoreHolder p_311114_, Objective p_309936_) {
    }

    public void onPlayerRemoved(ScoreHolder p_312272_) {
    }

    public void onPlayerScoreRemoved(ScoreHolder p_311030_, Objective pObjective) {
    }

    public void onTeamAdded(PlayerTeam pPlayerTeam) {
    }

    public void onTeamChanged(PlayerTeam pPlayerTeam) {
    }

    public void onTeamRemoved(PlayerTeam pPlayerTeam) {
    }

    public void entityRemoved(Entity pEntity) {
        if (!(pEntity instanceof Player) && !pEntity.isAlive()) {
            this.m_307153_(pEntity);
            this.removePlayerFromTeam(pEntity.getScoreboardName());
        }
    }

    protected ListTag savePlayerScores(HolderLookup.Provider p_333851_) {
        ListTag listtag = new ListTag();
        this.playerScores.forEach((p_327666_, p_327667_) -> p_327667_.m_307678_().forEach((p_327662_, p_327663_) -> {
                CompoundTag compoundtag = p_327663_.m_305101_(p_333851_);
                compoundtag.putString("Name", p_327666_);
                compoundtag.putString("Objective", p_327662_.getName());
                listtag.add(compoundtag);
            }));
        return listtag;
    }

    protected void loadPlayerScores(ListTag pTag, HolderLookup.Provider p_332084_) {
        for (int i = 0; i < pTag.size(); i++) {
            CompoundTag compoundtag = pTag.getCompound(i);
            Score score = Score.m_306631_(compoundtag, p_332084_);
            String s = compoundtag.getString("Name");
            String s1 = compoundtag.getString("Objective");
            Objective objective = this.getObjective(s1);
            if (objective == null) {
                LOGGER.error("Unknown objective {} for name {}, ignoring", s1, s);
            } else {
                this.m_307682_(s).m_305165_(objective, score);
            }
        }
    }
}