package com.project.onlineshop.activity.home.vitrine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.home.vitrine.bookmarks.BookmarksFragment;
import com.project.onlineshop.activity.home.vitrine.market.MarketFragment;

public class VitrineFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private FragmentContainer fragmentContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vitrine, container, false);

        // Setting up Bottom Navigation View functionality
        this.bottomNavigationView = view.findViewById(R.id.vitrine_bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(home_bottomNav_listener);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.vitrine_fragment_container, new MarketFragment())
                .commit();

        return view;
    }

    private final NavigationBarView.OnItemSelectedListener home_bottomNav_listener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.vitrine_market:
                            selectedFragment = new MarketFragment();
                            break;

                        case R.id.vitrine_bookmarks:
                            selectedFragment = new BookmarksFragment();
                            break;
                    }

                    if (selectedFragment == null)
                        return false;

                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.vitrine_fragment_container, selectedFragment)
                            .commit();

                    return true;
                }
            };
}
