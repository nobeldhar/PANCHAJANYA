package becker.andy.userapp.repository;

import android.util.Log;

import becker.andy.userapp.models.User;
import becker.andy.userapp.retrofit.ApiClient;
import becker.andy.userapp.retrofit.ApiInterface;
import becker.andy.userapp.utils.PrefConfig;
import becker.andy.userapp.utils.SingleLiveEvent;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private static final String TAG = "LoginRepository";
    private ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
    private SingleLiveEvent<String> loginResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<User> userResponse = new SingleLiveEvent<>();
    private SingleLiveEvent<String> logoutResponse = new SingleLiveEvent<>();


    public SingleLiveEvent<String> getLoginResponse() {
        return loginResponse;
    }

    public void loginUser(User user){


        Call<User> call = apiInterface.loginUser(user.getMobile(),user.getPassword());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        if(response.body().getAccess_token() != null){
                            writeUser(response.body());
                        }else {
                            loginResponse.setValue(response.body().getMessage());
                        }
                    }else {
                        loginResponse.setValue("RequestBody Null!");
                    }

                }else {
                    loginResponse.setValue("Login Not Successful");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loginResponse.setValue("Network error!");
            }
        });

    }

    private void writeUser(User user) {
        userResponse.setValue(user);
    }

    public SingleLiveEvent<User> getUserResponse() {
        return userResponse;
    }

    public void logoutUser(PrefConfig prefs) {

        Log.d(TAG, "onLocationResult logoutUser: "+prefs.readUser().getAccess_token());

        Call<User> call = apiInterface.logoutUser(prefs.readUser().getAccess_token());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        logoutResponse.setValue(response.body().getMessage());
                    }else {
                        logoutResponse.setValue("Response null");
                    }
                }else {
                    logoutResponse.setValue("Update not successful!");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                logoutResponse.setValue("Network Error!");
            }
        });
    }

    public SingleLiveEvent<String> getLogoutResponse() {
        return logoutResponse;
    }
}
