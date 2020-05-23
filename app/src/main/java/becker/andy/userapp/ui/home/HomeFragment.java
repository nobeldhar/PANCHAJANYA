package becker.andy.userapp.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentHomeBinding;
import becker.andy.userapp.service.LocationService;
import becker.andy.userapp.utils.NetworkStateReciever;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.viewmodel.HomeActivityViewModel;

public class HomeFragment extends Fragment implements NetworkStateReciever.NetworkListener{

    private static final String TAG = "HomeFragment";
    private static final int REQUEST_CHECK_SETTINGS = 2325;
    private HomeViewModel viewModel;
    private HomeActivityViewModel homeActivityViewModel;
    private FragmentHomeBinding binding;
    private PrefConfig prefs;
    private NetworkStateReciever mNetworkStateReciever;
    private boolean clicked;
    private Activity activity;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.setViewmodel(viewModel);

        homeActivityViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(HomeActivityViewModel.class);
        homeActivityViewModel.getOnLogoutClicked().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(getActivity(),"Logging out...", Toast.LENGTH_LONG).show();
                    binding.loadingHome.setVisibility(View.VISIBLE);
                    setClicked(false);
                }
            }
        });
        homeActivityViewModel.getOnLogoutEnd().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    binding.loadingHome.setVisibility(View.GONE);
                else {
                    binding.loadingHome.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Network error!", Toast.LENGTH_LONG).show();
                }
            }
        });

        activity = getActivity();

        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        binding.toggleButton.setChecked(prefs.readToggleState());

        binding.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isPressed()){
                    showAlert(isChecked);
                }

            }
        });

        mNetworkStateReciever = new NetworkStateReciever(this);
        Objects.requireNonNull(getActivity()).registerReceiver(mNetworkStateReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        viewModel.getLocationResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: ");
                Log.d(TAG, "onChanged: "+s);
            }
        });

        return root;
    }

    private void showAlert(final boolean isChecked) {
        AlertDialog.Builder alBuiler = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        if(isChecked){
            alBuiler.setTitle("Confirm");
            alBuiler.setMessage("Are you sure to start day?");
        }else {
            alBuiler.setTitle("Confirm");
            alBuiler.setMessage("Are you sure to end day?");
        }
        alBuiler.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setClicked(isChecked);
                onCheckedChange();
            }
        });
        alBuiler.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setClicked(!isChecked);
            }
        });

        alBuiler.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                setClicked(!isChecked);
            }
        });

        AlertDialog dialog = alBuiler.create();
        dialog.show();
    }


    private void onCheckedChange() {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Turn on Location Permission", Toast.LENGTH_LONG).show();
            setClicked(false);
            return;
        }

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(60000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        onSuccessThis(location);
                    }
                });
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setClicked(false);
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });

        task.addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                setClicked(false);
            }
        });

    }

    private void executeEndDay(Location location) {

        binding.loadingHome.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Ending...", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.loadingHome.setVisibility(View.GONE);
            }

        }, 8000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(getActivity(),LocationService.class);
                Objects.requireNonNull(getContext()).stopService(serviceIntent);
            }
        }).start();


    }

    private void executeStartDay(Location location) {

        binding.loadingHome.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Starting...", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.loadingHome.setVisibility(View.GONE);
            }

        }, 6000);

        if(activity != null){
            Intent serviceIntent = new Intent(activity,LocationService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                activity.startForegroundService(serviceIntent);
            } else {
                // Pre-O behavior.
                activity.startService(serviceIntent);
            }
        }else {
            activity = getActivity();
            setClicked(false);
        }


    }

    @Override
    public void onNetworkAvailable() {
        binding.toggleButton.setEnabled(true);

    }

    @Override
    public void onNetworkUnavailable() {
        binding.toggleButton.setEnabled(false);
    }


    public void onSuccessThis(Location location) {
        if(location != null){
            if(this.clicked){
                executeStartDay(location);
            }else {
                executeEndDay(location);
            }
        }else {
            setClicked(false);
            Toast.makeText(getActivity(), "Failed to get location", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Open Google Maps once", Toast.LENGTH_LONG).show();

        }
    }

    public void setClicked(boolean clicked) {
        binding.toggleButton.setChecked(clicked);
        this.clicked = clicked;
        viewModel.setChecked(clicked);
        prefs.writeToggleState(clicked);
    }
}