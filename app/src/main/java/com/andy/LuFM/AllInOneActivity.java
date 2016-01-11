package com.andy.LuFM;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.Utils.ImageLoaderUtil;
import com.andy.LuFM.controller.ControllerManager;
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
import com.andy.LuFM.fragments.DiscoverFragment;
import com.andy.LuFM.fragments.DownloadFragment;
import com.andy.LuFM.fragments.MineFragment;
import com.andy.LuFM.listener.ChannelDetailClickListener;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.player.AudioPlaybackService;
import com.andy.LuFM.player.MiniPlayerFragment;
import com.andy.LuFM.view.SecondaryViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllInOneActivity extends AppCompatActivity implements IEventHandler, RadioGroup.OnCheckedChangeListener, ChannelDetailClickListener {
    private int mViewType = 1;
    public List<CategoryNode> categoryNodes;
    private RadioGroup radioGroup;
    private Fragment[] fragments;
    private SecondaryViewGroup secondaryContainer;
    public static final String CHANNEL_DETAIL_TAG = "channel_detail_tag";
    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String MINI_PLAYER_TAG = "mini_player_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Sync", "onCreate");
        initUtils();
        setContentView(R.layout.layout_splash);
        initDataOperation();
        initFragments();
        initData();
        initListener();

    }

    private void initListener() {
        ControllerManager.getInstance(this).RegisterListener(this);
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
        secondaryContainer = (SecondaryViewGroup) findViewById(R.id.second_view);
    }

    private void setMainPane() {
        if (isFinishing()) return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = fragments[mViewType];
        ft.replace(R.id.content, fragment, MAIN_CONTENT_TAG);
        ft.commit();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        Fragment miniPlayerFragment = new MiniPlayerFragment();
        ft1.replace(R.id.mini_play, miniPlayerFragment, MINI_PLAYER_TAG);
        ft1.commit();

    }

    private void getCategoryList() {
        if (this.categoryNodes == null) {
            restoreChildFromDB();
        }
        if (!(this.categoryNodes == null || this.categoryNodes.size() <= 0 || ((CategoryNode) this.categoryNodes.get(0)).getSectionId() == 0)) {
            CategoryNode radioNode = new CategoryNode();
            radioNode.setSectionId(9999);
            radioNode.setName("\u7535\u53f0");
            radioNode.setCategoryId(99998);
            this.categoryNodes.add(0, radioNode);
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
        if (secondaryContainer.getVisibility() == View.GONE) {
            //  secondaryContainer.removeAllViews();
            secondaryContainer.setVisibility(View.VISIBLE);
        }
        secondaryContainer.switchView(type, param);
       /* if (type.equalsIgnoreCase("channeldetail")) {
            ChannelFragment fragment = null;
            if (fragment == null) {
                fragment = new ChannelFragment();
            }
            fragment.setData(type, param);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, 0
                    , 0, R.anim.slide_out_to_right);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.channel_detail, fragment, CHANNEL_DETAIL_TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        } else if (type.equalsIgnoreCase("specialtopic")) {
            TopicFragment fragment = null;
            if (fragment == null) {
                fragment = new TopicFragment();
            }
            fragment.setData(type, param);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, 0
                    , 0, R.anim.slide_out_to_right);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.channel_detail, fragment, "topics");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
*/
    }


    @Override
    public void onBackPressed() {
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }*/
        if (secondaryContainer.getContainerCount() > 0) {
            boolean isRemoveCompleted = secondaryContainer.removeChild();
            if (isRemoveCompleted) {
                //   secondaryContainer.removeAllViews();
                secondaryContainer.setVisibility(View.GONE);
            }

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销监听器
        ControllerManager.getInstance(this).unRegisterListener();
        //如果没有播放任务，停止service
        AudioPlaybackService service = TestApplication.from().getService();
        if (service != null && !service.isPlayingMusic()) {
            service.stopPlayback();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }
}
