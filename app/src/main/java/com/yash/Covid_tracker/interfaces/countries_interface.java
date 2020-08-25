package com.yash.Covid_tracker.interfaces;

import com.yash.Covid_tracker.gson_converters.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface countries_interface {
    @GET("countries")
    Call<List<country>> getCountries();

    @GET("summary")
    Call<world_data> getWorldData();

    @GET("total/country/{id}?from=2020-03-01T00:00:00Z&to=2022-12-01T00:00:00Z")
    Call<List<countries_details>> getCountryDetail(@Path("id") String country_slug);
}
