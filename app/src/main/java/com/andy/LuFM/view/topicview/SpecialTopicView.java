package com.andy.LuFM.view.topicview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.andy.LuFM.controller.ChannelListController;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.providers.TopicChannelsProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy.Wang on 2015/12/3.
 */
public class SpecialTopicView extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    private SpecialTopicNode topicNode;
    private ListView mListView;
    private Context context;
    List<ChannelNode> channelNodes = new ArrayList();
    private ChannelListController baseListController;
    private TopicChannelsProvider channelsProvider;
    private Handler mHandler = new Handler();
    private ImageButton iv_back;
    private TextView title_label;

    public SpecialTopicView(Context context) {
        this(context, null);
    }

    public SpecialTopicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialTopicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.list_item_color));
        inflate(context, R.layout.layout_detail_toolbar, this);
        iv_back = (ImageButton) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        title_label = (TextView) findViewById(R.id.title_label);
    }

    public void update(String type, Object param) {
        Log.i("Sync", "update");
        SpecialTopicNode temp = (SpecialTopicNode) param;
        if (this.topicNode != temp) {
            this.topicNode = temp;
            //init listview
            initListView();
            initData();

        }
    }

    private void initData() {

        if (this.topicNode != null) {
            List<ChannelNode> list = ChannelHelper.getInstance().getLstChannelsByKey(this.topicNode.getKey());
            if (list == null || list.size() <= 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        baseListController.loadData(topicNode);
                    }
                }, 300);
                return;
            }
            setData(list);
        }
    }

    private void initListView() {
        channelsProvider = new TopicChannelsProvider(context) {
            @Override
            public Object getParam() {
                return topicNode;
            }
        };
        this.baseListController = new ChannelListController(channelsProvider);
        this.baseListController.setActivity(context);
        FrameLayout listLayout = new FrameLayout(context);
        this.baseListController.assumeView(listLayout);
        this.mListView = baseListController.mListView;
        this.mListView.setOnItemClickListener(this);
        this.mListView.setDivider(new ColorDrawable(Color.parseColor("#DDDDDD")));
        this.mListView.setDividerHeight(1);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(listLayout, layoutParams);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Sync", "onItemClick" + position);
        ControllerManager.getInstance(getContext()).openChannelDetailController(channelNodes.get(position - 1), true);
    }

    public void setData(List<ChannelNode> list) {
        channelNodes.clear();
        channelNodes.addAll(list);
        ((TopicChannelsProvider.MYAdapter) this.channelsProvider.getAdapter()).setData(channelNodes);
        this.baseListController.mHeadView.update(topicNode, channelsProvider.getOptions());
        title_label.setText(topicNode.title);
    }

    @Override
    public void onClick(View v) {
        AllInOneActivity allInOneActivity = (AllInOneActivity) getContext();
        allInOneActivity.onBackPressed();
    }
}
