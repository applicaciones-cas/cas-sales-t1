/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.client.Client;
import org.guanzon.cas.client.ClientGUI;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Institution_Contact;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.parameter.UnitConversion;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Agent;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;

import javax.sql.rowset.CachedRowSet;

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

    public static class SalesAgentConstants {

        /**
         * Open status
         */
        public static final String OPEN = "0";

        /**
         * DISAPPROVE status
         */
        public static final String DISAPPROVE = "4";

        /**
         * ACTIVE status
         */
        public static final String ACTIVE = "1";

        /**
         * INACTIVE status
         */
        public static final String INACTIVE = "3";

    }
    public JSONObject seekApproval() throws SQLException, GuanzonException {

        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            if (Integer.parseInt(poJSON.get("nUserLevl").toString())
                    <= UserRight.ENCODER) {
                poJSON.put("result", "error");
                poJSON.put("message",
                        "User is not an authorized approving officer..");
                return poJSON;
            }
        }

        poJSON.put("result", "success");
        return poJSON;
    }

    public JSONObject DeActivateRecord(String remarks)
            throws SQLException, GuanzonException, ParseException, CloneNotSupportedException {

        String lsStatus = SalesAgent.SalesAgentConstants.INACTIVE;
        poJSON = new JSONObject();
        boolean lbConfirm = true;

        if (getEditMode() != EditMode.READY
                || getEditMode() != EditMode.UPDATE) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }

        if (lsStatus.equals(poModel.getRecordStatus())) {
            poJSON.put("error", "Record was already Inactive.");
            return poJSON;
        }

        poJSON = isEntryOkay();
        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        if (!pbWthParent) {
            poJSON = seekApproval();
            if ("error".equals(poJSON.get("result"))) {
                return poJSON;
            }
        }

        poJSON = statusChange(poModel.getTable(),
                (String) poModel.getValue("sClientID"),
                remarks, lsStatus, !lbConfirm, false);

        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("message", "Record successfully confirm.");
        return poJSON;
    }

    public JSONObject ActivateRecord(String remarks)
            throws SQLException, GuanzonException, ParseException, CloneNotSupportedException {

        String lsStatus = SalesAgent.SalesAgentConstants.ACTIVE;
        poJSON = new JSONObject();
        boolean lbConfirm = true;

        if (getEditMode() != EditMode.READY
                || getEditMode() != EditMode.UPDATE) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }

        if (lsStatus.equals(poModel.getRecordStatus())) {
            poJSON.put("error", "Record was already Active.");
            return poJSON;
        }

        poJSON = isEntryOkay();
        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        if (!pbWthParent) {
            poJSON = seekApproval();
            if ("error".equals(poJSON.get("result"))) {
                return poJSON;
            }
        }

        poJSON = statusChange(poModel.getTable(),
                (String) poModel.getValue("sClientID"),
                remarks, lsStatus, !lbConfirm, false);

        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("message", "Record successfully confirm.");
        return poJSON;
    }

    public JSONObject DisapproveRecord(String remarks)
            throws SQLException, GuanzonException, ParseException, CloneNotSupportedException {

        String lsStatus = SalesAgent.SalesAgentConstants.DISAPPROVE;
        poJSON = new JSONObject();
        boolean lbConfirm = true;

        if (getEditMode() != EditMode.READY
                || getEditMode() != EditMode.UPDATE) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }

        if (lsStatus.equals(poModel.getRecordStatus())) {
            poJSON.put("error", "Record was already Disapproved.");
            return poJSON;
        }

        poJSON = isEntryOkay();
        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        if (!pbWthParent) {
            poJSON = seekApproval();
            if ("error".equals(poJSON.get("result"))) {
                return poJSON;
            }
        }

        poJSON = statusChange(poModel.getTable(),
                (String) poModel.getValue("sClientID"),
                remarks, lsStatus, !lbConfirm, false);

        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("message", "Record successfully disapproved.");
        return poJSON;
    }

    public void ShowStatusHistory() throws SQLException, GuanzonException, Exception{
        CachedRowSet crs = getStatusHistory();

        crs.beforeFirst();

        while(crs.next()){
            switch (crs.getString("cRefrStat")){
                case "":
                    crs.updateString("cRefrStat", "-");
                    break;
                case SalesAgent.SalesAgentConstants.OPEN:
                    crs.updateString("cRefrStat", "OPEN");
                    break;
                case SalesAgent.SalesAgentConstants.ACTIVE:
                    crs.updateString("cRefrStat", "ACTIVE");
                    break;
                case SalesAgent.SalesAgentConstants.INACTIVE:
                    crs.updateString("cRefrStat", "INACTIVE");
                    break;
                case SalesAgent.SalesAgentConstants.DISAPPROVE:
                    crs.updateString("cRefrStat", "DISAPPROVE");
                    break;
                default:
                    char ch = crs.getString("cRefrStat").charAt(0);
                    String stat = String.valueOf((int) ch - 64);

                    switch (stat){
                        case SalesAgent.SalesAgentConstants.OPEN:
                            crs.updateString("cRefrStat", "OPEN");
                            break;
                        case SalesAgent.SalesAgentConstants.ACTIVE:
                            crs.updateString("cRefrStat", "ACTIVE");
                            break;
                        case SalesAgent.SalesAgentConstants.INACTIVE:
                            crs.updateString("cRefrStat", "INACTIVE");
                            break;
                        case SalesAgent.SalesAgentConstants.DISAPPROVE:
                            crs.updateString("cRefrStat", "DISAPPROVE");
                            break;

                    }
            }
            crs.updateRow();
        }

        JSONObject loJSON  = getEntryBy();
        String entryBy = "";
        String entryDate = "";

        if ("success".equals((String) loJSON.get("result"))){
            entryBy = (String) loJSON.get("sCompnyNm");
            entryDate = (String) loJSON.get("sEntryDte");
        }

        showStatusHistoryUI("Sales Agent", (String) poModel.getValue("sClientID"), entryBy, entryDate, crs);
    }

    public JSONObject getEntryBy() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        String lsEntry = "";
        String lsEntryDate = "";
        String lsSQL =  " SELECT b.sModified, b.dModified "
                + " FROM Sales_Agent a "
                + " LEFT JOIN xxxAuditLogMaster b ON b.sSourceNo = a.sClientID AND b.sEventNme LIKE 'ADD%NEW' AND b.sRemarksx = " + SQLUtil.toSQL(poModel.getTable());
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sClientID =  " + SQLUtil.toSQL(poModel.getClientId())) ;
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    if(loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))){
                        if(loRS.getString("sModified").length() > 10){
                            lsEntry = getSysUser(poGRider.Decrypt(loRS.getString("sModified")));
                        } else {
                            lsEntry = getSysUser(loRS.getString("sModified"));
                        }
                        // Get the LocalDateTime from your result set
                        LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                        lsEntryDate =  dModified.format(formatter);
                    }
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("sCompnyNm", lsEntry);
        poJSON.put("sEntryDte", lsEntryDate);
        return poJSON;
    }

    public String getSysUser(String fsId) throws SQLException, GuanzonException {
        String lsEntry = "";
        String lsSQL =   " SELECT b.sCompnyNm from xxxSysUser a "
                + " LEFT JOIN Client_Master b ON b.sClientID = a.sEmployNo ";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sUserIDxx =  " + SQLUtil.toSQL(fsId)) ;
        System.out.println("SQL " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    lsEntry = loRS.getString("sCompnyNm");
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return lsEntry;
    }
    public JSONObject getConfirmedBy() throws SQLException, GuanzonException {
        String lsConfirm = "";
        String lsDate = "";
        String lsSQL = "SELECT b.sModified,b.dModified FROM Sales_Agent a "
                + " LEFT JOIN Parameter_Status_History b ON b.sSourceNo = a.sClientID AND b.sTableNme = 'Sales_Agent' "
                + " AND ( b.cRefrStat = "+ SQLUtil.toSQL(SalesAgent.SalesAgentConstants.ACTIVE)
                + " OR (ASCII(b.cRefrStat) - 64)  = "+ SQLUtil.toSQL(SalesAgent.SalesAgentConstants.ACTIVE) + " )";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sClientID = " + SQLUtil.toSQL(poModel.getClientId())) ;
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    if (loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))) {
                        if (loRS.getString("sModified").length() > 10) {
                            lsConfirm = getSysUser(poGRider.Decrypt(loRS.getString("sModified")));
                        } else {
                            lsConfirm = getSysUser(loRS.getString("sModified"));
                        }
                        // Get the LocalDateTime from your result set
                        LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                        lsDate = dModified.format(formatter);
                    }
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("sConfirmed", lsConfirm);
        poJSON.put("sConfrmDte", lsDate);
        return poJSON;
    }



    public JSONObject addAgent() throws SQLException, GuanzonException, Exception {
        //initialize new json for result
        JSONObject loResult = new JSONObject();

        String lsClientId = poModel.getClientId();
        lsClientId = (lsClientId == null || lsClientId.isEmpty()) ? "" : lsClientId;

        //initialize Client GUI
        ClientGUI loClient = new ClientGUI();

        loClient.setGRider(poGRider);
        loClient.setLogWrapper(null);
//        loClient.setCategoryCode((String) getModel().getCategoryCode());

        //filter client type
        loClient.setClientType(ClientType.INDIVIDUAL);

        //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
        //bycode true client id
        //bycode false company

        //set search by code
        loClient.setByCode(false);

        //set cilent empty, to create a new record
        //Arsiela - 05-22-2026 - Load Client of Create new Client
        loClient.setClientId(lsClientId);

        //load record
        CommonUtils.showModal(loClient);

        //load if button
        if (!loClient.isCancelled()) {
            lsClientId = loClient.getClient().getModel().getClientId();
            //check existing record of client id to other supplier accreditation records
            loResult = checkDuplicateAgent(lsClientId); //Moved script to method by Arsiela 05-23-2026 09:19 AM
            if ("error".equals((String) loResult.get("result"))) {
                return loResult;
            }

            //set company id for supplier accreditation
            getModel().setClientId(lsClientId != null ? lsClientId : "");

        }
        loResult.put("result", "success");
        return loResult;
    }
    private JSONObject checkDuplicateAgent(String fsClientId) throws SQLException{
        JSONObject loResult = new JSONObject();
        //check existing record of client id to other supplier accreditation records
        String lsSQL = "SELECT " +
                "* " +
                "FROM " +
                "Sales_Agent";

        lsSQL = MiscUtil.addCondition(lsSQL,
                "sClientID = " + SQLUtil.toSQL(fsClientId != null ? fsClientId: "" + " ")
        );

        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) > 0) {
            loResult.put("result", "error");
            loResult.put("message", "Client is already exist!");
            return loResult;
        }
        MiscUtil.close(loRS);

        loResult.put("result", "success");
        loResult.put("message", "success");
        return loResult;
    }

    public JSONObject  getClientSummary(String fsClientId) throws SQLException {
        JSONObject loResult = new JSONObject();

        String lsSQL = "SELECT " +
                "a.sClientID, " +
                "a.sCompnyNm, " +
                "b.sEMailAdd, " +
                "c.sMobileNo, " +
                "CONCAT(" +
                "IFNULL(d.sHouseNox,''), ' ', " +
                "IFNULL(d.sAddressx,''), ', ', " +
                "IFNULL(f.sBrgyName,''), ', ', " +
                "IFNULL(g.sTownName,''), ', ', " +
                "IFNULL(h.sDescript,''), ', ', " +
                "IFNULL(g.sZippCode,'')" +
                ") AS fullAddress, " +
                "e.sSocialID, " +
                "e.cSocialTp, " +
                "e.sAccountx " +
                "FROM Client_Master a " +
                "LEFT JOIN Client_Email_Address b ON a.sClientID = b.sClientID " +
                "LEFT JOIN Client_Mobile c ON a.sClientID = c.sClientID " +
                "LEFT JOIN Client_Address d ON a.sClientID = d.sClientID " +
                "LEFT JOIN Client_Social_Media e ON a.sClientID = e.sClientID " +
                "LEFT JOIN Barangay f ON f.sBrgyIDxx = d.sBrgyIDxx " +
                "LEFT JOIN Towncity g ON g.sTownIDxx = d.sTownIDxx " +
                "LEFT JOIN Province h ON h.sProvIDxx = d.cProvince";

        lsSQL = MiscUtil.addCondition(lsSQL,
                "b.cPrimaryx = '1' " +
                        "AND c.cPrimaryx = '1' " +
                        "AND d.cPrimaryx = '1' " +
                        "AND a.sClientID = " + SQLUtil.toSQL(fsClientId));
        System.out.println("Executing SQL: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (!loRS.next()) {
            loResult.put("result", "error");
            loResult.put("message", "Client not found.");
            MiscUtil.close(loRS);
            return loResult;
        }

        loResult.put("email", loRS.getString("sEMailAdd"));
        loResult.put("mobile", loRS.getString("sMobileNo"));
        loResult.put("address", loRS.getString("fullAddress"));
        loResult.put("soctype", loRS.getString("cSocialTp"));
        loResult.put("acct", loRS.getString("sAccountx"));

        loResult.put("result", "success");
        loResult.put("message", "success");

        MiscUtil.close(loRS);

        return loResult;
    }

}
