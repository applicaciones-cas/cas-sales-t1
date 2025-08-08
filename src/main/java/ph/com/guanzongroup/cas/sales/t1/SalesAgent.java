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
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Agent;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class SalesAgent extends Parameter {
    Model_Sales_Agent poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException {
      psRecdStat = "1";
      poModel = new SalesModels(poGRider).SalesAgent();
      super.initialize();
    }

    @Override
    public Model_Sales_Agent getModel() {
      return poModel;
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();
//        if (poGRider.getUserLevel() < 16) {
//            poJSON.put("result", "error");
//            poJSON.put("message", "User is not allowed to save record.");
//            return poJSON;
//        } 
        if (poModel.getClientId().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Client ID must not be empty.");
            return poJSON;
        } 
        if (poModel.getProfession().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Profession must not be empty.");
            return poJSON;
        } 
        if (poModel.getCompany().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Company must not be empty.");
            return poJSON;
        } 
        if (poModel.getPosition().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Position must not be empty.");
            return poJSON;
        } 
        
        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());
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
                lsCondition = " a.cRecdStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = " a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
            }
        }
        
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Agent ID»Name",
                "sClientID»sCompnyNm",
                "a.sClientID»b.sCompnyNm",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sClientID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    @Override
    public String getSQ_Browse() {
        return    " SELECT "                                                    
                + "    a.sClientID "                                            
                + "  , a.sProfessn "                                            
                + "  , a.sCompanyx "                                            
                + "  , a.sPosition "                                            
                + "  , a.cRecdStat "                                            
                + "  , b.sCompnyNm "                                            
                + " FROM sales_agent a "                                        
                + " LEFT JOIN client_master b on b.sClientID = a.sClientID ";  
    }
}
