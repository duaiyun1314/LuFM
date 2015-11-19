package com.andy.LuFM.data;

import com.andy.LuFM.model.RootNode;

/**
 * 存储必要信息
 */
public class InfoManager {
    private static InfoManager instance;
    private RootNode mRootNode = new RootNode();

    private InfoManager() {

    }

    public static synchronized InfoManager getInstance() {
        if (instance == null) {
            instance = new InfoManager();
        }
        return instance;
    }

    public RootNode root() {
        return this.mRootNode;
    }
}
