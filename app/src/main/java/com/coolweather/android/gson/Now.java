package com.coolweather.android.gson;

/**
 * Created by Jerry on 2017/8/23.
 */

import com.google.gson.annotations.SerializedName;

/**
 * json文件中now段内容格式：
 * “now":{
 *     "tmp":"29”,
  *     "cond":{
 *         "txt":"阵雨"
 *     }
 * }
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
