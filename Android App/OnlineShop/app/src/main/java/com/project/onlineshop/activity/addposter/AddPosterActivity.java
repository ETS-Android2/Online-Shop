package com.project.onlineshop.activity.addposter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.project.onlineshop.R;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

public class AddPosterActivity extends AppCompatActivity {

    private Activity thisInstance;

    private Poster activity_current_poster = null;

    private ImageView iv_poster_image;
    private TextInputEditText tiet_name, tiet_price, tiet_description;
    private AutoCompleteTextView acet_poster_group;
    private Button btn_submit;

    private byte[] poster_image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poster);

        this.thisInstance = this;
        this.iv_poster_image = findViewById(R.id.add_poster_iv_poster_image);
        this.tiet_name = findViewById(R.id.add_poster_tiet_name);
        this.tiet_price = findViewById(R.id.add_poster_tiet_price);
        this.tiet_description = findViewById(R.id.add_poster_tiet_description);
        this.acet_poster_group = findViewById(R.id.add_poster_acet_poster_group);
        this.btn_submit = findViewById(R.id.add_poster_btn_submit);

        activity_current_poster = GlobalStorage.getAndClearPosterToShow();

        // Setting up PosterGroup drop down menu
        String[] categories = getResources().getStringArray(R.array.poster_group);
        ArrayAdapter<String> categoriesAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        acet_poster_group.setAdapter(categoriesAdapter);

        this.iv_poster_image.setOnClickListener(pickImageBtnListener);
        this.btn_submit.setOnClickListener(btnSubmitPoster);

        if (activity_current_poster != null) {
            if(activity_current_poster.poster_image != null)
                Glide.with(this).load(activity_current_poster.poster_image).into(iv_poster_image);

            this.tiet_name.setText(activity_current_poster.name);

            if (activity_current_poster.price == 0)
                this.tiet_price.setText("Adaptive");
            else
                this.tiet_price.setText("" + activity_current_poster.price);

            for(String category : categories)
                if(category.equalsIgnoreCase(activity_current_poster.posterGroup.name()))
                    acet_poster_group.setText(category, false);

            this.tiet_description.setText(activity_current_poster.description);
            this.btn_submit.setText("Save Changes");
        }
    }

    private final View.OnClickListener pickImageBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ImagePicker.with(thisInstance)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            GlobalStorage.worker.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        byte[] image = Glide.with(thisInstance)
                                .as(byte[].class)
                                .load(data.getData())
                                .submit()
                                .get();

                        updateImage(image);
                    } catch (Exception e) {
                        showMsg("Something went wrong");
                    }
                }
            });


        } else if (resultCode == ImagePicker.RESULT_ERROR)
            showMsg(ImagePicker.getError(data));

        else
            showMsg("Task Cancelled");
    }

    private final View.OnClickListener btnSubmitPoster = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Poster new_poster;

            // Disabling button so user doesn't submit poster multiple times...
            btn_submit.setEnabled(false);

            if (activity_current_poster != null) {
                new_poster = activity_current_poster;
                new_poster.owner.setTarget(Repository.getCurrentUser());
                new_poster.description = tiet_description.getText().toString();
                if( poster_image != null )
                    new_poster.poster_image = poster_image;
                new_poster.posterGroup = Poster.PosterGroup.valueOf(acet_poster_group.getText().toString().toUpperCase());
                new_poster.price = Double.parseDouble(tiet_price.getText().toString());
            }
            else
                new_poster = new Poster(
                        Repository.getCurrentUser(),
                        tiet_name.getText().toString(),
                        tiet_description.getText().toString(),
                        poster_image,
                        Poster.PosterGroup.valueOf(acet_poster_group.getText().toString().toUpperCase()),
                        Double.parseDouble(tiet_price.getText().toString()));

            GlobalStorage.worker.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Result<Response> result;
                        if(activity_current_poster == null)
                            result = Repository.addPoster(new_poster);
                        else
                            result = Repository.updatePoster(new_poster);

                        if (result instanceof Result.Error) {
                            btn_submit.setEnabled(true);
                            showMsg("Could not reach the server");
                        }
                        else {
                            showMsg(((Result.Success<Response>) result).data.getMessage());
                            thisInstance.finish();
                        }

                    } catch (Exception e) {
                        btn_submit.setEnabled(true);
                        showMsg("Something went wrong");
                    }
                }
            });
        }
    };

    private void updateImage(byte[] image) {
        this.poster_image = image;
        thisInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(thisInstance)
                        .load(image)
                        .into(iv_poster_image);
            }
        });
    }

    public void addPosterBackBtn(View view) {
        this.finish();
    }

    private void showMsg(String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(thisInstance, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}