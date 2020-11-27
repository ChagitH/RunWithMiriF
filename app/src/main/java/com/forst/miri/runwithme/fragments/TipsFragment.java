package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.objects.ConnectedUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class TipsFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = TipsFragment.class.getSimpleName();

    private static final String PAID_CUSTOMER_TAG = "paid_customer_tag";
    private boolean mPaidCustomer = false;
    private ViewPager mViewPager;

    public TipsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(PAID_CUSTOMER_TAG, mPaidCustomer);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_tips, container, false);
        mViewPager = (ViewPager) parentView.findViewById(R.id.tips_view_pager);

        if (savedInstanceState != null){
            mPaidCustomer = savedInstanceState.getBoolean(PAID_CUSTOMER_TAG, false);
        } else {
            if(ConnectedUser.getInstance() != null) {
                mPaidCustomer = ConnectedUser.getInstance().IsPaidRegistrationToRunWithMiri();
            }
        }


        TipsPageAdapter adapter = new TipsPageAdapter(getFragmentManager());

        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        float density = getResources().getDisplayMetrics().density;

        int partialWidth = (int)(16 * density); // 16dp
        int pageMargin = (int)(5 * density); // 8dp

        //int viewPagerPadding = partialWidth + pageMargin;

        mViewPager.setPadding(partialWidth, 0, partialWidth, 0);
        mViewPager.setPageMargin(-50);

        mViewPager.setAdapter(adapter);

        final ImageButton rightButton = (ImageButton)parentView.findViewById(R.id.tips_button_back);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //next page
                if (mViewPager.getCurrentItem() < mViewPager.getAdapter().getCount() - 1) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }

            }
        });
        final ImageButton leftButton = (ImageButton)parentView.findViewById(R.id.tips_button_forward);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previous page
                if (mViewPager.getCurrentItem() > 0) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
            }
        });




        return parentView;
    }

    @Override
    public void storagePermissionGranted() {

    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_tips;
    }


    private class TipsPageAdapter extends FragmentStatePagerAdapter{

        public TipsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SingleTipFragment.init(position, mPaidCustomer);
        }

        @Override
        public int getCount() {
            if (mPaidCustomer) {
                return SingleTipFragment.PAID_CUSTOMER_COUNT;
            } else {
                return SingleTipFragment.FREE_CUSTOMER_COUNT;
            }
        }
    }


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}



