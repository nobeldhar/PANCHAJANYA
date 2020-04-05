package becker.andy.userapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentHomeBinding;
import becker.andy.userapp.service.LocationService;
import becker.andy.userapp.utils.NetworkStateReciever;
import becker.andy.userapp.utils.PrefConfig;

public class HomeFragment extends Fragment implements NetworkStateReciever.NetworkListener, OnSuccessListener<Location> {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private PrefConfig prefs;
    private NetworkStateReciever mNetworkStateReciever;
    private boolean clicked;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false);
        View root = binding.getRoot();
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.mobile_navigation);
        viewModel = new ViewModelProvider(backStackEntry).get(HomeViewModel.class);
        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        binding.toggleButton.setChecked(prefs.readToggleState());

        binding.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setClicked(isChecked);
                onCheckedChange();
            }
        });

        mNetworkStateReciever = new NetworkStateReciever(this);
        Objects.requireNonNull(getActivity()).registerReceiver(mNetworkStateReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        viewModel.getLocationResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
        });


        return root;
    }



    private void onCheckedChange() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Turn on Location Permission", Toast.LENGTH_LONG).show();
            setClicked(false);
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this);
    }

    private void executeEndDay(Location location) {
        viewModel.sendEndingLocation(location, prefs);
        Intent serviceIntent = new Intent(getActivity(),LocationService.class);
        Objects.requireNonNull(getActivity()).stopService(serviceIntent);

    }

    private void executeStartDay(Location location) {
        viewModel.sendStartingLocation(location, prefs);
        Intent serviceIntent = new Intent(getActivity(),LocationService.class);
        ContextCompat.startForegroundService(Objects.requireNonNull(getActivity()),serviceIntent);

    }

    @Override
    public void onNetworkAvailable() {
        binding.toggleButton.setEnabled(true);

    }

    @Override
    public void onNetworkUnavailable() {
        binding.toggleButton.setEnabled(false);
    }

    @Override
    public void onSuccess(Location location) {
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
        this.clicked = clicked;
        viewModel.setChecked(clicked);
        prefs.writeToggleState(clicked);
    }
}