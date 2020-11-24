package com.mlt.dtc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.adapter.WeatherListViewAdapter;
import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import static com.mlt.dtc.utility.Constant.weatherDetailsListviewAList;


public
class WeatherFragment extends DialogFragment {

//    ArrayList<FetchWeatherResponse.FetchWeather> weatherDetailsListviewAList;
//    ArrayList<FetchWeatherForecastResponse.FetchWeatherForecast> weatherDetailsRecylerviewAlist;

//    FetchWeatherResponse.FetchWeather weatherDetailsList;
//    FetchWeatherForecastResponse.FetchWeatherForecast weatherDetailsRecycler;
    ListView listView;
    TextView tv_DeviceTime, tv_Devicemdd, tv_tempMax, tv_tempMin, tv_Humid, tv_wind, tv_Degree, tv_Weather;
    WeatherListViewAdapter weatherListviewAdapter;
//    WeatherRecyclerViewAdapter weatherRecyclerViewAdapter;
    RecyclerView recyclerView;
    ImageView iv_weatherImage;
    public String degree;
    Gson gson;
    private String JSON_RESULT = "";
//    public List<FetchWeatherResponse.FetchWeather> fetchweather;
//    public List<FetchWeatherFCastResponse> fetchweatherforecast;
    FetchCurrentWeatherResponse.Response objfetchWeatherResponse;
    private HashMap<String, Integer> hmweatherimageDay, hmweatherimagedayForecast;
    String[] forecastTime = new String[]{"12:00AM", "3:00AM", "6:00AM", "9:00AM", "12:00PM", "3:00PM", "6:00PM", "9:00PM"};
    Boolean isNight;
    Button btnBack;
    MainActivity mainActivity = new MainActivity();
    String ClassName;
    View view;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.weatherfragment, container, false);

        listView = view.findViewById(R.id.lv_cities);
        recyclerView = view.findViewById(R.id.rv_weather);
        tv_DeviceTime = view.findViewById(R.id.tv_currenttime);
        tv_Devicemdd = view.findViewById(R.id.tv_currentmdd);
        tv_tempMax = view.findViewById(R.id.tv_tempmax);
        tv_tempMin = view.findViewById(R.id.tv_tempmin);
        tv_Humid = view.findViewById(R.id.tv_humid);
        tv_wind = view.findViewById(R.id.tv_wind);
        tv_Degree = view.findViewById(R.id.tv_degree);
        tv_Weather = view.findViewById(R.id.tv_weather);
        iv_weatherImage = view.findViewById(R.id.iv_weatherimage);
        btnBack = view.findViewById(R.id.btnback);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        //get Class Name
        ClassName = getClass().getCanonicalName();

