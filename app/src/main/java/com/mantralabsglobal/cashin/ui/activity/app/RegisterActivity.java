package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
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


public class RegisterActivity extends BaseActivity implements Validator.ValidationListener {

    @NotEmpty
    @Email
    @InjectView(R.id.cet_email)
    CustomEditText et_userName;

    @Password
    @InjectView(R.id.cet_password)
    CustomEditText et_password;

    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.inject(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
    }


    @OnClick(R.id.btn_create_account)
    public void onCreateUser()
    {
        validator.validate();
    }

    @OnClick(R.id.btn_login_in)
    public void onLogin()
    {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        showProgressDialog(getString(R.string.title_please_wait), getString(R.string.registering), true, false);
        RestClient restClient =((Application) getApplication()).getRestClient();

        AuthenticationService.NewUser userPrincipal = new AuthenticationService.NewUser(et_userName.getText().toString(),et_password.getText().toString() );

        AuthenticationService service = restClient.getAuthenticationService();
        service.registerUser(userPrincipal, new Callback<AuthenticationService.AuthenticatedUser>() {
            @Override
            public void success(AuthenticationService.AuthenticatedUser authenticationResult, Response response) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.mantralabsglobal.cashin", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AuthenticationService.USER_EMAIL, authenticationResult.getEmail())
                        .putString(AuthenticationService.USER_ID, authenticationResult.getId());
                editor.apply();
                hideProgressDialog();
                showToastOnUIThread(getString(R.string.registration_sucess));
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
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
