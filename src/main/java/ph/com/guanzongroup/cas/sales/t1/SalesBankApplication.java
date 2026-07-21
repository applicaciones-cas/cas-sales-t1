/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.guanzon.cas.parameter.Banks;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Bank_Application;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;
import ph.com.guanzongroup.cas.sales.t1.validator.BankApplication;

/**
 *
 * @author Arsiela 07-20-2026
 */
public class SalesBankApplication extends Transaction{
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategorCd = "";

    List<Model_Sales_Inquiry_Master> paMasterList;

    public JSONObject InitTransaction() {
        SOURCE_CODE = "Bnkp";

        poMaster = new SalesModels(poGRider).SalesInquiryMaster();
        poDetail = new SalesModels(poGRider).BankApplication();

        paMasterList = new ArrayList<>();
        paDetail = new ArrayList<>();

        return initialize();
    }

    public void setCompanyId(String companyId) {
        psCompanyId = companyId;
    }
    public void setIndustryId(String industryId) {
        psIndustryId = industryId;
    }
    public void setCategoryId(String categoryId) {
        psCategorCd = categoryId;
    }

    public JSONObject NewTransaction()
            throws CloneNotSupportedException {
        return newTransaction();
    }

    public JSONObject SaveTransaction()
            throws SQLException,
            GuanzonException,
            CloneNotSupportedException {
        return saveTransaction();
    }

    public JSONObject OpenTransaction(String transactionNo)
            throws CloneNotSupportedException,
            SQLException,
            GuanzonException {
        return openTransaction(transactionNo);
    }

    /**
     * Overide openTransaction base class to open detail for bank application
     * @param transactionNo
     * @return
     * @throws CloneNotSupportedException
     * @throws SQLException
     * @throws GuanzonException
     */
    protected JSONObject openTransaction(String transactionNo) throws CloneNotSupportedException, SQLException, GuanzonException {
        poJSON = poMaster.openRecord(transactionNo);
        if (!"success".equals((String)poJSON.get("result"))) {
            poJSON.put("message", "Unable to open transaction master record.");
            clear();
            return poJSON;
        } else {
            paDetail.clear();
            String sql = "SELECT sTransNox FROM " + poDetail.getTable()
                    + " WHERE sSourceNo = " + SQLUtil.toSQL(transactionNo)
                    + " AND sSourceCd = " + SQLUtil.toSQL("SInq")
                    + " ORDER BY sTransNox";
            ResultSet rs = poGRider.executeQuery(sql);

            while(rs.next()) {
                Model loDetail = (Model)poDetail.clone();
                loDetail.newRecord();
                poJSON = loDetail.openRecord(rs.getString("sTransNox"));
                if (!"success".equals((String)poJSON.get("result"))) {
                    poJSON.put("message", "Unable to open transaction detail record.");
                    clear();
                    return poJSON;
                }

                paDetail.add(loDetail);
            }

            pnEditMode = 1;
            pbRecordExist = true;
            poJSON = new JSONObject();
            poJSON.put("result", "success");
            return poJSON;
        }
    }

    public JSONObject UpdateTransaction() {
        return updateTransaction();
    }
    
    public JSONObject ApproveBankApplication(String remarks, List<Model_Bank_Application> faModel)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        
        poJSON = new JSONObject();

        String lsStatus = BankApplicationStatus.APPROVED;
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            if (faModel.get(lnCtr).getEditMode() != EditMode.READY) {
                poJSON.put("result", "error");
                poJSON.put("message", "No bank application loaded.");
                return poJSON;
            }

            if (lsStatus.equals(faModel.get(lnCtr).getTransactionStatus())) {
                poJSON.put("result", "error");
                poJSON.put("message", "Bank Application was already approved.");
                return poJSON;
            }

            poJSON = isEntryOkay(lsStatus, faModel.get(lnCtr));
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        poJSON = callApproval();
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
        
            poGRider.beginTrans("UPDATE STATUS", "ApproveTransaction", SOURCE_CODE, faModel.get(lnCtr).getTransactionNo());
            
            Model_Bank_Application loObject = new SalesModels(poGRider).BankApplication();
            loObject.initialize();
            
