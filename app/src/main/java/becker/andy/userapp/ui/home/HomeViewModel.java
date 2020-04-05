package becker.andy.userapp.ui.home;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import becker.andy.userapp.models.UserLocation;
import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.service.LocationService;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class HomeViewModel extends ViewModel {

    public static final String TAG = HomeViewModel.class.toString();
    private boolean checked;
    private SingleLiveEvent<Boolean> toastMessageObserver = new SingleLiveEvent<>();
    private SingleLiveEvent<Location> locationObserver = new SingleLiveEvent<>();
    private MainRepository mainRepository = new MainRepository();

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public void sendStartingLocation(Location location, PrefConfig prefs){
        UserLocation userLocation = new UserLocation();
        String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        userLocation.setDate(currentDateTimeString);
        userLocation.setLatitude(String.valueOf(location.getLatitude()));
        userLocation.setLongitude(String.valueOf(location.getLongitude()));
        userLocation.setDistance("0");
        userLocation.setStatus("Active");

        mainRepository.updateLocation(userLocation, prefs);


    }

    public void sendEndingLocation(Location location, PrefConfig prefs){

        int distance = LocationService.getSomeValue()/1000;

        UserLocation userLocation = new UserLocation();
        String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        userLocation.setDate(currentDateTimeString);
        userLocation.setLatitude(String.valueOf(location.getLatitude()));
        userLocation.setLongitude(String.valueOf(location.getLongitude()));
        userLocation.setDistance(String.valueOf(distance));
        userLocation.setStatus("Active");

        Log.d(TAG, "onLocationResult Distance:"+ distance);

        mainRepository.updateLocation(userLocation, prefs);
    }

    public LiveData<Boolean> getToastMessageObserver() {
        return toastMessageObserver;
    }

    public SingleLiveEvent<Location> getLocationObserver() {
        return locationObserver;
    }

    public SingleLiveEvent<String> getLocationResponse() {
        return mainRepository.getLocationResponse();
    }
}