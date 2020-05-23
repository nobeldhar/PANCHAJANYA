package becker.andy.userapp.ui.uploads.image;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
//import com.greentoad.turtlebody.mediapicker.MediaPicker;
//import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig;

import net.alhazmy13.mediapicker.Image.ImagePicker;

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
import becker.andy.userapp.databinding.FragmentImageUploadBinding;
import becker.andy.userapp.utils.FileUtils;
import becker.andy.userapp.utils.PrefConfig;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;
/*
import in.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener;
import in.mayanknagwanshi.imagepicker.imagePicker.ImagePicker;
*/


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ImageUploadFragment extends Fragment implements Observer<Intent> {

    private static final String TAG = "ImageUploadFragment";
    private static final int IMAGE_REQUEST = 1213;
    private static final int BROWSE_VIDEO_REQUEST = 3029;
    private ImageUpViewModel viewModel;
    private FragmentImageUploadBinding binding;
    private PrefConfig prefs;
    private Context context;
    //private ImagePicker mImagePicker;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar mSnackbar;
    private AlertDialog.Builder mADBuilder;
    //private MediaPickerConfig mediaPickerConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_upload, container, false);
        final View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(ImageUpViewModel.class);
        binding.setViewmodel(viewModel);
        prefs = new PrefConfig(Objects.requireNonNull(getActivity()).getSharedPreferences("pref_file", Context.MODE_PRIVATE));
        viewModel.setPrefConfig(prefs);
        //mImagePicker = new ImagePicker();

        coordinatorLayout = root.findViewById(R.id.image_up_coordinator);
        mSnackbar = viewModel.makeSnackBar(coordinatorLayout);
        mADBuilder = viewModel.buildAlertDialog(getActivity());

        /*mediaPickerConfig = new MediaPickerConfig()
                .setAllowMultiSelection(false)
                .setUriPermanentAccess(true)
                .setShowConfirmationDialog(true)
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);*/

        return root;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.getInputValidationLive().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getProgressLive().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                setProgressVisibility(visibility);
            }
        });

        viewModel.getSnackBarLive().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                closeKeyBoard();
                if (aBoolean)
                    mSnackbar.show();
            }
        });

        viewModel.getImageUpResponseLive().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setProgressVisibility(View.GONE);
                if (s.contains("successfully")){
                    try {
                        binding.uploadImage.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
                        viewModel.setImageString(null);
                        AlertDialog dialog = mADBuilder.create();
                        dialog.show();
                    }catch (Exception e){

                    }
                }else
                    Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getGalleryIntentLive().observe(getViewLifecycleOwner(),this);

    }

    private void setProgressVisibility(int visibility) {
        binding.loadingImageUp.setVisibility(visibility);
    }

    private void closeKeyBoard() {
        View view = Objects.requireNonNull(getActivity()).getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    public void onChanged(Intent intent) {

        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        /*intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");*/
        startActivityForResult(intent, BROWSE_VIDEO_REQUEST);

        //mImagePicker.withFragment(this).withCompression(true).start();
        /*MediaPicker.with(Objects.requireNonNull(getActivity()),MediaPicker.MediaTypes.IMAGE)
                .setConfig(mediaPickerConfig)
                .setFileMissingListener(new MediaPicker.MediaPickerImpl.OnMediaListener() {
                    @Override
                    public void onMissingFileWarning() {
                        //trigger when some file are missing
                    }
                })
                .onResult()
                .subscribe(new io.reactivex.Observer<ArrayList<Uri>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ArrayList<Uri> uris) {
                        String filePath = FileUtils.getFilePathFromURI(getActivity(), uris.get(0));
                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

                        *//*Bitmap selectedImage = null;
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uris.get(0));
                        } catch (IOException e) {
                            Log.d(TAG, "onNext: "+e.getLocalizedMessage());
                        }*//*
                        binding.uploadImage.setImageBitmap(selectedImage);
                        viewModel.setImageString(filePath);
                    }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onComplete() { }
                });*/

        /*new ImagePicker.Builder(getActivity())
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.JPG)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();*/
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(contentUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "getRealPathFromURI: "+e.getMessage());
        }

        File file = new File(context.getCacheDir().getAbsolutePath()+"/"+"myfile");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BROWSE_VIDEO_REQUEST && resultCode == RESULT_OK){
            assert data != null;
            String filePath = getRealPathFromURI(getActivity(), data.getData());
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            binding.uploadImage.setImageBitmap(selectedImage);
            viewModel.setImageString(filePath);
        }
        /*if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            mImagePicker.addOnCompressListener(new ImageCompressionListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompressed(String filePath) {
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    binding.uploadImage.setImageBitmap(selectedImage);
                    viewModel.setImageString(filePath);

                }
            });
            String filePath = mImagePicker.getImageFilePath(data);
            if (filePath != null) {
                Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                binding.uploadImage.setImageBitmap(selectedImage);
                viewModel.setImageString(filePath);
            }
        }*/
    }
}
