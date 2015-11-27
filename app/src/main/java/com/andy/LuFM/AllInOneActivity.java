package com.andy.LuFM;

import android.app.FragmentManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.Utils.ImageLoaderUtil;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.DataOfflineManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.data.ds.CategoryNodeDs;
import com.andy.LuFM.data.ds.ChannelNodeDS;
import com.andy.LuFM.data.ds.NetDs;
import com.andy.LuFM.data.ds.ProgramNodeDs;
import com.andy.LuFM.event.EventType;
import com.andy.LuFM.event.IEventHandler;
import com.andy.LuFM.fragments.ChannelFragment;
import com.andy.LuFM.fragments.DiscoverFragment;
import com.andy.LuFM.fragments.DownloadFragment;
import com.andy.LuFM.fragments.MineFragment;
import com.andy.LuFM.listener.ChannelDetailClickListener;
import com.andy.LuFM.model.CategoryNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllInOneActivity extends AppCompatActivity implements IEventHandler, RadioGroup.OnCheckedChangeListener, ChannelDetailClickListener {
    private int mViewType = 1;
    public List<CategoryNode> categoryNodes;
    private RadioGroup radioGroup;
    private Fragment[] fragments;
    public static final String CHANNEL_DETAIL_TAG = "channel_detail_tag";
    public static final String MAIN_CONTENT_TAG = "main_content_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUtils();
        setContentView(R.layout.layout_splash);
        initDataOperation();
        initFragments();
        initData();

    }

    private void initUtils() {
        ImageLoaderUtil.initImageLoader(getApplicationContext());
    }

    private void initFragments() {
        fragments = new Fragment[3];
        fragments[0] = new MineFragment();
        fragments[1] = new DiscoverFragment();
        fragments[2] = new DownloadFragment();
    }

    private void initDataOperation() {
        DataManager.getInstance().addRequests(CategoryNodeDs.getInstance());
        DataManager.getInstance().addRequests(NetDs.getInstance());
        DataManager.getInstance().addRequests(ChannelNodeDS.getInstance());
        DataManager.getInstance().addRequests(ProgramNodeDs.getInstance());
    }

    private void initData() {
        DataOfflineManager.loadOfflineData(this, this);
        InfoManager.getInstance().loadDataCenterList();
    }


    @Override
    public void OnEvent(Object target, EventType type, Object params) {
        switch (type) {
            case LOAD_OFFLINE_DATA_SUCCEED:
                getCategoryList();
                break;
        }

    }

    private void showMainView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.layout_main);
                initView();
                setMainPane();
            }
        }, 3000);
    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);
        ((RadioButton) radioGroup.getChildAt(mViewType)).setChecked(true);
    }

    private void setMainPane() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = fragments[mViewType];
        ft.replace(R.id.content, fragment, MAIN_CONTENT_TAG);
        ft.commit();

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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rd_mine:
                mViewType = 0;
                break;
            case R.id.rd_discover:
                mViewType = 1;
                break;
            case R.id.rd_download:
                mViewType = 2;
                break;
        }
        setMainPane();
    }

    @Override
    public void onChannelSelected(String type, Object param) {
        Log.i("Sync", "onChannelSelected:" + type);
        ChannelFragment fragment = (ChannelFragment) getFragmentManager().findFragmentByTag(CHANNEL_DETAIL_TAG);
        if (true) {
            fragment = new ChannelFragment();
            fragment.setData(type, param);
            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left
                    , R.animator.slide_in_from_left, R.animator.slide_out_to_right);
            fragmentTransaction.replace(R.id.detail, fragment, CHANNEL_DETAIL_TAG);
            android.app.Fragment fragment1 = getFragmentManager().findFragmentById(R.id.content);
            if (fragment1 != null) {
                fragmentTransaction.hide(fragment1);
            }else{

            }
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }


    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
