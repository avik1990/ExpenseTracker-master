package com.app.exptracker.utility;

import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

public class PaddingItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int parentWidth = parent.getWidth();
        int childWidth = view.getWidth();
        int margin = (parentWidth - childWidth) / 2;

        int position = parent.getChildAdapterPosition(view);

        outRect.left = position == 0 ? margin : 0;
        outRect.right = position == (parent.getAdapter().getItemCount() - 1) ? margin : 0;
    }
}