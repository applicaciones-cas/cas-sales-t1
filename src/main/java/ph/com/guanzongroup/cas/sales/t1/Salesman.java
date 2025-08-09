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
import org.guanzon.cas.parameter.Branch;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Salesman;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

/**
 *
 * @author Arsiela
 */
public class Salesman extends Parameter {
    Model_Salesman poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException {
      psRecdStat = RecordStatus.ACTIVE;
      poModel = new SalesModels(poGRider).Salesman();
      super.initialize();
    }

    @Override
    public Model_Salesman getModel() {
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
        if (poModel.getEmployeeId().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Employee ID must not be empty.");
            return poJSON;
        } 
        if (poModel.getBranchCode().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Branch must not be empty.");
            return poJSON;
        } 
        if (poModel.getLastName().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Last name must not be empty.");
            return poJSON;
        } 
        if (poModel.getFirstName().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "First name must not be empty.");
            return poJSON;
        } 
        //Middle name is not required
//        if (poModel.getMiddleName().isEmpty()) {
//            poJSON.put("result", "error");
//            poJSON.put("message", "Middle name must not be empty.");
//            return poJSON;
//        } 
        
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
                "Employee ID»Salesman",
                "sEmployID»sFullName",
                "a.sEmployID»concat(a.sLastName,', ',a.sFrstName, ' ',a.sMiddName)",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sEmployID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    
    public JSONObject searchRecord(String value, boolean byCode, String branch) throws SQLException, GuanzonException {
        String lsCondition = "";
        if (psRecdStat != null) {
            if (psRecdStat.length() > 1) {
                for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                    lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
                }
                lsCondition = " AND a.cRecdStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = " AND a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
            }
        }
        
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), "a.sBranchCd = " + SQLUtil.toSQL(branch));
        if(lsCondition != null && !"".equals(lsCondition)){
            lsSQL = lsSQL + lsCondition;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Employee ID»Salesman",
                "sEmployID»sFullName",
                "a.sEmployID»concat(a.sLastName,', ',a.sFrstName, ' ',a.sMiddName)",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sEmployID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject searchEmployee(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsCondition = "";
        if (psRecdStat != null) {
            if (psRecdStat.length() > 1) {
                for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                    lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
                }
                lsCondition = " AND a.cRecdStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = " AND a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
            }
        }
        
        String lsSQL = MiscUtil.addCondition(getSQ_Employee(), " a.dFiredxxx IS NULL ");
        if (lsCondition != null && !"".equals(lsCondition)) {
            lsSQL = lsSQL + lsCondition;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Employee ID»Name",
                "sEmployID»sCompnyNm",
                "a.sEmployID»b.sCompnyNm",
                byCode ? 0 : 1);

        if (poJSON != null) {
            poModel.setEmployeeId((String) poJSON.get("sEmployID"));
            poModel.setLastName((String) poJSON.get("sLastName"));
            poModel.setFirstName((String) poJSON.get("sFrstName"));
            poModel.setMiddleName((String) poJSON.get("sMiddName"));
            poJSON.put("result", "success");
            return poJSON;
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject SearchBranch(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Branch object = new ParamControllers(poGRider, logwrapr).Branch();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            getModel().setBranchCode(object.getModel().getBranchCode());
        }

        return poJSON;
    }
    
    @Override
    public String getSQ_Browse() {
        return    " SELECT "                                                    
                + "    a.sEmployID "                                            
                + "  , a.sBranchCd "                                            
                + "  , a.sLastName "                                            
                + "  , a.sFrstName "                                             
                + "  , a.sMiddName "                                             
                + "  , a.cRecdStat " 
                + "  , concat(a.sLastName,', ',a.sFrstName, ' ',a.sMiddName) as sFullName "                           
                + " FROM salesman a ";                                        
//                + " LEFT JOIN ggc_isysdbf.client_master b on b.sClientID = a.sEmployID ";  
    }
    
    public String getSQ_Employee() {
        return  " SELECT "                                                             
                + "   a.sEmployID "                                                      
                + " , a.sBranchCd "                                                      
                + " , a.dFiredxxx "                                                      
                + " , a.cRecdStat "                                                      
                + " , b.sCompnyNm "                                                      
                + " , b.sLastName "                                                      
                + " , b.sFrstName "                                                      
                + " , b.sMiddName "
                + " , c.sBranchNm "                                                      
                + " FROM ggc_isysdbf.employee_master001 a "                              
                + " LEFT JOIN ggc_isysdbf.client_master b ON b.sClientID = a.sEmployID "
                + " LEFT JOIN ggc_isysdbf.branch c ON c.sBranchCd = a.sBranchCd ";

    }
}
