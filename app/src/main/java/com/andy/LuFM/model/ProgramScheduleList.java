package com.andy.LuFM.model;

import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.RequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy.Wang on 2015/11/25.
 */
public class ProgramScheduleList extends Node {
    public int channelId;
    private transient boolean hasUpdated;
    public List<ProgramSchedule> mLstProgramsScheduleNodes;
    public int type;

    public ProgramScheduleList(int pType) {
        this.mLstProgramsScheduleNodes = new ArrayList();
        this.hasUpdated = false;
        this.nodeName = "programschedulelist";
        this.type = pType;
    }

    public List<ProgramNode> getLstProgramNode(int dayofweek) {
        for (int i = 0; i < this.mLstProgramsScheduleNodes.size(); i++) {
            if (((ProgramSchedule) this.mLstProgramsScheduleNodes.get(i)).dayOfWeek == dayofweek) {
                return ((ProgramSchedule) this.mLstProgramsScheduleNodes.get(i)).mLstProgramNodes;
            }
        }
        return null;
    }

    public void setChannelId(int cid) {
        this.channelId = cid;
        for (int i = 0; i < this.mLstProgramsScheduleNodes.size(); i++) {
            for (int j = 0; j < ((ProgramSchedule) this.mLstProgramsScheduleNodes.get(i)).mLstProgramNodes.size(); j++) {
                ((ProgramNode) ((ProgramSchedule) this.mLstProgramsScheduleNodes.get(i)).mLstProgramNodes.get(j)).channelId = cid;
            }
        }
    }

    public boolean addProgramNode(ProgramNode node) {
        if (node == null) {
            return false;
        }
        if (this.mLstProgramsScheduleNodes.size() == 0) {
            this.mLstProgramsScheduleNodes.add(new ProgramSchedule());
        }
        return ((ProgramSchedule) this.mLstProgramsScheduleNodes.get(0)).addProgramNode(node);
    }

    public void updateToDB(int order) {
        if (this.type != 0 && !this.hasUpdated) {
            List<ProgramNode> lstNodes = getLstProgramNode(0);
            if (lstNodes != null && lstNodes.size() != 0) {
                this.hasUpdated = true;
                Map<String, Object> param = new HashMap();
                param.put("id", Integer.valueOf(this.channelId));
                param.put("nodes", lstNodes);
                param.put("size", Integer.valueOf(lstNodes.size()));
                if (order == 0) {
                    DataManager.getInstance().getData("ProgramNodeDs", null, new DataCommand(RequestType.UPDATEDB_PROGRAM_NODE, param));
                } else {
                    DataManager.getInstance().getData("ProgramNodeDs", null, new DataCommand(RequestType.UPDATEDB_PROGRAM_NODE_REV, param));
                }
            }
        }
    }

}
