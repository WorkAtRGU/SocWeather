package uk.ac.rgu.socweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import uk.ac.rgu.socweather.data.HourForecast;

public class HourForecastListItemViewAdapter extends ArrayAdapter<HourForecast> {

    public HourForecastListItemViewAdapter(@NonNull Context context, int resource, @NonNull List<HourForecast> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // get the View that will be used to display the HourForecast
        View itemView = convertView;
        if (itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.hour_forecast_list_item, parent,false);
        }

        // get the HourForecast to be displayed
        HourForecast hourForecast = getItem(position);

        // now update the TextViews in itemView with the hourForecast details
        // update the date
        TextView tvForecastDate = itemView.findViewById(R.id.tvForecastDate);
        tvForecastDate.setText(hourForecast.getDate());

        // update the time
        TextView tvForecastTime = itemView.findViewById(R.id.tvForecastTime);
        String hourStr = getContext().getString(R.string.tv_forecastItemHour, hourForecast.getHour());
        tvForecastTime.setText(hourStr);

        // update the temp
        ((TextView)itemView.findViewById(R.id.tvForecastTemp))
                .setText(
                        getContext().getString(R.string.tv_forecastItemTemp, hourForecast.getTemperature()));

        // update the humidity
        ((TextView)itemView.findViewById(R.id.tvForecastHumidity))
                .setText(
                        getContext().getString(R.string.tv_forecastItemHumidity, hourForecast.getHumidity()));

        // update the weather
        ((TextView)itemView.findViewById(R.id.tvForecastWeather))
                .setText(hourForecast.getWeather());

        return itemView;
    }
}
