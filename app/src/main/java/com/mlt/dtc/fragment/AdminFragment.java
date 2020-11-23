package com.mlt.dtc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mlt.dtc.R;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.utility.Constant;

import java.util.Objects;

public
class AdminFragment extends Fragment {
    ImageView btn_wifi,btn_adminlogin;
    Fragment mFragment;
    Button btn_back;
    String apkversion;
    TextView versiontxt;


    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_layout, container, false);

        btn_back=view.findViewById(R.id.btnback);
        btn_wifi=view.findViewById(R.id.wifi);

        btn_adminlogin=view.findViewById(R.id.adminlogin);

        versiontxt=view.findViewById(R.id.versiontxt);


        versiontxt.setText("Version :"+apkversion);


//        btn_wifi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFragment = WifiFragment.newInstance();
//                addFragment();
//            }
//        });

        btn_adminlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = SettingsLoginFragment.newInstance();
                addFragment();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constant.count = 0;

//                mFragment = MainBannerVideoFragment.newInstance();
//                addFragment();

            }
        });



        return view;
    }


    public void addFragment() {
        Objects.requireNonNull(getFragmentManager()).beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();
    }
}
