package com.andy.LuFM.data.ds;

import android.database.Cursor;

import com.andy.LuFM.LuFmApplication;
import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IDataRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.dbutil.DaoMaster;
import com.andy.LuFM.model.CategoryNode;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class CategoryNodeDs implements IDataOperation {
    private static CategoryNodeDs instance;
    private static DaoMaster.DevOpenHelper mHelper;

    private CategoryNodeDs() {
        mHelper = LuFmApplication.getInstance().getHelper();
    }

    public static synchronized CategoryNodeDs getInstance() {
        if (instance == null) {
            instance = new CategoryNodeDs();
        }
        return instance;
    }

    @Override
    public String dataRequestName() {
        return RequestType.GET_CATEGORY_LIST;
    }

    @Override
    public Result doCommand(DataCommand dataCommand, IDataRecvHandler iDataRecvHandler) {
        Map<String, Object> params = dataCommand.getParam();
        List<CategoryNode> lists = new ArrayList<>();
        int parentId = (int) params.get(Constants.PARENT_ID);
        Cursor cursor = mHelper.getReadableDatabase().rawQuery("select * from categoryNodes where parentId = " + parentId, null);
        Gson gson = new Gson();
        while (cursor.moveToNext()) {
            String nodeStr = cursor.getString(cursor.getColumnIndex("categoryNode"));
            CategoryNode categoryNode = gson.fromJson(nodeStr, CategoryNode.class);
            if (categoryNode != null) {
                lists.add(categoryNode);
            }
        }
        cursor.close();
        Result result = new Result();
        result.setData(lists);
        result.setSuccess(true);

        return result;
    }
}
