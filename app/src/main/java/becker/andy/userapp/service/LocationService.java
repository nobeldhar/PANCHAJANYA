package becker.andy.userapp.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;

import becker.andy.userapp.HomeActivity;
import becker.andy.userapp.R;
import becker.andy.userapp.utils.SingleLiveEvent;

import static becker.andy.userapp.service.App.CHANNEL_ID;

public class LocationService extends Service implements OnSuccessListener<Location> {

    private Location oldLocation;
    public static int someValue = 0;
    public static final String TAG = "LocationService";
    SingleLiveEvent<Location> locationUpdate = new SingleLiveEvent<>();
    LocationCallback callback;
    FusedLocationProviderClient mFusedLocationProviderClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this);

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("PANCHAJANYA")
                .setContentText("App is running in background")
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setSomeValue(0);
        mFusedLocationProviderClient.removeLocationUpdates(this.callback);

    }

    public static void setSomeValue(int someValue) {
        LocationService.someValue = someValue;
    }

    public static int getSomeValue() {
        return someValue;
    }

    private void getLocation() {
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(300000);
        mLocationRequestHighAccuracy.setFastestInterval(240000);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }


        this.callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: got location result.");

                if (location != null) {

                    DecimalFormat decimalFormat = new DecimalFormat("#.###");
                    Location tempLocation = new Location(location);
                    tempLocation.setLatitude(Double.valueOf(decimalFormat.format(location.getLatitude())));
                    tempLocation.setLongitude(Double.valueOf(decimalFormat.format(location.getLongitude())));

                    float value = tempLocation.distanceTo(oldLocation);
                    someValue += value;
                    setOldLocation(tempLocation);
                    Log.d(TAG, "onLocationResult: Location: " + tempLocation.getLatitude() + " " + tempLocation.getLongitude());
                    Log.d(TAG, "onLocationResult: Distance: " + someValue);
                }
            }
        };

        setSomeValue(0);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequestHighAccuracy, this.callback, Looper.myLooper());


    }


    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            oldLocation = location;
        }
    }

    public void setOldLocation(Location oldLocation) {
        this.oldLocation = oldLocation;
    }

}
