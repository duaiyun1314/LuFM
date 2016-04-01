package com.andy.LuFM.helper;

import android.util.Log;

import com.andy.LuFM.Utils.NetKit;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.ProgramSchedule;
import com.andy.LuFM.model.ProgramScheduleList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.util.Ln;

/**
 * Created by wanglu on 15/11/24.
 */
public class ProgramHelper extends Node {
    static final String TAG = "Sync";
    private static ProgramHelper _instance = null;
    private Map<Integer, ProgramScheduleList[]> mapProgramNodes;
    public Map<Integer, Boolean> mapUpdatePrograms;
    public transient ProgramNode programNodeTemp;

    private ProgramHelper() {
        this.mapProgramNodes = new HashMap();
        this.mapUpdatePrograms = new HashMap();
        this.nodeName = "programhelper";
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_VIRTUAL_PROGRAMS_SCHEDULE);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_LIVE_PROGRAMS_SCHEDULE);

    }

    public static ProgramHelper getInstance() {
        if (_instance == null) {
            _instance = new ProgramHelper();
        }
        return _instance;
    }

    public ProgramScheduleList getProgramSchedule(int channelId, int type, boolean readCache) {
        //int order = InfoManager.getInstance().root().getProgramListOrder(channelId);
        int order = 0;//正序
        //  Ln.d( String.format("sym:\u83b7\u53d6\u8282\u76ee\u5355id=%d\uff0corder=%d", new Object[]{Integer.valueOf(channelId), Integer.valueOf(order)}));
        ProgramScheduleList[] values = (ProgramScheduleList[]) this.mapProgramNodes.get(Integer.valueOf(channelId));
        ProgramScheduleList node = null;
        if (values == null && type == 1 && ((allowReadCache(channelId) || readCache) && restoreProgramSchedule(channelId, order))) {
            values = (ProgramScheduleList[]) this.mapProgramNodes.get(Integer.valueOf(channelId));
            node = values[order];
        }
        if (values != null) {
            return values[order];
        }
        return node;
    }

    private boolean allowReadCache(int channelId) {
        if (NetKit.isNetworkConnected()) {
            return false;
        }
        return true;
    }


    private boolean restoreProgramSchedule(int channelId, int order) {
        Result result = null;
        Ln.d( String.format("sym:\u5c1d\u8bd5\u52a0\u8f7d\u8282\u76ee\u5355\u6570\u636e\u5e93\u7f13\u5b58\uff0cid=%d\uff0corder=%d", new Object[]{Integer.valueOf(channelId), Integer.valueOf(order)}));
        ProgramScheduleList[] values = (ProgramScheduleList[]) this.mapProgramNodes.get(Integer.valueOf(channelId));
        if (values == null) {
            values = new ProgramScheduleList[2];
            this.mapProgramNodes.put(Integer.valueOf(channelId), values);
        }
        Map<String, Object> param = new HashMap();
        param.put("id", Integer.valueOf(channelId));
        if (order == 0) {
            result = DataManager.getInstance().getData("ProgramNodeDs", null, new DataCommand(RequestType.GETDB_PROGRAM_NODE, param));
        } else {
            // result = DataManager.getInstance().getData("ProgramNodeDs", null, new DataCommand(RequestType.GETDB_PROGRAM_NODE_REV, param));
        }
        List<ProgramNode> res = null;
        if (result != null && result.isSuccess()) {
            res = (List) result.getData();
        }
        if (res == null || res.size() == 0) {
            return false;
        }
        ProgramScheduleList node = new ProgramScheduleList(1);
        node.channelId = ((ProgramNode) res.get(0)).channelId;
        ProgramSchedule ps = new ProgramSchedule();
        ps.mLstProgramNodes = res;
        ps.dayOfWeek = 0;
        node.mLstProgramsScheduleNodes.add(ps);
        values[order] = node;
        Node prevNode = null;
        for (int i = 0; i < res.size(); i++) {
            if (prevNode != null) {
                prevNode.nextSibling = (Node) res.get(i);
                ((ProgramNode) res.get(i)).prevSibling = prevNode;
            }
            prevNode = (Node) res.get(i);
        }
        return true;
    }

    @Override
    public void onNodeUpdated(Object obj, Map<String, String> map, String type) {
        super.onNodeUpdated(obj, map, type);
        Node node = (Node) obj;
        if (node != null) {
            int order;
            if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_VIRTUAL_PROGRAMS_SCHEDULE)) {
                Ln.d( "sym:加载更多专辑节目单成功");
                boolean ret = addProgramSchedule((ProgramScheduleList) node, map);
                if (node != null && ret) {
                    order = 0;
                    if (map != null) {
                        order = Integer.valueOf((String) map.get("order")).intValue();
                    }
                    udpateToDB(((ProgramScheduleList) node).channelId, order);
                }
            } else if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_LIVE_PROGRAMS_SCHEDULE)) {
                Ln.d( "sym:获取到电台节目单成功");
                int channelId = 0;
                if (map != null) {
                    channelId = Integer.valueOf((String) map.get("id")).intValue();
                }
                ProgramScheduleList psl = (ProgramScheduleList) node;
                psl.channelId = channelId;
                setProgramSchedule(psl.channelId, 0, node);
            } else if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_RELOAD_VIRTUAL_PROGRAMS_SCHEDULE)) {
                Ln.d( "sym:重新获取专辑节目单成功");
                addProgramSchedule((ProgramScheduleList) node, map);
                if (node != null && node.nodeName.equalsIgnoreCase("programschedulelist")) {
                    order = 0;
                    if (map != null) {
                        order = Integer.valueOf((String) map.get("order")).intValue();
                    }
                    udpateToDB(((ProgramScheduleList) node).channelId, order);
                }
            }
        }

    }

    private boolean addProgramSchedule(ProgramScheduleList node, Map<String, String> mapParam) {
        if (node == null) {
            return false;
        }
        if (mapParam == null) {
            return false;
        }
        String id = (String) mapParam.get("id");
        if (id == null) {
            return false;
        }
        int channelId = Integer.valueOf(id).intValue();
        node.setChannelId(channelId);
        int order = Integer.valueOf((String) mapParam.get("order")).intValue();
        int page = Integer.valueOf((String) mapParam.get("page")).intValue();
        int size = Integer.valueOf((String) mapParam.get("pagesize")).intValue();
        ProgramScheduleList[] values = (ProgramScheduleList[]) this.mapProgramNodes.get(Integer.valueOf(channelId));
        Ln.d( "sym:ProgramHelper更新缓存");
        if (values == null || values[order] == null || values[order].type != 1) {
            setProgramSchedule(channelId, order, node);
        } else {
            List<ProgramNode> lstAddPrograms = node.getLstProgramNode(0);
            if (lstAddPrograms == null || lstAddPrograms.size() == 0) {
                return false;
            }
            int i;
            for (i = 0; i < lstAddPrograms.size(); i++) {
                ((ProgramNode) lstAddPrograms.get(i)).channelId = channelId;
            }
            List<ProgramNode> oldList = values[order].getLstProgramNode(0);
            int oldPage = oldList.size() / size;
            if (oldList.size() == 0 || oldPage == 0) {
                return setProgramSchedule(channelId, order, node);
            }
            int index;
            if (page == 1) {
                Ln.d( "sym:下拉刷新专辑id=" + channelId);
                ProgramNode oldFirst = (ProgramNode) oldList.get(0);
                index = -1;
                for (i = 0; i < lstAddPrograms.size(); i++) {
                    if (((ProgramNode) lstAddPrograms.get(i)).uniqueId == oldFirst.uniqueId) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    return setProgramSchedule(channelId, order, node);
                }
                Ln.d( "sym:更新" + index + "条");
                for (i = 0; i < lstAddPrograms.size(); i++) {
                    int j = 0;
                    boolean repeat = false;
                    while (j < oldList.size() && j < lstAddPrograms.size()) {
                        if (((ProgramNode) lstAddPrograms.get(i)).uniqueId == ((ProgramNode) oldList.get(j)).uniqueId) {
                            repeat = true;
                            break;
                        }
                        j++;
                    }
                    if (!repeat) {
                        ProgramNode addNode;
                        ProgramNode oldNode;
                        if (i < oldList.size()) {
                            addNode = (ProgramNode) lstAddPrograms.get(i);
                            oldNode = (ProgramNode) oldList.get(i);
                            addNode.nextSibling = oldNode;
                            addNode.prevSibling = oldNode.prevSibling;
                            if (oldNode.prevSibling != null) {
                                oldNode.prevSibling.nextSibling = addNode;
                            }
                            oldNode.prevSibling = addNode;
                            oldList.add(i, addNode);
                        } else {
                            addNode = (ProgramNode) lstAddPrograms.get(i);
                            oldNode = (ProgramNode) oldList.get(oldList.size() - 1);
                            addNode.nextSibling = null;
                            oldNode.nextSibling = addNode;
                            addNode.prevSibling = oldNode;
                            oldList.add(oldNode);
                        }
                    }
                }
            } else if (page > oldPage) {
                Ln.d( String.format("sym:加载更多,id=%d,page=%d,order=%d", new Object[]{Integer.valueOf(channelId), Integer.valueOf(page), Integer.valueOf(order)}));
                ProgramNode oldLast = (ProgramNode) oldList.get(oldList.size() - 1);
                index = -1;
                if (oldLast != null) {
                    for (i = 0; i < lstAddPrograms.size(); i++) {
                        if (((ProgramNode) lstAddPrograms.get(i)).uniqueId == oldLast.uniqueId) {
                            index = i;
                            break;
                        }
                    }
                }
                if (index == -1) {
                    Ln.d( "sym:\u8282\u76ee\u5355\u65e0\u91cd\u5408");
                    Node prevNode = (Node) lstAddPrograms.get(0);
                    prevNode.prevSibling = (Node) oldList.get(oldList.size() - 1);
                    ((ProgramNode) oldList.get(oldList.size() - 1)).nextSibling = prevNode;
                    oldList.addAll(lstAddPrograms);
                } else {
                    int len = index + 1;
                    Ln.d( "sym:\u8282\u76ee\u5355\u91cd\u5408\u957f\u5ea6" + len);
                    if (len >= lstAddPrograms.size()) {
                        return false;
                    }
                    ProgramNode node1 = (ProgramNode) oldList.get(oldList.size() - 1);
                    ProgramNode node2 = (ProgramNode) lstAddPrograms.get(len);
                    node1.nextSibling = node2;
                    node2.prevSibling = node1;
                    for (int p = len; p < lstAddPrograms.size(); p++) {
                        oldList.add((ProgramNode) lstAddPrograms.get(p));
                    }
                }
            } else {
                setProgramSchedule(channelId, order, node);
            }
        }
        return true;
    }

    private boolean setProgramSchedule(int channelId, int order, Node node) {
        Log.i(TAG, "setProgramSchedule");
        if (node == null) {
            return false;
        }
        ProgramScheduleList[] values = (ProgramScheduleList[]) this.mapProgramNodes.get(Integer.valueOf(channelId));
        if (values == null) {
            values = new ProgramScheduleList[2];
            this.mapProgramNodes.put(Integer.valueOf(channelId), values);
        }
        ProgramScheduleList psl;
        if (node.nodeName.equalsIgnoreCase("program")) {
            psl = values[order];
            if (psl == null && ((ProgramNode) node).channelType == 1) {
                psl = new ProgramScheduleList(1);
            }
            if (psl != null && psl.type == 1) {
                psl.addProgramNode((ProgramNode) node);
            }
        } else if (node.nodeName.equalsIgnoreCase("programschedulelist")) {
            psl = (ProgramScheduleList) node;
           /* ChannelNode temp = InfoManager.getInstance().root().getCurrentPlayingChannelNode();
            if (temp != null && temp.channelId == channelId) {
                Ln.d( "sym:\u8bbe\u7f6e\u5f53\u524d\u64ad\u653e\u7684\u8282\u76ee\u5355");
                temp.setProgramScheduleList((ProgramScheduleList) node);
            }*/
            values[order] = psl;
        }
        return true;
    }

    public void udpateToDB(ProgramScheduleList node) {
        int channelId = node.channelId;
        udpateToDB(channelId, 0);
    }

    private void udpateToDB(int channelId, int order) {
        ProgramScheduleList[] values = (ProgramScheduleList[]) this.mapProgramNodes.get(Integer.valueOf(channelId));
        if (values != null && values[order] != null && values[order].channelId != 0 && values[order].type != 0) {
            values[order].updateToDB(order);
        }
    }


}
