package net.minecraft.network.protocol;

/**
 * The direction of packets.
 */
public enum PacketFlow {
    SERVERBOUND("serverbound"),
    CLIENTBOUND("clientbound");

    private final String f_316308_;

    private PacketFlow(final String p_330878_) {
        this.f_316308_ = p_330878_;
    }

    public PacketFlow getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }

    public String m_321669_() {
        return this.f_316308_;
    }
}