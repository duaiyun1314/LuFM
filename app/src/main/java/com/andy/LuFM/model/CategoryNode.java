package com.andy.LuFM.model;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class CategoryNode extends Node {
    public static final int LIVE_CHANNEL = 5;
    public static final int MUSIC = 523;
    public static final int NEWS = 545;
    public static final int NOVEL = 521;
    public static final int SPECIAL_TOPIC = 2733;
    public int categoryId;
    int hasChild;
    String mAttributesPath;
    String name;
    int parentId;
    int sectionId;
    int type;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(int hasChild) {
        this.hasChild = hasChild;
    }

    public String getmAttributesPath() {
        return mAttributesPath;
    }

    public void setmAttributesPath(String mAttributesPath) {
        this.mAttributesPath = mAttributesPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    public boolean isRegionCategory() {
        if (this.name.startsWith("\u7701")) {
            return true;
        }
        return false;
    }

    public boolean isLiveCategory() {
        if (this.type == 0 || this.type == 3 || this.type == 4) {
            return true;
        }
        return false;
    }

    public boolean isLiveContentCategory() {
        if (this.type == 3) {
            return true;
        }
        return false;
    }

    public boolean isLiveRegionCategory() {
        if (this.type == 4) {
            return true;
        }
        return false;
    }

}
