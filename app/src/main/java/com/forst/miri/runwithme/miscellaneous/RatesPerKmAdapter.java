package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forst.miri.runwithme.R;

import java.util.ArrayList;

/**
 * Created by chagithazani on 10/18/17.
 */


public class RatesPerKmAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Float> ratesPerKm;
    private int color;

    public RatesPerKmAdapter(Context context, ArrayList<Float> items, int color) {
        mContext = context;
        ratesPerKm = items;
        this.color = color;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return ratesPerKm.size();
    }


    @Override
    public Object getItem(int position) {
        return ratesPerKm.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         Float rate = ratesPerKm.get(position);


        //convertView = mInflater.inflate(R.layout.rates_per_km_list_row, parent, false);



        TextView numTextView = null;
        //TextView lengthTextView = null;
        TextView rateTextView = null;

        if(color == R.color.colorPurple) {
            convertView = mInflater.inflate(R.layout.rates_per_km_list_row, parent, false);
            numTextView = (TextView) convertView.findViewById(R.id.rates_per_km_list_row_num_tv);
            //lengthTextView = (TextView) convertView.findViewById(R.id.rates_per_km_list_row_length_tv);
            rateTextView =  (TextView) convertView.findViewById(R.id.rates_per_km_list_row_avg_rate_tv);

        } else if (color == R.color.colorLightPink) {
            convertView = mInflater.inflate(R.layout.rates_per_km_list_row_pink, parent, false);
            numTextView = (TextView) convertView.findViewById(R.id.rates_per_km_list_row_num_tv);
            //lengthTextView = (TextView) convertView.findViewById(R.id.rates_per_km_list_row_length_tv);
            rateTextView =  (TextView) convertView.findViewById(R.id.rates_per_km_list_row_avg_rate_tv);

        }

        //String lengthText = String.format(mContext.getString(R.string.km_heb) + "1.00");
        String rateText = String.format(mContext.getString(R.string.minute_per_km_heb) + ": %1$:2f",rate);

        numTextView.setText(String.valueOf(position + 1));
        //lengthTextView.setText(lengthText);
        rateTextView.setText(rateText);



        return convertView;
    }

}
