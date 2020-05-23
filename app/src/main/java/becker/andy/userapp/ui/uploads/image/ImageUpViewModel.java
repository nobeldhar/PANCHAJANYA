package becker.andy.userapp.ui.uploads.image;

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

public class ImageUpViewModel extends ViewModel {

    private SingleLiveEvent<String> InputValidationLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Intent> galleryIntentLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> ProgressLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> SnackBarLive = new SingleLiveEvent<>();
    private UploadsRepository uploadsRepository = new UploadsRepository();
    public String imageString;
    private PrefConfig prefConfig;

    public void onUploadButton(View view){

        if(imageString != null){
            SnackBarLive.setValue(true);
        }else {
            InputValidationLive.setValue("Select Image");
        }
    }

    public void onImageClick(View view){
        galleryIntentLive.setValue(new Intent());
    }

    private void uploadImage() {

        InputValidationLive.setValue("Uploading Image");
        uploadsRepository.uploadImage(prefConfig, imageString);
    }

    public SingleLiveEvent<String> getImageUpResponseLive() {
        return uploadsRepository.getImageUpResponseLive();
    }

    public SingleLiveEvent<Boolean> getSnackBarLive() {
        return SnackBarLive;
    }

    public void setSnackBarLive(SingleLiveEvent<Boolean> snackBarLive) {
        SnackBarLive = snackBarLive;
    }

    public SingleLiveEvent<Integer> getProgressLive() {
        return ProgressLive;
    }

    public void setProgressLive(SingleLiveEvent<Integer> progressLive) {
        this.ProgressLive = progressLive;
    }

    public PrefConfig getPrefConfig() {
        return prefConfig;
    }

    public void setPrefConfig(PrefConfig prefConfig) {
        this.prefConfig = prefConfig;
    }

    public SingleLiveEvent<String> getInputValidationLive() {
        return InputValidationLive;
    }

    public void setInputValidationLive(SingleLiveEvent<String> inputValidationLive) {
        this.InputValidationLive = inputValidationLive;
    }

    public SingleLiveEvent<Intent> getGalleryIntentLive() {
        return galleryIntentLive;
    }

    public void setGalleryIntentLive(SingleLiveEvent<Intent> galleryIntentLive) {
        this.galleryIntentLive = galleryIntentLive;
    }




    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public Snackbar makeSnackBar(CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar;
        snackbar = Snackbar.make(coordinatorLayout, "Are you sure to upload this image?", BaseTransientBottomBar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                    if(event == DISMISS_EVENT_TIMEOUT){
                        ProgressLive.setValue(View.VISIBLE);
                        uploadImage();


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

    public AlertDialog.Builder buildAlertDialog(FragmentActivity activity) {
        AlertDialog.Builder alBuiler = new AlertDialog.Builder(activity);
        alBuiler.setTitle("Successful");
        alBuiler.setMessage("Image Uploaded Successfully.!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return alBuiler;
    }
}
