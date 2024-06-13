package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;

public class ServerPackCommand {
    public static void m_305387_(CommandDispatcher<CommandSourceStack> p_311476_) {
        p_311476_.register(
            Commands.literal("serverpack")
                .requires(p_312279_ -> p_312279_.hasPermission(2))
                .then(
                    Commands.literal("push")
                        .then(
                            Commands.argument("url", StringArgumentType.string())
                                .then(
                                    Commands.argument("uuid", UuidArgument.uuid())
                                        .then(
                                            Commands.argument("hash", StringArgumentType.word())
                                                .executes(
                                                    p_310536_ -> m_305052_(
                                                            p_310536_.getSource(),
                                                            StringArgumentType.getString(p_310536_, "url"),
                                                            Optional.of(UuidArgument.getUuid(p_310536_, "uuid")),
                                                            Optional.of(StringArgumentType.getString(p_310536_, "hash"))
                                                        )
                                                )
                                        )
                                        .executes(
                                            p_311224_ -> m_305052_(
                                                    p_311224_.getSource(),
                                                    StringArgumentType.getString(p_311224_, "url"),
                                                    Optional.of(UuidArgument.getUuid(p_311224_, "uuid")),
                                                    Optional.empty()
                                                )
                                        )
                                )
                                .executes(
                                    p_310851_ -> m_305052_(
                                            p_310851_.getSource(), StringArgumentType.getString(p_310851_, "url"), Optional.empty(), Optional.empty()
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("pop")
                        .then(
                            Commands.argument("uuid", UuidArgument.uuid())
                                .executes(p_311174_ -> m_306703_(p_311174_.getSource(), UuidArgument.getUuid(p_311174_, "uuid")))
                        )
                )
        );
    }

    private static void m_306175_(CommandSourceStack p_311498_, Packet<?> p_310286_) {
        p_311498_.getServer().getConnection().getConnections().forEach(p_310319_ -> p_310319_.send(p_310286_));
    }

    private static int m_305052_(CommandSourceStack p_309403_, String p_309919_, Optional<UUID> p_311640_, Optional<String> p_311429_) {
        UUID uuid = p_311640_.orElseGet(() -> UUID.nameUUIDFromBytes(p_309919_.getBytes(StandardCharsets.UTF_8)));
        String s = p_311429_.orElse("");
        ClientboundResourcePackPushPacket clientboundresourcepackpushpacket = new ClientboundResourcePackPushPacket(uuid, p_309919_, s, false, null);
        m_306175_(p_309403_, clientboundresourcepackpushpacket);
        return 0;
    }

    private static int m_306703_(CommandSourceStack p_311491_, UUID p_311737_) {
        ClientboundResourcePackPopPacket clientboundresourcepackpoppacket = new ClientboundResourcePackPopPacket(Optional.of(p_311737_));
        m_306175_(p_311491_, clientboundresourcepackpoppacket);
        return 0;
    }
}