package com.gram.gram_landlord_assistant.server;

public interface Constant {
    int DATA_HEADER_LENGTH = 4;
    String DES_KEY = "zhangxiaorong is god of gamblers";
    interface ReturnCode {
        //成功
        int CODE_200 = 200;
        //not found
        int CODE_404 = 404;
        //禁止
        int CODE_403 = 403;
        //服务器错误
        int CODE_500 = 500;
    }

    interface DataType {
        byte REQUEST = 1;
        byte RESPONSE = 2;
    }
}
