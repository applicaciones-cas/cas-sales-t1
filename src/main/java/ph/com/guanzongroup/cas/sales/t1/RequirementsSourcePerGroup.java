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
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
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
    public List<Model> paModel;
     @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = RecordStatus.ACTIVE;
        poModel = new SalesModels(poGRider).RequirementSourcePerGroup();
        paModel = new ArrayList<>();
      
      super.initialize();
    }

    @Override
    public Model_Requirement_Source_PerGroup getModel() {
        return poModel;
    }
    
    private Model_Requirement_Source_PerGroup RequirementsSourcePerGroup() {
        return new SalesModels(poGRider).RequirementSourcePerGroup();
    }
    
    public Model_Requirement_Source_PerGroup ParameterList(int row) {
        return (Model_Requirement_Source_PerGroup) paModel.get(row);
    }

    public int getParameterCount() {
        return this.paModel.size();
    }
    
    public JSONObject DeactivateRecord()
            throws SQLException,
            GuanzonException,
            CloneNotSupportedException {
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
    
    private JSONObject checkExistingSource() throws SQLException, GuanzonException{
        poJSON = new JSONObject();
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), " a.cPayModex = " +  SQLUtil.toSQL(poModel.getPaymentMode())
                                                            + " AND a.cCustGrpx = " +  SQLUtil.toSQL(poModel.getCustomerGroup())
                                                            + " AND a.sRqrmtCde = " +  SQLUtil.toSQL(poModel.getRequirementCode()));
        System.out.println("Execute SQL : " + lsSQL);
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
        
        String lsSQL = getSQ_Browse();
        if(!lsCondition.isEmpty()){
            lsSQL = MiscUtil.addCondition(lsSQL, lsCondition);
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Requirement ID»Description»Customer Group»Payment Mode",
                "sRqrmtIDx»sDescript»Customer_Group»PaymentMode",
                "a.sRqrmtIDx»b.sDescript»"+getCustomerGroupCase()+"»" + getPaymentModeCase(),
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
     
    /**
     * Load parameter list
     * @param fsCustomerType
     * @param fsPaymentMode
     * @return 
     */
    public JSONObject loadParameterList(String fsCustomerType, String fsPaymentMode){
        poJSON = new JSONObject();
        try {
            fsCustomerType = (fsCustomerType == null || "-1".equals(fsCustomerType)) ? "" : fsCustomerType;
            fsPaymentMode = (fsPaymentMode == null || "-1".equals(fsPaymentMode)) ? "" : fsPaymentMode;
            
            String lsSQL = MiscUtil.addCondition(getSQ_Browse(),
                    " a.cCustGrpx LIKE " + SQLUtil.toSQL("%"+fsCustomerType)
                + " AND a.cPayModex LIKE " + SQLUtil.toSQL("%"+fsPaymentMode)
            );
            
            String lsStatus = "";
            if (psRecdStat != null) {
                if (psRecdStat.length() > 1) {
                    for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                        lsStatus += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
                    }
                    lsStatus = " AND a.cRecdStat IN (" + lsStatus.substring(2) + ")";
                } else {
                    lsStatus = " AND a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
                }
            }

            lsSQL = lsSQL + "" + lsStatus +" GROUP BY a.sRqrmtIDx ORDER BY a.cCustGrpx, a.cPayModex ASC ";

            System.out.println("Executing SQL: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            poJSON = new JSONObject();

            int lnctr = 0;

            if (MiscUtil.RecordCount(loRS) >= 0) {
                paModel = new ArrayList<>();
                while (loRS.next()) {
                    // Print the result set
                    System.out.println("sRqrmtIDx: " + loRS.getString("sRqrmtIDx"));
                    System.out.println("cCustGrpx: " + loRS.getString("cCustGrpx"));
                    System.out.println("cPayModex: " + loRS.getString("cPayModex"));
                    System.out.println("sDescript: " + loRS.getString("sDescript"));
                    System.out.println("------------------------------------------------------------------------------");

                    paModel.add(RequirementsSourcePerGroup());
                    paModel.get(paModel.size() - 1).openRecord(loRS.getString("sRqrmtIDx"));
                    lnctr++;
                }

                System.out.println("Records found: " + lnctr);
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                paModel = new ArrayList<>();
                paModel.add(RequirementsSourcePerGroup());
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record found.");
            }
            MiscUtil.close(loRS);
        }catch (GuanzonException | SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON.put("result", "error");
            poJSON.put("message", MiscUtil.getException(ex));
        }
        poJSON.put("result", "success");
        return poJSON;
    }
     
    private String getPaymentModeCase(){
        return " CASE 					 "
                + "   WHEN a.cPayModex = '0' THEN 'Cash'         "
                + "   WHEN a.cPayModex = '1' THEN 'Cash Balance' "
                + "   WHEN a.cPayModex = '2' THEN 'Installment'  "
                + "   WHEN a.cPayModex = '3' THEN 'PO'           "
                + "   WHEN a.cPayModex = '4' THEN 'Financing'    "
                + "   WHEN a.cPayModex = '5' THEN 'Insurance'    "
                + "   WHEN a.cPayModex = '6' THEN 'Term'         "
                + "   ELSE ''            "
                + " END ";
    }
     
    private String getCustomerGroupCase(){
        return " CASE                 "
            + "   WHEN a.cCustGrpx = '0' THEN 'Any'        "
            + "   WHEN a.cCustGrpx = '1' THEN 'Employee'   "
            + "   WHEN a.cCustGrpx = '2' THEN 'OFW'        "
            + "   WHEN a.cCustGrpx = '3' THEN 'Seaman'     "
            + "   WHEN a.cCustGrpx = '4' THEN 'Business'   "
            + "   ELSE ''          "
            + " END ";
    }
    
    @Override
    public String getSQ_Browse() {
        return    " SELECT "
                + "   a.sRqrmtIDx, "
                + "   a.cPayModex, "
                + "   a.cCustGrpx, "
                + getPaymentModeCase() + " AS PaymentMode ,"
                + getCustomerGroupCase() + " AS Customer_Group,"
                + "   a.sRqrmtCde,         "
                + "   a.cRequired,         "
                + "   a.cRecdStat,         "
                + "   a.sModified,         "
                + "   a.dModified,         "
                + "   b.sDescript          "
                + " FROM requirement_source_pergroup a  "
                + " LEFT JOIN requirement_source b ON b.sRqrmtCde = a.sRqrmtCde ";
    }
    
}

