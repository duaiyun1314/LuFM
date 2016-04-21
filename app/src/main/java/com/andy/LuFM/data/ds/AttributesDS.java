package com.andy.LuFM.data.ds;

import android.database.Cursor;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.app.LuFmApplication;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.dbutil.DaoMaster;
import com.andy.LuFM.model.Attributes;
import com.andy.LuFM.model.ChannelNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglu on 15/11/20.
 */
public class AttributesDS implements IDataOperation {
    private static AttributesDS instance;
    private static DaoMaster.DevOpenHelper mHelper;

    private AttributesDS() {
        mHelper = LuFmApplication.getInstance().getCategoryAttrHelper();
    }

    public static synchronized AttributesDS getInstance() {
        if (instance == null) {
            instance = new AttributesDS();
        }
        return instance;
    }

    @Override
    public String dataRequestName() {
        return RequestType.DB_CATEGORY_ATTR;
    }

    @Override
    public Result doCommand(DataCommand dataCommand, IResultRecvHandler iDataRecvHandler) {
        String type = dataCommand.getType();
        Result result = new Result();
        Map<String, Object> param = dataCommand.getParam();
        if (type.equalsIgnoreCase(RequestType.GETDB_CATEGORY_ATTRIBUTES)) {
            List<Attributes> attributes = getListAttributes(param);
            if (attributes != null) {
                result.setSuccess(true);
                result.setData(attributes);
            } else {
                result.setSuccess(false);
            }
            return result;
        }
        return null;
    }

    private List<Attributes> getListAttributes(Map<String, Object> param) {
        List<Attributes> attributesList = new ArrayList<>();
        try {
            int catId = ((Integer) param.get("catid")).intValue();
            Cursor cursor = mHelper.getReadableDatabase().rawQuery("select * from categoryAttributes where catid = " + catId, null);
            Gson gson = new Gson();
            Type type = new TypeToken<Attributes>() {
            }.getType();
            Attributes attributes = null;
            while (cursor.moveToNext()) {
                try {

                    attributes = gson.fromJson(cursor.getString(cursor.getColumnIndex("attrs")), type);
                    attributesList.add(attributes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            return attributesList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
