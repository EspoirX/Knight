package com.lzx.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelationshipList {

    private RelationshipList() {
    }

    public static RelationshipList getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private static final RelationshipList sInstance = new RelationshipList();
    }

    public HashMap<String, String> serviceImplMap = new HashMap<>();
    public List<String> serviceList = new ArrayList<>();
}
