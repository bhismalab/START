package com.reading.start.tests.test_eye_tracking.data;

import android.content.Context;

import com.google.gson.Gson;
import com.reading.start.sdk.general.SdkLog;
import com.reading.start.tests.Server;
import com.reading.start.tests.ServerLog;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_eye_tracking.data.api.APISurveys;
import com.reading.start.tests.test_eye_tracking.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_eye_tracking.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_eye_tracking.data.response.AttachmentIdResponseData;
import com.reading.start.tests.test_eye_tracking.data.response.ObjectResponseData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseAssessmentPairsData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseAssessmentSlideData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_eye_tracking.data.response.ResultIdResponseData;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Hashtable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Represent all methods for working with server API.
 */
public class DataServerManager {
    private static final String TAG = DataServerManager.class.getSimpleName();

    private static DataServerManager mInstance = null;

    private final String mServer;

    public static DataServerManager getInstance(Context context) {
        if (mInstance == null ||
                (mInstance != null && !mInstance.mServer.equals(Server.getServer(context)))) {
            mInstance = new DataServerManager(context);
        }

        return mInstance;
    }

    private DataServerManager(Context context) {
        mServer = Server.getServer(context);
    }

    /**
     * Gets test content
     */
    public ResponseAssessmentPairsData getContentPairs(String token, String sinceDateTime) {
        ServerLog.log("EYE-TRACKING-getContentPairs", "START");
        ServerLog.log("EYE-TRACKING-getContentPairs", "PARAMETERS: token=" + token);
        ResponseAssessmentPairsData result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);

