package com.forst.miri.runwithme.widges;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.forst.miri.runwithme.R;


/**
 * Created by chagithazani on 6/11/18.
 */

public class TextViewCustomFont extends android.support.v7.widget.AppCompatTextView {

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewCustomFont, 0, 0);
        String fontStyle = a.getString(R.styleable.TextViewCustomFont_customFontStyle);
        a.recycle();

        Typeface font = null;


        if(fontStyle != null && fontStyle.matches(context.getString(R.string.bold))){
            font = Typeface.createFromAsset(context.getAssets(), "fonts/Rubik-Bold.ttf");
        }else { //bold
            font = Typeface.createFromAsset(context.getAssets(), "fonts/Rubik-Regular.ttf");
        }
        this .setTypeface(font);
    }


}
