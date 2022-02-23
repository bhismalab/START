package com.reading.start.tests.test_choice_touching.data.api;

import com.reading.start.tests.test_choice_touching.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_choice_touching.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_choice_touching.data.response.ResultIdResponseData;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Represent API for working test data
 */
public interface APISurveys {
    @GET("api/assestments/choose_touching")
    Call<ResponseAssessmentData> getContent(@Header("X-Auth-Token") String token,
                                            @Query("since_datetime") String sinceDateTime);

    @Headers("Content-Type: application/json")
    @POST("api/surveys/{survey_id}/results")
    Call<ResultIdResponseData> addResult(@Header("X-Auth-Token") String token,
                                         @Path("survey_id") String surveyId,
                                         @Body RequestBody jsonBody);

    @GET("api/surveys/{survey_id}/results/choose_touching")
    Call<ResponseDownloadSurveyResultData> getResult(@Header("X-Auth-Token") String token,
                                                     @Path("survey_id") String surveyId);
}
