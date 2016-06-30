package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by alfon on 2016-06-30.
 */
public class UrlImageView extends ImageView {
    private String mUrl;

    public UrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UrlImageView,
                0, 0);
        try {
            mUrl = a.getString(R.styleable.UrlImageView_url);
        } finally {
            a.recycle();
        }
    }

    public void setUrl(String url) {
        mUrl = url;
        invalidate();
        requestLayout();
    }

    public String getUrl() {
        return mUrl;
    }
}
