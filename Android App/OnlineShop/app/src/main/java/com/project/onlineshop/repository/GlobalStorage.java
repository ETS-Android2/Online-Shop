package com.project.onlineshop.repository;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.project.onlineshop.R;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.model.PosterGroup;
import com.project.onlineshop.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalStorage extends Application {

    private static final String PREFRENCES_FILE_NAME = "com.android.ilyashop.data";
    private static final String CURRENT_USER = "current_user";
    private static final String IS_NIGHT_MODE = "is_night_mode";

    private static final int[] posterGroupsColors = {
            Color.parseColor("#2fd65c"),
            Color.parseColor("#e1ed58"),
            Color.parseColor("#ffa53d"),
            Color.parseColor("#eb815e"),
            Color.parseColor("#d460eb"),
            Color.parseColor("#4c65e0")};

    private static long current_user_id = -99;

    private static SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static Poster temporaryPosterInstance = null;
    private static User temporaryUserInstance = null;

    public static final ExecutorService worker = Executors.newFixedThreadPool(2);

    @Override
    public void onCreate() {
        super.onCreate();

        //current_user_id = 54730902911798472L;
        pref = getSharedPreferences(PREFRENCES_FILE_NAME, MODE_PRIVATE);
        editor = pref.edit();
        current_user_id = pref.getLong(CURRENT_USER, -99);

        Repository.init(this,
                current_user_id,
                getResources().getString(R.string.server_ip),
                getResources().getString(R.string.server_port));
    }

    @Override
    public void onTerminate() {
        editor.putLong(CURRENT_USER, Repository.getCurrentUser().id);
        editor.apply();
        super.onTerminate();
    }

    public void saveUser(long user_id) {
        current_user_id = user_id;
        editor.putLong(CURRENT_USER, current_user_id);
        editor.apply();
    }

    public void cleanData() {
        editor.clear();
        current_user_id = -99;
    }

    public static void setUserToDisplay(User user) {
        temporaryUserInstance = user;
    }

    public static User getAndClearUserToShow() {
        User temp = temporaryUserInstance;
        temporaryUserInstance = null;
        return temp;
    }

    public static void setPosterToDisplay(Poster poster) {
        temporaryPosterInstance = poster;
    }

    public static Poster getAndClearPosterToShow() {
        Poster temp = temporaryPosterInstance;
        temporaryPosterInstance = null;
        return temp;
    }

    public static int getPosterGroupColor(int group) {
        return posterGroupsColors[group];
    }

    public static View.OnClickListener getOnClickListenerForLikeBtn(Activity activityInstance, ImageButton btnLike, Poster poster) {
        User target = Repository.getCurrentUser();
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalStorage.worker.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Boolean result = Repository.toggleBookmark(poster);
                            if (result) {
                                activityInstance.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Repository.getCurrentUser().isBookmarked(poster))
                                            btnLike.setImageResource(R.drawable.ic_red_heart);
                                        else
                                            btnLike.setImageResource(R.drawable.ic_gray_heart);
                                    }
                                });
                            }
                        }
                        catch (Exception e) {}
                    }
                });
            }
        };
    }
}
