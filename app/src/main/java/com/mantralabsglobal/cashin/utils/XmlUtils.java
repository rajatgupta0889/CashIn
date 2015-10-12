package com.mantralabsglobal.cashin.utils;

/**
 * Created by hello on 8/15/2015.
 */
public class XmlUtils {

    public static String removeDeclaration(String xml)
    {
        if(xml.startsWith("<?xml")) {
            int firstDeclarationTagEnd = xml.indexOf(">");
            return xml.substring(firstDeclarationTagEnd+1);
        }
        return xml;
    }

}
