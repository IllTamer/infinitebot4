package com.illtamer.infinite.bot.api.config;

import com.illtamer.infinite.bot.api.event.Event;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.function.Consumer;

/**
 * go-cqhttp WebSocket 连接初始化类
 * */
@Log
public class CQHttpWebSocketConfiguration {

    @Setter
    @Getter
    private static String httpUri;
    @Setter
    @Getter
    private static String authorization;
    @Getter
    private static boolean running = false;
    @Getter
    private static Channel channel;

    public static void start(@NotNull String httpUri, @NotNull String wsUri, @Nullable String authorization, Consumer<Event> eventConsumer) {
        CQHttpWebSocketConfiguration.httpUri = httpUri;
        CQHttpWebSocketConfiguration.authorization = authorization;
        EventHandler eventHandler = new EventHandler(eventConsumer);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new HttpClientCodec())
                                    // 添加一个用于支持大数据流的支持
                                    .addLast(new ChunkedWriteHandler())
                                    // 添加一个聚合器，这个聚合器主要是将HttpMessage聚合成FullHttpRequest/Response
                                    .addLast(new HttpObjectAggregator(1024 * 64))
                                    .addLast(eventHandler);
                        }
                    });

            URI websocketURI = new URI(wsUri);
            HttpHeaders httpHeaders = new DefaultHttpHeaders()
                    .set("Content-Type", "application/json");
            if (authorization != null && authorization.length() != 0)
                httpHeaders.set("Authorization", authorization);
            // 进行握手
            WebSocketClientHandshaker handshake = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, httpHeaders);
            channel = bootstrap
                    .connect(websocketURI.getHost(), websocketURI.getPort())
                    .sync()
                    .channel();
            eventHandler.setHandshake(handshake);
            handshake.handshake(channel);
            // 阻塞等待是否握手成功
            eventHandler.getHandshakeFuture().sync();
            running = true;
            log.info("go-cqhttp websocket 握手成功");
            channel.closeFuture().sync();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
