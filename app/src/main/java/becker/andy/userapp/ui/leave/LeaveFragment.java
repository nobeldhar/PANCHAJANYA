package becker.andy.userapp.ui.leave;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentLeaveBinding;
import becker.andy.userapp.ui.home.HomeViewModel;
import becker.andy.userapp.utils.PrefConfig;

public class LeaveFragment extends Fragment {

    private LeaveViewModel viewModel;
    private FragmentLeaveBinding binding;
    private PrefConfig prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leave,container,false);
        View root = binding.getRoot();
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.mobile_navigation);
        viewModel = new ViewModelProvider(backStackEntry).get(LeaveViewModel.class);

        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        final String currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            binding.date.setText(currentDateTimeString);
        }catch (Exception e){

        }

        viewModel.getmLeave().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    binding.loadingLeave.setVisibility(View.VISIBLE);
                }catch (Exception e){

                }
                if(s.equals("ok")){
                    viewModel.leaveUser(prefs,currentDateTimeString);
                }else {
                    try {
                        binding.loadingLeave.setVisibility(View.GONE);
                    }catch (Exception e){

                    }
                    Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
                }
            }
        });
        viewModel.getLeaveResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    binding.loadingLeave.setVisibility(View.GONE);
                }catch (Exception e){

                }
                Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
            }
        });

        binding.loadingLeave.setVisibility(View.GONE);
        return root;
    }
}