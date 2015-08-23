package com.mantralabsglobal.cashin.service;

import android.content.Context;
import android.os.AsyncTask;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.utils.PerfiosUtils;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by hello on 8/15/2015.
 */
public interface PerfiosService {

    @FormUrlEncoded
    @POST("/start")
    void startProcess( @Field("payload") String payload, @Field("signature") String signature, Callback<String> callback);

    @FormUrlEncoded
    @POST("/txnstatus")
    void getTransactionStatus(@Field("payload") String payload, @Field("signature") String signature, Callback<TransactionStatusResponse> callback);

    @FormUrlEncoded
    @POST("/txnstatus")
    TransactionStatusResponse getTransactionStatus(@Field("payload") String payload, @Field("signature") String signature);

    @FormUrlEncoded
    @POST("/retrieve")
    String retrieveReports(@Field("payload") String payload, @Field("signature") String signature);

    @FormUrlEncoded
    @POST("/institutions")
    void getInstitutions(@Field("payload") String payload, @Field("signature") String signature, Callback<String> callback);


    class PerfiosStatusUploadTask extends AsyncTask<String, String, PrimaryBankService.PerfiosTransactionResponse>{

        protected Exception exception;
        private final Context context;

        public PerfiosStatusUploadTask(Context context){
            this.context = context;
        }

        @Override
        protected PrimaryBankService.PerfiosTransactionResponse doInBackground(String... params) {
            String transactionId = params[0];
            PrimaryBankService.PerfiosTransactionResponse perfiosTransactionResponse = null;
            PerfiosService.TransactionStatusPayload payloadObj = new PerfiosService.TransactionStatusPayload(context.getString(R.string.perfios_api_version)
                                                                                    , context.getString(R.string.perfios_vendorId), transactionId );
            publishProgress("Generating payload");
            String payload = PerfiosUtils.serialize(payloadObj);
            PrimaryBankService primaryBankService = RestClient.getInstance().getPrimaryBankService();
            String signature = null;
            try {
                publishProgress("Signing payload");
                signature = PerfiosUtils.getPayloadSignature(payloadObj, PerfiosClient.getDefault().getPrivateKey() );
            } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | NoSuchProviderException | SignatureException | NoSuchPaddingException | UnsupportedEncodingException e) {
                e.printStackTrace();
                exception = e;
            }
            if(signature != null) {
                publishProgress("Fetching transaction status from Perfios");
                TransactionStatusResponse transactionStatusResponse = PerfiosClient.getDefault().getPerfoisService().getTransactionStatus(payload, signature);
                publishProgress("Uploading status to server");
                perfiosTransactionResponse = primaryBankService.uploadPerfiosTransactionStatus(transactionStatusResponse);
                publishProgress("Done!!");

            }
            return perfiosTransactionResponse;
        }

    }

    @Root(name = "payload")
    class InstitutionsPayload{
        @Element
        private String apiVersion;
        @Element
        private String vendorId;
        @Element
        private String destination;

        public String getApiVersion() {
            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }

        public String getVendorId() {
            return vendorId;
        }

        public void setVendorId(String vendorId) {
            this.vendorId = vendorId;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }
    }

    @Root(name = "payload")
    class StartProcessPayload{

        @Element
        private String apiVersion;
        @Element
        private String vendorId;
        @Element(name = "txnId")
        private String transactionId;
        @Element
        private String emailId;
        @Element
        private String destination;
        @Element
        private int loanAmount;
        @Element
        private int loanDuration;
        @Element
        private String loanType;
        @Element(required = false)
        private String institutionId;
        @Element(required = false)
        private String returnUrl;

        public String getApiVersion() {
            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }

        public String getVendorId() {
            return vendorId;
        }

        public void setVendorId(String vendorId) {
            this.vendorId = vendorId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public int getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(int loanAmount) {
            this.loanAmount = loanAmount;
        }

        public int getLoanDuration() {
            return loanDuration;
        }

        public void setLoanDuration(int loanDuration) {
            this.loanDuration = loanDuration;
        }

        public String getLoanType() {
            return loanType;
        }

        public void setLoanType(String loanType) {
            this.loanType = loanType;
        }

        public String getInstitutionId() {
            return institutionId;
        }

        public void setInstitutionId(String institutionId) {
            this.institutionId = institutionId;
        }

        public String getReturnUrl() {
            return returnUrl;
        }

        public void setReturnUrl(String returnUrl) {
            this.returnUrl = returnUrl;
        }
    }

    @Root(name = "payload")
    class TransactionStatusPayload{
        @Element
        private String apiVersion;
        @Element
        private String vendorId;
        @Element(name = "txnId")
        private String transactionId;

        public TransactionStatusPayload()
        {

        }

        public TransactionStatusPayload(String apiVersion, String vendorId, String transactionId){
            this.apiVersion = apiVersion;
            this.vendorId = vendorId;
            this.transactionId = transactionId;
        }

        public String getApiVersion() {
            return apiVersion;
        }

        public void setApiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
        }

        public String getVendorId() {
            return vendorId;
        }

        public void setVendorId(String vendorId) {
            this.vendorId = vendorId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }

    @Root(name = "Status")
    class TransactionStatusResponse{
        @Attribute(name = "txnId", required = false)
        private String transactionId;
        @Attribute(name="parts", required = false)
        private int partCount;
        @Attribute(name="processing", required = false)
        private String processing;
        @Attribute(name="files", required = false)
        private String files;
        @ElementList(type = TransactionStatusResponsePart.class, entry = "Part", required = false, inline = true)
        private List<TransactionStatusResponsePart> parts;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public int getPartCount() {
            return partCount;
        }

        public void setPartCount(int partCount) {
            this.partCount = partCount;
        }

        public String getProcessing() {
            return processing;
        }

        public void setProcessing(String processing) {
            this.processing = processing;
        }

        public String getFiles() {
            return files;
        }

        public void setFiles(String files) {
            this.files = files;
        }

        public List<TransactionStatusResponsePart> getParts() {
            return parts;
        }

        public void setParts(List<TransactionStatusResponsePart> parts) {
            this.parts = parts;
        }
    }

    @Element(name = "Part")
    class TransactionStatusResponsePart{
        @Attribute(name = "perfiosTransactionId")
        private String perfiosTransactionId;
        @Attribute(required = false)
        private String status;
        @Attribute(required = false)
        private String reason;

        public String getPerfiosTransactionId() {
            return perfiosTransactionId;
        }

        public void setPerfiosTransactionId(String perfiosTransactionId) {
            this.perfiosTransactionId = perfiosTransactionId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
