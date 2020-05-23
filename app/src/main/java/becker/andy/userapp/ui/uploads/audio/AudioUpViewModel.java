package becker.andy.userapp.ui.uploads.audio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import becker.andy.userapp.repository.UploadsRepository;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;

public class AudioUpViewModel extends ViewModel {

    private static final String TAG = "AudioUpViewModel";
    private Uri uri;
    private SingleLiveEvent<String> InputValidationLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Intent> audioIntentLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> ProgressLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> SnackBarLive = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> MediaOptionsLive = new SingleLiveEvent<>();
    private UploadsRepository uploadsRepository = new UploadsRepository();
    private PrefConfig prefConfig;
    private MediaPlayer mediaPlayer;
    private  String audioPath;


    public void onUploadButton(View view) {

        if (audioPath != null) {
            SnackBarLive.setValue(true);
        } else {
            InputValidationLive.setValue("Select Audio");
        }
    }

    private void uploadAudio() {

        uploadsRepository.uploadAudio(prefConfig, getAudioPath());
    }

    public void onAudioClick(View view) {
        audioIntentLive.setValue(new Intent());
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;

    }

    public SingleLiveEvent<String> getAudioUpResponseLive() {
        return uploadsRepository.getAudioUpResponseLive();
    }

    public SingleLiveEvent<Intent> getAudioIntentLive() {
        return audioIntentLive;
    }

    public void setAudioIntentLive(SingleLiveEvent<Intent> audioIntentLive) {
        this.audioIntentLive = audioIntentLive;
    }

    public SingleLiveEvent<Integer> getProgressLive() {
        return ProgressLive;
    }

    public void setProgressLive(SingleLiveEvent<Integer> progressLive) {
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

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
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
        InputValidationLive = inputValidationLive;
    }

    public Snackbar makeSnackBar(CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar;
        snackbar = Snackbar.make(coordinatorLayout, "Are you sure to upload this audio?", BaseTransientBottomBar.LENGTH_LONG);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION)
                    if (event == DISMISS_EVENT_TIMEOUT) {
                        ProgressLive.setValue(View.VISIBLE);
                        uploadAudio();


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
        alBuiler.setMessage("Audio Uploaded Successfully.!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return alBuiler;
    }

    public SingleLiveEvent<Boolean> getMediaOptionsLive() {
        return MediaOptionsLive;
    }

    public void setMediaOptionsLive(SingleLiveEvent<Boolean> mediaOptionsLive) {
        MediaOptionsLive = mediaOptionsLive;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
