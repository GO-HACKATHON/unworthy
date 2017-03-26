package gohackathon.unworthy;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Vibrator;
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
import android.widget.EditText;

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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gohackathon.unworthy.rest_api.ApiClient;
import gohackathon.unworthy.rest_api.model.Routes;
import gohackathon.unworthy.rest_api.services.RoutesService;
import okhttp3.OkHttpClient;
import okhttp3.internal.huc.HttpURLConnectionImpl;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

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
    public boolean activeRoad = false;
    private double startLatVar;
    private double startLangVar;
    private double endLatVar;
    private double endLangVar;
    long starttime = 0;

    private ArrayList<LatLng> currJSONLatlang;
    int SPEED = 3;
    int PLAYER_SPEED = 0;
    double fromX, fromY;
    double nextX,nextY;
    TextView txtSpeed;
    EditText txtTime;
    TextView roadWeather;
    int roadWeatherValue = 0;
    String currWeather = "none";
    int currSpeed = 0;

    Timer timer = new Timer();
    int lastSecond = 0;
    int skip = 0;
    final Handler h = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;
            if( seconds - lastSecond >= SPEED){
                double distance = Math.sqrt(Math.pow((fromX-nextX), 2) + Math.pow((fromY-nextY), 2));
                int kms = (int)(distance/SPEED * 300000);
                currSpeed = kms;
                if(txtSpeed != null){
                    txtSpeed.setText(""+kms);
                }
                lastSecond = seconds;
                int weatherDanger = (currSpeed-20)*roadWeatherValue;
                if(weatherDanger>=120)
                {
                    txtSpeed.setTextColor(Color.RED);
                    vibrate(2000);
                    vibrate(2000);

                }
                else if(weatherDanger>=60){
                    txtSpeed.setTextColor(Color.YELLOW);
                    vibrate(1000);
                }
                else
                {
                    txtSpeed.setTextColor(Color.GREEN);
                }
                skip++;
                functionReDrawPath();
            }
            return false;
        }
    });
    CountDownTimer cdt =  new CountDownTimer(600000, 1000) {

        public void onTick(long millisUntilFinished) {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;
            if( seconds - lastSecond >= SPEED){
                double distance = Math.sqrt(Math.pow((fromX-nextX), 2) + Math.pow((fromY-nextY), 2));
                int kms = (int)(distance/SPEED * 300000);
                currSpeed = kms;
                if(txtSpeed != null){
                    txtSpeed.setText(""+kms);
                }
                lastSecond = seconds;
                int weatherDanger = (currSpeed-20)*roadWeatherValue;
                if(weatherDanger>=120)
                {
                    txtSpeed.setTextColor(Color.RED);
                    vibrate(2000);
                    vibrate(2000);

                }
                else if(weatherDanger>=60){
                    txtSpeed.setTextColor(Color.YELLOW);
                    vibrate(1000);
                }
                else
                {
                    txtSpeed.setTextColor(Color.GREEN);
                }
                skip++;
                functionReDrawPath();
            }
        }

        public void onFinish() {

        }
    };

    public void vibrate(int time){
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
    }

    class firstTask extends TimerTask {

        @Override
        public void run() {
            h.sendEmptyMessage(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        starttime = System.currentTimeMillis();
        timer = new Timer();

        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        txtSpeed.setText("0");
        FABTrace = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        txtTime = (EditText) findViewById(R.id.boxTime);
        txtTime.setText(SPEED+"");
        roadWeather = (TextView) findViewById(R.id.txtRoadWeather);
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
                EditText startLat = (EditText) findViewById(R.id.startLat);
                EditText startLang = (EditText) findViewById(R.id.startLang);
                EditText endLat = (EditText) findViewById(R.id.endLat);
                EditText endLang = (EditText) findViewById(R.id.endLang);
                startLatVar = Double.parseDouble(startLat.getText().toString());
                startLangVar = Double.parseDouble(startLang.getText().toString());
                endLatVar = Double.parseDouble(endLat.getText().toString());
                endLangVar = Double.parseDouble(endLang.getText().toString());
                new proccedData().execute("http://10.17.10.77//asd/routes/"+startLatVar+"/"+startLangVar+"/"+endLatVar+"/"+endLangVar+"/1d"+Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-6.200529,106.786975)));
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
        if(activeRoad==false) {
            FABTrace.setImageResource(R.drawable.ic_fiber_manual_record_red_24dp);
            activeRoad = true;
            //timer.schedule(new firstTask(), 0,1000);
            cdt.start();
            onLocationChanged(getCurrentLocation());
            SPEED = Integer.parseInt(txtTime.getText().toString());
        }
        else
        {
            FABTrace.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            activeRoad=false;
            setCurrentLocation();
        }
    }

    private void moveCamOnRoute(){
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(startLatVar,startLangVar)).
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

    private void functionReDrawPath(){
        Log.v("asd","asd");

        if (currJSONLatlang.size()-2 < 0) return;
        fromX = currJSONLatlang.get(currJSONLatlang.size()-1).latitude;
        nextX = currJSONLatlang.get(currJSONLatlang.size()-2).latitude;

        fromY = currJSONLatlang.get(currJSONLatlang.size()-1).longitude;
        nextY = currJSONLatlang.get(currJSONLatlang.size()-2).longitude;

        currJSONLatlang.remove(currJSONLatlang.size()-1);
        mMap.clear();
        mMap.addPolyline(new PolylineOptions().addAll(currJSONLatlang).color(Color.GREEN));
    }

    public class proccedData extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder builder = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(http.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;

                while((line=reader.readLine())!=null){
                    builder.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<LatLng> latLang = new ArrayList<LatLng>();
            try {
                JSONObject obj = new JSONObject(s);
                JSONArray arr = obj.getJSONArray("routes_data");

                for(int i = 0 ; i <arr.length() ; i++){
                   JSONObject objTemp = arr.getJSONObject(i);
                   Double latitude = Double.parseDouble(objTemp.get("latitude").toString());
                   Double longitude = Double.parseDouble(objTemp.get("longitude").toString());
                   latLang.add(new LatLng(latitude,longitude));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mMap.addPolyline(new PolylineOptions().addAll(latLang).color(Color.GREEN));
            currJSONLatlang = latLang;
            try {
                JSONObject obj = new JSONObject(s);
                JSONArray arr = obj.getJSONArray("event");

                for(int i = 0 ; i <arr.length() ; i++){
                    JSONObject objTemp = arr.getJSONObject(i);
                    if(objTemp.get("type").toString().equals("TrafficJam")) {
                        JSONObject tmpLocation = objTemp.getJSONObject("location");
                        Double latitude = Double.parseDouble(tmpLocation.get("x").toString());
                        Double longitude = Double.parseDouble(tmpLocation.get("y").toString());
                        LatLng tempLatLng = new LatLng(latitude,longitude);
                        mMap.addPolyline(new PolylineOptions().add(tempLatLng).color(Color.RED));
                        mMap.addCircle(new CircleOptions().center(tempLatLng).radius(20).strokeColor(Color.RED).fillColor(Color.RED));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject obj = new JSONObject(s);
                JSONObject objWeather = obj.getJSONObject("weather");
                int weatherLevel = Integer.parseInt(objWeather.get("level").toString());
                roadWeatherValue = weatherLevel;
                switch (weatherLevel){
                    case 1: currWeather = "Clear";
                        break;
                    case 2: currWeather = "Shower";
                        break;
                    case 3: currWeather = "Rain";
                        break;
                    case 4: currWeather = "HeavyRain";
                        break;
                    case 5: currWeather = "ThunderShowers";
                        break;
                }
                roadWeather.setText(currWeather);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(s);
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
        if(activeRoad==true){
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
