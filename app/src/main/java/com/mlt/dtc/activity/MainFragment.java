package com.mlt.dtc.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import android.widget.VideoView;
import com.github.infinitebanner.InfiniteBannerView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mlt.dtc.BuildConfig;
import com.mlt.dtc.R;
import com.mlt.dtc.adapter.OffersRecyclerViewAdapter;
import com.mlt.dtc.adapter.RecyclerviewBottomAdapter;
import com.mlt.dtc.fragment.AdminFragment;
import com.mlt.dtc.fragment.ContactUsFragment;
import com.mlt.dtc.fragment.DriverFragment;
import com.mlt.dtc.fragment.MainBannerVideoFragment;
import com.mlt.dtc.fragment.OffersDialogFragment;
import com.mlt.dtc.fragment.TimeFragment;
import com.mlt.dtc.fragment.TopBannerDialogFragment;
import com.mlt.dtc.adapter.BannerAdapter;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.fragment.TripEndFragment;
import com.mlt.dtc.fragment.TripStartFragment;
import com.mlt.dtc.fragment.WeatherFragment;
import com.mlt.dtc.interfaces.DriverImageListener;
import com.mlt.dtc.interfaces.FareDialogListener;
import com.mlt.dtc.interfaces.FetchWeatherObjectCallback;
import com.mlt.dtc.interfaces.MainVideoBannerListener;
import com.mlt.dtc.interfaces.OverwriteTripFragmentListener;
import com.mlt.dtc.interfaces.TaskListener;
import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.model.request.AuthenticateRequest;
import com.mlt.dtc.pushnotification.MyFirebaseMessagingService;
import com.mlt.dtc.utility.ConfigrationDTC;
import com.mlt.dtc.utility.Constant;
import com.mlt.e200cp.controllers.deviceconfigcontrollers.ConfigurationClass;
import com.mlt.e200cp.controllers.presenters.PresenterClasses;
import com.mlt.e200cp.interfaces.ResultsCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import example.CustomKeyboard.Components.CustomKeyboardView;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import static com.mlt.dtc.common.Common.WriteTextInTextFile;
import static com.mlt.dtc.common.Common.checkPermissionREAD_EXTERNAL_STORAGE;
import static com.mlt.dtc.common.Common.getDateHome;
import static com.mlt.dtc.common.Common.getFetchWeatherResponse;
import static com.mlt.dtc.common.Common.getFilePath;
import static com.mlt.dtc.common.Common.getdateTime;
import static com.mlt.dtc.common.Common.getlistofclickLog;
import static com.mlt.dtc.common.Common.prepareMenuData;
import static com.mlt.dtc.common.Common.topBannerList;
import static com.mlt.dtc.utility.Constant.TAG;
import static com.mlt.dtc.utility.Constant.WeatherTime;


