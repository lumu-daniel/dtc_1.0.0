package com.mlt.dtc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.infinitebanner.AbsBannerAdapter;
import com.github.infinitebanner.InfiniteBannerView;
import com.mlt.dtc.common.RoundRectCornerImageView;
import com.mlt.dtc.model.TopBannerObject;
import java.util.ArrayList;


public
class BannerAdapter extends AbsBannerAdapter {
    private ArrayList<TopBannerObject> viewList;
    private Context mContext;
    private boolean flag;

    public BannerAdapter(ArrayList<TopBannerObject> viewList, Context mContext, Boolean flag) {
        this.flag = flag;
        this.viewList = viewList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    protected View makeView(InfiniteBannerView parent) {
        RoundRectCornerImageView imageView = new RoundRectCornerImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        return imageView;
    }

    @Override
    protected void bind(View view, int position) {

        Glide.with(mContext)
                .load(flag?viewList.get(position).getTBThumbnail():viewList.get(position).getTBImage())
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(800, 800))
                /*.apply(flag?new RequestOptions().override(700, 200):new RequestOptions().override(1300, 830))*/
                .into((RoundRectCornerImageView) view);

    }
}
