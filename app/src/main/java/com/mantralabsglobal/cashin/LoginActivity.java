package com.mantralabsglobal.cashin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mantralabsglobal.cashin.Activity.BaseActivity;
import com.mantralabsglobal.cashin.rest.RestClient;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends BaseActivity implements Validator.ValidationListener {

    @NotEmpty
    @Email
    @InjectView(R.id.et_userName)
    EditText et_userName;

    @Password
    @InjectView(R.id.et_password)
    EditText et_password;

    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
    }


    @OnClick(R.id.loginButton)
    public void onLogin()
    {
        /*Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();*/
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        showProgressDialog( getString(R.string.title_please_wait),getString(R.string.authenticating),true, false);
        RestClient restClient =((Application) getApplication()).getRestClient();

        AuthenticationService.UserPrincipal userPrincipal = new AuthenticationService.UserPrincipal();
        userPrincipal.setEmail(et_userName.getText().toString());
        userPrincipal.setPassword(et_password.getText().toString());

        AuthenticationService service = restClient.getAuthenticationService();

        service.authenticateUser(userPrincipal, new Callback<AuthenticationService.AuthenticatedUser>() {
            @Override
            public void success(AuthenticationService.AuthenticatedUser authenticationResult, Response response) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.mantralabsglobal.cashin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AuthenticationService.USER_EMAIL, authenticationResult.getEmail())
                        .putString(AuthenticationService.USER_ID, authenticationResult.getId());
                editor.apply();
                hideProgressDialog();
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
                        hideProgressDialog();
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
