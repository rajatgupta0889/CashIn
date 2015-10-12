package com.mantralabsglobal.cashin.utils;

import com.mantralabsglobal.cashin.service.PerfiosService;

import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by hello on 8/15/2015.
 */
public class PerfiosUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


    public static <T> String getPayloadSignature(T payload, PrivateKey privateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, NoSuchProviderException, SignatureException {
        String payloadString = serialize(payload);
       // return  base16(sha1withRSA(condense(payloadString).getBytes(), privateKey));
        return sign(sha1(condense(payloadString)), privateKey);
    }

    public static boolean validateSignature(String hexSignature, String payload, PublicKey publicKey)
    {
        try {

            Signature signature1 = Signature.getInstance("SHA1withRSA", "BC");
            signature1.initVerify(publicKey);
            signature1.update(condense(payload).getBytes());
            return signature1.verify(base16ToByteArray(hexSignature));

        } catch ( NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String condense(String xml)
    {
        String temp = xml;
        temp = StringUtils.replace(temp, "\n", "");
        temp = StringUtils.replace(temp, "\t", "");
        temp = StringUtils.deleteWhitespace(temp);
        temp = XmlUtils.removeDeclaration(temp);
        return temp;
    }

    public static String sha1(String payload) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(payload.getBytes("UTF-8"));
        byte[] digest = md.digest();
        //byte[] encoded = Hex.encode(digest);
        //return new String(encoded);
        return base16(digest);
    }

    public static String sign(String payload, PrivateKey privateKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        return encrypt(payload, privateKey);
    }

    public static byte [] sha1withRSA(byte [] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SignatureException, NoSuchProviderException {
        Signature signature = null;
        signature = Signature.getInstance("SHA1withRSA", "BC");
        signature.initSign(privateKey, new SecureRandom());
        signature.update(data);
        return signature.sign();
    }

    public static String encrypt(String payload, Key privateKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encrypted = cipher.doFinal(payload.getBytes("UTF-8"));
        //byte[] encoded = Hex.encode(encrypted);
        //return new String(encoded);
        return base16(encrypted);
    }

    public static byte [] decrypt(byte [] data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static String base16(byte [] bytes)
    {
        final StringBuilder builder = new StringBuilder();
        for(byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] base16ToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static <T> String serialize(T payload){
        Serializer serializer = new Persister();
        StringWriter stringWriter = new StringWriter();
        try {
            serializer.write(payload, stringWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

}
