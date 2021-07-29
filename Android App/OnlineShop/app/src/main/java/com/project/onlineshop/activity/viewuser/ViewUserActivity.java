package com.project.onlineshop.activity.viewuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.project.onlineshop.R;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserActivity extends AppCompatActivity {

    private Activity thisInstance;

    private Toolbar toolbar;
    private CircleImageView iv_avatar;
    private TextView tv_fullname, tv_posters_cnt;
    private Button btn_phone, btn_email, rv_btn_switch;
    private TextView tv_rv_content_label, tv_empty_rv;
    private RecyclerView rv_posters;

    private User displayableUser;

    private UserPostersRVAdapter userPostersRVAdapter;
    private UserLogsRVAdapter userLogsRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // Initializing variables
        this.thisInstance = this;
        this.toolbar = findViewById(R.id.view_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.iv_avatar = findViewById(R.id.view_user_iv_image);
        this.tv_fullname = findViewById(R.id.view_user_tv_fullname);
        this.tv_posters_cnt = findViewById(R.id.view_user_tv_posters_count);
        this.btn_phone = findViewById(R.id.view_user_btn_call_user);
        this.btn_email = findViewById(R.id.view_user_btn_mail_user);
        this.rv_posters = findViewById(R.id.view_user_rv_user_posters);
        this.rv_btn_switch = findViewById(R.id.view_user_rv_content_switch);
        this.tv_rv_content_label = findViewById(R.id.view_user_rv_label);
        this.tv_empty_rv = findViewById(R.id.view_user_tv_empty_rv);
        this.displayableUser = GlobalStorage.getAndClearUserToShow();

        if( displayableUser == null )
            this.finish();

        if( displayableUser.isAdmin() )
            rv_btn_switch.setVisibility(View.INVISIBLE);

        if( displayableUser.posters.size() > 0 )
            tv_empty_rv.setVisibility(View.GONE);

        this.userPostersRVAdapter = new UserPostersRVAdapter(this, displayableUser);
        this.userLogsRVAdapter = new UserLogsRVAdapter(this, displayableUser);

        // Setting adapter for recyclerview if user has posters...
        rv_posters.setAdapter(userPostersRVAdapter);
        rv_posters.setLayoutManager(new LinearLayoutManager(this));

        Glide.with(this).load(displayableUser.avatar).into(iv_avatar);
        this.tv_fullname.setText(displayableUser.name + " " + displayableUser.family);
        this.tv_posters_cnt.setText(displayableUser.posters.size() + " posters");
        this.btn_phone.setText(displayableUser.phone_number);
        this.btn_email.setText(displayableUser.email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_user_toolbar_menu, menu);

        if (Repository.getCurrentUser().isAdmin())
            menu.findItem(R.id.view_user_remove_user).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.view_user_remove_user:
                removeUser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchAdapter(View view) {
        if( rv_btn_switch.getText().toString().equalsIgnoreCase("logs") ) {
            rv_posters.setAdapter(userLogsRVAdapter);
            rv_btn_switch.setText("Posters");
            tv_rv_content_label.setText("User Logs:");

            if( this.displayableUser.logs.size() < 1 ) {
                tv_empty_rv.setText("No Posters yet");
                tv_empty_rv.setVisibility(View.VISIBLE);
            }
            else
                tv_empty_rv.setVisibility(View.INVISIBLE);

        }
        else {
            rv_posters.setAdapter(userPostersRVAdapter);
            rv_btn_switch.setText("Logs");
            tv_rv_content_label.setText("User Posters:");

            if( this.displayableUser.posters.size() < 1 ) {
                tv_empty_rv.setText("No Posters yet");
                tv_empty_rv.setVisibility(View.VISIBLE);
            }
            else
                tv_empty_rv.setVisibility(View.INVISIBLE);

        }
    }

    private void removeUser() {
        EditText et_password = new EditText(ViewUserActivity.this);
        et_password.setHint("Password");
        et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Getting password as input in alert dialog for verification
        MaterialAlertDialogBuilder authenticationDialog = new MaterialAlertDialogBuilder(this);
        authenticationDialog.setTitle("Authentication");
        authenticationDialog.setMessage("Input your password");
        authenticationDialog.setIcon(R.drawable.ic_lock);
        authenticationDialog.setView(et_password);

        // Show alert that account will be remove
        MaterialAlertDialogBuilder removeAlertDialog = new MaterialAlertDialogBuilder(this);
        removeAlertDialog.setTitle("Warning");
        removeAlertDialog.setMessage("Do you really want to remove this user?");
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
                        try {
                            Result<Response> result = Repository.removeAccount(displayableUser.id);

                            if (result instanceof Result.Error)
                                showMsg("Could not reach the server");

                            else if (!((Result.Success<Response>) result).data.getResult())
                                showMsg(((Result.Success<Response>) result).data.getMessage());

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

        authenticationDialog.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!Repository.getCurrentUser().password.equals(et_password.getText().toString()))
                    showMsg("Password is wrong");

                else
                    removeAlertDialog.show();
            }
        });

        authenticationDialog.show();
    }

    public void callUser(View view) {
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
                    callUser(this.btn_phone);

                break;

            default:
                break;
        }
    }

    public void mailUser(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, displayableUser.email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Saw you on ILYA Shop!");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();;
    }

    public void viewUserBackBtn(View view) {
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