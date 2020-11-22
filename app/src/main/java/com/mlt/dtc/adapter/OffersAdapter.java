package com.mlt.dtc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mlt.dtc.R;
import com.mlt.dtc.modal.SideBannerObject;
import com.mlt.dtc.utility.RoundRectCornerImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;




public class OffersAdapter extends PagerAdapter {
    private RoundRectCornerImageView imageView;
        private ArrayList<SideBannerObject> viewList;
        private Context mContext;
        private View view;


    public OffersAdapter(ArrayList<SideBannerObject> viewList, Context mContext) {
        this.viewList = viewList;
        this.mContext = mContext;
    }


    @Override
        public Object instantiateItem(ViewGroup collection, int position) {
             view = LayoutInflater.from(mContext).inflate(R.layout.offerdialog_viewpager,collection,false);

            imageView =  view.findViewById(R.id.image);

        Glide.with(mContext)
                .load(viewList.get(position).getSBMainImage())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(800, 800))
                .into(imageView);


        collection.addView(view, 0);

            return view;
        }


        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }



    }


