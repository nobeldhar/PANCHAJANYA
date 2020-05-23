package becker.andy.userapp.ui.uploads.audio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
//import com.greentoad.turtlebody.mediapicker.MediaPicker;
//import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import becker.andy.userapp.R;
import becker.andy.userapp.databinding.FragmentAudioUploadBinding;
import becker.andy.userapp.databinding.FragmentImageUploadBinding;
import becker.andy.userapp.ui.uploads.image.ImageUpViewModel;
import becker.andy.userapp.utils.PrefConfig;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioUploadFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = "AudioUploadFragment";
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 9005;
    private static final int BROWSE_VIDEO_REQUEST = 2019;
    private AudioUpViewModel viewModel;
    private FragmentAudioUploadBinding binding;
    private PrefConfig prefs;
    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar mSnackbar;
    private AlertDialog.Builder mADBuilder;
    //MediaPickerConfig mediaPickerConfig;

    MediaPlayer player;
    MediaRecorder recorder;
    private int length;
    private String fileName;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private Snackbar mDefaultSnack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_upload, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(AudioUpViewModel.class);
        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));
        viewModel.setPrefConfig(prefs);

        fileName = getActivity().getCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        binding.btnPlay.setOnClickListener(this);
        binding.btnPause.setOnClickListener(this);
        binding.btnStop.setOnClickListener(this);


        coordinatorLayout = root.findViewById(R.id.audio_up_coordinator);
        mSnackbar = viewModel.makeSnackBar(coordinatorLayout);
        mADBuilder = viewModel.buildAlertDialog(getActivity());
        mDefaultSnack = Snackbar.make(coordinatorLayout, "Select the audio", Snackbar.LENGTH_LONG);

        /*mediaPickerConfig = new MediaPickerConfig()
                .setAllowMultiSelection(false)
                .setUriPermanentAccess(true)
                .setShowConfirmationDialog(true)
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);*/


        return root;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        if (isChecked) {
            binding.btnAudio.setVisibility(View.GONE);
            binding.mediaOptions.setVisibility(View.GONE);
            stopPlaying();
            startRecording();
        } else{
            stopRecording();
            binding.mediaOptions.setVisibility(View.VISIBLE);
            binding.btnAudio.setVisibility(View.VISIBLE);
        }
    }


    private void stopPlaying() {
        if(player != null){
            player.release();
            player = null;
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        if (recorder != null){
            recorder.stop();
            recorder.release();
            recorder = null;
        }
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

        viewModel.getProgressLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                setProgressVisibility(visibility);
            }
        });

        viewModel.getSnackBarLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mSnackbar.show();
                    onStopButton();
                }

            }
        });

        viewModel.getAudioUpResponseLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String s) {
                setProgressVisibility(View.GONE);
                if (s.contains("successfully")) {
                    try {
                        binding.mediaOptions.setVisibility(View.GONE);
                        viewModel.setUri(null);
                        viewModel.setMediaPlayer(null);
                        AlertDialog dialog = mADBuilder.create();
                        dialog.show();
                    } catch (Exception e) {

                    }
                } else
                    Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAudioIntentLive().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {

                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                //intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, BROWSE_VIDEO_REQUEST);

                /*new VideoPicker.Builder(getActivity())
                        .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .enableDebuggingMode(true)
                        .build();*/
                /*MediaPicker.with(Objects.requireNonNull(getActivity()), MediaPicker.MediaTypes.AUDIO)
                        .setConfig(mediaPickerConfig)
                        .setFileMissingListener(new MediaPicker.MediaPickerImpl.OnMediaListener() {
                            @Override
                            public void onMissingFileWarning() {
                                //trigger when some file are missing
                            }
                        })
                        .onResult()
                        .subscribe(new Observer<ArrayList<Uri>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(ArrayList<Uri> uris) {
                                Uri uri = uris.get(0);
                                String audioPath = getRealPathFromURI(getActivity(), uri);
                                viewModel.setAudioPath(audioPath);
                                *//*DocumentFile file = DocumentFile.fromSingleUri(getActivity(), uri);
                                assert file != null;
                                viewModel.setUri(file.getUri());*//*
                                Toast.makeText(getActivity(), "Audio selected", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });*/
            }
        });
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(contentUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "getRealPathFromURI: "+e.getMessage());
        }

        File file = new File(context.getCacheDir().getAbsolutePath()+"/"+"myfile.3gp");
        writeFile(inputStream, file);
        String filePath = file.getAbsolutePath();
        return filePath;
    }

    private void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void setProgressVisibility(Integer visibility) {
        binding.loadingAudioUp.setVisibility(visibility);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                onPlayButton();
                break;
            case R.id.btn_pause:
                onPauseButton();
                break;
            case R.id.btn_stop:
                onStopButton();
                break;

        }
    }

    private void onDeleteButton() {
        binding.mediaOptions.setVisibility(View.GONE);
        binding.btnAudio.setVisibility(View.GONE);
        if(player != null){
            player.release();
            player = null;
        }
        if(recorder != null){
            recorder.release();
            recorder = null;
        }


    }

    private void onStopButton() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void onPlayButton() {
        if(viewModel.getAudioPath() != null){
            if (player == null) {
                player = MediaPlayer.create(getActivity(), Uri.parse(viewModel.getAudioPath()));

                player.setOnCompletionListener(this);
                player.start();

            } else {
                player.seekTo(length);
                player.start();
            }
        }else
            mDefaultSnack.show();
    }


    private void onPauseButton() {
        if (player != null) {
            player.pause();
            length = player.getCurrentPosition();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        onStopButton();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        onStopButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BROWSE_VIDEO_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            String uripath = getRealPathFromURI(Objects.requireNonNull(getActivity()),data.getData());
            assert uripath != null;
            String filePath = new File(uripath).getPath();
            viewModel.setAudioPath(filePath);
            Toast.makeText(getActivity(), "Audio Selected", Toast.LENGTH_LONG).show();

            //Your Code
        }else
            Log.d(TAG, "onActivityResult: ");
    }
}
