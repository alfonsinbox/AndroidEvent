package com.example.alfon.eventtest;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by alfon on 2016-07-10.
 */
public class EventsGridScrollingBehavior extends AppBarLayout.ScrollingViewBehavior {

    private View layout;

    public EventsGridScrollingBehavior() {
    }

    public EventsGridScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        boolean result = super.onDependentViewChanged(parent, child, dependency);
        if (layout != null) {
            layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout
                    .getPaddingRight(), layout.getTop());
        }
        return result;
    }

    public void setLayout(View layout) {
        this.layout = layout;
    }

}
