package uk.ac.rgu.socweather.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object for {@link HourForecast} entities.
 */
@Dao
public interface HourForecastDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(HourForecast hourForecast);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<HourForecast> hourForecast);

    @Delete
    public void delete(HourForecast hourForecast);

    @Delete
    public void delete(List<HourForecast> hourForecast);

    @Query("SELECT * FROM HourForecast WHERE location = :location AND date = :date")
    public List<HourForecast> findByLocationDate(String location, String date);
}
