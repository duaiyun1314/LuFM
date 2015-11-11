package com.andy.LuFM.model;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class CategoryNode {
    int categoryId;
    int hasChild;
    String mAttributesPath;
    String name;
    int parentId;
    int sectionId;
    int type;
    String nodeName;

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
}
