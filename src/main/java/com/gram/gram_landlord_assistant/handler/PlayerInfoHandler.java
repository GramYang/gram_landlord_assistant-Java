package com.gram.gram_landlord_assistant.handler;

import com.google.protobuf.ByteString;
import com.gram.gram_landlord_assistant.server.entity.Request;
import com.gram.gram_landlord_assistant.server.entity.Response;
import com.gram.gram_landlord_assistant.server.protos.AssistantProto;
import com.gram.gram_landlord_assistant.services.PlayerInfoService;
import com.gram.gram_landlord_assistant.util.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlayerInfoHandler{
    @Autowired
    private PlayerInfoService playerInfoService;

    public void handle(ChannelHandlerContext ctx, Request request) {
        Response response = new Response();
        //查询玩家信息
        if(request.getKey().equals(RequestKey.GET_PLAYER_INFO)) {
            //查询的应该是用户名
            if(CommonUtil.checkNotNull(request, "username")) {
                response = playerInfoService.getPlayerInfo(request.get("username"));
                response.put("username", request.get("username"));
                response.setKey(request.getKey());
            }
            doWriteAndFlush(ctx, response);
        }

        //玩家赢了xxx分
        if(request.getKey().equals(RequestKey.PLAYER_WIN)) {
            if(CommonUtil.checkNotNull(request, "username", "password", "money")) {
                log.info("玩家：" + request.get("username") + "赢取：" + request.get("money") + "分！");
                response = playerInfoService.winOnce(request.get("username"), request.get("password"), request.get("money"));
                response.setKey(request.getKey());
            }
            doWriteAndFlush(ctx, response);
        }

        //玩家输了xxx分
        if(request.getKey().equals(RequestKey.PLAYER_LOSE)) {
            if(CommonUtil.checkNotNull(request, "username", "password", "money")) {
                log.info("玩家：" + request.get("username") + "输了：" + request.get("money") + "分！");
                response = playerInfoService.loseOnce(request.get("username"), request.get("password"), request.get("money"));
                response.setKey(request.getKey());
            }
            doWriteAndFlush(ctx, response);
        }
        
        //玩家上传头像
        if(request.getKey().equals(RequestKey.UPDATE_AVATAR)) {
            if(CommonUtil.checkNotNull(request, "username", "password", "avatar") && request.getPicture() != null) {
                response = playerInfoService.uploadAvatar(request.get("username"), request.get("password"), request.get("avatar"), request.getPicture());
                response.setKey(request.getKey());
            }
            doWriteAndFlush(ctx, response);
        }
    }

    private void doWriteAndFlush(ChannelHandlerContext ctx, Response response) {
        if(ctx != null && response != null) {
            AssistantProto.Response.Builder builder = AssistantProto.Response.newBuilder();
            builder.setKey(response.getKey());
            builder.setCode(response.getCode());
            builder.setMessage(response.getMessage());
            builder.putAllData(response.getAll());
            if(response.getPiture() != null) builder.setPicture(ByteString.copyFrom(response.getPiture()));
            AssistantProto.Response sendResponse = builder.build();
            ctx.writeAndFlush(sendResponse);
        }
    }
}
