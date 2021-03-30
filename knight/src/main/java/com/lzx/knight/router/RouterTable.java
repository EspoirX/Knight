package com.lzx.knight.router;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RouterTable implements IRouterTable {

    private static HashMap<String, String> routerMap = new HashMap<>();

    static {
        routerMap.put("KnightRouter://ActivityA", "com.lzx.demo.ActivityA");

        routerMap.put("KnightRouter://ActivityB", "com.lzx.demo.ActivityB");
        routerMap.put("大胸萝莉://ActivityB", "com.lzx.demo.ActivityB");

        routerMap.put("KnightRouter://ActivityC", "com.lzx.demo.ActivityC");

        routerMap.put("KnightRouter://ActivityD", "com.lzx.demo.ActivityD");
        routerMap.put("XIAN://ActivityD", "com.lzx.demo.ActivityD");
    }

    @NotNull
    @Override
    public HashMap<String, String> getRouterMap() {
        return routerMap;
    }
}
