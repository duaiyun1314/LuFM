package com.andy.LuFM.model;

import java.io.Serializable;

/**
 * Created by wanglu on 15/11/16.
 */
public class Node implements Serializable {
    public String nodeName = "node";
    public transient Node parent = null;
}
