package uk.ac.rgu.socweather.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles converting the JSON returned by the WeatherAPI Web Services into
 * objects usable by the app.
 */
public class WeatherApiParser {

    /**
     *
     * @param jsonString The JSON String returned by the Weather API's forecast web service
     * @return A {@link List} of {@link HourForecast} objects parsed from the jsonString.
     * @throws JSONException if any error occurs with processing the jsonString
     * @throws ParseException if any error occurs with processing the jsonString
     */
    public List<HourForecast> convertForecastJson(String jsonString) throws JSONException, ParseException{
        // for processing the date coming from Weather API
        SimpleDateFormat dateInParser = new SimpleDateFormat("yyyy-MM-dd");

        // for processing Dates for display in our app
        SimpleDateFormat dateOutFormatter = new SimpleDateFormat(ForecastRepository.DATE_FORMAT);

        // for storing the parsed HourForecasts
        List<HourForecast> hourForecasts = new ArrayList<HourForecast>();

        // process response to get a list of HourForecast objects
        // in the JSON, we're interested in forecast>[forecastday]>[hour]
        try {
            JSONObject rootObject = new JSONObject(jsonString);
            JSONObject forecastObj = rootObject.getJSONObject("forecast");
            JSONArray forecastDayArray = forecastObj.getJSONArray("forecastday");

            for (int i = 0, j = forecastDayArray.length(); i < j; i++){
                JSONObject forecastDayObject = forecastDayArray.getJSONObject(i);
                JSONArray hoursArray = forecastDayObject.getJSONArray("hour");
                for (int ii = 0, jj = hoursArray.length(); ii < jj; ii++){
                    JSONObject hourObj = hoursArray.getJSONObject(ii);

                    // time is in the format YYYY-MM-DD HH:mm
                    // need to split into day and hour
                    String time = hourObj.getString("time");
                    int hour = Integer.parseInt(time.substring(11,13));
                    Date date = dateInParser.parse(time.substring(0,10));
                    String dateStr = dateOutFormatter.format(date);

                    // get the temp
                    int temp = (int)Math.round(hourObj.getDouble("temp_c"));

                    // get the humidity
                    int humidity = hourObj.getInt("humidity");

                    // get the weather from condition>text
                    JSONObject conditionObj = hourObj.getJSONObject("condition");
                    String weather = conditionObj.getString("text");

                    // create an HourForecast with the extracted information
                    HourForecast hf = new HourForecast();
                    hf.setDate(dateStr);
                    hf.setHour(hour);
                    hf.setTemperature(temp);
                    hf.setHumidity(humidity);
                    hf.setWeather(weather);
                    hourForecasts.add(hf);
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            throw e;
        }
        return hourForecasts;
    }
}
