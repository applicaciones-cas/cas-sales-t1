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
import org.guanzon.cas.parameter.model.Model_Banks;
import org.guanzon.cas.parameter.model.Model_Branch;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.model.Model_Industry;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class Model_Bank_Application extends Model {
    
    //reference objects
    Model_Branch poBranch;
    Model_Industry poIndustry;
    Model_Company poCompany;
    Model_Client_Master poClient;
    Model_Client_Address poClientAddress;
    Model_Client_Mobile poClientMobile;
    Model_Client_Master poAgent;
    Model_Salesman poSalesPerson;
    Model_Sales_Inquiry_Sources poSource;
    
    Model_Banks poBank;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("dEntryDte", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dModified", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dAppliedx", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dApproved", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dCancelld", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("nEntryNox", 0);
            poEntity.updateString("cTranStat", SalesInquiryStatic.OPEN);
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
            poBank = model.Banks();

            ClientModels clientModel = new ClientModels(poGRider);
            poClient = clientModel.ClientMaster();
            poAgent = clientModel.ClientMaster();
            poClientAddress = clientModel.ClientAddress();
            poClientMobile = clientModel.ClientMobile();
            
            SalesModels sales = new SalesModels(poGRider);
            poSalesPerson = sales.Salesman();
            poSource = sales.SalesInquirySources();
            
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

    public JSONObject setEntryNo(int entryNo) {
        return setValue("nEntryNox", entryNo);
    }

    public int getEntryNo() {
        if (getValue("nEntryNox") == null || "".equals(getValue("nEntryNox"))) {
            return 0;
        }
        return (int) getValue("nEntryNox");
    }

    public JSONObject setApplicationNo(String applicationNo) {
        return setValue("sApplicNo", applicationNo);
    }

    public String getApplicationNo() {
        return (String) getValue("sApplicNo");
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

    public JSONObject setBankId(String bankCode) {
        return setValue("sBankIDxx", bankCode);
    }

    public String getBankId() {
        return (String) getValue("sBankIDxx");
    }

    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks);
    }

    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }

    public JSONObject setTransactionStatus(String transactionStatus) {
        return setValue("cTranStat", transactionStatus);
    }

    public String getTransactionStatus() {
        return (String) getValue("cTranStat");
    }

    public JSONObject setCancelledBy(String cancelledBy) {
        return setValue("sCancelld", cancelledBy);
    }

    public String getCancelledBy() {
        return (String) getValue("sCancelld");
    }

    public JSONObject setCancelledDate(Date cancelledDate) {
        return setValue("dCancelld", cancelledDate);
    }

    public Date getCancelledDate() {
        return (Date) getValue("dCancelld");
    }
    
    public JSONObject setEntryBy(String modifiedBy) {
        return setValue("sEntryByx", modifiedBy);
    }

    public String getEntryBy() {
        return (String) getValue("sEntryByx");
    }

    public JSONObject setEntryDate(Date entryDate) {
        return setValue("dEntryDte", entryDate);
    }

    public Date getEntryDate() {
        return (Date) getValue("dEntryDte");
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
    
//    public JSONObject isProcessed(boolean isProcessed) {
//        return setValue("cProcessd", isProcessed ? "1" : "0");
//    }
//
//    public boolean isProcessed() {
//        return ((String) getValue("cProcessd")).equals("1");
//    }

    
    @Override
    public String getNextCode() {
        return ""; //MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    //reference object models
    public Model_Banks Bank() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sBankCode"))) {
            if (poBank.getEditMode() == EditMode.READY
                    && poBank.getBankCode().equals((String) getValue("sBankCode"))) {
                return poBank;
            } else {
                poJSON = poBank.openRecord((String) getValue("sBankCode"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poBank;
                } else {
                    poBranch.initialize();
                    return poBank;
                }
            }
        } else {
            poBank.initialize();
            return poBank;
        }
    }
    
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
        if (!"".equals((String) getValue("sClientID"))) {
            if (poClientAddress.getEditMode() == EditMode.READY
                    && poClientAddress.getClientId().equals((String) getValue("sClientID"))) {
                return poClientAddress;
            } else {
                poJSON = poClientAddress.openRecord((String) getValue("sClientID")); //sAddrssID

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

    public Model_Salesman SalesPerson() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSalesman"))) {
            if (poSalesPerson.getEditMode() == EditMode.READY
                    && poSalesPerson.getEmployeeId().equals((String) getValue("sSalesman"))) {
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
    
    public Model_Sales_Inquiry_Sources Source() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSourceCd"))) {
            if (poSource.getEditMode() == EditMode.READY
                    && poSource.getSourceId().equals((String) getValue("sSourceCd"))) {
                return poSource;
            } else {
                poJSON = poSource.openRecord((String) getValue("sSourceCd"));

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
