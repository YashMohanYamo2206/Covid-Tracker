package com.yash.delta_project.interfaces;
import com.google.gson.JsonObject;
import com.yash.delta_project.gson_converters.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface countries_interface {
    @GET("countries")
    Call<List<country>> getCountries();
    @GET("summary")
    Call<world_data> getWorldData();
}
