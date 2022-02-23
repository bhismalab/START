package com.reading.start.data.api;

import com.reading.start.data.response.ResponseChildrenData;
import com.reading.start.data.response.ResponseConsent;
import com.reading.start.data.response.ResponseLanguages;
import com.reading.start.data.response.ResponseStates;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Represent API for working with application data.
 */
public interface APIData {
    @GET("api/consent")
    Call<ResponseConsent> getConsent(@Header("X-Auth-Token") String token);

    @GET("api/states")
    Call<ResponseStates> getStates(@Header("X-Auth-Token") String token);

    @GET("api/languages")
    Call<ResponseLanguages> getLanguages(@Header("X-Auth-Token") String token);
}
