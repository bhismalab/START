package com.reading.start.tests.test_jabble.data.api;

import com.reading.start.tests.test_jabble.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_jabble.data.response.ResultIdResponseData;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Represent API for working test data
 */
public interface APISurveys {
    @Headers("Content-Type: application/json")
    @POST("api/surveys/{survey_id}/results")
    Call<ResultIdResponseData> addResult(@Header("X-Auth-Token") String token,
                                         @Path("survey_id") String surveyId,
                                         @Body RequestBody jsonBody);

    @GET("api/surveys/{survey_id}/results/bubbles_jubbing")
    Call<ResponseDownloadSurveyResultData> getResult(@Header("X-Auth-Token") String token,
                                                     @Path("survey_id") String surveyId);
}
