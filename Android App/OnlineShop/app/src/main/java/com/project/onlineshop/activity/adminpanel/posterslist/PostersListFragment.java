package com.project.onlineshop.activity.adminpanel.posterslist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.onlineshop.R;

public class PostersListFragment extends Fragment {

    private TextInputLayout ti_search;
    private TextInputEditText tiet_search;
    private ImageButton imgbtn_sortOptions;
    private RecyclerView posters_listRecyclerView;

    private PostersListRVAdapter posters_listRVAdapter;

    private static final String[] sortOptions = {"Name Ascending", "Name Descending", "Price Ascending", "Price Descending"};
    private int selectedOption = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posters_list, container, false);

        ti_search = view.findViewById(R.id.posters_list_ti_search);
        tiet_search = view.findViewById(R.id.posters_list_tiet_search);
        imgbtn_sortOptions = view.findViewById(R.id.posters_list_imgbtn_sort_options);
        posters_listRecyclerView = view.findViewById(R.id.rv_posters_list);

        // Setting OnClickListener for search & sort options buttons
        ti_search.setEndIconOnClickListener(searchBtnOnClickListener);
        ti_search.clearFocus();
        imgbtn_sortOptions.setOnClickListener(sortOptionsImgBtnOnClickListener);

        // Setting up recycler view adapter
        posters_listRVAdapter = new PostersListRVAdapter(getActivity(), tiet_search.getText().toString(), sortOptions[selectedOption]);
        posters_listRecyclerView.setAdapter(posters_listRVAdapter);
        posters_listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private final View.OnClickListener sortOptionsImgBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            MaterialAlertDialogBuilder authenticationDialog = new MaterialAlertDialogBuilder(getActivity());
            authenticationDialog.setTitle("Sort Options").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }

            }).setSingleChoiceItems(sortOptions, selectedOption, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    selectedOption = i;
                }

            }).show();

        }
    };

    private final View.OnClickListener searchBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ti_search.clearFocus();
            //posters_listRVAdapter.updateApiConnectionDetails(tiet_search.getText().toString(), sortOptions[selectedOption]);
            posters_listRecyclerView.setAdapter(new PostersListRVAdapter(getActivity(), tiet_search.getText().toString(), sortOptions[selectedOption]));
        }
    };
}
