package com.andy.LuFM.view.channeldetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.LuFM.app.AllInOneActivity;
import com.andy.LuFM.R;
import com.andy.LuFM.app.PlayApplication;
import com.andy.LuFM.controller.BaseListController;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.event.PlayActionEvent;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.providers.ProgramNodesProvider;
import com.andy.LuFM.player.AudioPlaybackService;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelDetailView extends LinearLayout implements ChannelHelper.IDataChangeObserver, AdapterView.OnItemClickListener, View.OnClickListener {
    private ChannelNode channelNode;
    private ChannelDetailCoverView coverView;
    private ListView mListView;
    private BaseListController baseListController;
    private ProgramNodesProvider programNodesProvider;
    private Context context;
    List<ProgramNode> programs = new ArrayList();
    private Handler mHandler = new Handler();
    private ImageButton iv_back;
    private TextView title_label;

    public ChannelDetailView(Context context) {
        this(context, null);
    }

    public ChannelDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChannelDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.list_item_color));
        inflate(context, R.layout.layout_detail_toolbar, this);
        iv_back = (ImageButton) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        title_label = (TextView) findViewById(R.id.title_label);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        coverView = new ChannelDetailCoverView(context);
        addView(coverView, layoutParams);
        EventBus.getDefault().register(this);

    }

    public void update(String type, Object param) {
        ChannelNode temp = (ChannelNode) param;
        if (this.channelNode != temp) {
            this.channelNode = temp;
            this.coverView.update("setdata", param);
            title_label.setText(this.channelNode.title);
            ChannelHelper.getInstance().addObserver(this.channelNode.channelId, this);
            //init listview
            initListView();
            initData();

        }
    }

    private void initListView() {
        programNodesProvider = new ProgramNodesProvider(context) {
            @Override
            public Object getParam() {
                return channelNode;
            }
        };
        this.baseListController = new BaseListController(programNodesProvider) {
            @Override
            public void onLoadSuccess(Object object) {
                super.onLoadSuccess(object);
                String type = (String) object;
                if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE)) {//分页获取
                    setData(channelNode.getAllLstProgramNode());
                } else if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_RELOAD_PROGRAMS_SCHEDULE)) {//刷新
                    setData(channelNode.reloadAllLstProgramNode());
                }
                super.onLoadFinish(30);
            }

            @Override
            public void onRefresh() {
                super.onRefresh();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        baseListController.loadData(channelNode);
                    }
                }, 300);

            }

            @Override
            protected boolean setLoaderEnable() {
                return true;
            }
        };
        this.baseListController.setActivity(context);
        FrameLayout listLayout = new FrameLayout(context);
        this.baseListController.assumeView(listLayout);
        this.mListView = baseListController.mListView;
        this.mListView.setOnItemClickListener(this);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(listLayout, layoutParams);

    }


    private void initData() {

        if (this.channelNode != null) {
            if (this.mListView != null) {
                //  this.mListView.setSelection(0);
            }
            if (this.channelNode.hasEmptyProgramSchedule()) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        baseListController.loadData(channelNode);
                    }
                }, 300);
                // this.mCoverView.setButtonEnable(false);
                return;
            }
            setData(this.channelNode.getAllLstProgramNode());
        }

    }

    @Override
    public void onChannelNodeInfoUpdate(ChannelNode channelNode) {
        if (this.channelNode != null && this.channelNode.channelId == channelNode.channelId) {
            this.channelNode.updateAllInfo(channelNode);
            this.coverView.update("setData", this.channelNode);
            title_label.setText(this.channelNode.title);
        }
    }

    private boolean setData(List<ProgramNode> programNodes) {
        if (programNodes == null) {
            return false;
        }
        programs.clear();
        programs.addAll(programNodes);
        ((ProgramNodesProvider.MYAdapter) this.programNodesProvider.getAdapter()).setData(programs);
        AudioPlaybackService service = PlayApplication.from().getService();
        if (service == null || !service.isPlayingMusic()) {
            programNodesProvider.setSelectedItem(0);
            ((PlayApplication) PlayApplication.from()).getPlaybackKickstarter().initPlayback(context, programs, 0, false, true);
        } else if (service.isPlayingMusic()) {
            int playingChannelId = service.getCurrentSong().getId();
            if (channelNode.channelId == playingChannelId) {
                programNodesProvider.setSelectedItem(service.getCurrentSongIndex());

            }
            //  ((PlayApplication) PlayApplication.from()).getPlaybackKickstarter().getBuildCursorListener().onServiceCursorUpdated(programNodes);

        }
        return true;
    }

    public ChannelNode getChannelNode() {
        return this.channelNode;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        programNodesProvider.setSelectedItem(position);
        ((PlayApplication) PlayApplication.from()).getPlaybackKickstarter().initPlayback(context, programs, position, true, true);

    }

    @Override
    public void onClick(View v) {
        AllInOneActivity allInOneActivity = (AllInOneActivity) getContext();
        allInOneActivity.onBackPressed();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(PlayActionEvent actionEvent) {
        Intent intent = actionEvent.getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle.containsKey(PlayApplication.UPDATE_PAGER_POSTIION)) {
            AudioPlaybackService service = PlayApplication.from().getService();
            int playingChannelId = service.getCurrentSong().getId();
            if (channelNode.channelId == playingChannelId) {
                programNodesProvider.setSelectedItem(service.getCurrentSongIndex());

            }

        }
    }
}
