import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public  interface RoutesService {
    @GET("")
    Call<HotspotResponse> getRoutes(
            @Query("route") String route,
            @Query("lat1") Double latitude1,
            @Query("lon1") Double longitude1,
            @Query("lat2") Double latitude2,
            @Query("lon2") Double longitude2,
            @Query("device_id") String device_id);

    // @GET("")
    // Call<HotspotResponse> postLocation(
    //         @Query("longitude") Double longitude,
    //         @Query("latitude") Double latitude
    //         );

}
