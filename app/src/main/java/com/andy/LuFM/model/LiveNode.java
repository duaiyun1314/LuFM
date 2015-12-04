package com.andy.LuFM.model;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy.Wang on 2015/12/4.
 */
public class LiveNode extends Node {
    private transient boolean hasRestored;
    private transient boolean hasRestoredLiveCategory;
    public int id;
    private transient Node mLocalAttrNode;
    private List<CategoryNode> mLstContentCategoryNodes;
    private List<CategoryNode> mLstLiveCategoryNodes;
    private List<CategoryNode> mLstLiveCategoryNodesToDB;
    private List<CategoryNode> mLstRegionCategoryNodes;
    private CategoryNode mRadioCategoryNode;
    //public transient RadioNode mRadioNode;
    public String name;
    public String type;

    public LiveNode() {
        this.name = "\u5e7f\u64ad\u7535\u53f0";
        this.id = 1;
        this.type = "channel";
        this.mLstContentCategoryNodes = new ArrayList();
        this.mLstRegionCategoryNodes = new ArrayList();
        this.hasRestoredLiveCategory = false;
        this.hasRestored = false;
        this.nodeName = "live";
        //  init();
    }

    private void buildCategory() {
        if (this.mLstContentCategoryNodes.size() <= 0 || this.mLstRegionCategoryNodes.size() <= 0) {
            List<CategoryNode> lstnodes = getLiveCategoryNodes();
            if (lstnodes != null) {
                for (int i = 0; i < lstnodes.size(); i++) {
                    if (((CategoryNode) lstnodes.get(i)).isLiveContentCategory()) {
                        this.mLstContentCategoryNodes.add((CategoryNode) lstnodes.get(i));
                    } else if (((CategoryNode) lstnodes.get(i)).isLiveRegionCategory()) {
                        this.mLstRegionCategoryNodes.add((CategoryNode) lstnodes.get(i));
                    }
                }
            }
        }
    }

    /* public List<ChannelNode> getLstChannelByRegionAndContent(CategoryNode content, CategoryNode region) {
         if (content != null && region == null) {
             return content.getLstChannels();
         }
         if (content == null && region != null) {
             return region.getLstChannels();
         }
         if (!(content == null || region == null)) {
             List<ChannelNode> lstRNodes = region.getLstLiveChannels(true);
             List<ChannelNode> lstCNodes = content.getLstLiveChannels(true);
             if (!(lstRNodes == null || lstCNodes == null)) {
                 List<ChannelNode> lstNodes = new ArrayList();
                 for (int i = 0; i < lstRNodes.size(); i++) {
                     for (int j = 0; j < lstCNodes.size(); j++) {
                         if (((ChannelNode) lstRNodes.get(i)).channelId == ((ChannelNode) lstCNodes.get(j)).channelId) {
                             lstNodes.add((ChannelNode) lstRNodes.get(i));
                             break;
                         }
                     }
                 }
                 if (lstNodes.size() > 0) {
                     return lstNodes;
                 }
             }
         }
         return null;
     }

     public List<ChannelNode> getLstChannelByRegionAndContent(CategoryNode content, Attribute region) {
         if (content != null && region == null) {
             return content.getLstChannels();
         }
         if (content == null && region != null) {
             return region.getLstChannels();
         }
         if (!(content == null || region == null)) {
             List<ChannelNode> lstRNodes = region.getLstLiveChannels(true);
             List<ChannelNode> lstCNodes = content.getLstLiveChannels(true);
             if (!(lstRNodes == null || lstCNodes == null)) {
                 List<ChannelNode> lstNodes = new ArrayList();
                 for (int i = 0; i < lstRNodes.size(); i++) {
                     for (int j = 0; j < lstCNodes.size(); j++) {
                         if (((ChannelNode) lstRNodes.get(i)).channelId == ((ChannelNode) lstCNodes.get(j)).channelId) {
                             lstNodes.add((ChannelNode) lstRNodes.get(i));
                             break;
                         }
                     }
                 }
                 if (lstNodes.size() > 0) {
                     return lstNodes;
                 }
             }
         }
         return null;
     }
 */
    public List<CategoryNode> getContentCategory() {
        if (this.mLstContentCategoryNodes.size() > 0) {
            return this.mLstContentCategoryNodes;
        }
        buildCategory();
        return this.mLstContentCategoryNodes;
    }

