package com.project.onlineshop.activity.adminpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.adminpanel.administration.AdministrationFragment;
import com.project.onlineshop.activity.adminpanel.posterslist.PostersListFragment;
import com.project.onlineshop.activity.adminpanel.userslist.UsersListFragment;

public class AdminPanelActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private AdministrationFragment administration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Setting up Bottom Navigation View functionality
        this.bottomNavigationView = findViewById(R.id.admin_panel_bottom_navigation);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(view_poster_bottomNav_listener);

        //this.administration = new AdministrationFragment();

        bottomNavigationView.setSelectedItemId(R.id.admin_panel_administration);
    }

    private final NavigationBarView.OnItemSelectedListener view_poster_bottomNav_listener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.admin_panel_administration:
                            selectedFragment = new AdministrationFragment();
                            break;

                        case R.id.admin_panel_users_list:
                            selectedFragment = new UsersListFragment();
                            break;

                        case R.id.admin_panel_posters_list:
                            selectedFragment = new PostersListFragment();
                            break;
                    }

                    if( selectedFragment == null )
                        return false;

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.admin_panel_fragment_container, selectedFragment)
                            .commit();

                    return true;
                }
            };
}