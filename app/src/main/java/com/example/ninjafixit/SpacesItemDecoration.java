package com.example.ninjafixit;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, View view, @NonNull RecyclerView parent,RecyclerView.State state) {
        if((parent.getChildLayoutPosition(view)%2)==0)
        {
            //position starts from 0 hence left items will be even and viceversa.
            outRect.left=space;
            outRect.right=0;
        }
        else {
            outRect.left=0;
            outRect.right=space;
        }

        outRect.top=0;
    }
}
