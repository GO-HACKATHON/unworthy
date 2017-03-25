package gohackathon.unworthy.rest_api.services;

import gohackathon.unworthy.rest_api.model.Routes;
import retrofit2.Call;

import retrofit2.http.GET;

import retrofit2.http.Path;



public  interface RoutesService {
    @GET("asd/routes/{lat1}/{lon1}/{lat2}/{lon2}/{device_id}")
    Call<Routes> getRoutes(
            @Path("lat1") Double latitude1,
            @Path("lon1") Double longitude1,
            @Path("lat2") Double latitude2,
            @Path("lon2") Double longitude2,
            @Path("device_id") String device_id);

    // @GET("")
    // Call<HotspotResponse> postLocation(
    //         @Query("longitude") Double longitude,
    //         @Query("latitude") Double latitude
    //         );

}
