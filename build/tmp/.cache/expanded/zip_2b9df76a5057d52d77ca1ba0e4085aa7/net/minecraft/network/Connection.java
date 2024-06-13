package net.minecraft.network;

import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.HandshakeProtocols;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.StatusProtocols;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection extends SimpleChannelInboundHandler<Packet<?>> {
    private static final float AVERAGE_PACKETS_SMOOTHING = 0.75F;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Marker ROOT_MARKER = MarkerFactory.getMarker("NETWORK");
    public static final Marker PACKET_MARKER = Util.make(MarkerFactory.getMarker("NETWORK_PACKETS"), p_202569_ -> p_202569_.add(ROOT_MARKER));
    public static final Marker PACKET_RECEIVED_MARKER = Util.make(MarkerFactory.getMarker("PACKET_RECEIVED"), p_202562_ -> p_202562_.add(PACKET_MARKER));
    public static final Marker PACKET_SENT_MARKER = Util.make(MarkerFactory.getMarker("PACKET_SENT"), p_202557_ -> p_202557_.add(PACKET_MARKER));
    public static final Supplier<NioEventLoopGroup> NETWORK_WORKER_GROUP = Suppliers.memoize(
        () -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build())
    );
    public static final Supplier<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = Suppliers.memoize(
        () -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build())
    );
    public static final Supplier<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = Suppliers.memoize(
        () -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build())
    );
    private static final ProtocolInfo<ServerHandshakePacketListener> f_315400_ = HandshakeProtocols.f_316563_;
    private final PacketFlow receiving;
    private volatile boolean f_316930_ = true;
    private final Queue<Consumer<Connection>> pendingActions = Queues.newConcurrentLinkedQueue();
    private Channel channel;
    private SocketAddress address;
    @Nullable
    private volatile PacketListener disconnectListener;
    @Nullable
    private volatile PacketListener packetListener;
    @Nullable
    private Component disconnectedReason;
    private boolean encrypted;
    private boolean disconnectionHandled;
    private int receivedPackets;
    private int sentPackets;
    private float averageReceivedPackets;
    private float averageSentPackets;
    private int tickCount;
    private boolean handlingFault;
    @Nullable
    private volatile Component delayedDisconnect;
    @Nullable
    BandwidthDebugMonitor bandwidthDebugMonitor;
    private java.util.function.Consumer<Connection> activationHandler;
    private final net.minecraftforge.common.util.PacketLogger packetLogger = new net.minecraftforge.common.util.PacketLogger(this);
    private ProtocolInfo<?> outboundProtocol = null;
    private ProtocolInfo<?> inboundProtocol = null;

    public Connection(PacketFlow pReceiving) {
        this.receiving = pReceiving;
        if (this.receiving == PacketFlow.SERVERBOUND)
            this.inboundProtocol = f_315400_;
        else
            this.outboundProtocol = f_315400_;
    }

    @Override
    public void channelActive(ChannelHandlerContext pContext) throws Exception {
        super.channelActive(pContext);
        this.channel = pContext.channel();
        this.address = this.channel.remoteAddress();
        if (activationHandler != null) activationHandler.accept(this);
        if (this.delayedDisconnect != null) {
            this.disconnect(this.delayedDisconnect);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext pContext) {
        this.disconnect(Component.translatable("disconnect.endOfStream"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext pContext, Throwable pException) {
        if (pException instanceof SkipPacketException) {
            LOGGER.debug("Skipping packet due to errors", pException.getCause());
        } else {
            boolean flag = !this.handlingFault;
            this.handlingFault = true;
            if (this.channel.isOpen()) {
                if (pException instanceof TimeoutException) {
                    LOGGER.debug("Timeout", pException);
                    this.disconnect(Component.translatable("disconnect.timeout"));
                } else {
                    Component component = Component.translatable("disconnect.genericReason", "Internal Exception: " + pException);
                    if (flag) {
                        LOGGER.debug("Failed to sent packet", pException);
                        if (this.getSending() == PacketFlow.CLIENTBOUND) {
                            Packet<?> packet = (Packet<?>)(this.f_316930_
                                ? new ClientboundLoginDisconnectPacket(component)
                                : new ClientboundDisconnectPacket(component));
                            this.send(packet, PacketSendListener.thenRun(() -> this.disconnect(component)));
                        } else {
                            this.disconnect(component);
                        }

                        this.setReadOnly();
                    } else {
                        LOGGER.debug("Double fault", pException);
                        this.disconnect(component);
                    }
                }
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext pContext, Packet<?> pPacket) {
        if (this.channel.isOpen()) {
            PacketListener packetlistener = this.packetListener;
            if (packetlistener == null) {
                throw new IllegalStateException("Received a packet before the packet listener was initialized");
            } else {
                if (packetlistener.shouldHandleMessage(pPacket)) {
                    try {
                        packetLogger.recv(pPacket);
                        genericsFtw(pPacket, packetlistener);
                    } catch (RunningOnDifferentThreadException runningondifferentthreadexception) {
                    } catch (RejectedExecutionException rejectedexecutionexception) {
                        this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
                    } catch (ClassCastException classcastexception) {
                        LOGGER.error("Received {} that couldn't be processed", pPacket.getClass(), classcastexception);
                        this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
                    }

                    this.receivedPackets++;
                }
            }
        }
    }

    private static <T extends PacketListener> void genericsFtw(Packet<T> pPacket, PacketListener pListener) {
        pPacket.handle((T)pListener);
    }

    private void m_318737_(ProtocolInfo<?> p_336036_, PacketListener p_331542_) {
        Validate.notNull(p_331542_, "packetListener");
        PacketFlow packetflow = p_331542_.flow();
        if (packetflow != this.receiving) {
            throw new IllegalStateException("Trying to set listener for wrong side: connection is " + this.receiving + ", but listener is " + packetflow);
        } else {
            ConnectionProtocol connectionprotocol = p_331542_.protocol();
            if (p_336036_.m_320326_() != connectionprotocol) {
                throw new IllegalStateException("Listener protocol (" + connectionprotocol + ") does not match requested one " + p_336036_);
            }
        }
    }

    private static void m_319552_(ChannelFuture p_330528_) {
        try {
            p_330528_.syncUninterruptibly();
        } catch (Exception exception) {
            if (exception instanceof ClosedChannelException) {
                LOGGER.info("Connection closed during protocol change");
            } else {
                throw exception;
            }
        }
    }

    public <T extends PacketListener> void m_324855_(ProtocolInfo<T> p_333271_, T p_330962_) {
        this.m_318737_(p_333271_, p_330962_);
        if (p_333271_.m_319133_() != this.getReceiving()) {
            throw new IllegalStateException("Invalid inbound protocol: " + p_333271_.m_320326_());
        } else {
            this.inboundProtocol = p_333271_;
            this.packetListener = p_330962_;
            this.disconnectListener = null;
            UnconfiguredPipelineHandler.InboundConfigurationTask unconfiguredpipelinehandler$inboundconfigurationtask = UnconfiguredPipelineHandler.m_318869_(
                p_333271_
            );
            BundlerInfo bundlerinfo = p_333271_.m_320896_();
            if (bundlerinfo != null) {
                PacketBundlePacker packetbundlepacker = new PacketBundlePacker(bundlerinfo);
                unconfiguredpipelinehandler$inboundconfigurationtask = unconfiguredpipelinehandler$inboundconfigurationtask.m_323054_(
                    p_326046_ -> p_326046_.pipeline().addAfter("decoder", "bundler", packetbundlepacker)
                );
            }

            m_319552_(this.channel.writeAndFlush(unconfiguredpipelinehandler$inboundconfigurationtask));
        }
    }

    public void m_319763_(ProtocolInfo<?> p_329145_) {
        if (p_329145_.m_319133_() != this.getSending()) {
            throw new IllegalStateException("Invalid outbound protocol: " + p_329145_.m_320326_());
        } else {
            UnconfiguredPipelineHandler.OutboundConfigurationTask unconfiguredpipelinehandler$outboundconfigurationtask = UnconfiguredPipelineHandler.m_320645_(
                p_329145_
            ).m_322612_(
                f -> this.outboundProtocol = p_329145_
            );
            BundlerInfo bundlerinfo = p_329145_.m_320896_();
            if (bundlerinfo != null) {
                PacketBundleUnpacker packetbundleunpacker = new PacketBundleUnpacker(bundlerinfo);
                unconfiguredpipelinehandler$outboundconfigurationtask = unconfiguredpipelinehandler$outboundconfigurationtask.m_322612_(
                    p_326044_ -> p_326044_.pipeline().addAfter("encoder", "unbundler", packetbundleunpacker)
                );
            }

            boolean flag = p_329145_.m_320326_() == ConnectionProtocol.LOGIN;
            m_319552_(this.channel.writeAndFlush(unconfiguredpipelinehandler$outboundconfigurationtask.m_322612_(p_326048_ -> this.f_316930_ = flag)));
        }
    }

    public void setListenerForServerboundHandshake(PacketListener pPacketListener) {
        if (this.packetListener != null) {
            throw new IllegalStateException("Listener already set");
        } else if (this.receiving == PacketFlow.SERVERBOUND
            && pPacketListener.flow() == PacketFlow.SERVERBOUND
            && pPacketListener.protocol() == f_315400_.m_320326_()) {
            this.packetListener = pPacketListener;
        } else {
            throw new IllegalStateException("Invalid initial listener");
        }
    }

    public void initiateServerboundStatusConnection(String pHostName, int pPort, ClientStatusPacketListener pDisconnectListener) {
        this.initiateServerboundConnection(pHostName, pPort, StatusProtocols.f_316093_, StatusProtocols.f_315277_, pDisconnectListener, ClientIntent.STATUS);
    }

    public void initiateServerboundPlayConnection(String pHostName, int pPort, ClientLoginPacketListener pDisconnectListener) {
        this.initiateServerboundConnection(pHostName, pPort, LoginProtocols.f_316141_, LoginProtocols.f_313900_, pDisconnectListener, ClientIntent.LOGIN);
    }

    public <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void m_321635_(
        String p_332429_, int p_334200_, ProtocolInfo<S> p_332351_, ProtocolInfo<C> p_328002_, C p_329302_, boolean p_331884_
    ) {
        this.initiateServerboundConnection(p_332429_, p_334200_, p_332351_, p_328002_, p_329302_, p_331884_ ? ClientIntent.TRANSFER : ClientIntent.LOGIN);
    }

    private <S extends ServerboundPacketListener, C extends ClientboundPacketListener> void initiateServerboundConnection(
        String pHostName, int pPort, ProtocolInfo<S> p_328134_, ProtocolInfo<C> p_329827_, C p_330656_, ClientIntent pIntention
    ) {
        if (p_328134_.m_320326_() != p_329827_.m_320326_()) {
            throw new IllegalStateException("Mismatched initial protocols");
        } else {
            this.disconnectListener = p_330656_;
            this.runOnceConnected(p_326042_ -> {
                this.m_324855_(p_329827_, p_330656_);
                // TODO: Change this to be a immediately sent login custom payload packet?
                p_326042_.sendPacket(new ClientIntentionPacket(SharedConstants.getCurrentVersion().getProtocolVersion(), net.minecraftforge.network.NetworkContext.enhanceHostName(pHostName), pPort, pIntention), null, true);
                this.m_319763_(p_328134_);
            });
        }
    }

    public void send(Packet<?> pPacket) {
        this.send(pPacket, null);
    }

    public void send(Packet<?> pPacket, @Nullable PacketSendListener pSendListener) {
        this.send(pPacket, pSendListener, true);
    }

    public void send(Packet<?> pPacket, @Nullable PacketSendListener pListener, boolean pFlush) {
        if (this.isConnected()) {
            this.flushQueue();
            this.sendPacket(pPacket, pListener, pFlush);
        } else {
            this.pendingActions.add(p_296381_ -> p_296381_.sendPacket(pPacket, pListener, pFlush));
        }
    }

    public void runOnceConnected(Consumer<Connection> pAction) {
        if (this.isConnected()) {
            this.flushQueue();
            pAction.accept(this);
        } else {
            this.pendingActions.add(pAction);
        }
    }

    private void sendPacket(Packet<?> pPacket, @Nullable PacketSendListener pSendListener, boolean pFlush) {
        this.sentPackets++;
        if (this.channel.eventLoop().inEventLoop()) {
            this.doSendPacket(pPacket, pSendListener, pFlush);
        } else {
            this.channel.eventLoop().execute(() -> this.doSendPacket(pPacket, pSendListener, pFlush));
        }
    }

    private void doSendPacket(Packet<?> pPacket, @Nullable PacketSendListener pSendListener, boolean pFlush) {
        ChannelFuture channelfuture = pFlush ? this.channel.writeAndFlush(pPacket) : this.channel.write(pPacket);
        channelfuture.addListener(f -> packetLogger.send(pPacket));
        if (pSendListener != null) {
            channelfuture.addListener(p_243167_ -> {
                if (p_243167_.isSuccess()) {
                    pSendListener.onSuccess();
                } else {
                    Packet<?> packet = pSendListener.onFailure();
                    if (packet != null) {
                        ChannelFuture channelfuture1 = this.channel.writeAndFlush(packet);
                        channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                }
            });
        }

        channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void flushChannel() {
        if (this.isConnected()) {
            this.flush();
        } else {
            this.pendingActions.add(Connection::flush);
        }
    }

    private void flush() {
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.flush();
        } else {
            this.channel.eventLoop().execute(() -> this.channel.flush());
        }
    }

    private void flushQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            synchronized (this.pendingActions) {
                Consumer<Connection> consumer;
                while ((consumer = this.pendingActions.poll()) != null) {
                    consumer.accept(this);
                }
            }
        }
    }

    public void tick() {
        this.flushQueue();
        if (this.packetListener instanceof TickablePacketListener tickablepacketlistener) {
            tickablepacketlistener.tick();
        }

        if (!this.isConnected() && !this.disconnectionHandled) {
            this.handleDisconnection();
        }

        if (this.channel != null) {
            this.channel.flush();
        }

        if (this.tickCount++ % 20 == 0) {
            this.tickSecond();
        }

        if (this.bandwidthDebugMonitor != null) {
            this.bandwidthDebugMonitor.tick();
        }
    }

    protected void tickSecond() {
        this.averageSentPackets = Mth.lerp(0.75F, (float)this.sentPackets, this.averageSentPackets);
        this.averageReceivedPackets = Mth.lerp(0.75F, (float)this.receivedPackets, this.averageReceivedPackets);
        this.sentPackets = 0;
        this.receivedPackets = 0;
    }

    public SocketAddress getRemoteAddress() {
        return this.address;
    }

    public String getLoggableAddress(boolean pLogIps) {
        if (this.address == null) {
            return "local";
        } else {
            return pLogIps ? net.minecraftforge.network.DualStackUtils.getAddressString(this.address) : "IP hidden";
        }
    }

    public void disconnect(Component pMessage) {
        if (this.channel == null) {
            this.delayedDisconnect = pMessage;
        }

        if (this.isConnected()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectedReason = pMessage;
        }
    }

    public boolean isMemoryConnection() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public PacketFlow getReceiving() {
        return this.receiving;
    }

    public PacketFlow getSending() {
        return this.receiving.getOpposite();
    }

    public static Connection connectToServer(InetSocketAddress pAddress, boolean pUseEpollIfAvailable, @Nullable LocalSampleLogger p_333468_) {
        Connection connection = new Connection(PacketFlow.CLIENTBOUND);
        if (p_333468_ != null) {
            connection.setBandwidthLogger(p_333468_);
        }

        ChannelFuture channelfuture = connect(pAddress, pUseEpollIfAvailable, connection);
        channelfuture.syncUninterruptibly();
        return connection;
    }

    public static ChannelFuture connect(InetSocketAddress pAddress, boolean pUseEpollIfAvailable, final Connection pConnection) {
        net.minecraftforge.network.DualStackUtils.checkIPv6(pAddress.getAddress());
        pConnection.activationHandler = net.minecraftforge.network.NetworkRegistry::onConnectionStart;
        Class<? extends SocketChannel> oclass;
        EventLoopGroup eventloopgroup;
        if (Epoll.isAvailable() && pUseEpollIfAvailable) {
            oclass = EpollSocketChannel.class;
            eventloopgroup = NETWORK_EPOLL_WORKER_GROUP.get();
        } else {
            oclass = NioSocketChannel.class;
            eventloopgroup = NETWORK_WORKER_GROUP.get();
        }

        return new Bootstrap().group(eventloopgroup).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel p_129552_) {
                try {
                    p_129552_.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException channelexception) {
                }

                ChannelPipeline channelpipeline = p_129552_.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
                Connection.configureSerialization(channelpipeline, PacketFlow.CLIENTBOUND, false, pConnection.bandwidthDebugMonitor);
                pConnection.configurePacketHandler(channelpipeline);
            }
        }).channel(oclass).connect(pAddress.getAddress(), pAddress.getPort());
    }

    private static String m_323525_(boolean p_334174_) {
        return p_334174_ ? "encoder" : "outbound_config";
    }

    private static String m_323969_(boolean p_334983_) {
        return p_334983_ ? "decoder" : "inbound_config";
    }

    public void configurePacketHandler(ChannelPipeline pPipeline) {
        pPipeline.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext p_335545_, Object p_329198_, ChannelPromise p_332397_) throws Exception {
                super.write(p_335545_, p_329198_, p_332397_);
            }
        }).addLast("packet_handler", this);
    }

    public static void configureSerialization(ChannelPipeline pPipeline, PacketFlow pFlow, boolean p_328504_, @Nullable BandwidthDebugMonitor pBandwithMonitor) {
        PacketFlow packetflow = pFlow.getOpposite();
        boolean flag = pFlow == PacketFlow.SERVERBOUND;
        boolean flag1 = packetflow == PacketFlow.SERVERBOUND;
        pPipeline.addLast("splitter", m_320433_(pBandwithMonitor, p_328504_))
            .addLast(new FlowControlHandler())
            .addLast(m_323969_(flag), (ChannelHandler)(flag ? new PacketDecoder<>(f_315400_) : new UnconfiguredPipelineHandler.Inbound()))
            .addLast("prepender", m_322880_(p_328504_))
            .addLast(m_323525_(flag1), (ChannelHandler)(flag1 ? new PacketEncoder<>(f_315400_) : new UnconfiguredPipelineHandler.Outbound()));
    }

    private static ChannelOutboundHandler m_322880_(boolean p_335200_) {
        return (ChannelOutboundHandler)(p_335200_ ? new NoOpFrameEncoder() : new Varint21LengthFieldPrepender());
    }

    private static ChannelInboundHandler m_320433_(@Nullable BandwidthDebugMonitor p_329567_, boolean p_335874_) {
        if (!p_335874_) {
            return new Varint21FrameDecoder(p_329567_);
        } else {
            return (ChannelInboundHandler)(p_329567_ != null ? new MonitorFrameDecoder(p_329567_) : new NoOpFrameDecoder());
        }
    }

    public static void configureInMemoryPipeline(ChannelPipeline pPipeline, PacketFlow pFlow) {
        configureSerialization(pPipeline, pFlow, true, null);
    }

    public static Connection connectToLocalServer(SocketAddress pAddress) {
        final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
        connection.activationHandler = net.minecraftforge.network.NetworkRegistry::onConnectionStart;
        new Bootstrap().group(LOCAL_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel p_332618_) {
                ChannelPipeline channelpipeline = p_332618_.pipeline();
                Connection.configureInMemoryPipeline(channelpipeline, PacketFlow.CLIENTBOUND);
                connection.configurePacketHandler(channelpipeline);
            }
        }).channel(LocalChannel.class).connect(pAddress).syncUninterruptibly();
        return connection;
    }

    public void setEncryptionKey(Cipher pDecryptingCipher, Cipher pEncryptingCipher) {
        this.encrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder(pDecryptingCipher));
        this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder(pEncryptingCipher));
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnecting() {
        return this.channel == null;
    }

    @Nullable
    public PacketListener getPacketListener() {
        return this.packetListener;
    }

    @Nullable
    public Component getDisconnectedReason() {
        return this.disconnectedReason;
    }

    public void setReadOnly() {
        if (this.channel != null) {
            this.channel.config().setAutoRead(false);
        }
    }

    public void setupCompression(int pThreshold, boolean pValidateDecompressed) {
        if (pThreshold >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder compressiondecoder) {
                compressiondecoder.setThreshold(pThreshold, pValidateDecompressed);
            } else {
                this.channel.pipeline().addAfter("splitter", "decompress", new CompressionDecoder(pThreshold, pValidateDecompressed));
            }

            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder compressionencoder) {
                compressionencoder.setThreshold(pThreshold);
            } else {
                this.channel.pipeline().addAfter("prepender", "compress", new CompressionEncoder(pThreshold));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.disconnectionHandled) {
                LOGGER.warn("handleDisconnection() called twice");
            } else {
                this.disconnectionHandled = true;
                PacketListener packetlistener = this.getPacketListener();
                PacketListener packetlistener1 = packetlistener != null ? packetlistener : this.disconnectListener;
                if (packetlistener1 != null) {
                    Component component = Objects.requireNonNullElseGet(this.getDisconnectedReason(), () -> Component.translatable("multiplayer.disconnect.generic"));
                    packetlistener1.onDisconnect(component);
                }
            }
        }
    }

    public float getAverageReceivedPackets() {
        return this.averageReceivedPackets;
    }

    public float getAverageSentPackets() {
        return this.averageSentPackets;
    }

    public Channel channel() {
        return this.channel;
    }

    public ConnectionProtocol getProtocol() {
        return outboundProtocol != null ? outboundProtocol.m_320326_() : inboundProtocol.m_320326_();
    }

    public ProtocolInfo<?> getInboundProtocolInfo() {
        return this.inboundProtocol;
    }

    public ProtocolInfo<?> getOutputboundProtocolInfo() {
        return this.outboundProtocol;
    }

    public void setBandwidthLogger(LocalSampleLogger p_333554_) {
        this.bandwidthDebugMonitor = new BandwidthDebugMonitor(p_333554_);
    }
}
