package com.reading.start.tests.test_wheel.data.api;

import com.reading.start.tests.test_wheel.data.response.AttachmentIdResponseData;
import com.reading.start.tests.test_wheel.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_wheel.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_wheel.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_wheel.data.response.ResultIdResponseData;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Represent API for working test data
 */
public interface APISurveys {
    @GET("api/assestments/wheel")
    Call<ResponseAssessmentData> getContent(@Header("X-Auth-Token") String token,
                                            @Query("since_datetime") String sinceDateTime);

    @Headers("Content-Type: application/json")
    @POST("api/surveys/{survey_id}/results")
    Call<ResultIdResponseData> addResult(@Header("X-Auth-Token") String token,
                                         @Path("survey_id") String surveyId,
                                         @Body RequestBody jsonBody);

    @Multipart
    @POST("api/surveys/{survey_id}/attachments")
    Call<AttachmentIdResponseData> addAttachment(@Header("X-Auth-Token") String token,
                                                 @Path("survey_id") String surveyId,
                                                 @PartMap Map<String, RequestBody> params);

    @GET("api/surveys/{survey_id}/results/wheel")
    Call<ResponseDownloadSurveyResultData> getResult(@Header("X-Auth-Token") String token,
                                                     @Path("survey_id") String surveyId);

    @GET("api/surveys/{survey_id}/attachments/wheel")
    Call<ResponseDownloadSurveyAttachmentData> getAttachment(@Header("X-Auth-Token") String token,
                                                             @Path("survey_id") String surveyId);
}
