package com.project.onlineshop.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.project.onlineshop.R;
import com.project.onlineshop.activity.adminpanel.AdminPanelActivity;
import com.project.onlineshop.activity.resendpassword.ResendPasswordActivity;
import com.project.onlineshop.activity.signup.SignupActivity;
import com.project.onlineshop.activity.home.HomeActivity;
import com.project.onlineshop.model.User;
import com.project.onlineshop.repository.GlobalStorage;
import com.project.onlineshop.repository.Repository;
import com.project.onlineshop.repository.Result;

public class LoginActivity extends AppCompatActivity {

    private EditText tiet_email, tiet_password;
    private TextView tv_resendpass, tv_signup;
    private Button txt_login;
    private Switch switch_showpassword;
    private AppCompatActivity thisIntance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.tiet_email = (EditText) findViewById(R.id.login_tiet_email);
        this.tiet_password = (EditText) findViewById(R.id.login_tiet_password);
        this.tv_resendpass = (TextView) findViewById(R.id.login_tv_resendpass);
        this.tv_signup = (TextView) findViewById(R.id.login_tv_signup);
        this.txt_login = (Button) findViewById(R.id.login_btn_login);

        // Checking if user already logged in so log in automatically
        // Also trying to update user's data if it has been updated on another device
        if( Repository.getCurrentUser() != null ) {
            User currentUser = Repository.getCurrentUser();
            GlobalStorage.worker.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Result<User> result = Repository.loginUser(currentUser.email, currentUser.password);
                        if (result instanceof Result.Error)
                            return;

                        // User probably has deleted account
                        if (((Result.Success<User>) result).data.email == null)
                            Repository.cleanData();

                        else {
                            Repository.changeCurrentUser(((Result.Success<User>) result).data);

                            Intent mainActivity;
                            if( Repository.getCurrentUser().isAdmin() )
                                mainActivity = new Intent(thisIntance, AdminPanelActivity.class);
                            else
                                mainActivity = new Intent(thisIntance, HomeActivity.class);

                            thisIntance.startActivity(mainActivity);
                            thisIntance.finish();
                        }
                    }
                    catch (Exception e) {}
                }
            });
            if( Repository.getCurrentUser() != null ) {
                Intent mainActivity;
                if( Repository.getCurrentUser().isAdmin() )
                    mainActivity = new Intent(thisIntance, AdminPanelActivity.class);
                else
                    mainActivity = new Intent(thisIntance, HomeActivity.class);

                thisIntance.startActivity(mainActivity);
                thisIntance.finish();
            }
        }
    }

    public void login(View view) {
        String email = tiet_email.getText().toString().trim();
        String password = tiet_password.getText().toString().trim();
        GlobalStorage.worker.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Result<User> result = Repository.loginUser(email, password);
                    if (result instanceof Result.Error) {
                        showMsg(((Result.Error<User>) result).exception.getMessage());
                        return;
                    }

                    if (((Result.Success<User>) result).data.email == null)
                        showMsg("User not found");

                    else {
                        showMsg("Hello " + ((Result.Success<User>) result).data.name);
                        Repository.changeCurrentUser(((Result.Success<User>) result).data);

                        Intent mainActivity;
                        if( Repository.getCurrentUser().isAdmin() )
                            mainActivity = new Intent(thisIntance, AdminPanelActivity.class);
                        else
                            mainActivity = new Intent(thisIntance, HomeActivity.class);

                        thisIntance.startActivity(mainActivity);
                        thisIntance.finish();
                    }
                }
                catch (Exception e) {
                    showMsg("Something went wrong");
                }
            }
        });
    }

    public void openSignUp(View view) {
        Intent signUp_intent = new Intent(this, SignupActivity.class);
        signUp_intent.putExtra("email", tiet_email.getText().toString());

        startActivity(signUp_intent);
    }

    public void openResendPassword(View view) {
        Intent resendPass_intent = new Intent(this, ResendPasswordActivity.class);
        resendPass_intent.putExtra("email", tiet_email.getText().toString());

        startActivity(resendPass_intent);
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