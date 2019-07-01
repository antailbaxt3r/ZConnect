package com.zconnect.zutto.zconnect;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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

    /**
     * Called when a nested scroll in progress is about to update, before the target has
     * consumed any of the scrolled distance.
     *
     * <p>Any Behavior associated with the direct child of the CoordinatorLayout may elect
     * to accept the nested scroll as part of {@link #onStartNestedScroll}. Each Behavior
     * that returned true will receive subsequent nested scroll events for that nested scroll.
     * </p>
     *
     * <p><code>onNestedPreScroll</code> is called each time the nested scroll is updated
     * by the nested scrolling child, before the nested scrolling child has consumed the scroll
     * distance itself. <em>Each Behavior responding to the nested scroll will receive the
     * same values.</em> The CoordinatorLayout will report as consumed the maximum number
     * of pixels in either direction that any Behavior responding to the nested scroll reported
     * as consumed.</p>
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     *                          associated with
     * @param child             the child view of the CoordinatorLayout this Behavior is associated with
     * @param target            the descendant view of the CoordinatorLayout performing the nested scroll
     * @param dx                the raw horizontal number of pixels that the user attempted to scroll
     * @param dy                the raw vertical number of pixels that the user attempted to scroll
     * @param consumed          out parameter. consumed[0] should be set to the distance of dx that
     *                          was consumed, consumed[1] should be set to the distance of dy that
     *                          was consumed
     * @param type              the type of input which cause this scroll event
     */
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull TabLayout child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
//        child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy));
        FloatingActionButton fb = coordinatorLayout.findViewById(R.id.fab_cat_infone);
        float min = child.getTranslationY()+dy;
        float max = 0;
        if(((float)child.getHeight())<min){
            min = child.getHeight();
        }
        if(min>max){
            max = min;
        }
        child.setTranslationY(max);
        fb.setTranslationY(max);

    }

//    @Override
//    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull TabLayout child,
//                               @NonNull View target, int dxConsumed, int dyConsumed,
//                               int dxUnconsumed, int dyUnconsumed,
//                               @ViewCompat.NestedScrollType int type)
//    {
//
//        if (dyConsumed > 0) {
//            float d = ((float)dyConsumed/(float)(dyConsumed+dyUnconsumed))*height;
//            Log.d("Given",Float.toString(d));
//            Log.d("animate",Integer.toString(height));
//            slideDown(child,d);
//        } else if (dyConsumed < 0) {
//            slideUp(child);
//        }
//    }
//
//    private void slideUp(TabLayout child) {
//        Log.d("BottomNavigation", "sligin up");
//        child.clearAnimation();
//        child.animate().translationY(0).setDuration(200);
//    }
//
//    private void slideDown(TabLayout child,float d) {
//        Log.d("BottomNavigation", "sligin down");
//
//        child.clearAnimation();
//        child.animate().translationY(d).setDuration(2);
//    }
}