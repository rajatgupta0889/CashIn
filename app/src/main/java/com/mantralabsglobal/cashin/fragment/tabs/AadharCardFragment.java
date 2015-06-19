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
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.AadharDetail;
import com.mantralabsglobal.cashin.fragment.DatepickerDialogFragment;

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
public class AadharCardFragment extends Fragment implements DatePickerDialog.OnDateSetListener ,ZXingScannerView.ResultHandler{

    View currentView;
    private EditText dobEditText;
    private AadharDetail aadharDetail;
    private Spinner gender;
    private ArrayAdapter<CharSequence> genderAdapter ;
    private ZXingScannerView scannerView;
    private Spinner relation;
    private ArrayAdapter<CharSequence> relationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_aadhar_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //viewFlipper = (ViewFlipper) view.findViewById(R.id.aadhar_card_viewflipper);
        currentView = view;
        Button btnEdit = (Button) view.findViewById(R.id.bt_launch_aadhar_form);
        btnEdit.setOnClickListener(listener);

        ImageButton btn = (ImageButton) view.findViewById(R.id.ib_launchScanner);
        btn.setOnClickListener(listener);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_launchScanner);
        fab.setOnClickListener(listener);

        dobEditText = (EditText)view.findViewById(R.id.et_dob);

        dobEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
                Date defaultDate = null;
                try {
                    defaultDate = dateFormat.parse(dobEditText.getText().toString());
                } catch (ParseException e) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        defaultDate = sdf.parse(getString(R.string.default_dateof_birth));

                    } catch (ParseException e1) {

                        e1.printStackTrace();
                    }
                }

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new DatepickerDialogFragment(AadharCardFragment.this, defaultDate);
                newFragment.show(ft, "date_dialog");

            }
        });

        scannerView = (ZXingScannerView)view.findViewById(R.id.scanner);
        scannerView.setResultHandler(this);

        relation = (Spinner) view.findViewById(R.id.spin_relationship);
        relationAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.relationship_array, android.R.layout.simple_spinner_item);
        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relation.setAdapter(relationAdapter);

        gender = (Spinner) view.findViewById(R.id.spin_gender);
        genderAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);

        loadAadharCamera();

    }

    private AadharDetail parseAadharXML(String xml)
    {
        XmlPullParserFactory xmlFactoryObject = null;
        AadharDetail aadharDetail = new AadharDetail();

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser aadharparser = xmlFactoryObject.newPullParser();
            aadharparser.setInput(new StringReader(xml));


            int event = aadharparser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=aadharparser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("PrintLetterBarcodeData")){
                            aadharDetail.setUid(aadharparser.getAttributeValue(null, "uid"));
                            aadharDetail.setName(aadharparser.getAttributeValue(null, "name"));
                            aadharDetail.setGender(aadharparser.getAttributeValue(null, "gender"));
                            aadharDetail.setYearOfBirth(aadharparser.getAttributeValue(null, "yob"));
                            aadharDetail.setHouse(aadharparser.getAttributeValue(null, "house"));
                            aadharDetail.setStreet(aadharparser.getAttributeValue(null, "street"));
                            aadharDetail.setLandmark(aadharparser.getAttributeValue(null, "lm"));
                            aadharDetail.setLoc(aadharparser.getAttributeValue(null, "loc"));
                            aadharDetail.setVtc(aadharparser.getAttributeValue(null, "vtc"));
                            aadharDetail.setPostOffice(aadharparser.getAttributeValue(null, "po"));
                            aadharDetail.setDistrict(aadharparser.getAttributeValue(null, "dist"));
                            aadharDetail.setSubDistrict(aadharparser.getAttributeValue(null, "subdist"));
                            aadharDetail.setState(aadharparser.getAttributeValue(null, "state"));
                            aadharDetail.setPincode(aadharparser.getAttributeValue(null, "pc"));

                        }
                        break;
                }
                event = aadharparser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aadharDetail;
    }

    private void loadAadharCamera() {
        resetVisibilityOfFragments(R.id.ll_aadhar_camera);
    }

    private void loadAadharForm() {
        resetVisibilityOfFragments(R.id.rl_aadhar_detail);
        currentView.findViewById(R.id.fab_launchScanner).setVisibility(View.VISIBLE);

        if(aadharDetail!= null) {
            ((EditText) currentView.findViewById(R.id.et_name)).setText(aadharDetail.getName());
            ((EditText) currentView.findViewById(R.id.et_address)).setText(aadharDetail.getAddress());
            ((EditText) currentView.findViewById(R.id.et_aadhar)).setText(aadharDetail.getUid());
            gender.setSelection(genderAdapter.getPosition(aadharDetail.getGender()));
            relation.setSelection(relationAdapter.getPosition(aadharDetail.getRelation()));

        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());

        dobEditText.setText(dateFormat.format(cal.getTime()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            View child = null;
            if(v.getId() == currentView.findViewById(R.id.bt_launch_aadhar_form).getId())
            {
               loadAadharForm();
            }
            else if(v.getId() == currentView.findViewById(R.id.ib_launchScanner).getId()
                    || v.getId() == currentView.findViewById(R.id.fab_launchScanner).getId()
                    )
            {
                loadAadharScanner();
            }
           // if(child != null)
          //      viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(child));
        }
    };

    private void resetVisibilityOfFragments(int id)
    {
        currentView.findViewById(id).setVisibility(View.VISIBLE);
        if(id != R.id.scanner)
            currentView.findViewById(R.id.scanner).setVisibility(View.GONE);
        if(id != R.id.rl_aadhar_detail)
            currentView.findViewById(R.id.rl_aadhar_detail).setVisibility(View.GONE);
        if(id != R.id.ll_aadhar_camera)
            currentView.findViewById(R.id.ll_aadhar_camera).setVisibility(View.GONE);
        if(id != R.id.fab_launchScanner)
            currentView.findViewById(R.id.fab_launchScanner).setVisibility(View.GONE);
    }


    private void loadAadharScanner() {
        resetVisibilityOfFragments(R.id.scanner);
        List<BarcodeFormat> formatList = new ArrayList<>();
        formatList.add(BarcodeFormat.QR_CODE);
        scannerView.setFormats(formatList);
        scannerView.setFlash(true);
        scannerView.setAutoFocus(true);
        scannerView.startCamera();

    }

    @Override
    public void handleResult(Result rawResult) {
        scannerView.stopCamera();
        Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        aadharDetail = parseAadharXML(rawResult.getText());
        loadAadharForm();
    }
}
