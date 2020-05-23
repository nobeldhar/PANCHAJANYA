package becker.andy.userapp.retrofit;

import becker.andy.userapp.models.User;
import becker.andy.userapp.models.UserLocation;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("location")
    Call<UserLocation> updateLocation(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("leave")
    Call<User> leaveUser(@Header("Authorization") String authorization,
                         @Field("from_date") String from,
                         @Field("to_date") String to,
                         @Field("no_of_days") String no_of_days,
                         @Field("reason") String reason);
    @Multipart
    @POST("remark")
    Call<User> remarkUser(@Header("Authorization") String authorization,
                          @Part("remark") RequestBody remarkText,
                          @Part MultipartBody.Part remarkImage,
                          @Part MultipartBody.Part remarkAudio);

    @POST("task")
    Call<User> taskUser(@Header("Authorization") String authorization);

    @POST("logout")
    Call<User> logoutUser(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("login")
    Call<User> loginUser(@Field("mobile") String mobile, @Field("password") String password);
}
