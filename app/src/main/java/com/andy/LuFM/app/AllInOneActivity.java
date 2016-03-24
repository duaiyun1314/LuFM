package com.andy.LuFM.app;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.andy.LuFM.R;
import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.Utils.ImageLoaderUtil;
import com.andy.LuFM.Utils.PrefKit;
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
import com.andy.LuFM.event.SwitchContentEvent;
import com.andy.LuFM.fragments.CategoryDetailFragment;
import com.andy.LuFM.fragments.ChannelDetailFragment;
import com.andy.LuFM.fragments.DiscoverFragment;
import com.andy.LuFM.fragments.DownloadFragment;
import com.andy.LuFM.fragments.MineFragment;
import com.andy.LuFM.fragments.SpecialTopicFragment;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.player.AudioPlaybackService;
import com.andy.LuFM.fragments.MiniPlayerFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllInOneActivity extends BaseActivity implements IEventHandler, RadioGroup.OnCheckedChangeListener {
    private int mViewType = 1;
    public List<CategoryNode> categoryNodes;
    private RadioGroup radioGroup;
    private Fragment[] fragments;
    private Fragment mCurrentContent;
    public static final String CHANNEL_DETAIL_TAG = "channel_detail_tag";
    public static final String SPECIAL_TOPIC_TAG = "special_topic_tag";
    public static final String CATEGORY_DETAIL_TAG = "category_detail_tag";
    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String MINI_PLAYER_TAG = "mini_player_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUtils();
        setWindowFullScreen(true);
        setContentView(R.layout.layout_splash);
        initDataOperation();
        initFragments();
        initData();
        initListener();

    }

    private void initListener() {
    }

    private void initUtils() {
        //初始化图片加载工具
        ImageLoaderUtil.initImageLoader(getApplicationContext());
    }

    /**
     * 初始化fragment
     */
    private void initFragments() {
        fragments = new Fragment[3];
        fragments[0] = new MineFragment();
        fragments[1] = new DiscoverFragment();
        fragments[2] = new DownloadFragment();
    }

    /**
     * 初始化data操作源，不同的操作类型（根据request name）会被分配到相对应的操作类中处理
     */
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
                setWindowFullScreen(false);
                setContentView(R.layout.layout_main);
                initView();
                setMainPane();
            }
        }, 4000);
    }


    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);
        ((RadioButton) radioGroup.getChildAt(mViewType)).setChecked(true);
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
            //showADAndMainView();
            InfoManager.getInstance().loadAdvertisement();
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


    /**
     * 切换fragment的事件回调
     *
     * @param event 切换fragment的事件
     */
    public void onEventMainThread(SwitchContentEvent event) {
        String type = event.type;
        Object param = event.params;
        if (type.equalsIgnoreCase(SwitchContentEvent.SWITCH_TYPE_CHANNEL_DETAIL)) {
            ChannelDetailFragment channelDetailFragment = new ChannelDetailFragment();
            channelDetailFragment.update(type, param);
            switchContent(channelDetailFragment, CHANNEL_DETAIL_TAG);
        } else if (type.equalsIgnoreCase(SwitchContentEvent.SWITCH_TYPE_SPECIAL_TOPIC)) {
            SpecialTopicFragment specialTopicFragment = new SpecialTopicFragment();
            specialTopicFragment.update(type, param);
            switchContent(specialTopicFragment, SPECIAL_TOPIC_TAG);
        } else if (type.equalsIgnoreCase(SwitchContentEvent.SWITCH_TYPE_CATEGORY_DETAIL)) {
            CategoryDetailFragment categoryDetailFragment = new CategoryDetailFragment();
            categoryDetailFragment.update(type, param);
            switchContent(categoryDetailFragment, CATEGORY_DETAIL_TAG);
        }
    }

    public void onEventMainThread(String url) {
        if (url != null) {
            setContentView(R.layout.layout_ad);
            ImageView ad_img = (ImageView) findViewById(R.id.ad_img);
            ImageLoader.getInstance().displayImage(url, ad_img, ((PlayApplication) getApplication()).getDisplayImageOptions());
            ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    PrefKit.writeString(PlayApplication.from(), Constants.PREF_AD_ADDRESS, imageUri);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
        showMainView();
    }

    /**
     * fragment 切换
     *
     * @param to
     */
    public void switchContent(Fragment to, String tag) {
        if (mCurrentContent == null) {
            mCurrentContent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_right);
            transaction.add(R.id.second_view, to, tag);
            transaction.addToBackStack(null);
            transaction.commit();
            return;
        }
        if (mCurrentContent != to) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_right);
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.add(R.id.second_view, to, tag);
            } else {
                transaction.show(to);
            }
            transaction.addToBackStack(null);
            transaction.commit();
            mCurrentContent = to;
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //如果没有播放任务，停止service
        AudioPlaybackService service = PlayApplication.from().getService();
        if (service != null && !service.isPlayingMusic()) {
            service.stopPlayback();
        }
    }

    public void setWindowFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
