package com.mantralabsglobal.cashin.fragment.tabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.AadharDetail;
import com.mantralabsglobal.cashin.dao.AadharDAO;
import com.mantralabsglobal.cashin.fragment.DatepickerDialogFragment;
import com.mantralabsglobal.cashin.views.CustomEditText;
import com.mantralabsglobal.cashin.views.CustomSpinner;
import com.mantralabsglobal.cashin.views.SonOfSpinner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

//import eu.livotov.zxscan.ScannerView;

/**
 * Created by pk on 13/06/2015.
 */
public class AadharCardFragment extends BaseFragment implements ZXingScannerView.ResultHandler{

    private AadharDetail aadharDetail;
    private CustomSpinner gender;
    private ZXingScannerView scannerView;
    private SonOfSpinner relation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_aadhar_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnEdit = (Button) view.findViewById(R.id.bt_launch_aadhar_form);
        btnEdit.setOnClickListener(listener);

        ImageButton btn = (ImageButton) view.findViewById(R.id.ib_launchScanner);
        btn.setOnClickListener(listener);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_launchScanner);
        fab.setOnClickListener(listener);

        FloatingActionButton fabForm = (FloatingActionButton) view.findViewById(R.id.fab_launch_aadhar_form);
        fabForm.setOnClickListener(listener);

        scannerView = (ZXingScannerView)view.findViewById(R.id.scanner);
        scannerView.setResultHandler(this);

        gender = (CustomSpinner) view.findViewById(R.id.cs_gender);
        gender.setAdapter(getGenderAdapter());

        relation = (SonOfSpinner) view.findViewById(R.id.cs_sonOf);
        gender.setAdapter(gender.getAdapter());

        registerChildView(getCurrentView().findViewById(R.id.ll_aadhar_camera), View.VISIBLE);
        registerChildView(getCurrentView().findViewById(R.id.scanner), View.GONE);
        registerChildView(getCurrentView().findViewById(R.id.rl_aadhar_detail), View.GONE);
        registerFloatingActionButton((FloatingActionButton) getCurrentView().findViewById(R.id.fab_launchScanner), getCurrentView().findViewById(R.id.rl_aadhar_detail));
        registerFloatingActionButton( (FloatingActionButton)getCurrentView().findViewById(R.id.fab_launch_aadhar_form),getCurrentView().findViewById(R.id.scanner) );
    }



    private void loadAadharForm() {
       setVisibleChildView(getCurrentView().findViewById(R.id.rl_aadhar_detail));
        //TODO: Replace with form binding
         if(aadharDetail!= null) {
            ((CustomEditText) getCurrentView().findViewById(R.id.cc_name)).setText(aadharDetail.getName());
            ((CustomEditText) getCurrentView().findViewById(R.id.cc_address)).setText(aadharDetail.getAddress());
            ((CustomEditText) getCurrentView().findViewById(R.id.cc_aadhar)).setText(aadharDetail.getUid());
            gender.setSelection(getGenderAdapter().getPosition(aadharDetail.getGender()));
            relation.setSelection(relation.getRelationAdapter().getPosition(aadharDetail.getRelation()));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scannerView.getVisibility() == View.VISIBLE)
            scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(scannerView.getVisibility() == View.VISIBLE)
            scannerView.stopCamera();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            if(v.getId() == getCurrentView().findViewById(R.id.bt_launch_aadhar_form).getId()
                    || v.getId() == getCurrentView().findViewById(R.id.fab_launch_aadhar_form).getId())
            {

                loadAadharForm();
            }
            else if(v.getId() == getCurrentView().findViewById(R.id.ib_launchScanner).getId()
                    || v.getId() == getCurrentView().findViewById(R.id.fab_launchScanner).getId()
                    )
            {
                loadAadharScanner();
            }

            if(v.getId() == getCurrentView().findViewById(R.id.fab_launch_aadhar_form).getId())
            {
                scannerView.stopCamera();
            }
           // if(child != null)
          //      viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
        }
    };

    private void loadAadharScanner() {
        setVisibleChildView(getCurrentView().findViewById(R.id.scanner));
        List<BarcodeFormat> formatList = new ArrayList<>();
        formatList.add(BarcodeFormat.QR_CODE);
        scannerView.setFormats(formatList);
        scannerView.setFlash(false);
        scannerView.setAutoFocus(true);
        scannerView.startCamera();

    }

    @Override
    public void handleResult(Result rawResult) {
        scannerView.stopCamera();
        Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        aadharDetail = AadharDAO.getAadharDetailFromXML(rawResult.getText());
        loadAadharForm();
    }

}
