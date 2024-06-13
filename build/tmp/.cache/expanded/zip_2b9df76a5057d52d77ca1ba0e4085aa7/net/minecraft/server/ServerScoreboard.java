package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardSaveData;

public class ServerScoreboard extends Scoreboard {
    private final MinecraftServer server;
    private final Set<Objective> trackedObjectives = Sets.newHashSet();
    private final List<Runnable> dirtyListeners = Lists.newArrayList();

    public ServerScoreboard(MinecraftServer pServer) {
        this.server = pServer;
    }

    @Override
    protected void onScoreChanged(ScoreHolder p_311591_, Objective p_310366_, Score pScore) {
        super.onScoreChanged(p_311591_, p_310366_, pScore);
        if (this.trackedObjectives.contains(p_310366_)) {
            this.server
                .getPlayerList()
                .broadcastAll(
                    new ClientboundSetScorePacket(
                        p_311591_.getScoreboardName(),
                        p_310366_.getName(),
                        pScore.m_305685_(),
                        Optional.ofNullable(pScore.m_307077_()),
                        Optional.ofNullable(pScore.m_305750_())
                    )
                );
        }

        this.setDirty();
    }

    @Override
    protected void m_304975_(ScoreHolder p_309548_, Objective p_312571_) {
        super.m_304975_(p_309548_, p_312571_);
        this.setDirty();
    }

    @Override
    public void onPlayerRemoved(ScoreHolder p_310662_) {
        super.onPlayerRemoved(p_310662_);
        this.server.getPlayerList().broadcastAll(new ClientboundResetScorePacket(p_310662_.getScoreboardName(), null));
        this.setDirty();
    }

    @Override
    public void onPlayerScoreRemoved(ScoreHolder p_310122_, Objective pObjective) {
        super.onPlayerScoreRemoved(p_310122_, pObjective);
        if (this.trackedObjectives.contains(pObjective)) {
            this.server.getPlayerList().broadcastAll(new ClientboundResetScorePacket(p_310122_.getScoreboardName(), pObjective.getName()));
        }

        this.setDirty();
    }

