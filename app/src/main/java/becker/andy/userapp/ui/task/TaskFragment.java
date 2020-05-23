package becker.andy.userapp.ui.task;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentTaskBinding;
import becker.andy.userapp.utils.PrefConfig;

public class TaskFragment extends Fragment {

    private TaskViewModel viewModel;
    private FragmentTaskBinding binding;
    private PrefConfig prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task,container,false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));

        try {
            binding.loadingTask.setVisibility(View.VISIBLE);
        }catch (Exception e){

        }
        viewModel.getTasks(prefs);
        viewModel.getTaskResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                try {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                    binding.loadingTask.setVisibility(View.GONE);
                    binding.taskNotFound.setVisibility(View.VISIBLE);
                }catch (Exception e){
                }

            }
        });

        viewModel.getTaskList().observe(getActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                try {
                    binding.loadingTask.setVisibility(View.GONE);
                    binding.taskRecycler.setAdapter(new TaskAdapter(strings));
                    binding.taskRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                }catch (Exception e){

                }
            }
        });
        return root;
    }
}