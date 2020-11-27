package com.forst.miri.runwithme.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.forst.miri.runwithme.BuildConfig;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.UIHelper;
import com.forst.miri.runwithme.miscellaneous.ViewPagerAdapter;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.PracticeData;
import com.forst.miri.runwithme.widges.OutputDataBox;
import com.google.android.gms.maps.GoogleMap;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeDataFragment extends ParentFragment {


    public static final String FRAGMENT_TAG = PracticeDataFragment.class.getSimpleName();

    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;
    private int mDelta, mPracticeNum, mTemperature;
    private OutputDataBox durationBox, lengthBox, rateBox, calorieBox;
    private PathFragment pathFragment = null;
    private RatePerKmFragment ratePerKMTableFragment = null;
    private RateAndHeightsGraphFragment graphFragment = null;
    private PracticeData mPracticeData = null;
    private TextView tempTextView, dateTextView, timeTextView;
    private ImageButton bBack;
    private FloatingActionButton bShare;
    public static String P_DATA_TO_PASS = "p_data_to_pass_";

    public PracticeDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View ParentView = inflater.inflate(R.layout.fragment_practice_data, container, false);


        mPracticeData = PracticeData.readFromSharedPreferences(getContext(), P_DATA_TO_PASS);



        CircleImageView userImage = (CircleImageView) ParentView.findViewById(R.id.pink_data_layout_circle_image);
        try {
            Bitmap image = ConnectedUser.getInstance().getImage();
            userImage.setImageBitmap(image);

        }catch (Exception e){
            e.printStackTrace();
            userImage.setVisibility(View.GONE);
        }

        mViewPager = (ViewPager) ParentView.findViewById(R.id.tabbed_run_data_layout_viewpager_pink);
        mTabLayout = (TabLayout) ParentView.findViewById(R.id.tabbed_run_data_layout_tabs_pink);

        pathFragment = new PathFragment();
        ratePerKMTableFragment = new RatePerKmFragment();
//        ratePerKMTableFragment.setColor(R.color.colorLightPink);
        graphFragment = new RateAndHeightsGraphFragment();

        final String[] tabTitles = {getString(R.string.title_path), getString(R.string.title_rate_table), getString(R.string.title_graph)};
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(pathFragment, getString(R.string.title_path));
        adapter.addFragment(ratePerKMTableFragment, getString(R.string.title_rate_table));
        adapter.addFragment(graphFragment, getString(R.string.title_graph));


        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        int size = mViewPager.getAdapter().getCount();
        for (int i = 0; i < size; i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            SpannableString sb = new SpannableString(tabTitles[i]);
            sb.setSpan(new RelativeSizeSpan(0.6f), 0, sb.length(), 0);
            if (i == 0) { // setting first tab to look selected
                sb.setSpan(new StyleSpan(Typeface.BOLD), 0, sb.length(), 0);
            }
            tab.setText(sb);
        }

        durationBox = (OutputDataBox) ParentView.findViewById(R.id.pink_data_layout_output_box_time);
        lengthBox = (OutputDataBox) ParentView.findViewById(R.id.pink_data_layout_output_box_length);
        rateBox = (OutputDataBox) ParentView.findViewById(R.id.pink_data_layout_output_box_rate);
        calorieBox = (OutputDataBox) ParentView.findViewById(R.id.pink_data_layout_output_box_kcl);

        tempTextView = (TextView) ParentView.findViewById(R.id.pink_data_layout_tv_temperature);
        dateTextView = (TextView) ParentView.findViewById(R.id.pink_data_layout_tv_date);
        timeTextView = (TextView) ParentView.findViewById(R.id.pink_data_layout_tv_time);

        bBack = (ImageButton) ParentView.findViewById(R.id.pink_data_layout_back_arrow);
        bShare = (FloatingActionButton) ParentView.findViewById(R.id.pink_data_layout_share_button);

//        setFragmentWithData();



        return ParentView;
    }




    @Override
    public void onResume() {

        super.onResume();
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAndShareScreenShot();
            }
        });
        if(mPracticeData == null){
            //notify user that a problem rendered
            Toast.makeText(getActivity(), getString(R.string.problem_with_renderring_heb), Toast.LENGTH_LONG).show();
            finish();
        } else {
            setFragmentWithData();
        }
    }

    private void finish(){
        try {
            if (getActivity() != null) {
                if(mPracticeData != null) mPracticeData.eraseFromSharedPreferences(getContext(), P_DATA_TO_PASS);
                ((MainActivity) getActivity()).onBackPressed();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private void takeAndShareScreenShot(/*View rootView*/) {
        final View rootView  = getActivity().getWindow().getDecorView().getRootView();

        if (mTabLayout.getSelectedTabPosition() == 0) {
            takeAndShareScreenShotWithMap(rootView);
        } else {
            takeAndShareScreenShotWithOutMap(rootView);
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_SEND);
        Uri outputFileUri;
        if(Build.VERSION.SDK_INT > M) {
            //Uri photoURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", createImageFile());
            //outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + getString(R.string.file_provider), imageFile);
            outputFileUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + getString(R.string.file_provider), imageFile);
//            intent.putExtra(Intent.EXTRA_STREAM, outputFileUri);
//            intent.setType("image/jpeg");
//            startActivity(Intent.createChooser(intent, getString(R.string.share_heb)));
        } else {
            outputFileUri = Uri.fromFile(imageFile);
//            intent.setDataAndType(outputFileUri, "image/*");
//            startActivity(intent);
        }

        intent.putExtra(Intent.EXTRA_STREAM, outputFileUri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, getString(R.string.share_heb)));
    }

    private void takeAndShareScreenShotWithMap(final View rootView){

        final int height = rootView.getHeight();
        final int[] mapPosition = new int[2];
        //final View logo = rootView.findViewById(R.id.run_data_layout_pink_logo);
        //final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)logo.getLayoutParams();
        //logo.setVisibility(View.INVISIBLE);
        final int[] mapWidthAndHeight = pathFragment.getMapFragmentSize();
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
                    root.mkdirs();
                    final String fname = System.currentTimeMillis() + ".png";
                    final File sdImageFileInPicturesDirectory = new File(root, fname);

                    bShare.setVisibility(View.INVISIBLE);
                    bBack.setVisibility(View.INVISIBLE);



                    rootView.setDrawingCacheEnabled(true);
                    Bitmap backBitmap = rootView.getDrawingCache();
                    Bitmap bmOverlay = Bitmap.createBitmap(backBitmap.getWidth(), backBitmap.getHeight(),backBitmap.getConfig());

                    //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.logo_for_pic);
                    //Bitmap logoBitmap = Bitmap.createScaledBitmap(b, logo.getWidth(), logo.getHeight(), false);

                    Bitmap mapBitmap = Bitmap.createScaledBitmap(snapshot, mapWidthAndHeight[0], mapWidthAndHeight[1], false);

                    Canvas canvas = new Canvas(bmOverlay);
                    canvas.drawBitmap(backBitmap, new Matrix(), null);
                    canvas.drawBitmap(mapBitmap, mapPosition[0], mapPosition[1], null);
                    //canvas.drawBitmap(logoBitmap , logo.getLeft() , logo.getTop() , null);

                    rootView.setDrawingCacheEnabled(false);
                    bShare.setVisibility(View.VISIBLE);
                    bBack.setVisibility(View.VISIBLE);

                    FileOutputStream out = new FileOutputStream( sdImageFileInPicturesDirectory );
                    bmOverlay.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();

                    openScreenshot(sdImageFileInPicturesDirectory);
                } catch (Exception e) {
                    e.printStackTrace();
                    Dialogs.getAlertDialog(getActivity(), getString(R.string.can_not_share_image) + '\n' + e.getMessage(), getString(R.string.sorry_heb)).show();
                }
            }
        };

        GoogleMap map = pathFragment.getMap();
        if(map != null) {
            map.snapshot(callback);
            pathFragment.getMapPositionOnView(mapPosition);
        }


    }

    public void setFragmentWithData() {
        if(mPracticeData != null) {
            durationBox.setValueText(String.valueOf(UIHelper.formatTime(mPracticeData.getDuration())));
            lengthBox.setValueText(String.format("%1$.2f", mPracticeData.getDistanceInKm()));
            rateBox.setValueText(String.valueOf(UIHelper.formatTime(mPracticeData.getAvgRate())));
            calorieBox.setValueText(String.valueOf(mPracticeData.getKcl()));

            tempTextView.setText(mPracticeData.getTemperatureAsString());
            dateTextView.setText(UIHelper.formatDate(mPracticeData.getDate()));
            timeTextView.setText(UIHelper.formatDateToTimeOnly(mPracticeData.getDate()));

            //pathFragment.setPracticeData(this.mPracticeData);
            pathFragment.setPracticeData(this.mPracticeData.getLocations());
            graphFragment.setPracticeData(null);
            ratePerKMTableFragment.setRatesArray(this.mPracticeData.getRatesPerKM());
        }
    }

    private Bitmap takeAndShareScreenShotWithOutMap(View rootView) {


        try {
            // Determine Uri of camera image to save.
            final File root = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES)));
            root.mkdirs();
            final String fname = System.currentTimeMillis() + "IMG";
            final File sdImageFileInPicturesDirectory = new File(root, fname);
//            Uri outputFileUri;



            // create bitmap screen capture
            //View v1 = getWindow().getDecorView().getRootView();

            bShare.setVisibility(View.INVISIBLE);
            bBack.setVisibility(View.INVISIBLE);
            rootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);
            bShare.setVisibility(View.VISIBLE);
            bBack.setVisibility(View.VISIBLE);
            //File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(sdImageFileInPicturesDirectory);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(sdImageFileInPicturesDirectory);
            return bitmap;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }


        return null;
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
        return R.layout.fragment_practice_data;
    }

//    private void openScreenshot(File imageFile) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        Uri outputFileUri;
//        if(Build.VERSION.SDK_INT > M) {
//            outputFileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + getString(R.string.file_provider), imageFile);
//        } else {
//            outputFileUri = Uri.fromFile(imageFile);
//        }
//
//        intent.setDataAndType(outputFileUri, "image/*");
//        startActivity(intent);
//    }

    // for setting status bar fully transparent.
//    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
//        Window win = activity.getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }




}
