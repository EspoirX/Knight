package com.lzx.knight.router;

import com.lzx.knight.router.intercept.ISyInterceptor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RouterRegister {

    private HashMap<String, RouterInfo> routerMap = new HashMap<>();

    public void register(String key, String className, ISyInterceptor... interceptors) {
        RouterInfo routerInfo = routerMap.get(key);
        if (routerInfo == null) {
            routerInfo = new RouterInfo(className, interceptors);
            routerMap.put(key, routerInfo);
        }
    }

    public HashMap<String, RouterInfo> getRouterMap() {
        return routerMap;
    }

    static class RouterInfo {
        String className;
        List<ISyInterceptor> interceptors;

        public RouterInfo(String className, ISyInterceptor... interceptors) {
            this.className = className;
            this.interceptors = Arrays.asList(interceptors);
        }
    }
}
