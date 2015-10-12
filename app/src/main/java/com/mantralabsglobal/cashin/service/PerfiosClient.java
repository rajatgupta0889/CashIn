package com.mantralabsglobal.cashin.service;

import android.content.Context;
import android.util.Base64;

import com.mantralabsglobal.cashin.R;
import com.squareup.okhttp.OkHttpClient;

import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.SimpleXMLConverter;

/**
 * Created by hello on 8/15/2015.
 */
public class PerfiosClient {

    private final PerfiosService perfoisService;

    private static PerfiosClient DEFAULT;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public PerfiosClient(Context context)
    {
        OkHttpClient client = new OkHttpClient(); //create OKHTTPClient
        client.setFollowRedirects(false);
        client.setFollowSslRedirects(false);
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(60, TimeUnit.SECONDS);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager); //finally set the cookie handler on client

        OkClient serviceClient = new OkClient(client);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(context.getString(R.string.perfios_url))
                .setConverter(new SimpleXMLConverter())
                .setClient(serviceClient)
                .build();


        perfoisService = restAdapter.create(PerfiosService.class);
        initPrivateKey();
        initPublicKey();
        DEFAULT = this;
    }

    public static PerfiosClient getDefault() {
        return DEFAULT;
    }

    public PerfiosService getPerfoisService() {
        return perfoisService;
    }

    private void initPrivateKey(){
        String privKeyPEM = BEGIN_RSA_PRIVATE_KEY.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
        privKeyPEM = privKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");

        byte [] privateEncoded = Base64.decode(privKeyPEM, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateEncoded);

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            privateKey = kf.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private void initPublicKey()
    {
        String publicKeyPEM = BEGIN_RSA_PUBLIC_KEY.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");

        byte [] publicEncoded = new byte[0];
        try {
            publicEncoded = Base64.decode(publicKeyPEM.getBytes("utf-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec publicKeyspec = new X509EncodedKeySpec(publicEncoded);

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
            publicKey = kf.generatePublic(publicKeyspec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private static final String BEGIN_RSA_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIBOgIBAAJBALG85+2ZhOVmEK9dmdzrBNLOZk7gsa4pccSnTBvDeSsRhmvoc4IU\n" +
            "OPiEgZ+itD+CatSwyS4t3WSIbSP9eKXDV+0CAwEAAQJATs+4975PE3lChMA4baE4\n" +
            "rlEfRHKV1uhBrjWQpe7zV4jcqbdRgi06aEjAi7PpJ9WCDw6v5wKfOWuLdJSR6LHS\n" +
            "gQIhANY+IBzzuHlnUgBK/YoHALJW2qUWhEsGL24+liJiZAVpAiEA1GFVybvueMwN\n" +
            "HGgXm8sSLXCLn3qfQ0KXZLswYT8BWeUCIQC9dcsoT0+/7OLTZ323ZmHzVGOewr7T\n" +
            "KfEf6TETzzbsoQIgVndScBGdY3zgEaKnifxBaFKb5dAMq1ufO5midtUp/akCIApe\n" +
            "R7wLegniAXoBuxLlTxC6oeKEyU1LkW04wL1ZXG05\n" +
            "-----END RSA PRIVATE KEY-----";

    private static final String BEGIN_RSA_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALG85+2ZhOVmEK9dmdzrBNLOZk7gsa4p\n" +
            "ccSnTBvDeSsRhmvoc4IUOPiEgZ+itD+CatSwyS4t3WSIbSP9eKXDV+0CAwEAAQ==\n" +
            "-----END PUBLIC KEY-----";
}
