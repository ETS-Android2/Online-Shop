package com.project.onlineshop.activity.adminpanel.posterslist;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.viewposter.ViewPosterActivity;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import java.util.ArrayList;
import java.util.List;

public class PostersListRVAdapter extends RecyclerView.Adapter<PostersListRVAdapter.PostersListAdapterViewHolder> {

    private FragmentActivity activityInstance;

    private final int pageSize = 10; // Amount of posters getting from each call from server
    private int pageIndex = 0;
    private Boolean hasData = true;

    private String word_in_name = "";
    private String sort_option = "Name Ascending";

    private List<Poster> posters = new ArrayList<>();

    public PostersListRVAdapter(FragmentActivity activityInstance, String word_in_name, String sort_option) {
        this.activityInstance = activityInstance;
        this.word_in_name = word_in_name;
        this.sort_option = sort_option;
        if (hasData)
            getNewPosters();
    }

    @NonNull
    @Override
    public PostersListRVAdapter.PostersListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activityInstance);
        View row = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new PostersListRVAdapter.PostersListAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostersListRVAdapter.PostersListAdapterViewHolder holder, int position) {
        holder.setImage(this.posters.get(position).poster_image);
        holder.setTitle(this.posters.get(position).name);
        holder.setDescription("price : " + this.posters.get(position).price);
        holder.setupMainLayout(activityInstance, this.posters.get(position));

        if (position >= posters.size() - 2 && hasData)
            getNewPosters();
    }

    private void getNewPosters() {
        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Result<List<Poster>> call_data = Repository.getPosters(word_in_name,
                            sort_option.toLowerCase(),
                            pageIndex,
                            pageSize);

                    if (call_data instanceof Result.Success) {

                        List<Poster> new_posters = ((Result.Success<List<Poster>>) call_data).data;

                        for (Poster poster : new_posters)
                            posters.add(poster);

                        activityInstance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });

                        if (new_posters.size() < pageSize)
                            hasData = false;
                        pageIndex++;
                    }
                    else {
                        showMsg(((Result.Error<List<Poster>>) call_data).exception.getMessage());
                    }

                } catch (Exception e) {
                    showMsg("Something went wrong");
                }
            }
        });
    }

    private void showMsg(String message) {
        activityInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activityInstance.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.posters.size();
    }

    public class PostersListAdapterViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rowBox;
        private RelativeLayout categoryLine;
        private TextView tv_title, tv_description;
        private ImageView iv_image;
        private ImageButton btn_like;

        public PostersListAdapterViewHolder(@NonNull View itemView) {
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