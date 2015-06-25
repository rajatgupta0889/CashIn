package com.mantralabsglobal.cashin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.mantralabsglobal.cashin.service.AuthenticationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class LoginActivity extends Activity {

    @InjectView(R.id.et_userName)
    EditText et_userName;

    @InjectView(R.id.et_password)
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.loginButton)
    public void onLogin()
    {
        Gson gson=  new GsonBuilder().setDateFormat(getString(R.string.server_date_time_format)).create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.server_url))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();

        AuthenticationService.UserPrincipal userPrincipal = new AuthenticationService.UserPrincipal();
        userPrincipal.setEmail(et_userName.getText().toString());
        userPrincipal.setPassword(et_password.getText().toString());

        AuthenticationService service = restAdapter.create(AuthenticationService.class);
        service.authenticateUser(userPrincipal, new Callback<AuthenticationService.AuthenticationResult>() {
            @Override
            public void success(AuthenticationService.AuthenticationResult authenticationResult, Response response) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.mantralabsglobal.cashin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AuthenticationService.USER_EMAIL, authenticationResult.getEmail());
                editor.putString(AuthenticationService.USER_ID, authenticationResult.getId());
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("LoginActivity", error);
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
