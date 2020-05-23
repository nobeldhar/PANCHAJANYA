package becker.andy.userapp.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;

import becker.andy.userapp.HomeActivity;
import becker.andy.userapp.R;
import becker.andy.userapp.models.UserLocation;
import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;


public class LocationService extends Service {

    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int REQUEST_CHECK_SETTINGS = 2423;
    private Location oldLocation;
    public static int someValue = 0;
    public static final String TAG = "LocationService";
    SingleLiveEvent<Location> locationUpdate = new SingleLiveEvent<>();
    LocationCallback mCallback;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private HandlerThread mHandlerThread;
    private PrefConfig prefConfig;
    private Looper mLooper;
    private Handler mHandler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandlerThread = new HandlerThread("MyHandlerThread");
        mHandlerThread.start();
        // Now get the Looper from the HandlerThread
        // NOTE: This call will block until the HandlerThread gets control and initializes its Looper
        mLooper = mHandlerThread.getLooper();
        mHandler = new Handler(mLooper);

        prefConfig = new PrefConfig(getApplicationContext()
                .getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationAndStartForeground();
        }

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setChannelId(CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle("PANCHAJANYA")
                .setContentText("App is running in background")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeNotificationAndStartForeground() {


        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        serviceChannel.enableLights(true);
        serviceChannel.setLightColor(R.color.colorPrimaryDark);
        serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        serviceChannel.enableVibration(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        assert manager != null;
        manager.createNotificationChannel(serviceChannel);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mFusedLocationProviderClient.removeLocationUpdates(this.mCallback);
        mHandler.removeCallbacksAndMessages(null);
        mHandlerThread.quitSafely();

        updateEndinglocation();

        super.onDestroy();

    }

    private void updateEndinglocation() {
        MainRepository mainRepository = new MainRepository();
        UserLocation userLocation = new UserLocation();
        String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        userLocation.setDate(currentDateTimeString);
        userLocation.setLatitude(String.valueOf(oldLocation.getLatitude()));
        userLocation.setLongitude(String.valueOf(oldLocation.getLongitude()));

        int distance = getSomeValue() / 1000;

        userLocation.setDistance(String.valueOf(distance));
        userLocation.setStatus("Inactive");

        mainRepository.updateLocation(userLocation, prefConfig);
    }

    public static void setSomeValue(int someValue) {
        LocationService.someValue = someValue;
    }

    public static int getSomeValue() {
        return someValue;
    }

    private void getLocation() {
        final LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(60000);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }

        mCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: got location result.");

                if (location != null) {

                    MainRepository mainRepository = new MainRepository();

                    if (oldLocation != null) {
                        float value = location.distanceTo(oldLocation);
                        someValue += value;
                    } else {
                        float value = location.distanceTo(location);
                        someValue += value;
                    }

                    UserLocation userLocation = new UserLocation();
                    String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    userLocation.setDate(currentDateTimeString);
                    userLocation.setLatitude(String.valueOf(location.getLatitude()));
                    userLocation.setLongitude(String.valueOf(location.getLongitude()));

                    int distance = getSomeValue() / 1000;

                    userLocation.setDistance(String.valueOf(distance));
                    userLocation.setStatus("Active");

                    mainRepository.updateLocation(userLocation, prefConfig);


                    setOldLocation(location);
                    Log.d(TAG, "onLocationResult: Location: " + userLocation.getLatitude() + " " + userLocation.getLongitude());
                    Log.d(TAG, "onLocationResult: Distance: " + getSomeValue());
                }
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            oldLocation = location;
                        }
                    }
                });

                setSomeValue(0);
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequestHighAccuracy, mCallback, mLooper);

            }
        });


    }


    public void setOldLocation(Location oldLocation) {
        this.oldLocation = oldLocation;
    }

}
