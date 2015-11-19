package com.andy.LuFM.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.LuFM.AllInOneActivity;
import com.andy.LuFM.R;
import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.view.BaseRecommendView;
import com.andy.LuFM.view.RecommendNovelView;
import com.andy.LuFM.view.RecommendView;
import com.andy.LuFM.view.SlidingTabLayout;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class DiscoverFragment extends Fragment {
    private ViewPager mViewPager;
    private SlidingTabLayout slidingTabLayout;
    private List<CategoryNode> lists;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        lists = ((AllInOneActivity) activity).categoryNodes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_discover);
        this.slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        this.slidingTabLayout.setDistributeEvenly(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager.setAdapter(new MyAdapter());
        slidingTabLayout.setViewPager(mViewPager);
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return lists == null ? 0 : lists.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            CategoryNode node = lists.get(position);
            BaseRecommendView viewGroup = null;
            switch (node.getSectionId()) {
                case 0:
                    viewGroup = new RecommendView(getActivity());
                    break;
                case Constants.NOVEL_SECTION:
                    viewGroup = new RecommendNovelView(getActivity());
                    break;
            }
            if (viewGroup != null) {
                (container).addView(viewGroup);
                viewGroup.update();
            }

            return viewGroup;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            // super.destroyItem(container, position, object);
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return lists.get(position).getName();
        }
    }
}
