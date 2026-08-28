package com.abs.app.common.util;

import java.util.UUID;

public class GenerateIdUtil {
    public static String GenerateId(){
        return UUID.randomUUID().toString();
    }

    public static String GenerateId(String salt, int limit) {
        return salt + "_" + UUID.randomUUID().toString().substring(0, limit);
    }
}
