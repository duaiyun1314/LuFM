package com.andy.LuFM.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.app.AllInOneActivity;
import com.andy.LuFM.controller.ChannelListController;
import com.andy.LuFM.controller.ChannelListController4Live;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.providers.LiveChannelsProvider;
import com.andy.LuFM.providers.TopicChannelsProvider;

import java.util.ArrayList;
import java.util.List;

import roboguice.util.Ln;

/**
 * Created by Andy.Wang on 2015/12/3.
 */
public class LiveChannelListView extends LinearLayout implements AdapterView.OnItemClickListener, View.OnClickListener {
    private CategoryNode categoryNode;
    private ListView mListView;
    private Context context;
    List<ChannelNode> channelNodes = new ArrayList();
    private ChannelListController4Live baseListController;
    private LiveChannelsProvider liveChannelsProvider;
    private Handler mHandler = new Handler();
    private ImageButton iv_back;
    private TextView title_label;

    public LiveChannelListView(Context context) {
        this(context, null);
    }

    public LiveChannelListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveChannelListView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        CategoryNode temp = (CategoryNode) param;
        if (this.categoryNode != temp) {
            this.categoryNode = temp;
            //init listview
            initListView();
            initData();

        }
    }

    private void initData() {

        if (this.categoryNode != null) {
            baseListController.loadData(categoryNode);
        }
    }

    private void initListView() {
        liveChannelsProvider = new LiveChannelsProvider(context) {
            @Override
            public Object getParam() {
                return categoryNode;
            }
        };
        this.baseListController = new ChannelListController4Live(liveChannelsProvider);
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
        // ControllerManager.getInstance(getContext()).openChannelDetailController(channelNodes.get(position - 1), true);
        ControllerManager.getInstance(getContext()).openPlayController(channelNodes.get(position).channelId);
    }

    public void setData(List<ChannelNode> list) {
        channelNodes.clear();
        channelNodes.addAll(list);
        ((LiveChannelsProvider.MYAdapter) this.liveChannelsProvider.getAdapter()).setData(channelNodes);
        title_label.setText(categoryNode.getName());
    }

    @Override
    public void onClick(View v) {
        AllInOneActivity allInOneActivity = (AllInOneActivity) getContext();
        allInOneActivity.onBackPressed();
    }
}
