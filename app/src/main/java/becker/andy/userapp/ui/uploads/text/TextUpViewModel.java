package becker.andy.userapp.ui.uploads.text;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import becker.andy.userapp.repository.UploadsRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class TextUpViewModel extends ViewModel {

    public String uploadText;
    private SingleLiveEvent<String> InputValidationLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> ProgressLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> SnackBarLive = new SingleLiveEvent<>();
    private UploadsRepository uploadsRepository = new UploadsRepository();
    private PrefConfig prefConfig;

    public void onUploadButton(View view){

        if(uploadText != null){
            SnackBarLive.setValue(true);
        }else {
            InputValidationLive.setValue("Provide Remarks");
        }
    }

    private void uploadTextFile() {
        uploadsRepository.uploadText(prefConfig, uploadText);
    }

    public String getUploadText() {
        return uploadText;
    }

    public void setUploadText(String uploadText) {
        this.uploadText = uploadText;
    }

    public SingleLiveEvent<String> getInputValidationLive() {
        return InputValidationLive;
    }

    public void setInputValidationLive(SingleLiveEvent<String> inputValidationLive) {
        InputValidationLive = inputValidationLive;
    }


    public SingleLiveEvent<Boolean> getProgressLive() {
        return ProgressLive;
    }

    public void setProgressLive(SingleLiveEvent<Boolean> progressLive) {
        ProgressLive = progressLive;
    }

    public SingleLiveEvent<Boolean> getSnackBarLive() {
        return SnackBarLive;
    }

    public void setSnackBarLive(SingleLiveEvent<Boolean> snackBarLive) {
        SnackBarLive = snackBarLive;
    }

    public UploadsRepository getUploadsRepository() {
        return uploadsRepository;
    }

    public void setUploadsRepository(UploadsRepository uploadsRepository) {
        this.uploadsRepository = uploadsRepository;
    }

    public PrefConfig getPrefConfig() {
        return prefConfig;
    }

    public void setPrefConfig(PrefConfig prefConfig) {
        this.prefConfig = prefConfig;
    }

    public AlertDialog.Builder buildAlertDialog(FragmentActivity activity) {
        AlertDialog.Builder alBuiler = new AlertDialog.Builder(activity);
        alBuiler.setTitle("Successful");
        alBuiler.setMessage("Text Uploaded Successfully.!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return alBuiler;
    }
    public Snackbar makeSnackBar(CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar;
        snackbar = Snackbar.make(coordinatorLayout, "Are you sure to upload this text?", BaseTransientBottomBar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                    if (event == DISMISS_EVENT_TIMEOUT) {
                        ProgressLive.setValue(true);
                        uploadTextFile();


                    }
            }
        });
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).setActionTextColor(Color.GREEN);

        return snackbar;
    }

    public SingleLiveEvent<String> getTextUpResponseLive() {
        return uploadsRepository.getTextUpResponseLive();
    }


}
