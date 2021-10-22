package com.example.covidtrackerbd;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface myApi {
    @GET("countries")
    Call<List<Countries>> getContries();

}