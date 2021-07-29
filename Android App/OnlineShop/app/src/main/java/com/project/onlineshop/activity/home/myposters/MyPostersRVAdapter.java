package com.project.onlineshop.activity.home.myposters;

import android.app.Activity;;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.viewposter.ViewPosterActivity;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;

public class MyPostersRVAdapter extends RecyclerView.Adapter<MyPostersRVAdapter.MyPostersAdapterViewHolder> {

    private Activity activityInstance;
    private User current_user_instance;

    public MyPostersRVAdapter(Activity activityInstance) {
        this.activityInstance = activityInstance;
        this.current_user_instance = Repository.getCurrentUser();
    }

    @NonNull
    @Override
    public MyPostersRVAdapter.MyPostersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activityInstance.getApplicationContext());
        View row = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MyPostersAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostersRVAdapter.MyPostersAdapterViewHolder holder, int position) {
        holder.setImage( current_user_instance.posters.get(position).poster_image );
        holder.setTitle( current_user_instance.posters.get(position).name );
        holder.setDescription( "price : " + current_user_instance.posters.get(position).price );
        holder.setupMainLayout(activityInstance, current_user_instance.posters.get(position));
    }

    @Override
    public int getItemCount() {
        return current_user_instance.posters.size();
    }

    public class MyPostersAdapterViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rowBox;
        private RelativeLayout categoryLine;
        private TextView tv_title, tv_description;
        private ImageView iv_image;
        private ImageButton btn_like;

        public MyPostersAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            rowBox = itemView.findViewById(R.id.rv_row_mainlayout);
            categoryLine = itemView.findViewById(R.id.rv_row_category_line);
            tv_title = itemView.findViewById(R.id.tv_item_title);
            tv_description = itemView.findViewById(R.id.tv_item_description);
            iv_image = itemView.findViewById(R.id.poster_image);
            btn_like = itemView.findViewById(R.id.btn_like);
            btn_like.setVisibility(View.GONE);
        }

        public void setupMainLayout(Activity activityInstance, Poster poster) {
            //((GradientDrawable)designBox.getBackground())
            //        .setColor(GlobalStorage.getPosterGroupColor(poster.posterGroup.ordinal()));

            categoryLine.setBackgroundColor(GlobalStorage.getPosterGroupColor(poster.posterGroup.ordinal()));

            rowBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent open_ViewPoster = new Intent(activityInstance, ViewPosterActivity.class);
                    GlobalStorage.setPosterToDisplay(poster);
                    activityInstance.startActivity(open_ViewPoster);
                }
            });
        }

        public void setImage(byte[] image) {
            if( image != null )
                Glide.with(iv_image.getContext())
                        .load(image)
                        .into(iv_image);
        }

        public void setTitle(String title) {
            this.tv_title.setText(title);
        }

        public void  setDescription(String description) {
            this.tv_description.setText(description);
        }

    }
}