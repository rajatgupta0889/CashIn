package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.EStatementService;
import com.mantralabsglobal.cashin.service.NetBankingService;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.ui.Application;

import butterknife.InjectView;
import retrofit.Callback;

public class NetBankingFragment extends BaseBindableFragment<NetBankingService.NetBanking>
{

    NetBankingService netBankingService;

    @InjectView(R.id.net_banking_text)
    TextView netBankingText;

    @InjectView(R.id.net_banking_view)
    ViewGroup netBankingView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_net_banking, container, false);
    }

    @Override
    public void bindDataToForm(final NetBankingService.NetBanking value) {
        if (value != null) {
            if(value.getStatus() == 1)
                netBankingText.setText("Information already retrieved");
                    /*Toast.makeText(getActivity(),"Information already retrieved", Toast.LENGTH_SHORT).show();*/
            else if(value.getStatus() == 0){
                //TODO make request to perfios
                netBankingText.setText("Information not present");
                Toast.makeText(getActivity(),"Information not present\n"+value.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            save();
        }
    }

    @Override
    protected void onUpdate(NetBankingService.NetBanking updatedData, Callback<NetBankingService.NetBanking> saveCallback) {
        netBankingService.createNetBankingService(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(NetBankingService.NetBanking updatedData, Callback<NetBankingService.NetBanking> saveCallback) {
        netBankingService.createNetBankingService(updatedData, saveCallback);
    }

    @Override
    public NetBankingService.NetBanking getDataFromForm(NetBankingService.NetBanking base) {
        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<NetBankingService.NetBanking> dataCallback) {
        netBankingService.getNetBankingDetail(dataCallback);
    }


    @Override
    protected View getFormView() {
        return netBankingView;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        netBankingService = ((Application)getActivity().getApplication()).getRestClient().getNetBankingService();
        reset(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}