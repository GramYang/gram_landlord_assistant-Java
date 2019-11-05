package com.gram.gram_landlord_assistant.util;

import com.gram.gram_landlord_assistant.server.entity.Request;


public class CommonUtil {

    public static boolean checkNotNull(Request request, String... keys) {
        boolean isNotNull = true;
        for(String key : keys) {
            if(request.get(key) == null || request.get(key).equals("")) {
                isNotNull = false;
                break;
            }
        }
        return isNotNull;
    }

    public static boolean checkNotNull(String... strings) {
        boolean isNotNull = true;
        for(String s : strings) {
            if(s == null || s.equals("")) {
                isNotNull = false;
                break;
            }
        }
        return isNotNull;
    }


}
