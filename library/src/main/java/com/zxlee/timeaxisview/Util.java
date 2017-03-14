package com.zxlee.timeaxisview;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by lzx on 2017/1/4 0004
 */
public class Util {

    public static int calculateHightestIcon(ArrayList<Bitmap> bitmaps) {
        int max;
        if (bitmaps == null) {
            max = -1;
        } else {
            max = 0;
            int [] arr = new int[bitmaps.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = bitmaps.get(i).getHeight();
            }

            for (int i = 0; i < arr.length; i++) {
                if (arr[i] > max) {
                    max = arr[i];
                }
            }
        }
        return max;
    }

    /**
     * translate the unit of dp to px
     *
     * @param context  context
     * @param dipValue dp
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
