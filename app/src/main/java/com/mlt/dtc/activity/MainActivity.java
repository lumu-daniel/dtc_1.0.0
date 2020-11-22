package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.github.infinitebanner.InfiniteBannerView;
import com.mlt.dtc.R;
import com.mlt.dtc.common.SystemUIService;

public class MainActivity extends AppCompatActivity {
    public InfiniteBannerView infiniteBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findviewbyid();

        HideNavigationBar();
    }

    private void findviewbyid() {
        infiniteBannerView = findViewById(R.id.pager);
    }


    public void HideNavigationBar() {
        SystemUIService.setNaviButtonVisibility(this.getApplicationContext(), SystemUIService.NAVIBUTTON_NAVIBAR, View.GONE);
        SystemUIService.setStatusBarVisibility(this.getApplicationContext(), View.GONE);
    }
}