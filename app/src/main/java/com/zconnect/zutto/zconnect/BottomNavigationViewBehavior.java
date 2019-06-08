package com.zconnect.zutto.zconnect;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<TabLayout> {

    private int height;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, TabLayout child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       TabLayout child, @NonNull
                                               View directTargetChild, @NonNull View target,
                                       int axes, int type)
    {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull TabLayout child,
                               @NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               @ViewCompat.NestedScrollType int type)
    {
        if (dyConsumed > 0) {
            slideDown(child);
        } else if (dyConsumed < 0) {
            slideUp(child);
        }
    }

    private void slideUp(TabLayout child) {
        Log.d("BottomNavigation", "sligin up");
        child.clearAnimation();
        child.animate().translationY(0).setDuration(200);
    }

    private void slideDown(TabLayout child) {
        Log.d("BottomNavigation", "sligin down");

        child.clearAnimation();
        child.animate().translationY(height).setDuration(200);
    }
}