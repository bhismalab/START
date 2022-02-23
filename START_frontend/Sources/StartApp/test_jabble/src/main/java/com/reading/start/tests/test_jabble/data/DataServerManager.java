package com.reading.start.tests.test_jabble.data;

import android.content.Context;

import com.google.gson.Gson;
import com.reading.start.tests.Server;
import com.reading.start.tests.ServerLog;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_jabble.data.api.APISurveys;
import com.reading.start.tests.test_jabble.data.entity.DataSurveyResultItem;
import com.reading.start.tests.test_jabble.data.response.ObjectResponseData;
import com.reading.start.tests.test_jabble.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_jabble.data.response.ResultIdResponseData;

import java.io.IOException;
import java.lang.annotation.Annotation;

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
    public int addSurveyResult(String token, DataSurveyResultItem surveyResult) {
        ServerLog.log("JABBLE-addSurveyResult", "START");
        ServerLog.log("JABBLE-addSurveyResult", "PARAMETERS: token=" + token);
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
                        ServerLog.log("JABBLE-addSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResultIdResponseData data = response.body();

                        if (data.isSuccess()) {
                            result = data.getData().getResultId();
                        }

                        ServerLog.log("JABBLE-addSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("JABBLE-addSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("JABBLE-addSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("JABBLE-addSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("JABBLE-addSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("JABBLE-addSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("JABBLE-addSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("JABBLE-addSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("JABBLE-addSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("JABBLE-addSurveyResult", "END");
        return result;
    }

    /**
     * Gets survey result from server
     */
    public ResponseDownloadSurveyResultData getSurveyResult(String token, int surveyId) {
        ServerLog.log("JABBLE-getSurveyResult", "START");
        ServerLog.log("JABBLE-getSurveyResult", "PARAMETERS: token=" + token + "; surveyId=" + surveyId);
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
                        ServerLog.log("JABBLE-getSurveyResult", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseDownloadSurveyResultData data = response.body();

                        if (data.isSuccess()) {
                            result = data;
                        }

                        ServerLog.log("JABBLE-getSurveyResult", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("JABBLE-getSurveyResult", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("JABBLE-getSurveyResult", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("JABBLE-getSurveyResult", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("JABBLE-getSurveyResult", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("JABBLE-getSurveyResult", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("JABBLE-getSurveyResult", "SERVER ERROR BODY: " + response.errorBody().string());
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
            ServerLog.log("JABBLE-getSurveyResult", "ERROR: " + e.getMessage());
        }

        ServerLog.log("JABBLE-getSurveyResult", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("JABBLE-getSurveyResult", "END");
        return result;
    }
}
