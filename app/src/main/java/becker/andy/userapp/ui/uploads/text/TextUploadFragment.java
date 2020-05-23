package becker.andy.userapp.ui.uploads.text;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentAudioUploadBinding;
import becker.andy.userapp.databinding.FragmentTaskBinding;
import becker.andy.userapp.databinding.FragmentTextUploadBinding;
import becker.andy.userapp.ui.task.TaskViewModel;
import becker.andy.userapp.ui.uploads.audio.AudioUpViewModel;
import becker.andy.userapp.utils.PrefConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextUploadFragment extends Fragment {

    private static final String TAG = "TextUploadFragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 9005;
    private static final int BROWSE_VIDEO_REQUEST = 2019;
    private TextUpViewModel viewModel;
    private FragmentTextUploadBinding binding;
    private PrefConfig prefs;
    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar mSnackbar;
    private AlertDialog.Builder mADBuilder;
    private Snackbar mDefaultSnack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_text_upload, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(TextUpViewModel.class);

        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));
        viewModel.setPrefConfig(prefs);

        coordinatorLayout = root.findViewById(R.id.text_up_coordinator);
        mSnackbar = viewModel.makeSnackBar(coordinatorLayout);
        mADBuilder = viewModel.buildAlertDialog(getActivity());
        mDefaultSnack = Snackbar.make(coordinatorLayout, "Provide remarks", Snackbar.LENGTH_LONG);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getInputValidationLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String s) {
                mDefaultSnack.show();
            }
        });

        viewModel.getProgressLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visibility) {
                setProgressVisibility(visibility);
            }
        });

        viewModel.getSnackBarLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    closeKeyBoard();
                    mSnackbar.show();

                }

            }
        });

        viewModel.getTextUpResponseLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String s) {
                setProgressVisibility(false);
                if (s.contains("successfully")) {
                    try {
                        binding.remark.setText(null);
                        viewModel.setUploadText(null);
                        AlertDialog dialog = mADBuilder.create();
                        dialog.show();
                    } catch (Exception e) {

                    }
                } else
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProgressVisibility(Boolean visibility) {
        if (visibility){
            binding.loadingTextUp.setVisibility(View.VISIBLE);
        } else
            binding.loadingTextUp.setVisibility(View.GONE);
    }

    private void closeKeyBoard() {
        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