    public List<CategoryNode> getRegionCategory() {
        if (this.mLstRegionCategoryNodes.size() > 0) {
            return this.mLstRegionCategoryNodes;
        }
        buildCategory();
        return this.mLstRegionCategoryNodes;
    }

    public CategoryNode getCategoryNode(int catid) {
        int i;
        if (this.mLstRegionCategoryNodes == null) {
            getRegionCategory();
        }
        if (this.mLstRegionCategoryNodes != null) {
            for (i = 0; i < this.mLstRegionCategoryNodes.size(); i++) {
                if (((CategoryNode) this.mLstRegionCategoryNodes.get(i)).categoryId == catid) {
                    return (CategoryNode) this.mLstRegionCategoryNodes.get(i);
                }
            }
        }
        if (this.mLstLiveCategoryNodes == null) {
            getLiveCategoryNodes();
        }
        if (this.mLstLiveCategoryNodes != null) {
            for (i = 0; i < this.mLstLiveCategoryNodes.size(); i++) {
                if (((CategoryNode) this.mLstLiveCategoryNodes.get(i)).categoryId == catid) {
                    return (CategoryNode) this.mLstLiveCategoryNodes.get(i);
                }
            }
        }
        return null;
    }

  /*  public List<Attribute> getRegionAttribute() {
        List<Attributes> lstAttrs = getLstAttributes();
        if (lstAttrs == null) {
            return null;
        }
        for (int i = 0; i < lstAttrs.size(); i++) {
            if (((Attributes) lstAttrs.get(i)).id == 20) {
                if (((Attributes) lstAttrs.get(i)).mLstAttribute != null && InfoManager.getInstance().disableGD()) {
                    for (int j = 0; j < ((Attributes) lstAttrs.get(i)).mLstAttribute.size(); j++) {
                        if (((Attribute) ((Attributes) lstAttrs.get(i)).mLstAttribute.get(j)).name.equalsIgnoreCase("\u5e7f\u4e1c")) {
                            ((Attributes) lstAttrs.get(i)).mLstAttribute.remove(j);
                            break;
                        }
                    }
                }
                return ((Attributes) lstAttrs.get(i)).mLstAttribute;
            }
        }
        return null;
    }

    public List<Attribute> getContentAttribute() {
        List<Attributes> lstAttrs = getLstAttributes();
        if (lstAttrs == null) {
            return null;
        }
        for (int i = 0; i < lstAttrs.size(); i++) {
            if (((Attributes) lstAttrs.get(i)).id == 91) {
                return ((Attributes) lstAttrs.get(i)).mLstAttribute;
            }
        }
        return null;
    }*/

  /*  public List<Attributes> getLstAttributes() {
        if (this.mRadioCategoryNode != null) {
            return this.mRadioCategoryNode.getLstAttributes(true);
        }
        return null;
    }*/

    public List<CategoryNode> getLiveCategoryNodes() {
        if (this.mLstLiveCategoryNodes == null) {
            restoreLiveCategory();
        }
        return this.mLstLiveCategoryNodes;
    }

    public boolean restoreLiveCategory() {
        if (this.hasRestoredLiveCategory) {
            return false;
        }
        this.hasRestoredLiveCategory = true;
        Map<String, Object> param = new HashMap();
        param.put(Constants.PARENT_ID, Integer.valueOf(1));
        Result result = DataManager.getInstance().getData(RequestType.GET_CATEGORY_LIST, null, new DataCommand(null, param));
        List<CategoryNode> res = null;
        if (result.isSuccess()) {
            res = (List) result.getData();
        }
        if (res == null || res.size() <= 0) {
            this.mLstLiveCategoryNodes = new ArrayList();
            return false;
        }
        this.mLstLiveCategoryNodes = res;
        return true;
    }

