package com.andy.LuFM.model;

/**
 * Created by wanglu on 15/11/16.
 */
public class SectionItem {
    static final int TYPE_3RD_PARTY_AD_ITEM = 8;
    static final int TYPE_AD_ITEM = 7;
    static final int TYPE_ALL = 1;
    static final int TYPE_COLLECTION = 4;
    static final int TYPE_COUNT = 9;
    static final int TYPE_ITEM = 3;
    static final int TYPE_SECTION = 0;
    static final int TYPE_TAG = 2;
    public Object data;
    public final int type;

    public SectionItem(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