//        objfetchWeatherResponse = new FetchWeatherResponse.FetchWeather();
//        fetchweather = new ArrayList<>();
//        weatherDetailsRecylerviewAlist = new ArrayList<>();

        //2017-October-16 09:33 AM
        Date now = new Date();
        String YMD = new SimpleDateFormat("MMMM dd yyyy").format(now); //EEEE =day name will appear
        String Time = new SimpleDateFormat("HH:mm a").format(now);

        tv_DeviceTime.setText(Time);
        tv_Devicemdd.setText(YMD);

        //Check Day or Night
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        isNight = hour < 6 || hour > 18;

        try {
            hmweatherimageDay = new HashMap<>();
            if (isNight == false) {
                hmweatherimageDay.put("clear sky", R.drawable.wdayclearskybig);
                hmweatherimageDay.put("few clouds", R.drawable.wdayfewcloudsbig);
            } else {
                hmweatherimageDay.put("clear sky", R.drawable.wnightclearbig);
                hmweatherimageDay.put("few clouds", R.drawable.wnightfewcloudsbig);
            }

            hmweatherimageDay.put("scattered clouds", R.drawable.wscatteredcloudsbig);
            hmweatherimageDay.put("broken clouds", R.drawable.wbrokencloudsbig);
            hmweatherimageDay.put("shower rain", R.drawable.wshowerrainbig);
            hmweatherimageDay.put("rain", R.drawable.wdaynightrainbig);
            hmweatherimageDay.put("thunderstorm", R.drawable.wdaynnightthunderbig);
            hmweatherimageDay.put("mist", R.drawable.wmistbig);
            hmweatherimageDay.put("haze", R.drawable.wmistbig);

            hmweatherimagedayForecast = new HashMap<>();

            hmweatherimagedayForecast.put("scattered clouds", R.drawable.wscatteredclouds);
            hmweatherimagedayForecast.put("broken clouds", R.drawable.wbrokenclouds);
            hmweatherimagedayForecast.put("shower rain", R.drawable.wshowerrain);
            hmweatherimagedayForecast.put("rain", R.drawable.wdaynightrain);
            hmweatherimagedayForecast.put("light rain", R.drawable.wdaynightrain);
            hmweatherimagedayForecast.put("thunderstorm", R.drawable.wdaynnightthunder);
            hmweatherimagedayForecast.put("mist", R.drawable.wmist);
            hmweatherimagedayForecast.put("haze", R.drawable.wmist);


            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            weatherListviewAdapter = new WeatherListViewAdapter(weatherDetailsListviewAList, getContext());
            listView.setAdapter(weatherListviewAdapter);

            setWeatherbyCity(0);

//            weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(weatherDetailsRecylerviewAlist, getContext());
//            recyclerView.setAdapter(weatherRecyclerViewAdapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {

                objfetchWeatherResponse = weatherDetailsListviewAList.get(position);
//                getweatherForecastUpdate("fetchWeatherForecast", 2039);
                setWeatherbyCity(position);

            });


            btnBack.setOnClickListener(view1 -> {

                if (getFragmentManager()!=null){
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content, MainBannerVideoFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                }

            });
        } catch (Exception e) {
        }

    }



    private void setWeatherbyCity(int Position) {
        objfetchWeatherResponse = weatherDetailsListviewAList.get(Position);
        tv_tempMax.setText(Math.round(objfetchWeatherResponse.getTempMax()) + " \u2103");
        tv_tempMin.setText(Math.round(objfetchWeatherResponse.getTempMin()) + " \u2103");
        tv_Humid.setText(String.valueOf(Math.round(objfetchWeatherResponse.getHumidity())));
        tv_wind.setText(String.valueOf(Math.round(objfetchWeatherResponse.getWindSpeed())));
        degree=String.valueOf(Math.round(objfetchWeatherResponse.getTemperature()));
        tv_Degree.setText(String.valueOf(Math.round(objfetchWeatherResponse.getTemperature())));
        tv_Weather.setText(objfetchWeatherResponse.getWeather());
        iv_weatherImage.setImageResource(hmweatherimageDay.get(objfetchWeatherResponse.getWeather()));
    }

