package com.guo.android.zhihu_topanswer.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.guo.android.architecture.adapter.BaseAdapter;
import com.guo.android.zhihu_topanswer.R;



public class SelectPopupWindow extends PopupWindow {

    private final View mMenuView;
    private final RecyclerView mRecyclerView;

    public SelectPopupWindow(Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_layout, null);
        mRecyclerView = (RecyclerView) mMenuView.findViewById(R.id.pop_layout1);

        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        setOutsideTouchable(false);

    }

    public void setAdapter(BaseAdapter adapter) {
        GridLayoutManager layoutManager = new GridLayoutManager(mRecyclerView.getContext(), 4);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);


    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }



}
