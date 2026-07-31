/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.agent.services.Transaction;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.appdriver.iface.GValidator;
import org.guanzon.cas.client.Client;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.inv.Inventory;
import org.guanzon.cas.inv.services.InvControllers;
import org.guanzon.cas.parameter.Banks;
import org.guanzon.cas.parameter.Term;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Commitment_Detail;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Commitment_Master;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Detail;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;
import ph.com.guanzongroup.cas.sales.t1.validator.SalesCommitmentValidator;

/**
 *
 * @author Arsiela 07282026
 */
public class SalesCommitment extends Transaction {
    public String psCategoryId = "";
    public String psIndustryId = "";
    public String psCompanyId = "";
    public String psClient = "";
    public String psApprover = "";
    
    public List<Model> paMaster;
    public List<Model_Sales_Inquiry_Master> paSalesInquiry;
    
    public JSONObject InitTransaction() throws SQLException, GuanzonException {
        SOURCE_CODE = "Slcm";

        poMaster = new SalesModels(poGRider).SalesCommitmentMaster();
        poDetail = new SalesModels(poGRider).SalesCommitmentDetail();

        paMaster = new ArrayList<Model>();
        paSalesInquiry = new ArrayList<Model_Sales_Inquiry_Master>();
        psApprover = "";
        setApproving("");
        return initialize();
    }

    //Transaction Source Code 
    @Override
    public String getSourceCode() { return SOURCE_CODE; }
    
    //Set value for private strings used in searching / filtering data
    public void setIndustryId(String industryId) { psIndustryId = industryId; }
    public void setCompanyId(String companyId) { psCompanyId = companyId; }
    public void setCategoryId(String categoryId) { psCategoryId = categoryId; }
    public void setClient(String client) { psClient = client; }
    public String getClient() { return psClient;}
    /**
    * Creates a JSONObject with "result" and "message" fields.
    *
    * @param fsResult  The result value (e.g., "success", "error")
    * @param fsMessage The message describing the result
    * @return JSONObject containing the result and message
    */
    private JSONObject setJSON(String fsResult, String fsMessage) {
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", fsResult);
        loJSON.put("message", fsMessage);
        return loJSON;
    }

    /**
     * Checks whether a JSONObject indicates a successful result.
     *
     * Returns true if the "result" field equals "success" or is not "error".
     *
     * @param foJSON The JSONObject to check
     * @return true if successful, false otherwise
     */
    public boolean isJSONSuccess(JSONObject foJSON) {
        return ("success".equals((String) foJSON.get("result")) || !"error".equals((String) foJSON.get("result")));
    }
    
    /**
    * Creates a new transaction record.
    *
    * @return JSONObject result of the operation
    * @throws CloneNotSupportedException if cloning fails
    * @throws SQLException if a database error occurs
    * @throws GuanzonException if application-specific error occurs
    */
    public JSONObject NewTransaction()
            throws CloneNotSupportedException, SQLException, GuanzonException {
        return newTransaction();
    }
    
    /**
    * Opens an existing transaction and loads its associated data.
    * 
    * This method resets the current transaction state, retrieves the specified transaction 
    * record, and automatically loads any related attachments.
    * 
    * @param transactionNo the unique identifier of the transaction to be opened.
    * @return a {@link JSONObject} containing the success status or an error message if the 
    *         transaction or its attachments fail to load.
    * @throws CloneNotSupportedException if an error occurs during object cloning.
    * @throws SQLException if a database access error occurs.
    * @throws GuanzonException if a business logic or validation error occurs.
    * @throws ScriptException if an error occurs during script execution.
    */
    public JSONObject OpenTransaction(String transactionNo) throws CloneNotSupportedException, SQLException, GuanzonException, ScriptException {
        return openTransaction(transactionNo);
    }

    /**
    * Prepares the current transaction for modification.
    * 
    * This method initiates the update state for the transaction and refreshes 
    * its associated attachments to ensure data consistency during editing.
    * 
    * @return a {@link JSONObject} indicating the success or failure of the update request.
    * @throws SQLException if a database error occurs.
    * @throws GuanzonException if business logic validation fails.
    * @throws CloneNotSupportedException if an error occurs during data cloning.
    * @throws ScriptException if an error occurs during script-based processing.
    */
   public JSONObject UpdateTransaction() throws SQLException, GuanzonException, CloneNotSupportedException, ScriptException {
       return updateTransaction();
   }
    
    /**
     * Commits the current transaction changes to the database.
     * 
     * @return A {@link JSONObject} containing the result of the save operation.
     * @throws SQLException, GuanzonException, CloneNotSupportedException 
     *          If a database constraint is violated or business logic validation fails.
     */
    public JSONObject SaveTransaction() throws SQLException, GuanzonException, CloneNotSupportedException {
        return saveTransaction();
    }
    
