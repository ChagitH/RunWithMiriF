package com.forst.miri.runwithme.widges;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forst.miri.runwithme.R;

/**
 * Created by chagithazani on 9/3/17.
 */

public class OutputDataBox extends FrameLayout {
    private TextView mValueTextView;
    private TextView mTitleTextView;
    private ImageView mRightImageView;
    private LinearLayout mMainLayout;

    public OutputDataBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.OutputDataBox, 0, 0);
        String titleText = a.getString(R.styleable.OutputDataBox_titleText);
        String valueText = a.getString(R.styleable.OutputDataBox_valueText);
        Drawable rightSideImage = a.getDrawable(R.styleable.OutputDataBox_circleImage);
        boolean imageVisible = a.getBoolean(R.styleable.OutputDataBox_imageVisible, true);
        float textSize = a.getDimension(R.styleable.OutputDataBox_textSize, 15);
        @SuppressWarnings("ResourceAsColor")
        int textColor = a.getColor(R.styleable.OutputDataBox_textColor, R.color.colorPurple);
        Drawable boxBackground = a.getDrawable(R.styleable.OutputDataBox_backgroundDrawable);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.output_data_box_layout, this, true);

        mTitleTextView = (TextView) findViewById(R.id.data_box_tv_title);
        mTitleTextView.setText(titleText);
        mTitleTextView.setTextSize(textSize);

        mValueTextView = (TextView) findViewById(R.id.data_box_tv_value);
        mValueTextView.setText(valueText);
        mValueTextView.setTextSize(textSize);
        mValueTextView.setTextColor(textColor);

        mRightImageView = (ImageView) findViewById(R.id.data_box_image);
        mRightImageView.setImageDrawable(rightSideImage);
        setImageVisible(imageVisible);

        mMainLayout = (LinearLayout) findViewById(R.id.data_box_main_linear_layout);
        mMainLayout.setBackground(boxBackground);

    }

    public OutputDataBox(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setImageVisible(boolean visible) {
        mRightImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setValueText (String text){
        mValueTextView.setText(text);
    }

    public void setImage(int imageId){
        mRightImageView.setImageResource(imageId);
        setImageVisible(true);
    }

}
