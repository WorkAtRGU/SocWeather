package uk.ac.rgu.socweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.rgu.socweather.data.HourForecast;

public class HourForecastRecyclerViewAdapter extends RecyclerView.Adapter<HourForecastRecyclerViewAdapter.HourForecastViewHolder> {

    private final Context context;
    private final List<HourForecast> hourForecasts;

    HourForecastRecyclerViewAdapter(Context context, List<HourForecast> hourForecasts){
        super();
        this.context = context;
        this.hourForecasts = hourForecasts;
    }

    @NonNull
    @Override
    public HourForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the layout for the hour forecast
        View view = LayoutInflater.from(this.context).inflate(R.layout.hour_forecast_list_item, parent, false);
        HourForecastViewHolder viewHolder = new HourForecastViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HourForecastViewHolder holder, int position) {
        // get the HourForecast to display
        HourForecast hourForecast = this.hourForecasts.get(position);

        // update the View being held by holder with details of hourForecast
        View itemView = holder.itemView;

        TextView tvForecastDate = itemView.findViewById(R.id.tvForecastDate);
        tvForecastDate.setText(hourForecast.getDate());

        // update the time
        TextView tvForecastTime = itemView.findViewById(R.id.tvForecastTime);
        String hourStr = context.getString(R.string.tv_forecastItemHour, hourForecast.getHour());
        tvForecastTime.setText(hourStr);

        // update the temp
        ((TextView)itemView.findViewById(R.id.tvForecastTemp))
                .setText(
                        context.getString(
                                R.string.tv_forecastItemTemp, hourForecast.getTemperature()));

        // update the humidity
        ((TextView)itemView.findViewById(R.id.tvForecastHumidity))
                .setText(
                        context.getString(R.string.tv_forecastItemHumidity, hourForecast.getHumidity()));

        // update the weather
        ((TextView)itemView.findViewById(R.id.tvForecastWeather))
                .setText(hourForecast.getWeather());

    }

    @Override
    public int getItemCount() {
        return this.hourForecasts.size();
    }

    class HourForecastViewHolder extends RecyclerView.ViewHolder {

        public HourForecastViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
