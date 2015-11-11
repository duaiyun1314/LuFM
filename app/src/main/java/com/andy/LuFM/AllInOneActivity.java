package com.andy.LuFM;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.DataOfflineManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.data.ds.CategoryNodeDs;
import com.andy.LuFM.event.EventType;
import com.andy.LuFM.event.IEventHandler;
import com.andy.LuFM.model.CategoryNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllInOneActivity extends AppCompatActivity implements IEventHandler {
    private List<CategoryNode> categoryNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        initDataOperation();
        initData();
    }

    private void initDataOperation() {
        DataManager.getInstance().addRequests(CategoryNodeDs.getInstance());
    }

    private void initData() {
        DataOfflineManager.loadOfflineData(this, this);
        getCategoryList();
    }


    @Override
    public void OnEvent(Object target, EventType type, Object params) {
        switch (type) {
            case LOAD_OFFLINE_DATA_SUCCEED:
                //  showMainView();
                getCategoryList();
                break;
        }

    }

    private void showMainView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.layout_main);
            }
        }, 3000);
    }

    private void getCategoryList() {
        if (this.categoryNodes == null) {
            restoreChildFromDB();
        }
        if (!(this.categoryNodes == null || this.categoryNodes.size() <= 0 || ((CategoryNode) this.categoryNodes.get(0)).getSectionId() == 0)) {
            CategoryNode node = new CategoryNode();
            node.setSectionId(0);
            node.setCategoryId(99999);
            node.setName("\u7cbe\u9009");
            this.categoryNodes.add(0, node);
        }

        if (categoryNodes != null && categoryNodes.size() > 0) {
            showMainView();
        }
    }

    private void restoreChildFromDB() {
        Map<String, Object> param = new HashMap();
        param.put(Constants.PARENT_ID, Integer.valueOf(0));
        Result result = DataManager.getInstance().getData(RequestType.GET_CATEGORY_LIST, null, new DataCommand(null, param));
        List<CategoryNode> res = null;
        if (result.isSuccess()) {
            res = (List) result.getData();
        }
        if (!(res == null || res.size() <= 0)) {
            categoryNodes = res;
        }

    }
}
