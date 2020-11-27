package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.objects.PracticeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chagithazani on 10/18/17.
 */


public class RunsListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<PracticeData> practices;
    private android.text.format.DateFormat dateFromatter;

    public RunsListAdapter(Context context, List<PracticeData> practices) {
        mContext = context;
        if(practices != null) {
            this.practices = practices;
        } else {
            this.practices = new ArrayList<PracticeData>();
        }

        if(this.mContext == null || this.practices == null) return;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return practices.size();
    }


    @Override
    public Object getItem(int position) {
        return practices.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PracticeData pData = practices.get(position);

        ViewHolder holder;

        if(convertView == null) {


            convertView = mInflater.inflate(R.layout.list_item_run, parent, false);


            holder = new ViewHolder();
            holder.dateTextView = (TextView) convertView.findViewById(R.id.item_run_data_date_tv);
            holder.lengthTextView = (TextView) convertView.findViewById(R.id.item_run_data_length_tv);
            holder.durationTextView = (TextView) convertView.findViewById(R.id.item_run_data_duration_tv);

            convertView.setTag(holder);
        }
        else{

            holder = (ViewHolder) convertView.getTag();
        }

        TextView dateTextView = holder.dateTextView;
        TextView lengthTextView = holder.lengthTextView;
        TextView durationTextView =  holder.durationTextView;

        dateTextView.setText(dateFromatter.format("dd.MM.yyyy", pData.getDate()));
        double lengthInKm = pData.getDistanceInMeters()/1000f;
        lengthTextView.setText(String.format("%.2f", lengthInKm) + " " + mContext.getString(R.string.km_heb));
        durationTextView.setText(UIHelper.formatTime(pData.getDuration()));

        return convertView;
    }
    private static class ViewHolder {
        public TextView dateTextView;
        public TextView lengthTextView;
        public TextView durationTextView;
    }
}
