package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;


import com.github.infinitebanner.InfiniteBannerView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mlt.dtc.R;
import com.mlt.dtc.adapter.OffersRecyclerViewAdapter;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.fragments.OffersDialogFragment;
import com.mlt.dtc.modal.SideBannerObject;
import com.mlt.dtc.utility.Constant;

import java.io.File;
import java.util.ArrayList;

import static com.mlt.dtc.common.Common.WriteTextInTextFile;
import static com.mlt.dtc.common.Common.getFilePath;
import static com.mlt.dtc.common.Common.multimediaPath;

public class MainActivity extends AppCompatActivity implements OffersRecyclerViewAdapter.RecyclerViewClickListener {
    public InfiniteBannerView infiniteBannerView;
    private RecyclerView recycler_view_side_offers;
    private RelativeLayout layout_uparrow,down_arrow;
    private OffersRecyclerViewAdapter adapter_menus;
    public ArrayList<SideBannerObject> fileList;
    public static int restrict_double_click = 0;
    private int Position,sideoffersCount;
    public static MainActivity mainActivity;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findviewbyid();

        HideNavigationBar();

        processOffers();
    }

    private void findviewbyid() {
        mainActivity = this;
        infiniteBannerView = findViewById(R.id.pager);
        recycler_view_side_offers = findViewById(R.id.recycler_view_side_offers);
        layout_uparrow = findViewById(R.id.layout_uparrow);
        down_arrow = findViewById(R.id.down_arrow);
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

    @Override
    public void onFinished(String result) {
//        iv_happyscreen.setEnabled(true);
//        ll_Driverinfo.setEnabled(true);
//        ll_Time.setEnabled(true);
//        iv_weatherimage.setEnabled(true);
//        tripdetail.setEnabled(true);
//        ivMenu.setEnabled(true);
//        view.setEnabled(true);
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }
}