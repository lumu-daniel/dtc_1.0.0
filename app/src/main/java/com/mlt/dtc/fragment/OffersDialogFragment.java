package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.adapter.OffersAdapter;
import com.mlt.dtc.model.SideBannerObject;
import com.mlt.dtc.utility.Constant;


import java.util.ArrayList;
;

public class OffersDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    Context context;
    Fragment mFragment;

    ArrayList<SideBannerObject> getListofImages = new ArrayList<>();

    ImageView iv_close,iv_arrowleft_offers_Dialog,iv_arrowright_offers_Dialog;
    ViewPager AdsAiewPagerOffers;
    Button btnBuy;
    int position;
    String ClassName;
    private ShimmerFrameLayout mShimmerViewContainer;
    View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {

            AlertDialog.Builder customDialogMain = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
            context = getContext();

            LayoutInflater inflater = getActivity().getLayoutInflater();
            view = inflater.inflate(R.layout.custom_dialog_offers, null);

            iv_close = view.findViewById(R.id.iv_close);
            iv_arrowleft_offers_Dialog = view.findViewById(R.id.iv_arrowleft_offers_dialog);
            iv_arrowright_offers_Dialog = view.findViewById(R.id.iv_arrowright_offers_dialog);
            AdsAiewPagerOffers = view.findViewById(R.id.adsviewPageroffer);
            btnBuy = view.findViewById(R.id.btnbuy);


            customDialogMain.setView(view);
            customDialogMain.setCancelable(false);
            dialog = customDialogMain.show();


        } catch (Exception ex) {
        }
        return dialog;
    }


    private void initViews(ViewPager adsAiewPagerOffers) {
        try {

            OffersAdapter adapter = new OffersAdapter(getListofImages,getContext());


            adsAiewPagerOffers.setAdapter(adapter);
            //sets the position in viewpager
            adsAiewPagerOffers.setCurrentItem(position);

            adsAiewPagerOffers.setOffscreenPageLimit(3);



            adsAiewPagerOffers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int currentPage = adsAiewPagerOffers.getCurrentItem();
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception ex) {
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Bundle bundle = getArguments();

            getListofImages = (ArrayList<SideBannerObject>) getArguments().getSerializable(Constant.ArrayImagesSelectedOffers);
            position = bundle.getInt(Constant.PositionSelectedOffers);



            initViews(AdsAiewPagerOffers);

            //get Class Name
            ClassName = getClass().getCanonicalName();

            iv_arrowleft_offers_Dialog.setOnClickListener(v -> {
                int tab = AdsAiewPagerOffers.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    AdsAiewPagerOffers.setCurrentItem(tab);
                } else if (tab == 0) {
                    AdsAiewPagerOffers.setCurrentItem(tab);
                }
            });
            iv_arrowright_offers_Dialog.setOnClickListener(v -> {
                int tab = AdsAiewPagerOffers.getCurrentItem();
                tab++;
                AdsAiewPagerOffers.setCurrentItem(tab);
            });

            iv_close.setOnClickListener(v -> {

                MainActivity.mainActivity.onFinished("Finish");

                MainActivity.restrict_double_click = 0;
                dialog.dismiss();
            });

            btnBuy.setOnClickListener(view1 -> {
//                    try {
//                        mFragment = OffersPayNowFragmnent.newInstance();
//                        addFragment();
//                        dialog.dismiss();
//                    } catch (Exception e) {
//                        ExceptionService.ExceptionLogService(getContext(), e.getMessage(), ClassName, AndroidSerialNo);
//                    }
            });
        }catch (Exception ex) {
        }

        }


    public void addFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Window window = getDialog().getWindow();
        window.setLayout(1200, 750);
        window.setGravity(Gravity.CENTER);

    }

    @Override
    public void onPause() {

        super.onPause();
        Glide.get(getActivity()).clearMemory();
    }

    public void onStop() {
        super.onStop();
        dialog = null;
        context = null;
        mFragment = null;
//        getListofImages.clear();
        AdsAiewPagerOffers = null;
//        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iv_close=null;
        iv_arrowleft_offers_Dialog=null;
        iv_arrowright_offers_Dialog=null;
        dialog = null;
        context = null;
        mFragment = null;

//        getListofImages.clear();
        AdsAiewPagerOffers = null;
//        System.gc();

        Glide.get(getActivity()).clearMemory();
    }


}