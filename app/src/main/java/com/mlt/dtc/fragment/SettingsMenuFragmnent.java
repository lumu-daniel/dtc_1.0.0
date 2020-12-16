package com.mlt.dtc.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainFragment;
import com.mlt.dtc.common.SystemUIService;

public
class SettingsMenuFragmnent extends Fragment {

    Button btnBack;
    ImageView iv_Setting, iv_XACLauncher,iv_update;

    String ClassName;
    private ProgressDialog progressDialog;
    AdminFragment adminFragment;


    public SettingsMenuFragmnent() {
        // Required empty public constructor
    }

    public static SettingsMenuFragmnent newInstance() {
        return new SettingsMenuFragmnent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.settingsmenu, container, false);
        btnBack = view.findViewById(R.id.btnback);


        iv_Setting = view.findViewById(R.id.iv_setting);
        iv_XACLauncher = view.findViewById(R.id.launcher);

        iv_update=view.findViewById(R.id.update);

        //get Class Name
        ClassName = getClass().getCanonicalName();


        adminFragment=new AdminFragment();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);


        iv_Setting.setOnClickListener(v -> {
            try {
                getActivity().runOnUiThread(()->{
                    new AlertDialog.Builder(getActivity())
                            .setPositiveButton("Yes",(dialog, which) -> {
                                dialog.dismiss();
                                SettingsFramgnet don = new SettingsFramgnet();
                                don.show(getFragmentManager(), "");
                            })
                            .setNegativeButton("No",(dialog, which) -> {
                                dialog.dismiss();
                            })
                            .setTitle("Attention!")
                            .setMessage("Driver And Plate Details already entered.\n Do you want to change them?").create().show();
                });
            } catch (Exception e) {

            }


        });
        iv_XACLauncher.setOnClickListener(v -> {
            SystemUIService.setNaviButtonVisibility(getContext(), SystemUIService.NAVIBUTTON_NAVIBAR, View.VISIBLE);
            //It exits to the installer where the apk gets installed
            Intent intent = new Intent();
            intent.setClassName("com.xac.util.saioutility", "com.xac.util.saioutility.Main");
            startActivity(intent);
        });






        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment.count = 0;
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, SettingsLoginFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;

    }





}
