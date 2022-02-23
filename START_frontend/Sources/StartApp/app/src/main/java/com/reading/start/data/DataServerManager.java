package com.reading.start.data;

import com.google.gson.Gson;
import com.reading.start.AppCore;
import com.reading.start.data.api.APIChild;
import com.reading.start.data.api.APIData;
import com.reading.start.data.api.APILogin;
import com.reading.start.data.api.APISurveys;
import com.reading.start.data.entity.DataChildrenItem;
import com.reading.start.data.entity.DataLoginRequest;
import com.reading.start.data.entity.DataPasswordRecoveryRequest;
import com.reading.start.data.entity.DataPasswordResetRequest;
import com.reading.start.data.entity.DataSurveyItem;
import com.reading.start.data.response.ObjectResponseData;
import com.reading.start.data.response.ResponseCheckAuthTokenData;
import com.reading.start.data.response.ResponseChildrenData;
import com.reading.start.data.response.ResponseChildrenIdData;
import com.reading.start.data.response.ResponseChildrenSurveysData;
import com.reading.start.data.response.ResponseConsent;
import com.reading.start.data.response.ResponseLanguages;
import com.reading.start.data.response.ResponseLoginData;
import com.reading.start.data.response.ResponsePasswordRecovery;
import com.reading.start.data.response.ResponsePasswordReset;
import com.reading.start.data.response.ResponseStates;
import com.reading.start.data.response.ResponseSurveyIdData;
import com.reading.start.general.TLog;
import com.reading.start.tests.Server;
import com.reading.start.tests.ServerLog;

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

    public static DataServerManager getInstance() {
        if (mInstance == null) {
            mInstance = new DataServerManager();
        }

        return mInstance;
    }

    public static void reset() {
        mInstance = null;
    }

    private DataServerManager() {
        mServer = Server.getServer(AppCore.getInstance());
    }

    /**
     * Request to login of social worker.
     */
    public ResponseLoginData login(String email, String password) {
        ServerLog.log("APP-login", "START");
        ServerLog.log("APP-login", "PARAMETERS: email=" + email + "; password=" + password);
        ResponseLoginData result = null;

        try {
            if (email != null && password != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APILogin api = retrofit.create(APILogin.class);
                DataLoginRequest data = new DataLoginRequest(email, password);

                Gson gson = new Gson();
                String content = gson.toJson(data);
                ServerLog.log("APP-login", "CONTENT: " + content);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);

                Call<ResponseLoginData> call = api.login(body);
                Response<ResponseLoginData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-login", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();
                        ServerLog.log("APP-login", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-login", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-login", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-login", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-login", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-login", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-login", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseLoginData> converter = retrofit.responseBodyConverter(
                                ResponseLoginData.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-login", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-login", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-login", "END");
        return result;
    }

    /**
     * Request to check token.
     */
    public ResponseCheckAuthTokenData checkAuthToke(String token) {
        ServerLog.log("APP-checkAuthToke", "START");
        ServerLog.log("APP-checkAuthToke", "PARAMETERS: token=" + token);
        ResponseCheckAuthTokenData result = null;

        try {
            if (token != null && token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APILogin api = retrofit.create(APILogin.class);
                Call<ResponseCheckAuthTokenData> call = api.checkAuthToken(token);
                Response<ResponseCheckAuthTokenData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-checkAuthToke", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();
                        ServerLog.log("APP-checkAuthToke", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-checkAuthToke", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-checkAuthToke", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-checkAuthToke", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-checkAuthToke", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-checkAuthToke", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-checkAuthToke", "SERVER ERROR BODY: " + response.errorBody().string());
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-checkAuthToke", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-checkAuthToke", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-checkAuthToke", "END");
        return result;
    }

    /**
     * Request for password recovery.
     */
    public ResponsePasswordRecovery passwordRecovery(String email, String birthDate) {
        ServerLog.log("APP-passwordRecovery", "START");
        ServerLog.log("APP-passwordRecovery", "PARAMETERS: email=" + email + "; birthDate=" + birthDate);
        ResponsePasswordRecovery result = null;

        try {
            if (email != null && birthDate != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APILogin api = retrofit.create(APILogin.class);

                DataPasswordRecoveryRequest data = new DataPasswordRecoveryRequest(email, birthDate);
                Gson gson = new Gson();
                String content = gson.toJson(data);
                ServerLog.log("APP-passwordRecovery", "CONTENT: " + content);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);

                Call<ResponsePasswordRecovery> call = api.passwordRecovery(body);
                Response<ResponsePasswordRecovery> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-passwordRecovery", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-passwordRecovery", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-passwordRecovery", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-passwordRecovery", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-passwordRecovery", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-passwordRecovery", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-passwordRecovery", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-passwordRecovery", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponsePasswordRecovery> converter = retrofit.responseBodyConverter(
                                ResponsePasswordRecovery.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-passwordRecovery", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-passwordRecovery", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-passwordRecovery", "END");
        return result;
    }

    /**
     * Request for password reset.
     */
    public ResponsePasswordReset passwordReset(String id, String password, String hash) {
        ServerLog.log("APP-passwordReset", "START");
        ServerLog.log("APP-passwordReset", "PARAMETERS: id=" + id + "; password=" + password + "; hash=" + hash);
        ResponsePasswordReset result = null;

        try {
            if (id != null && password != null && hash != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APILogin api = retrofit.create(APILogin.class);

                DataPasswordResetRequest data = new DataPasswordResetRequest(password, hash);
                Gson gson = new Gson();
                String content = gson.toJson(data);
                ServerLog.log("APP-passwordReset", "CONTENT: " + content);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);

                Call<ResponsePasswordReset> call = api.passwordReset(id, body);
                Response<ResponsePasswordReset> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-passwordReset", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-passwordReset", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-passwordReset", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-passwordReset", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-passwordReset", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-passwordReset", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-passwordReset", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-passwordReset", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponsePasswordReset> converter = retrofit.responseBodyConverter(
                                ResponsePasswordReset.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-passwordReset", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-passwordReset", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-passwordReset", "END");
        return result;
    }

    /**
     * Request to fetch children.
     */
    public ResponseChildrenData getChildren(String token, String sinceDateTime) {
        ServerLog.log("APP-getChildren", "START");
        ServerLog.log("APP-getChildren", "PARAMETERS: token=" + token + "; sinceDateTime=" + sinceDateTime);
        ResponseChildrenData result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIChild api = retrofit.create(APIChild.class);

                Call<ResponseChildrenData> call = api.getChildren(token, sinceDateTime);
                Response<ResponseChildrenData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-getChildren", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-getChildren", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getChildren", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getChildren", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-getChildren", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getChildren", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getChildren", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-getChildren", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseChildrenData> converter = retrofit.responseBodyConverter(
                                ResponseChildrenData.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-getChildren", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-getChildren", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-getChildren", "END");
        return result;
    }

    /**
     * Request to fetch child's surveys.
     */
    public ResponseChildrenSurveysData getChildrenSurveys(String token, String childId) {
        ServerLog.log("APP-getChildrenSurveys", "START");
        ServerLog.log("APP-getChildrenSurveys", "PARAMETERS: token=" + token + "; childId=" + childId);
        ResponseChildrenSurveysData result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIChild api = retrofit.create(APIChild.class);

                Call<ResponseChildrenSurveysData> call = api.getChildrenSurveys(token, childId);
                Response<ResponseChildrenSurveysData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-getChildrenSurveys", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-getChildrenSurveys", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getChildrenSurveys", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getChildrenSurveys", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-getChildrenSurveys", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getChildrenSurveys", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getChildrenSurveys", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-getChildrenSurveys", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseChildrenSurveysData> converter = retrofit.responseBodyConverter(
                                ResponseChildrenSurveysData.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-getChildrenSurveys", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-getChildrenSurveys", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-getChildrenSurveys", "END");
        return result;
    }

    /**
     * Request to update child info.
     */
    public boolean updateChildren(String token, DataChildrenItem child) {
        ServerLog.log("APP-updateChildren", "START");
        ServerLog.log("APP-updateChildren", "PARAMETERS: token=" + token);
        boolean result = false;
        boolean successRequest = false;

        try {
            if (token != null && child != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIChild api = retrofit.create(APIChild.class);
                Gson gson = new Gson();
                String content = gson.toJson(child);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);

                Call<ObjectResponseData> call = api.updateChildren(token, String.valueOf(child.getId()), body);
                Response<ObjectResponseData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-updateChildren", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ObjectResponseData data = response.body();
                        result = data.isSuccess();

                        ServerLog.log("APP-updateChildren", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-updateChildren", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-updateChildren", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-updateChildren", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-updateChildren", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-updateChildren", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-updateChildren", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ObjectResponseData> converter = retrofit.responseBodyConverter(
                                ObjectResponseData.class, new Annotation[0]);

                        try {
                            ObjectResponseData data = converter.convert(response.errorBody());
                            result = data.isSuccess();
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-updateChildren", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-updateChildren", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("APP-updateChildren", "END");
        return result;
    }

    /**
     * Request to add new child.
     */
    public int addChildren(String token, DataChildrenItem child) {
        ServerLog.log("APP-addChildren", "START");
        ServerLog.log("APP-addChildren", "PARAMETERS: token=" + token);
        int result = -1;
        boolean successRequest = false;

        try {
            if (token != null && child != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIChild api = retrofit.create(APIChild.class);
                Gson gson = new Gson();
                String content = gson.toJson(child);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);
                Call<ResponseChildrenIdData> call = api.addChildren(token, body);
                Response<ResponseChildrenIdData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-addChildren", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseChildrenIdData data = response.body();

                        if (data.isSuccess()) {
                            result = Integer.parseInt(data.getData().getChildId());
                        }

                        ServerLog.log("APP-addChildren", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-addChildren", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-addChildren", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-addChildren", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-addChildren", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-addChildren", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-addChildren", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ObjectResponseData> converter = retrofit.responseBodyConverter(
                                ObjectResponseData.class, new Annotation[0]);

                        try {
                            ObjectResponseData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                           // TLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-addChildren", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-addChildren", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("APP-addChildren", "END");
        return result;
    }

    /**
     * Request to ann new survey.
     */
    public int addSurvey(String token, DataSurveyItem survey) {
        ServerLog.log("APP-addSurvey", "START");
        ServerLog.log("APP-addSurvey", "PARAMETERS: token=" + token);
        int result = -1;
        boolean successRequest = false;

        try {
            if (token != null && survey != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Gson gson = new Gson();
                String content = gson.toJson(survey);
                ServerLog.log("APP-addSurvey", "CONTENT: " + content);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);
                Call<ResponseSurveyIdData> call = api.addSurvey(token, body);
                Response<ResponseSurveyIdData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-addSurvey", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ResponseSurveyIdData data = response.body();

                        if (data.isSuccess()) {
                            result = Integer.parseInt(data.getData().getSurveyId());
                        }

                        ServerLog.log("APP-addSurvey", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-addSurvey", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-addSurvey", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-addSurvey", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-addSurvey", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-addSurvey", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-addSurvey", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ObjectResponseData> converter = retrofit.responseBodyConverter(
                                ObjectResponseData.class, new Annotation[0]);

                        try {
                            ObjectResponseData data = converter.convert(response.errorBody());
                            // no need any action
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-addSurvey", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-addSurvey", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("APP-addSurvey", "END");
        return result;
    }

    /**
     * Request to update survey.
     */
    public boolean updateSurveyCompleted(String token, DataSurveyItem survey) {
        ServerLog.log("APP-updateSurveyCompleted", "START");
        ServerLog.log("APP-updateSurveyCompleted", "PARAMETERS: token=" + token);
        boolean result = false;
        boolean successRequest = false;

        try {
            if (token != null && survey != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APISurveys api = retrofit.create(APISurveys.class);
                Gson gson = new Gson();
                String content = gson.toJson(survey);
                ServerLog.log("APP-updateSurveyCompleted", "CONTENT: " + content);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), content);

                Call<ObjectResponseData> call = api.updateSurveyCompleted(token, String.valueOf(survey.getId()), body);
                Response<ObjectResponseData> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-updateSurveyCompleted", "SERVER HEADER: " + response.headers().toString());
                    }

                    successRequest = true;

                    if (response.body() != null) {
                        ObjectResponseData data = response.body();
                        result = data.isSuccess();

                        ServerLog.log("APP-updateSurveyCompleted", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-updateSurveyCompleted", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-updateSurveyCompleted", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-updateSurveyCompleted", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-updateSurveyCompleted", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-updateSurveyCompleted", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-updateSurveyCompleted", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ObjectResponseData> converter = retrofit.responseBodyConverter(
                                ObjectResponseData.class, new Annotation[0]);

                        try {
                            ObjectResponseData data = converter.convert(response.errorBody());
                            result = data.isSuccess();
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-updateSurveyCompleted", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-updateSurveyCompleted", "REQUEST SUCCESS: " + (!successRequest ? "fail" : "success"));
        ServerLog.log("APP-updateSurveyCompleted", "END");
        return result;
    }

    /**
     * Request for fetch consent form.
     */
    public ResponseConsent getConsent(String token) {
        ServerLog.log("APP-getConsent", "START");
        ServerLog.log("APP-getConsent", "PARAMETERS: token=" + token);
        ResponseConsent result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIData api = retrofit.create(APIData.class);

                Call<ResponseConsent> call = api.getConsent(token);
                Response<ResponseConsent> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-getConsent", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-getConsent", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getConsent", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getConsent", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-getConsent", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getConsent", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getConsent", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-getConsent", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseConsent> converter = retrofit.responseBodyConverter(
                                ResponseConsent.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-getConsent", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-getConsent", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-getConsent", "END");
        return result;
    }

    /**
     * Request to fetch list of the states.
     */
    public ResponseStates getStates(String token) {
        ServerLog.log("APP-getStates", "START");
        ServerLog.log("APP-getStates", "PARAMETERS: token=" + token);
        ResponseStates result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIData api = retrofit.create(APIData.class);

                Call<ResponseStates> call = api.getStates(token);
                Response<ResponseStates> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-getStates", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-getStates", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getStates", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getStates", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-getStates", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getStates", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getStates", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-getStates", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseStates> converter = retrofit.responseBodyConverter(
                                ResponseStates.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-getStates", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-getStates", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-getStates", "END");
        return result;
    }

    /**
     * Request to fetch list of languages.
     */
    public ResponseLanguages getLanguages(String token) {
        ServerLog.log("APP-getLanguages", "START");
        ServerLog.log("APP-getLanguages", "PARAMETERS: token=" + token);
        ResponseLanguages result = null;

        try {
            if (token != null) {
                Retrofit retrofit = Server.getRetrofit(mServer);
                APIData api = retrofit.create(APIData.class);

                Call<ResponseLanguages> call = api.getLanguages(token);
                Response<ResponseLanguages> response = call.execute();

                if (response != null) {
                    if (response.headers() != null) {
                        ServerLog.log("APP-getLanguages", "SERVER HEADER: " + response.headers().toString());
                    }

                    if (response.body() != null) {
                        result = response.body();

                        ServerLog.log("APP-getLanguages", "SERVER SUCCESS: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getLanguages", "SERVER SUCCESS RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getLanguages", "SERVER SUCCESS MESSAGE: " + response.message());
                        }
                    } else if (response.errorBody() != null) {
                        ServerLog.log("APP-getLanguages", "SERVER ERROR: " + String.valueOf(response.code()));

                        if (response.raw() != null) {
                            ServerLog.log("APP-getLanguages", "SERVER ERROR RAW MESSAGE: " + response.raw().toString());
                        } else {
                            ServerLog.log("APP-getLanguages", "SERVER ERROR MESSAGE: " + response.message());
                        }

                        if (response.errorBody() != null) {
                            ServerLog.log("APP-getLanguages", "SERVER ERROR BODY: " + response.errorBody().string());
                        }

                        Converter<ResponseBody, ResponseLanguages> converter = retrofit.responseBodyConverter(
                                ResponseLanguages.class, new Annotation[0]);

                        try {
                            result = converter.convert(response.errorBody());
                        } catch (IOException e) {
                            //TLog.e(TAG, e);
                        }
                    }

                    if (result != null) {
                        result.setCode(response.code());
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            ServerLog.log("APP-getLanguages", "ERROR: " + e.getMessage());
        }

        ServerLog.log("APP-getLanguages", "REQUEST SUCCESS: " + (result == null ? "fail" : "success"));
        ServerLog.log("APP-getLanguages", "END");
        return result;
    }
}
