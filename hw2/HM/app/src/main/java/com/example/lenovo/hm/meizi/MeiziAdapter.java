package com.example.lenovo.hm.meizi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lenovo.hm.R;
import com.example.lenovo.hm.fragment.searchFragment;

import java.util.List;

public class MeiziAdapter extends RecyclerView.Adapter<MeiziAdapter.ViewHolder> {

    Context context;
    List<user> meizis;

    public MeiziAdapter(Context context, List<user> meizis) {
        this.context = context;
        this.meizis = meizis;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.meizi_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user meizi = meizis.get(position);

        Glide.with(context)
                .load(meizi.getImageUrl())
                .into(holder.image);

        holder.title.setText(meizi.getTitle());
        holder.name.setText(meizi.getName());
        holder.favorites.setText(String.valueOf(meizi.getFavorites()));
        holder.comments.setText(String .valueOf(meizi.getComments()));

    }

    @Override
    public int getItemCount() {
        return meizis.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout meiziItem; //这里也拿到了条目的Layout，后面添加onClick()方法用；
        ImageView image;
        TextView title;
        TextView name;
        TextView favorites;
        TextView comments;
        public ViewHolder(View itemView) {
            super(itemView);
            meiziItem = itemView.findViewById(R.id.meizi_item);
            image = itemView.findViewById(R.id.meizi_item_image);
            title = itemView.findViewById(R.id.meizi_item_title);
            name = itemView.findViewById(R.id.meizi_item_name);
            favorites = itemView.findViewById(R.id.meizi_item_favorites);
            comments = itemView.findViewById(R.id.meizi_item_comments);
        }
    }
}
