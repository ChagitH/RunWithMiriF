package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forst.miri.runwithme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chagithazani on 10/24/17.
 */

public class RateListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Long> rates = new ArrayList<>();
//    private int color;

    public RateListAdapter(Context context, List<Long> rates){//}, int color) {
        mContext = context;
        this.rates.addAll(rates);
//        this.color = color;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return rates.size();
    }


    @Override
    public Object getItem(int position) {
        return rates.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String numText = null;
        //String lengthText = null;
        String rateText = null;

        if(position == 0){
            numText = mContext.getString(R.string.num_of_km_heb);
            //lengthText = mContext.getString(R.string.length_heb);
            rateText = mContext.getString(R.string.rate_heb);
        } else {
            Long rate = rates.get(position);

            numText = String.valueOf(position);
            //lengthText = String.format(" 1.00 " + mContext.getString(R.string.km_heb));
            rateText = UIHelper.formatTimeToMinutes(rate).concat( " " + mContext.getString(R.string.minute_per_km_heb) );

        }
        //convertView = mInflater.inflate(R.layout.rates_per_km_list_row, parent, false);

        TextView numTextView = null;
        TextView rateTextView = null;

//        if(color == R.color.colorPurple) {
//            convertView = mInflater.inflate(R.layout.rates_per_km_list_row, parent, false);
//            numTextView = (TextView) convertView.findViewById(R.id.rates_per_km_list_row_num_tv);
//            rateTextView =  (TextView) convertView.findViewById(R.id.rates_per_km_list_row_avg_rate_tv);
//
//        } else if (color == R.color.colorLightPink) {
            convertView = mInflater.inflate(R.layout.rates_per_km_list_row_pink, parent, false);
            numTextView = (TextView) convertView.findViewById(R.id.rates_per_km_list_row_num_tv);
            rateTextView =  (TextView) convertView.findViewById(R.id.rates_per_km_list_row_avg_rate_tv);

//        }

        numTextView.setText(numText);
        rateTextView.setText(rateText);


        return convertView;
    }


    public void setData(ArrayList<Long> newRates) {
        if(rates != null) {
            this.rates.clear();
            this.rates.add(Long.valueOf(0)); //just for first row
            this.rates.addAll(newRates);
            notifyDataSetChanged();
        }
    }
}

