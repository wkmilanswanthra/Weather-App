package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private TextView cityName, temperature, condition;
    private TextInputEditText cityEdit;
    private RecyclerView weatherList;
    private ImageView backg, icon, searchIcon, helpBtn, abtBtn;

    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter adapter;

    private LocationManager locationManager;
    private int CODE = 1;

    private  String city ;

    private String apiKey = "38ffb76904af415882d120724222612&q"; //API key for www.weatherapi.com

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.home);
        progressBar = findViewById(R.id.loader);
        cityName = findViewById(R.id.city);
        temperature = findViewById(R.id.temp);
        condition = findViewById(R.id.condition);
        cityEdit = findViewById(R.id.edt_city);
        weatherList = findViewById(R.id.recyclerView);
        backg = findViewById(R.id.bg_img);
        icon = findViewById(R.id.icon);
        searchIcon = findViewById(R.id.search);
        helpBtn = findViewById(R.id.help);
        abtBtn = findViewById(R.id.about);

        weatherModalArrayList = new  ArrayList<>();
        adapter = new WeatherAdapter(this,weatherModalArrayList);
        weatherList.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CODE);
        }

        //Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //Use for Device
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);   //Use for Emulator
        city = getCityName(location.getLatitude(), location.getLongitude());
        getWeatherInput(city);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city2 = cityEdit.getText().toString();
                if (city2.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }else {
                    cityName.setText(city);
                    getWeatherInput(city2);
                }
            }
        });
        abtBtn.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), About.class);
            startActivity(intent);
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Help.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, " Permissions granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, " Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double latitude, double longitude){
        String cityName2 = "Not found";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,10);
            for(Address address: addresses){
                if (address!=null){
                    String city = address.getLocality();
                    if (city!=null && !city.equals("")){
                        cityName2 = city;
                        break;
                    }else{
                        Log.d("TAG", "City not found");
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return cityName2;
    }

    private void getWeatherInput( String city){
        String Url = "http://api.weatherapi.com/v1/forecast.json?key="+apiKey+"="+city+"&days=1&aqi=no&alerts=no";
        System.out.println(Url);
        cityName.setText(city.toUpperCase());
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, response -> {
            progressBar.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            weatherModalArrayList.clear();
            try {
                String temp = response.getJSONObject("current").getString("temp_c");
                temperature.setText(temp+"°C");
                int isDay = response.getJSONObject("current").getInt("is_day");
                String condit = response.getJSONObject("current").getJSONObject("condition").getString("text");
                String conditIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                Picasso.get().load("https:"+conditIcon).into(icon);
                condition.setText(condit);
                if (isDay==1){
                    Picasso.get().load("https://images.unsplash.com/photo-1597217190944-3615102a0c6d?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8OHx8ZGF5JTIwc2t5fGVufDB8MXwwfHw%3D&auto=format&fit=crop&w=400&q=60").into(backg);
                }else{
                    Picasso.get().load("https://images.unsplash.com/photo-1599148400620-8e1ff0bf28d8?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8bmlnaHQlMjBza3l8ZW58MHwxfDB8fA%3D%3D&auto=format&fit=crop&w=400&q=60").into(backg);
                }

                JSONObject frcstObj = response.getJSONObject("forecast");
                JSONObject frcst0 = frcstObj.getJSONArray("forecastday").getJSONObject(0);
                JSONArray hourArr = frcst0.getJSONArray("hour");

                for(int i=0;i<hourArr.length();i++){
                    JSONObject hourObj = hourArr.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temperature = hourObj.getString("temp_c");
                    String img = hourObj.getJSONObject("condition").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    weatherModalArrayList.add(new WeatherModal(time, temperature, img, wind));
                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(MainActivity.this, " Enter a valid city name", Toast.LENGTH_SHORT).show();
        });

        requestQueue.add(jsonObjectRequest);
    }
}