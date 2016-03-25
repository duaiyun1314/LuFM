package com.andy.LuFM.model;

/**
 * Created by Andy.Wang on 2016/3/24.
 */
public class BroadcasterNode {
    public String avatar;
    public int id;
    public String nick;
    public String qqId;
    public String qqName;
    public int ringToneId;
    public String weiboId;
    public String weiboName;

    public void update(BroadcasterNode node) {
        if (node != null) {
            this.id = node.id;
            this.nick = node.nick;
            this.weiboId = node.weiboId;
            this.weiboName = node.weiboName;
            this.qqId = node.qqId;
            this.qqName = node.qqName;
            this.avatar = node.avatar;
            this.ringToneId = node.ringToneId;
        }
    }

}