            poJSON = loObject.openRecord(faModel.get(lnCtr).getTransactionNo());
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
            poJSON = loObject.updateRecord();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }

            loObject.setApprovedDate(faModel.get(lnCtr).getApprovedDate());
            poJSON = loObject.saveRecord();
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
            //change status
            poJSON = statusChange(faModel.get(lnCtr).getTable(),faModel.get(lnCtr).getTransactionNo(),"", lsStatus, false,true);
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }

            poGRider.commitTrans();
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Bank Application approved successfully.");
        return poJSON;
    }
    
    public JSONObject DisapproveBankApplication(String remarks, List<Model_Bank_Application> faModel)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        
        poJSON = new JSONObject();

        String lsStatus = BankApplicationStatus.DISAPPROVED;
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            if (faModel.get(lnCtr).getEditMode() != EditMode.READY) {
                poJSON.put("result", "error");
                poJSON.put("message", "No bank application loaded.");
                return poJSON;
            }

            if (lsStatus.equals(faModel.get(lnCtr).getTransactionStatus())) {
                poJSON.put("result", "error");
                poJSON.put("message", "Bank Application was already disapprove.");
                return poJSON;
            }

            poJSON = isEntryOkay(lsStatus, faModel.get(lnCtr));
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        poJSON = callApproval();
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            //change status
            poJSON = statusChange(faModel.get(lnCtr).getTable(),faModel.get(lnCtr).getTransactionNo(),"", lsStatus, false);
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Bank Application disapproved successfully.");
        return poJSON;
    }
    
    public JSONObject CancelBankApplication(String remarks, List<Model_Bank_Application> faModel)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        
        poJSON = new JSONObject();

        String lsStatus = BankApplicationStatus.CANCELLED;
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            if (faModel.get(lnCtr).getEditMode() != EditMode.READY) {
                poJSON.put("result", "error");
                poJSON.put("message", "No bank application loaded.");
                return poJSON;
            }

            if (lsStatus.equals(faModel.get(lnCtr).getTransactionStatus())) {
                poJSON.put("result", "error");
                poJSON.put("message", "Bank Application was already cancelled.");
                return poJSON;
            }

            poJSON = isEntryOkay(lsStatus, faModel.get(lnCtr));
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        poJSON = callApproval();
        if (!isJSONSuccess(poJSON)) {
            return poJSON;
        }
        
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            //change status
            poJSON = statusChange(faModel.get(lnCtr).getTable(),faModel.get(lnCtr).getTransactionNo(),"", lsStatus, false);
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Bank Application disapproved successfully.");
        return poJSON;
    }
    
    public JSONObject VoidBankApplication(String remarks, List<Model_Bank_Application> faModel)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        
        poJSON = new JSONObject();

        String lsStatus = BankApplicationStatus.VOID;
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            if (faModel.get(lnCtr).getEditMode() != EditMode.READY) {
                poJSON.put("result", "error");
                poJSON.put("message", "No bank application loaded.");
                return poJSON;
            }

            if (lsStatus.equals(faModel.get(lnCtr).getTransactionStatus())) {
                poJSON.put("result", "error");
                poJSON.put("message", "Bank Application was already voided.");
                return poJSON;
            }

            poJSON = isEntryOkay(lsStatus, faModel.get(lnCtr));
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }
        
        for(int lnCtr = 0;lnCtr < faModel.size()-1;lnCtr++){
            //change status
            poJSON = statusChange(faModel.get(lnCtr).getTable(),faModel.get(lnCtr).getTransactionNo(),"", lsStatus, false);
            if (!isJSONSuccess(poJSON)) {
                return poJSON;
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Bank Application voided successfully.");
        return poJSON;
    }
    
    public JSONObject checkPendingBankApplication(){
        for(int lnRow = 0; lnRow <= getDetailCount() - 1; lnRow++){
            if(Detail(lnRow).getEditMode() == EditMode.UPDATE){
                if (Detail(lnRow).getTransactionStatus().equals(BankApplicationStatus.OPEN)) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "You have a pending bank application. Update the status before changing the purchase type.");
                    return poJSON;
                } 
            }
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }


    public JSONObject loadTransactionList(String client, String referenceNo) {
        try {
            if (client == null) {
                client = "";
            }
            if (referenceNo == null) {
                referenceNo = "";
            }

            String lsTransStat = "";
            if (psTranStat != null) {
                if (psTranStat.length() > 1) {
                    for (int lnCtr = 0; lnCtr <= psTranStat.length() - 1; lnCtr++) {
                        lsTransStat += ", " + SQLUtil.toSQL(Character.toString(psTranStat.charAt(lnCtr)));
                    }
                    lsTransStat = " AND a.cTranStat IN (" + lsTransStat.substring(2) + ")";
                } else {
                    lsTransStat = " AND a.cTranStat = " + SQLUtil.toSQL(psTranStat);
                }
            }

            initSQL();
            String lsSQL = MiscUtil.addCondition(SQL_BROWSE, " a.sIndstCdx = " + SQLUtil.toSQL(psIndustryId)
//                            + " AND a.sCategrCd = " + SQLUtil.toSQL(psCategorCd)
                            + " AND a.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())
                            + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL("%" + client)
                            + " AND a.sTransNox LIKE " + SQLUtil.toSQL("%" + referenceNo)
//                            + " AND a.cProcessd = '0' "
            );

            //If current user is an ordinary user load only its inquiries
//            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
//                lsSQL = MiscUtil.addCondition(lsSQL,
//                        " a.sSalesman = " + SQLUtil.toSQL(poGRider.getUserID()));
//            }

            if (lsTransStat != null && !"".equals(lsTransStat)) {
                lsSQL = lsSQL + lsTransStat;
            }

            lsSQL = lsSQL + " ORDER BY a.dTransact DESC ";

            System.out.println("Executing SQL: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            poJSON = new JSONObject();

            int lnctr = 0;

            if (MiscUtil.RecordCount(loRS) >= 0) {
                paMasterList = new ArrayList<>();
                while (loRS.next()) {
                    // Print the result set
                    System.out.println("sTransNox: " + loRS.getString("sTransNox"));
                    System.out.println("dTransact: " + loRS.getDate("dTransact"));
                    System.out.println("sCompnyNm: " + loRS.getString("sClientNm"));
                    System.out.println("------------------------------------------------------------------------------");

                    paMasterList.add(SalesInquiryMaster());
                    paMasterList.get(paMasterList.size() - 1).openRecord(loRS.getString("sTransNox"));
                    lnctr++;
                }

                System.out.println("Records found: " + lnctr);
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                paMasterList = new ArrayList<>();
                paMasterList.add(SalesInquiryMaster());
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

    private Model_Sales_Inquiry_Master SalesInquiryMaster() {
        return new SalesModels(poGRider).SalesInquiryMaster();
    }
    public int getSalesInquiryCount() {
        if (paMasterList == null) {
            paMasterList = new ArrayList<>();
        }

        return paMasterList.size();
    }

    public Model_Sales_Inquiry_Master SalesInquiryList(int row) {
        return (Model_Sales_Inquiry_Master) paMasterList.get(row);
    }


    /**
     * Search Bank
     * @param value
     * @param byCode
     * @param row
     * @return JSONObject success or error
     * @throws SQLException
     * @throws GuanzonException 
     */
    public JSONObject SearchBank(String value, boolean byCode, int row)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        poJSON.put("row", row);
        
        if(!(Master().getPurchaseType().equals(SalesInquiryStatic.PurchaseType.PO)
            || Master().getPurchaseType().equals(SalesInquiryStatic.PurchaseType.FINANCING))){
            poJSON.put("result", "error");
            poJSON.put("message", "Sales Inquiry purchase type must be PO or Financing.");
            return poJSON;
        }

        Banks object = new ParamControllers(poGRider, logwrapr).Banks();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        poJSON.put("row", row);
        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = checkExistingBank(object.getModel().getBankID(), row);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            Detail(row).setBankId(object.getModel().getBankID());
            Detail(row).setAppliedDate(poGRider.getServerDate());
        }
        
        System.out.println("Bank Name : " + Detail(row).Bank().getBankName());
        
        poJSON.put("row", row);
        return poJSON;
    }
    /**
     * Check Existing Bank
     * @param bankId
     * @param row
     * @return JSONObject success or error
     */
    private JSONObject checkExistingBank(String bankId, int row){
        poJSON = new JSONObject();
        
        for(int lnCtr = 0;lnCtr <= getDetailCount() - 1; lnCtr++){
            if(lnCtr != row){
                if(Detail(lnCtr).getTransactionStatus().equals(BankApplicationStatus.OPEN)
                    || Detail(lnCtr).getTransactionStatus().equals(BankApplicationStatus.APPROVED)){
                    
                    if(bankId.equals(Detail(lnCtr).getBankId())){
                        poJSON.put("result", "error");
                        poJSON.put("message", "Bank already exists in the table at row " + (lnCtr+1) + ".");
                        poJSON.put("row", lnCtr);
                        return poJSON;
                    }
                    
                }
            }
        }
        
        return poJSON;
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
            case BankApplicationStatus.VOID:
                return "Void";
            case BankApplicationStatus.APPROVED:
                return "Approved";
            case BankApplicationStatus.OPEN:
                return "Open";
            default:
                return "Unknown";
        }
    }
    /**
    * Returns the master record model for the current cash disbursement transaction.
    * 
    * @return The {@link Model_Sales_Inquiry_Master} instance representing the transaction header.
    */
    @Override
    public Model_Sales_Inquiry_Master Master() { 
        return (Model_Sales_Inquiry_Master) poMaster; 
    }
    /**
    * Returns the detail record model at the specified row index.
    * 
    * @param row The index of the detail row to retrieve.
    * @return The {@link Model_Bank_Application} instance for the given row.
    */
    @Override
    public Model_Bank_Application Detail(int row) {
        return (Model_Bank_Application) paDetail.get(row); 
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
            if ((Detail(getDetailCount() - 1).getBankId() == null || "".equals(Detail(getDetailCount() - 1).getBankId()))
                && Detail(getDetailCount() - 1).getApplicationNo() == null || "".equals(Detail(getDetailCount() - 1).getApplicationNo())){
                poJSON = new JSONObject();
                poJSON = setJSON("error", "Last row has empty item.");
                return poJSON;
            }
        }

        return addDetail();
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
            if ((Detail(lnCtr).getBankId() == null || "".equals(Detail(lnCtr).getBankId()))
                && (Detail(lnCtr).getApplicationNo() == null || "".equals(Detail(lnCtr).getApplicationNo()))) {
                deleteDetail(lnCtr);
            } 
            lnCtr--;
        }
            
        if ((getDetailCount() - 1) >= 0) {
            if (
                (Detail(getDetailCount() - 1).getBankId() != null && !"".equals(Detail(getDetailCount() - 1).getBankId()))
                && (Detail(lnCtr).getApplicationNo() != null && !"".equals(Detail(lnCtr).getApplicationNo()))
                ) {
                AddDetail();
            }
        }

        if ((getDetailCount() - 1) < 0) {
            AddDetail();
        }
    }
    
    @Override
    public JSONObject willSave()
            throws SQLException,
            GuanzonException,
            CloneNotSupportedException {
        /*Put system validations and other assignments here*/
        poJSON = new JSONObject();
        
        Master().setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        Master().setModifiedDate(poGRider.getServerDate());

        Iterator<Model> detail = Detail().iterator();
        while (detail.hasNext()) {
            Model item = detail.next();
            if ((item.getValue("sBankIDxx") == null || "".equals(item.getValue("sBankIDxx")))) {
                detail.remove();
            }
        }
        //Validate detail after removing all zero qty and empty stock Id
        if (getDetailCount() <= 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction detail to be save.");
            return poJSON;
        }
        
        for(int lnRow = 0; lnRow <= getDetailCount() - 1; lnRow++){
            Detail(lnRow).setTransactionNo(Master().getTransactionNo());
            Detail(lnRow).setEntryNo(lnRow+1);
            
            poJSON = isEntryOkay(Detail(lnRow).getTransactionStatus(), Detail(lnRow));
            if (!"success".equals((String) poJSON.get("result"))) {
                poJSON.put("result", "error");
                return poJSON;
            } 
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public JSONObject save() throws CloneNotSupportedException, SQLException, GuanzonException {
        /*Put saving business rules here*/
        return isEntryOkay(SalesInquiryStatic.OPEN);
    }
    
    @Override
    public void saveComplete() {
        /*This procedure was called when saving was complete*/
        System.out.println("Transaction saved successfully.");
    }
    
    @Override
    public JSONObject saveOthers() {
        /*Only modify this if there are other tables to modify except the master and detail tables*/
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    

    @Override
    public JSONObject initFields() {
        try {
            /*Put initial model values here*/
            poJSON = new JSONObject();
            System.out.println("Dept ID : " + poGRider.getDepartment());
            System.out.println("Current User : " + poGRider.getUserID());
            
            Master().setBranchCode(poGRider.getBranchCode());
            Master().setIndustryId(psIndustryId);
            Master().setCompanyId(psCompanyId);
            Master().setCategoryCode(psCategorCd);
            Master().setTransactionDate(poGRider.getServerDate());

        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON.put("result", "error");
            poJSON.put("message", MiscUtil.getException(ex));
            return poJSON;
        }

        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject isEntryOkay(String status, Model_Bank_Application master) {
        GValidator loValidator = new BankApplication();

        loValidator.setApplicationDriver(poGRider);
        loValidator.setTransactionStatus(status);
        loValidator.setMaster(master);

        poJSON = loValidator.validate();

        return poJSON;
    }

    @Override
    public void initSQL() {
        SQL_BROWSE =  " SELECT "
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
                + " FROM sales_inquiry_master a "
                + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "
                + " LEFT JOIN salesman c ON c.sEmployID = a.sSalesman "
                + " LEFT JOIN client_master d ON d.sClientID = a.sAgentIDx "
                + " LEFT JOIN branch e ON e.sBranchCd = a.sBranchCd "
                + " LEFT JOIN company f ON f.sCompnyID = a.sCompnyID "
                + " LEFT JOIN industry g ON g.sIndstCdx = a.sIndstCdx " ;

    }
    
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
    
    
}