    @Override
    public void setDisplayObjective(DisplaySlot pSlot, @Nullable Objective pObjective) {
        Objective objective = this.getDisplayObjective(pSlot);
        super.setDisplayObjective(pSlot, pObjective);
        if (objective != pObjective && objective != null) {
            if (this.getObjectiveDisplaySlotCount(objective) > 0) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(pSlot, pObjective));
            } else {
                this.stopTrackingObjective(objective);
            }
        }

        if (pObjective != null) {
            if (this.trackedObjectives.contains(pObjective)) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(pSlot, pObjective));
            } else {
                this.startTrackingObjective(pObjective);
            }
        }

        this.setDirty();
    }

    @Override
    public boolean addPlayerToTeam(String pPlayerName, PlayerTeam pTeam) {
        if (super.addPlayerToTeam(pPlayerName, pTeam)) {
            this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(pTeam, pPlayerName, ClientboundSetPlayerTeamPacket.Action.ADD));
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removePlayerFromTeam(String pUsername, PlayerTeam pPlayerTeam) {
        super.removePlayerFromTeam(pUsername, pPlayerTeam);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createPlayerPacket(pPlayerTeam, pUsername, ClientboundSetPlayerTeamPacket.Action.REMOVE));
        this.setDirty();
    }

    @Override
    public void onObjectiveAdded(Objective pObjective) {
        super.onObjectiveAdded(pObjective);
        this.setDirty();
    }

    @Override
    public void onObjectiveChanged(Objective pObjective) {
        super.onObjectiveChanged(pObjective);
        if (this.trackedObjectives.contains(pObjective)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket(pObjective, 2));
        }

        this.setDirty();
    }

    @Override
    public void onObjectiveRemoved(Objective pObjective) {
        super.onObjectiveRemoved(pObjective);
        if (this.trackedObjectives.contains(pObjective)) {
            this.stopTrackingObjective(pObjective);
        }

        this.setDirty();
    }

    @Override
    public void onTeamAdded(PlayerTeam pPlayerTeam) {
        super.onTeamAdded(pPlayerTeam);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(pPlayerTeam, true));
        this.setDirty();
    }

    @Override
    public void onTeamChanged(PlayerTeam pPlayerTeam) {
        super.onTeamChanged(pPlayerTeam);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(pPlayerTeam, false));
        this.setDirty();
    }

    @Override
    public void onTeamRemoved(PlayerTeam pPlayerTeam) {
        super.onTeamRemoved(pPlayerTeam);
        this.server.getPlayerList().broadcastAll(ClientboundSetPlayerTeamPacket.createRemovePacket(pPlayerTeam));
        this.setDirty();
    }

    public void addDirtyListener(Runnable pRunnable) {
        this.dirtyListeners.add(pRunnable);
    }

    protected void setDirty() {
        for (Runnable runnable : this.dirtyListeners) {
            runnable.run();
        }
    }

    public List<Packet<?>> getStartTrackingPackets(Objective pObjective) {
        List<Packet<?>> list = Lists.newArrayList();
        list.add(new ClientboundSetObjectivePacket(pObjective, 0));

        for (DisplaySlot displayslot : DisplaySlot.values()) {
            if (this.getDisplayObjective(displayslot) == pObjective) {
                list.add(new ClientboundSetDisplayObjectivePacket(displayslot, pObjective));
            }
        }

        for (PlayerScoreEntry playerscoreentry : this.m_306706_(pObjective)) {
            list.add(
                new ClientboundSetScorePacket(
                    playerscoreentry.f_302847_(),
                    pObjective.getName(),
                    playerscoreentry.f_303807_(),
                    Optional.ofNullable(playerscoreentry.f_303157_()),
                    Optional.ofNullable(playerscoreentry.f_303706_())
                )
            );
        }

        return list;
    }

    public void startTrackingObjective(Objective pObjective) {
        List<Packet<?>> list = this.getStartTrackingPackets(pObjective);

        for (ServerPlayer serverplayer : this.server.getPlayerList().getPlayers()) {
            for (Packet<?> packet : list) {
                serverplayer.connection.send(packet);
            }
        }

        this.trackedObjectives.add(pObjective);
    }

    public List<Packet<?>> getStopTrackingPackets(Objective pObjective) {
        List<Packet<?>> list = Lists.newArrayList();
        list.add(new ClientboundSetObjectivePacket(pObjective, 1));

        for (DisplaySlot displayslot : DisplaySlot.values()) {
            if (this.getDisplayObjective(displayslot) == pObjective) {
                list.add(new ClientboundSetDisplayObjectivePacket(displayslot, pObjective));
            }
        }

        return list;
    }

    public void stopTrackingObjective(Objective pObjective) {
        List<Packet<?>> list = this.getStopTrackingPackets(pObjective);

        for (ServerPlayer serverplayer : this.server.getPlayerList().getPlayers()) {
            for (Packet<?> packet : list) {
                serverplayer.connection.send(packet);
            }
        }

        this.trackedObjectives.remove(pObjective);
    }

    public int getObjectiveDisplaySlotCount(Objective pObjective) {
        int i = 0;

        for (DisplaySlot displayslot : DisplaySlot.values()) {
            if (this.getDisplayObjective(displayslot) == pObjective) {
                i++;
            }
        }

        return i;
    }

    public SavedData.Factory<ScoreboardSaveData> dataFactory() {
        return new SavedData.Factory<>(this::createData, this::createData, DataFixTypes.SAVED_DATA_SCOREBOARD);
    }

    private ScoreboardSaveData createData() {
        ScoreboardSaveData scoreboardsavedata = new ScoreboardSaveData(this);
        this.addDirtyListener(scoreboardsavedata::setDirty);
        return scoreboardsavedata;
    }

    private ScoreboardSaveData createData(CompoundTag p_180014_, HolderLookup.Provider p_336023_) {
        return this.createData().load(p_180014_, p_336023_);
    }

    public static enum Method {
        CHANGE,
        REMOVE;
    }
}