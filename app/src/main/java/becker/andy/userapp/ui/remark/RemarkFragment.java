package becker.andy.userapp.ui.remark;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentRemarkBinding;
import becker.andy.userapp.ui.leave.LeaveViewModel;
import becker.andy.userapp.utils.PrefConfig;

public class RemarkFragment extends Fragment {

    private RemarkViewModel viewModel;
    private FragmentRemarkBinding binding;
    private PrefConfig prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_remark,container,false);
        View root = binding.getRoot();
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.mobile_navigation);
        viewModel = new ViewModelProvider(backStackEntry).get(RemarkViewModel.class);

        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        viewModel.getmRemark().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    binding.loadingRemark.setVisibility(View.VISIBLE);
                }catch (Exception e){

                }
                if(s.equals("ok")){
                    viewModel.remarkUser(prefs);
                }else {
                    try {
                        binding.loadingRemark.setVisibility(View.GONE);
                    }catch (Exception e){

                    }
                    Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getRemarkResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    binding.loadingRemark.setVisibility(View.GONE);
                }catch (Exception e){

                }
                Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
            }
        });

        binding.loadingRemark.setVisibility(View.GONE);

        return root;
    }
}