package com.andy.LuFM.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Andy.Wang on 2015/12/4.
 */
public class RecommendPlayingInfoNode extends Node {
    private int MAX_ITEMS_FRONTPAGE;
    private List<RecommendPlayingItemNode> mLstPlayingItemsForAll;
    private List<RecommendPlayingItemNode> mLstPlayingItemsForRest;
    private List<RecommendPlayingItemNode> mLstRecommendPlaying;
    private List<RecommendPlayingItemNode> mLstRecommendPlayingForFrontPage;
    private int minEndTimeForAll;
    private int minEndTimeForFrontPage;
    private int minPlayingProgramIndex;

    class RecommendPlayTimeComparator implements Comparator<Node> {
        RecommendPlayTimeComparator() {
        }

        public int compare(Node obj1, Node obj2) {
            if (((RecommendPlayingItemNode) obj1).startTime() < ((RecommendPlayingItemNode) obj2).startTime()) {
                return -1;
            }
            if (((RecommendPlayingItemNode) obj1).startTime() > ((RecommendPlayingItemNode) obj2).startTime()) {
                return 1;
            }
            if (((RecommendPlayingItemNode) obj1).endTime() < ((RecommendPlayingItemNode) obj2).endTime()) {
                return -1;
            }
            if (((RecommendPlayingItemNode) obj1).endTime() > ((RecommendPlayingItemNode) obj2).endTime()) {
                return 1;
            }
            return 0;
        }
    }

    public RecommendPlayingInfoNode() {
        this.mLstRecommendPlayingForFrontPage = new ArrayList();
        this.MAX_ITEMS_FRONTPAGE = 3;
        this.mLstPlayingItemsForAll = new ArrayList();
        this.mLstPlayingItemsForRest = new ArrayList();
        this.minPlayingProgramIndex = 0;
        this.nodeName = "recommendplayinginfo";
    }

    public void updateDB() {
    }

    public boolean checkRecommendPlayingList(long time) {
        if (this.mLstRecommendPlaying == null || this.mLstRecommendPlaying.size() == 0) {
            return false;
        }
        long relativeTime = (long) getRelativeTime(1000 * time);
        if (((long) this.minEndTimeForAll) < relativeTime) {
            return true;
        }
        if (((long) this.minEndTimeForFrontPage) < relativeTime) {
            return true;
        }
        int i = 0;
        while (i < this.mLstPlayingItemsForRest.size() && ((long) ((RecommendPlayingItemNode) this.mLstPlayingItemsForRest.get(i)).startTime()) > relativeTime) {
            i++;
        }
        if (i == this.mLstPlayingItemsForRest.size()) {
            return false;
        }
        getCurrPlayingForAll();
        getCurrPlayingFrontPageNodes();
        return true;
    }

    public void setRecommendList(List<RecommendPlayingItemNode> lstNodes) {
        if (lstNodes.size() != 0) {
            this.mLstPlayingItemsForAll.clear();
            this.mLstPlayingItemsForRest.clear();
            this.mLstRecommendPlayingForFrontPage.clear();
            this.minEndTimeForFrontPage = 0;
            this.minEndTimeForAll = 0;
            this.minPlayingProgramIndex = 0;
            this.mLstRecommendPlaying = lstNodes;
            try {
                Collections.sort(this.mLstRecommendPlaying, new RecommendPlayTimeComparator());
            } catch (Exception e) {
            }
        }
    }

    public List<RecommendPlayingItemNode> getCurrPlayingForShow() {
        return getCurrPlayingForAll();
    }

    private List<RecommendPlayingItemNode> getCurrPlayingForAll() {
        if (this.mLstRecommendPlaying == null || this.mLstRecommendPlaying.size() == 0) {
            return null;
        }
        long relativeTime = (long) getRelativeTime(System.currentTimeMillis());
        this.mLstPlayingItemsForAll.clear();
        this.mLstPlayingItemsForRest.clear();
        boolean flag = false;
        for (int i = this.minPlayingProgramIndex; i < this.mLstRecommendPlaying.size(); i++) {
            RecommendPlayingItemNode temp = (RecommendPlayingItemNode) this.mLstRecommendPlaying.get(i);
            if (relativeTime >= ((long) temp.startTime()) && relativeTime < ((long) temp.endTime())) {
                if (!flag) {
                    flag = true;
                    this.minPlayingProgramIndex = i;
                }
                this.mLstPlayingItemsForAll.add(temp);
            } else if (relativeTime < ((long) temp.startTime())) {
                this.mLstPlayingItemsForRest.add(temp);
            }
        }
        this.minEndTimeForAll = Integer.MAX_VALUE;
        if (this.mLstPlayingItemsForAll.size() <= 0) {
            return null;
        }
        this.minEndTimeForAll = ((RecommendPlayingItemNode) this.mLstPlayingItemsForAll.get(0)).endTime();
        for (int j = 0; j < this.mLstPlayingItemsForAll.size(); j++) {
            if (((RecommendPlayingItemNode) this.mLstPlayingItemsForAll.get(j)).endTime() < this.minEndTimeForAll) {
                this.minEndTimeForAll = ((RecommendPlayingItemNode) this.mLstPlayingItemsForAll.get(j)).endTime();
            }
        }
        return this.mLstPlayingItemsForAll;
    }

