package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Jerry on 2017/8/22.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private static final String mServerURL = "http://guolin.tech/api/china";

    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private Button mBackButton;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    private List<String> mDataList = new ArrayList<>();
    private List<Province> mProvinceList;   // 省份列表
    private List<City> mCityList;           // 城市列表
    private List<County> mCountyList;       // 县级列表

    private Province mSelectedProvince;      //被选中的省份
    private City mSelectedCity;              //被选中的城市

    private int mCurrentLevel;              //当前被选中的级别


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area, container, false);

        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);

        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 点击某项
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCurrentLevel == LEVEL_PROVINCE){ //点击某个省份，显示该省份下的城市列表
                   mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                }else if(mCurrentLevel == LEVEL_CITY){ //点击某个城市，显示该城市下的县列表
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                }else if(mCurrentLevel == LEVEL_COUNTY){ //点击某个县，显示详细天气信息
                    String weatherId = mCountyList.get(position).getWeatherId();

                    if(getActivity() instanceof  MainActivity) {
                        // 如果当前是在主活动，直接打开天气详情活动
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);  //启动WeatherActivity活动
                        getActivity().finish(); //结束当前活动(ChooseAreaFragment)
                    }else if(getActivity() instanceof WeatherActivity){
                        //如果当前已经在天气详情活动（通过导航页选择县），刷新当前县天气详情
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);

                    }
                }
            }
        });

        // 点击后退按键
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentLevel == LEVEL_COUNTY){ //从县列表后退，显示城市列表
                    queryCities();
                }else if(mCurrentLevel == LEVEL_CITY){  //从城市列表后退，显示省份列表
                    queryProvinces();
                }
            }
        });

        queryProvinces();   //启动时显示省份信息
    }

    // 查询省份信息并显示
    private void queryProvinces(){
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE); //隐藏后退按键
        mProvinceList = DataSupport.findAll(Province.class); //从数据库的Province表获得全部省份
        if(mProvinceList.size() > 0){ //存在省份信息
            mDataList.clear();
            for(Province province : mProvinceList){
                mDataList.add(province.getProvinceName());  //添加全部省份到列表
            }
            mAdapter.notifyDataSetChanged();    //刷新列表
            mListView.setSelection(0);          //光标指向第一行
            mCurrentLevel = LEVEL_PROVINCE;     //当前选中级别为“省”
        }else{  //如果不存在省份信息，则从服务器上查询
            queryFromServer(mServerURL, "province");
        }
    }

    // 查询城市信息并显示
    private void queryCities(){
        mTitleText.setText(mSelectedProvince.getProvinceName());    //所属省份名称
        mBackButton.setVisibility(View.VISIBLE);    //显示后退按键
        mCityList = DataSupport.where("provinceid = ?", String.valueOf(mSelectedProvince.getId()))
                .find(City.class);  //从City表查询与当前选中的省份的ID相同的城市列表
        if(mCityList.size() > 0){
            mDataList.clear();
            for (City city : mCityList){
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        }else{
            queryFromServer(mServerURL + "/" + mSelectedProvince.getProvinceCode(), "city");
        }
    }

    // 查询县级信息并显示
    private void queryCounties(){
        mTitleText.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityid = ?", String.valueOf(mSelectedCity.getId()))
                .find(County.class);
        if(mCountyList.size() > 0){
            mDataList.clear();
            for (County county : mCountyList){
                mDataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(mServerURL + "/" + mSelectedProvince.getProvinceCode()
                    + "/" + mSelectedCity.getCityCode(), "county");
        }

    }

    private void queryFromServer(String address, final String type){
        showProgressDialog(); //显示加载进度条
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;

                if("province".equals(type)){        //查询省份信息
                    //使用自定义的类Utility的方法HandleProvinceResponse
                    //从服务器得到省份列表，并存入数据库
                    //操作是否成功将返回一个boolean
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){      //查询城市信息
                    result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());
                }else if("county".equals(type)){    //查询县信息
                    result = Utility.handleCountyResponse(responseText, mSelectedCity.getId());
                }

                if(result){
                    //切换回主线程，操作UI
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog(); //关闭加载进度条

                            // 由于前面从服务器读取数据，存入数据库了，需要重新执行对应的方式显示对应的列表
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //切换回主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    //显示加载进度条
    private void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);   //不允许点击外围区域取消进度条
        }
        mProgressDialog.show();
    }

    //关闭加载进度条
    private void closeProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}
