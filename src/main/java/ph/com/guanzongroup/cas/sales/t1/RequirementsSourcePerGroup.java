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
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Requirement_Source_PerGroup;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class RequirementsSourcePerGroup extends Parameter {
    Model_Requirement_Source_PerGroup poModel;
  
     @Override
    public void initialize() throws SQLException, GuanzonException {
      psRecdStat = RecordStatus.ACTIVE;
      poModel = new SalesModels(poGRider).RequirementSourcePerGroup();
      
      super.initialize();
    }

    @Override
    public Model_Requirement_Source_PerGroup getModel() {
        return poModel;
    }
    
    private Model_Requirement_Source_PerGroup SalesInquirySources() {
        return new SalesModels(poGRider).RequirementSourcePerGroup();
    }
  
    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException {
      poJSON = new JSONObject();
        if (poModel.getRequirementId().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Requirement id must not be empty.");
            return poJSON;
        }
        if (poModel.getRequirementCode().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Requirement code must not be empty.");
            return poJSON;
        }
        if (poModel.getCustomerGroup().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Customer Group cannot be empty.");
            return poJSON;
        } 
        if (poModel.getPaymentMode().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Payment mode cannot be empty.");
            return poJSON;
        } 
        
        poJSON = checkExistingSource();
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
        }
        
        if (poModel.getEditMode() == 0) {
            poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
            poModel.setModifiedDate(poGRider.getServerDate());
        } 
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject checkExistingSource() throws SQLException, GuanzonException{
        poJSON = new JSONObject();
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), " a.cPayModex = " +  SQLUtil.toSQL(poModel.getPaymentMode())
                                                            + " AND a.cCustGrpx = " +  SQLUtil.toSQL(poModel.getCustomerGroup())
                                                            + " AND a.sRqrmtCde = " +  SQLUtil.toSQL(poModel.getRequirementCode()));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) >= 0) {
            while (loRS.next()) {
                // Print the result set
                System.out.println("sRqrmtIDx: " + loRS.getString("sRqrmtIDx"));
                System.out.println("sRqrmtCde: " + loRS.getString("sRqrmtCde"));
                System.out.println("------------------------------------------------------------------------------");

                poJSON.put("result", "error");
                poJSON.put("message", poModel.RequirementSource().getDescription()+ " already exists.");
            }
        } else {
            poJSON.put("result", "success");
            poJSON.put("continue", true);
            poJSON.put("message", "No record found.");
        }
        MiscUtil.close(loRS);
        
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
                lsCondition = " a.cRecdStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = " a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
            }
        }
        
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Requirement Code»Description",
                "sRqrmtIDx»sDescript",
                "a.sRqrmtIDx»b.sDescript",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sRqrmtIDx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
     public JSONObject SearchRequirmentSource(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        RequirementsSource object = new SalesControllers(poGRider, logwrapr).RequirementsSource();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            getModel().setRequirementCode(object.getModel().getRequirementCode());
        }

        return poJSON;
    }
    
    @Override
    public String getSQ_Browse() {
        return " SELECT  "
              + "   a.sRqrmtIDx "
              + " , a.cPayModex "
              + " , a.cCustGrpx "
              + " , a.sRqrmtCde "
              + " , a.cRequired "
              + " , a.cRecdStat "
              + " , a.sModified "
              + " , a.dModified "
              + " , b.sDescript "
              + "  FROM requirement_source_pergroup a "
              + " LEFT JOIN requirement_source b ON b.sRqrmtCde = a.sRqrmtCde ";
    }
    
}

