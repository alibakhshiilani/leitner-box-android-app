package com.alibakhshiilani.leitnerbox;

import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public final class OverWriteFont {

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setTitleFont(Context context, String fontAssetName, TextView myTitle){
        Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        myTitle.setTypeface(regular);
    }

    public static Typeface setChartFont(Context context, String fontAssetName){
        Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        return regular;
    }
}