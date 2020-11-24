package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.infinitebanner.InfiniteBannerView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mlt.dtc.R;
import com.mlt.dtc.adapter.OffersRecyclerViewAdapter;
import com.mlt.dtc.adapter.RecyclerviewBottomAdapter;
import com.mlt.dtc.fragment.AdminFragment;
import com.mlt.dtc.fragment.ContactUsFragment;
import com.mlt.dtc.fragment.DriverFragment;
import com.mlt.dtc.fragment.MainBannerVideoFragment;
import com.mlt.dtc.fragment.TimeFragment;
import com.mlt.dtc.fragment.TopBannerDialogFragment;
import com.mlt.dtc.adapter.BannerAdapter;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.fragment.TripEndFragment;
import com.mlt.dtc.fragment.TripStartFragment;
import com.mlt.dtc.interfaces.DriverImageListener;
import com.mlt.dtc.interfaces.FareDialogListener;
import com.mlt.dtc.interfaces.FetchWeatherObjectCallback;
import com.mlt.dtc.interfaces.MainVideoBannerListener;
import com.mlt.dtc.interfaces.OverwriteTripFragmentListener;
import com.mlt.dtc.interfaces.TaskListener;
import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;
import com.mlt.dtc.model.SideBannerObject;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.model.request.AuthenticateRequest;
import com.mlt.dtc.networking.NetWorkRequest;
import com.mlt.dtc.pushnotification.MyFirebaseMessagingService;
import com.mlt.dtc.utility.Constant;
import com.mlt.dtc.utility.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import example.CustomKeyboard.Components.CustomKeyboardView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.Date;
import static com.mlt.dtc.common.Common.WriteTextInTextFile;
import static com.mlt.dtc.common.Common.checkPermissionREAD_EXTERNAL_STORAGE;
import static com.mlt.dtc.common.Common.getDateHome;
import static com.mlt.dtc.common.Common.getFilePath;
import static com.mlt.dtc.common.Common.getdateTime;
import static com.mlt.dtc.common.Common.getlistofclickLog;
import static com.mlt.dtc.common.Common.prepareMenuData;
import static com.mlt.dtc.common.Common.topBannerList;
import static com.mlt.dtc.utility.Constant.TAG;
import static com.mlt.dtc.utility.Constant.multimediaPath;



public class MainActivity extends AppCompatActivity implements View.OnClickListener, TaskListener,RecyclerviewBottomAdapter.ClickListener, OffersRecyclerViewAdapter.RecyclerViewClickListener, FareDialogListener, DriverImageListener,MainVideoBannerListener {
    public InfiniteBannerView infiniteBannerView;
    public Boolean isSelected=false;
    private RecyclerView rvBottomMenu, recycler_view_side_offers;
    public ArrayList<SideBannerObject> fileList;
    int topBannerCount,positionTopBanner,multipletripviewClick;
    public OffersRecyclerViewAdapter adapter_menus = null;
    public static MainActivity mainActivity;
    private GoogleApiClient googleApiClient;
    private int Position, sideOffersCount,DTCServicesCount,mltButtonCount,rtaServicesCount,rlvideoMainBanner,timeCount;
    Fragment mFragment;
    public static CustomKeyboardView keyboard;

    public static int restrict_double_click = 0, count = 0;
    private int  sideoffersCount,driverCount,fareCount;
    private TextView tv_timemainbox,tv_datemainbox,tv_VideoBr,tv_degree;
    private LinearLayout ll_driverinfo,llMenuBottom,llMenuUp,llsideOffers;
    private RelativeLayout relativeLayoutfragment,rlviewpagerMain;
    private FrameLayout iv_weatherimage,tripdetail;
    private DialogFragment dialogFragment;
    private ImageView iv_Driver_Image;
    private static OverwriteTripFragmentListener overwriteTripFragmentListener;
    TripStartFragment tripStartFragment = new TripStartFragment();
    TripEndFragment tripEndFragment = new TripEndFragment();
    MainBannerVideoFragment mainBannerVideoFragment;
    RelativeLayout rlvideobrbox;
    VideoView videoviewMainBanner;
    boolean fullscreen = true;
    WindowManager mWindowManager;
    private Double Weather;
    HandlerThread handlerThread;
    private JSONObject object;
    private Handler handler;
    Gson gson = new Gson();




    public static MainActivity getInstance(){

        return mainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        //Keep the screen on
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        handlerThread = new HandlerThread("weather");
        handlerThread.start();

        handler =new Handler(handlerThread.getLooper());


        mainBannerVideoFragment = new MainBannerVideoFragment();

        //Setting call back method
        mainBannerVideoFragment.setMethodCallBack(this);


        mainActivity = this;

        checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this);

