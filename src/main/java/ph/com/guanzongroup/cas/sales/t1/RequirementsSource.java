/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Requirement_Source;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Requirement_Source_PerGroup;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class RequirementsSource extends Parameter {
    Model_Requirement_Source poModel;
  
     @Override
    public void initialize() throws SQLException, GuanzonException {
      psRecdStat = RecordStatus.ACTIVE;
      poModel = new SalesModels(poGRider).RequirementSource();
      
      super.initialize();
    }

    @Override
    public Model_Requirement_Source getModel() {
        return poModel;
    }
    
    private Model_Requirement_Source SalesInquirySources() {
        return new SalesModels(poGRider).RequirementSource();
    }
    
    public JSONObject DeactivateRecord()
            throws SQLException,
            GuanzonException,
            CloneNotSupportedException {
        
        poJSON = checkExistingSourcePerGroup();
        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }
        
        poJSON = deactivateRecord();
        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }
        poJSON.put("result", "success");
        poJSON.put("message", "Record deactivated successfully.");
        return poJSON;
    }
    
    public JSONObject ActivateRecord()
            throws SQLException,
            GuanzonException,
            CloneNotSupportedException {
        
        poJSON = activateRecord();
        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }
        poJSON.put("result", "success");
        poJSON.put("message", "Record activated successfully.");
        return poJSON;
    }
  
    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException {
      poJSON = new JSONObject();
        if (poModel.getRequirementCode().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Requirement code must not be empty.");
            return poJSON;
        }
        if (poModel.getDescription().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Description cannot be empty.");
            return poJSON;
        } 
        
        if(getEditMode() == EditMode.ADDNEW){
            poJSON = checkExistingSource();
            if("error".equals((String) poJSON.get("result"))){
                return poJSON;
            }
        }
        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject checkExistingSource() throws SQLException{
        poJSON = new JSONObject();
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), " upper(sDescript) = " +  SQLUtil.toSQL(poModel.getDescription().toUpperCase()));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) >= 0) {
            while (loRS.next()) {
                // Print the result set
                System.out.println("sRqrmtCde: " + loRS.getString("sRqrmtCde"));
                System.out.println("sDescript: " + loRS.getString("sDescript"));
                System.out.println("------------------------------------------------------------------------------");

                poJSON.put("result", "error");
                poJSON.put("message", poModel.getDescription() + " already exists.");
            }
        } else {
            poJSON.put("result", "success");
            poJSON.put("continue", true);
            poJSON.put("message", "No record found.");
        }
        MiscUtil.close(loRS);
        
        return poJSON;
    
    }
    
    private JSONObject checkExistingSourcePerGroup() throws SQLException{
        poJSON = new JSONObject();
        boolean lbIsError = false;
        String lsReqID = "";
        Model_Requirement_Source_PerGroup loObject = new SalesModels(poGRider).RequirementSourcePerGroup();
        String lsSQL = MiscUtil.addCondition(MiscUtil.makeSelect(loObject), " sRqrmtCde = " +  SQLUtil.toSQL(poModel.getRequirementCode())
                                            + " AND cRecdStat = " +  SQLUtil.toSQL(RecordStatus.ACTIVE));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) >= 0) {
            while (loRS.next()) {
                // Print the result set
                System.out.println("sRqrmtIDx: " + loRS.getString("sRqrmtIDx"));
                System.out.println("sRqrmtCde: " + loRS.getString("sRqrmtCde"));
                System.out.println("------------------------------------------------------------------------------");
                lbIsError = true;
                if(lsReqID.isEmpty()){
                    lsReqID = loRS.getString("sRqrmtIDx");
                } else {
                    lsReqID = lsReqID + "," + loRS.getString("sRqrmtIDx");
                }
            }
        } else {
            poJSON.put("result", "success");
            poJSON.put("continue", true);
            poJSON.put("message", "No record found.");
        }
        MiscUtil.close(loRS);
        if(lbIsError){
            poJSON.put("result", "error");
            poJSON.put("message", poModel.getDescription() + " already used in requirement source per group <"+lsReqID+">.");
        }
        return poJSON;
    }
    
    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsCondition = "";
        if (psRecdStat != null) {
            if (psRecdStat.length() > 1) {
                for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                    lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
                }
                lsCondition = " cRecdStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = " cRecdStat = " + SQLUtil.toSQL(psRecdStat);
            }
        }
        
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Requirement Code»Description",
                "sRqrmtCde»sDescript",
                "sRqrmtCde»sDescript",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sRqrmtCde"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
}

