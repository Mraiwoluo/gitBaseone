package com.pvetec.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pvetec.weather.R;

/**
 * Created by admin on 2016/7/6.
 */
public class IndicatorView  extends LinearLayout {
    int indicator = -1;
    public IndicatorView(Context context) {
        this(context, null, 0);

    }
    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public int getCount() {
        return getChildCount();
    }

    public View add() {
        ImageView v = new ImageView(getContext());
        v.setBackgroundResource(R.drawable.selectable);
        v.setScaleType(ImageView.ScaleType.FIT_XY);
        //LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams params = new LayoutParams(dip2px(getContext(), 10), dip2px(getContext(), 10));
        params.setMargins(dip2px(getContext(), 3), 0, dip2px(getContext(), 3), 0);
        addView(v, params);
        if (indicator < 0) {
            setIndicator(0);
        }
        return v;
    }

    public void del() {
        int count = getCount();
        if (count > 0) {
            removeViewAt(0);
            if (indicator > (getCount()-1)) {
                setIndicator((getCount()-1));
            }
        }
    }

    public void add(int num) {
        for (int i = 0; i < num; i++) {
            add();
        }
    }

    public void del(int num) {
        for (int i = 0; i < num; i++) {
            del();
        }
    }

    public void clear() {
        removeAllViews();
    }

    public void setIndicator(int indicator) {
        View v = null;
        if (indicator < getCount()) {
            if (this.indicator < 0 || (null != (v = getChildAt(this.indicator)))) {
                if (null != v) {
                    v.setBackgroundResource(R.drawable.selectable);
                }
                this.indicator = indicator;
                v = getChildAt(this.indicator);
                if (null != v) {
                    v.setBackgroundResource(R.drawable.selected);
                }
            }
        }
    }

    public int getIndicator() {
        return indicator;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
