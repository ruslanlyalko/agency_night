package com.ruslanlyalko.agency.presentation.utils;

import android.content.res.Resources;

import com.ruslanlyalko.agency.R;

/**
 * Created by Ruslan Lyalko
 * on 26.09.2018.
 */
public class ColorUtils {

    public static int getTextColorByStatus(Resources r, boolean isMyOrder) {
        if (isMyOrder)
            return R.color.colorAccent;
        return R.color.colorYellow;
    }
}
