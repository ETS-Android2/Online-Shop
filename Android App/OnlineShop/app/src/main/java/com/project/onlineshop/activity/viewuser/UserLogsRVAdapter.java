package com.project.onlineshop.activity.viewuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.onlineshop.R;
import com.project.onlineshop.model.Log;
import com.project.onlineshop.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserLogsRVAdapter extends RecyclerView.Adapter<UserLogsRVAdapter.ViewUserAdapterViewHolder> {

    private Context context;

    private List<Log> logs;

    public UserLogsRVAdapter(Context context, User targetUser) {
        this.context = context;
        if( targetUser.logs == null )
            this.logs = new ArrayList<>();
        else
            this.logs = targetUser.logs;

        Collections.sort(this.logs, (a,b) -> {
         return b.submissionDate.compareTo(a.submissionDate);
        });
    }

    @NonNull
    @Override
    public UserLogsRVAdapter.ViewUserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.rv_logs_row, parent, false);
        return new ViewUserAdapterViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UserLogsRVAdapter.ViewUserAdapterViewHolder holder, int position) {
        holder.setReport( this.logs.get(position).report );
        holder.setEndpoint( this.logs.get(position).endpoint );
        holder.setDate( this.logs.get(position).submissionDate );
    }

    @Override
    public int getItemCount() {
        return this.logs.size();
    }

    public class ViewUserAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_report, tv_endpoint, tv_date;

        public ViewUserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_report = itemView.findViewById(R.id.rv_logs_tv_report);
            tv_endpoint = itemView.findViewById(R.id.rv_logs_tv_endpoint);
            tv_date = itemView.findViewById(R.id.rv_logs_tv_date);
        }

        public void setReport(String report) {
            this.tv_report.setText(report);
        }

        public void setEndpoint(String endpoint) {
            this.tv_endpoint.setText(endpoint);
        }

        public void  setDate(Date date) {
            this.tv_date.setText(date.toString());
        }

    }
}