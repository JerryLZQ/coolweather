package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jerry on 2017/8/22.
 */

/**
 * json文件中Basic段内容格式：
 * “basic":{
 *     "city":"苏州”,
 *     "id":"CN101190401",
 *     "update":{
 *         "loc":"2017-08-22 17:46"
 *     }
 * }
 */

public class Basic {

    @SerializedName("city")
    public String cityName; //json数据中的city映射到cityName

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }


}
