package gohackathon.unworthy;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import gohackathon.unworthy.rest_api.ApiClient;
import gohackathon.unworthy.rest_api.model.Routes;
import gohackathon.unworthy.rest_api.services.RoutesService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, SensorEventListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteFragment auFragment;
    private FloatingActionButton FABTrace;
    private Float mDeclination;
    private double mBearing;
    private SensorManager sensorManager;
    private Sensor rotationVector;
    public static Routes routes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        auFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        auFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                //mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress().toString()));
                Location currLocation = getCurrentLocation();
                LatLng newLatLng = new LatLng(currLocation.getLatitude(),currLocation.getLongitude());
                //mMap.addPolyline(new PolylineOptions().add(newLatLng,place.getLatLng()).color(Color.RED));
                ApiClient.getInstance().init();
//                ApiClient.getInstance().getRoutesService().getRoutes(-6.202383,106.779548,-6.202383,106.779548,
//                        Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)).enqueue(new Callback<Routes>() {
//                    @Override
//                    public void onResponse(Call<Routes> call, Response<Routes> response) {
//                        Log.v("StringDebug","data: "+response.body().getRoutesData().size());
//                        Routes routesData = response.body();
//                        ArrayList<LatLng> latLang = new ArrayList<LatLng>();
//                        for(int i = 0 ; i < routesData.getRoutesData().size(); i++){
//                            Double lat = Double.parseDouble(routesData.getRoutesData().get(i).getLatitude());
//                            Double lang = Double.parseDouble(routesData.getRoutesData().get(i).getLongitude());
//                            latLang.add(new LatLng(lat,lang));
//                        }
//                        mMap.addPolyline(new PolylineOptions().addAll(latLang));
//                    }
//
//                    @Override
//                    public void onFailure(Call<Routes> call, Throwable t) {
//
//                    }
//                });

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.17.10.77/")
                        .client(getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RoutesService service = retrofit.create(RoutesService.class);
                Call<Routes> call = service.getRoutes(-6.202383,106.779548,-6.202383,106.779548, Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                call.enqueue(new Callback<Routes>() {
                    @Override
                    public void onResponse(Call<Routes> call, Response<Routes> response) {
                        routes = response.body();
                        Log.v("StringDebug","data: "+response.body().getRoutesData().size());
                        Log.v("StringDebug","data: "+response.body().getStatus());
                        Log.v("StringDebug","data: "+response.body().getRoutesData().isEmpty());

                    }

                    @Override
                    public void onFailure(Call<Routes> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onError(Status status) {

            }
        });
        FABTrace = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }

    private static OkHttpClient getOkHttpClient(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        return okClient;
    }

    private void initWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 123);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void initGoogleApi(GoogleMap googleMap) {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.zoomBy(15.f));

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                setCurrentLocation();
                return true;
            }
        });
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("StringDebug","CheckResume");
        sensorManager.registerListener(this,rotationVector,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startTrace(View v){
        FABTrace.setVisibility(View.INVISIBLE);
        onLocationChanged(getCurrentLocation());
    }

    private void moveCamOnRoute(){
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(getCurrentLocation().getLatitude(),getCurrentLocation().getLongitude())).
                tilt(90).
                zoom(17).
                bearing((float) mBearing).
                build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private Location getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 123);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

        }
        Location currLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        return currLocation;
    }

    private void setCurrentLocation(){
        Location currLocation = getCurrentLocation();
        // Add a marker in Sydney and move the camera
        if(currLocation!=null){
            Log.v("StringDebug","containLocation");

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(new LatLng(getCurrentLocation().getLatitude(),getCurrentLocation().getLongitude())).
                    zoom(15).
                    build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        initGoogleApi(googleMap);
        initWindow();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("StringDebug","ConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("StringDebug","connectedLocation");
        setCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("StringDebug","ConnectionSuspended");
    }


    @Override
    public void onLocationChanged(Location location) {
        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );
        mDeclination =  field.getDeclination();
        if(FABTrace.getVisibility()==View.INVISIBLE){
            moveCamOnRoute();
        }
        else {
            setCurrentLocation();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.v("StringDebug","Sensor Rotate");
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && mDeclination!=null){
            float[] mRotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(mRotationMatrix,sensorEvent.values);
            float[] orientation =  new float[3];
            SensorManager.getOrientation(mRotationMatrix,orientation);
            mBearing = Math.toDegrees(orientation[0]) + mDeclination;

        }
        if(routes != null)
            Log.v("StringDebug","Routes Size : " + routes.getRoutesData().size());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
