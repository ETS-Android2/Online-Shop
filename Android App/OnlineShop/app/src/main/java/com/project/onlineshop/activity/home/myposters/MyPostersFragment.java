package com.project.onlineshop.activity.home.myposters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.addposter.AddPosterActivity;
import com.project.onlineshop.repository.Repository;

public class MyPostersFragment extends Fragment {

    private RecyclerView myPostersRecyclerView;

    private FloatingActionButton fab_addPoster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posters, container, false);

        // Setting My Posters Recycler View
        myPostersRecyclerView = view.findViewById(R.id.rv_my_posters);
        myPostersRecyclerView.setAdapter(new MyPostersRVAdapter(getActivity()));
        myPostersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fab_addPoster = view.findViewById(R.id.my_posters_fab_add_poster);
        fab_addPoster.setOnClickListener(addPosterBtnListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myPostersRecyclerView.setAdapter(new MyPostersRVAdapter(getActivity()));
        myPostersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private final View.OnClickListener addPosterBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent open_addPoster = new Intent(getActivity(), AddPosterActivity.class);
            startActivity(open_addPoster);
        }
    };
}
