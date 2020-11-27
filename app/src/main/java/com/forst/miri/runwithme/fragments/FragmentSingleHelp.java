package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forst.miri.runwithme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSingleHelp extends Fragment {


    public FragmentSingleHelp() {
        // Required empty public constructor
    }



    public static String POSITION_TAG = "position_help_tag";
    public static int COUNT = 5;
    int mPosition;
    private ImageView image = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_single_help, container, false);
        image = (ImageView)parentView.findViewById(R.id.single_help_fragment_image_view);

        image.setImageResource(getImageResId(mPosition));
        return parentView;
    }

    public static int getImageResId(int position) {
        switch (position){
            case 4:
                return R.drawable.update_details_screen;
            case 3:
                return R.drawable.help_screen_reminders;
            case 2:
                return R.drawable.help_screen_register_to_program;
            case 1:
                return R.drawable.help_post_running;
            case 0:
                return R.drawable.help_while_running;
            default:
                return R.drawable.help_screen_register_to_program;

        }
    }

    public static int getThumbResId(int position) {
        switch (position){
            case 4:
                return R.drawable.thumb_update_details_screen;
            case 3:
                return R.drawable.thumb_screen_reminders;
            case 2:
                return R.drawable.thumb_screen_register_to_program;
            case 1:
                return R.drawable.thumb_post_run_screen;
            case 0:
                return R.drawable.thumb_screen_while_running;
            default:
                return R.drawable.thumb_screen_register_to_program;

        }
    }


    static FragmentSingleHelp init(int position) {
        FragmentSingleHelp helpFrag = new FragmentSingleHelp();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt(POSITION_TAG, position);
        helpFrag.setArguments(args);
        return helpFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments() != null ? getArguments().getInt(POSITION_TAG) : 1;
    }



//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        image.getDrawable().
//        bitmap.recycle();
//        bitmap = null;
//    }

}
