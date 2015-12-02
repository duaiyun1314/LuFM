package com.andy.LuFM.fragments;

import android.support.v4.app.Fragment;

import java.lang.reflect.Field;

/**
 * Created by Andy.Wang on 2015/12/2.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
