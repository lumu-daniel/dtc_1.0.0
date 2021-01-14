package com.mlt.dtc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mlt.dtc.R;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.utility.RoundRectCornerImageView;
import java.util.ArrayList;

public
class TopBannerAdapter  extends PagerAdapter {
    private ArrayList<TopBannerObject> viewList;
    private Context mContext;
    private RoundRectCornerImageView imageView;
    private View view;
    private boolean flag;
    int pxn;

    public TopBannerAdapter(ArrayList<TopBannerObject> viewList, Context mContext,Boolean flag,int position) {
        this.flag = flag;
        this.viewList = viewList;
        this.mContext = mContext;
        this.pxn = position;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        view = LayoutInflater.from(mContext).inflate(R.layout.topbanner_viewpager,collection,false);


        imageView =  view.findViewById(R.id.image);

        try{
            if(pxn>viewList.size()){
                pxn = pxn%viewList.size();
            }

            //Todo convert image to base64
            Glide.with(mContext)
                    .load(flag?viewList.get(pxn).getTBImage():viewList.get(position).getTBImage())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().override(800, 800))
                    /*.apply(flag?new RequestOptions().override(700, 200):new RequestOptions().override(1300, 830))*/
                    .into(imageView);
//        Picasso.with(mContext).load(flag?viewList.get(position).getTBThumbnail():viewList.get(position).getTBImage()).into(imageView);
            flag = false;
            collection.addView(view, 0);
        }catch (Exception ex){
            ex.printStackTrace();
            Glide.with(mContext)
                    .load(flag?viewList.get(0).getTBThumbnail():viewList.get(0).getTBImage())
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().override(800, 800))
                    /*.apply(flag?new RequestOptions().override(700, 200):new RequestOptions().override(1300, 830))*/
                    .into(imageView);
        }

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
