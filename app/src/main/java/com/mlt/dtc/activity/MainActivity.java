package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.infinitebanner.InfiniteBannerView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mlt.dtc.R;
import com.mlt.dtc.adapter.OffersRecyclerViewAdapter;
import com.mlt.dtc.adapter.RecyclerviewBottomAdapter;
import com.mlt.dtc.fragment.DriverFragment;
import com.mlt.dtc.fragment.TopBannerDialogFragment;
import com.mlt.dtc.adapter.BannerAdapter;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.fragment.OffersDialogFragment;
import com.mlt.dtc.fragment.TripEndFragment;
import com.mlt.dtc.fragment.TripStartFragment;
import com.mlt.dtc.interfaces.FareDialogListener;
import com.mlt.dtc.interfaces.OverwriteTripFragmentListener;
import com.mlt.dtc.interfaces.TaskListener;
import com.mlt.dtc.model.SideBannerObject;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.utility.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static com.mlt.dtc.common.Common.WriteTextInTextFile;
import static com.mlt.dtc.common.Common.getDateHome;
import static com.mlt.dtc.common.Common.getFilePath;
import static com.mlt.dtc.common.Common.getdateTime;
import static com.mlt.dtc.common.Common.getlistofclickLog;
import static com.mlt.dtc.common.Common.prepareMenuData;
import static com.mlt.dtc.common.Common.topBannerList;
import static com.mlt.dtc.utility.Constant.multimediaPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TaskListener,RecyclerviewBottomAdapter.ClickListener, OffersRecyclerViewAdapter.RecyclerViewClickListener, FareDialogListener {
    public InfiniteBannerView infiniteBannerView;
    public Boolean isSelected=false;
    private RecyclerView rvBottomMenu, recycler_view_side_offers;
    public ArrayList<SideBannerObject> fileList;
    int topBannerCount,positionTopBanner,multipletripviewClick;
    public OffersRecyclerViewAdapter adapter_menus = null;
    public static MainActivity mainActivity;
    private GoogleApiClient googleApiClient;
    public static int restrict_double_click = 0, count = 0;
    private int Position, sideoffersCount,driverCount,fareCount;
    private TextView tv_timemainbox,tv_datemainbox;
    private LinearLayout ll_driverinfo;
    private FrameLayout iv_weatherimage,tripdetail;
    private DialogFragment dialogFragment;
    private static OverwriteTripFragmentListener overwriteTripFragmentListener;
    TripStartFragment tripStartFragment = new TripStartFragment();
    TripEndFragment tripEndFragment = new TripEndFragment();
    public static MainActivity getInstance(){

        return mainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findviewbyid();

        HideNavigationBar();

        processOffers();

        topBannerViews(topBannerList());


        setRecyclerViewBottomAdapter();

    }

    private void findviewbyid() {
        mainActivity = this;
        infiniteBannerView = findViewById(R.id.pager);
        rvBottomMenu=findViewById(R.id.recycler_bottom_menu);
        tv_timemainbox = findViewById(R.id.tv_timemainbox);
        ll_driverinfo = findViewById(R.id.ll_driverinfo);
        tv_datemainbox  = findViewById(R.id.tv_datemainbox);
        tripdetail=findViewById(R.id.tripdetail);
        recycler_view_side_offers = findViewById(R.id.recycler_view_side_offers);
        findViewById(R.id.relative_left_arrow).setOnClickListener(this);
        findViewById(R.id.relative_right_arrow).setOnClickListener(this);
        findViewById(R.id.layout_uparrow).setOnClickListener(this);
        findViewById(R.id.down_arrow).setOnClickListener(this);
        findViewById(R.id.pager).setOnClickListener(this);
        ll_driverinfo.setOnClickListener(this);
        tripdetail.setOnClickListener(this);
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

        tv_datemainbox.setText(getDateHome());
        Common.DateTimeRunning(tv_timemainbox);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        restrict_double_click = restrict_double_click + 1;
        if (restrict_double_click == 1) {
            Position = position;
            sideoffersCount++;
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.SideBannerCount, sideoffersCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameSideAds);

            Constant.ButtonClicked = Constant.nameSideAds;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);

            try {
                OffersDialogFragment offersDialogFragment = new OffersDialogFragment();
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

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvBottomMenu.setLayoutManager(horizontalLayoutManagaer);
        rvBottomMenu.setAdapter(bottomAdapter);


    }

    private void topBannerViews(ArrayList<TopBannerObject> topBannerList) {
        BannerAdapter bannerAdapter= new BannerAdapter(topBannerList,getApplicationContext(),true );
        infiniteBannerView.setAdapter( bannerAdapter );
    }

    public static void reloadPage(Context context){
        if(count==0){
            Intent intent = new Intent(context,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            count++;
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
//            mltbuttonCount++;
//
//            PreferenceConnector.writeInteger(getApplicationContext(), Constant.MLTButtonCount, mltbuttonCount);
//            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameMLTButton);
//            Constant.ButtonClicked = Constant.nameMLTButton;
//            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
//            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
//            mFragment = new ContactUsFragment();
//            addFragment();
        }else if (position==1){
//            rtaservicesCount++;
//            PreferenceConnector.writeInteger(getApplicationContext(), Constant.RTAServicesCount, rtaservicesCount);
//            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameRTAServices);
//
//            Constant.ButtonClicked = Constant.nameRTAServices;
//            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
//            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
//
//            i = new Intent(Intent.ACTION_MAIN);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.putExtra("isFromDTC",true);
//            i.setComponent(new ComponentName("example.rta","rtaservices.RTAMainActivity"));
//            startActivity(i);

        }else if (position==2){
//            DTCservicesCount++;
//            PreferenceConnector.writeInteger(getApplicationContext(), Constant.RTAServicesCount, DTCservicesCount);
//            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameDTCServices);
//
//            Constant.ButtonClicked = Constant.nameDTCServices;
//            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
//            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());

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
}