package net.minecraft.world.scores;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.slf4j.Logger;

public class ScoreboardSaveData extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String FILE_ID = "scoreboard";
    private final Scoreboard scoreboard;

    public ScoreboardSaveData(Scoreboard pScoreboard) {
        this.scoreboard = pScoreboard;
    }

    public ScoreboardSaveData load(CompoundTag pTag, HolderLookup.Provider p_330463_) {
        this.loadObjectives(pTag.getList("Objectives", 10), p_330463_);
        this.scoreboard.loadPlayerScores(pTag.getList("PlayerScores", 10), p_330463_);
        if (pTag.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots(pTag.getCompound("DisplaySlots"));
        }

        if (pTag.contains("Teams", 9)) {
            this.loadTeams(pTag.getList("Teams", 10), p_330463_);
        }

        return this;
    }

    private void loadTeams(ListTag pTagList, HolderLookup.Provider p_329597_) {
        for (int i = 0; i < pTagList.size(); i++) {
            CompoundTag compoundtag = pTagList.getCompound(i);
            String s = compoundtag.getString("Name");
            PlayerTeam playerteam = this.scoreboard.addPlayerTeam(s);
            Component component = Component.Serializer.fromJson(compoundtag.getString("DisplayName"), p_329597_);
            if (component != null) {
                playerteam.setDisplayName(component);
            }

            if (compoundtag.contains("TeamColor", 8)) {
                playerteam.setColor(ChatFormatting.getByName(compoundtag.getString("TeamColor")));
            }

            if (compoundtag.contains("AllowFriendlyFire", 99)) {
                playerteam.setAllowFriendlyFire(compoundtag.getBoolean("AllowFriendlyFire"));
            }

            if (compoundtag.contains("SeeFriendlyInvisibles", 99)) {
                playerteam.setSeeFriendlyInvisibles(compoundtag.getBoolean("SeeFriendlyInvisibles"));
            }

            if (compoundtag.contains("MemberNamePrefix", 8)) {
                Component component1 = Component.Serializer.fromJson(compoundtag.getString("MemberNamePrefix"), p_329597_);
                if (component1 != null) {
                    playerteam.setPlayerPrefix(component1);
                }
            }

            if (compoundtag.contains("MemberNameSuffix", 8)) {
                Component component2 = Component.Serializer.fromJson(compoundtag.getString("MemberNameSuffix"), p_329597_);
                if (component2 != null) {
                    playerteam.setPlayerSuffix(component2);
                }
            }

            if (compoundtag.contains("NameTagVisibility", 8)) {
                Team.Visibility team$visibility = Team.Visibility.byName(compoundtag.getString("NameTagVisibility"));
                if (team$visibility != null) {
                    playerteam.setNameTagVisibility(team$visibility);
                }
            }

            if (compoundtag.contains("DeathMessageVisibility", 8)) {
                Team.Visibility team$visibility1 = Team.Visibility.byName(compoundtag.getString("DeathMessageVisibility"));
                if (team$visibility1 != null) {
                    playerteam.setDeathMessageVisibility(team$visibility1);
                }
            }

            if (compoundtag.contains("CollisionRule", 8)) {
                Team.CollisionRule team$collisionrule = Team.CollisionRule.byName(compoundtag.getString("CollisionRule"));
                if (team$collisionrule != null) {
                    playerteam.setCollisionRule(team$collisionrule);
                }
            }

            this.loadTeamPlayers(playerteam, compoundtag.getList("Players", 8));
        }
    }

    private void loadTeamPlayers(PlayerTeam pPlayerTeam, ListTag pTagList) {
        for (int i = 0; i < pTagList.size(); i++) {
            this.scoreboard.addPlayerToTeam(pTagList.getString(i), pPlayerTeam);
        }
    }

    private void loadDisplaySlots(CompoundTag pCompound) {
        for (String s : pCompound.getAllKeys()) {
            DisplaySlot displayslot = DisplaySlot.CODEC.byName(s);
            if (displayslot != null) {
                String s1 = pCompound.getString(s);
                Objective objective = this.scoreboard.getObjective(s1);
                this.scoreboard.setDisplayObjective(displayslot, objective);
            }
        }
    }

    private void loadObjectives(ListTag pTag, HolderLookup.Provider p_329349_) {
        for (int i = 0; i < pTag.size(); i++) {
            CompoundTag compoundtag = pTag.getCompound(i);
            String s = compoundtag.getString("CriteriaName");
            ObjectiveCriteria objectivecriteria = ObjectiveCriteria.byName(s).orElseGet(() -> {
                LOGGER.warn("Unknown scoreboard criteria {}, replacing with {}", s, ObjectiveCriteria.DUMMY.getName());
                return ObjectiveCriteria.DUMMY;
            });
            String s1 = compoundtag.getString("Name");
            Component component = Component.Serializer.fromJson(compoundtag.getString("DisplayName"), p_329349_);
            ObjectiveCriteria.RenderType objectivecriteria$rendertype = ObjectiveCriteria.RenderType.byId(compoundtag.getString("RenderType"));
            boolean flag = compoundtag.getBoolean("display_auto_update");
            NumberFormat numberformat = NumberFormatTypes.f_303316_
                .parse(p_329349_.m_318927_(NbtOps.INSTANCE), compoundtag.get("format"))
                .result()
                .orElse(null);
            this.scoreboard.addObjective(s1, objectivecriteria, component, objectivecriteria$rendertype, flag, numberformat);
        }
    }

    @Override
    public CompoundTag save(CompoundTag pCompound, HolderLookup.Provider p_331672_) {
        pCompound.put("Objectives", this.saveObjectives(p_331672_));
        pCompound.put("PlayerScores", this.scoreboard.savePlayerScores(p_331672_));
        pCompound.put("Teams", this.saveTeams(p_331672_));
        this.saveDisplaySlots(pCompound);
        return pCompound;
    }

    private ListTag saveTeams(HolderLookup.Provider p_330101_) {
        ListTag listtag = new ListTag();

        for (PlayerTeam playerteam : this.scoreboard.getPlayerTeams()) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("Name", playerteam.getName());
            compoundtag.putString("DisplayName", Component.Serializer.toJson(playerteam.getDisplayName(), p_330101_));
            if (playerteam.getColor().getId() >= 0) {
                compoundtag.putString("TeamColor", playerteam.getColor().getName());
            }

            compoundtag.putBoolean("AllowFriendlyFire", playerteam.isAllowFriendlyFire());
            compoundtag.putBoolean("SeeFriendlyInvisibles", playerteam.canSeeFriendlyInvisibles());
            compoundtag.putString("MemberNamePrefix", Component.Serializer.toJson(playerteam.getPlayerPrefix(), p_330101_));
            compoundtag.putString("MemberNameSuffix", Component.Serializer.toJson(playerteam.getPlayerSuffix(), p_330101_));
            compoundtag.putString("NameTagVisibility", playerteam.getNameTagVisibility().name);
            compoundtag.putString("DeathMessageVisibility", playerteam.getDeathMessageVisibility().name);
            compoundtag.putString("CollisionRule", playerteam.getCollisionRule().name);
            ListTag listtag1 = new ListTag();

            for (String s : playerteam.getPlayers()) {
                listtag1.add(StringTag.valueOf(s));
            }

            compoundtag.put("Players", listtag1);
            listtag.add(compoundtag);
        }

        return listtag;
    }

    private void saveDisplaySlots(CompoundTag pCompound) {
        CompoundTag compoundtag = new CompoundTag();

        for (DisplaySlot displayslot : DisplaySlot.values()) {
            Objective objective = this.scoreboard.getDisplayObjective(displayslot);
            if (objective != null) {
                compoundtag.putString(displayslot.getSerializedName(), objective.getName());
            }
        }

        if (!compoundtag.isEmpty()) {
            pCompound.put("DisplaySlots", compoundtag);
        }
    }

    private ListTag saveObjectives(HolderLookup.Provider p_335863_) {
        ListTag listtag = new ListTag();

        for (Objective objective : this.scoreboard.getObjectives()) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("Name", objective.getName());
            compoundtag.putString("CriteriaName", objective.getCriteria().getName());
            compoundtag.putString("DisplayName", Component.Serializer.toJson(objective.getDisplayName(), p_335863_));
            compoundtag.putString("RenderType", objective.getRenderType().getId());
            compoundtag.putBoolean("display_auto_update", objective.m_305930_());
            NumberFormat numberformat = objective.m_306659_();
            if (numberformat != null) {
                NumberFormatTypes.f_303316_
                    .encodeStart(p_335863_.m_318927_(NbtOps.INSTANCE), numberformat)
                    .ifSuccess(p_309378_ -> compoundtag.put("format", p_309378_));
            }

            listtag.add(compoundtag);
        }

        return listtag;
    }
}