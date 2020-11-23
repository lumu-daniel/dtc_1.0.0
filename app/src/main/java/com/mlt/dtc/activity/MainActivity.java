package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.github.infinitebanner.InfiniteBannerView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mlt.dtc.R;
import com.mlt.dtc.adapter.OffersRecyclerViewAdapter;
import com.mlt.dtc.adapter.RecyclerviewBottomAdapter;
import com.mlt.dtc.fragment.AdminFragment;
import com.mlt.dtc.fragment.ContactUsFragment;
import com.mlt.dtc.fragment.TopBannerDialogFragment;
import com.mlt.dtc.adapter.BannerAdapter;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.fragment.OffersDialogFragment;
import com.mlt.dtc.interfaces.TaskListener;
import com.mlt.dtc.model.SideBannerObject;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.utility.Constant;
import java.io.File;
import java.util.ArrayList;
import example.CustomKeyboard.Components.CustomKeyboardView;
import static com.mlt.dtc.common.Common.WriteTextInTextFile;
import static com.mlt.dtc.common.Common.checkPermissionREAD_EXTERNAL_STORAGE;
import static com.mlt.dtc.common.Common.getFilePath;
import static com.mlt.dtc.common.Common.getdateTime;
import static com.mlt.dtc.common.Common.prepareMenuData;
import static com.mlt.dtc.common.Common.topBannerList;
import static com.mlt.dtc.utility.Constant.count;
import static com.mlt.dtc.utility.Constant.multimediaPath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TaskListener,RecyclerviewBottomAdapter.ClickListener,
        OffersRecyclerViewAdapter.RecyclerViewClickListener{
    public InfiniteBannerView infiniteBannerView;
    public Boolean isSelected=false;
    private RecyclerView rvBottomMenu, recycler_view_side_offers;
    private RelativeLayout layout_uparrow,down_arrow;
    public ArrayList<SideBannerObject> fileList;
    int topBannerCount,positionTopBanner;
    public OffersRecyclerViewAdapter adapter_menus = null;
    public static MainActivity mainActivity;
    private GoogleApiClient googleApiClient;
    public static int restrict_double_click = 0;
    private int Position, sideOffersCount,DTCServicesCount,mltButtonCount,rtaServicesCount;
    Fragment mFragment;
    public static CustomKeyboardView keyboard;

    public static MainActivity getInstance(){

        return mainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //Keep the screen on
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mainActivity = this;

        checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this);

        findViewById();

        HideNavigationBar();

        topBannerViews(topBannerList());


        setRecyclerViewBottomAdapter();

        processOffers();

    }

    private void findViewById() {
        keyboard = findViewById(R.id.customKeyboardView);
        rvBottomMenu=findViewById(R.id.recycler_bottom_menu);
        infiniteBannerView = findViewById(R.id.pager);
        rvBottomMenu=findViewById(R.id.recycler_bottom_menu);
        recycler_view_side_offers = findViewById(R.id.recycler_view_side_offers);
        layout_uparrow = findViewById(R.id.layout_uparrow);
        down_arrow = findViewById(R.id.down_arrow);

        findViewById(R.id.relative_left_arrow).setOnClickListener(this);
        findViewById(R.id.relative_right_arrow).setOnClickListener(this);
        findViewById(R.id.img_choose_service).setOnClickListener(this);

        findViewById(R.id.pager).setOnClickListener(this);
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

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvBottomMenu.setLayoutManager(horizontalLayoutManager);
        rvBottomMenu.setAdapter(bottomAdapter);

        bottomAdapter.setOnItemClickListener(this);

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
            case R.id.img_choose_service:
                count++;
                if (count == 10) {
                    mFragment = AdminFragment.newInstance();
                    addFragment();
                    break;
                }
        }
    }

    @Override
    public void onFinished(String result) {
        findViewById(R.id.ll_driverinfo).setEnabled(true);
        findViewById(R.id.ll_time).setEnabled(true);
        findViewById(R.id.weatherimage).setEnabled(true);
        findViewById(R.id.tripdetail).setEnabled(true);
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

}