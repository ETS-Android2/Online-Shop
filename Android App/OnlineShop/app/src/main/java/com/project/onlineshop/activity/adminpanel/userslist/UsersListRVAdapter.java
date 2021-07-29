package com.project.onlineshop.activity.adminpanel.userslist;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.viewposter.ViewPosterActivity;
import com.project.onlineshop.activity.viewuser.ViewUserActivity;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import java.util.ArrayList;
import java.util.List;

public class UsersListRVAdapter extends RecyclerView.Adapter<UsersListRVAdapter.UsersListAdapterViewHolder> {

    private FragmentActivity activityInstance;

    private final int pageSize = 10; // Amount of users getting from each call from server
    private int pageIndex = 0;
    private Boolean hasData = true;

    private String word_in_name = "";
    private String sort_option = "Name Ascending";

    private List<User> users = new ArrayList<>();

    public UsersListRVAdapter(FragmentActivity activityInstance, String word_in_name, String sort_option) {
        this.activityInstance = activityInstance;
        this.word_in_name = word_in_name;
        this.sort_option = sort_option;
        if (hasData)
            getNewUsers();
    }

    @NonNull
    @Override
    public UsersListRVAdapter.UsersListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activityInstance);
        View row = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new UsersListRVAdapter.UsersListAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListRVAdapter.UsersListAdapterViewHolder holder, int position) {
        holder.setImage(this.users.get(position).avatar);
        holder.setTitle(this.users.get(position).name + " " + this.users.get(position).family);
        holder.setDescription("posters : " + this.users.get(position).posters.size());
        holder.setMainOnClickListener(activityInstance, this.users.get(position));

        if (position >= users.size() - 2 && hasData)
            getNewUsers();
    }

    private void getNewUsers() {
        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Result<List<User>> call_data = Repository.getUsers(word_in_name,
                            sort_option.toLowerCase(),
                            pageIndex,
                            pageSize);

                    if (call_data instanceof Result.Success) {

                        List<User> new_users = ((Result.Success<List<User>>) call_data).data;

                        for (User user : new_users)
                            users.add(user);

                        activityInstance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });

                        if (new_users.size() < pageSize)
                            hasData = false;
                        pageIndex++;
                    }
                    else {
                        showMsg(((Result.Error<List<User>>) call_data).exception.getMessage());
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
        return this.users.size();
    }

    public class UsersListAdapterViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rowBox;
        private RelativeLayout categoryLine;
        private TextView tv_title, tv_description;
        private ImageView iv_image;
        private ImageButton btn_like;

        public UsersListAdapterViewHolder(@NonNull View itemView) {
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