    public List<RecommendPlayingItemNode> getCurrPlayingFrontPageNodes() {
        if (this.mLstRecommendPlaying == null || this.mLstRecommendPlaying.size() == 0) {
            return null;
        }
        long relativeTime = (long) getRelativeTime(System.currentTimeMillis());
        this.mLstRecommendPlayingForFrontPage.clear();
        int cnt = 0;
        for (int i = 0; i < this.mLstRecommendPlaying.size(); i++) {
            RecommendPlayingItemNode temp = (RecommendPlayingItemNode) this.mLstRecommendPlaying.get(i);
            if (relativeTime > ((long) temp.startTime()) && relativeTime < ((long) temp.endTime()) && cnt < this.MAX_ITEMS_FRONTPAGE) {
                this.mLstRecommendPlayingForFrontPage.add(temp);
                cnt++;
                if (cnt == this.MAX_ITEMS_FRONTPAGE) {
                    break;
                }
            }
        }
        this.minEndTimeForFrontPage = Integer.MAX_VALUE;
        if (this.mLstRecommendPlayingForFrontPage.size() <= 0) {
            return null;
        }
        this.minEndTimeForFrontPage = ((RecommendPlayingItemNode) this.mLstRecommendPlayingForFrontPage.get(0)).endTime();
        for (int j = 0; j < this.mLstRecommendPlayingForFrontPage.size(); j++) {
            if (((RecommendPlayingItemNode) this.mLstRecommendPlayingForFrontPage.get(j)).endTime() < this.minEndTimeForFrontPage) {
                this.minEndTimeForFrontPage = ((RecommendPlayingItemNode) this.mLstRecommendPlayingForFrontPage.get(j)).endTime();
            }
        }
        return this.mLstRecommendPlayingForFrontPage;
    }

    public List<RecommendPlayingItemNode> getRecommendPlayingItemNodes(long time) {
        if (this.mLstRecommendPlaying == null || this.mLstRecommendPlaying.size() == 0) {
            return null;
        }
        List<RecommendPlayingItemNode> lstNodes = new ArrayList();
        long relativeTime = (long) getRelativeTime(1000 * time);
        for (int i = 0; i < this.mLstRecommendPlaying.size(); i++) {
            RecommendPlayingItemNode temp = (RecommendPlayingItemNode) this.mLstRecommendPlaying.get(i);
            if (relativeTime > ((long) temp.startTime()) && relativeTime < ((long) temp.endTime())) {
                lstNodes.add(temp);
            }
        }
        if (lstNodes.size() == 0) {
            return null;
        }
        return lstNodes;
    }

    public List<RecommendPlayingItemNode> getPlayingItemNodes(long time) {
        if (this.mLstRecommendPlaying == null || this.mLstRecommendPlaying.size() == 0) {
            return null;
        }
        List<RecommendPlayingItemNode> lstNodes = new ArrayList();
        long relativeTime = (long) getRelativeTime(1000 * time);
        for (int i = 0; i < this.mLstRecommendPlaying.size(); i++) {
            RecommendPlayingItemNode temp = (RecommendPlayingItemNode) this.mLstRecommendPlaying.get(i);
            if (relativeTime > ((long) temp.startTime()) && relativeTime < ((long) temp.endTime())) {
                lstNodes.add(temp);
            }
        }
        if (lstNodes.size() == 0) {
            return null;
        }
        return lstNodes;
    }

    private int getRelativeTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return ((calendar.get(Calendar.HOUR_OF_DAY) * 60) * 60) + (calendar.get(Calendar.MINUTE) * 60);
    }

}
