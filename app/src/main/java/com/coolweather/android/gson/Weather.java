package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jerry on 2017/8/23.
 */

public class Weather {
    public String status;
    public AQI aqi;
    public Basic basic;
    public Forecast forecast;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> ForecastList;

}
