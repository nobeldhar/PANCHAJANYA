package becker.andy.userapp.repository;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.List;

import becker.andy.userapp.models.User;
import becker.andy.userapp.models.UserLocation;
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


public class MainRepository {
    private static final String TAG = "MainRepository";
    public static ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
    public static SingleLiveEvent<String> locationResponse = new SingleLiveEvent<>();
    SingleLiveEvent<String> leaveResponse = new SingleLiveEvent<>();
    SingleLiveEvent<String> remarkResponse = new SingleLiveEvent<>();
    MutableLiveData<String> taskResponse = new MutableLiveData<>();
    MutableLiveData<List<String>> taskList = new MutableLiveData<>();
    public static Handler mainHandler = new Handler();




    public void updateLocation(final UserLocation userLocation, PrefConfig prefs){

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String date = userLocation.getDate();
        String lat = userLocation.getLatitude();
        String lon = userLocation.getLongitude();
        String dis = userLocation.getDistance();
        String sta = userLocation.getStatus();
        String token = prefs.readUser().getAccess_token();
        final RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("date", date)
                .addFormDataPart("latitude", lat)
                .addFormDataPart("longitude", lon)
                .addFormDataPart("distance", dis)
                .addFormDataPart("status", sta)
                .build();

        Call<UserLocation> call = apiInterface.updateLocation(token, requestBody);
        Log.d(TAG, "onLocationResult updateLocation: "+token);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                locationResponse.setValue("getting there");
            }
        });
        call.enqueue(new Callback<UserLocation>() {
            @Override
            public void onResponse(Call<UserLocation> call, final Response<UserLocation> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        if(response.body().getMessage().startsWith("User location updated")){
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    locationResponse.setValue("Done...");
                                }
                            });
                        }

                        Log.d(TAG, "onLocationResult onResponse: "+userLocation.getLatitude()+" "+ userLocation.getLongitude()+" "+userLocation.getStatus()+" "+userLocation.getDistance()+" "+response.body().getMessage());
                    }else {
                    }
                }else {
                }
            }

            @Override
            public void onFailure(Call<UserLocation> call, Throwable t) {

            }
        });

    }

    public SingleLiveEvent<String> getLocationResponse() {
        return locationResponse;
    }

    public void leaveUser(PrefConfig prefConfig, String from, String to, String no_of_days, String reason) {

        Call<User> call = apiInterface.leaveUser(prefConfig.readUser().getAccess_token(),from, to,no_of_days,reason);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        leaveResponse.setValue(response.body().getMessage());
                    }else {
                        leaveResponse.setValue("Response null");
                    }
                }else {
                    leaveResponse.setValue("Update not successful!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                leaveResponse.setValue("Network error!");
            }
        });
    }

    public SingleLiveEvent<String> getLeaveResponse() {
        return leaveResponse;
    }

    public void remarkUser(PrefConfig prefs, String remarkImage, String remark) {


        File file = new File(remarkImage);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("image_file", file.getName(), fileReqBody);

        RequestBody remark_text =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, remark);

        Call<User> call = apiInterface.remarkUser(prefs.readUser().getAccess_token(),null, null, null);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        remarkResponse.setValue(response.body().getMessage());
                    }else {
                        remarkResponse.setValue("Response null");
                    }
                }else {
                    remarkResponse.setValue("Update not successful!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                remarkResponse.setValue("Network Error!");
            }
        });
    }
    public void remarkUserText(PrefConfig prefs, String remark) {

        MultipartBody.Part part = null;

        RequestBody remark_text =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, remark);

        Call<User> call = apiInterface.remarkUser(prefs.readUser().getAccess_token(),null, null, null);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        remarkResponse.setValue(response.body().getMessage());
                    }else {
                        remarkResponse.setValue("Response null");
                    }
                }else {
                    remarkResponse.setValue("Update not successful!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                remarkResponse.setValue("Network Error!");
            }
        });
    }
    public void remarkUserImage(PrefConfig prefs, String remarkImage) {


        File file = new File(remarkImage);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("image_file", file.getName(), fileReqBody);

        RequestBody remark_text =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, new String(""));

        Call<User> call = apiInterface.remarkUser(prefs.readUser().getAccess_token(),null, null, null);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        remarkResponse.setValue(response.body().getMessage());
                    }else {
                        remarkResponse.setValue("Response null");
                    }
                }else {
                    remarkResponse.setValue("Update not successful!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                remarkResponse.setValue("Network Error!");
            }
        });
    }

    public SingleLiveEvent<String> getRemarkResponse() {
        return remarkResponse;
    }

    public void getTasks(PrefConfig prefs) {

        Call<User> call = apiInterface.taskUser(prefs.readUser().getAccess_token());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        if(response.body().getTask_list() != null){
                            taskList.setValue(response.body().getTask_list());
                        }else {
                            taskResponse.setValue(response.body().getMessage());
                        }
                    }else {
                        taskResponse.setValue("Response null");
                    }
                }else {
                    taskResponse.setValue("Update not successful!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                taskResponse.setValue("Network Error!");
            }
        });
    }

    public MutableLiveData<String> getTaskResponse() {
        return taskResponse;
    }

    public MutableLiveData<List<String>> getTaskList() {
        return taskList;
    }
}
