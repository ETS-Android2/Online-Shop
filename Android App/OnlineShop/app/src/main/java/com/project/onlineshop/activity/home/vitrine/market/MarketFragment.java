package com.project.onlineshop.activity.home.vitrine.market;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.project.onlineshop.R;
import com.project.onlineshop.activity.home.vitrine.bookmarks.BookmarksRVAdapter;

public class MarketFragment extends Fragment {

    private TextInputLayout ti_search;
    private TextInputEditText tiet_search;
    private ImageButton imgbtn_sortOptions;
    private RecyclerView marketRecyclerView;

    private MarketRVAdapter marketRVAdapter;

    private static final String[] sortOptions = {"Name Ascending", "Name Descending", "Price Ascending", "Price Descending"};
    private int selectedOption = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market, container, false);

        ti_search = view.findViewById(R.id.market_ti_search);
        tiet_search = view.findViewById(R.id.market_tiet_search);
        imgbtn_sortOptions = view.findViewById(R.id.market_imgbtn_sort_options);
        marketRecyclerView = view.findViewById(R.id.rv_market);

        // Setting OnClickListener for search & sort options buttons
        ti_search.setEndIconOnClickListener(searchBtnOnClickListener);
        ti_search.clearFocus();
        imgbtn_sortOptions.setOnClickListener(sortOptionsImgBtnOnClickListener);

        // Setting up recycler view adapter
        marketRVAdapter = new MarketRVAdapter(getActivity(), tiet_search.getText().toString(), sortOptions[selectedOption]);
        marketRecyclerView.setAdapter(marketRVAdapter);
        marketRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        marketRecyclerView.setAdapter(marketRVAdapter);
        marketRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
           //marketRVAdapter.updateApiConnectionDetails(tiet_search.getText().toString(), sortOptions[selectedOption]);
            marketRecyclerView.setAdapter(new MarketRVAdapter(getActivity(), tiet_search.getText().toString(), sortOptions[selectedOption]));
        }
    };
}
