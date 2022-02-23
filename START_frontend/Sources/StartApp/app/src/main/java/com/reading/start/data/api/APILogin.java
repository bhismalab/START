package com.reading.start.data.api;

import com.reading.start.data.response.ResponseCheckAuthTokenData;
import com.reading.start.data.response.ResponseLoginData;
import com.reading.start.data.response.ResponsePasswordRecovery;
import com.reading.start.data.response.ResponsePasswordReset;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Represent API for working with login and password.
 */
public interface APILogin {
    @Headers("Content-Type: application/json")
    @POST("api/login")
    Call<ResponseLoginData> login(@Body RequestBody jsonBody);

    @GET("api/check-auth-token")
    Call<ResponseCheckAuthTokenData> checkAuthToken(@Header("X-Auth-Token") String token);

    @Headers("Content-Type: application/json")
    @POST("api/password-recovery")
    Call<ResponsePasswordRecovery> passwordRecovery(@Body RequestBody jsonBody);

    @Headers("Content-Type: application/json")
    @PUT("api/social-workers/{id}")
    Call<ResponsePasswordReset> passwordReset(@Path("id") String id, @Body RequestBody jsonBody);
}