                Call<ResponseAssessmentPairsData> call = api.getContentPairs(token, sinceDateTime);
                Response<ResponseAssessmentPairsData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-getContentPairs", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseAssessmentPairsData> converter = retrofit.responseBodyConverter(
                                ResponseAssessmentPairsData.class, new Annotation[0]);

                        try {
                            if (response.errorBody() != null) {
                                result = converter.convert(response.errorBody());
                            }
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-getContentPairs", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-getContentPairs", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-getContentPairs", "END");
        return result;
    }

    /**
     * Gets test content
     */
    public ResponseAssessmentSlideData getContentSlide(String token, String sinceDateTime) {
        ResponseAssessmentSlideData result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);

                Call<ResponseAssessmentSlideData> call = api.getContentSlide(token, sinceDateTime);
                Response<ResponseAssessmentSlideData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseAssessmentSlideData> converter = retrofit.responseBodyConverter(
                                ResponseAssessmentSlideData.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    /**
     * Send survey results to server
     */
    public int addSurveyResult(String token, DataUploadSurveyResultItem surveyResult) {
        ServerLog.log("EYE-TRACKING-addSurveyResult", "START");
        ServerLog.log("EYE-TRACKING-addSurveyResult", "PARAMETERS: token=" + token);
        int result = -1;
        boolean successRequest = false;

        try {
            if (token != null && surveyResult != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Gson gson = new Gson();
                String content = gson.toJson(surveyResult);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);
                Call<ResultIdResponseData> call = api.addResult(token, surveyResult.getSurveyId(), body);
                Response<ResultIdResponseData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResultIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            result = data.getData().getResultId();
                        }

                        ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-addSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
                        }
                        Converter<ResponseBody, ObjectResponseData> converter = retrofit.responseBodyConverter(
                                ObjectResponseData.class, new Annotation[0]);
                        try {
                            ObjectResponseData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-addSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-addSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-addSurveyResult", "END");
        return result;
    }

    /**
     * Send survey results to server
     */
    public int addSurveyAttachment(String token, String surveyId, DataUploadSurveyAttachmentItem surveyResult, File file) {
        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "START");
        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        int result = -1;
        boolean successRequest = false;

        try {
            if (token != null && surveyResult != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Gson gson = new Gson();
                String content = gson.toJson(surveyResult);
                RequestBody jsonBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);
                RequestBody fileBody = null;

                if (file != null) {
                    fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                }

                Hashtable<String, RequestBody> names = new Hashtable();
                names.put("data", jsonBody);

                if (fileBody != null) {
                    names.put("file\"; filename=\"" + file.getName(), fileBody);
                }

                Call<AttachmentIdResponseData> call = api.addAttachment(token, surveyId, names);
                Response<AttachmentIdResponseData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        SdkLog.d(TAG, "addSurveyAttachment: body not null");
                        AttachmentIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            SdkLog.d(TAG, "addSurveyAttachment: success");
                            result = data.getData().getAttachmentId();
                            SdkLog.d(TAG, "addSurveyAttachment, attachment_id:" + result);
                        }

                        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-addSurveyAttachment", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ObjectResponseData> converter = retrofit.responseBodyConverter(
                                ObjectResponseData.class, new Annotation[0]);

                        try {
                            ObjectResponseData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-addSurveyAttachment", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-addSurveyAttachment", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyResultData getSurveyResultPair(String token, int surveyId) {
        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "START");
        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        ResponseDownloadSurveyResultData result = null;
        boolean successRequest = false;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Call<ResponseDownloadSurveyResultData> call = api.getResultPair(token, String.valueOf(surveyId));
                Response<ResponseDownloadSurveyResultData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        ResponseDownloadSurveyResultData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyResultPair", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        result = null;
                        Converter<ResponseBody, ResponseDownloadSurveyResultData> converter = retrofit.responseBodyConverter(
                                ResponseDownloadSurveyResultData.class, new Annotation[0]);

                        try {
                            ResponseDownloadSurveyResultData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-getSurveyResultPair", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-getSurveyResultPair", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyResultData getSurveyResultSlide(String token, int surveyId) {
        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "START");
        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        ResponseDownloadSurveyResultData result = null;
        boolean successRequest = false;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Call<ResponseDownloadSurveyResultData> call = api.getResultSlide(token, String.valueOf(surveyId));
                Response<ResponseDownloadSurveyResultData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseDownloadSurveyResultData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "SERVER ERROR BODY: " + response.errorBody().string());
                        }
                        result = null;
                        Converter<ResponseBody, ResponseDownloadSurveyResultData> converter = retrofit.responseBodyConverter(
                                ResponseDownloadSurveyResultData.class, new Annotation[0]);

                        try {
                            ResponseDownloadSurveyResultData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-getSurveyResultSlide", "END");
        return result;
    }

    public ResponseDownloadSurveyAttachmentData getSurveyAttachmentPair(String token, int surveyId) {
        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "START");
        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        ResponseDownloadSurveyAttachmentData result = null;
        boolean successRequest = false;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Call<ResponseDownloadSurveyAttachmentData> call = api.getAttachmentPair(token, String.valueOf(surveyId));
                Response<ResponseDownloadSurveyAttachmentData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;
                    if (response.body() != null) {
                        ResponseDownloadSurveyAttachmentData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "SERVER ERROR BODY: " + response.errorBody().string());
                        }
                        result = null;
                        Converter<ResponseBody, ResponseDownloadSurveyAttachmentData> converter = retrofit.responseBodyConverter(
                                ResponseDownloadSurveyAttachmentData.class, new Annotation[0]);

                        try {
                            ResponseDownloadSurveyAttachmentData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-getSurveyAttachmentPair", "END");
        return result;
    }

    public ResponseDownloadSurveyAttachmentData getSurveyAttachmentSlide(String token, int surveyId) {
        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "START");
        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        ResponseDownloadSurveyAttachmentData result = null;
        boolean successRequest = false;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Call<ResponseDownloadSurveyAttachmentData> call = api.getAttachmentSlide(token, String.valueOf(surveyId));
                Response<ResponseDownloadSurveyAttachmentData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseDownloadSurveyAttachmentData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        result = null;
                        Converter<ResponseBody, ResponseDownloadSurveyAttachmentData> converter = retrofit.responseBodyConverter(
                                ResponseDownloadSurveyAttachmentData.class, new Annotation[0]);

                        try {
                            ResponseDownloadSurveyAttachmentData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TestLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "ERROR: " + e.getMessage());
        }

        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-getSurveyAttachmentSlide", "END");
        return result;
    }
}
