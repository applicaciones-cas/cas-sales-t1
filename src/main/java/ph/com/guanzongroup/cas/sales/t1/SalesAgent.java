/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.client.Client;
import org.guanzon.cas.client.services.ClientControllers;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Agent;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class SalesAgent extends Parameter {
    Model_Sales_Agent poModel;
    List<Model_Sales_Agent> paModelList;
    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = RecordStatus.ACTIVE;
        poModel = new SalesModels(poGRider).SalesAgent();
        paModelList = new ArrayList<>();
        super.initialize();
    }

    @Override
    public Model_Sales_Agent getModel() {
      return poModel;
    }
    
    public void resetModel() {
        poModel = new SalesModels(poGRider).SalesAgent();
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
                value,
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
    
    public JSONObject loadModelList() {
        try {

            String lsSQL = getSQ_Browse();
            lsSQL = lsSQL + " ORDER BY b.sCompnyNm DESC ";

            System.out.println("Executing SQL: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            poJSON = new JSONObject();

            int lnctr = 0;

            if (MiscUtil.RecordCount(loRS) >= 0) {
                paModelList = new ArrayList<>();
                while (loRS.next()) {
                    // Print the result set
                    System.out.println("sClientID: " + loRS.getString("sClientID"));
                    System.out.println("sCompnyNm: " + loRS.getString("sCompnyNm"));
                    System.out.println("------------------------------------------------------------------------------");

                    paModelList.add(SalesmanModel());
                    paModelList.get(paModelList.size() - 1).openRecord(loRS.getString("sClientID"));
                    lnctr++;
                }

                System.out.println("Records found: " + lnctr);
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                paModelList = new ArrayList<>();
                paModelList.add(SalesmanModel());
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record found.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON.put("result", "error");
            poJSON.put("message", MiscUtil.getException(ex));
        }
        return poJSON;
    }

    private Model_Sales_Agent SalesmanModel() {
        return new SalesModels(poGRider).SalesAgent();
    }
    public int getModelCount() {
        if (paModelList == null) {
            paModelList = new ArrayList<>();
        }

        return paModelList.size();
    }
    
    public Model_Sales_Agent ModelList(int row) {
        return (Model_Sales_Agent) paModelList.get(row);
    }
    
    public JSONObject SearchClient(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Client object = new ClientControllers(poGRider, logwrapr).Client();
        object.Master().setRecordStatus(RecordStatus.ACTIVE);
        object.Master().setClientType("0");
        poJSON = object.Master().searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            for(int lnCtr = 0; lnCtr <= getModelCount() - 1; lnCtr++){
                if(object.Master().getModel().getClientId().equals(ModelList(lnCtr).getClientId())){
                    poJSON.put("result", "error");
                    poJSON.put("message", "Sales Agent already exists in the list.");
                    return poJSON;
                }
            }
            getModel().setClientId(object.Master().getModel().getClientId());
        }
        
        System.out.println("Client ID : " + getModel().getClientId());

        return poJSON;
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
