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
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Master;
import org.guanzon.cas.client.model.Model_Client_Mobile;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.parameter.model.Model_Branch;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.model.Model_Industry;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class Model_Sales_Inquiry_Master extends Model {
    
    String psClientType = "";
    
    //reference objects
    Model_Branch poBranch;
    Model_Industry poIndustry;
    Model_Company poCompany;
    Model_Client_Master poClient;
    Model_Client_Address poClientAddress;
    Model_Client_Mobile poClientMobile;
    Model_Client_Master poAgent;
    Model_Client_Master poSalesPerson;
    Model_Client_Master poSource;


    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("dTransact", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dModified", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dTargetxx", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dFollowUp", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateString("cProcessd", "0");
            poEntity.updateObject("nEntryNox", 0);
            poEntity.updateString("cInqrStat", SalesInquiryStatic.OPEN);
            poEntity.updateString("cTranStat", SalesInquiryStatic.OPEN);
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

            ClientModels clientModel = new ClientModels(poGRider);
            poClient = clientModel.ClientMaster();
            poAgent = clientModel.ClientMaster();
            poSalesPerson = clientModel.ClientMaster();
            poClientAddress = clientModel.ClientAddress();
            poClientMobile = clientModel.ClientMobile();
            poSource = clientModel.ClientMaster();
            
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

    public JSONObject setIndustryId(String industryId) {
        return setValue("sIndstCdx", industryId);
    }

    public String getIndustryId() {
        return (String) getValue("sIndstCdx");
    }

    public JSONObject setBranchCode(String branchCode) {
        return setValue("sBranchCd", branchCode);
    }

    public String getBranchCode() {
        return (String) getValue("sBranchCd");
    }
    
    public JSONObject setTransactionDate(Date transactionDate) {
        return setValue("dTransact", transactionDate);
    }

    public Date getTransactionDate() {
        return (Date) getValue("dTransact");
    }

    public JSONObject setCompanyId(String companyId) {
        return setValue("sCompnyID", companyId);
    }

    public String getCompanyId() {
        return (String) getValue("sCompnyID");
    }

    public JSONObject setCategoryCode(String categoryCode) {
        return setValue("sCategrCd", categoryCode);
    }

    public String getCategoryCode() {
        return (String) getValue("sCategrCd");
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
    
    public JSONObject setTargetDate(Date targetDate) {
        return setValue("dTargetxx", targetDate);
    }

    public Date getTargetDate() {
        return (Date) getValue("dTargetxx");
    }
    
    public JSONObject setFollowUpDate(Date followUpDate) {
        return setValue("dFollowUp", followUpDate);
    }

    public Date getFollowUpDate() {
        return (Date) getValue("dFollowUp");
    }

    public JSONObject setPurchaseType(String purchaseType) {
        return setValue("cPurcType", purchaseType);
    }

    public String getPurchaseType() {
        return (String) getValue("cPurcType");
    }

    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks);
    }

    public String getRemarks() {
        return (String) getValue("sRemarksx");
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

    public JSONObject setSalesMan(String salesMan) {
        return setValue("sSalesman", salesMan);
    }

    public String getSalesMan() {
        return (String) getValue("sSalesman");
    }

    public JSONObject setAgentId(String agentId) {
        return setValue("sAgentIDx", agentId);
    }

    public String getAgentId() {
        return (String) getValue("sAgentIDx");
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

    public JSONObject setInquiryStatus(String inquiryStatus) {
        return setValue("cInqrStat", inquiryStatus);
    }

    public String getInquiryStatus() {
        return (String) getValue("cInqrStat");
    }

    public JSONObject setCategoryType(String categoryType) {
        return setValue("cConditnx", categoryType);
    }

    public String getCategoryType() {
        return (String) getValue("cConditnx");
    }

    public JSONObject setTransactionStatus(String transactionStatus) {
        return setValue("cTranStat", transactionStatus);
    }

    public String getTransactionStatus() {
        return (String) getValue("cTranStat");
    }
    
    public JSONObject isProcessed(boolean isProcessed) {
        return setValue("cProcessd", isProcessed ? "1" : "0");
    }

    public boolean isProcessed() {
        return ((String) getValue("cProcessd")).equals("1");
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
    
    public void setClientType(String clientType){
        psClientType = clientType;
    }
    
    public String getClientType(){
        return psClientType;
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    //reference object models
    public Model_Branch Branch() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sBranchCd"))) {
            if (poBranch.getEditMode() == EditMode.READY
                    && poBranch.getBranchCode().equals((String) getValue("sBranchCd"))) {
                return poBranch;
            } else {
                poJSON = poBranch.openRecord((String) getValue("sBranchCd"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poBranch;
                } else {
                    poBranch.initialize();
                    return poBranch;
                }
            }
        } else {
            poBranch.initialize();
            return poBranch;
        }
    }

    public Model_Industry Industry() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sIndstCdx"))) {
            if (poIndustry.getEditMode() == EditMode.READY
                    && poIndustry.getIndustryId().equals((String) getValue("sIndstCdx"))) {
                return poIndustry;
            } else {
                poJSON = poIndustry.openRecord((String) getValue("sIndstCdx"));

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
        if (!"".equals((String) getValue("sCompnyID"))) {
            if (poCompany.getEditMode() == EditMode.READY
                    && poCompany.getCompanyId().equals((String) getValue("sCompnyID"))) {
                return poCompany;
            } else {
                poJSON = poCompany.openRecord((String) getValue("sCompnyID"));

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
    
    public Model_Client_Address ClientAddress() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sAddrssID"))) {
            if (poClientAddress.getEditMode() == EditMode.READY
                    && poClientAddress.getClientId().equals((String) getValue("sAddrssID"))) {
                return poClientAddress;
            } else {
                poJSON = poClientAddress.openRecord((String) getValue("sAddrssID"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poClientAddress;
                } else {
                    poClientAddress.initialize();
                    return poClientAddress;
                }
            }
        } else {
            poClientAddress.initialize();
            return poClientAddress;
        }
    }
    
    public Model_Client_Mobile ClientMobile() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sContctID"))) {
            if (poClientMobile.getEditMode() == EditMode.READY
                    && poClientMobile.getClientId().equals((String) getValue("sContctID"))) {
                return poClientMobile;
            } else {
                poJSON = poClientMobile.openRecord((String) getValue("sContctID"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poClientMobile;
                } else {
                    poClientMobile.initialize();
                    return poClientMobile;
                }
            }
        } else {
            poClientMobile.initialize();
            return poClientMobile;
        }
    }

    public Model_Client_Master SalesPerson() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSalesman"))) {
            if (poSalesPerson.getEditMode() == EditMode.READY
                    && poSalesPerson.getClientId().equals((String) getValue("sSalesman"))) {
                return poSalesPerson;
            } else {
                poJSON = poSalesPerson.openRecord((String) getValue("sSalesman"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poSalesPerson;
                } else {
                    poSalesPerson.initialize();
                    return poSalesPerson;
                }
            }
        } else {
            poSalesPerson.initialize();
            return poSalesPerson;
        }
    }

    public Model_Client_Master ReferralAgent() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sAgentIDx"))) {
            if (poAgent.getEditMode() == EditMode.READY
                    && poAgent.getClientId().equals((String) getValue("sAgentIDx"))) {
                return poAgent;
            } else {
                poJSON = poAgent.openRecord((String) getValue("sAgentIDx"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poAgent;
                } else {
                    poAgent.initialize();
                    return poAgent;
                }
            }
        } else {
            poAgent.initialize();
            return poAgent;
        }
    }
    
    public Model_Client_Master Source() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSourceNo"))) {
            if (poSource.getEditMode() == EditMode.READY
                    && poSource.getClientId().equals((String) getValue("sSourceNo"))) {
                return poSource;
            } else {
                poJSON = poSource.openRecord((String) getValue("sSourceNo"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poSource;
                } else {
                    poSource.initialize();
                    return poSource;
                }
            }
        } else {
            poSource.initialize();
            return poSource;
        }
    }
    //end - reference object models

}
