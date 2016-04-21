package com.andy.LuFM.model;

import com.andy.LuFM.helper.ChannelHelper;

import java.util.List;

/**
 * Created by Andy.Wang on 2016/4/21.
 */
public class Attribute extends Node {
    public int id;
    public String name;

    public Attribute() {
        this.name = "";
        this.id = 0;
        this.nodeName = "attribute";
    }

    public int getCatid() {
        if (this.parent == null || !this.parent.nodeName.equalsIgnoreCase("category")) {
            return 0;
        }
        return ((CategoryNode) this.parent).categoryId;
    }

  /*  public List<ChannelNode> getLstLiveChannels(boolean readCache) {
        if (this.parent != null && this.parent.nodeName.equalsIgnoreCase("category") && ((CategoryNode) this.parent).isLiveCategory()) {
            return ChannelHelper.getInstance().getLstLiveChannelsByAttrPath(((CategoryNode) this.parent).categoryId, String.valueOf(this.id), readCache);
        }
        return null;
    }

    public List<ChannelNode> getLstChannels() {
        int catid = getCatid();
        if (catid != 0) {
            return ChannelHelper.getInstance().getLstChannelsByAttrPath(catid, String.valueOf(this.id));
        }
        return null;
    }*/

}
