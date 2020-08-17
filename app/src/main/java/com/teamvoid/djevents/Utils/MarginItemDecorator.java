package com.teamvoid.djevents.Utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecorator extends RecyclerView.ItemDecoration {
    private int top, bottom, left, right;
    private boolean isHorizontal = false;

    public MarginItemDecorator(Context context, int top, int bottom, int left, int right) {
        this.top = dp2px(context, top);
        this.bottom = dp2px(context, bottom);
        this.left = dp2px(context, left);
        this.right = dp2px(context, right);
    }

    public MarginItemDecorator(Context context, int top, int bottom, int left, int right, boolean isHorizontal) {
        this.top = dp2px(context, top);
        this.bottom = dp2px(context, bottom);
        this.left = dp2px(context, left);
        this.right = dp2px(context, right);
        this.isHorizontal = isHorizontal;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (!isHorizontal) {
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = top;
            outRect.left = left;
        } else {
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.left = left;
            outRect.top = top;
        }
        outRect.right = right;
        outRect.bottom = bottom;
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
