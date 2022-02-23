package com.reading.start.tests.test_motor_following.data.api;

import com.reading.start.tests.test_motor_following.data.response.AttachmentIdResponseData;
import com.reading.start.tests.test_motor_following.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_motor_following.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_motor_following.data.response.ResultIdResponseData;

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

/**
 * Represent API for working test data
 */
public interface APISurveys {
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

    @GET("api/surveys/{survey_id}/results/motoric_following")
    Call<ResponseDownloadSurveyResultData> getResult(@Header("X-Auth-Token") String token,
                                                     @Path("survey_id") String surveyId);

    @GET("api/surveys/{survey_id}/attachments/motoric_following")
    Call<ResponseDownloadSurveyAttachmentData> getAttachment(@Header("X-Auth-Token") String token,
                                                             @Path("survey_id") String surveyId);
}
