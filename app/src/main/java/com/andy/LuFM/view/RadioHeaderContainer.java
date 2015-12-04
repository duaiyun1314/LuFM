package com.andy.LuFM.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andy.LuFM.model.CategoryNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy.Wang on 2015/12/4.
 */
public class RadioHeaderContainer extends LinearLayout {
    private boolean isRegion;
    private List<CategoryNode> list;
    private boolean isDrag = false;
    private android.support.v7.widget.RecyclerView recyclerView;

    public RadioHeaderContainer(Context context) {
        this(context, null);
    }

    public RadioHeaderContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioHeaderContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        recyclerView = new RecyclerView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(recyclerView, layoutParams);

    }

    private void initRecyclerView() {
        int spanCount = isRegion ? 4 : 3;
        recyclerView.setLayoutManager(new MyGridLayoutManager(getContext(), spanCount));
        MyAdapter recyclerAdapter = new MyAdapter(this.list);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addItemDecoration(new DividerGridItemDecoration(getContext()));
    }


    public void update(boolean isRegion, List<CategoryNode> list) {
        this.isRegion = isRegion;
        this.list = list;


        initRecyclerView();
    }

    private class MyAdapter extends RecyclerView.Adapter implements MyItemClickListener {
        public List<CategoryNode> normallist = new ArrayList<>();
        public List<CategoryNode> initlist = new ArrayList<>();
        private MyItemClickListener mItemClickListener;

        public MyAdapter(List<CategoryNode> list) {
            initlist = list;
            this.normallist.clear();
            CategoryNode emptyNode = new CategoryNode();
            for (int i = 0; i < initlist.size(); i++) {
                this.normallist.add(initlist.get(i));
                if (isDrag) {
                    if (i == initlist.size() - 1) {
                        this.normallist.add(emptyNode);
                    }
                } else {

                    if (i == 4) {
                        this.normallist.add(emptyNode);
                        break;
                    }
                }

            }

            setOnItemClickListener(this);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RadioHeaderItemView itemView = new RadioHeaderItemView(getContext());
            MyViewHolder holder = new MyViewHolder(itemView, mItemClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder) holder).itemView.update(normallist.get(position), isDrag);
        }

        @Override
        public int getItemCount() {
            return this.normallist == null ? 0 : normallist.size();
        }


        /**
         * 设置Item点击监听
         *
         * @param listener
         */
        public void setOnItemClickListener(MyItemClickListener listener) {
            this.mItemClickListener = listener;
        }

        @Override
        public void onItemClick(View view, int postion) {
            CategoryNode categoryNode = this.normallist.get(postion);

            if (categoryNode.getName() == null) {
                CategoryNode emptyNode = new CategoryNode();
                this.normallist.clear();
                for (int i = 0; i < initlist.size(); i++) {
                    this.normallist.add(initlist.get(i));
                    if (isDrag) {
                        if (i == 4) {
                            this.normallist.add(emptyNode);
                            break;
                        }
                    } else {
                        if (i == initlist.size() - 1) {
                            this.normallist.add(emptyNode);
                        }
                    }

                }
                isDrag = !isDrag;
                Log.i("Sync", "initlist:" + initlist.size());
                notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), categoryNode.getName(), Toast.LENGTH_SHORT).show();
            }
        }

        private class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

            public RadioHeaderItemView itemView;
            public MyItemClickListener listener;

            public MyViewHolder(View itemView, MyItemClickListener listener) {
                super(itemView);
                this.listener = listener;
                this.itemView = (RadioHeaderItemView) itemView;
                this.itemView.setClickable(true);
                this.itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (this.listener != null) {
                    listener.onItemClick(v, getPosition());
                }
            }
        }
    }

    public class MyGridLayoutManager extends GridLayoutManager {

        public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public MyGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            View view = recycler.getViewForPosition(0);
            if (view != null) {
                measureChild(view, widthSpec, heightSpec);
                int measuredWidth = MeasureSpec.getSize(widthSpec);
                int measuredHeight = view.getMeasuredHeight();
                if (isRegion) {
                    setMeasuredDimension(measuredWidth, measuredHeight);
                } else if (!isRegion && isDrag) {
                    setMeasuredDimension(measuredWidth, measuredHeight * (list.size() / 3 + (list.size() % 3 > 0 ? 1 : 0)));
                } else if (!isRegion && !isDrag) {
                    setMeasuredDimension(measuredWidth, measuredHeight * 2);
                }

            }
        }
    }

    public interface MyItemClickListener {
        public void onItemClick(View view, int postion);
    }

    private class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable mDivider;

        public DividerGridItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            drawHorizontal(c, parent);
            drawVertical(c, parent);

        }

        private int getSpanCount(RecyclerView parent) {
            // 列数
            int spanCount = -1;
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {

                spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                spanCount = ((StaggeredGridLayoutManager) layoutManager)
                        .getSpanCount();
            }
            return spanCount;
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getLeft() - params.leftMargin;
                final int right = child.getRight() + params.rightMargin
                        + mDivider.getIntrinsicWidth();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);

                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getTop() - params.topMargin;
                final int bottom = child.getBottom() + params.bottomMargin;
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicWidth();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                    int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                    {
                        return true;
                    }
                } else {
                    childCount = childCount - childCount % spanCount;
                    if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                        return true;
                }
            }
            return false;
        }

        private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                                  int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager)
                        .getOrientation();
                // StaggeredGridLayoutManager 且纵向滚动
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    childCount = childCount - childCount % spanCount;
                    // 如果是最后一行，则不需要绘制底部
                    if (pos >= childCount)
                        return true;
                } else
                // StaggeredGridLayoutManager 且横向滚动
                {
                    // 如果是最后一行，则不需要绘制底部
                    if ((pos + 1) % spanCount == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition,
                                   RecyclerView parent) {
            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();
            if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            } else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
            {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(),
                        mDivider.getIntrinsicHeight());
            }
        }
    }
}
