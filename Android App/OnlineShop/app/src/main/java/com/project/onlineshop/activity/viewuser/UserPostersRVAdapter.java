package com.project.onlineshop.activity.viewuser;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class UserPostersRVAdapter extends RecyclerView.Adapter<UserPostersRVAdapter.ViewUserAdapterViewHolder> {

    private Context context;

    private List<Poster> posters;

    public UserPostersRVAdapter(Context context, User targetUser) {
        this.context = context;
        if( targetUser.posters == null )
            this.posters = new ArrayList<>();
        else
            this.posters = targetUser.posters;
    }

    @NonNull
    @Override
    public UserPostersRVAdapter.ViewUserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ViewUserAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostersRVAdapter.ViewUserAdapterViewHolder holder, int position) {
        holder.setImage( this.posters.get(position).poster_image );
        holder.setTitle( this.posters.get(position).name );
        holder.setDescription( "price : " + this.posters.get(position).price );
        holder.setupMainLayout(context, this.posters.get(position));
    }

    @Override
    public int getItemCount() {
        return this.posters.size();
    }

    public class ViewUserAdapterViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rowBox;
        private CardView designBox;
        private TextView tv_title, tv_description;
        private ImageView iv_image;
        private ImageButton btn_like;

        public ViewUserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            rowBox = itemView.findViewById(R.id.rv_row_mainlayout);
            designBox = itemView.findViewById(R.id.rv_row_poster_box);
            tv_title = itemView.findViewById(R.id.tv_item_title);
            tv_description = itemView.findViewById(R.id.tv_item_description);
            iv_image = itemView.findViewById(R.id.poster_image);
            btn_like = itemView.findViewById(R.id.btn_like);
            btn_like.setVisibility(View.GONE);
        }

        public void setupMainLayout(Context context, Poster poster) {
            //((GradientDrawable)designBox.getBackground())
            //        .setColor(GlobalStorage.getPosterGroupColor(poster.posterGroup.ordinal()));

            designBox.setCardBackgroundColor(GlobalStorage.getPosterGroupColor(poster.posterGroup.ordinal()));

            rowBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent open_ViewPoster = new Intent(context, ViewPosterActivity.class);
                    GlobalStorage.setPosterToDisplay(poster);
                    context.startActivity(open_ViewPoster);
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