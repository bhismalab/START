package com.reading.start.tests.test_eye_tracking.data.api;

import com.reading.start.tests.test_eye_tracking.data.response.AttachmentIdResponseData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseAssessmentPairsData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseAssessmentSlideData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_eye_tracking.data.response.ResultIdResponseData;

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
    @GET("api/assestments/eye_tracking_pairs")
    Call<ResponseAssessmentPairsData> getContentPairs(@Header("X-Auth-Token") String token,
                                                      @Query("since_datetime") String sinceDateTime);

    @GET("api/assestments/eye_tracking_slide")
    Call<ResponseAssessmentSlideData> getContentSlide(@Header("X-Auth-Token") String token,
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

    @GET("api/surveys/{survey_id}/results/eye_tracking_pairs")
    Call<ResponseDownloadSurveyResultData> getResultPair(@Header("X-Auth-Token") String token,
                                                         @Path("survey_id") String surveyId);

    @GET("api/surveys/{survey_id}/attachments/eye_tracking_pairs")
    Call<ResponseDownloadSurveyAttachmentData> getAttachmentPair(@Header("X-Auth-Token") String token,
                                                                 @Path("survey_id") String surveyId);

    @GET("api/surveys/{survey_id}/results/eye_tracking_slide")
    Call<ResponseDownloadSurveyResultData> getResultSlide(@Header("X-Auth-Token") String token,
                                                          @Path("survey_id") String surveyId);

    @GET("api/surveys/{survey_id}/attachments/eye_tracking_slide")
    Call<ResponseDownloadSurveyAttachmentData> getAttachmentSlide(@Header("X-Auth-Token") String token,
                                                                  @Path("survey_id") String surveyId);
}
