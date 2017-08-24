package com.coolweather.android.gson;

/**
 * Created by Jerry on 2017/8/23.
 */

import com.google.gson.annotations.SerializedName;

/**
 * json文件中Forecast段内容格式：
 * “daily_forecast":[
 *  {
 *      "date":"2017-08-22",
 *      "cond":{
 *          "txt_d":"阵雨”
 *      },
 *      "tmp":{
 *          "max":"34",
 *          "min":"27"
 *      }
 *  },
 *  {
 *      "date":"2017-08-23",
 *      "cond":{
 *          "txt_d":"阵雨”
 *      },
 *      "tmp":{
 *          "max":"34",
 *          "min":"27"
 *      }
 *  },
 *  .......
 *  ]
 *
 */

// 说明：daily_forecast字段为未来几天的天气，为数组。本实体类仅定义出单天天气，引用时再用集合类型即可。

public class Forecast {
    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature{
        public String max;
        public String min;
    }
}
