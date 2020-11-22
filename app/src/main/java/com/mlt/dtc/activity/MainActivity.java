package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import com.github.infinitebanner.InfiniteBannerView;
import com.mlt.dtc.R;
import com.mlt.dtc.adapter.RecyclerviewBottomAdapter;
import com.mlt.dtc.fragment.TopBannerDialogFragment;
import com.mlt.dtc.adapter.BannerAdapter;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.Constant;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.interfaces.TaskListener;
import com.mlt.dtc.model.TopBannerObject;
import java.util.ArrayList;
import static com.mlt.dtc.common.Common.WriteTextInTextFile;
import static com.mlt.dtc.common.Common.checkPermissionREAD_EXTERNAL_STORAGE;
import static com.mlt.dtc.common.Common.getFilePath;
import static com.mlt.dtc.common.Common.getdateTime;
import static com.mlt.dtc.common.Common.prepareMenuData;
import static com.mlt.dtc.common.Common.topBannerList;
import static com.mlt.dtc.common.Constant.count;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TaskListener,RecyclerviewBottomAdapter.ClickListener {
    public InfiniteBannerView infiniteBannerView;
    public Boolean isSelected=false;
    private RecyclerView rvBottomMenu;

    int topBannerCount,positionTopBanner,DTCservicesCount,rtaservicesCount,mltbuttonCount;

    public static MainActivity mainActivity;


    public static MainActivity getInstance(){

        return mainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        mainActivity = this;

        checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this);

        findViewById();

        HideNavigationBar();

        topBannerViews(topBannerList());


        setRecyclerViewBottomAdapter();

    }

    private void findViewById() {
        rvBottomMenu=findViewById(R.id.recycler_bottom_menu);
        infiniteBannerView = findViewById(R.id.pager);

        findViewById(R.id.relative_left_arrow).setOnClickListener(this);
        findViewById(R.id.relative_right_arrow).setOnClickListener(this);

        findViewById(R.id.pager).setOnClickListener(this);
    }


    public void HideNavigationBar() {
        SystemUIService.setNaviButtonVisibility(this.getApplicationContext(), SystemUIService.NAVIBUTTON_NAVIBAR, View.GONE);
        SystemUIService.setStatusBarVisibility(this.getApplicationContext(), View.GONE);
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
            mltbuttonCount++;

            PreferenceConnector.writeInteger(getApplicationContext(), Constant.MLTButtonCount, mltbuttonCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameMLTButton);
            Constant.ButtonClicked = Constant.nameMLTButton;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());
//            mFragment = new ContactUsFragment();
//            addFragment();
        }else if (position==1){
            rtaservicesCount++;
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.RTAServicesCount, rtaservicesCount);
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
            DTCservicesCount++;
            PreferenceConnector.writeInteger(getApplicationContext(), Constant.RTAServicesCount, DTCservicesCount);
            PreferenceConnector.writeString(getApplicationContext(), Constant.ButtonClicked, Constant.nameDTCServices);

            Constant.ButtonClicked = Constant.nameDTCServices;
            WriteTextInTextFile(getFilePath(), Constant.ButtonClicked);
            Common.getlistofclickLog(getApplicationContext(), Constant.ButtonClicked, getdateTime());

        }
    }
}