package com.andy.LuFM.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andy.LuFM.AllInOneActivity;
import com.andy.LuFM.R;
import com.andy.LuFM.model.CategoryNode;
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
            RecommendView recommendView = new RecommendView(getActivity());
            ((ViewPager) container).addView(recommendView);
            recommendView.update();
            return recommendView;
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
