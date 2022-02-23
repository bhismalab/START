package com.reading.start.tests.test_motor_following.data;

import android.content.Context;

import com.google.gson.Gson;
import com.reading.start.tests.Server;
import com.reading.start.tests.ServerLog;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_motor_following.data.api.APISurveys;
import com.reading.start.tests.test_motor_following.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_motor_following.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_motor_following.data.response.AttachmentIdResponseData;
import com.reading.start.tests.test_motor_following.data.response.ObjectResponseData;
import com.reading.start.tests.test_motor_following.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_motor_following.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_motor_following.data.response.ResultIdResponseData;

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
     * Send survey results to server
     */
    public int addSurveyResult(String token, DataUploadSurveyResultItem surveyResult) {
        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "START");
        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "PARAMETERS: token=" + token);
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
                        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResultIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            result = data.getData().getResultId();
                        }

                        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("MOTOR-FOLLOWING-addSurveyResult", "END");
        return result;
    }

    /**
     * Send survey results to server
     */
    public int addSurveyAttachment(String token, String surveyId, DataUploadSurveyAttachmentItem surveyResult, File file) {
        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "START");
        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
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
                        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        AttachmentIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            result = data.getData().getAttachmentId();
                        }

                        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "ERROR: " + e.getMessage());
        }

        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("MOTOR-FOLLOWING-addSurveyAttachment", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyResultData getSurveyResult(String token, int surveyId) {
        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "START");
        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
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
                        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseDownloadSurveyResultData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("MOTOR-FOLLOWING-getSurveyResult", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyAttachmentData getSurveyAttachment(String token, int surveyId) {
        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "START");
        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
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
                        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseDownloadSurveyAttachmentData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "ERROR: " + e.getMessage());
        }

        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("MOTOR-FOLLOWING-getSurveyAttachment", "END");
        return result;
    }
}
