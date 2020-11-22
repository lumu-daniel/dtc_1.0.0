package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.adapter.TopBannerAdapter;
import com.mlt.dtc.common.Constant;
import com.mlt.dtc.model.TopBannerObject;
import java.util.ArrayList;

public
class TopBannerDialogFragment  extends DialogFragment{
    private AlertDialog dialog;
    Context context;
    Fragment mFragment;
    MainActivity mainActivity;
    ArrayList<TopBannerObject> getListOfImages = new ArrayList<>();

    ViewPager AdsAiewPager;
    ImageView iv_close,iv_arrowleft_topbanner_Dialog,iv_arrowright_topbanner_Dialog;
    int position;
    private TopBannerAdapter adapter;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try{


            mainActivity = MainActivity.getInstance();

            AlertDialog.Builder customDialogMain = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
            context = getContext();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.tobbanner_dialog, null);
            iv_close = view.findViewById(R.id.iv_close);
            iv_arrowleft_topbanner_Dialog = view.findViewById(R.id.iv_arrowleft_topbanner_dialog);
            iv_arrowright_topbanner_Dialog = view.findViewById(R.id.iv_arrowright_topbanner_dialog);
            AdsAiewPager = view.findViewById(R.id.adsviewPager);
//            AdsAiewPager.setScrollDuration(2000);

            customDialogMain.setView(view);
            customDialogMain.setCancelable(false);
            dialog = customDialogMain.show();
            dialog.setCanceledOnTouchOutside(false);


        }catch (Exception ex){
            ex.printStackTrace();
        }
        return dialog;

    }

    private void initViews( ViewPager adsAiewPagerOffers) {
        try {

            adapter = new TopBannerAdapter(getListOfImages,getContext(),false);

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
            bundle.get(Constant.PositionTopBanner);
            getListOfImages = (ArrayList<TopBannerObject>) getArguments().getSerializable(Constant.TopImagesSelectedOffers);
            position = bundle.getInt(Constant.PositionTopBanner);

            initViews(AdsAiewPager);

            iv_close.setOnClickListener(v -> {

                MainActivity.mainActivity.onFinished("Finish");
                dialog.dismiss();

            });

            iv_arrowleft_topbanner_Dialog.setOnClickListener(v -> {

                int tab = AdsAiewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    AdsAiewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    AdsAiewPager.setCurrentItem(tab);
                }
            });
            iv_arrowright_topbanner_Dialog.setOnClickListener(v -> {
                int tab = AdsAiewPager.getCurrentItem();
                tab++;
                AdsAiewPager.setCurrentItem(tab);
            });
        } catch (Exception ex) {
        }

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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        mainActivity.isSelected=false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context=null;
        AdsAiewPager=null;
        iv_close=null;
        iv_arrowleft_topbanner_Dialog=null;
        iv_arrowright_topbanner_Dialog=null;

        dialog=null;
//        System.gc();
        Glide.get(getActivity()).clearMemory();

    }


}