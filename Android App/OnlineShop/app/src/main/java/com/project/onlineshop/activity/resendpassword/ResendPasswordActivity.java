package com.project.onlineshop.activity.resendpassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.project.onlineshop.R;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

public class ResendPasswordActivity extends AppCompatActivity {

    private static EditText tiet_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_password);

        tiet_email = (EditText) findViewById(R.id.resendpassword_tiet_email);

        Intent resendPass_intent = this.getIntent();
        tiet_email.setText(resendPass_intent.getStringExtra("email"));
    }

    public void resendPassword(View view) {
        String email = tiet_email.getText().toString();
        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<Response> result = Repository.resendPassword(email);
                    if (result instanceof Result.Error) {
                        showMsg("Could not reach the server");
                        return;
                    }

                    showMsg(((Result.Success<Response>) result).data.getMessage());

                    if( ((Result.Success<Response>) result).data.getResult() )
                        finish();
                }
                catch (Exception e) {
                    showMsg("Something went wrong");
                }
            }
        });
    }

    private void showMsg(String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}