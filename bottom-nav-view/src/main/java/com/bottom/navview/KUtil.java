package com.bottom.navview;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class KUtil {
//    public static int dpToPx(float dp, Context context) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                context.getResources().getDisplayMetrics());
//    }
//
//    public static int spToPx(float sp, Context context) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
//                context.getResources().getDisplayMetrics());
//    }
//    public static int pxToSp(float px, Context context) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
//                context.getResources().getDisplayMetrics());
//    }
    //Convert dp to pixel:
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
  public static int spToPx(int sp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,displayMetrics);
    }



    //Convert pixel to dp:
    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int dpToSp(float dp) {
        return (int) (dpToPx((int) dp) / Resources.getSystem().getDisplayMetrics().scaledDensity);
    }
}
