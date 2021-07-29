package com.project.onlineshop.activity.home.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.home.HomeActivity;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private final Fragment thisInstance = this;
    private HomeActivity homeInstance;

    private CircleImageView iv_avatar;
    private TextInputEditText tiet_name, tiet_family, tiet_email;
    private TextView tv_posters_cnt;
    private Button btn_save;

    private byte[] fragment_current_user_avatar;

    public ProfileFragment(HomeActivity homeInstance) {
        this.homeInstance = homeInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.iv_avatar = view.findViewById(R.id.profile_iv_avatar);
        this.tiet_name = view.findViewById(R.id.profile_tiet_name);
        this.tiet_family = view.findViewById(R.id.profile_tiet_family);
        this.tiet_email = view.findViewById(R.id.profile_tiet_email);
        this.tv_posters_cnt = view.findViewById(R.id.profile_tv_posters_cnt);
        this.btn_save = view.findViewById(R.id.profile_btn_savechanges);
        this.fragment_current_user_avatar = Repository.getCurrentUser().avatar;

        if( fragment_current_user_avatar != null )
            Glide.with(thisInstance).asBitmap()
                .load(fragment_current_user_avatar)
                .into(iv_avatar);

        tiet_name.setText(Repository.getCurrentUser().name);
        tiet_family.setText(Repository.getCurrentUser().family);
        tiet_email.setText(Repository.getCurrentUser().email);
        tv_posters_cnt.setText("Total Posters : \n" + Repository.getCurrentUser().posters.size());

        iv_avatar.setOnClickListener(avatarImageViewListener);
        btn_save.setOnClickListener(btnSaveListener);

        return view;
    }

    private final View.OnClickListener avatarImageViewListener = new View.OnClickListener() {
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
                        byte[] image = Glide.with(thisInstance.getContext())
                                .as(byte[].class)
                                .load(data.getData())
                                .submit()
                                .get();

                        updateImage(image);
                    }
                    catch (Exception e) {
                        showMsg("Something went wrong");
                    }
                }
            });


        } else if (resultCode == ImagePicker.RESULT_ERROR)
            showMsg(ImagePicker.getError(data));

        else
            showMsg("Task Cancelled");
    }

    private final View.OnClickListener btnSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            User updatedUser = Repository.getCurrentUser();

            updatedUser.name = tiet_name.getText().toString();
            updatedUser.family = tiet_family.getText().toString();
            updatedUser.avatar = fragment_current_user_avatar;

            GlobalStorage.worker.execute(new Runnable() {
                @Override
                public void run() {
                    Result<Response> result = Repository.updateUserToServer(updatedUser);

                    if (result instanceof Result.Error)
                        showMsg("Could not reach the server");

                    else {
                        Response response = ((Result.Success<Response>) result).data;

                        if (!response.getResult())
                            showMsg(response.getMessage());

                        else {
                            Repository.changeCurrentUser(updatedUser);
                            homeInstance.updateHeader();
                            homeInstance.goToMarket();
                            showMsg(response.getMessage());
                        }
                    }

                }
            });
        }
    };

    private void updateImage(byte[] image) {
        this.fragment_current_user_avatar = image;
        thisInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(thisInstance.getContext())
                        .load(image)
                        .into(iv_avatar);
            }
        });
    }

    private void showMsg(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(thisInstance.getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
