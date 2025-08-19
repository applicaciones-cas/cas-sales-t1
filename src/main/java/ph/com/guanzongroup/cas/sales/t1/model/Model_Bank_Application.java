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
import org.guanzon.cas.parameter.model.Model_Banks;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class Model_Bank_Application extends Model {
    
    //reference objects
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
            poEntity.updateString("cTranStat", BankApplicationStatus.OPEN);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sTransNox";
            ID2 = "nEntryNox";

            //initialize reference objects
            ParamModels model = new ParamModels(poGRider);
            poBank = model.Banks();
            
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
    
    public JSONObject setEntryBy(String entryBy) {
        return setValue("sEntryByx", entryBy);
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
