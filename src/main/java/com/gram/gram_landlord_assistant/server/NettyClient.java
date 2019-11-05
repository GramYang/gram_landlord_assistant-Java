package com.gram.gram_landlord_assistant.server;

import com.gram.gram_landlord_assistant.server.protos.AssistantProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class NettyClient {
    @Value("${assist.port}")
    private int port;
    private static final ConcurrentHashMap<Long, Channel> channelMap = new ConcurrentHashMap<>();
    private static final EventExecutorGroup handleGroup = new DefaultEventExecutorGroup(16);
    private static NioEventLoopGroup boss = new NioEventLoopGroup(1);
    private static NioEventLoopGroup work = new NioEventLoopGroup();
    @Autowired
    private NettyHandler handler;

    @PostConstruct
    public void startServer() {
        ServerBootstrap b = new ServerBootstrap();
        handler.setChannelMap(channelMap);
        b.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(120, 0, 0));
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(AssistantProto.Request.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(handleGroup, handler);
//                        SSLEngine engine = ContextSSLFactory.getServerSslContext().createSSLEngine();
//                        engine.setUseClientMode(false);
//                        engine.setNeedClientAuth(true);
//                        ch.pipeline().addFirst(new SslHandler(engine));
                    }
                }).bind(port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.removeListener(this);
                if(!future.isSuccess() && future.cause() != null) log.error("服务器绑定端口失败", future.cause());
                if(future.isSuccess()) log.info("服务器绑定端口成功");
            }
        });

    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        boss.shutdownGracefully().sync();
        work.shutdownGracefully().sync();
        log.info("Assist服务器关闭！！");
    }
}
