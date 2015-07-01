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

    public static AadharService.AadharDetail getAadharDetailFromXML(String xml)
    {
        XmlPullParserFactory xmlFactoryObject = null;
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
                        if(name.equals("PrintLetterBarcodeData")){
                            aadharDetail.setAadharNumber(aadharparser.getAttributeValue(null, "uid"));
                            aadharDetail.setName(aadharparser.getAttributeValue(null, "name"));

                            String yob = aadharparser.getAttributeValue(null, "yob");

                            Calendar c = Calendar.getInstance();
                            c.set(Integer.parseInt(yob), 1, 1);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            aadharDetail.setDob(sdf.format(c.getTime()));

                            String gender = aadharparser.getAttributeValue(null, "gender");
                            if(Objects.equals(gender, "M"))
                                gender = "Male";
                            else if (Objects.equals(gender, "F"))
                                gender = "Female";

                            aadharDetail.setGender(gender);
                            Log.d("AadharDAO", "Gender: " + gender);

                            aadharDetail.setAddress(aadharparser.getAttributeValue(null, "house") +
                                    aadharparser.getAttributeValue(null, "street") +
                                    aadharparser.getAttributeValue(null, "lm") +
                                    aadharparser.getAttributeValue(null, "po") +
                                    aadharparser.getAttributeValue(null, "dist") +
                                    aadharparser.getAttributeValue(null, "subdist")+
                                            aadharparser.getAttributeValue(null, "state") +
                                            aadharparser.getAttributeValue(null, "pc")
                            );
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

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aadharDetail;
    }
}
