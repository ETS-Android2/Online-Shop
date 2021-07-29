package com.project.onlineshop.activity.adminpanel.userslist;

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

public class UsersListFragment extends Fragment {

    private TextInputLayout ti_search;
    private TextInputEditText tiet_search;
    private ImageButton imgbtn_sortOptions;
    private RecyclerView users_listRecyclerView;

    private UsersListRVAdapter users_listRVAdapter;

    private static final String[] sortOptions = {"Name Ascending", "Name Descending", "Posters Count Ascending", "Posters Count Descending"};
    private int selectedOption = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        ti_search = view.findViewById(R.id.users_list_ti_search);
        tiet_search = view.findViewById(R.id.users_list_tiet_search);
        imgbtn_sortOptions = view.findViewById(R.id.users_list_imgbtn_sort_options);
        users_listRecyclerView = view.findViewById(R.id.rv_users_list);

        // Setting OnClickListener for search & sort options buttons
        ti_search.setEndIconOnClickListener(searchBtnOnClickListener);
        ti_search.clearFocus();
        imgbtn_sortOptions.setOnClickListener(sortOptionsImgBtnOnClickListener);

        // Setting up recycler view adapter
        users_listRVAdapter = new UsersListRVAdapter(getActivity(),
                tiet_search.getText().toString().trim(),
                sortOptions[selectedOption].replace("Posters Count", "posterscnt").trim());
        users_listRecyclerView.setAdapter(users_listRVAdapter);
        users_listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private final View.OnClickListener sortOptionsImgBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            MaterialAlertDialogBuilder sortOptionsDialog = new MaterialAlertDialogBuilder(getActivity());
            sortOptionsDialog.setTitle("Sort Options").setPositiveButton("Ok", new DialogInterface.OnClickListener() {

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
            //users_listRVAdapter.updateApiConnectionDetails(tiet_search.getText().toString(), sortOptions[selectedOption]);
            users_listRecyclerView.setAdapter(
                    new UsersListRVAdapter(getActivity(),
                            tiet_search.getText().toString().trim(),
                            sortOptions[selectedOption].replace("Posters Count", "posterscnt").trim()));
        }
    };
}
