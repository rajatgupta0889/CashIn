package com.mantralabsglobal.cashin.service;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import retrofit.Callback;
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

    @POST("/txnstatus")
    void transactionStatus(TransactionStatusPayload payload, String signature, Callback<String> callback);

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
        @Attribute(name = "txnId")
        private String transactionId;
        @Attribute(name="parts")
        private String partCount;
        private String processing;
        private String files;
        @ElementList(type = TransactionStatusResponsePart.class, name = "Part")
        private List<TransactionStatusResponsePart> parts;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getPartCount() {
            return partCount;
        }

        public void setPartCount(String partCount) {
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

    @Root(name = "Part")
    class TransactionStatusResponsePart{
        @Attribute(name = "perfTxnId")
        private String perfiosTransactionId;
        private String status;

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
    }
}
