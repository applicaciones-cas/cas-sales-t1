/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Requirement_Source;
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
        if (poModel.getEditMode() == 0) {
            poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
            poModel.setModifiedDate(poGRider.getServerDate());
        } 
        poJSON.put("result", "success");
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

