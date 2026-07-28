/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.model.Model_Client_Master;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.parameter.model.Model_Banks;
import org.guanzon.cas.parameter.model.Model_Branch;
import org.guanzon.cas.parameter.model.Model_Category;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.model.Model_Industry;
import org.guanzon.cas.parameter.model.Model_Term;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;

/**
 *
 * @author Arsiela
 */
public class Model_Sales_Commitment_Master extends Model {
    
    String psClientType = "";
    String psIndustry = "";
    String psCompany = "";
    String psCategory = "";
    
    //reference objects
    Model_Branch poBranch;
    Model_Industry poIndustry;
    Model_Company poCompany;
    Model_Category poCategory;
    Model_Term poTerm;
    Model_Client_Master poClient;
    Model_Sales_Inquiry_Master poSalesInquiry;
    Model_Banks poBank;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            poEntity.updateObject("dTransact", poGRider.getServerDate());
            poEntity.updateNull("dModified");
            poEntity.updateNull("dAppliedx");
            poEntity.updateNull("dApproved");
            poEntity.updateNull("dDueDatex");
            
            poEntity.updateString("cIssuerxx", "0");
            poEntity.updateString("cPayModex", "0");
            poEntity.updateString("cTranStat", "0");
            poEntity.updateObject("nEntryNox", 0);
            poEntity.updateString("cTranStat", BankApplicationStatus.OPEN);
            
            poEntity.updateObject("nSalesAmt", 0.0000);
            poEntity.updateObject("nTranTotl", 0.0000);
            poEntity.updateObject("nTWithHld", 0.0000);
            poEntity.updateObject("nVATAmtxx", 0.0000);
            poEntity.updateObject("nVATExmpt", 0.0000);
            poEntity.updateObject("nVATRatex", 0.0000);
            poEntity.updateObject("nVATSales", 0.0000);
            poEntity.updateObject("nWTaxRate", 0.0000);
            
            psClientType = "0";
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sTransNox";

            //initialize reference objects
            ParamModels model = new ParamModels(poGRider);
            poBranch = model.Branch();
            poIndustry = model.Industry();
            poCompany = model.Company();
            poCategory = model.Category();
            poTerm = model.Term();
            poBank = model.Banks();

            ClientModels clientModel = new ClientModels(poGRider);
            poClient = clientModel.ClientMaster();
            
            SalesModels salesModel = new SalesModels(poGRider);
            poSalesInquiry = salesModel.SalesInquiryMaster();
//            end - initialize reference objects

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setTransactionNo(String transactionNo) {
        return setValue("sTransNox", transactionNo);
    }

    public String getTransactionNo() {
        return (String) getValue("sTransNox");
    }
    
    public JSONObject setTransactionDate(Date transactionDate) {
        return setValue("dTransact", transactionDate);
    }

    public Date getTransactionDate() {
        return (Date) getValue("dTransact");
    }
    
    
    public JSONObject setAppliedDate(Date appliedDate) {
        return setValue("dAppliedx", appliedDate);
    }

    public Date getAppliedDate() {
        return (Date) getValue("dAppliedx");
    }
    
    public JSONObject setApprovedDate(Date approvedDate) {
        return setValue("dApproved", approvedDate);
    }

    public Date getApprovedDate() {
        return (Date) getValue("dApproved");
    }
    
    public JSONObject setDueDate(Date dueDate) {
        return setValue("dDueDatex", dueDate);
    }

    public Date getDueDate() {
        return (Date) getValue("dDueDatex");
    }

    public JSONObject setPaymentMode(String paymentMode) {
        return setValue("cPayModex", paymentMode);
    }

    public String getPaymentMode() {
        return (String) getValue("cPayModex");
    }

    public JSONObject setSourceCode(String sourceCode) {
        return setValue("sSourceCd", sourceCode);
    }

    public String getSourceCode() {
        return (String) getValue("sSourceCd");
    }

    public JSONObject setSourceNo(String sourceNo) {
        return setValue("sSourceNo", sourceNo);
    }

    public String getSourceNo() {
        return (String) getValue("sSourceNo");
    }
    