public class MainFragment extends Fragment implements View.OnClickListener, TaskListener,
        RecyclerviewBottomAdapter.ClickListener, OffersRecyclerViewAdapter.RecyclerViewClickListener,
        FareDialogListener, DriverImageListener, MainVideoBannerListener, ResultsCallback {

    private TextView tv_timemainbox, tv_datemainbox, tv_VideoBr, tv_degree;
    private LinearLayout ll_driverinfo, llMenuBottom, llMenuUp, llsideOffers;
    private RecyclerView rvBottomMenu, recycler_view_side_offers;
    public static CustomKeyboardView keyboard;
    private ImageView iv_Driver_Image, weatherimg;
    RelativeLayout rlvideobrbox,relativeLayoutfragment, rlviewpagerMain;
    VideoView videoviewMainBanner;
    private FrameLayout  tripdetail;
    public InfiniteBannerView infiniteBannerView;

    public Boolean isSelected = false;

    int topBannerCount, positionTopBanner, multipletripviewClick;
    public OffersRecyclerViewAdapter adapter_menus = null;
    public static MainFragment mainActivity;
    private GoogleApiClient googleApiClient;
    private int Position, sideOffersCount, DTCServicesCount, mltButtonCount, rtaServicesCount, rlvideoMainBanner, timeCount;
    Fragment mFragment;
    public static int restrict_double_click = 0, count = 0;
    private int driverCount, fareCount, weathermainCount;
    private DialogFragment dialogFragment;
    private static OverwriteTripFragmentListener overwriteTripFragmentListener;
    TripStartFragment tripStartFragment = new TripStartFragment();
    TripEndFragment tripEndFragment = new TripEndFragment();
    MainBannerVideoFragment mainBannerVideoFragment;

    boolean fullscreen = true;
    WindowManager mWindowManager;
    private Double Weather;
    HandlerThread handlerThread;
    private JSONObject object;
    private Handler handler;
    Gson gson = new Gson();
    private String DriverImage;
    private HashMap<String, Integer> hmweatherimageDay;
    private View view;

    private boolean isAppInstalled;


    public static MainFragment getInstance() {

        return mainActivity;
    }

    @NonNull
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_main, container, false);

        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        //Keep the screen on
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handlerThread = new HandlerThread("weather");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());


        mainBannerVideoFragment = new MainBannerVideoFragment();

        //Setting call back method
        mainBannerVideoFragment.setMethodCallBack(this);


        mainActivity = this;

        checkPermissionREAD_EXTERNAL_STORAGE(getContext());

        findViewById(view);

        processOffers();

        topBannerViews(topBannerList());


        setRecyclerViewBottomAdapter();


        getFetchWeather();



        try {
            // String DeviceSerialNumber = "E2C1000063";//E2C1000954 /<-**Prod**/ //"E2C1000590";  /<-**UAT**/

            String DeviceSerialNumber = "E2C1000590";//E2C1000954 /<-**Prod**/ //"E2C1000590";  /<-**UAT**/

            PresenterClasses.getConfigData("ADJD-Configs", DeviceSerialNumber, PreferenceConnector.getPreferences(getContext()), this);

        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }



    private void findViewById(View view) {

        //this text is moving
        weatherimg = view.findViewById(R.id.weathertypeimg);
        tv_degree = view.findViewById(R.id.tv_Degree);
        view.findViewById(R.id.tv_services).setSelected(true);
        llsideOffers = view.findViewById(R.id.llsideoffers);
        rlviewpagerMain = view.findViewById(R.id.viewpagermain);
        llMenuUp = view.findViewById(R.id.llmenuup);
        llMenuBottom = view.findViewById(R.id.llmenubottom);
        relativeLayoutfragment = view.findViewById(R.id.rlcontent);
        keyboard = view.findViewById(R.id.customKeyboardView);
        rvBottomMenu = view.findViewById(R.id.recycler_bottom_menu);
        infiniteBannerView = view.findViewById(R.id.pager);
        rvBottomMenu = view.findViewById(R.id.recycler_bottom_menu);
        tv_timemainbox = view.findViewById(R.id.tv_timemainbox);
        ll_driverinfo = view.findViewById(R.id.ll_driverinfo);
        tv_datemainbox = view.findViewById(R.id.tv_datemainbox);
        tripdetail = view.findViewById(R.id.tripdetail);
        recycler_view_side_offers = view.findViewById(R.id.recycler_view_side_offers);
        view.findViewById(R.id.relative_left_arrow).setOnClickListener(this);
        view.findViewById(R.id.relative_right_arrow).setOnClickListener(this);
        view.findViewById(R.id.img_choose_service).setOnClickListener(this);
        //Setting the image of the driver
        iv_Driver_Image = view.findViewById(R.id.iv_driver_image);
        view.findViewById(R.id.layout_uparrow).setOnClickListener(this);
        view.findViewById(R.id.down_arrow).setOnClickListener(this);
        view.findViewById(R.id.pager).setOnClickListener(this);
        ll_driverinfo.setOnClickListener(this);
        tripdetail.setOnClickListener(this);
        view.findViewById(R.id.ll_time).setOnClickListener(this);
        view.findViewById(R.id.weatherimage).setOnClickListener(this);

    }


    private void processOffers() {
        try {
            adapter_menus = new OffersRecyclerViewAdapter(Common.sideOfferList(), getContext(), this);
            recycler_view_side_offers.setAdapter(adapter_menus);

            recycler_view_side_offers.post(new Runnable() {
                @Override
                public void run() {
                    adapter_menus.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient = getAPIClientInstance();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        try {
            DriverImage = "";//PreferenceConnector.readString(getApplicationContext(), Constant.DriverImage, "");
            if (DriverImage != null || DriverImage.equals("")) {
                //Picasso.with(getApplicationContext()).load(DriverImage).placeholder(R.drawable.dtcdriverphoto).into(iv_Driver_Image);
                Glide.with(getContext()).asBitmap().load(DriverImage).apply(new RequestOptions().placeholder(R.drawable.dtcdriverphoto)).into(iv_Driver_Image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        grantPermission();

        //To prevent Network on main thread exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //        fareFragment.setMethodcallbackmultipleViewClick(this);
        MyFirebaseMessagingService.setcardValuesCallBackMethod(this);
        MyFirebaseMessagingService.setDriverImageCallBackMethod(this);

        tv_datemainbox.setText(getDateHome());
        Common.DateTimeRunning(tv_timemainbox);

        String versionName = String.valueOf(BuildConfig.VERSION_NAME);


        PreferenceConnector.writeString(getContext(), ConfigrationDTC.APK_VERSION, versionName);

        PreferenceConnector.RemoveItem(getContext(), Constant.Language);
    }



    @Override
    public void recyclerViewListClicked(View v, int position) {
        restrict_double_click = restrict_double_click + 1;

        if (restrict_double_click == 1) {
            Position = position;
            sideOffersCount++;
            PreferenceConnector.writeInteger(getContext(), Constant.SideBannerCount, sideOffersCount);
            PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameSideAds);

            Constant.ButtonClicked = Constant.nameSideAds;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);

            try {
                OffersDialogFragment offersDialogFragment = new OffersDialogFragment();
                Bundle bundlepos = new Bundle();
                bundlepos.putInt(Constant.PositionSelectedOffers, position);
//                bundlepos.putSerializable(Constant.ArrayImagesSelectedOffers, Common.sideOfferList());
                offersDialogFragment.setArguments(bundlepos);
                offersDialogFragment.show(getFragmentManager(), "");

            } catch (Exception e) {

            }
        }
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    private void setRecyclerViewBottomAdapter() {
        RecyclerviewBottomAdapter bottomAdapter = new RecyclerviewBottomAdapter(getContext(), prepareMenuData());
        bottomAdapter.setOnItemClickListener(this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvBottomMenu.setLayoutManager(horizontalLayoutManager);
        rvBottomMenu.setAdapter(bottomAdapter);


    }

    private void topBannerViews(ArrayList<TopBannerObject> topBannerList) {
        BannerAdapter bannerAdapter = new BannerAdapter(topBannerList, getContext(), true);
        infiniteBannerView.setAdapter(bannerAdapter);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative_left_arrow:
                Common.leftArrow(infiniteBannerView);
                break;
            case R.id.relative_right_arrow:
                Common.rightArrow(infiniteBannerView);
                break;
            case R.id.pager:
                if (!isSelected) {
                    isSelected = true;
                    topBannerCount++;

                    PreferenceConnector.writeInteger(getContext(), Constant.TopBannerCount, topBannerCount);
                    PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameTopBanner);

                    Constant.ButtonClicked = Constant.nameTopBanner;
                    WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);

                    try {
                        TopBannerDialogFragment topBannerDialogFragment = new TopBannerDialogFragment();
                        Bundle bundlepos = new Bundle();
                        bundlepos.putInt(Constant.PositionTopBanner, positionTopBanner);
//                        bundlepos.putSerializable(Constant.TopImagesSelectedOffers, topBannerList());
                        topBannerDialogFragment.setArguments(bundlepos);
                        topBannerDialogFragment.show(getFragmentManager(), "");

                    } catch (Exception e) {
                    }
                }
                break;
            case R.id.img_choose_service:
                count++;
                if (count == 10) {
                    mFragment = AdminFragment.newInstance();
                    addFragment();
                }
                break;
            case R.id.ll_driverinfo:
                driverCount++;

                ll_driverinfo.setEnabled(false);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                PreferenceConnector.writeInteger(getContext(), Constant.DriverCount, driverCount);
                PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameDriver);
                Constant.ButtonClicked = Constant.nameDriver;
                WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());
                DriverFragment driverFragment = new DriverFragment();
                driverFragment.show(getFragmentManager(), "");
                break;
            case R.id.tripdetail:
                fareCount++;
                tripdetail.setEnabled(false);

                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                PreferenceConnector.writeInteger(getContext(), Constant.FareCount, fareCount);
                PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameFare);
                Constant.ButtonClicked = Constant.nameFare;
                WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());
                dialogFragment = new TripEndFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.TripEndServiceCall, true);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "");
                multipletripviewClick = 1;
                break;

            case R.id.layout_uparrow:
                recycler_view_side_offers.scrollBy(0, -50);
                break;
            case R.id.down_arrow:
                recycler_view_side_offers.scrollBy(0, 50);
                break;
            case R.id.ll_time:
                timeCount++;
                view.findViewById(R.id.ll_time).setEnabled(false);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                PreferenceConnector.writeInteger(getContext(), Constant.TimeCount, timeCount);
                PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameTime);
                Constant.ButtonClicked = Constant.nameTime;
                WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());
                dialogFragment = new TimeFragment();
                dialogFragment.show(getFragmentManager(), "");
                break;
            case R.id.weatherimage:
                try {
                    if (Weather.equals("")){

                    }else {
                        weathermainCount++;
                        PreferenceConnector.writeInteger(getContext(), Constant.WeatherCount, weathermainCount);
                        PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameWeather);
                        Constant.ButtonClicked = Constant.nameWeatherinfo;
                        WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                        getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());
                        mFragment = WeatherFragment.newInstance();
                        addFragment();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }


    @Override
    public void onFinished(String result) {
        ll_driverinfo.setEnabled(true);
        view.findViewById(R.id.ll_time).setEnabled(true);
        view.findViewById(R.id.weatherimage).setEnabled(true);
        tripdetail.setEnabled(true);
    }

    @Override
    public void onItemClick(int position, View v) {
        Intent i;

        if (position == 0) {
            mltButtonCount++;

            PreferenceConnector.writeInteger(getContext(), Constant.MLTButtonCount, mltButtonCount);
            PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameMLTButton);
            Constant.ButtonClicked = Constant.nameMLTButton;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());
            mFragment = new ContactUsFragment();
            addFragment();
        } else if (position == 1) {
            try {

                // Use package name which we want to check
                isAppInstalled = appInstalledOrNot("example.rta");

                if(isAppInstalled) {

                    rtaServicesCount++;
                    PreferenceConnector.writeInteger(getContext(), Constant.RTAServicesCount, rtaServicesCount);
                    PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameRTAServices);

                    Constant.ButtonClicked = Constant.nameRTAServices;
                    WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                    Common.getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());

                    i = new Intent(Intent.ACTION_MAIN);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("isFromDTC", true);
                    i.setComponent(new ComponentName("example.rta", "rtaservices.RTAMainActivity"));
                    startActivity(i);

                } else {

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (position == 2) {
            DTCServicesCount++;
            PreferenceConnector.writeInteger(getContext(), Constant.RTAServicesCount, DTCServicesCount);
            PreferenceConnector.writeString(getContext(), Constant.ButtonClicked, Constant.nameDTCServices);

            Constant.ButtonClicked = Constant.nameDTCServices;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getContext(), Constant.ButtonClicked, getdateTime());

        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    //Set Call back to close the opened dialog
    public static void dialogdismissCallBackMethod(OverwriteTripFragmentListener CallBack) {
        overwriteTripFragmentListener = CallBack;
    }


    //Call back when get the data open the dialog automatically
    @Override
    public void FareCallBackMethod(String Eventcode, boolean TripEndCallService, String TripCode) {
        //TripEndCallService is true if the notification end comes
        Bundle bundle = new Bundle();
        try {
            if (Constant.TripStartEventCode.equalsIgnoreCase(TripCode)) {

                Fragment fragmentA = getFragmentManager().findFragmentByTag("tripstartfragment");
                if (fragmentA == null) {
                    //tripStartTime = new Date();
                    //Close the dialog
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                    }
                    // DialogFragment.show() will take care of adding the fragment
                    // in a transaction.  We also want to remove any currently showing
                    // dialog, so make our own transaction and take care of that here.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                    tripStartFragment.setArguments(bundle);
                    tripStartFragment.show(getFragmentManager(), "tripstartfragment");
                } else {
                    Date tripStartTime = new Date();
                    //Close the dialog
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                        Thread.sleep(1000);
                        bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                        tripStartFragment.setArguments(bundle);
                        tripStartFragment.show(getFragmentManager(), "tripstartfragment");
                        Toast.makeText(getContext(), "Trip Start Fragment already exists", Toast.LENGTH_SHORT).show();
                    }

                }

            } else if
            (Constant.TripEndEventCode.equalsIgnoreCase(TripCode)) {

                Fragment fragmentB = getFragmentManager().findFragmentByTag("tripendfragment");
                if (fragmentB == null) {
                    //Close the dialog
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                    }
                    bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                    tripEndFragment.setArguments(bundle);
                    tripEndFragment.show(getFragmentManager(), "tripendfragment");

                } else {
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                        Thread.sleep(1000);
                        bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                        tripEndFragment.setArguments(bundle);
                        tripEndFragment.show(getFragmentManager(), "tripendfragment");
                        Toast.makeText(getContext(), "Trip End Fragment already exists", Toast.LENGTH_SHORT).show();

                    }
                }
                //}
            }
        } catch (Exception ex) {
            Log.e("TTTT", ex.getLocalizedMessage() + "");
        }
    }

    public void addFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void DriverImageCallBackMethod(String driverImage, Context context) {
        String DriverImage = driverImage;
        getActivity().runOnUiThread(() -> {
            // Can call RequestCreator.into here
            //Picasso.with(Context).load(DriverImage).placeholder(R.drawable.dtcdriverphoto).into(iv_Driver_Image);
            Glide.with(context).asBitmap().load(DriverImage).apply(new RequestOptions().placeholder(R.drawable.dtcdriverphoto)).dontAnimate().into(iv_Driver_Image);
        });
        PreferenceConnector.writeString(getContext(), Constant.DriverImage, driverImage);

    }


    @Override
    public void onResume() {
        super.onResume();
        mFragment = MainBannerVideoFragment.newInstance();
        addFragment();

//        HideNavigationBar();
    }

    @Override
    public void onPause() {
        super.onPause();
//        handler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().finish();
            rlvideobrbox = null;
            mFragment = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void MainVideoBannerallBackMethod(ImageView imageview, RelativeLayout relativeLayout, TextView textView, int videobox, VideoView videoView) {
        rlvideobrbox = relativeLayout;
        tv_VideoBr = textView;
        rlvideoMainBanner = videobox;
        videoviewMainBanner = videoView;

        if (fullscreen) {
            // Common.startStopWatch(getApplicationContext());
            imageview.setImageResource(0);
            imageview.setBackgroundResource(R.drawable.ic_fullscreen_exit_black_36dp);
            recycler_view_side_offers.setVisibility(View.GONE);
            llMenuBottom.setVisibility(View.GONE);
            llMenuUp.setVisibility(View.GONE);
            rlviewpagerMain.setVisibility(View.GONE);
            llsideOffers.setVisibility(View.GONE);

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

            ViewGroup.LayoutParams videoLayout1Params = relativeLayoutfragment.getLayoutParams();
            videoLayout1Params.width = displayMetrics.widthPixels;
            videoLayout1Params.height = displayMetrics.heightPixels;
            fullscreen = false;

            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayoutfragment.setLayoutParams(relativeParams);
        } else {
            relativeLayoutfragment.setBackgroundColor(Color.WHITE);
            imageview.setImageResource(0);
            imageview.setBackgroundResource(R.drawable.ic_fullscreen_black_36dp);
            recycler_view_side_offers.setVisibility(View.VISIBLE);
            llMenuBottom.setVisibility(View.VISIBLE);
            llMenuUp.setVisibility(View.VISIBLE);
            rlviewpagerMain.setVisibility(View.VISIBLE);
            llsideOffers.setVisibility(View.VISIBLE);
            fullscreen = true;
        }
    }

    private void getFetchWeather() {


        String DateTime = getdateTime();
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setUsername("nips_inventory");
        authenticateRequest.setPassword("nips@2016");
        authenticateRequest.setSecureHash(Common.getencryptedsecureHash(DateTime, "B15m1L2ah"));
        authenticateRequest.setTimestamp(DateTime);

        gson = new GsonBuilder().create();
        final JSONObject req = new JSONObject();
        try {
            req.put("username", authenticateRequest.getUsername());
            req.put("password", authenticateRequest.getPassword());
            req.put("timestamp", authenticateRequest.getTimestamp());
            req.put("secureHash", authenticateRequest.getSecureHash());

        } catch (JSONException e) {

        }

        object = new JSONObject();
        try {
            object.put("request", req);

        } catch (JSONException e) {

        }

        handler.post(runnable);
    }


    private final Runnable runnable = () -> {

        getFetchWeatherResponse(object.toString(), getContext(), new FetchWeatherObjectCallback() {
            @Override
            public void successful(FetchCurrentWeatherResponse fetchCurrentWeatherResponse) {

                Constant.weatherDetailsListviewAList = fetchCurrentWeatherResponse.getFetchCurrentWeatherInfrormationResult().getResponse();
                Weather = fetchCurrentWeatherResponse.getFetchCurrentWeatherInfrormationResult().getResponse().get(0).getTemperature();

                try {
                    getActivity().runOnUiThread(() -> {

                        tv_degree.setText(String.valueOf(Math.round(Weather) + " \u2103"));

                        Calendar cal = Calendar.getInstance();
                        int hour = cal.get(Calendar.HOUR_OF_DAY);

                        Boolean isNight = hour < 6 || hour > 18;

                        try {
                            hmweatherimageDay = new HashMap<>();
                            if (isNight == false) {
                                hmweatherimageDay.put("clear sky", R.drawable.sunny);
                                hmweatherimageDay.put("few clouds", R.drawable.sunny);
                                weatherimg.setImageResource(R.drawable.sunny);
                            } else {
                                hmweatherimageDay.put("clear sky", R.drawable.wnightclear);
                                hmweatherimageDay.put("few clouds", R.drawable.wnightclear);
                                weatherimg.setImageResource(R.drawable.wnightclear);
                            }

                        } catch (Exception e) {

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }



                handler.postDelayed(runnable, WeatherTime);

            }

            @Override
            public void failure(String errorMessage) {
                Log.d(TAG, "failure: " + errorMessage);
            }
        });
    };


    private void grantPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Location Permissions not granted yet", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getContext(), "Please Grant these permissions", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        } else {
//            Toast.makeText(this, "You have all the permissions you need to get Locations", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public String onResponseSuccess(String data) {
        try {
            Log.e(ContentValues.TAG, "onResponseFailure: "+data );
//            dialog.dismiss();
            JSONObject obj = new JSONObject(data);
            updateRTAConfigs(obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private void updateRTAConfigs(JSONObject object){
        ConfigurationClass.DEVICE_SERIAL_NUMBER = object.optString("DeviceSerialNumber");
        ConfigurationClass.MERCHANT_ID = object.optString("MerchantId");
        ConfigurationClass.SECRET_KEY = object.optString("SecretKey");
        ConfigurationClass.PAYMENT_SECRET_KEY = object.optString("PaymentSecretKey");
        ConfigurationClass.TERMINAL_ID = object.optString("TerminalId");
        ConfigurationClass.BANK_ID = object.optString("BankID");
        ConfigurationClass.LOGIN_ID = object.optString("LoginId");
        ConfigurationClass.PASSWORD = object.optString("Password");
        ConfigurationClass.PAYMENT_USER_ID = object.optString("PaymentUserId");
        ConfigurationClass.PAYMENT_PASSWORD = object.optString("PaymentPassword");
        ConfigurationClass.SOURCE_APPLICATION = object.optString("SourceApplication");
//        ConfigurationClass.SOURCE_APPLICATION = ConfigurationClass.TERMINAL_ID;
        ConfigurationClass.PAYMENT_ACTION = object.optString("PaymentAction");
        ConfigurationClass.PAYMENT_CALL_BACK_URL = object.optString("CallBackURL");
        ConfigurationClass.PAYMENT_DEVICE_FINGER_PRINT = object.optString("DeviceFingerPrint");
        ConfigurationClass.PAYMENT_CURRENCY = object.optString("Currency");
        ConfigurationClass.PAYMENT_CHANNEL = object.optString("Channel");
        ConfigurationClass.PAYMENT_LANGUAGE = object.optString("Language_Merchant");
        ConfigurationClass.PAYMENT_REQUEST_CATEGORY = object.optString("RequestCategory");
        ConfigurationClass.REQUEST_TYPE_PAYMENT = object.optString("RequestTypePayment");
        ConfigurationClass.IsThreeDSecure = object.optString("Is3DSecure");
        ConfigurationClass.SilentAPIURL = object.optString("SilentOrderAPIURL");
        ConfigurationClass.plainText = object.optString("plaintext");
    }

    @Override
    public String onResponseFailure(String t)
    {
        return null;
    }

}