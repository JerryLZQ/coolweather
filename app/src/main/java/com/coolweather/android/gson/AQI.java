package com.coolweather.android.gson;

/**
 * Created by Jerry on 2017/8/23.
 */

/**
 * Json文件中AQI段内容格式：
 * “aqi":{
 *     "city":{
 *         "aqi":"44",
 *         "pm25":"13"
 *     }
 * }
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
