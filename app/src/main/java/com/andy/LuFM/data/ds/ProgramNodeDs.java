package com.andy.LuFM.data.ds;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.andy.LuFM.app.LuFmApplication;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.dbutil.DaoMaster;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy.Wang on 2015/11/25.
 */
public class ProgramNodeDs implements IDataOperation {
    private static ProgramNodeDs instance;
    private static DaoMaster.DevOpenHelper mHelper;

    private ProgramNodeDs() {
        mHelper = LuFmApplication.getInstance().getProgramNodesHelperHelper();
    }

    public static synchronized ProgramNodeDs getInstance() {
        if (instance == null) {
            instance = new ProgramNodeDs();
        }
        return instance;
    }

    @Override
    public String dataRequestName() {
        return "ProgramNodeDs";
    }

    @Override
    public Result doCommand(DataCommand dataCommand, IResultRecvHandler iDataRecvHandler) {
        Result result = new Result();
        String type = dataCommand.getType();
        Map<String, Object> param = dataCommand.getParam();
        if (type.equalsIgnoreCase(RequestType.GETDB_PROGRAM_NODE)) {
            List<Node> list = acquireProgramNodes(param);
            if (list != null && list.size() > 0) {
                result.setSuccess(true);
                result.setData(list);
                return result;
            }
        } else if (type.equalsIgnoreCase(RequestType.UPDATEDB_PROGRAM_NODE)) {
            updateProgramNodes(param);
        } else if (type.equalsIgnoreCase(RequestType.UPDATEDB_PROGRAM_NODE_REV)) {

        }
        return null;
    }

    private List<Node> acquireProgramNodes(Map<String, Object> param) {
        List<Node> lstProgram = null;
        try {
            String sql = "select programNode from programNodes where cid = '" + ((Integer) param.get("id")) + "'";
            List<Node> lstProgram2 = new ArrayList();
            try {
                Cursor cursor = mHelper.getReadableDatabase().rawQuery(sql, null);
                Node prevProgramNode = null;
                Gson gson = new Gson();
                while (cursor.moveToNext()) {
                    try {
                        Node program = (Node) gson.fromJson(cursor.getString(cursor.getColumnIndex("programNode")), ProgramNode.class);
                        if (!(prevProgramNode == null || program == null)) {
                            program.prevSibling = prevProgramNode;
                            prevProgramNode.nextSibling = program;
                        }
                        prevProgramNode = program;
                        lstProgram2.add(program);
                    } catch (Exception e) {
                    }
                }
                cursor.close();
                lstProgram = lstProgram2;
                return lstProgram2;
            } catch (Exception e2) {
                lstProgram = lstProgram2;
                return lstProgram;
            }
        } catch (Exception e3) {
            return lstProgram;
        }
    }

    private boolean updateProgramNodes(Map<String, Object> param) {
        List<Node> lstProgramNode = (List) param.get("nodes");
        int cid = ((Integer) param.get("id")).intValue();
        int size = ((Integer) param.get("size")).intValue();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        try {
            db.beginTransaction();
            if (Build.VERSION.SDK_INT >= 11) {
                long rowCount = DatabaseUtils.queryNumEntries(db, "programNodes");
                //Log.d(TAG, "sym:\u6570\u636e\u5e93\u7f13\u5b58\u8282\u76ee\u5355\u6570\u91cf:" + rowCount);
                if (rowCount > 1000) {
                    String sql = "select distinct cid from programNodes";
                    Cursor cursor = db.query(true, "programNodes", new String[]{"cid"}, null, null, null, null, null, null);
                    List<String> cidLst = new ArrayList();
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            cidLst.add(cursor.getString(0));
                        }
                    }
                    for (String id : cidLst) {
                        if (DatabaseUtils.queryNumEntries(db, "programNodes", "cid=?", new String[]{id}) < 300) {
                            //Log.d(TAG, String.format("sym:\u6570\u636e\u5e93\u5220\u9664id\u4e3a%s\u7684\u7f13\u5b58", new Object[]{id}));
                            db.delete("programNodes", "cid=?", new String[]{id});
                            break;
                        }
                    }
                }
            }
            db.execSQL("delete from programNodes" + " where cid = '" + cid + "'");
            Gson gson = new Gson();
            int i = 0;
            while (i < lstProgramNode.size() && i < size) {
                ProgramNode node = (ProgramNode) lstProgramNode.get(i);
                String nodeJson = gson.toJson(node);
                db.execSQL("insert into programNodes(cid,pid,dw,programNode) values(?, ?, ?, ?)", new Object[]{Integer.valueOf(cid), Integer.valueOf(((ProgramNode) node).uniqueId), Integer.valueOf(0), nodeJson});
                i++;
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
