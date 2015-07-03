package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;
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

    private static final String TAG = "LoginActivity";
    @NotEmpty
    @Email
    @InjectView(R.id.et_userName)
    EditText et_userName;

    @Password
    @InjectView(R.id.et_password)
    EditText et_password;

    @InjectView(R.id.gplus_sign_in_button)
    SignInButton btn_googlePlusSignIn;


    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    @Override
    protected void handleGooglePlusConnectionSuccess(String email)
    {
        createServerSession(email, "GOOGLE_PLUS_SIGNIN");
    }

    protected void createServerSession(final String email, final String password) {
        RestClient restClient =((Application) getApplication()).getRestClient();
        final AuthenticationService service = restClient.getAuthenticationService();

        AuthenticationService.UserPrincipal userPrincipal = new AuthenticationService.UserPrincipal();
        userPrincipal.setEmail(email);
        userPrincipal.setPassword(password);

        service.authenticateUser(userPrincipal, new Callback<AuthenticationService.AuthenticatedUser>() {
            @Override
            public void success(AuthenticationService.AuthenticatedUser authenticationResult, Response response) {

                hideProgressDialog();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(final RetrofitError error) {

                if (RetrofitUtils.isUserNotRegisteredError(error)) {
                    service.registerUser(new AuthenticationService.NewUser(email, password), new Callback<AuthenticationService.AuthenticatedUser>() {
                        @Override
                        public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                            createServerSession(email, password);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            signInFailed(RetrofitUtils.getErrorMessage(error).getMessage());
                        }
                    });
                } else {
                    signInFailed(RetrofitUtils.getErrorMessage(error).getMessage());
                }
            }
        });

    }

    public void signInFailed(final String error)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w("LoginActivity", error);
                hideProgressDialog();
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }



    @OnClick(R.id.loginButton)
    public void onLogin()
    {
        /*Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();*/
        validator.validate();
    }


    @OnClick(R.id.btn_create_account)
    public void onRegister()
    {
        Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.gplus_sign_in_button)
    public void signInWithGoogle() {
        super.googlePlusSignIn();
    }

    @Override
    public void onValidationSucceeded() {
        showProgressDialog(getString(R.string.title_please_wait), getString(R.string.authenticating), true, false);

        createServerSession(et_userName.getText().toString(), et_password.getText().toString());
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
