package com.mlt.dtc.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mlt.dtc.R;
import com.mlt.dtc.modal.SideBannerObject;
import com.mlt.dtc.utility.RoundRectCornerImageView;

import java.util.ArrayList;


/**
 * Created by talal on 06/02/2017.
 */

public class OffersRecyclerViewAdapter extends RecyclerView.Adapter<OffersRecyclerViewAdapter.DataObjectHolder> {

    private Context mContext;
    private ArrayList<SideBannerObject> mDataSetmenus;


    private static RecyclerViewClickListener itemListener;

    public OffersRecyclerViewAdapter(ArrayList<SideBannerObject> mDataSetmenusimages, Context context, RecyclerViewClickListener itemListener) {
        this.itemListener = itemListener;
        this.mDataSetmenus = mDataSetmenusimages;
        this.mContext = context;

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_sideoffers_customize, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if (mDataSetmenus.get(position).equals(""))
            holder.mImage.setBackgroundResource(R.drawable.comingsoonoffers);
        else

            Glide.with(mContext)
                    .load(mDataSetmenus.get(position).getSBThumbNail())
                    .dontAnimate()
                    .apply(new RequestOptions().override(800, 400))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.mImage);
//            Glide.with(mContext).asBitmap().load(mDataSetmenus.get(position)).apply(new RequestOptions().placeholder(R.drawable.mltbottommenu)).into(holder.mImage);
//        Picasso.with(mContext).load(mDataSetmenus.get(position).getSBThumbNail()).placeholder(R.drawable.mltbottommenu)
//                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(holder.mImage);
//            Picasso.with(mContext).load(mDataSetmenus.get(position)).placeholder(R.drawable.mltbottommenu).into(holder.mImage);
//            Glide.with(mContext).asBitmap().load(mDataSetmenus.get(position))
//                .apply( new RequestOptions().override( ( imageWidth * 2 ) / 3, ((( imageWidth * 2 ) / 3) * imageHeight) / imageWidth ))
//                .into(holder.mImage);
//            Picasso.with(mContext).load(new File(mainActivity.FileStoragePath(Constant.Images) + "/" + mainActivity.GetFileName(mDataSetmenus.get(position)))).into(holder.mImage);


    }

    @Override
    public int getItemCount() {
        return mDataSetmenus.size();
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RoundRectCornerImageView mImage;

        public DataObjectHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setClickable(true);
            mImage = itemView.findViewById(R.id.iv_sideoffers);


        }

        @Override
        public void onClick(View view) {

            itemListener.recyclerViewListClicked(view, getAdapterPosition());

        }
    }

    public interface RecyclerViewClickListener {
       void recyclerViewListClicked(View v, int position);

        void onFinished(String result);
    }


}
