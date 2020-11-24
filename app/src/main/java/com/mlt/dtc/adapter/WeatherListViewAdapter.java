package com.mlt.dtc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.mlt.dtc.R;
import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;
import java.util.List;

public
class WeatherListViewAdapter extends ArrayAdapter<FetchCurrentWeatherResponse.Response> implements View.OnClickListener{

    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txt_UpdateWeather;
        TextView txt_UpdateDegree;
        TextView txt_UpdateTime;
        TextView txt_City;
        TextView txt_Country;
    }

    public WeatherListViewAdapter(List<FetchCurrentWeatherResponse.Response> data, Context context) {
        super(context, R.layout.rowitemslistview_weather, data);
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        FetchCurrentWeatherResponse dataModel=(FetchCurrentWeatherResponse) object;


        if (v.getId() == 0) {
            Snackbar.make(v, "Release date ", Snackbar.LENGTH_LONG)
                    .setAction("No action", null).show();
        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FetchCurrentWeatherResponse.Response dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rowitemslistview_weather, parent, false);
            viewHolder.txt_UpdateWeather = convertView.findViewById(R.id.tv_updateweather);
            viewHolder.txt_UpdateDegree = convertView.findViewById(R.id.tv_updatedegree);
            viewHolder.txt_UpdateTime = convertView.findViewById(R.id.tv_updatetime);
            viewHolder.txt_Country = convertView.findViewById(R.id.tv_countryname);
            viewHolder.txt_City = convertView.findViewById(R.id.tv_cityname);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.txt_UpdateWeather.setText(dataModel.getWeather());
        viewHolder.txt_UpdateDegree.setText(dataModel.getTemperature() +" \u2103");
        viewHolder.txt_UpdateTime.setText(String.valueOf(dataModel.getHumidity()));
        viewHolder.txt_Country.setText(dataModel.getCountry());
        viewHolder.txt_City.setText(dataModel.getCity());
        // Return the completed view to render on screen
        return convertView;
    }


}
