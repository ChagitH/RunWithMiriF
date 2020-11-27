package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forst.miri.runwithme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleTipFragment extends Fragment {

    public static String POSITION_TAG = "position_tip_tag";
    public static String PAID_CUSTOMER_TAG = "paid_customer_tag";
    public static int PAID_CUSTOMER_COUNT = 17;
    public static int FREE_CUSTOMER_COUNT = 3;
    int mPosition;
    boolean mPaidCustomer;


    public SingleTipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_tip, container, false);
        ImageView image = (ImageView)parentView.findViewById(R.id.tip_top_icon);
        TextView title = (TextView)parentView.findViewById(R.id.tip_tip_text_title);
        final TextView body = (TextView)parentView.findViewById(R.id.tip_tip_text_text);

//        body.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                event.getAction()
//                body.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });

        body.setMovementMethod(new ScrollingMovementMethod());

        image.setImageResource(getImageResId());
        title.setText(getString(getTitleResId()));
        body.setText(getString(getBodyResId()));
        return parentView;
    }

    private int getImageResId() {
        if(mPaidCustomer) {
            switch (mPosition) {
                case 16:
                    return R.drawable.tip17_icon;
                case 15:
                    return R.drawable.tip16_icon;
                case 14:
                    return R.drawable.tip15_icon;
                case 13:
                    return R.drawable.tip14_icon;
                case 12:
                    return R.drawable.tip13_icon;
                case 11:
                    return R.drawable.tip12_icon;
                case 10:
                    return R.drawable.tip11_icon;
                case 9:
                    return R.drawable.tip10_icon;
                case 8:
                    return R.drawable.tip9_icon;
                case 7:
                    return R.drawable.tip8_icon;
                case 6:
                    return R.drawable.tip7_icon;
                case 5:
                    return R.drawable.tip6_icon;
                case 4:
                    return R.drawable.tip5_icon;
                case 3:
                    return R.drawable.tip4_icon;
                case 2:
                    return R.drawable.tip3_icon;
                case 1:
                    return R.drawable.tip2_icon;
                case 0:
                    return R.drawable.tip1_icon;
                default:
                    return R.drawable.tip1_icon;

            }
        } else {
            switch (mPosition) {
                case 2:
                    return R.drawable.tip_more_icon;
                case 1:
                    return R.drawable.tip12_icon;
                case 0:
                    return R.drawable.tip7_icon;
                default:
                    return R.drawable.tip7_icon;

            }
        }
    }

    private int getTitleResId() {
        if(mPaidCustomer) {
            switch (mPosition) {
                case 16:
                    return R.string.tip17_title_heb;
                case 15:
                    return R.string.tip16_title_heb;
                case 14:
                    return R.string.tip15_title_heb;
                case 13:
                    return R.string.tip14_title_heb;
                case 12:
                    return R.string.tip13_title_heb;
                case 11:
                    return R.string.tip12_title_heb;
                case 10:
                    return R.string.tip11_title_heb;
                case 9:
                    return R.string.tip10_title_heb;
                case 8:
                    return R.string.tip9_title_heb;
                case 7:
                    return R.string.tip8_title_heb;
                case 6:
                    return R.string.tip7_title_heb;
                case 5:
                    return R.string.tip6_title_heb;
                case 4:
                    return R.string.tip5_title_heb;
                case 3:
                    return R.string.tip4_title_heb;
                case 2:
                    return R.string.tip3_title_heb;
                case 1:
                    return R.string.tip2_title_heb;
                case 0:
                    return R.string.tip1_title_heb;
                default:
                    return R.string.tip1_title_heb;

            }
        } else {
            switch (mPosition) {
                case 2:
                    return R.string.more_tips_title_heb;
                case 1:
                    return R.string.tip12_title_heb;
                case 0:
                    return R.string.tip7_title_heb;
                default:
                    return R.string.tip7_title_heb;

            }
        }
    }

    private int getBodyResId() {
        if(mPaidCustomer) {
            switch (mPosition) {
                case 16:
                    return R.string.tip_17_heb;
                case 15:
                    return R.string.tip_16_heb;
                case 14:
                    return R.string.tip_15_heb;
                case 13:
                    return R.string.tip_14_heb;
                case 12:
                    return R.string.tip_13_heb;
                case 11:
                    return R.string.tip_12_heb;
                case 10:
                    return R.string.tip_11_heb;
                case 9:
                    return R.string.tip_10_heb;
                case 8:
                    return R.string.tip_9_heb;
                case 7:
                    return R.string.tip_8_heb;
                case 6:
                    return R.string.tip_7_heb;
                case 5:
                    return R.string.tip_6_heb;
                case 4:
                    return R.string.tip_5_heb;
                case 3:
                    return R.string.tip_4_heb;
                case 2:
                    return R.string.tip_3_heb;
                case 1:
                    return R.string.tip_2_heb;
                case 0:
                    return R.string.tip_1_heb;
                default:
                    return R.string.tip_1_heb;

            }
        } else {
            switch (mPosition) {

                case 2:
                    return R.string.more_tip_for_registered_heb;
                case 1:
                    return R.string.tip_12_heb;
                case 0:
                    return R.string.tip_7_heb;
                default:
                    return R.string.tip_7_heb;

            }
        }
    }

    static SingleTipFragment init(int position , boolean paidCustomer) {
        SingleTipFragment tipFrag = new SingleTipFragment();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt(POSITION_TAG, position);
        args.putBoolean(PAID_CUSTOMER_TAG, paidCustomer);
        tipFrag.setArguments(args);
        return tipFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments() != null ? getArguments().getInt(POSITION_TAG) : 1;
        mPaidCustomer = getArguments() != null ? getArguments().getBoolean(PAID_CUSTOMER_TAG) : false;
    }
}
