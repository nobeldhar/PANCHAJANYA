package becker.andy.userapp.ui.leave;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentLeaveBinding;
import becker.andy.userapp.ui.home.HomeViewModel;
import becker.andy.userapp.utils.PrefConfig;

public class LeaveFragment extends Fragment implements Observer<View> {

    private LeaveViewModel viewModel;
    private FragmentLeaveBinding binding;
    private PrefConfig prefs;
    private Activity activity;
    private AlertDialog.Builder alBuiler;
    private Snackbar mSnackbar;
    private CoordinatorLayout coordinatorLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leave,container,false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(LeaveViewModel.class);
        binding.setViewmodel(viewModel);

        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));
        activity = getActivity();
        coordinatorLayout = root.findViewById(R.id.leave_coordinator);
        mSnackbar = Snackbar.make(coordinatorLayout, "Are you sure to submit leave?", BaseTransientBottomBar.LENGTH_LONG);
        mSnackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                    if(event == DISMISS_EVENT_TIMEOUT){
                        try {
                            binding.loadingLeave.setVisibility(View.VISIBLE);
                        }catch (Exception e){

                        }
                        viewModel.leaveUser(prefs);
                    }
            }
        });
        mSnackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).setActionTextColor(Color.GREEN);

        alBuiler = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        alBuiler.setTitle("Successful");
        alBuiler.setMessage("Leave Submitted Successfully.!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        binding.loadingLeave.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getOnDateClick().observe(getViewLifecycleOwner(), this);
        viewModel.getmLeave().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                closeKeyBoard();
                if(s.equals("ok")){
                    mSnackbar.show();
                }else {
                    Toast.makeText(activity, s,Toast.LENGTH_LONG).show();
                }
            }
        });
        viewModel.getLeaveResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.loadingLeave.setVisibility(View.GONE);
                if(s.contains("successfully")){
                    binding.to.setText(null);
                    binding.from.setText(null);
                    binding.noOfDays.setText(null);
                    binding.reason.setText(null);
                    viewModel.setFrom(null);
                    viewModel.setTo(null);
                    viewModel.setNo_of_days(null);
                    viewModel.setReason(null);
                    AlertDialog dialog = alBuiler.create();
                    dialog.show();

                }else
                    Toast.makeText(activity, s,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void closeKeyBoard() {
        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @SuppressLint("ResourceType")
    @Override
    public void onChanged(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        DatePickerDialog pickerDialog;
        switch (view.getId()) {
            case R.id.from:
                pickerDialog = new DatePickerDialog(Objects.requireNonNull(activity), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.from.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                pickerDialog.show();
                break;
            case R.id.to:
                pickerDialog = new DatePickerDialog(Objects.requireNonNull(activity), new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        binding.to.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                pickerDialog.show();
                break;
            }
    }

}