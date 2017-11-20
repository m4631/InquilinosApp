package com.inquilinosrd.inquilinosmobile.Services;

import com.inquilinosrd.inquilinosmobile.Models.Residence;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InquilinosAPI {
    @GET("api/residences")
    public Call<List<Residence>> getResidences();

    @POST("api/residences")
    public Call<Residence> postResidence(@Body Residence residence);

    @GET("api/residences/{id}")
    public Call<Residence> getResidenceById(@Path("id") int id);

    @GET("api/residences")
    public Call<List<Residence>> getResidencesByEmail(@Query("email") String email);
}
