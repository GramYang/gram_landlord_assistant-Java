package com.gram.gram_landlord_assistant.server;

import com.gram.gram_landlord_assistant.handler.PlayerInfoHandler;
import com.gram.gram_landlord_assistant.server.entity.Request;
import com.gram.gram_landlord_assistant.server.protos.AssistantProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ChannelHandler.Sharable
public class NettyHandler extends ChannelInboundHandlerAdapter {
    private ConcurrentHashMap<Long, Channel> channelMap;
    private Long key = System.currentTimeMillis();
    @Autowired
    private PlayerInfoHandler handler;

    public void setChannelMap(ConcurrentHashMap<Long, Channel> channelMap) {
        this.channelMap = channelMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelMap.put(key, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(channelMap.get(key) != null) channelMap.get(key).close();
        channelMap.remove(key);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) throws Exception {
        if(o instanceof AssistantProto.Request) {
            Request request = new Request();
            AssistantProto.Request builder = (AssistantProto.Request) o;
            request.setKey(builder.getKey());
            request.putAll(builder.getDataMap());
            if(builder.getPicture() != null) request.setPicture(builder.getPicture().toByteArray());
            handler.handle(ctx, request);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent && ((IdleStateEvent)evt).state().equals(IdleState.READER_IDLE)) {
            ctx.close();
            channelMap.remove(key);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
