package com.example.android.androidify.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

public class MusicColorUtils {
    private static final String TAG = "MUSIC_UTILS";

    @Nullable
    public static Palette generatePalette(Bitmap bitmap) {
        if (bitmap == null) return null;
        return Palette.from(bitmap).generate();
    }

    @ColorInt
    public static int getColor(@Nullable Palette palette, int fallback) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null) {
                //Log.i(TAG, "returning vibrant color");
                return palette.getVibrantSwatch().getRgb();
            } else if (palette.getMutedSwatch() != null) {
                //Log.i(TAG, "returning muted color");
                return palette.getMutedSwatch().getRgb();
            } else if (palette.getDarkVibrantSwatch() != null) {
                //Log.i(TAG, "returning dark vibrant color");
                return palette.getDarkVibrantSwatch().getRgb();
            } else if (palette.getDarkMutedSwatch() != null) {
                //Log.i(TAG, "returning dark muted color");
                return palette.getDarkMutedSwatch().getRgb();
            } else if (palette.getLightVibrantSwatch() != null) {
                //Log.i(TAG, "returning light vibrant color");
                return palette.getLightVibrantSwatch().getRgb();
            } else if (palette.getLightMutedSwatch() != null) {
                //Log.i(TAG, "returning light muted color");
                return palette.getLightMutedSwatch().getRgb();
            } else if (!palette.getSwatches().isEmpty()) {
                return Collections.max(palette.getSwatches(), SwatchComparator.getInstance()).getRgb();
            }
        }
        return fallback;
    }

    public static Palette.Swatch getSwatch(@Nullable Palette palette) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null) {
                //Log.i(TAG, "returning vibrant swatch");
                return palette.getVibrantSwatch();
            } else if (palette.getMutedSwatch() != null) {
                //Log.i(TAG, "returning muted swatch");
                return palette.getMutedSwatch();
            } else if (palette.getDarkVibrantSwatch() != null) {
                //Log.i(TAG, "returning dark vibrant swatch");
                return palette.getDarkVibrantSwatch();
            } else if (palette.getDarkMutedSwatch() != null) {
                //Log.i(TAG, "returning dark muted swatch");
                return palette.getDarkMutedSwatch();
            } else if (palette.getLightVibrantSwatch() != null) {
                //Log.i(TAG, "returning light vibrant swatch");
                return palette.getLightVibrantSwatch();
            } else if (palette.getLightMutedSwatch() != null) {
                //Log.i(TAG, "returning light muted swatch");
                return palette.getLightMutedSwatch();
            } else if (!palette.getSwatches().isEmpty()) {
                return Collections.max(palette.getSwatches(), SwatchComparator.getInstance());
            }
        }
        return null;
    }

    private static class SwatchComparator implements Comparator<Palette.Swatch> {
        private static SwatchComparator sInstance;

        static SwatchComparator getInstance() {
            if (sInstance == null) {
                sInstance = new SwatchComparator();
            }
            return sInstance;
        }

        @Override
        public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
            return lhs.getPopulation() - rhs.getPopulation();
        }
    }


    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    @ColorInt
    public static int lightenColor(@ColorInt int color) {
        return shiftColor(color, 1.1f);
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float by) {
        if (by == 1f) return color;
        int alpha = Color.alpha(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return (alpha << 24) + (0x00ffffff & Color.HSVToColor(hsv));
    }

    @ColorInt
    public static int modifyBackgroundColor(@ColorInt int backgroundColor) {
        while (MusicColorUtils.isColorLight(backgroundColor)) {
            backgroundColor = MusicColorUtils.darkenColor(backgroundColor);
            //Log.i(TAG, "isLight = " + isColorLight(backgroundColor));
        }
        return backgroundColor;
    }



    public static boolean isColorLight(@ColorInt int color) {
        final double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        //Log.i(TAG, "darkness value = " + darkness);
        return darkness < 0.6;
    }

    public static boolean isLight(@ColorInt int color) {
        final double luminance =  ColorUtils.calculateLuminance(color);
        Log.i(TAG, "luminance = " + luminance);
        return luminance > 0.2;
    }
}
