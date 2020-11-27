package com.forst.miri.runwithme.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.forst.miri.runwithme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = HelpFragment.class.getSimpleName();

    private ViewPager mViewPager_big;
    private LinearLayout thumbnailsContainer;


    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_help, container, false);

        mViewPager_big = (ViewPager) parentView.findViewById(R.id.help_fragment_view_pager);
        thumbnailsContainer = (LinearLayout) parentView.findViewById(R.id.help_fragment_thumb_ll);

        HelpPageAdapter_Big bigAdapter = new HelpPageAdapter_Big(getChildFragmentManager());



        mViewPager_big.setPageTransformer(true, new ZoomOutPageTransformer());


        mViewPager_big.setAdapter(bigAdapter);



        final ImageButton rightButton = (ImageButton)parentView.findViewById(R.id.help_fragment_view_pager_right_button);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //next page
                if (mViewPager_big.getCurrentItem() < mViewPager_big.getAdapter().getCount() - 1) {
                    mViewPager_big.setCurrentItem(mViewPager_big.getCurrentItem() + 1);
                }

            }
        });
        final ImageButton leftButton = (ImageButton)parentView.findViewById(R.id.help_fragment_view_pager_left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previous page
                if (mViewPager_big.getCurrentItem() > 0) {
                    mViewPager_big.setCurrentItem(mViewPager_big.getCurrentItem() - 1);
                }
            }
        });
        leftButton.setVisibility(View.INVISIBLE);

        mViewPager_big.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    rightButton.setVisibility(View.VISIBLE);
                    leftButton.setVisibility(View.INVISIBLE);
                } else {
                    if(position == mViewPager_big.getAdapter().getCount() - 1){
                        rightButton.setVisibility(View.INVISIBLE);
                        leftButton.setVisibility(View.VISIBLE);
                    } else {
                        rightButton.setVisibility(View.VISIBLE);
                        leftButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        inflateThumbnails();
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
        return R.layout.fragment_help;
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

    private class HelpPageAdapter_Big extends FragmentStatePagerAdapter {

        public HelpPageAdapter_Big(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentSingleHelp.init(position);
        }

        @Override
        public int getCount() {
            return FragmentSingleHelp.COUNT;
        }

    }

    private void inflateThumbnails() {
        for (int i = 0; i < FragmentSingleHelp.COUNT; i++) {
            View imageLayout = getLayoutInflater().inflate(R.layout.help_thumbnail_layout, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_thumb);
            imageView.setOnClickListener(onChagePageClickListener(i));
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 3;
            options.inDither = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), FragmentSingleHelp.getThumbResId(i), options );
            imageView.setImageBitmap(bitmap);
            //set to image view
            //imageView.setImageBitmap(bitmap);
            //add imageview
            thumbnailsContainer.addView(imageLayout, i);
        }
    }

    private View.OnClickListener onChagePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager_big.setCurrentItem(i);
            }
        };
    }
}
