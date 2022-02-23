package com.reading.start.data.api;

import com.reading.start.data.response.ObjectResponseData;
import com.reading.start.data.response.ResponseSurveyIdData;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Represent API for working with surveys.
 */
public interface APISurveys {
    @Headers("Content-Type: application/json")
    @POST("api/surveys")
    Call<ResponseSurveyIdData> addSurvey(@Header("X-Auth-Token") String token,
                                         @Body RequestBody jsonBody);

    @Headers("Content-Type: application/json")
    @PUT("api/surveys/{survey_id}")
    Call<ObjectResponseData> updateSurveyCompleted(@Header("X-Auth-Token") String token,
                                                   @Path("survey_id") String surveyId,
                                                   @Body RequestBody jsonBody);
}
