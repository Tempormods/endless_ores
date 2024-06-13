package net.minecraft.network.protocol;

import net.minecraft.resources.ResourceLocation;

public record PacketType<T extends Packet<?>>(PacketFlow f_314819_, ResourceLocation f_314851_) {
    @Override
    public String toString() {
        return this.f_314819_.m_321669_() + "/" + this.f_314851_;
    }
}