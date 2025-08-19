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
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class Model_Sales_Inquiry_Requirements extends Model {
    
    //reference objects
    
    Model_Requirement_Source_PerGroup poRequirementsPerGroup;
    Model_Requirement_Source poRequirementsSource;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("dModified", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("dReceived", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("nEntryNox", 0);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sTransNox";
            ID2 = "nEntryNox";

            //initialize reference objects
            SalesModels sales = new SalesModels(poGRider);
            poRequirementsPerGroup = sales.RequirementSourcePerGroup();
            poRequirementsSource = sales.RequirementSource();
            
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

    @Override
    public String getNextCode() {
        return  ""; //MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    //reference object models
    public Model_Requirement_Source_PerGroup RequirementSourcePerGroup(String paymentMode) throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sRqrmtCde"))) {
            if (poRequirementsPerGroup.getEditMode() == EditMode.READY
                    && poRequirementsPerGroup.getRequirementCode().equals((String) getValue("sRqrmtCde"))) {
                return poRequirementsPerGroup;
            } else {
                poJSON = poRequirementsPerGroup.openRecord((String) getValue("sRqrmtCde"), paymentMode);

                if ("success".equals((String) poJSON.get("result"))) {
                    return poRequirementsPerGroup;
                } else {
                    poRequirementsPerGroup.initialize();
                    return poRequirementsPerGroup;
                }
            }
        } else {
            poRequirementsPerGroup.initialize();
            return poRequirementsPerGroup;
        }
    }
    
    
    public Model_Requirement_Source RequirementSource() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sRqrmtCde"))) {
            if (poRequirementsSource.getEditMode() == EditMode.READY
                    && poRequirementsSource.getRequirementCode().equals((String) getValue("sRqrmtCde"))) {
                return poRequirementsSource;
            } else {
                poJSON = poRequirementsSource.openRecord((String) getValue("sRqrmtCde"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poRequirementsSource;
                } else {
                    poRequirementsSource.initialize();
                    return poRequirementsSource;
                }
            }
        } else {
            poRequirementsSource.initialize();
            return poRequirementsSource;
        }
    }
    //end - reference object models
    
//    public JSONObject deleteRecord() throws SQLException, GuanzonException{
//        poJSON = new JSONObject();
//        
//        String lsSQL = " DELETE FROM "+getTable()+" WHERE "
//                    + " sTransNox = " + SQLUtil.toSQL(getTransactionNo())
//                    + " AND sRqrmtCde = " + SQLUtil.toSQL(getRequirementCode());
//        if (!lsSQL.isEmpty()) {
//            if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "", "") <= 0L) {
//                poJSON.put("result", "success");
//                poJSON.put("message", "Record deleted successfully.");
//            } else {
//                poJSON.put("result", "error");
//                poJSON.put("continue", true);
//                poJSON.put("message", poGRider.getMessage());
//            }
//        }
//        return poJSON;
//    }
    
}
