package com.project.onlineshop.activity.adminpanel.administration;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.onlineshop.R;
import com.project.onlineshop.activity.login.LoginActivity;
import com.project.onlineshop.api.ServerReport;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

import java.util.ArrayList;

public class AdministrationFragment extends Fragment {

    private Activity parent;

    private TextView tv_users, tv_posters;
    private RecyclerView rv_top_sellers;
    private Button btn_log_out;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administration, container, false);

        this.parent = getActivity();
        this.tv_users = view.findViewById(R.id.administration_tv_users_count);
        this.tv_posters = view.findViewById(R.id.administration_tv_posters_count);
        this.rv_top_sellers = view.findViewById(R.id.administration_rv_top_sellers);
        this.btn_log_out = view.findViewById(R.id.administration_btn_log_out);
        this.btn_log_out.setOnClickListener(btnLogOut);

        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<ServerReport> result = Repository.getReport();

                    if( result instanceof Result.Error )
                        showMsg(((Result.Error<ServerReport>) result).exception.getMessage());

                    else
                        setupUI( ((Result.Success<ServerReport>) result).data );

                }
                catch (Exception e) {
                    showMsg("Something went wrong");
                }
            }
        });

        return view;
    }

    private void setupUI(ServerReport report) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_users.setText("" + report.getUsersCount());
                tv_posters.setText("" + report.getPostersCount());

                if (report.getTopSellers() == null || report.getTopSellers().size() < 1)
                    rv_top_sellers.setVisibility(View.INVISIBLE);
                else {
                    rv_top_sellers.setVisibility(View.VISIBLE);
                    rv_top_sellers.setAdapter(new TopSellersRVAdapter(parent, report.getTopSellers()));
                    rv_top_sellers.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            }
        });
    }

    private View.OnClickListener btnLogOut = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Repository.cleanData();
            Intent loginIntent = new Intent(parent, LoginActivity.class);
            parent.startActivity(loginIntent);
            parent.finish();
        }
    };

    private void showMsg(String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
