package becker.andy.userapp.repository;

import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import becker.andy.userapp.models.User;
import becker.andy.userapp.retrofit.ApiClient;
import becker.andy.userapp.retrofit.ApiInterface;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadsRepository {

    private static final String TAG = "UploadsRepository";
    public static ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

    SingleLiveEvent<String> imageUpResponseLive = new SingleLiveEvent<>();
    SingleLiveEvent<String> audioUpResponseLive = new SingleLiveEvent<>();
    SingleLiveEvent<String> textUpResponseLive = new SingleLiveEvent<>();



    public SingleLiveEvent<String> getImageUpResponseLive() {
        return imageUpResponseLive;
    }

    public void setImageUpResponseLive(SingleLiveEvent<String> imageUpResponseLive) {
        this.imageUpResponseLive = imageUpResponseLive;
    }

    public SingleLiveEvent<String> getAudioUpResponseLive() {
        return audioUpResponseLive;
    }

    public void setAudioUpResponseLive(SingleLiveEvent<String> audioUpResponseLive) {
        this.audioUpResponseLive = audioUpResponseLive;
    }

    public SingleLiveEvent<String> getTextUpResponseLive() {
        return textUpResponseLive;
    }

    public void setTextUpResponseLive(SingleLiveEvent<String> textUpResponseLive) {
        this.textUpResponseLive = textUpResponseLive;
    }

    public void uploadImage(PrefConfig prefConfig, String imagePath) {

        File file = new File(imagePath);

        /*byte[] bytes = new byte[(int)file.length()];

        String encoded = Base64.encodeToString(bytes, 0);*/
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("image_file", file.getName(), fileReqBody);

        RequestBody remark_text =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "");


        Call<User> call = apiInterface.remarkUser(prefConfig.readUser().getAccess_token(),remark_text, part, null);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d(TAG, "onResponse: "+response.body().getMessage());
                        imageUpResponseLive.setValue(response.body().getMessage());
                    }else {
                        imageUpResponseLive.setValue("Response null");

                    }
                }else {
                    imageUpResponseLive.setValue(response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                imageUpResponseLive.setValue("Network Error!");
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

        Log.d(TAG, "uploadAudio: "+imagePath);

    }

    public void uploadAudio(PrefConfig prefConfig, String audioPath){

        File file = new File(audioPath);

        /*byte[] bytes = new byte[(int)file.length()];

        String encoded = Base64.encodeToString(bytes, 0);*/
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("audio/*"), file );
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("audio_file", file.getName(), fileReqBody);

        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

        MultipartBody.Part nullImage = MultipartBody.Part.createFormData("image_file", "", attachmentEmpty);

        RequestBody remark_text =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, "");

        Call<User> call = apiInterface.remarkUser(prefConfig.readUser().getAccess_token(),remark_text, nullImage, part);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d(TAG, "onResponse: "+response.body().getMessage());
                        audioUpResponseLive.setValue(response.body().getMessage());
                    }else {
                        audioUpResponseLive.setValue("Response null");

                    }
                }else {
                    audioUpResponseLive.setValue(response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                audioUpResponseLive.setValue("Network Error!");
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

        Log.d(TAG, "uploadAudio: "+audioPath);
    }

    public void uploadText(PrefConfig prefConfig, String uploadText) {


        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");

        MultipartBody.Part nullImage = MultipartBody.Part.createFormData("image_file", "", attachmentEmpty);


        MultipartBody.Part nullAudio = MultipartBody.Part.createFormData("audio_file", "", attachmentEmpty);

        RequestBody remark_text =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, uploadText);

        Call<User> call = apiInterface.remarkUser(prefConfig.readUser().getAccess_token(),remark_text, nullImage, nullAudio);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Log.d(TAG, "onResponse: "+response.body().getMessage());
                        textUpResponseLive.setValue(response.body().getMessage());
                    }else {
                        textUpResponseLive.setValue("Response null");

                    }
                }else {
                    textUpResponseLive.setValue(response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                textUpResponseLive.setValue("Network Error!");
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

        Log.d(TAG, "uploadAudio: "+uploadText);
    }
}
