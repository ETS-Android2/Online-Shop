package com.project.onlineshop.activity.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.changepassword.ChangePasswordActivity;
import com.project.onlineshop.activity.home.myposters.MyPostersFragment;
import com.project.onlineshop.activity.home.profile.ProfileFragment;
import com.project.onlineshop.activity.home.vitrine.VitrineFragment;
import com.project.onlineshop.activity.login.LoginActivity;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity thisInstance;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    User current_user;

    // Navigation drawer header
    private CircleImageView iv_avatar;
    private TextView tv_fullName, tv_email;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        current_user = Repository.getCurrentUser();
        thisInstance = this;

        // Making toolbar instance and set as ActionBar
        this.toolbar = findViewById(R.id.home_toolbar);
        this.setSupportActionBar(toolbar);

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Setting up Navigation Drawer functionality
        this.navigationView = (NavigationView) findViewById(R.id.home_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(home_navigation_drawer_listener);

        // Setting up navigation drawer header
        View nv_header = navigationView.getHeaderView(0);
        this.iv_avatar = nv_header.findViewById(R.id.home_navigation_drawer_header_iv_user_avatar);
        this.tv_fullName = nv_header.findViewById(R.id.home_navigation_drawer_header_tv_user_fullname);
        this.tv_email = nv_header.findViewById(R.id.home_navigation_drawer_header_tv_user_email);
        this.updateHeader();

        // Making Hamburger Icon for Toolbar
        this.drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.home_navigation_drawer_open, R.string.home_navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_fragment_container, new VitrineFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.home_navigation_drawer_options_market);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void updateHeader() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                current_user = Repository.getCurrentUser();
                if (current_user.avatar != null)
                    Glide.with(thisInstance).load(current_user.avatar).into(iv_avatar);
                tv_fullName.setText(current_user.name + " " + current_user.family);
                tv_email.setText(current_user.email);
            }
        });
    }

    private final NavigationView.OnNavigationItemSelectedListener home_navigation_drawer_listener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.home_navigation_drawer_options_profile:
                            selectedFragment = new ProfileFragment(thisInstance);
                            break;

                        case R.id.home_navigation_drawer_options_market:
                            selectedFragment = new VitrineFragment();
                            break;

                        case R.id.home_navigation_drawer_options_my_posters:
                            selectedFragment = new MyPostersFragment();
                            break;

                        case R.id.home_navigation_drawer_actions_change_password:
                            Intent open_changePassword = new Intent(thisInstance, ChangePasswordActivity.class);
                            startActivity(open_changePassword);
                            break;

                        case R.id.home_navigation_drawer_actions_remove_account:
                            removeAccount();
                            break;

                        case R.id.home_navigation_drawer_actions_log_out:
                            Repository.cleanData();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent loginIntent = new Intent(thisInstance, LoginActivity.class);
                            thisInstance.startActivity(loginIntent);
                            thisInstance.finish();
                            return false;

                    }

                    if (selectedFragment == null)
                        return false;

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_fragment_container, selectedFragment)
                            .commit();

                    drawerLayout.closeDrawer(GravityCompat.START);

                    return true;
                }
            };

    public void removeAccount() {
        EditText et_password = new EditText(HomeActivity.this);
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
        removeAlertDialog.setMessage("You account will be remove.\nAre you sure?");
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
                            Result<Response> result = Repository.removeAccount(Repository.getCurrentUser().id);

                            if (result instanceof Result.Error)
                                showMsg("Could not reach the server");

                            else if (!((Result.Success<Response>) result).data.getResult())
                                showMsg(((Result.Success<Response>) result).data.getMessage());

                            else {
                                Intent open_login = new Intent(thisInstance, LoginActivity.class);
                                Repository.cleanData();
                                showMsg(((Result.Success<Response>) result).data.getMessage());
                                thisInstance.finish();
                                thisInstance.startActivity(open_login);
                            }

                        } catch (Exception e) {
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

    public void goToMarket() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_fragment_container, new VitrineFragment())
                        .commit();
                navigationView.setCheckedItem(R.id.home_navigation_drawer_options_market);
            }
        });
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