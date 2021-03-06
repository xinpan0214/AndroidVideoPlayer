package com.caldremch.common.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * @author Caldremch
 * @date 2019-04-24 10:54
 * @email caldremch@163.com
 * @describe
 **/
public class MetricsUtils {
    public static int getStatusBarHeight(Context context) {
        int result = DensityUtil.dp2px(25);
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void compatTitle(Context context, final View view){
        final  int  statusBarHeight = getStatusBarHeight(context);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (lp instanceof ViewGroup.MarginLayoutParams){
                    ViewGroup.MarginLayoutParams  mlp = (ViewGroup.MarginLayoutParams) lp;
                    mlp.topMargin = statusBarHeight;
                    view.requestLayout();
                }
            }
        });
    }
}
