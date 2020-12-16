package com.mlt.dtc.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.mlt.dtc.R;
import com.mlt.dtc.common.SystemUIService;
import com.mlt.dtc.fragment.MainBannerVideoFragment;
import com.mlt.dtc.utility.Constant;

import static com.mlt.dtc.activity.MainFragment.keyboard;
import static com.mlt.dtc.common.Common.ScreenBrightness;
import static com.mlt.dtc.common.Common.checkPermissionREAD_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mainayout);

        checkPermissionREAD_EXTERNAL_STORAGE(this);


        mFragment = MainFragment.newInstance();
        addFragment();



        HideNavigationBar();
    }

    public void addFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainViewLinear, mFragment)
                .addToBackStack(null)
                .commit();
    }

    public void HideNavigationBar() {
        SystemUIService.setNaviButtonVisibility(this.getApplicationContext(), SystemUIService.NAVIBUTTON_NAVIBAR, View.GONE);
        SystemUIService.setStatusBarVisibility(this.getApplicationContext(), View.GONE);
    }


    @Override
    public void onRestart() {
        super.onRestart();
        mFragment = MainBannerVideoFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();

        try {
            //Set the screen brightness what is currently set
            ScreenBrightness(getApplicationContext(), MainActivity.this);

        } catch (Exception ex) {
            ex.getMessage();
        }

        HideNavigationBar();
    }


    public static void reloadPage(Context context) {
        if (Constant.count == 0) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Constant.count++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HideNavigationBar();
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