package com.reading.start.data.api;

import com.reading.start.data.response.ObjectResponseData;
import com.reading.start.data.response.ResponseChildrenData;
import com.reading.start.data.response.ResponseChildrenIdData;
import com.reading.start.data.response.ResponseChildrenSurveysData;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Represent API for working with child.
 */
public interface APIChild {
    @GET("api/children")
    Call<ResponseChildrenData> getChildren(@Header("X-Auth-Token") String token,
                                           @Query("since_datetime") String sinceDateTime);

    @GET("api/children/{childId}/surveys")
    Call<ResponseChildrenSurveysData> getChildrenSurveys(@Header("X-Auth-Token") String token,
                                                         @Path("childId") String childId);

    @Headers("Content-Type: application/json")
    @POST("api/children")
    Call<ResponseChildrenIdData> addChildren(@Header("X-Auth-Token") String token,
                                             @Body RequestBody jsonBody);

    @Headers("Content-Type: application/json")
    @PUT("api/children/{childId}")
    Call<ObjectResponseData> updateChildren(@Header("X-Auth-Token") String token,
                                            @Path("childId") String childId,
                                            @Body RequestBody jsonBody);
}
