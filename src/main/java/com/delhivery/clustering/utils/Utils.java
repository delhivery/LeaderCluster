package com.delhivery.clustering.utils;

import static com.delhivery.clustering.config.Constants.DECIMAL_FORMAT;
import static com.delhivery.clustering.config.Constants.TOLERANCE;
import static java.lang.Math.abs;

public final class Utils {

    private Utils() {}

    public static String formatNumber(double n) {
        return DECIMAL_FORMAT.format(n);
    }

    public static boolean isZero(double d) {
        return abs(d) <= TOLERANCE;
    }

}
