package com.pvetec.weather.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pvetec.weather.utils.LogUtils;

/**
 * widgets 时间字体颜色
 *
 * Created by Administrator on 2017/6/14 0014.
 */

public class CustomFontTextView extends TextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, textStyle);
        if (customFont != null) setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        /*
        * information about the TextView textStyle:
        * http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
        */
        switch (textStyle) {
            case Typeface.BOLD: // bold
                return getCustomTypeface(context);

            case Typeface.ITALIC: // italic
                // return FontCache.getTypeface("SourceSansPro-Italic.ttf", context);

            case Typeface.BOLD_ITALIC: // bold italic
                //return FontCache.getTypeface("SourceSansPro-BoldItalic.ttf", context);

            case Typeface.NORMAL: // regular
            default:
                //return FontCache.getTypeface("SourceSansPro-Regular.ttf", context);
        }
        return null;
    }

    private Typeface getCustomTypeface(Context context){
        Typeface typeface=null;
        try{
            if(context!=null){
                typeface= Typeface.createFromAsset(context.getAssets(),"HELVETICAINSERAT-ROMAN-SEMIB.TTF");
                LogUtils.i("CustomFontTextView","----getCustomTypeface---ok-");
            }
        }catch (Exception e){

        }
        return typeface;
    }

}