//    private void getweatherForecastUpdate(String functionname, int cityId) {
//
//        String DateTime = getdateTime();
//        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
//        authenticateRequest.setUsername("nips_inventory");
//        authenticateRequest.setPassword("nips@2016");
//        authenticateRequest.setSecureHash(Common.getencryptedsecureHash(DateTime, "B15m1L2ah"));
//        authenticateRequest.setTimestamp(DateTime);
//
//        gson = new GsonBuilder().create();
//        final JSONObject req = new JSONObject();
//        try {
//            req.put("username", authenticateRequest.getUsername());
//            req.put("password", authenticateRequest.getPassword());
//            req.put("timestamp", authenticateRequest.getTimestamp());
//            req.put("secureHash", authenticateRequest.getSecureHash());
//            req.put("cityId", cityId);
//
//        } catch (JSONException e) {
//
//        }
//
//        final JSONObject object = new JSONObject();
//        try {
//            object.put("request", req);
//
//        } catch (JSONException e) {
//
//        }
//
//        JSON_RESULT = functionname;
//        Context context = getContext();
//        ServiceWrapper.serviceCall(NetworkURL.URL + functionname, object, context, new ServiceCallback() {
//            @Override
//            public void onSuccess(JSONObject obj) throws JSONException {
//
//                try {
//                    FetchWeatherFCastResponse response = gson.fromJson(obj.getJSONObject(JSON_RESULT + "Result").toString(), FetchWeatherFCastResponse.class);
//                    fetchweatherforecast = response.getResponse();
//
//                    for (int i = 0; i < fetchweatherforecast.size(); i++) {
//                        weatherDetailsRecycler = new FetchWeatherForecastResponse.FetchWeatherForecast();
//                        weatherDetailsRecycler.setTimeTo(forecastTime[i]);
//                        weatherDetailsRecycler.setTemperature(Math.round(fetchweatherforecast.get(i).getTemperature() - 273.15));
//                        //"12:00AM","3:00AM","6:00AM","9:00AM","12:00PM","3:00PM","6:00PM","9:00PM"
//                        if (forecastTime[i].equals("12:00AM") || forecastTime[i].equals("3:00AM") || forecastTime[i].equals("6:00PM")
//                                || forecastTime[i].equals("9:00PM")) {
//                            hmweatherimagedayForecast.put("clear sky", R.drawable.wnightclear);
//                            hmweatherimagedayForecast.put("few clouds", R.drawable.wnightfewclouds);
//                        } else {
//                            hmweatherimagedayForecast.put("clear sky", R.drawable.wdayclearsky);
//                            hmweatherimagedayForecast.put("few clouds", R.drawable.wdayfewclouds);
//                        }
//                        weatherDetailsRecycler.setWeatherImage(hmweatherimagedayForecast.get(fetchweatherforecast.get(i).getWeather()));
//                        weatherDetailsRecylerviewAlist.add(weatherDetailsRecycler);
//                    }
//                    weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(weatherDetailsRecylerviewAlist, getContext());
//                    recyclerView.setAdapter(weatherRecyclerViewAdapter);
//                    weatherDetailsRecylerviewAlist = new ArrayList<>();
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onFailure(String obj) {
//
//            }
//        });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            listView=null;
//            weatherRecyclerViewAdapter = null;
            tv_DeviceTime.setText(null);
            tv_Devicemdd.setText(null);
            tv_Degree.setText(null);
            tv_Humid.setText(null);
            tv_Weather.setText(null);
            tv_tempMin.setText(null);
            tv_tempMax.setText(null);
            tv_wind.setText(null);


            iv_weatherImage = null;
            tv_DeviceTime = null;
            tv_Devicemdd = null;
            tv_Degree = null;
            tv_Humid = null;
            tv_Weather = null;
            tv_tempMax = null;
            tv_tempMin = null;
            tv_wind = null;

            btnBack = null;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try{
//            listView = null;
            recyclerView = null;

            weatherDetailsListviewAList.clear();
//            weatherDetailsRecylerviewAlist.clear();
//            fetchweather.clear();
//            fetchweatherforecast.clear();
//            weatherRecyclerViewAdapter = null;
            weatherListviewAdapter = null;


            btnBack.setOnClickListener(null);
            iv_weatherImage.setImageDrawable(null);


            tv_DeviceTime.setText(null);
            tv_Devicemdd.setText(null);
            tv_Degree.setText(null);
            tv_Humid.setText(null);
            tv_Weather.setText(null);
            tv_tempMin.setText(null);
            tv_tempMax.setText(null);
            tv_wind.setText(null);


            iv_weatherImage = null;
            tv_DeviceTime = null;
            tv_Devicemdd = null;
            tv_Degree = null;
            tv_Humid = null;
            tv_Weather = null;
            tv_tempMax = null;
            tv_tempMin = null;
            tv_wind = null;

            btnBack = null;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        view = null;
//        System.gc();


    }


}
