package becker.andy.userapp.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import becker.andy.userapp.models.User;
import becker.andy.userapp.models.UserLocation;
import becker.andy.userapp.retrofit.ApiClient;
import becker.andy.userapp.retrofit.ApiInterface;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRepository {
    ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
    SingleLiveEvent<String> locationResponse = new SingleLiveEvent<>();
    SingleLiveEvent<String> leaveResponse = new SingleLiveEvent<>();
    SingleLiveEvent<String> remarkResponse = new SingleLiveEvent<>();
    MutableLiveData<String> taskResponse = new MutableLiveData<>();



    public void updateLocation(UserLocation userLocation, PrefConfig prefs){

        String date = userLocation.getDate();
        String lat = userLocation.getLatitude();
        String lon = userLocation.getLongitude();
        String dis = userLocation.getDistance();
        String sta = userLocation.getStatus();
        String token = prefs.readUser().getAccess_token();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("date", date)
                .addFormDataPart("latitude", lat)
                .addFormDataPart("longitude", lon)
                .addFormDataPart("distance", dis)
                .addFormDataPart("status", sta)
                .build();

        Call<UserLocation> call = apiInterface.updateLocation(token, requestBody);
        call.enqueue(new Callback<UserLocation>() {
            @Override
            public void onResponse(Call<UserLocation> call, Response<UserLocation> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        locationResponse.setValue(response.body().getMessage());
                    }else {
                        locationResponse.setValue("Response null");
                    }
                }else {
                    locationResponse.setValue("Update not successful!");
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

    public void leaveUser(PrefConfig prefConfig, String date, String reason) {

        Call<User> call = apiInterface.leaveUser(prefConfig.readUser().getAccess_token(),date,reason);

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

    public void remarkUser(PrefConfig prefs, String remark) {

        Call<User> call = apiInterface.remarkUser(prefs.readUser().getAccess_token(), remark);
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
                        if(response.body().getTask() != null){
                            taskResponse.setValue(response.body().getTask());
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
}
