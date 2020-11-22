package com.mlt.dtc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.dtc.R;
import com.mlt.dtc.model.BottomMenu;
import java.util.ArrayList;

public
class RecyclerviewBottomAdapter extends RecyclerView.Adapter<RecyclerviewBottomAdapter.BottomViewHolder> {
    private Context context;
    ArrayList<BottomMenu> bottomMenus;

    private static ClickListener clickListener;

    public RecyclerviewBottomAdapter(Context context, ArrayList<BottomMenu> bottomMenus) {
        this.context = context;
        this.bottomMenus = bottomMenus;
    }

    @NonNull
    @Override
    public RecyclerviewBottomAdapter.BottomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bottom_menu, parent, false);

        return new RecyclerviewBottomAdapter.BottomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewBottomAdapter.BottomViewHolder holder, int position) {
        holder.itemimage.setImageResource(bottomMenus.get(position).getImages());


    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class BottomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView itemimage;
        public BottomViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemimage=itemView.findViewById(R.id.itemimage);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerviewBottomAdapter.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
