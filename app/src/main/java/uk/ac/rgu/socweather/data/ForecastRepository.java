package uk.ac.rgu.socweather.data;

import android.content.Context;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * This class provides the single point of truth in the app for {@link LocationForecast}s, and
 * will in the future deal with downloading, storing, and retrieving them.
 * @author  David Corsar
 */
public class ForecastRepository {

    /**
     * A field for how dates should be formatted before displaying to users
     * with the day of the month as a number, and the month as text
     */
    private static final String DATE_FORMAT = "dd MMM";

    /**
     * The Singleton instance for this repository
     */
    private static ForecastRepository INSTANCE;

    /**
     * The Context that the app is operating within
     */
    private Context context;

    /**
     * Gets the singleton {@link ForecastRepository} for use when managing {@link LocationForecast}s
     * in the app.
     * @return The {@link ForecastRepository} to be used for managing {@link LocationForecast}s in the app.
     */
    public static ForecastRepository getRepository(Context context){
        if (INSTANCE == null){
            synchronized (ForecastRepository.class) {
                if (INSTANCE == null)
                    INSTANCE = new ForecastRepository();
                    INSTANCE.context = context;
            }
        }
        return INSTANCE;
    }

    /**
     * Returns a {@link LocationForecast} for the given location name, generated at random
     * @param locationName The name of the location to return the LocationForecast for
     * @return a {@link LocationForecast} for today for the given location, with randomly generated temperature range.
     */
    public LocationForecast getForecastFor(String locationName){
        return getForecastFor(locationName, 0);
    }

    /**
     * Returns a {@link LocationForecast} for the given location name, generated at random.
     * @param locationName The name of the location to return the LocationForecast for.
     * @param  daysInTheFuture The number of days in the future to return a LocationForecast for.
     * @return a {@link LocationForecast} for the specified number of days in the future for the given location, with randomly generated temperature range.
     */
    public LocationForecast getForecastFor(String locationName, int daysInTheFuture){
        LocationForecast forecast = new LocationForecast();
        forecast.setLocationName(locationName);

        // create a random temperature range between 1 and 30
        Random random = new Random();
        int min = random.nextInt((20 -1) + 1) + 1;
        int max = (int)(min * 1.5);
        forecast.setMinTemp(min);
        forecast.setMaxTemp(max);

        // note - as a SimpleDataFormat will be created every time this method is called,
        // this is not particularly good coding style.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        long time = System.currentTimeMillis();
        time += daysInTheFuture * (1000 * 60 * 60 * 24);

        forecast.setDate(sdf.format(new Date(time)));

        // again, not the best practice here - this could be improved
        RandomWeatherForecastGetter rwf = new RandomWeatherForecastGetter(context);
        forecast.setWeather(rwf.getWeather());

        return forecast;
    }


    /**
     * Returns a {@link List} of number {@link HourForecast}s, starting from now.
     * @param number The number of hour forecasts to return - 24 will be 1 day, 48 is 2 days, etc.
     * @return a {@link List} of number {@link HourForecast}s, starting from now.
     */
    public List<HourForecast> getHourlyForecasts(int number){
        // placeholder for the forecasts to be returned
        List<HourForecast> forecasts = new ArrayList<HourForecast>(number);
        // get the current time
        Calendar now = Calendar.getInstance();
        // for setting the date of the forecasts
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        // for a random temperature
        Random random = new Random();

        // for a random weather type
        RandomWeatherForecastGetter rwf = new RandomWeatherForecastGetter(context);


        // create number of forecasts with random details, add to forecasts
        for (int i = 0; i < number; i++){
            HourForecast forecast = new HourForecast();
            forecast.setTemperature(random.nextInt(29)+1);
            forecast.setHumidity(random.nextInt(99)+1);
            forecast.setWeather(rwf.getWeather());
            // would be more efficient to only do the next line once per day
            forecast.setDate(sdf.format(now.getTime()));
            forecast.setHour(now.get(Calendar.HOUR_OF_DAY));
            now.add(Calendar.HOUR_OF_DAY, 1);
            forecasts.add(forecast);
        }

        return forecasts;
    }
}
