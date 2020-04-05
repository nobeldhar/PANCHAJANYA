package becker.andy.userapp.ui.task;

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
import becker.andy.userapp.databinding.FragmentTaskBinding;
import becker.andy.userapp.ui.remark.RemarkViewModel;
import becker.andy.userapp.utils.PrefConfig;

public class TaskFragment extends Fragment {

    private TaskViewModel viewModel;
    private FragmentTaskBinding binding;
    private PrefConfig prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task,container,false);
        View root = binding.getRoot();
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.mobile_navigation);
        viewModel = new ViewModelProvider(backStackEntry).get(TaskViewModel.class);

        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        viewModel.getTasks(prefs);
        viewModel.getTaskResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    binding.tasks.setText(s);
                }catch (Exception e){
                }

            }
        });
        return root;
    }
}