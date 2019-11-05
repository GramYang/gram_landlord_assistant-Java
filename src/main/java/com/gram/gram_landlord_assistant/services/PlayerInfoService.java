package com.gram.gram_landlord_assistant.services;

import com.gram.gram_landlord_assistant.server.entity.Response;

public interface PlayerInfoService {
    Response getPlayerInfo(String username);
    Response winOnce(String username, String password, String money);
    Response loseOnce(String username, String password, String money);
    Response uploadAvatar(String username, String password, String avatar, byte[] picture);
}
