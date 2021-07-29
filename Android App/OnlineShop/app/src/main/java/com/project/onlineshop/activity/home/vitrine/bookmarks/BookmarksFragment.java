package com.project.onlineshop.activity.home.vitrine.bookmarks;

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

import com.project.onlineshop.R;
import com.project.onlineshop.activity.home.myposters.MyPostersRVAdapter;
import com.project.onlineshop.repository.Repository;

public class BookmarksFragment extends Fragment {

    private RecyclerView bookmarksRecyclerView;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        bookmarksRecyclerView = view.findViewById(R.id.rv_bookmarks);
        bookmarksRecyclerView.setAdapter(new BookmarksRVAdapter(getActivity()));
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bookmarksRecyclerView.setAdapter(new BookmarksRVAdapter(getActivity()));
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
