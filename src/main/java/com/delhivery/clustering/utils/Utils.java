package com.delhivery.clustering.utils;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.String.format;

public final class Utils {
    public static String formatNumber(double n) {
        return format("%.6f", n);
    }

    public static double truncate(double d, int placeAfterDecimal) {

        double multiplier = pow(10, placeAfterDecimal);

        return ((int) (d * multiplier)) / multiplier;
    }

    public static boolean isZero(double d, double tolerance) {
        return abs(d) <= tolerance;
    }

    public static boolean isZero(double d) {
        return isZero(d, 10e-8);
    }

}
