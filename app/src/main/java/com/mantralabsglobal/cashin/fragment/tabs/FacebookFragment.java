package com.mantralabsglobal.cashin.fragment.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.util.List;

/**
 * Created by pk on 13/06/2015.
 */
public class FacebookFragment extends BaseFragment  {

    private EditText dobEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnFacebookConnect = (Button) view.findViewById(R.id.btn_facebook_connect);
        final SimpleFacebook simpleFacebook = SimpleFacebook.getInstance(getActivity());

        btnFacebookConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!simpleFacebook.isLogin())
                {
                    simpleFacebook.login(new OnLoginListener() {
                        @Override
                        public void onLogin(String s, List<Permission> list, List<Permission> list1) {
                            Toast.makeText(getActivity(), "Logged in sucessfully", Toast.LENGTH_LONG).show();
                            getFacebookProfile();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getActivity(), "Logged in cancelled", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            Toast.makeText(getActivity(), "Logged in failed " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFail(String s) {
                            Toast.makeText(getActivity(), "Logged in failed " + s, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    getFacebookProfile();
                }

            }
        });

        registerChildView(getCurrentView().findViewById(R.id.ll_facebook_connect), View.VISIBLE);
        registerChildView(getCurrentView().findViewById(R.id.rl_facebook_details), View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SimpleFacebook simpleFacebook = SimpleFacebook.getInstance(getActivity());
        simpleFacebook.onActivityResult(requestCode, resultCode, data);
    }

    protected void getFacebookProfile()
    {
        SimpleFacebook simpleFacebook = SimpleFacebook.getInstance(getActivity());
        simpleFacebook.getProfile(new OnProfileListener() {

            @Override
            public void onThinking() {
                showDialog();
            }

            @Override
            public void onException(Throwable throwable) {
                hideDialog();
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFail(String reason) {
                hideDialog();
                Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete(Profile response) {
                hideDialog();

                Toast.makeText(getActivity(), response.getHometown(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