    /*public void connectRadioNode() {
        if (this.mRadioNode != null) {
            this.mRadioNode.parent = this;
            this.mRadioNode.restoreFromDB();
            QTLocation loc = InfoManager.getInstance().getCurrentLocation();
            if (loc != null && loc.city != null) {
                this.mRadioNode.restoreFromDBByCity(loc.city);
            }
        }
    }

    private void init() {
        if (FMManager.getInstance().isAvailable()) {
            this.mRadioNode = new RadioNode();
        }
        this.mRadioCategoryNode = new CategoryNode();
        this.mRadioCategoryNode.parentId = this.id;
        this.mRadioCategoryNode.categoryId = 5;
        this.mRadioCategoryNode.name = "\u76f4\u64ad\u7535\u53f0";
        this.mRadioCategoryNode.type = 0;
        this.mRadioCategoryNode.hasChild = 1;
        this.mRadioCategoryNode.parent = this;
    }

    public boolean isRadioCategoryNode(CategoryNode node) {
        if (node != null && node.categoryId == this.mRadioCategoryNode.categoryId) {
            return true;
        }
        return false;
    }

    public CategoryNode getRadioCategoryNode() {
        return this.mRadioCategoryNode;
    }

    public void setRegion(String region) {
        if (region != null) {
            List<Attribute> lstAttrs = getRegionAttribute();
            if (lstAttrs != null) {
                for (int i = 0; i < lstAttrs.size(); i++) {
                    if (((Attribute) lstAttrs.get(i)).name.contains(region)) {
                        this.mLocalAttrNode = (Node) lstAttrs.get(i);
                    }
                }
            }
        }
    }

    public Node getLocalCategoryNode() {
        if (this.mLocalAttrNode != null) {
            return this.mLocalAttrNode;
        }
        QTLocation temp = InfoManager.getInstance().getCurrentLocation();
        if (temp != null) {
            setRegion(temp.region);
        }
        return this.mLocalAttrNode;
    }

    public void updateLiveCategoryToDB() {
        if (this.mLstLiveCategoryNodesToDB != null) {
            Map<String, Object> param = new HashMap();
            param.put("nodes", this.mLstLiveCategoryNodesToDB);
            param.put(Constants.PARENT_ID, Integer.valueOf(this.id));
            DataManager.getInstance().getData(RequestTypeold.UPDATEDB_CATEGORY_NODE, null, param);
        }
    }

    public void updateLiveCategory(List<CategoryNode> lstNodes) {
        if (lstNodes != null) {
            for (int i = 0; i < lstNodes.size(); i++) {
                ((CategoryNode) lstNodes.get(i)).parentId = this.id;
            }
            if (this.mLstLiveCategoryNodes == null || this.mLstLiveCategoryNodes.size() == 0) {
                this.mLstLiveCategoryNodes = lstNodes;
            }
            this.mLstLiveCategoryNodesToDB = lstNodes;
            Message msg = new Message();
            msg.what = 8;
            msg.obj = this;
            InfoManager.getInstance().getDataStoreHandler().sendMessage(msg);
        }
    }

    public boolean restoreChildFromDB() {
        if (this.hasRestored) {
            return false;
        }
        this.hasRestored = true;
        Map<String, Object> param = new HashMap();
        param.put(Constants.PARENT_ID, Integer.valueOf(this.id));
        Result result = DataManager.getInstance().getData(RequestTypeold.GETDB_CATEGORY_NODE, null, param).getResult();
        List<Node> res = null;
        if (result.getSuccess()) {
            res = (List) result.getData();
        }
        if (res == null || res.size() <= 0) {
            return false;
        }
        return true;
    }

    public boolean saveChildToDB() {
        return false;
    }*/

}
