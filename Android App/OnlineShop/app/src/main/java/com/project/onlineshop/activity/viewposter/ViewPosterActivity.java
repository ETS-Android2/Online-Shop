package com.project.onlineshop.activity.viewposter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.addposter.AddPosterActivity;
import com.project.onlineshop.activity.viewuser.ViewUserActivity;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.model.Poster;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

public class ViewPosterActivity extends AppCompatActivity {

    private Activity thisInstance;

    private RelativeLayout imageLayout;
    private ImageView iv_image;
    private TextView tv_price, tv_phone, tv_name, tv_description;
    private Button btn_call;

    private Poster displayablePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_poster);

        thisInstance = this;
        displayablePoster = GlobalStorage.getAndClearPosterToShow();
        this.iv_image = findViewById(R.id.view_poster_iv_poster_image);
        this.imageLayout = findViewById(R.id.view_poster_image_layout);
        this.tv_price = findViewById(R.id.view_poster_tv_price);
        this.tv_phone = findViewById(R.id.view_poster_tv_phone);
        this.tv_name = findViewById(R.id.view_poster_tv_name);
        this.tv_description = findViewById(R.id.view_poster_tv_description);
        this.btn_call = findViewById(R.id.view_poster_btn_call_owner);

        Toolbar toolbar = findViewById(R.id.view_poster_toolbar);
        this.setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (displayablePoster == null)
            this.finish();

        imageLayout.setBackgroundColor(GlobalStorage.getPosterGroupColor(displayablePoster.posterGroup.ordinal()));

        if (displayablePoster.poster_image != null)
            Glide.with(this).load(displayablePoster.poster_image).into(iv_image);
        tv_price.setText("" + displayablePoster.price);
        tv_phone.setText(displayablePoster.owner_phone_number);
        tv_name.setText(displayablePoster.name);
        tv_description.setText(displayablePoster.description);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (displayablePoster == null)
            this.finish();

        imageLayout.setBackgroundColor(GlobalStorage.getPosterGroupColor(displayablePoster.posterGroup.ordinal()));

        if (displayablePoster.poster_image != null)
            Glide.with(this).load(displayablePoster.poster_image).into(iv_image);
        tv_price.setText("" + displayablePoster.price);
        tv_phone.setText(displayablePoster.owner_phone_number);
        tv_name.setText(displayablePoster.name);
        tv_description.setText(displayablePoster.description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_poster_toolbar_menu, menu);

        if (!Repository.getCurrentUser().isAdmin() && !Repository.getCurrentUser().hasPoster(displayablePoster))
            menu.findItem(R.id.view_poster_like).setVisible(true);

        if (Repository.getCurrentUser().isBookmarked(displayablePoster))
            menu.findItem(R.id.view_poster_like).setIcon(R.drawable.ic_red_heart);

        if (Repository.getCurrentUser().hasPoster(displayablePoster) || Repository.getCurrentUser().isAdmin()) {
            menu.findItem(R.id.view_poster_remove_poster).setVisible(true);
            menu.findItem(R.id.view_poster_edit_poster).setVisible(true);
        }

        if(Repository.getCurrentUser().isAdmin())
            menu.findItem(R.id.view_poster_change_poster_priority).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.view_poster_like:
                toggleLikePoster(item);
                break;

            case R.id.view_poster_change_poster_priority:
                changePriority();
                break;

            case R.id.view_poster_edit_poster:
                editPoster();
                break;

            case R.id.view_poster_remove_poster:
                removePoster();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleLikePoster(MenuItem item) {
        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Boolean result = Repository.toggleBookmark(displayablePoster);
                    if (result) {
                        thisInstance.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Repository.getCurrentUser().isBookmarked(displayablePoster))
                                    item.setIcon(R.drawable.ic_red_heart);
                                else
                                    item.setIcon(R.drawable.ic_gray_heart);
                            }
                        });
                    }
                }
                catch (Exception Ignored) { }
            }
        });
    }

    private void changePriority() {
        EditText et_priority = new EditText(ViewPosterActivity.this);
        et_priority.setHint("Priority");
        et_priority.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_priority.setText("" + displayablePoster.priority);

        // Getting priority as input in dialog
        MaterialAlertDialogBuilder priorityInputDialog = new MaterialAlertDialogBuilder(this);
        priorityInputDialog.setTitle("Poster Update");
        priorityInputDialog.setMessage("Input priority : ");
        priorityInputDialog.setIcon(R.drawable.ic_priority);
        priorityInputDialog.setView(et_priority);

        priorityInputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        priorityInputDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                displayablePoster.priority = Integer.parseInt(et_priority.getText().toString());
                GlobalStorage.worker.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Result<Response> result = Repository.updatePoster(displayablePoster);

                            if( result instanceof Result.Error )
                                showMsg("Could not reach the server");

                            else
                                showMsg(((Result.Success<Response>) result).data.getMessage());

                        }
                        catch (Exception e) {
                            showMsg("Something went wrong");
                        }
                    }
                });
            }
        });

        priorityInputDialog.show();
    }

    private void editPoster() {
        Intent open_addPoster = new Intent(this, AddPosterActivity.class);
        GlobalStorage.setPosterToDisplay(displayablePoster);
        startActivity(open_addPoster);
    }

    private void removePoster() {
        MaterialAlertDialogBuilder removeAlertDialog = new MaterialAlertDialogBuilder(this);
        removeAlertDialog.setTitle("Warning");
        removeAlertDialog.setMessage("Are you really want to remove this poster?");
        removeAlertDialog.setIcon(R.drawable.ic_warning);

        removeAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        removeAlertDialog.setPositiveButton("Do it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GlobalStorage.worker.execute(new Runnable() {
                    @Override
                    public void run() {
                        GlobalStorage.worker.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Result<Response> result = Repository.removePoster(displayablePoster);

                                    if (result instanceof Result.Error)
                                        showMsg("Could not reach the server");

                                    else {
                                        showMsg(((Result.Success<Response>) result).data.getMessage());
                                        thisInstance.finish();
                                    }
                                }
                                catch (Exception e) {
                                    showMsg("Something went wrong");
                                }
                            }
                        });
                    }
                });
            }
        });

        removeAlertDialog.show();
    }

    public void callOwner(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    123);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:12345678901")));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    callOwner(this.btn_call);

                break;

            default:
                break;
        }
    }

    public void viewOwnerProfile(View view) {
        Intent open_ViewUser = new Intent(this, ViewUserActivity.class);

        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<User> result = Repository.getPosterOwner(displayablePoster.id);

                    if (result instanceof Result.Error)
                        showMsg(((Result.Error<User>) result).exception.getMessage());

                    else {
                        User owner = ((Result.Success<User>) result).data;

                        if (owner.email == null)
                            showMsg("User not found");

                        else {
                            GlobalStorage.setUserToDisplay(owner);
                            thisInstance.startActivity(open_ViewUser);
                        }
                    }

                } catch (Exception e) {
                    showMsg(e.getMessage());
                }
            }
        });
    }

    public void viewPosterBackBtn(View view) {
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