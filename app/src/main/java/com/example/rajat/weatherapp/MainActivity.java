package com.example.rajat.weatherapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    HttpURLConnection connection = null, forimg = null;
    InputStream inputStream = null;

    EditText location;
    ImageView img;
    String place, icon;
    Button btn;
    String jsonStr;
    Bitmap pic;
    TextView temp, realLoc, des, humid, minTemp, maxTemp, snrise, snset, wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);

        temp = (TextView) findViewById(R.id.temp);

        des = (TextView) findViewById(R.id.desc);
        img = (ImageView) findViewById(R.id.img);
        realLoc = (TextView) findViewById(R.id.location);
        wind = (TextView) findViewById(R.id.wind);
        humid = (TextView) findViewById(R.id.humidity);
        minTemp = (TextView) findViewById(R.id.minTemp);
        maxTemp = (TextView) findViewById(R.id.maxTemp);
        snrise = (TextView) findViewById(R.id.snrise);
        snset = (TextView) findViewById(R.id.snset);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                place = String.valueOf(location.getText());
                place = place.trim();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=" + place + "&appid=88df7272323d701e2e3bcfc570693d97";
                try {

                    connection = (HttpURLConnection) (new URL(BASE_URL)).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.connect();

                    StringBuilder stringBuffer = new StringBuilder();
                    inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    inputStream.close();
                    connection.disconnect();
                    jsonStr = stringBuffer.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {

                }


                double temprature = 0, min = 0, max = 0, speed = 0;
                String description = null, con = null, place = null, iconid = null;
                int humidity = 0;
                long r = 0, s = 0;
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        place = jsonObject.getString("name");
                        JSONArray weather = jsonObject.getJSONArray("weather");
                        JSONObject wea = weather.getJSONObject(0);
                        description = wea.getString("description");
                        iconid = wea.getString("icon");
                        JSONObject main = jsonObject.getJSONObject("main");
                        temprature = main.getDouble("temp");
                        temprature -= 273.15;
                        temprature = (Math.round(temprature * 100.0) / 100.0);
                        humidity = main.getInt("humidity");
                        JSONObject wind = jsonObject.getJSONObject("wind");
                        speed = wind.getDouble("speed");
                        speed *= 2.23;
                        speed = (Math.round(speed * 100.0) / 100.0);
                        min = main.getDouble("temp_min");
                        min -= 273.15;
                        min = (Math.round(min * 100.0) / 100.0);
                        max = main.getDouble("temp_max");
                        max -= 273.15;
                        max = (Math.round(max * 100.0) / 100.0);
                        JSONObject sys = jsonObject.getJSONObject("sys");
                        con = sys.getString("country");
                        r = sys.getLong("sunrise");
                        s = sys.getLong("sunset");
                        icon = iconid;


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Date time = new Date(r * 1000L);
                    Date time1 = new Date(s * 1000L);

                    temp.setText(String.valueOf(temprature + "°C"));
                    realLoc.setText(place + "," + con);
                    des.setText(description);
                    wind.setText(String.valueOf(speed + "Miles/Hour"));
                    humid.setText(String.valueOf(humidity + " %"));
                    minTemp.setText(String.valueOf(min + "°C"));
                    maxTemp.setText(String.valueOf(max + "°C"));
                    snrise.setText(String.valueOf(time));
                    snset.setText(String.valueOf(time1));

                    String IMG_URL = "http://openweathermap.org/img/w/" + iconid + ".png";

                    try {
                        forimg = (HttpURLConnection) (new URL(IMG_URL)).openConnection();
                        forimg.setRequestMethod("GET");
                        forimg.connect();
                        InputStream input = forimg.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        img.setImageBitmap(myBitmap);

                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}