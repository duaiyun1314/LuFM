package com.andy.LuFM.view.channeldetail;

import android.content.Context;
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

import com.andy.LuFM.AllInOneActivity;
import com.andy.LuFM.R;
import com.andy.LuFM.TestApplication;
import com.andy.LuFM.controller.BaseListController;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.providers.ProgramNodesProvider;
import com.andy.LuFM.player.AudioPlaybackService;

import java.util.ArrayList;
import java.util.List;


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

    }

    public void update(String type, Object param) {
        Log.i("Sync", "update");
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
                if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE)) {
                    setData(channelNode.getAllLstProgramNode());
                } else if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_RELOAD_PROGRAMS_SCHEDULE)) {
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
            //  dispatchActionEvent("resetNavi", null);
        }
    }

    private boolean setData(List<ProgramNode> programNodes) {
        if (programNodes == null) {
            return false;
        }
      /*  List<PlayedMetaData> history = PlayedMetaInfo.getInstance().getPlayedMetadata();
        boolean userCancel = this.mChannelNode != null && userCancelChannels.contains(Integer.valueOf(this.mChannelNode.channelId));
        boolean isRecommend = ControllerManager.getInstance().getChannelSource() == 1;
        this.mRecentIncompleteProgram = null;
        if (!(userCancel || isRecommend || history == null || history.size() <= 0)) {
            for (PlayedMetaData item : history) {
                if (this.mChannelNode.channelId == item.channelId && item.position > 5 && item.position < item.duration - 5) {
                    if (this.mRecentIncompleteProgram == null || item.playedTime > this.mRecentIncompleteProgram.playedTime) {
                        this.mRecentIncompleteProgram = item;
                    }
                }
            }
        }
        this.mRecentNode = null;
        if (this.mRecentIncompleteProgram != null) {
            for (PlayHistoryNode item2 : InfoManager.getInstance().root().mPersonalCenterNode.playHistoryNode.getPlayHistoryNodes()) {
                if (((ProgramNode) item2.playNode).id == this.mRecentIncompleteProgram.programId) {
                    this.mRecentNode = item2;
                    break;
                }
            }
        }*/

        int index = -1;
     /*   if (programNodes.size() > 0) {
            sendProgramsShowLog(programNodes, this.channelNode);
            Node temp = InfoManager.getInstance().root().getCurrentPlayingNode();
            if (temp != null && temp.nodeName.equalsIgnoreCase("program")) {
                int i = 0;
                while (i < programNodes.size()) {
                    if (((ProgramNode) temp).id == ((ProgramNode) programNodes.get(i)).id) {
                        index = i;
                        if (this.mRecentNode != null && ((ProgramNode) temp).id == ((ProgramNode) this.mRecentNode.playNode).id) {
                            this.mRecentNode = null;
                        }
                        if (temp.prevSibling == null && temp.nextSibling == null) {
                            temp.prevSibling = ((ProgramNode) programNodes.get(i)).prevSibling;
                            temp.nextSibling = ((ProgramNode) programNodes.get(i)).nextSibling;
                        }
                    } else {
                        i++;
                    }
                }
            }
            handleAutoPlay(this.mChannelNode);
        }*/
       /* if (this.mRecentNode != null) {
            QTMSGManage.getInstance().sendStatistcsMessage("resumerecent_display");
            programs.add(this.mRecentNode);
        }*/
        programs.clear();
        programs.addAll(programNodes);
        ((ProgramNodesProvider.MYAdapter) this.programNodesProvider.getAdapter()).setData(programs);
        //  PlayerAgent.getInstance().play(programNodes.get(0));
        AudioPlaybackService service = TestApplication.from().getService();
        if (service == null || !service.isPlayingMusic()) {
            programNodesProvider.setSelectedItem(0);
            ((TestApplication) TestApplication.from()).getPlaybackKickstarter().initPlayback(context, programs, 0, false, true);
        } else if (service.isPlayingMusic()) {
            int playingChannelId = service.getCurrentSong().getId();
            if (channelNode.channelId == playingChannelId) {
                programNodesProvider.setSelectedItem(service.getCurrentSongIndex());

            }
        }
        //  this.mAdapter.setData(ListUtils.convertToObjectList(programs));
       /* if (!(!this.mFirstTime || index == -1 || this.mListView == null)) {
            this.mFirstTime = false;
            this.mListView.setSelection(index);
        }*/
        return true;
    }

    public ChannelNode getChannelNode() {
        return this.channelNode;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        programNodesProvider.setSelectedItem(position);
        ((TestApplication) TestApplication.from()).getPlaybackKickstarter().initPlayback(context, programs, position, false, true);

    }

    @Override
    public void onClick(View v) {
        AllInOneActivity allInOneActivity = (AllInOneActivity) getContext();
        allInOneActivity.onBackPressed();
    }
}
