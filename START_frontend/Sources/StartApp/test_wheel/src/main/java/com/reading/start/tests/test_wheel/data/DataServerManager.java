package com.reading.start.tests.test_wheel.data;

import android.content.Context;

import com.google.gson.Gson;
import com.reading.start.tests.Server;
import com.reading.start.tests.ServerLog;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_wheel.data.api.APISurveys;
import com.reading.start.tests.test_wheel.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_wheel.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_wheel.data.response.AttachmentIdResponseData;
import com.reading.start.tests.test_wheel.data.response.ObjectResponseData;
import com.reading.start.tests.test_wheel.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_wheel.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_wheel.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_wheel.data.response.ResultIdResponseData;

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
import retrofit2.converter.gson.GsonConverterFactory;

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
    public ResponseAssessmentData getContent(String token, String sinceDateTime) {
        ServerLog.log("WHEEL-getContent", "START");
        ServerLog.log("WHEEL-getContent", "PARAMETERS: token=" + token);
        ResponseAssessmentData result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);

                Call<ResponseAssessmentData> call = api.getContent(token, sinceDateTime);
                Response<ResponseAssessmentData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("WHEEL-getContent", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("WHEEL-getContent", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-getContent", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-getContent", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("WHEEL-getContent", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-getContent", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-getContent", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("WHEEL-getContent", "SERVER ERROR BODY: " + response.errorBody().string());
                        }
                        
                        Converter<ResponseBody, ResponseAssessmentData> converter = retrofit.responseBodyConverter(
                                ResponseAssessmentData.class, new Annotation[0]);

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
            ServerLog.log("WHEEL-getContent", "ERROR: " + e.getMessage());
        }

        ServerLog.log("WHEEL-getContent", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("WHEEL-getContent", "END");
        return result;
    }

    /**
     * Send survey results to server
     */
    public int addSurveyResult(String token, DataUploadSurveyResultItem surveyResult) {
        ServerLog.log("WHEEL-addSurveyResult", "START");
        ServerLog.log("WHEEL-addSurveyResult", "PARAMETERS: token=" + token);
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
                        ServerLog.log("WHEEL-addSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResultIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            result = data.getData().getResultId();
                        }

                        ServerLog.log("WHEEL-addSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-addSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-addSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("WHEEL-addSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-addSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-addSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("WHEEL-addSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("WHEEL-addSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("WHEEL-addSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("WHEEL-addSurveyResult", "END");
        return result;
    }

    /**
     * Send survey results to server
     */
    public int addSurveyAttachment(String token, String surveyId, DataUploadSurveyAttachmentItem surveyResult, File file) {
        ServerLog.log("WHEEL-addSurveyAttachment", "START");
        ServerLog.log("WHEEL-addSurveyAttachment", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
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
                        ServerLog.log("WHEEL-addSurveyAttachment", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        AttachmentIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            result = data.getData().getAttachmentId();
                        }

                        ServerLog.log("WHEEL-addSurveyAttachment", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-addSurveyAttachment", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-addSurveyAttachment", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("WHEEL-addSurveyAttachment", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-addSurveyAttachment", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-addSurveyAttachment", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("WHEEL-addSurveyAttachment", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("WHEEL-addSurveyAttachment", "ERROR: " + e.getMessage());
        }

        ServerLog.log("WHEEL-addSurveyAttachment", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("WHEEL-addSurveyAttachment", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyResultData getSurveyResult(String token, int surveyId) {
        ServerLog.log("WHEEL-getSurveyResult", "START");
        ServerLog.log("WHEEL-getSurveyResult", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        ResponseDownloadSurveyResultData result = null;
        boolean successRequest = false;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Call<ResponseDownloadSurveyResultData> call = api.getResult(token, String.valueOf(surveyId));
                Response<ResponseDownloadSurveyResultData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("WHEEL-getSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseDownloadSurveyResultData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("WHEEL-getSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-getSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-getSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("WHEEL-getSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-getSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-getSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("WHEEL-getSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("WHEEL-getSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("WHEEL-getSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("WHEEL-getSurveyResult", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyAttachmentData getSurveyAttachment(String token, int surveyId) {
        ServerLog.log("WHEEL-getSurveyAttachment", "START");
        ServerLog.log("WHEEL-getSurveyAttachment", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
        ResponseDownloadSurveyAttachmentData result = null;
        boolean successRequest = false;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Call<ResponseDownloadSurveyAttachmentData> call = api.getAttachment(token, String.valueOf(surveyId));
                Response<ResponseDownloadSurveyAttachmentData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("WHEEL-getSurveyAttachment", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;
                    
                    if (response.body() != null) {
                        ResponseDownloadSurveyAttachmentData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("WHEEL-getSurveyAttachment", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-getSurveyAttachment", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-getSurveyAttachment", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("WHEEL-getSurveyAttachment", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("WHEEL-getSurveyAttachment", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("WHEEL-getSurveyAttachment", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("WHEEL-getSurveyAttachment", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("WHEEL-getSurveyAttachment", "ERROR: " + e.getMessage());
        }

        ServerLog.log("WHEEL-getSurveyAttachment", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("WHEEL-getSurveyAttachment", "END");
        return result;
    }
}
