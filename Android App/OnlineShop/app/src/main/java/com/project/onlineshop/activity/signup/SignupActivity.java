package com.project.onlineshop.activity.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.project.onlineshop.R;
import com.project.onlineshop.api.Response;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

public class SignupActivity extends AppCompatActivity {

    private SignupActivity thisInstance;

    private static EditText tiet_name, tiet_family, tiet_phone_number, tiet_email, tiet_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        this.thisInstance = this;

        tiet_name = (EditText) findViewById(R.id.signup_tiet_name);
        tiet_family = (EditText) findViewById(R.id.signup_tiet_family);
        tiet_phone_number = (EditText) findViewById(R.id.signup_tiet_phone_number);
        tiet_email = (EditText) findViewById(R.id.signup_tiet_email);
        tiet_password = (EditText) findViewById(R.id.signup_tiet_password);
    }

    public void signUp(View view) {
        if (tiet_name.getText().toString().equals("") ||
                tiet_family.getText().toString().equals("") ||
                tiet_phone_number.getText().toString().equals("") ||
                tiet_email.getText().toString().equals("") ||
                tiet_password.getText().toString().equals(""))
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

        else
            GlobalStorage.worker.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Result<Response> result = Repository.signUp(
                                tiet_name.getText().toString().trim(),
                                tiet_family.getText().toString().trim(),
                                "+98" + tiet_phone_number.getText().toString().trim(),
                                tiet_email.getText().toString().trim(),
                                tiet_password.getText().toString().trim()
                        );

                        if (result instanceof Result.Error) {
                            showMsg("Could not reach the server");
                            return;
                        }
                        else if (((Result.Success<Response>) result).data.getResult())
                            thisInstance.finish();

                        showMsg(((Result.Success<Response>) result).data.getMessage());

                    } catch (Exception e) {
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