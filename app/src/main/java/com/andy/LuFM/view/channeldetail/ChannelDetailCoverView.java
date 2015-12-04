package com.andy.LuFM.view.channeldetail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.helper.PodcasterHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.UserInfo;
import com.andy.LuFM.view.CircleImageView;
import com.andy.LuFM.view.RatingBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelDetailCoverView extends LinearLayout implements InfoManager.ISubscribeEventListener {
    private ImageView info_pic;
    private TextView info_discription;
    private CircleImageView info_avaimg;
    private TextView info_avaname;
    private TextView tag_name;
    private Button tag_sort_bt;
    private DisplayImageOptions options;
    private RatingBar ratingBar;
    private int mPodcasterId;
    private UserInfo mPodcasterInfo;


    private ChannelNode mChannelNode;

    public ChannelDetailCoverView(Context context) {
        this(context, null);
    }

    public ChannelDetailCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChannelDetailCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.detail_cover_view, this);
        info_pic = (ImageView) findViewById(R.id.info_pic);
        info_discription = (TextView) findViewById(R.id.info_discription);
        info_avaimg = (CircleImageView) findViewById(R.id.info_avaimg);
        info_avaname = (TextView) findViewById(R.id.info_avaname);
        tag_name = (TextView) findViewById(R.id.tag_name);
        tag_sort_bt = (Button) findViewById(R.id.tag_sort_bt);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.recommend_defaultbg)
                .showImageOnFail(R.drawable.recommend_defaultbg)
                .build();
    }

    public void update(String type, Object param) {
        if (type.equalsIgnoreCase("setdata")) {
            this.mChannelNode = (ChannelNode) param;
            updateView();
        }
    }

    private void updateView() {
        //update info view
        String thumb = this.mChannelNode.getApproximativeThumb(250, 250, true);
        if (!TextUtils.isEmpty(thumb)) {
        } else if (this.mChannelNode.parent == null || !this.mChannelNode.parent.nodeName.equalsIgnoreCase("recommenditem")) {
        } else {
            thumb = ((RecommendItemNode) this.mChannelNode.parent).getApproximativeThumb(250, 250);
        }
        ImageLoader.getInstance().displayImage(thumb, this.info_pic, options);
        this.info_discription.setText(this.mChannelNode.desc);
        if (this.mChannelNode.lstPodcasters == null || this.mChannelNode.lstPodcasters.size() == 0) {
            setPodcasterInfo(null);
        } else {
            this.mPodcasterId = ((UserInfo) this.mChannelNode.lstPodcasters.get(0)).podcasterId;
            this.mPodcasterInfo = PodcasterHelper.getInstance().getPodcaster(this.mPodcasterId);
            setPodcasterInfo(this.mPodcasterInfo);
            InfoManager.getInstance().loadPodcasterBaseInfo(this.mPodcasterId, this);
        }
        this.ratingBar.setStar(this.mChannelNode.ratingStar / 10f * 5);
        //update tag view
        if (mChannelNode.programCnt > 0) {
            this.tag_name.setText(String.format("\u5171%d\u671f", new Object[]{Integer.valueOf(mChannelNode.programCnt)}));
        }
    }

    private void setPodcasterInfo(UserInfo podcasterInfo) {
        if (podcasterInfo == null /*|| podcasterInfo.snsInfo == null*/) {
            return;
        }

        ImageLoader.getInstance().displayImage(podcasterInfo.snsInfo.sns_avatar, info_avaimg, options);
        info_avaname.setText(podcasterInfo.podcasterName);
    }

    @Override
    public void onNotification(String type) {
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_PODCASTER_BASEINFO)) {
            this.mPodcasterInfo = PodcasterHelper.getInstance().getPodcaster(this.mPodcasterId);
            setPodcasterInfo(this.mPodcasterInfo);
        }

    }
}
