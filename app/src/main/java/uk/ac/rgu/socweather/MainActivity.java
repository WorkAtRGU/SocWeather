package uk.ac.rgu.socweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);
        bnv.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case R.id.miAppBarSettings:
                NavController navController = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
                int currentFragmentId = navController.getCurrentDestination().getId();
                if (currentFragmentId != R.id.settingsFragment){
                    navController.navigate(R.id.settingsFragment);
                    return true;
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
        int currentFragmentId = navController.getCurrentDestination().getId();

        if (item.getItemId() == R.id.miBottomNavSettings){
            if (currentFragmentId != R.id.settingsFragment){
                navController.navigate(R.id.settingsFragment);
            }
            return true;
        } else if (item.getItemId() == R.id.miBottomNavLocationSelection){
            if (currentFragmentId != R.id.locationSelectionFragment){
                navController.navigate(R.id.locationSelectionFragment);
            }
            return true;
        }

        return false;
    }
}