package uk.ac.rgu.socweather.data;

import android.app.Activity;
import android.content.Context;

import java.util.Random;

import uk.ac.rgu.socweather.R;

public class RandomWeatherForecastGetter {

    // array of Strings describing the weather, to be lazy initialised
    private String[] weather;

    // for creating random numbers
    private Random random;

    /**
     * Creates anew RandomFeatherForecastGetter,
     *
     * @param context The application {@link Context}, the best way to get this is via {@link Activity#getApplicationContext()}
     */
    public RandomWeatherForecastGetter(Context context) {
        super();
        // initialise weather if it hasn't already been
        if (weather == null) {
            weather = new String[]{
                    context.getString(R.string.weather_sun),
                    context.getString(R.string.weather_rain),
                    context.getString(R.string.weather_snow),
                    context.getString(R.string.weather_windy),
            };
        }

        // initialise the random
        this.random = new Random();
    }

    /**
     * Returns a String describing the weather. These Strings are initialised from the String resources by the {@link RandomWeatherForecastGetter}.
     *
     * @return A random String describing the weather
     */
    public String getWeather() {
        // create a random number between 0 and 3
        int r = random.nextInt(4);
        // return the String at index r from the weather array
        return weather[r];
    }

}
