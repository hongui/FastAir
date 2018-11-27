package com.mob.lee.fastair.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2016/10/1.
 */

public class ContentMenuRecyclerView extends RecyclerView {
    private RecyclerviewContentMenuInfo mMenuInfo;
    public ContentMenuRecyclerView(Context context) {
        super(context);
    }

    public ContentMenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentMenuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mMenuInfo;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        int position=getChildAdapterPosition(originalView);
        if(0<=position){
            long id = getAdapter().getItemId(position);
            mMenuInfo=new RecyclerviewContentMenuInfo(id,position);
            super.showContextMenuForChild(originalView);
            return true;
        }
        return false;
    }

    public static class RecyclerviewContentMenuInfo implements ContextMenu.ContextMenuInfo{
        public final long id;
        public final int position;

        public RecyclerviewContentMenuInfo(long id,int position){
            this.id=id;
            this.position=position;
        }
    }

}
