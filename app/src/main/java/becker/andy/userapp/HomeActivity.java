package becker.andy.userapp;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import becker.andy.userapp.models.User;
import becker.andy.userapp.service.LocationService;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.viewmodel.HomeActivityViewModel;

public class HomeActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 9002;
    private static final String TAG = HomeActivity.class.toString();
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private AppBarConfiguration mAppBarConfiguration;
    private PrefConfig prefs;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDToggle;
    private BottomNavigationView bottomNav;
    private HomeActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = new PrefConfig(getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);

        drawer = findViewById(R.id.drawer_layout);

        mDToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.nav_drawer_open,
                R.string.nav_drawer_close);

        drawer.addDrawerListener(mDToggle);
        mDToggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.user_name_head);
        name.setText(prefs.readUser().getName());
        TextView email = headerView.findViewById(R.id.user_email_head);
        email.setText(prefs.readUser().getEmail());
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_leave,
                R.id.nav_upload_image, R.id.nav_task)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setItemHorizontalTranslationEnabled(true);
        NavigationUI.setupWithNavController(bottomNav, navController);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(this);


        viewModel.getLogoutResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("User Logged Out")){
                    viewModel.onLogoutEnd(true);
                    logoutUser();
                }else if (s.equals("Network Error!")){
                    viewModel.onLogoutEnd(true);
                    Toast.makeText(HomeActivity.this,s,Toast.LENGTH_LONG).show();
                } else {
                    viewModel.onLogoutEnd(true);
                    Toast.makeText(HomeActivity.this,s,Toast.LENGTH_LONG).show();
                    Toast.makeText(HomeActivity.this,"Clear app data or Reinstall",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller,
                                     @NonNull NavDestination destination,
                                     @Nullable Bundle arguments) {
        if(destination.getId() == R.id.nav_upload_image){
            bottomNav.getMenu().getItem(0).setChecked(true);
            bottomNav.setVisibility(View.VISIBLE);
        }
        else if(destination.getId()==R.id.nav_upload_audio||
                destination.getId()==R.id.nav_upload_text){
            bottomNav.setVisibility(View.VISIBLE);
        }else{
            bottomNav.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                viewModel.onLogoutClicked(true);
                if(isNetworkConnected()){
                    if(isMyServiceRunning(LocationService.class)){
                        stopLocationService();
                    }else {
                        viewModel.logoutUser(prefs);
                    }
                }else {
                    viewModel.onLogoutEnd(false);
                }

            default:
                return true;
        }
    }

    private void stopLocationService() {
        Intent serviceIntent = new  Intent(this, LocationService.class);
        stopService(serviceIntent);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                viewModel.logoutUser(prefs);
            }
        }, 5000);
    }

    private void logoutUser() {

        prefs.writeLoginStatus(false);
        prefs.writeToggleState(false);

        User user = new User();
        prefs.writeUser(user);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        assert conMgr != null;
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (isLocationPermissionGranted()) {
                if(!isNetworkConnected()){
                    Toast.makeText(HomeActivity.this,"Turn on Data or Wifi", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "onResume: ");

            } else {
                getLocationPermission();
            }
        }
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.FOREGROUND_SERVICE},
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }

    }

    private boolean isLocationPermissionGranted() {
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        &&
                        ContextCompat.checkSelfPermission(this.getApplicationContext(),
                                Manifest.permission.FOREGROUND_SERVICE)
                                == PackageManager.PERMISSION_GRANTED;
            }
            else
                return true;
        }
        else {
            return false;
        }
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            return isGPSEnabled();
        }
        return false;
    }

    private boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                assert manager != null;
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                }
            }
        }
    }


}
