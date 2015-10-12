package com.mantralabsglobal.cashin.utils;

import android.util.Log;

import com.mantralabsglobal.cashin.service.AadharService;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

/**
 * Created by pk on 6/20/2015.
 */
public class AadharDAO {

    public static final int FATHER_SPOUSE_NAME_FIRST_PART_REMOVAL = 4;

    public static AadharService.AadharDetail getAadharDetailFromXML(String xml)
    {
        xml = fixAadharXMLString(xml);
        XmlPullParserFactory xmlFactoryObject;
        AadharService.AadharDetail aadharDetail = new AadharService.AadharDetail();

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
                        if(name != null && name.equals("PrintLetterBarcodeData")){
                            aadharDetail.setAadharNumber(aadharparser.getAttributeValue(null, "uid"));
                            aadharDetail.setName(aadharparser.getAttributeValue(null, "name"));

                            String yob = aadharparser.getAttributeValue(null, "yob");

                            Calendar c = Calendar.getInstance();
                            c.set(Integer.parseInt(yob), 1, 1);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            aadharDetail.setDob(sdf.format(c.getTime()));

                            String gender = aadharparser.getAttributeValue(null, "gender");
                            if(Objects.equals(gender, "M") || Objects.equals(gender, "MALE"))
                                gender = "Male";
                            else if (Objects.equals(gender, "F") || Objects.equals(gender, "FEMALE"))
                                gender = "Female";

                            aadharDetail.setGender(gender);
                            Log.d("AadharDAO", "Gender: " + gender);

                            aadharDetail.setAddress(getAttributeValue(aadharparser, "house") +
                                            getAttributeValue(aadharparser, "street") +
                                            getAttributeValue(aadharparser, "lm") +
                                            getAttributeValue(aadharparser, "po") +
                                            getAttributeValue(aadharparser, "dist") +
                                            getAttributeValue(aadharparser, "subdist") +
                                            getAttributeValue(aadharparser, "state") +
                                            getAttributeValue(aadharparser, "pc")
                            );

                            aadharDetail.setSonOf(getAttributeValue(aadharparser,"co").trim().substring(FATHER_SPOUSE_NAME_FIRST_PART_REMOVAL));
                            //aadharDetail.setLoc(aadharparser.getAttributeValue(null, "loc"));
                            //aadharDetail.setVtc(aadharparser.getAttributeValue(null, "vtc"));
                            //aadharDetail.setPostOffice(aadharparser.getAttributeValue(null, "po"));
                            //aadharDetail.setDistrict(aadharparser.getAttributeValue(null, "dist"));
                            //aadharDetail.setSubDistrict(aadharparser.getAttributeValue(null, "subdist"));
                            //aadharDetail.setState(aadharparser.getAttributeValue(null, "state"));
                           // aadharDetail.setPincode(aadharparser.getAttributeValue(null, "pc"));

                        }
                        break;
                }
                event = aadharparser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return aadharDetail;
    }

    private static String getAttributeValue(XmlPullParser aadharParser, String attributeName)
    {
        String value = aadharParser.getAttributeValue(null, attributeName);
        if(value== null)
            value = "";
        return value;
    }

    private static String fixAadharXMLString(String xml)
    {
        return XmlUtils.removeDeclaration(xml);
    }
}
