package com.mantralabsglobal.cashin.businessobjects;

import android.content.Context;
import android.content.res.AssetManager;

import com.mantralabsglobal.cashin.R;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hello on 8/15/2015.
 */
public class BankProvider {

    private static BankProvider instance;
    private BankList banks;

    public BankProvider(){
        instance = this;
    }

    public static BankProvider getInstance() {
        return instance;
    }

    public void init(Context context) {

        String xmlString = null;
        AssetManager am = context.getAssets();
        try {

            Serializer serializer = new Persister();

            banks = serializer.read(BankList.class, am.open(context.getResources().getString(R.string.bank_db)));
            if (banks != null) {
                banks.bankCodeList = new ArrayList<>();
                banks.bankNameList = new ArrayList<>();
                banks.bankCodeMap = new HashMap<>();
                banks.bankNameMap = new HashMap<>();
                for (Bank bank : banks.bankList) {
                    banks.bankCodeList.add(bank.getBankCode());
                    banks.bankNameList.add(bank.getName());
                    banks.bankCodeMap.put(bank.getBankCode(), bank);
                    banks.bankNameMap.put(bank.getName(), bank);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BankList getBanks() {
        return banks;
    }

    @Root(name="banks")
    public static class BankList
    {
        @ElementList(entry = "bank", inline=true)
        private List<Bank> bankList;

        //@ElementList
        //@Path("bank/bank_code")
        private List<String> bankCodeList;

        //@ElementList
        //@Path("bank/name")
        private List<String> bankNameList;

        private Map<String,Bank> bankCodeMap;
        private Map<String,Bank> bankNameMap;

        public Bank getByCode(String code)
        {
            return bankCodeMap.get(code);
        }
        public Bank getByName(String name)
        {
            return bankNameMap.get(name);
        }

        public List<Bank> getBankList() {
            return bankList;
        }

        public void setBankList(List<Bank> bankList) {
            this.bankList = bankList;
        }

        public List<String> getBankCodeList() {
            return bankCodeList;
        }

        public void setBankCodeList(List<String> bankCodeList) {
            this.bankCodeList = bankCodeList;
        }

        public List<String> getBankNameList() {
            return bankNameList;
        }

        public void setBankNameList(List<String> bankNameList) {
            this.bankNameList = bankNameList;
        }

        public Map<String, Bank> getBankCodeMap() {
            return bankCodeMap;
        }

        public void setBankCodeMap(Map<String, Bank> bankCodeMap) {
            this.bankCodeMap = bankCodeMap;
        }

        public Map<String, Bank> getBankNameMap() {
            return bankNameMap;
        }

        public void setBankNameMap(Map<String, Bank> bankNameMap) {
            this.bankNameMap = bankNameMap;
        }

        public Bank getByCodeOrName(String bankName) {
            Bank bank = getByCode(bankName);
            if(bank == null)
                bank = getByName(bankName);
            return bank;
        }
    }

    public static class Bank
    {
        @Element(required = false)
        private String name;



        @Element(required = false)
        private String logo;
        @Element(required = false)
        private String description;
        @Element(required = false)
        private String aliases;
        @Element(name="bank_code", required = false)
        private String bankCode;
        @Element(name = "normalized_name", required = false)
        private String normalizedName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAliases() {
            return aliases;
        }

        public void setAliases(String aliases) {
            this.aliases = aliases;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getNormalizedName() {
            return normalizedName;
        }

        public void setNormalizedName(String normalizedName) {
            this.normalizedName = normalizedName;
        }

        public String getLogo() {
            return logo;
        }
        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}
