package com.project.onlineshop.activity.home.vitrine.market;

import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.home.vitrine.bookmarks.BookmarksRVAdapter;
import com.project.onlineshop.activity.viewposter.ViewPosterActivity;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import java.util.ArrayList;
import java.util.List;

public class MarketRVAdapter extends RecyclerView.Adapter<MarketRVAdapter.MarketAdapterViewHolder> {

    private FragmentActivity activityInstance;

    private final int pageSize = 10; // Amount of posters getting from each call from server
    private int pageIndex = 0;
    private Boolean hasData = true;

    private String word_in_name = "";
    private String sort_option = "Name Ascending";

    private List<Poster> posters = new ArrayList<>();

    public MarketRVAdapter(FragmentActivity activityInstance, String word_in_name, String sort_option) {
        this.activityInstance = activityInstance;
        this.word_in_name = word_in_name;
        this.sort_option = sort_option;
        if (hasData)
            getNewPosters();
    }

    @NonNull
    @Override
    public MarketRVAdapter.MarketAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activityInstance);
        View row = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new MarketRVAdapter.MarketAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketRVAdapter.MarketAdapterViewHolder holder, int position) {
        holder.setImage(this.posters.get(position).poster_image);
        holder.setTitle(this.posters.get(position).name);
        holder.setDescription("price : " + this.posters.get(position).price);
        holder.setupMainLayout(activityInstance, this.posters.get(position));
        holder.setLikeBtnOnClickListener(this.posters.get(position));

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
                        showMsg("Could not reach the server");
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

    public class MarketAdapterViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rowBox;
        private RelativeLayout categoryLine;
        private TextView tv_title, tv_description;
        private ImageView iv_image;
        private ImageButton btn_like;

        public MarketAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            rowBox = itemView.findViewById(R.id.rv_row_mainlayout);
            categoryLine = itemView.findViewById(R.id.rv_row_category_line);
            tv_title = itemView.findViewById(R.id.tv_item_title);
            tv_description = itemView.findViewById(R.id.tv_item_description);
            iv_image = itemView.findViewById(R.id.poster_image);
            btn_like = itemView.findViewById(R.id.btn_like);
        }

        public void setupMainLayout(Context context, Poster poster) {
            categoryLine.setBackgroundColor(GlobalStorage.getPosterGroupColor(poster.posterGroup.ordinal()));

            rowBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent open_ViewPoster = new Intent(context, ViewPosterActivity.class);
                    GlobalStorage.setPosterToDisplay(poster);
                    context.startActivity(open_ViewPoster);
                }
            });
        }

        public void setLikeBtnOnClickListener(Poster poster) {
            if(Repository.getCurrentUser().hasPoster(poster))
                this.btn_like.setVisibility(View.INVISIBLE);
            if( Repository.getCurrentUser().isBookmarked(poster) )
                    this.btn_like.setImageResource(R.drawable.ic_red_heart);
            btn_like.setOnClickListener(GlobalStorage.getOnClickListenerForLikeBtn(activityInstance, this.btn_like, poster));
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