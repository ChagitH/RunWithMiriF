package com.forst.miri.runwithme.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.activities.MainActivity;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.RunsListAdapter;
import com.forst.miri.runwithme.miscellaneous.SQLiteDatabaseHandler;
import com.forst.miri.runwithme.objects.ConnectedUser;
import com.forst.miri.runwithme.objects.PracticeData;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllRunsDataFragment extends ParentFragment {

    public static final String FRAGMENT_TAG = AllRunsDataFragment.class.getSimpleName();
    private static final int FETCH_SUM = 15;
    private static String PRACTICES_ARRAY = "all_practices_array";
    public static String RE_FETCH_PDs = "re_fetch_pds";

    private RunsListAdapter adapter;

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }


    private CircleImageView userImage;
    private ListView runsListView;
//    private boolean refetchPDs = false;

    private boolean isFetching = false;

    public AllRunsDataFragment() {
        // Required empty public constructor
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_all_runs_data;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if(savedInstanceState != null){
//            mPractices = savedInstanceState.getParcelableArrayList(ALL_PRACTICES);
//        } else {
//            Bundle b = getArguments();
//            if (b != null) {
//                refetchPDs = b.getBoolean(RE_FETCH_PDs);
//            }
//        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_runs_data, container, false);
        userImage = (CircleImageView) rootView.findViewById(R.id.all_runs_fragment_small_image_circle);
        if(ConnectedUser.getInstance() != null && ConnectedUser.getInstance().getImage() != null){
            userImage.setImageBitmap(ConnectedUser.getInstance().getImage());
        } else {
            userImage.setVisibility(View.INVISIBLE);
        }


        runsListView = (ListView) rootView.findViewById(R.id.all_runs_list_view);
        runsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PracticeData selectedPracticeData = (PracticeData)runsListView.getAdapter().getItem(position);

                selectedPracticeData.writeToSharedPreferences(getActivity().getApplicationContext(), PracticeDataFragment.P_DATA_TO_PASS, true);

                if(getActivity() != null) {
                    ((MainActivity) getActivity()).replaceFragment(PracticeDataFragment.FRAGMENT_TAG, null, false);
                }
            }

        });


//        runsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.i("ListView- OnScroll()", " ^v^v^v^v^ firstVisibleItem " + firstVisibleItem + " visibleItemCount " + visibleItemCount + " totalItemCount " + totalItemCount +  " getLastVisiblePosition() " + view.getLastVisiblePosition());
//                if(mPractices == null) {
//                    fetchPracticesFromLocalDataBase(0, FETCH_SUM);
//                } else {
//
//                    if (totalItemCount > 0 &&  view.getLastVisiblePosition() == totalItemCount - 1) {
//                        Log.i("ListView- OnScroll()", " ^v^v^v^ ^v^v^v^ ^v^v^v^ GOING TO FETCH!!! ^v^v^v^ ^v^v^v^ ^v^v^v^");
//                        //if(visibleItemCount > 0 && totalItemCount > 0 && visibleItemCount >= totalItemCount - 2){
//                        fetchPracticesFromLocalDataBase(mPractices.size(), FETCH_SUM);
////                    fetchPracticesFromLocalDataBase(totalItemCount, totalItemCount + FETCH_SUM);
//                    }
//                }
//            }
//        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.all_runs_fragment_pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    private void refreshData() {
        mPractices = null;
        fetchPracticesFromLocalDataBase(0, FETCH_SUM);
    }

    @Override
    public void storagePermissionGranted() {
        if(ConnectedUser.getInstance() != null && ConnectedUser.getInstance().getImage() != null){
            userImage.setImageBitmap(ConnectedUser.getInstance().getImage());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mPractices != null){
            adapter = new RunsListAdapter(getContext(), mPractices);
            runsListView.setAdapter(adapter);
        }

        runsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("ListView- OnScroll()", " ^v^v^v^v^ firstVisibleItem " + firstVisibleItem + " visibleItemCount " + visibleItemCount + " totalItemCount " + totalItemCount +  " getLastVisiblePosition() " + view.getLastVisiblePosition());
                if(mPractices == null) {
                    fetchPracticesFromLocalDataBase(0, FETCH_SUM);
                } else {

                    if (totalItemCount > 0 &&  view.getLastVisiblePosition() == totalItemCount - 1) {
                        Log.i("ListView- OnScroll()", " ^v^v^v^ ^v^v^v^ ^v^v^v^ GOING TO FETCH!!! ^v^v^v^ ^v^v^v^ ^v^v^v^");
                        //if(visibleItemCount > 0 && totalItemCount > 0 && visibleItemCount >= totalItemCount - 2){
                        fetchPracticesFromLocalDataBase(mPractices.size(), FETCH_SUM);
//                    fetchPracticesFromLocalDataBase(totalItemCount, totalItemCount + FETCH_SUM);
                    }
                }
            }
        });
    }

    List<PracticeData> mPractices = null;

    private void fetchPracticesFromLocalDataBase(final int from, final int numOfRows) {
        if (isFetching == true) return; //already processing

        isFetching = true;
        if(mPractices == null) {
            mPractices = new ArrayList<>();
            adapter = new RunsListAdapter(getContext(), mPractices);
            runsListView.setAdapter(adapter);
        }

        Dialog waitD = Dialogs.showSpinningWheelDialog(getActivity());
        runsListView.setEnabled(false);
        waitD.show();

        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(getContext());
        List<PracticeData> nextPractices = db.getPracticesFromTo(from, numOfRows);

        if(nextPractices != null && nextPractices.size() > 0) {
            mPractices.addAll(nextPractices);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new RunsListAdapter(getContext(), mPractices);
                runsListView.setAdapter(adapter);
            }
        }
        isFetching = false;
        if (waitD != null && waitD.isShowing()) waitD.dismiss();
        runsListView.setEnabled(true);

//        new AsyncTask<Void, Void, List<PracticeData>>() {
//
//            @Override
//            protected List<PracticeData> doInBackground(Void[] objects) {
//
//
//                SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(getContext());
//                //User user = User.createUserFromSharedPreferences(getContext(), true);
//                //if (user == null) return null;
//                return db.getAllPractices();//getContext()/*, user.getEmail()*/, from , numOfRows);
////                return db.getAllPractices(getContext(), user.getEmail());
//
//            }
//
//            @Override
//            protected void onPostExecute(List<PracticeData> list) {
//                super.onPostExecute(list);
//                if (list != null) {
//                    mPractices = new ArrayList<PracticeData>(list);
//
//                    //mPractices.addAll(list);
//
//                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        adapter = new RunsListAdapter(getContext(), mPractices);
//                        runsListView.setAdapter(adapter);
//                    }
//                }
//                isFetching = false;
//                if (waitD != null && waitD.isShowing()) waitD.dismiss();
//                runsListView.setEnabled(true);
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //db.close();
    }


}



