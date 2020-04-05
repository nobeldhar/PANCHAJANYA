package becker.andy.userapp.viewmodel;

import android.location.Location;
import android.widget.CompoundButton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import becker.andy.userapp.models.UserLocation;
import becker.andy.userapp.repository.MainRepository;
import becker.andy.userapp.utils.SingleLiveEvent;

public class MainViewModel extends ViewModel {
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


    public void onClickToggle(CompoundButton button, boolean clicked){

        if(button.isPressed()){
            setChecked(clicked);
            toastMessageObserver.setValue(clicked);

        }
    }

    public void sendStartingLocation(Location location){
        UserLocation userLocation = new UserLocation();
        String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        userLocation.setDate(currentDateTimeString);
        userLocation.setLatitude(String.valueOf(location.getLatitude()));
        userLocation.setLongitude(String.valueOf(location.getLongitude()));
        userLocation.setDistance("0");
        userLocation.setStatus("Active");



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
