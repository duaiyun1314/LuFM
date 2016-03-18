package com.andy.LuFM.app;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.andy.LuFM.R;


public abstract class BaseToolBarActivity extends AppCompatActivity {
    protected FrameLayout content;
    protected Toolbar toolbar;
    protected int colorPrimary;
    protected int colorPrimaryDark;
    protected int colorAccent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getBasicContentLayout());
        TypedArray array = obtainStyledAttributes(new int[]{R.attr.colorPrimary, R.attr.colorPrimaryDark, R.attr.colorAccent});
        colorPrimary = array.getColor(0, 0xFF1473AF);
        colorPrimaryDark = array.getColor(1, 0xFF11659A);
        colorAccent = array.getColor(2, 0xFF3C69CE);
        array.recycle();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        content = (FrameLayout) findViewById(R.id.content);
    }

    @Override
    public void setContentView(int layoutResID) {
        content.removeAllViews();
        getLayoutInflater().inflate(layoutResID, content);
    }

    public void setContentViewSuper(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        content.removeAllViews();
        content.addView(view);
    }

    public void setContentViewUseSuper(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        content.removeAllViews();
        content.addView(view, params);
    }

    public void setContentViewsSuper(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected int getBasicContentLayout() {
        return R.layout.activity_basetoolbar;
    }
}
