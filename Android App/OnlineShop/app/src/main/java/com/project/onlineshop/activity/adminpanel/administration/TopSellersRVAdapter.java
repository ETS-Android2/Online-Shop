package com.project.onlineshop.activity.adminpanel.administration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.viewuser.ViewUserActivity;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;

import java.util.ArrayList;
import java.util.List;

public class TopSellersRVAdapter extends RecyclerView.Adapter<TopSellersRVAdapter.TopSellersAdapterViewHolder> {

    private Activity activityInstance;

    private List<User> topSellers = new ArrayList<>();

    public TopSellersRVAdapter(Activity activityInstance, List<User> topSellers) {
        this.activityInstance = activityInstance;
        this.topSellers = topSellers;
    }

    @NonNull
    @Override
    public TopSellersRVAdapter.TopSellersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activityInstance.getApplicationContext());
        View row = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new TopSellersRVAdapter.TopSellersAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull TopSellersRVAdapter.TopSellersAdapterViewHolder holder, int position) {
        holder.setImage(this.topSellers.get(position).avatar);
        holder.setTitle(this.topSellers.get(position).name + " " + this.topSellers.get(position).family);
        holder.setDescription("posters : " + this.topSellers.get(position).posters.size());
        holder.setMainOnClickListener(activityInstance, this.topSellers.get(position));
    }

    @Override
    public int getItemCount() {
        return this.topSellers.size();
    }

    public class TopSellersAdapterViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rowBox;
        private RelativeLayout categoryLine;
        private TextView tv_title, tv_description;
        private ImageView iv_image;
        private ImageButton btn_like;

        public TopSellersAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            rowBox = itemView.findViewById(R.id.rv_row_mainlayout);
            categoryLine = itemView.findViewById(R.id.rv_row_category_line);
            tv_title = itemView.findViewById(R.id.tv_item_title);
            tv_description = itemView.findViewById(R.id.tv_item_description);
            iv_image = itemView.findViewById(R.id.poster_image);
            iv_image.setImageResource(R.drawable.ic_person_gray);
            btn_like = itemView.findViewById(R.id.btn_like);
            btn_like.setVisibility(View.GONE);
        }

        public void setMainOnClickListener(Activity activityInstance, User user) {
            categoryLine.setBackgroundColor(Color.WHITE);

            rowBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent open_ViewUser = new Intent(activityInstance.getApplicationContext(), ViewUserActivity.class);
                    GlobalStorage.setUserToDisplay(user);
                    activityInstance.startActivity(open_ViewUser);
                }
            });
        }

        public void setImage(byte[] image) {
            if (image != null)
                Glide.with(iv_image.getContext())
                        .load(image)
                        .into(iv_image);
        }

        public void setTitle(String title) {
            this.tv_title.setText(title);
        }

        public void setDescription(String description) {
            this.tv_description.setText(description);
        }
    }
}