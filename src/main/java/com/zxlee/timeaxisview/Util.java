package com.zxlee.timeaxisview;

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

}
