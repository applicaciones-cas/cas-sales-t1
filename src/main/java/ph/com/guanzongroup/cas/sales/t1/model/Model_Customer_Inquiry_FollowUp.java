/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.model;

import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.CustomerInquiryFollowUpStatic;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

/**
 *
 * @author TEEJEI DE CELIS
 * @date July 25, 2026
 * @module Customer Inquiry Follow Up
 */
public class Model_Customer_Inquiry_FollowUp extends Model {
    
    //reference objects
    Model_Salesman poSalesMan;
    Model_Sales_Inquiry_Master poSalesInquiryMaster;
    Model_Sales_Inquiry_Detail poSalesInquiryDetail;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("dTransact", poGRider.getServerDate());
            poEntity.updateObject("sEntryByx", poGRider.getUserID());
            poEntity.updateObject("dEntryDte", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateString("cRecdStat", CustomerInquiryFollowUpStatic.FOLLOWED_UP);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sTransNox";

            //initialize reference objects
            SalesModels salesModels = new SalesModels(poGRider);
            poSalesMan = salesModels.Salesman();
            poSalesInquiryMaster = salesModels.SalesInquiryMaster();
            poSalesInquiryDetail = salesModels.SalesInquiryDetails();

            
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

    public JSONObject setReferenceNo(String referenceNo) {
        return setValue("sReferNox", referenceNo);
    }

    public String getReferenceNo() {
        return (String) getValue("sReferNox");
    }

    public JSONObject setTransactionDate(Date transactionDate) {
        return setValue("dTransact", transactionDate);
    }

    public Date getTransactionDate() {
        return (Date) getValue("dTransact");
    }

    public JSONObject setSourceCode(String sourceCode) {
        return setValue("sSourceCD", sourceCode);
    }

    public String getSourceCode() {
        return (String) getValue("sSourceCD");
    }

    public JSONObject setSourceNo(String sourceNo) {
        return setValue("sSourceNo", sourceNo);
    }

    public String getSourceNo() {
        return (String) getValue("sSourceNo");
    }

    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks);
    }

    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }

    public JSONObject setMessage(String message) {
        return setValue("sMessagex", message);
    }

    public String getMessage() {
        return (String) getValue("sMessagex");
    }

    public JSONObject setMethodCode(String methodCode) {
        return setValue("sMethodCd", methodCode);
    }

    public String getMethodCode() {
        return (String) getValue("sMethodCd");
    }

    public JSONObject setSocialMediaCode(String socialMediaCode) {
        return setValue("sSclMedia", socialMediaCode);
    }

    public String getSocialMediaCode() {
        return (String) getValue("sSclMedia");
    }

    public JSONObject setFollowUpDate(Date followUpDate) {
        return setValue("dFollowUp", followUpDate);
    }

    public Date getFollowUpDate() {
        Object loValue = getValue("dFollowUp");

        if (loValue == null) {
            return null;
        }

        if (loValue instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) loValue).getTime());
        }

        if (loValue instanceof Date) {
            return (Date) loValue;
        }

        if (loValue instanceof String) {
            String lsValue = ((String) loValue).trim();

            if (lsValue.isEmpty()) {
                return null;
            }

            try {
                // Handle values like "yyyy-MM-dd HH:mm:ss" by extracting the date part.
                String lsDateOnly = lsValue.length() >= 10 ? lsValue.substring(0, 10) : lsValue;
                return SQLUtil.toDate(lsDateOnly, SQLUtil.FORMAT_SHORT_DATE);
            } catch (Exception ex) {
                throw new ClassCastException("Invalid dFollowUp value: " + loValue);
            }
        }

        throw new ClassCastException("Unsupported dFollowUp type: " + loValue.getClass().getName());
    }

    public JSONObject setFollowUpTime(Time followUpTime) {
        return setValue("tFollowUp", followUpTime);
    }

    public Time getFollowUpTime() {
        Object loValue = getValue("tFollowUp");

        if (loValue == null) {
            return null;
        }

        if (loValue instanceof Time) {
            return (Time) loValue;
        }

        if (loValue instanceof java.sql.Timestamp) {
            return new Time(((java.sql.Timestamp) loValue).getTime());
        }

        if (loValue instanceof Date) {
            return new Time(((Date) loValue).getTime());
        }

        if (loValue instanceof String) {
            String lsValue = ((String) loValue).trim();

            if (lsValue.isEmpty()) {
                return null;
            }

            // Accept common formats like "HH:mm", "HH:mm:ss", or "yyyy-MM-dd HH:mm:ss".
            if (lsValue.contains(" ")) {
                lsValue = lsValue.substring(lsValue.lastIndexOf(' ') + 1);
            }
            if (lsValue.length() == 5) {
                lsValue = lsValue + ":00";
            }
            if (lsValue.length() > 8) {
                lsValue = lsValue.substring(0, 8);
            }

            try {
                return Time.valueOf(lsValue);
            } catch (IllegalArgumentException ex) {
                throw new ClassCastException("Invalid tFollowUp value: " + loValue);
            }
        }

        throw new ClassCastException("Unsupported tFollowUp type: " + loValue.getClass().getName());
    }

    public JSONObject setNextFollowUpDate(Date nextFollowUpDate) {
        return setValue("dNxtFolUp", nextFollowUpDate);
    }

    public Date getNextFollowUpDate() {
        return (Date) getValue("dNxtFolUp");
    }

    public JSONObject setCompetitorGoods(String competitorGoods) {
        return setValue("sGdsCmptr", competitorGoods);
    }

    public String getCompetitorGoods() {
        return (String) getValue("sGdsCmptr");
    }

    public JSONObject setCompetitorMake(String competitorMake) {
        return setValue("sMkeCmptr", competitorMake);
    }

    public String getCompetitorMake() {
        return (String) getValue("sMkeCmptr");
    }

    public JSONObject setCompetitorDealer(String competitorDealer) {
        return setValue("sDlrCmptr", competitorDealer);
    }

    public String getCompetitorDealer() {
        return (String) getValue("sDlrCmptr");
    }

    public JSONObject setResponseCode(String responseCode) {
        return setValue("sRspnseCd", responseCode);
    }

    public String getResponseCode() {
        return (String) getValue("sRspnseCd");
    }

    public JSONObject setClientId(String clientId) {
        return setValue("sClientID", clientId);
    }

    public String getClientId() {
        return (String) getValue("sClientID");
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

    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    public JSONObject setTimeStamp(Date timeStamp) {
        return setValue("dTimeStmp", timeStamp);
    }

    public Date getTimeStamp() {
        return (Date) getValue("dTimeStmp");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(
                this.getTable(),
                ID,
    true,
                poGRider.getGConnection().getConnection(),
                poGRider.getBranchCode());
    }

    //reference object models
    public Model_Salesman Salesman() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sClientID"))) {
            if (poSalesMan.getEditMode() == EditMode.READY
                    && poSalesMan.getEmployeeId().equals((String) getValue("sClientID"))) {
                return poSalesMan;
            } else {
                poJSON = poSalesMan.openRecord((String) getValue("sClientID"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poSalesMan;
                } else {
                    poSalesMan.initialize();
                    return poSalesMan;
                }
            }
        } else {
            poSalesMan.initialize();
            return poSalesMan;
        }
    }
    
    
    public Model_Sales_Inquiry_Master SalesInquiryMaster() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSourceNo"))) {
            if (poSalesInquiryMaster.getEditMode() == EditMode.READY
                    && poSalesInquiryMaster.getTransactionNo().equals((String) getValue("sSourceNo"))) {
                return poSalesInquiryMaster;
            } else {
                poJSON = poSalesInquiryMaster.openRecord((String) getValue("sSourceNo"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poSalesInquiryMaster;
                } else {
                    poSalesInquiryMaster.initialize();
                    return poSalesInquiryMaster;
                }
            }
        } else {
            poSalesInquiryMaster.initialize();
            return poSalesInquiryMaster;
        }
    }

    public Model_Sales_Inquiry_Detail SalesInquiryDetail() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sSourceNo"))) {
            if (poSalesInquiryDetail.getEditMode() == EditMode.READY
                    && poSalesInquiryDetail.getTransactionNo().equals((String) getValue("sSourceNo"))) {
                return poSalesInquiryDetail;
            } else {
                poJSON = poSalesInquiryDetail.openRecord((String) getValue("sSourceNo"), 1);

                if ("success".equals((String) poJSON.get("result"))) {
                    return poSalesInquiryDetail;
                } else {
                    poSalesInquiryDetail.initialize();
                    return poSalesInquiryDetail;
                }
            }
        } else {
            poSalesInquiryDetail.initialize();
            return poSalesInquiryDetail;
        }
    }
    //end - reference object models

}
