package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientboundMerchantOffersPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMerchantOffersPacket> f_315243_ = Packet.m_319422_(
        ClientboundMerchantOffersPacket::m_132469_, ClientboundMerchantOffersPacket::new
    );
    private final int containerId;
    private final MerchantOffers offers;
    private final int villagerLevel;
    private final int villagerXp;
    private final boolean showProgress;
    private final boolean canRestock;

    public ClientboundMerchantOffersPacket(int pContainerId, MerchantOffers pOffers, int pVillagerLevel, int pVillagerXp, boolean pShowProgress, boolean pCanRestock) {
        this.containerId = pContainerId;
        this.offers = pOffers.copy();
        this.villagerLevel = pVillagerLevel;
        this.villagerXp = pVillagerXp;
        this.showProgress = pShowProgress;
        this.canRestock = pCanRestock;
    }

    private ClientboundMerchantOffersPacket(RegistryFriendlyByteBuf p_336176_) {
        this.containerId = p_336176_.readVarInt();
        this.offers = MerchantOffers.f_315991_.m_318688_(p_336176_);
        this.villagerLevel = p_336176_.readVarInt();
        this.villagerXp = p_336176_.readVarInt();
        this.showProgress = p_336176_.readBoolean();
        this.canRestock = p_336176_.readBoolean();
    }

    private void m_132469_(RegistryFriendlyByteBuf p_333887_) {
        p_333887_.writeVarInt(this.containerId);
        MerchantOffers.f_315991_.m_318638_(p_333887_, this.offers);
        p_333887_.writeVarInt(this.villagerLevel);
        p_333887_.writeVarInt(this.villagerXp);
        p_333887_.writeBoolean(this.showProgress);
        p_333887_.writeBoolean(this.canRestock);
    }

    @Override
    public PacketType<ClientboundMerchantOffersPacket> write() {
        return GamePacketTypes.f_313974_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleMerchantOffers(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public MerchantOffers getOffers() {
        return this.offers;
    }

    public int getVillagerLevel() {
        return this.villagerLevel;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public boolean showProgress() {
        return this.showProgress;
    }

    public boolean canRestock() {
        return this.canRestock;
    }
}