    public JSONObject setClientId(String clientId) {
        return setValue("sClientID", clientId);
    }

    public String getClientId() {
        return (String) getValue("sClientID");
    }

    public JSONObject setAddressId(String addressId) {
        return setValue("sAddrssID", addressId);
    }

    public String getAddressId() {
        return (String) getValue("sAddrssID");
    }

    public JSONObject setContactId(String contactId) {
        return setValue("sContctID", contactId);
    }

    public String getContactId() {
        return (String) getValue("sContctID");
    }
    
    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks);
    }

    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }

    public JSONObject setIssuer(String issuer) {
        return setValue("cIssuerxx", issuer);
    }

    public String getIssuer() {
        return (String) getValue("cIssuerxx");
    }

    public JSONObject setBank(String bankId) {
        return setValue("sIssuerID", bankId);
    }

    public String getBank() {
        return (String) getValue("sIssuerID");
    }

    public JSONObject setATDNumber(String ATDNumber) {
        return setValue("sATDNumbr", ATDNumber);
    }

    public String getATDNumber() {
        return (String) getValue("sATDNumbr");
    }

    public JSONObject setPONumber(String PONumber) {
        return setValue("sPONumber", PONumber);
    }

    public String getPONumber() {
        return (String) getValue("sPONumber");
    }

    public JSONObject setTermCode(String termCode) {
        return setValue("sTermCode", termCode);
    }

    public String getTermCode() {
        return (String) getValue("sTermCode");
    }

    public JSONObject setEntryNo(int entryNo) {
        return setValue("nEntryNox", entryNo);
    }

    public int getEntryNo() {
        if (getValue("nEntryNox") == null || "".equals(getValue("nEntryNox"))) {
            return 0;
        }
        return (int) getValue("nEntryNox");
    }

    public JSONObject setTransactionStatus(String transactionStatus) {
        return setValue("cTranStat", transactionStatus);
    }

    public String getTransactionStatus() {
        return (String) getValue("cTranStat");
    }
    
    public JSONObject setModifyingId(String modifiedBy) {
        return setValue("sModified", modifiedBy);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }
    
    public JSONObject setTransactionTotal(double transactionTotal) {
        return setValue("nTranTotl", transactionTotal);
    }

    public double getTransactionTotal() {
        return Double.parseDouble(String.valueOf(getValue("nTranTotl")));
    }

    public JSONObject setSalesAmount(double salesAmount) {
        return setValue("nSalesAmt", salesAmount);
    }

    public double getSalesAmount() {
        return Double.parseDouble(String.valueOf(getValue("nSalesAmt")));
    }

    public JSONObject setWithholdingTax(double withTaxTotal) {
        return setValue("nTWithHld", withTaxTotal);
    }

    public double getWithholdingTax(){
        return Double.parseDouble(String.valueOf(getValue("nTWithHld")));
    }

    public JSONObject setVATSale(double VATSale) {
        return setValue("nVATSales", VATSale);
    }

    public double getVATSale() {
        return Double.parseDouble(String.valueOf(getValue("nVATSales")));
    }
    
    public JSONObject setVATAmount(double vatAmount) {
        return setValue("nVATAmtxx", vatAmount);
    }

    public double getVATAmount() {
        return Double.parseDouble(String.valueOf(getValue("nVATAmtxx")));
    }

    public JSONObject setVATExmpt(double vatExmpt) {
        return setValue("nVatExmpt", vatExmpt);
    }

    public double getVATExmpt() {
        return Double.parseDouble(String.valueOf(getValue("nVatExmpt")));
    }

    public JSONObject setVATRates(double vatRates) {
        return setValue("nVATRatex", vatRates);
    }

    public double getVATRates() {
        return Double.parseDouble(String.valueOf(getValue("nVATRatex")));
    }

    public JSONObject setWTaxRate(double wtaxRate) {
        return setValue("nWTaxRate", wtaxRate);
    }

    public double getWTaxRate() {
        return Double.parseDouble(String.valueOf(getValue("nWTaxRate")));
    }
    
    public void setClientType(String clientType){
        psClientType = clientType;
    }
    
    public String getClientType(){
        return psClientType;
    }

    public void setIndustryId(String industryId) {
        psIndustry = industryId;
    }

    public String getIndustryId() {
        return psIndustry;
    }
    
    public void setCompanyId(String companyId) {
        psCompany = companyId;
    }

    public String getCompanyId() {
        return psCompany;
    }

    public void setCategoryCode(String categoryCode) {
        psCategory = categoryCode;
    }

    public String getCategoryCode() {
        return psCategory;
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    //reference object models
    public Model_Industry Industry() throws SQLException, GuanzonException {
        if (!"".equals(psIndustry)) {
            if (poIndustry.getEditMode() == EditMode.READY
                    && poIndustry.getIndustryId().equals(psIndustry)) {
                return poIndustry;
            } else {
                poJSON = poIndustry.openRecord(psIndustry);

                if ("success".equals((String) poJSON.get("result"))) {
                    return poIndustry;
                } else {
                    poIndustry.initialize();
                    return poIndustry;
                }
            }
        } else {
            poIndustry.initialize();
            return poIndustry;
        }
    }

    public Model_Company Company() throws SQLException, GuanzonException {
        if (!"".equals(psCompany)) {
            if (poCompany.getEditMode() == EditMode.READY
                    && poCompany.getCompanyId().equals(psCompany)) {
                return poCompany;
            } else {
                poJSON = poCompany.openRecord(psCompany);

                if ("success".equals((String) poJSON.get("result"))) {
                    return poCompany;
                } else {
                    poCompany.initialize();
                    return poCompany;
                }
            }
        } else {
            poCompany.initialize();
            return poCompany;
        }
    }

    public Model_Category Category() throws SQLException, GuanzonException {
        if (!"".equals(psCategory)) {
            if (poCategory.getEditMode() == EditMode.READY
                    && poCategory.getCategoryId().equals(psCategory)) {
                return poCategory;
            } else {
                poJSON = poCategory.openRecord(psCategory);

                if ("success".equals((String) poJSON.get("result"))) {
                    return poCategory;
                } else {
                    poCategory.initialize();
                    return poCategory;
                }
            }
        } else {
            poCategory.initialize();
            return poCategory;
        }
    }

    public Model_Term Term() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sTermCode"))) {
            if (poTerm.getEditMode() == EditMode.READY
                    && poTerm.getTermId().equals((String) getValue("sTermCode"))) {
                return poTerm;
            } else {
                poJSON = poTerm.openRecord((String) getValue("sTermCode"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poTerm;
                } else {
                    poTerm.initialize();
                    return poTerm;
                }
            }
        } else {
            poTerm.initialize();
            return poTerm;
        }
    }

    public Model_Client_Master Client() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sClientID"))) {
            if (poClient.getEditMode() == EditMode.READY
                    && poClient.getClientId().equals((String) getValue("sClientID"))) {
                return poClient;
            } else {
                poJSON = poClient.openRecord((String) getValue("sClientID"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poClient;
                } else {
                    poClient.initialize();
                    return poClient;
                }
            }
        } else {
            poClient.initialize();
            return poClient;
        }
    }

    public Model_Sales_Inquiry_Master Inquiry() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSourceNo"))) {
            if (poSalesInquiry.getEditMode() == EditMode.READY
                    && poSalesInquiry.getTransactionNo().equals((String) getValue("sSourceNo"))) {
                return poSalesInquiry;
            } else {
                poJSON = poSalesInquiry.openRecord((String) getValue("sSourceNo"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poSalesInquiry;
                } else {
                    poSalesInquiry.initialize();
                    return poSalesInquiry;
                }
            }
        } else {
            poSalesInquiry.initialize();
            return poSalesInquiry;
        }
    }
    
    public Model_Banks Bank() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sBankIDxx"))) {
            if (poBank.getEditMode() == EditMode.READY
                    && poBank.getBankCode().equals((String) getValue("sBankIDxx"))) {
                return poBank;
            } else {
                poJSON = poBank.openRecord((String) getValue("sBankIDxx"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poBank;
                } else {
                    poBank.initialize();
                    return poBank;
                }
            }
        } else {
            poBank.initialize();
            return poBank;
        }
    }
    //end - reference object models

}
