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
public class Model_Sales_Inquiry_Requirements extends Model {
    
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
            poEntity.updateObject("dReceived", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("nEntryNox", 0);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sTransNox";
            ID2 = "nEntryNox";

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

    public JSONObject setRequirementCode(String requirementCode) {
        return setValue("sRqrmtCde", requirementCode);
    }

    public String getRequirementCode() {
        return (String) getValue("sRqrmtCde");
    }
    
    public JSONObject isRequired(boolean isRequired) {
        return setValue("cRequired", isRequired ? "1" : "0");
    }

    public boolean isRequired() {
        return ((String) getValue("cRequired")).equals("1");
    }
    
    public JSONObject isSubmitted(boolean isSubmitted) {
        return setValue("cSubmittd", isSubmitted ? "1" : "0");
    }

    public boolean isSubmitted() {
        return ((String) getValue("cSubmittd")).equals("1");
    }

    public JSONObject setReceivedBy(String receivedBy) {
        return setValue("sReceived", receivedBy);
    }

    public String getReceivedBy() {
        return (String) getValue("sReceived");
    }
    
    public JSONObject setReceivedDate(Date receivedDate) {
        return setValue("dReceived", receivedDate);
    }

    public Date getReceivedDate() {
        return (Date) getValue("dReceived");
    }
   
//    public JSONObject isProcessed(boolean isProcessed) {
//        return setValue("cProcessd", isProcessed ? "1" : "0");
//    }
//
//    public boolean isProcessed() {
//        return ((String) getValue("cProcessd")).equals("1");
//    }
//
//    public JSONObject setModifyingId(String modifiedBy) {
//        return setValue("sModified", modifiedBy);
//    }
//
//    public String getModifyingId() {
//        return (String) getValue("sModified");
//    }
//
//    public JSONObject setModifiedDate(Date modifiedDate) {
//        return setValue("dModified", modifiedDate);
//    }
//
//    public Date getModifiedDate() {
//        return (Date) getValue("dModified");
//    }

    
    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    //reference object models
    //end - reference object models

}