        findViewById();

        HideNavigationBar();

        processOffers();

        topBannerViews(topBannerList());


        setRecyclerViewBottomAdapter();

        processOffers();


        getFetchWeather();

    }

    private void findViewById() {

        //this text is moving
        tv_degree=findViewById(R.id.tv_Degree);
        findViewById(R.id.tv_services).setSelected(true);
        llsideOffers = findViewById(R.id.llsideoffers);
        rlviewpagerMain = findViewById(R.id.viewpagermain);
        llMenuUp = findViewById(R.id.llmenuup);
        llMenuBottom = findViewById(R.id.llmenubottom);
        relativeLayoutfragment = findViewById(R.id.rlcontent);
        keyboard = findViewById(R.id.customKeyboardView);
        rvBottomMenu=findViewById(R.id.recycler_bottom_menu);
        infiniteBannerView = findViewById(R.id.pager);
        rvBottomMenu=findViewById(R.id.recycler_bottom_menu);
        tv_timemainbox = findViewById(R.id.tv_timemainbox);
        ll_driverinfo = findViewById(R.id.ll_driverinfo);
        tv_datemainbox  = findViewById(R.id.tv_datemainbox);
        tripdetail=findViewById(R.id.tripdetail);
        recycler_view_side_offers = findViewById(R.id.recycler_view_side_offers);
        findViewById(R.id.relative_left_arrow).setOnClickListener(this);
        findViewById(R.id.relative_right_arrow).setOnClickListener(this);
        findViewById(R.id.img_choose_service).setOnClickListener(this);

        findViewById(R.id.layout_uparrow).setOnClickListener(this);
        findViewById(R.id.down_arrow).setOnClickListener(this);
        findViewById(R.id.pager).setOnClickListener(this);
        ll_driverinfo.setOnClickListener(this);
        tripdetail.setOnClickListener(this);
        findViewById(R.id.ll_time).setOnClickListener(this);
    }


    public void HideNavigationBar() {
        SystemUIService.setNaviButtonVisibility(this.getApplicationContext(), SystemUIService.NAVIBUTTON_NAVIBAR, View.GONE);
        SystemUIService.setStatusBarVisibility(this.getApplicationContext(), View.GONE);
    }

    private void processOffers(){
        try {
            fileList = new ArrayList<>();
            adapter_menus = new OffersRecyclerViewAdapter(fileList, getApplicationContext(),this);
            recycler_view_side_offers.setAdapter(adapter_menus);

            File dir = new File(multimediaPath+"/adv/");
            File dirMI = new File(multimediaPath+"/SBMainImage/");
            File[] files = dir.listFiles();
            File[] MIFiles = dirMI.listFiles();
            //fileList = new ArrayList<File>();
            fileList.clear();
            for (File file : files) {
                if(file.getName().toLowerCase().startsWith("baner") || file.getName().toUpperCase().startsWith("BANER")){
                    // it's a match, call your function
                    for(File miFile:MIFiles){
                        if(miFile.getName().equals(file.getName())){
                            fileList.add(new SideBannerObject(file,miFile));
                        }
                    }
                }
            }

            recycler_view_side_offers.post(new Runnable() {
                @Override
                public void run() {
                    adapter_menus.notifyDataSetChanged();
                }
            });
        }catch (Exception e){

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient = getAPIClientInstance();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        //To prevent Network on main thread exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //        fareFragment.setMethodcallbackmultipleViewClick(this);
        MyFirebaseMessagingService.setcardValuesCallBackMethod(this);
        MyFirebaseMessagingService.setDriverImageCallBackMethod(this);

        tv_datemainbox.setText(getDateHome());
        Common.DateTimeRunning(tv_timemainbox);

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        restrict_double_click = restrict_double_click + 1;
        if (restrict_double_click == 1) {
            Position = position;
            sideOffersCount++;
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.SideBannerCount, sideOffersCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameSideAds);

            Constant.ButtonClicked = Constant.nameSideAds;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);

            try {
                com.mlt.dtc.fragments.OffersDialogFragment offersDialogFragment = new com.mlt.dtc.fragments.OffersDialogFragment();
                Bundle bundlepos = new Bundle();
                bundlepos.putInt(Constant.PositionSelectedOffers, position);
                bundlepos.putSerializable(Constant.ArrayImagesSelectedOffers, fileList);
                offersDialogFragment.setArguments(bundlepos);
                offersDialogFragment.show(getSupportFragmentManager(), "");

            } catch (Exception e) {

            }
        }
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    private void setRecyclerViewBottomAdapter() {
        RecyclerviewBottomAdapter bottomAdapter = new RecyclerviewBottomAdapter(getApplicationContext(),prepareMenuData());
        bottomAdapter.setOnItemClickListener(this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvBottomMenu.setLayoutManager(horizontalLayoutManager);
        rvBottomMenu.setAdapter(bottomAdapter);


    }

    private void topBannerViews(ArrayList<TopBannerObject> topBannerList) {
        BannerAdapter bannerAdapter= new BannerAdapter(topBannerList,getApplicationContext(),true );
        infiniteBannerView.setAdapter( bannerAdapter );
    }

    public static void reloadPage(Context context){
        if(Constant.count==0){
            Intent intent = new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Constant.count++;
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.relative_left_arrow:
                Common.leftArrow(infiniteBannerView);
                break;
            case R.id.relative_right_arrow:
                Common.rightArrow(infiniteBannerView);
                break;
            case R.id.pager:
                if (!isSelected){
                    isSelected =true;
                    topBannerCount++;

                    PreferenceConnector.writeInteger(getApplicationContext(), Constant.TopBannerCount, topBannerCount);
                    PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameTopBanner);

                    Constant.ButtonClicked = Constant.nameTopBanner;
                    WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);

                    try {
                        TopBannerDialogFragment topBannerDialogFragment = new TopBannerDialogFragment();
                        Bundle bundlepos = new Bundle();
                        bundlepos.putInt(Constant.PositionTopBanner, positionTopBanner);
                        bundlepos.putSerializable(Constant.TopImagesSelectedOffers, topBannerList());
                        topBannerDialogFragment.setArguments(bundlepos);
                        topBannerDialogFragment.show(getSupportFragmentManager(), "");

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
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                PreferenceConnector.writeInteger(getApplicationContext(), Constant.DriverCount, driverCount);
                PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameDriver);
                Constant.ButtonClicked = Constant.nameDriver;
                WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
                DriverFragment driverFragment = new DriverFragment();
                driverFragment.show(getSupportFragmentManager(), "");
                break;
            case R.id.tripdetail:
                fareCount++;
                tripdetail.setEnabled(false);

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                PreferenceConnector.writeInteger(getApplicationContext(), Constant.FareCount, fareCount);
                PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameFare);
                Constant.ButtonClicked = Constant.nameFare;
                WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
                getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
                dialogFragment = new TripEndFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.TripEndServiceCall, true);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getSupportFragmentManager(), "");
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
            findViewById(R.id.ll_time).setEnabled(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.TimeCount, timeCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameTime);
            Constant.ButtonClicked = Constant.nameTime;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
            dialogFragment = new TimeFragment();
            dialogFragment.show(getSupportFragmentManager(), "");
            break;

        }
    }

    @Override
    public void onFinished(String result) {
        ll_driverinfo.setEnabled(true);
        findViewById(R.id.ll_time).setEnabled(true);
        findViewById(R.id.weatherimage).setEnabled(true);
        tripdetail.setEnabled(true);
    }

    @Override
    public void onItemClick(int position, View v) {
        Intent i;
        if (position==0){
            mltButtonCount++;

            PreferenceConnector.writeInteger(getApplicationContext(), Constant.MLTButtonCount, mltButtonCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameMLTButton);
            Constant.ButtonClicked = Constant.nameMLTButton;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
            mFragment = new ContactUsFragment();
            addFragment();
        }else if (position==1){
            rtaServicesCount++;
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.RTAServicesCount, rtaServicesCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameRTAServices);

            Constant.ButtonClicked = Constant.nameRTAServices;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());

            i = new Intent(Intent.ACTION_MAIN);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("isFromDTC",true);
            i.setComponent(new ComponentName("example.rta","rtaservices.RTAMainActivity"));
            startActivity(i);

        }else if (position==2){
            DTCServicesCount++;
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.RTAServicesCount, DTCServicesCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameDTCServices);

            Constant.ButtonClicked = Constant.nameDTCServices;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());

        }
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

                Fragment fragmentA = getSupportFragmentManager().findFragmentByTag("tripstartfragment");
                if (fragmentA == null) {
                    //tripStartTime = new Date();
                    //Close the dialog
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                    }
                    // DialogFragment.show() will take care of adding the fragment
                    // in a transaction.  We also want to remove any currently showing
                    // dialog, so make our own transaction and take care of that here.
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                    tripStartFragment.setArguments(bundle);
                    tripStartFragment.show(getSupportFragmentManager(), "tripstartfragment");
                } else {
                    Date tripStartTime = new Date();
                    //Close the dialog
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                        Thread.sleep(1000);
                        bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                        tripStartFragment.setArguments(bundle);
                        tripStartFragment.show(getSupportFragmentManager(), "tripstartfragment");
                        Toast.makeText(getApplicationContext(), "Trip Start Fragment already exists", Toast.LENGTH_SHORT).show();
                    }

                }

            } else if
            (Constant.TripEndEventCode.equalsIgnoreCase(TripCode)) {

                Fragment fragmentB = getSupportFragmentManager().findFragmentByTag("tripendfragment");
                if (fragmentB == null) {
                    //Close the dialog
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                    }
                    bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                    tripEndFragment.setArguments(bundle);
                    tripEndFragment.show(getSupportFragmentManager(), "tripendfragment");

                } else {
                    if (overwriteTripFragmentListener != null) {
                        overwriteTripFragmentListener.OverwriteTripFragmentListenerBackMethod();
                        Thread.sleep(1000);
                        bundle.putBoolean(Constant.TripEndServiceCall, TripEndCallService);
                        tripEndFragment.setArguments(bundle);
                        tripEndFragment.show(getSupportFragmentManager(), "tripendfragment");
                        Toast.makeText(getApplicationContext(), "Trip End Fragment already exists", Toast.LENGTH_SHORT).show();

                    }
                }
                //}
            }
        } catch (Exception ex) {
            Log.e("TTTT", ex.getLocalizedMessage() + "");
        }
    }



    public void addFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (keyboard.isExpanded()) {
            keyboard.translateLayout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void DriverImageCallBackMethod(String driverImage, Context context) {
        String DriverImage = driverImage;
        //Setting the image of the driver
        iv_Driver_Image = findViewById(R.id.iv_driver_image);
        runOnUiThread(() -> {
            // Can call RequestCreator.into here
            //Picasso.with(Context).load(DriverImage).placeholder(R.drawable.dtcdriverphoto).into(iv_Driver_Image);
            Glide.with(context).asBitmap().load(DriverImage).apply(new RequestOptions().placeholder(R.drawable.dtcdriverphoto)).dontAnimate().into(iv_Driver_Image);
        });
        PreferenceConnector.writeString(getApplicationContext(), Constant.DriverImage, driverImage);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        mFragment = MainBannerVideoFragment.newInstance();
        addFragment();

        HideNavigationBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFragment = MainBannerVideoFragment.newInstance();
        addFragment();

        HideNavigationBar();
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
        try{
            finish();
            rlvideobrbox = null;
            mFragment = null;
        }catch (Exception ex){
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

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        object  = new JSONObject();
        try {
            object.put("request", req);

        } catch (JSONException e) {

        }

        handler.post(runnable);
    }



    private final Runnable runnable = ()->{

        getFetchWeatherResponse(object.toString(), getApplicationContext(), new FetchWeatherObjectCallback() {
            @Override
            public void successful(FetchCurrentWeatherResponse fetchCurrentWeatherResponse) {
                Weather =fetchCurrentWeatherResponse.getFetchCurrentWeatherInfrormationResult().getResponse().get(0).getTemperature();
                runOnUiThread(()->{
                    tv_degree.setText(String.valueOf(Math.round(Weather)+ " \u2103"));
                });
                handler.postDelayed(runnable,10000);
            }

            @Override
            public void failure(String errorMessage) {
                Log.d(TAG, "failure: "+errorMessage);
            }
        });
    };

    private void getFetchWeatherResponse( String body, Context context, FetchWeatherObjectCallback fetchWeatherObjectCallback ){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://dtcwbsvc.networkips.com/ServiceModule/DTCService.svc/")
                .client(new OkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetWorkRequest netWorkRequest =     retrofit.create(NetWorkRequest .class);

        Call<FetchCurrentWeatherResponse> categoryProductCall = netWorkRequest.GetFetchWeatherResponse( body );
        categoryProductCall.enqueue(new Callback<FetchCurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<FetchCurrentWeatherResponse> call, Response<FetchCurrentWeatherResponse> response) {
                if( response.isSuccessful() ){
                    fetchWeatherObjectCallback.successful( response.body() );
                }else{
                    fetchWeatherObjectCallback.successful( response.body() );
                }
            }


            @Override
            public void onFailure(Call<FetchCurrentWeatherResponse> call, Throwable t) {
                String errorMessage = t.getLocalizedMessage();
                if( errorMessage == null || errorMessage.equals("timeout") ) {

                }else if( errorMessage.contains("Unable to resolve host") ) {

                }else{

                }
            }
        });
    }


}