    /**
    * Requests user approval for the current transaction.
    *
    * @return JSONObject containing approval result and message
    */
    public JSONObject callApproval(){
        poJSON = new JSONObject();
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
            String lsUserIDxx = poJSON.get("sUserIDxx").toString();
            if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                poJSON = setJSON("error", "User is not an authorized approving officer.");
                return poJSON;
            }
            setApproving(lsUserIDxx);
            psApprover = lsUserIDxx;
        }   
        
        poJSON = setJSON("success","success");
        return poJSON;
    }
    
    /**
    * Converts a numeric or short-code transaction status into a human-readable string.
    * 
    * @param lsStatus the status code to be converted.
    * @return the descriptive name of the status (e.g., "Voided", "Confirmed"), 
    *         or "Unknown" if the code is not recognized.
    */
    public String getStatus(String lsStatus) {
        switch (lsStatus) {
            case BankApplicationStatus.DISAPPROVED:
                return "Disapproved";
            case BankApplicationStatus.CANCELLED:
                return "Cancelled";
            case BankApplicationStatus.APPROVED:
                return "Approved";
            case BankApplicationStatus.OPEN:
                return "Open";
            default:
                return "Unknown";
        }
    }
    
    public JSONObject ApproveTransaction() throws ParseException, SQLException, GuanzonException, CloneNotSupportedException, ScriptException {
        poJSON = new JSONObject();
        String lsStatus = BankApplicationStatus.APPROVED;
        
        if (getEditMode() != EditMode.READY) {
            poJSON = setJSON("error", "No transacton was loaded.");
            return poJSON;
        }
        
        Model_Sales_Commitment_Master loObject = new SalesModels(poGRider).SalesCommitmentMaster();
        poJSON = loObject.openRecord(Master().getTransactionNo());
        if (!isJSONSuccess(poJSON)) {
            poJSON = setJSON((String) poJSON.get("result"), "Unable to load transaction. " + (String) poJSON.get("message"));
            return poJSON;
        }
        
        if (loObject.getTransactionStatus().equals(lsStatus)) {
            poJSON = setJSON("error", "Transaction was already approved.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(lsStatus);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        if(!pbWthParent){
            psApprover = poGRider.getUserID();
            poJSON = callApproval();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        poGRider.beginTrans("UPDATE STATUS", "ApproveTransaction", SOURCE_CODE, Master().getTransactionNo());
        
        Model_Sales_Commitment_Master loModel = new SalesModels(poGRider).SalesCommitmentMaster();
        loModel.initialize();

        poJSON = loModel.openRecord(Master().getTransactionNo());
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        if(loModel.getApprovedDate() == null){
            poJSON = loModel.updateRecord();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
            loModel.setApprovedDate(Master().getApprovedDate());
            poJSON = loModel.saveRecord();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }

        //change status
        poJSON = statusChange(Master().getTable(), (String) Master().getValue("sTransNox"),"", lsStatus, false,true);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }

        poGRider.commitTrans();
        
        poJSON = new JSONObject();
        poJSON = setJSON("success", "Transaction approved successfully.");
        return poJSON;
    }
    
    /**
    * Void transaction
    *
    * @return JSONObject containing the result of the confirmation process
    * @throws ParseException if date parsing fails
    * @throws SQLException if a database error occurs
    * @throws GuanzonException if a system error occurs
    * @throws CloneNotSupportedException if cloning is not supported
     * @throws javax.script.ScriptException
    */
    public JSONObject DisapproveTransaction()
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException,
            ScriptException {
        poJSON = new JSONObject();

        String lsStatus = BankApplicationStatus.DISAPPROVED;

        if (getEditMode() != EditMode.READY) {
            poJSON = setJSON("error", "No record was loaded.");
            return poJSON;
        }
        
        Model_Sales_Commitment_Master loObject = new SalesModels(poGRider).SalesCommitmentMaster();
        poJSON = loObject.openRecord(Master().getTransactionNo());
        if (!isJSONSuccess(poJSON)) {
            poJSON = setJSON((String) poJSON.get("result"), "Unable to load transaction. " + (String) poJSON.get("message"));
            return poJSON;
        }
        
        if (loObject.getTransactionStatus().equals(lsStatus)) {
            poJSON = setJSON("error", "Transaction was already disapproved.");
            return poJSON;
        }
        
        //validator
        poJSON = isEntryOkay(lsStatus);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }

        if(!pbWthParent){
            psApprover = poGRider.getUserID();
            poJSON = callApproval();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        //change status
        poJSON = statusChange(Master().getTable(), (String) Master().getValue("sTransNox"),"", lsStatus, false);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        poJSON = new JSONObject();
        poJSON = setJSON("success", "Transaction disapproved successfully.");
        return poJSON;
    }
    
    /**
    * Cancel transaction
    *
    * @return JSONObject containing the result of the confirmation process
    * @throws ParseException if date parsing fails
    * @throws SQLException if a database error occurs
    * @throws GuanzonException if a system error occurs
    * @throws CloneNotSupportedException if cloning is not supported
     * @throws javax.script.ScriptException
    */
    public JSONObject CancelTransaction()
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException,
            ScriptException {
        poJSON = new JSONObject();

        String lsStatus = BankApplicationStatus.CANCELLED;

        if (getEditMode() != EditMode.READY) {
            poJSON = setJSON("error", "No record was loaded.");
            return poJSON;
        }
        
        Model_Sales_Commitment_Master loObject = new SalesModels(poGRider).SalesCommitmentMaster();
        poJSON = loObject.openRecord(Master().getTransactionNo());
        if (!isJSONSuccess(poJSON)) {
            poJSON = setJSON((String) poJSON.get("result"), "Unable to load transaction. " + (String) poJSON.get("message"));
            return poJSON;
        }
        
        if (loObject.getTransactionStatus().equals(lsStatus)) {
            poJSON = setJSON("error", "Transaction was already cancelled.");
            return poJSON;
        }
        
        //validator
        poJSON = isEntryOkay(lsStatus);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }

        if(!pbWthParent){
            psApprover = poGRider.getUserID();
            poJSON = callApproval();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        //change status
        poJSON = statusChange(Master().getTable(), (String) Master().getValue("sTransNox"),"", lsStatus, false);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        poJSON = new JSONObject();
        poJSON = setJSON("success", "Transaction cancelled successfully.");
        return poJSON;
    }
    
    /*Search Master References*/
    public JSONObject SearchTransaction() throws CloneNotSupportedException, SQLException, GuanzonException, ScriptException{
        poJSON = new JSONObject();

        initSQL();
        String lsSQL = MiscUtil.addCondition(SQL_BROWSE,
                " b.sCompnyID = " + SQLUtil.toSQL(psCompanyId)
                + " AND b.sIndstCdx = " + SQLUtil.toSQL(psIndustryId)
                + " AND b.sCategrCd = " + SQLUtil.toSQL(psCategoryId)
                + " AND b.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode()));
        
        //If current user is an ordinary user load only its inquiries
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            lsSQL = MiscUtil.addCondition(lsSQL,
                    " b.sSalesman = " + SQLUtil.toSQL(getSysUser(poGRider.getUserID(), true)));
        }
        
        lsSQL = lsSQL + " GROUP BY a.sTransNox ";
        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Transaction Date»Transaction No»Client»Sales Person",
                "dTransact»sTransNox»sClientNm»sSalesman",
                "a.dTransact»a.sTransNox»c.sCompnyNm»concat(d.sLastName,', ',d.sFrstName, ' ',d.sMiddName)",
                0);

        if (poJSON != null) {
            return OpenTransaction((String) poJSON.get("sTransNox"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject SearchTransaction(String fsClient, String fsTransaction, boolean fbIsSearchClient) throws CloneNotSupportedException, SQLException, GuanzonException, ScriptException{
        poJSON = new JSONObject();
        int lnSort = 1;
        
        if(fbIsSearchClient){
            lnSort = 2;
        }
        
        initSQL();
        String lsSQL = MiscUtil.addCondition(SQL_BROWSE,
                " b.sCompnyID = " + SQLUtil.toSQL(psCompanyId)
                + " AND b.sIndstCdx = " + SQLUtil.toSQL(psIndustryId)
                + " AND b.sCategrCd = " + SQLUtil.toSQL(psCategoryId)
                + " AND b.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())
                + " AND c.sCompnyNm LIKE " + SQLUtil.toSQL("%" + fsClient + "%")
                + " AND a.sTransNox LIKE " + SQLUtil.toSQL("%" + fsTransaction + "%"));
        
        //If current user is an ordinary user load only its inquiries
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            lsSQL = MiscUtil.addCondition(lsSQL,
                    " b.sSalesman = " + SQLUtil.toSQL(getSysUser(poGRider.getUserID(), true)));
        }
        
        lsSQL = lsSQL + " GROUP BY a.sTransNox ";
        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Transaction Date»Transaction No»Client»Sales Person",
                "dTransact»sTransNox»sClientNm»sSalesman",
                "a.dTransact»a.sTransNox»c.sCompnyNm»concat(d.sLastName,', ',d.sFrstName, ' ',d.sMiddName)",
                lnSort);

        if (poJSON != null) {
            return OpenTransaction((String) poJSON.get("sTransNox"));
        } else {
            poJSON = new JSONObject();
            poJSON = setJSON("error", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject SearchClient(String value, boolean byCode, boolean isSearch)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Client object = new ClientControllers(poGRider, logwrapr).Client();
        object.Master().setRecordStatus(RecordStatus.ACTIVE);
        object.Master().setClientType(Master().getClientType());
        poJSON = object.Master().searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            if(isSearch) {
                 setClient(object.Master().getModel().getCompanyName());
            } else {
                Master().setClientId(object.Master().getModel().getClientId());
                Master().setSourceNo("");
                Master().setSourceCode("");
                System.out.println("Client : " + Master().Client().getCompanyName());
            }
        }
        

        return poJSON;
    }
    
    public JSONObject SearchTerm(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        if(Master().getSourceNo() == null || "".equals(Master().getSourceNo())){
            poJSON.put("result", "error");
            poJSON.put("message", "Inquiry details cannot be empty.");
            return poJSON;
        }
        
        Term object = new ParamControllers(poGRider, logwrapr).Term();
        object.getModel().setRecordStatus(RecordStatus.ACTIVE);

        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            Master().setTermCode(object.getModel().getTermId());
        }
        return poJSON;
    }
    
    public JSONObject SearchBank(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        
        if(Master().getSourceNo() == null || "".equals(Master().getSourceNo())){
            poJSON.put("result", "error");
            poJSON.put("message", "Inquiry details cannot be empty.");
            return poJSON;
        }
        
        if(!(Master().Inquiry().getPurchaseType().equals(SalesInquiryStatic.PurchaseType.PO)
            || Master().Inquiry().getPurchaseType().equals(SalesInquiryStatic.PurchaseType.FINANCING))){
            poJSON.put("result", "error");
            poJSON.put("message", "Sales Inquiry purchase type must be PO or Financing.");
            return poJSON;
        }

        Banks object = new ParamControllers(poGRider, logwrapr).Banks();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = checkExistingBank(object.getModel().getBankID());
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            Master().setBankId(object.getModel().getBankID());
        }
        
        System.out.println("Bank Name : " + Master().Bank().getBankName());
        
        return poJSON;
    }
    /**
     * Check Existing Bank
     * @param bankId
     * @param row
     * @return JSONObject success or error
     */
    private JSONObject checkExistingBank(String bankId){
        poJSON = new JSONObject();
        boolean lbExist = false;
        try {
            initSQL();
            String lsSQL = MiscUtil.addCondition(SQL_BROWSE,
                    " b.sSourceNo = " + SQLUtil.toSQL(Master().getSourceNo())
                    + " AND b.sSourceCd = " + SQLUtil.toSQL(Master().getSourceCode())
                    + " AND a.sIssuerID = " + SQLUtil.toSQL(bankId)
                    + " AND ( a.cTranStat = " + SQLUtil.toSQL(BankApplicationStatus.OPEN)
                    + " OR a.cTranStat = " + SQLUtil.toSQL(BankApplicationStatus.APPROVED)
                    + " ) "
                    );
            System.out.println("Executing SQL: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            poJSON = new JSONObject();
            if (MiscUtil.RecordCount(loRS) >= 0) {
                lbExist = loRS.next();
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            poJSON.put("result", "error");
            poJSON.put("message",  e.getMessage());
            return poJSON;
        }
        
        if(lbExist){
            poJSON.put("result", "error");
            poJSON.put("message",  "Found existing sales commitment for the selected bank.");
            return poJSON;
        }
        
        poJSON.put("result", "success");
        poJSON.put("message", "success");
        return poJSON;
    }
    
    
    public JSONObject SearchInventory(String value, boolean byCode, int row) throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        poJSON.put("row", row);
        
        Inventory object = new InvControllers(poGRider, logwrapr).Inventory();
        object.setRecordStatus(RecordStatus.ACTIVE);
        String lsSQL = MiscUtil.addCondition(object.getSQ_Browse(), 
                                            " a.sCategCd1 = " + SQLUtil.toSQL(Master().getCategoryCode())
                                            );
        lsSQL = lsSQL + " GROUP BY a.sStockIDx ";
        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Barcode»Description»Brand»Model»Variant»UOM",
                "sBarCodex»sDescript»xBrandNme»xModelNme»xVrntName»xMeasurNm",
                "a.sBarCodex»TRIM(a.sDescript)»IFNULL(b.sDescript, '')»IFNULL(c.sDescript, '')»IFNULL(f.sDescript, '')»IFNULL(e.sDescript, '')",
                byCode ? 0 : 1);

        if (poJSON != null) {
            poJSON = object.getModel().openRecord((String) poJSON.get("sStockIDx"));
            if ("success".equals((String) poJSON.get("result"))) {
                JSONObject loJSON = checkExistingDetail(row,
                        object.getModel().getStockId()
                        );
                if ("error".equals((String) loJSON.get("result"))) {
                    if((boolean) loJSON.get("reverse")){
                        return loJSON;
                    } else {
                        row = (int) loJSON.get("row");
                        Detail(row).isReversed(true);
                    }
                }

                Detail(row).setStockId(object.getModel().getStockId());
                   
                if(object.getModel().getSellingPrice() != null){
                    Detail(row).setUnitPrice(object.getModel().getSellingPrice().doubleValue());
                } else {
                    Detail(row).setUnitPrice(0.0000);
                }
            } 
            
            System.out.println("Barcode : " + Detail(row).Inventory().getBarCode());
            System.out.println("Description : " + Detail(row).Inventory().getDescription());
            
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }
        
        poJSON.put("result", "success");
        poJSON.put("message", "success");
        poJSON.put("row", row);
        return poJSON;
    }
    
    public JSONObject checkExistingDetail(int row, String stockId){
        JSONObject loJSON = new JSONObject();
        loJSON.put("row", row);
        if(stockId == null){
            stockId = "";
        }
        int lnRow = 0;
        for (int lnCtr = 0; lnCtr <= getDetailCount()- 1; lnCtr++) {
            if(Detail(lnCtr).isReversed()){
                lnRow++;
            }
            if (lnCtr != row) {
                //Check Existing Stock ID and Description
                if(!"".equals(stockId)){
                    if((stockId.equals(Detail(lnCtr).getStockId()))){
                        if(Detail(lnCtr).isReversed()){
                            loJSON.put("result", "error");
                            loJSON.put("message", "Stock Description already exists in the transaction detail at row "+lnRow+".");
                            loJSON.put("reverse", true);
                            loJSON.put("row", lnCtr);
                            return loJSON;
                        } else {
                            loJSON.put("result", "error");
                            loJSON.put("reverse", false);
                            loJSON.put("row", lnCtr);
                            return loJSON;
                        }
                    }
                }    
            }
        }
        
        loJSON.put("result", "success");
        loJSON.put("message", "success");
        return loJSON;
    }
    
    /**
    * Calculates and validates transaction totals, including VAT components and net amounts.
    *
    * This method aggregates values from detail rows, performs non-negative 
    * validation for all calculated totals, and updates the master record summary fields.
    *
    * @param isValidate Set to true to return an error immediately if any calculated total is negative.
    * @return A {@link JSONObject} indicating "success" or "error", including the specific column 
    *         name where a validation failure occurred.
    */
    public JSONObject computeFields(boolean isValidate) {
        try {
            poJSON = new JSONObject();
            poJSON.put("column", "");
            Double ldblTransactionTotal = 0.0000;
            Double ldblVATSalesTotal = 0.0000;
            Double ldblVATAmountTotal = 0.0000;
            Double ldblVATExemptTotal = 0.0000;
            Double ldblZeroVATSales = 0.0000;
            Double ldblNetAmountDue = 0.0000;
            
            if(SalesInquiryStatic.CategoryCode.CAR.equals(Master().Inquiry().getCategoryCode())){
                ldblTransactionTotal = Master().getSalesAmount();
            } else {
                for (int lnCntr = 0; lnCntr <= getDetailCount() - 1; lnCntr++) {
                    if(Detail(lnCntr).isReversed()){
                        ldblTransactionTotal += (Detail(lnCntr).getUnitPrice() * Detail(lnCntr).getQuantity());
                        
                    }
                }
            
//                ldblNetAmountDue = ldblTransactionTotal - Master().getWithholdingTax();
//                if (ldblNetAmountDue < 0.0000) {
//                    poJSON = setJSON("error", "Invalid Net Total Amount.");
//                    poJSON.put("column", "nNetTotal");
//                    if(isValidate){
//                        return poJSON;
//                    }
//                }
//                
//                if(ldblVATExemptTotal < 0.0000) {
//                    poJSON = setJSON("error", "Invalid Vat Exempt Total.");
//                    poJSON.put("column", "nVatExmpt");
//                    if(isValidate){
//                        return poJSON;
//                    }
//                }
            }
            
//            if(ldblTransactionTotal < 0.0000) {
//                poJSON = setJSON("error", "Invalid Transaction Total.");
//                poJSON.put("column", "nTranTotl");
//                if(isValidate){
//                    return poJSON;
//                }
//            }
//            if(ldblVATSalesTotal < 0.0000) {
//                poJSON = setJSON("error", "Invalid Vat Sales Total.");
//                poJSON.put("column", "nVATSales");
//                if(isValidate){
//                    return poJSON;
//                }
//            }
//            if(ldblVATAmountTotal < 0.0000) {
//                poJSON = setJSON("error", "Invalid Vat Amount Total.");
//                poJSON.put("column", "nVATAmtxx");
//                if(isValidate){
//                    return poJSON;
//                }
//            }
            
            Master().setSalesAmount(ldblTransactionTotal);
            Master().setTransactionTotal(ldblTransactionTotal);
//            Master().setVATSale(ldblVATSalesTotal);
//            Master().setVATAmount(ldblVATAmountTotal);
//            Master().setVATExmpt(ldblVATExemptTotal);
//            Master().setVATRates(ldblZeroVATSales);
            
            poJSON = setJSON("success", "computed successfully");
            poJSON.put("column", "");
            return poJSON;
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesCommitment.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON = setJSON("error", MiscUtil.getException(ex));
            poJSON.put("column", "");
            return poJSON;
        } 
    }
    
    //according to ma'am grace for the mean time user input for vat in CAR 07302026
    public JSONObject setVatRate(String fsValue) {
        poJSON = new JSONObject();
        if (fsValue == null || fsValue.isEmpty()) {
            fsValue = "0.00";
        }
        
        Double ldblValue = Double.parseDouble(fsValue);
        Double lnTotalAmount = Master().getTransactionTotal();
        
        if(ldblValue == 0.0000){
            Master().setVATRates(ldblValue);
            poJSON.put("result", "success");
            return poJSON;
        }
        
        if (lnTotalAmount == 0.0000) {
            poJSON.put("message", "You're not allowed to enter vat rate, no transaction total amount.");
            poJSON.put("result", "error");
            Master().setVATRates(0.00);
            return poJSON;
        }
        if (ldblValue < 0.00 || ldblValue > 100.00) {
            poJSON.put("message", "Invalid vat rate. Must be between 0.00 and 100.00");
            poJSON.put("result", "error");
            Master().setVATRates(0.00);
            return poJSON;
        }

        Master().setVATRates(ldblValue);
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject setVatableSales(String fsValue) {
        poJSON = new JSONObject();
        if (fsValue == null || fsValue.isEmpty()) {
            fsValue = "0.0000";
        }
        
        Double ldblValue = Double.parseDouble(fsValue);
        Double ldblTotalAmount = Master().getTransactionTotal();
        
        if(ldblValue == 0.0000){
            Master().setVATSale(ldblValue);
            poJSON.put("result", "success");
            return poJSON;
        }
        
        if (ldblTotalAmount == 0.0000) {
            poJSON.put("message", "You're not allowed to enter vatable sales, no transaction total amount.");
            poJSON.put("result", "error");
            Master().setVATSale(0.00);
            return poJSON;
        }
        
        if (ldblValue < 0.00) {
            poJSON.put("message", "Invalid vatable sales.");
            poJSON.put("result", "error");
            Master().setVATSale(0.00);
            return poJSON;
        }
        if (ldblValue > ldblTotalAmount) {
            poJSON.put("message", "Vatable sales cannot be greater than transaction total.");
            poJSON.put("result", "error");
            Master().setVATSale(0.00);
            return poJSON;
        }

        Master().setVATSale(ldblValue);
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject setVatableAmount(String fsValue) {
        poJSON = new JSONObject();
        if (fsValue == null || fsValue.isEmpty()) {
            fsValue = "0.0000";
        }
        
        Double ldblTotalAmount = Master().getTransactionTotal();
        Double ldblVatableSales = Master().getVATSale();
        Double ldblValue = Double.parseDouble(fsValue);
        
        if(ldblValue == 0.0000){
            Master().setVATAmount(ldblValue);
            poJSON.put("result", "success");
            return poJSON;
        }
        
        if (ldblTotalAmount == 0.0000) {
            poJSON.put("message", "You're not allowed to enter vat amount, no transaction total amount.");
            poJSON.put("result", "error");
            Master().setVATAmount(0.00);
            return poJSON;
        }
        
        if (ldblValue < 0.00) {
            poJSON.put("message", "Invalid vat amount.");
            poJSON.put("result", "error");
            Master().setVATAmount(0.00);
            return poJSON;
        }
        if (ldblValue > ldblTotalAmount || ldblValue > ldblVatableSales) {
            poJSON.put("message", "Vat amount cannot be greater than vatable sales or transaction total.");
            poJSON.put("result", "error");
            Master().setVATAmount(0.00);
            return poJSON;
        }

        Master().setVATAmount(ldblValue);
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject setWTaxRate(String fsValue) {
        poJSON = new JSONObject();
        if (fsValue == null || fsValue.isEmpty()) {
            fsValue = "0.00";
        }
        
        Double ldblValue = Double.parseDouble(fsValue);
        Double lnTotalAmount = Master().getTransactionTotal();
        
        if(ldblValue == 0.0000){
            Master().setWTaxRate(ldblValue);
            poJSON.put("result", "success");
            return poJSON;
        }
        
        if (lnTotalAmount == 0.0000) {
            poJSON.put("message", "You're not allowed to enter tax rate, no transaction total amount.");
            poJSON.put("result", "error");
            Master().setWTaxRate(0.00);
            return poJSON;
        }
        if (ldblValue < 0.00 || ldblValue > 100.00) {
            poJSON.put("message", "Invalid tax rate. Must be between 0.00 and 100.00");
            poJSON.put("result", "error");
            Master().setWTaxRate(0.00);
            return poJSON;
        }

        Master().setWTaxRate(ldblValue);
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject setWithholdingTax(String fsValue) {
        poJSON = new JSONObject();
        if (fsValue == null || fsValue.isEmpty()) {
            fsValue = "0.0000";
        }
        
        Double ldblValue = Double.parseDouble(fsValue);
        Double ldblTotalAmount = Master().getTransactionTotal();
        
        if(ldblValue == 0.0000){
            Master().setWithholdingTax(ldblValue);
            poJSON.put("result", "success");
            return poJSON;
        }
        
        if (ldblTotalAmount == 0.0000) {
            poJSON.put("message", "You're not allowed to enter withholding tax, no transaction total amount.");
            poJSON.put("result", "error");
            Master().setWithholdingTax(0.00);
            return poJSON;
        }
        
        if (ldblValue < 0.00) {
            poJSON.put("message", "Invalid withholding tax.");
            poJSON.put("result", "error");
            Master().setWithholdingTax(0.00);
            return poJSON;
        }
        if (ldblValue > ldblTotalAmount) {
            poJSON.put("message", "Withholding tax cannot be greater than transaction total.");
            poJSON.put("result", "error");
            Master().setWithholdingTax(0.00);
            return poJSON;
        }

        Master().setWithholdingTax(ldblValue);
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject setVatExempt(String fsValue) {
        poJSON = new JSONObject();
        if (fsValue == null || fsValue.isEmpty()) {
            fsValue = "0.0000";
        }
        
        Double ldblValue = Double.parseDouble(fsValue);
        Double ldblTotalAmount = Master().getTransactionTotal();
        
        if(ldblValue == 0.0000){
            Master().setVATExmpt(ldblValue);
            poJSON.put("result", "success");
            return poJSON;
        }
        
        if (ldblTotalAmount == 0.0000) {
            poJSON.put("message", "You're not allowed to enter vat exempt, no transaction total amount.");
            poJSON.put("result", "error");
            Master().setVATExmpt(0.00);
            return poJSON;
        }
        
        if (ldblValue < 0.00) {
            poJSON.put("message", "Invalid vat exempt.");
            poJSON.put("result", "error");
            Master().setVATExmpt(0.00);
            return poJSON;
        }
        if (ldblValue > ldblTotalAmount) {
            poJSON.put("message", "Vat exempt cannot be greater than transaction total.");
            poJSON.put("result", "error");
            Master().setVATExmpt(0.00);
            return poJSON;
        }

        Master().setVATExmpt(ldblValue);
        poJSON.put("result", "success");
        return poJSON;
    }
    
    public JSONObject loadTransactionList(String fsClient, String fsTransactionNo) throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        paMaster = new ArrayList<>();
        
        initSQL();
        String lsSQL = MiscUtil.addCondition(SQL_BROWSE,
                " b.sIndstCdx = " + SQLUtil.toSQL(psIndustryId)
                + " AND b.sCompnyID = " + SQLUtil.toSQL(psCompanyId)
                + " AND b.sCategrCd = " + SQLUtil.toSQL(psCategoryId)
                + " AND b.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())
                + " AND c.sCompnyNm LIKE " + SQLUtil.toSQL("%" + fsClient + "%")
                + " AND a.sTransNox LIKE " + SQLUtil.toSQL("%" + fsTransactionNo + "%")
            );
        
       
        //If current user is an ordinary user load only its inquiries
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            lsSQL = MiscUtil.addCondition(lsSQL,
                    " b.sSalesman = " + SQLUtil.toSQL(getSysUser(poGRider.getUserID(), true)));
        }
        
        lsSQL = lsSQL + " ORDER BY a.dTransact, c.sCompnyNm  ASC ";
        System.out.println("Executing SQL: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) <= 0) {
            poJSON = setJSON("error", "No record found.");
            return poJSON;
        }

        while (loRS.next()) {
            Model_Sales_Commitment_Master loObject = new SalesModels(poGRider).SalesCommitmentMaster();
            poJSON = loObject.openRecord(loRS.getString("sTransNox"));
            if (isJSONSuccess(poJSON)) {
                paMaster.add((Model) loObject);
            } else {
                return poJSON;
            }
        }
        MiscUtil.close(loRS);
        return poJSON;
    }
    
    public JSONObject loadSalesInquiryList(String fsClient) throws SQLException, GuanzonException {
          try {

            String lsSQL = MiscUtil.addCondition(salesInquirySQL(), 
                            " a.sIndstCdx = " + SQLUtil.toSQL(psIndustryId)
                            + " AND a.sCompnyID = " + SQLUtil.toSQL(psCompanyId)
                            + " AND a.sCategrCd = " + SQLUtil.toSQL(psCategoryId)
                            + " AND a.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())
                            + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL("%" + fsClient)
                            + " AND ( a.cTranStat = " + SQLUtil.toSQL(SalesInquiryStatic.OPEN)
                            + " OR a.cTranStat = " + SQLUtil.toSQL(SalesInquiryStatic.CONFIRMED)
                            + " OR a.cTranStat = " + SQLUtil.toSQL(SalesInquiryStatic.QUOTED)
                            + " ) "
            );

            //If current user is an ordinary user load only its inquiries
            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
                lsSQL = MiscUtil.addCondition(lsSQL,
                        " a.sSalesman = " + SQLUtil.toSQL(getSysUser(poGRider.getUserID(), true)));
            }

            lsSQL = lsSQL + " ORDER BY a.dTransact, d.sCompnyNm ASC ";

            System.out.println("Executing SQL: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            poJSON = new JSONObject();

            int lnctr = 0;

            if (MiscUtil.RecordCount(loRS) >= 0) {
                paSalesInquiry = new ArrayList<>();
                while (loRS.next()) {
                    // Print the result set
                    System.out.println("sTransNox: " + loRS.getString("sTransNox"));
                    System.out.println("dTransact: " + loRS.getDate("dTransact"));
                    System.out.println("sCompnyNm: " + loRS.getString("sClientNm"));
                    System.out.println("------------------------------------------------------------------------------");

                    paSalesInquiry.add(SalesInquiryMaster());
                    paSalesInquiry.get(paSalesInquiry.size() - 1).openRecord(loRS.getString("sTransNox"));
                    lnctr++;
                }

                System.out.println("Records found: " + lnctr);
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                paSalesInquiry = new ArrayList<>();
                paSalesInquiry.add(SalesInquiryMaster());
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
    
    public String getPriorityUnit(){
        String lsBrand = "";
        String lsModel = "";
        String lsModelVariant = "";
        String lsColor = "";
        
        if(Master().getSourceNo() == null || "".equals(Master().getSourceNo())){
            return "";
        }
        
        try {
            Model_Sales_Inquiry_Detail loObject = new SalesModels(poGRider).SalesInquiryDetails();
            loObject.initialize();
            String lsSQL = MiscUtil.addCondition(MiscUtil.makeSelect(loObject), 
                                " sTransNox = " + SQLUtil.toSQL(Master().getSourceNo())
                                + " AND nPriority = '1'" 
                                );
            System.out.println("SQL : " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            if (loRS.next()) {
                poJSON = loObject.openRecord(Master().getSourceNo(), loRS.getInt("nEntryNox"));

                if ("success".equals((String) poJSON.get("result"))) {
                    if(loObject.getStockId() != null && !"".equals(loObject.getStockId())){
                        lsBrand = loObject.Inventory().Brand().getDescription();
                        lsModel = loObject.Inventory().Model().getDescription();
                        lsModelVariant = loObject.Inventory().Variant().getDescription();
                        lsColor = loObject.Inventory().Color().getDescription();
                    } else {
                        lsBrand = loObject.Brand().getDescription();
                        lsModel = loObject.Model().getDescription();
                        lsModelVariant = loObject.ModelVariant().getDescription();
                        lsColor = loObject.Color().getDescription();
                    }
                               
                    return (lsBrand == null ? "" : lsBrand)
                            + (lsModel == null ? "" : " " + lsModel)
                            + (lsModelVariant == null ? "" : " " + lsModelVariant)
                            + (lsColor == null ? "" : " " + lsColor);
                } else {
                    return "";
                }
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesCommitment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    private Model_Sales_Inquiry_Master SalesInquiryMaster() {
        return new SalesModels(poGRider).SalesInquiryMaster();
    }
    
    public int getSalesInquiryCount() {
        if (paSalesInquiry == null) {
            paSalesInquiry = new ArrayList<>();
        }

        return paSalesInquiry.size();
    }

    public Model_Sales_Inquiry_Master SalesInquiryList(int row) {
        return (Model_Sales_Inquiry_Master) paSalesInquiry.get(row);
    }
    
    public JSONObject populateDetail(String fsTransNo) throws SQLException, GuanzonException, CloneNotSupportedException{
        poJSON = new JSONObject();
        
        if(getEditMode() != EditMode.ADDNEW){
            poJSON = setJSON("error", "Invalid update mode.\nUnable to populate detail.");
            return poJSON;
        }
        
//        String lsExi = checkExistingBank(Master().getBank());

        Model_Sales_Inquiry_Master loMaster = new SalesModels(poGRider).SalesInquiryMaster();
        poJSON = loMaster.openRecord(fsTransNo);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        //Populate Master
        Master().setIndustryId(loMaster.getIndustryId());
        Master().setCompanyId(loMaster.getCompanyId());
        Master().setClientId(loMaster.getClientId());
        Master().setPaymentMode(loMaster.getPurchaseType());
        Master().setSourceNo(loMaster.getTransactionNo());
        Master().setSourceCode("SInq");
        
        return poJSON;
    }
    
    /**
    * Returns the master record model for the current transaction.
    * 
    * @return The {@link Model_Sales_Commitment_Master} instance representing the transaction header.
    */
    @Override
    public Model_Sales_Commitment_Master Master() { 
        return (Model_Sales_Commitment_Master) poMaster; 
    }
    /**
    * Returns the detail record model at the specified row index.
    * 
    * @param row The index of the detail row to retrieve.
    * @return The {@link Model_Sales_Commitment_Detail} instance for the given row.
    */
    @Override
    public Model_Sales_Commitment_Detail Detail(int row) {
        return (Model_Sales_Commitment_Detail) paDetail.get(row); 
    }
    /**
    * Adds a new detail row to the transaction list after validating the current entries.
    * 
    * This method ensures that a new row can only be added if the preceding row has a 
    * valid "Particular" item selected. If the last row is incomplete, an error is returned.
    * 
    * @return A JSONObject indicating the result of the operation.
    * @throws CloneNotSupportedException If an error occurs while creating the new detail model.
    */
    public JSONObject AddDetail() throws CloneNotSupportedException {
        if (getDetailCount() > 0) {
            if ((Detail(getDetailCount() - 1).getStockId() == null || "".equals(Detail(getDetailCount() - 1).getStockId()))){
                poJSON = new JSONObject();
                poJSON = setJSON("error", "Last row has empty item.");
                return poJSON;
            }
        }

        return addDetail();
    }
    
    /**
    * Retrieves a specific record from the transaction list.
    * 
    * @param row The index of the record to retrieve.
    * @return The Model_Sales_Commitment_Master instance at the specified row.
    */
    public Model_Sales_Commitment_Master TransactionList(int row) {
        return (Model_Sales_Commitment_Master) paMaster.get(row);
    }
    /**
     * Returns the total number of records in the transaction list.
     * 
     * @return The size of the transaction list.
     */
    public int getTransactionListCount() {
        return this.paMaster.size();
    }
    /*RESET CACHE ROW SET*/
    /**
     * Resets the master record to its default initial state.
     */
    public void resetMaster() {
        poMaster = new SalesModels(poGRider).SalesCommitmentMaster();
    }
    
    /**
     * Refines and validates the transaction detail list.
     * 
     * This method prunes invalid rows (those with empty particulars or zero amounts for new records) 
     * and automatically appends a new detail row if the list is empty or the last entry is valid.
     * 
     * @throws CloneNotSupportedException If an error occurs while adding a new detail row.
     */
    public void ReloadDetail() throws CloneNotSupportedException{
        int lnCtr = getDetailCount() - 1;
        while (lnCtr >= 0) {
            if ((Detail(lnCtr).getStockId() == null || "".equals(Detail(lnCtr).getStockId()))) {
                deleteDetail(lnCtr);
            } 
            lnCtr--;
        }
            
        if ((getDetailCount() - 1) >= 0) {
            if (
                (Detail(getDetailCount() - 1).getStockId() != null && !"".equals(Detail(getDetailCount() - 1).getStockId()))
                && Detail(getDetailCount() - 1).getQuantity() > 0.0000
                ) {
                AddDetail();
            }
        }

        if ((getDetailCount() - 1) < 0) {
            AddDetail();
        }
    }
    
    /**
     * Sets default master record values for a new transaction.
     * 
     * Configures the branch, industry, and company identifiers, sets the current 
     * server date, and initializes the status to {@link BankApplicationStatus#OPEN}.
     * 
     * @return A {@link JSONObject} indicating the initialization result.
     */
    @Override
    public JSONObject initFields() {
        //Put initial model values here/
        poJSON = new JSONObject();
        try {
            poJSON = new JSONObject();
            Master().setIndustryId(psIndustryId);
            Master().setCompanyId(psCompanyId);
            Master().setCategoryCode(psCategoryId);
            Master().setTransactionDate(poGRider.getServerDate());
            Master().setAppliedDate(poGRider.getServerDate());
            Master().setTransactionStatus(BankApplicationStatus.OPEN);

        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON = setJSON("error", MiscUtil.getException(ex));
            return poJSON;
        }
        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    protected JSONObject isEntryOkay(String status) {
        GValidator loValidator = new SalesCommitmentValidator();
        loValidator.setApplicationDriver(poGRider);
        loValidator.setTransactionStatus(status);
        loValidator.setMaster(Master());
        poJSON = loValidator.validate();
        return poJSON;
    }
    
    /**
     * Prepares and validates the transaction data before committing to the database.
     * 
     * This method performs final integrity checks, generates transaction numbers for new records, 
     * prunes empty detail rows, and synchronizes metadata across master, details, and 
     * attachments. It also handles attachment filename collisions by renaming duplicates 
     * and triggers the file upload process for unsent attachments.
     * 
     * @return A {@link JSONObject} indicating success or detailing validation/upload errors.
     * @throws SQLException, GuanzonException, CloneNotSupportedException 
     *          If an error occurs during data processing, file operations, or validation.
     */
    @Override
    public JSONObject willSave() throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();
        
        /*Put system validations and other assignments here*/
        System.out.println("Class Edit Mode : " + getEditMode());
        System.out.println("Master Edit Mode : " + Master().getEditMode());
        
        //Re-set the transaction no 
        if(getEditMode() == EditMode.ADDNEW){
            Master().setTransactionNo(Master().getNextCode());
        }
        
        JSONObject loJSON = checkExistingBank(Master().getBankId());
        if(!isJSONSuccess(loJSON)){
            return poJSON;
        }
        
        poJSON = isEntryOkay(Master().getTransactionStatus());
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        //Recompute fields to validate
        poJSON = computeFields(true);
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        if(SalesInquiryStatic.CategoryCode.CAR.equals(Master().Inquiry().getCategoryCode())){
            //Do not save detail for CAR
            Detail().clear();
        } else {
            Iterator<Model> detail = Detail().iterator();
            while (detail.hasNext()) {
                Model item = detail.next(); // Store the item before checking conditions
                String lsDetail = (String) item.getValue("sStockIDx");
                int lnQty = Integer.parseInt(String.valueOf(item.getValue("nQuantity")));
                if ((lnQty == 0.0000 || (lsDetail == null || "".equals(lsDetail)))){
                    if(item.getEditMode() == EditMode.ADDNEW){
                        detail.remove(); // Correctly remove the item
                    } else {
                        item.setValue("cReversex", BankApplicationStatus.Reverse.EXCLUDE);
                    }
                }
            }
            
            //Validate detail after removing all zero qty and empty stock Id
            if (getDetailCount() <= 0) {
                poJSON.put("result", "error");
                poJSON.put("message", "No transaction detail to be save.");
                return poJSON;
            }
            
            for (int lnCtr = 0; lnCtr <= getDetailCount() - 1; lnCtr++) {
                Detail(lnCtr).setTransactionNo(Master().getTransactionNo());
                Detail(lnCtr).setEntryNo(lnCtr + 1);
                Detail(lnCtr).setModifiedDate(poGRider.getServerDate());
            }
        }
        
        //Recompute amounts
        computeFields(false);
        
        Master().setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        Master().setModifiedDate(poGRider.getServerDate());
        poJSON = setJSON("success", "success");
        return poJSON;
    }
    
    /**
     * Executes business rule validations prior to the final save.
     * 
     * @return A {@link JSONObject} containing the validation result.
     * @throws CloneNotSupportedException, SQLException, GuanzonException If validation fails.
     */
    @Override
    public JSONObject save() throws CloneNotSupportedException, SQLException, GuanzonException {
        /*Put saving business rules here*/
        return isEntryOkay(BankApplicationStatus.OPEN);
    }

    @Override
    public void initSQL() {
        String lsCondition = "";
        
        if(psTranStat != null && !"".equals(psTranStat)){
            if (psTranStat.length() > 1) {
                for (int lnCtr = 0; lnCtr <= psTranStat.length() - 1; lnCtr++) {
                    lsCondition += ", " + SQLUtil.toSQL(Character.toString(psTranStat.charAt(lnCtr)));
                }

                lsCondition = "a.cTranStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = "a.cTranStat = " + SQLUtil.toSQL(psTranStat);
            }
        }

        SQL_BROWSE =" SELECT " +
                    "  a.sTransNox " +
                    " , a.dTransact " +
                    " , a.sClientID " +
                    " , a.sSourceCd " +
                    " , a.sSourceNo " +
                    " , a.cIssuerxx " +
                    " , a.sIssuerID " +
                    " , a.cPayModex " +
                    " , a.sTermCode " +
                    " , a.sPONumber " +
                    " , a.sATDNumbr " +
                    " , a.nTranTotl " +
                    " , a.nVATRatex " +
                    " , a.nVATSales " +
                    " , a.nVATAmtxx " +
                    " , a.nVATExmpt " +
                    " , a.nWTaxRate " +
                    " , a.nTWithHld " +
                    " , a.dAppliedx " +
                    " , a.dApproved " +
                    " , a.dDueDatex " +
                    " , a.sRemarksx " +
                    " , a.nSalesAmt " +
                    " , a.cTranStat " +
                    " , c.sCompnyNm AS sClientNm " +
                    " , e.sBranchNm AS  sBranchNm " +
                    " , CONCAT(d.sLastName,', ',d.sFrstName, ' ',d.sMiddName) AS sSalesman " +
                    "FROM Sales_Commitment_Master a " +
                    "LEFT JOIN Sales_Inquiry_Master b ON b.sTransNox = a.sSourceNo " +
                    "LEFT JOIN Client_Master c ON c.sClientID = a.sClientID  " +
                    "LEFT JOIN Salesman d ON d.sEmployID = b.sSalesman  " +
                    "LEFT JOIN Client_Master dd ON dd.sClientID = d.sEmployID  " +
                    "LEFT JOIN Branch e ON e.sBranchCd = b.sBranchCd  " +
                    "LEFT JOIN Company f ON f.sCompnyID = b.sCompnyID  " +
                    "LEFT JOIN Industry g ON g.sIndstCdx = b.sIndstCdx ";
        if(lsCondition != null && !"".equals(lsCondition)){
            SQL_BROWSE = MiscUtil.addCondition(SQL_BROWSE, lsCondition);
        }
    }
    
    public String salesInquirySQL() {
        return   " SELECT "
                + " a.sTransNox "
                + " , a.dTransact "
                + " , a.cTranStat "
                + " , a.sClientID "
                + " , b.sCompnyNm AS sClientNm "
                + " , concat(c.sLastName,', ',c.sFrstName, ' ',c.sMiddName) AS sSalePrsn "
                + " , d.sCompnyNm AS sAgentNme "
                + " , e.sBranchNm "
                + " , f.sCompnyNm "
                + " , g.sDescript "
                + " FROM Sales_Inquiry_Master a "
                + " LEFT JOIN Client_Master b ON b.sClientID = a.sClientID "
                + " LEFT JOIN Salesman c ON c.sEmployID = a.sSalesman "
                + " LEFT JOIN Client_Master d ON d.sClientID = a.sAgentIDx "
                + " LEFT JOIN Branch e ON e.sBranchCd = a.sBranchCd "
                + " LEFT JOIN Company f ON f.sCompnyID = a.sCompnyID "
                + " LEFT JOIN Industry g ON g.sIndstCdx = a.sIndstCdx ";
    }
    
    public JSONObject getUpdateStatusBy(String fsStatus) throws SQLException, GuanzonException {
        String lsUpdateBy = "";
        String lsDate = "";
        String lsSQL = "SELECT b.sModified,b.dModified FROM "+Master().getTable()+" a "
                     + " LEFT JOIN Transaction_Status_History b ON b.sSourceNo = a.sTransNox AND b.sTableNme = "+ SQLUtil.toSQL(Master().getTable())
                     + " AND b.cRefrStat = "+ SQLUtil.toSQL(fsStatus) ;
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(Master().getTransactionNo())) ;
        lsSQL = lsSQL + " ORDER BY b.dModified DESC ";
        System.out.println("Execute SQL STATUS : "+fsStatus+" : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
          if (MiscUtil.RecordCount(loRS) > 0L) {
            if (loRS.next()) {
                if(loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))){
                    if(loRS.getString("sModified").length() > 10){
                        lsUpdateBy = getSysUser(poGRider.Decrypt(loRS.getString("sModified")),false); 
                    } else {
                        lsUpdateBy = getSysUser(loRS.getString("sModified"),false); 
                    }
                    // Get the LocalDateTime from your result set
                    LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                    lsDate =  dModified.format(formatter);
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
        poJSON.put("sUpdateByx", lsUpdateBy);
        poJSON.put("sUpdateDte", lsDate);
        return poJSON;
    }
    
    public void ShowStatusHistory() throws SQLException, GuanzonException, Exception {
        CachedRowSet crs = getStatusHistory();

        crs.beforeFirst();

        while (crs.next()) {
            switch (crs.getString("cRefrStat")) {
                case "":
                    crs.updateString("cRefrStat", "-");
                    break;
                case BankApplicationStatus.OPEN:
                    crs.updateString("cRefrStat", "OPEN");
                    break;
                case BankApplicationStatus.CANCELLED:
                    crs.updateString("cRefrStat", "CANCELLED");
                    break;
                case BankApplicationStatus.APPROVED:
                    crs.updateString("cRefrStat", "APPROVED");
                    break;
                case BankApplicationStatus.DISAPPROVED:
                    crs.updateString("cRefrStat", "DISAPPROVED");
                    break;
                default:
                    char ch = crs.getString("cRefrStat").charAt(0);
                    String stat = String.valueOf((int) ch - 64);

                    switch (stat) {
                        case BankApplicationStatus.OPEN:
                            crs.updateString("cRefrStat", "OPEN");
                            break;
                        case BankApplicationStatus.CANCELLED:
                            crs.updateString("cRefrStat", "CANCELLED");
                            break;
                        case BankApplicationStatus.APPROVED:
                            crs.updateString("cRefrStat", "APPROVED");
                            break;
                        case BankApplicationStatus.DISAPPROVED:
                            crs.updateString("cRefrStat", "DISAPPROVED");
                            break;
                    }
            }
            crs.updateRow();
        }

        JSONObject loJSON = getEntryBy();
        String entryBy = "";
        String entryDate = "";

        if (isJSONSuccess(loJSON)) {
            entryBy = (String) loJSON.get("sCompnyNm");
            entryDate = (String) loJSON.get("sEntryDte");
        }

        showStatusHistoryUI("Sales Commitment", (String) poMaster.getValue("sTransNox"), entryBy, entryDate, crs);
    }
    /**
     * Retrieves the user and timestamp of who created the current transaction.
     *
     * @return JSONObject containing "sCompnyNm" (user) and "sEntryDte" (timestamp)
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if application-specific error occurs
     */
    public JSONObject getEntryBy() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        String lsEntry = "";
        String lsEntryDate = "";
        String lsSQL = " SELECT b.sModified, b.dModified "
                + " FROM "+Master().getTable()+" a "
                + " LEFT JOIN xxxAuditLogMaster b ON b.sSourceNo = a.sTransNox AND b.sEventNme LIKE 'ADD%NEW' AND b.sRemarksx = " + SQLUtil.toSQL(Master().getTable());
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox =  " + SQLUtil.toSQL(Master().getTransactionNo()));
        lsSQL = lsSQL + " ORDER BY b.dModified DESC ";
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    if (loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))) {
                        if (loRS.getString("sModified").length() > 10) {
                            lsEntry = getSysUser(poGRider.Decrypt(loRS.getString("sModified")),false);
                        } else {
                            lsEntry = getSysUser(loRS.getString("sModified"),false);
                        }
                        // Get the LocalDateTime from your result set
                        LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                        lsEntryDate = dModified.format(formatter);
                    }
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON = setJSON("error", e.getMessage());
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("sCompnyNm", lsEntry);
        poJSON.put("sEntryDte", lsEntryDate);
        return poJSON;
    }
    
    public String getSysUser(String fsId, boolean fbIsID) throws SQLException, GuanzonException {
        String lsEntry = "";
        String lsSQL =   " SELECT b.sCompnyNm, a.sEmployNo from xxxSysUser a "
                + " LEFT JOIN Client_Master b ON b.sClientID = a.sEmployNo ";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sUserIDxx =  " + SQLUtil.toSQL(fsId)) ;
        System.out.println("SQL " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    if(fbIsID) {
                        lsEntry = loRS.getString("sEmployNo");
                    } else {
                        lsEntry = loRS.getString("sCompnyNm");
                    }
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return lsEntry;
    }
}
