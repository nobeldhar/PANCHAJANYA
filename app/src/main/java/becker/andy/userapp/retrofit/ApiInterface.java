package becker.andy.userapp.retrofit;

import becker.andy.userapp.models.User;
import becker.andy.userapp.models.UserLocation;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("location")
    Call<UserLocation> updateLocation(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("leave")
    Call<User> leaveUser(@Header("Authorization") String authorization, @Field("date") String date,
                                 @Field("reason") String reason);
    @FormUrlEncoded
    @POST("remark")
    Call<User> remarkUser(@Header("Authorization") String authorization, @Field("remark") String date);

    @POST("task")
    Call<User> taskUser(@Header("Authorization") String authorization);

    @POST("logout")
    Call<User> logoutUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("login")
    Call<User> loginUser(@Field("mobile") String mobile, @Field("password") String password);
}
