package com.andy.LuFM.model;

import com.andy.LuFM.data.InfoManager;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by wanglu on 15/11/16.
 */
public class Node implements Serializable, InfoManager.INodeEventListener {
    public transient Node child = null;
    public transient Node nextSibling = null;
    public String nodeName = "node";
    public transient Node parent = null;
    public transient Node prevSibling = null;


    @Override
    public void onNodeUpdated(Object obj, String str) {

    }

    @Override
    public void onNodeUpdated(Object obj, Map<String, String> map, String str) {

    }
}
