/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Sources;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class SalesInquirySources extends Parameter {

    Model_Sales_Inquiry_Sources poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException {
      psRecdStat = RecordStatus.ACTIVE;
      poModel = new SalesModels(poGRider).SalesInquirySources();
      
      super.initialize();
    }

    @Override
    public Model_Sales_Inquiry_Sources getModel() {
        return poModel;
    }
    
    private Model_Sales_Inquiry_Sources SalesInquirySources() {
        return new SalesModels(poGRider).SalesInquirySources();
    }
    
    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();
//        if (poGRider.getUserLevel() < 16) {
//            poJSON.put("result", "error");
//            poJSON.put("message", "User is not allowed to save record.");
//            return poJSON;
//        } 
        if (poModel.getSourceId().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source ID must not be empty.");
            return poJSON;
        } 
        if (poModel.getDescription().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Description must not be empty.");
            return poJSON;
        } 
        
        poJSON = checkExistingSource();
        if("error".equals((String) poJSON.get("result"))){
            return poJSON;
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
                System.out.println("sSourceID: " + loRS.getString("sSourceID"));
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
                "Source ID»Source",
                "sSourceID»sDescript",
                "sSourceID»sDescript",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sSourceID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
}
