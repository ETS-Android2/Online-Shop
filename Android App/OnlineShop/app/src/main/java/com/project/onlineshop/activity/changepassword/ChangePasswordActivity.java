package com.project.onlineshop.activity.changepassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.project.onlineshop.R;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

public class ChangePasswordActivity extends AppCompatActivity {

    private Activity thisInstance;

    private TextInputEditText tiet_old_password, tiet_new_password, tiet_repeat_new_password;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        this.thisInstance = this;
        this.tiet_old_password = findViewById(R.id.change_password_tiet_old_password);
        this.tiet_new_password = findViewById(R.id.change_password_tiet_new_password);
        this.tiet_repeat_new_password = findViewById(R.id.change_password_tiet_repeat_new_password);
        this.btn_submit = findViewById(R.id.change_password_btn_submit);
    }

    public void changePassword(View view) {
        String old_password = tiet_old_password.getText().toString();
        String new_password = tiet_new_password.getText().toString();
        String repeat_new_password = tiet_repeat_new_password.getText().toString();

        if (!new_password.equals(repeat_new_password))
            showMsg("Repeated password doesn't match");

        else if(!Repository.getCurrentUser().password.equals(old_password))
            showMsg("Password is wrong");

        else {
            btn_submit.setEnabled(false);

            GlobalStorage.worker.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Result<Response> result = Repository.changePassword(old_password, new_password);

                        if( result instanceof Result.Error ) {
                            showMsg("Could not reach the server");
                            btn_submit.setEnabled(true);
                        }

                        else {
                            showMsg(((Result.Success<Response>) result).data.getMessage());
                            thisInstance.finish();
                        }

                    } catch (Exception e) {
                        btn_submit.setEnabled(true);
                        showMsg("Something went wrong");
                    }
                }
            });
        }
    }

    private void showMsg(String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(thisInstance, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}