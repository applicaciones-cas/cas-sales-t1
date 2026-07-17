/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1;

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
import org.guanzon.cas.parameter.*;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.cashflow.status.CashDisbursementStatus;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Bank_Application;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Giveaways_Item;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Giveaways_Master;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Requirements;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;
import ph.com.guanzongroup.cas.sales.t1.status.SalesGiveawaysStatus;
import ph.com.guanzongroup.cas.sales.t1.status.SalesGiveawaysStatus;
import ph.com.guanzongroup.cas.sales.t1.validator.BankApplication;
import ph.com.guanzongroup.cas.sales.t1.validator.SalesInquiryRequirements;
import ph.com.guanzongroup.cas.sales.t1.validator.SalesInquiryValidatorFactory;

import javax.sql.rowset.CachedRowSet;
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

/**
 *
 * @author Arsiela 07-17-2026
 */
public class SalesGiveaways extends Transaction {
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategorCd = "";
    private String psApprover = "";
    
    public JSONObject InitTransaction() {
        SOURCE_CODE = "PARM";

        poMaster = new SalesModels(poGRider).SalesGiveawaysMaster();
        poDetail = new SalesModels(poGRider).SalesGiveawaysItem();

        paDetail = new ArrayList<>();

        return initialize();
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
        //Clear data
        resetMaster();
        Detail().clear();
        return openTransaction(transactionNo);
    }

    public JSONObject UpdateTransaction() {
        return updateTransaction();
    }

    public String getStatus(String fsStatus) {
        switch (fsStatus){
            case SalesGiveawaysStatus.OPEN:
                return "OPEN";
            case SalesGiveawaysStatus.ACTIVE:
                return "ACTIVE";
            case SalesGiveawaysStatus.DEACTIVATE:
                return "INACTIVE";
            case SalesGiveawaysStatus.DISAPPROVE:
                return "DISAPPROVE";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Requests approval if the current user lacks sufficient rights and
     * validates the approving officer.
     *
     * @return result as a {@link JSONObject}
     */
    public JSONObject callApproval(){
        poJSON = new JSONObject();
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                poJSON.put("result", "error");
                poJSON.put("message", "User is not an authorized approving officer.");
                return poJSON;
            }
            setApproving((String) poJSON.get("sUserIDxx"));
            psApprover = (String) poJSON.get("sUserIDxx");
        }

        poJSON.put("result", "success");
        poJSON.put("message", "success");
        return poJSON;
    }

    /**
     * Confirm the transaction
     * @param remarks
     * @return JSONObject success or error
     * @throws ParseException
     * @throws SQLException
     * @throws GuanzonException
     * @throws CloneNotSupportedException 
     */
    public JSONObject ActivateTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesGiveawaysStatus.ACTIVE;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already confirmed.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(lsStatus);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        
        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, false);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Record confirmed successfully.");
        return poJSON;
    }

    /**
     * Cancel the transaction
     * @param remarks
     * @return JSONObject success or error
     * @throws ParseException
     * @throws SQLException
     * @throws GuanzonException
     * @throws CloneNotSupportedException 
     */
    public JSONObject DeactiveTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesGiveawaysStatus.DEACTIVATE;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already deactivated.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(SalesGiveawaysStatus.DEACTIVATE);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, false);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Record deactivate successfully.");
        return poJSON;
    }
    
    /**
     * Void the transaction
     * @param remarks
     * @return JSONObject success or error
     * @throws ParseException
     * @throws SQLException
     * @throws GuanzonException
     * @throws CloneNotSupportedException 
     */
    public JSONObject DisapproveTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesGiveawaysStatus.DISAPPROVE;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already voided.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(SalesGiveawaysStatus.DISAPPROVE);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, false);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "Record voided successfully.");
        return poJSON;
    }

    /**
     * Search Record
     * @return
     * @throws CloneNotSupportedException
     * @throws SQLException
     * @throws GuanzonException 
     */
    public JSONObject searchRecord()
            throws CloneNotSupportedException,
            SQLException,
            GuanzonException {
        poJSON = new JSONObject();
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
                                            + " AND a.sCategrCd = " + SQLUtil.toSQL(psCategorCd));
//                                            + " AND a.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())

        if (lsTransStat != null && !"".equals(lsTransStat)) {
            lsSQL = lsSQL + lsTransStat;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Giveaway Code»Description»From Date»Thru Date",
                "sGAWayCde»sGAWayDsc»dFromDate»dThruDate",
                "a.sGAWayCde»a.sGAWayDsc»a.dFromDate»a.dThruDate",
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

    /**
     * Search Record
     * @return
     * @throws CloneNotSupportedException
     * @throws SQLException
     * @throws GuanzonException
     */
    public JSONObject searchRecord(String value, boolean byCode)
            throws CloneNotSupportedException,
            SQLException,
            GuanzonException {
        poJSON = new JSONObject();
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
                + " AND a.sCategrCd = " + SQLUtil.toSQL(psCategorCd));
//                                            + " AND a.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())

        if (lsTransStat != null && !"".equals(lsTransStat)) {
            lsSQL = lsSQL + lsTransStat;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Giveaway Code»Description»From Date»Thru Date",
                "sGAWayCde»sGAWayDsc»dFromDate»dThruDate",
                "a.sGAWayCde»a.sGAWayDsc»a.dFromDate»a.dThruDate",
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

    public JSONObject SearchCategory(String value, boolean byCode ) throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        Category object = new ParamControllers(poGRider, logwrapr).Category();
        object.setRecordStatus(RecordStatus.ACTIVE);

        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            System.out.println("Category ID: " + object.getModel().getCategoryId());
            System.out.println("Description " + object.getModel().getDescription());
            Master().setCategoryCode(object.getModel().getCategoryId());
        }
        return poJSON;
    }
    /**
     * Search Inventory
     * @param value
     * @param byCode
     * @param row
     * @return JSONObject success or error
     * @throws SQLException
     * @throws GuanzonException 
     */
    public JSONObject SearchInventory(String value, boolean byCode, int row) throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        if(Master().getCategoryCode()== null || "".equals(Master().getCategoryCode())){
            poJSON.put("result", "error");
            poJSON.put("message", "Category is not set.");
            return poJSON;
        }
        Inventory object = new InvControllers(poGRider, logwrapr).Inventory();
        String lsSQL = MiscUtil.addCondition(object.getSQ_Browse(), 
                                            // " a.cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE)
                                            " a.sCategCd1 = " + SQLUtil.toSQL(Master().getCategoryCode())
                                            + " AND a.sIndstCdx = " + SQLUtil.toSQL(Master().getIndustryId())
                                            );
        
        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Barcode»Description»Brand»Model»Variant»UOM",
                "sBarCodex»sDescript»xBrandNme»xModelNme»xVrntName»xMeasurNm",
                "a.sBarCodex»a.sDescript»IFNULL(b.sDescript, '')»IFNULL(c.sDescript, '')»IFNULL(f.sDescript, '')»IFNULL(e.sDescript, '')",
                byCode ? 0 : 1);

        if (poJSON != null) {
            poJSON = object.getModel().openRecord((String) poJSON.get("sStockIDx"));
            if ("success".equals((String) poJSON.get("result"))) {
                poJSON = checkExistingDetail(row,
                        object.getModel().getStockId() 
                        );
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }

                Detail(row).setStockId(object.getModel().getStockId());
            }
            
            System.out.println("Barcode : " + Detail(row).Inventory().getBarCode());
            System.out.println("Description : " + Detail(row).Inventory().getDescription());
            
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }
        
        return poJSON;
    }

    /**
     * Check Existing product in detail
     * @param row
     * @param stockId
     * @return 
     */
    private JSONObject checkExistingDetail(int row, String stockId){
        poJSON = new JSONObject();
        poJSON.put("row", row);

        stockId = (stockId == null) ? "" : stockId;
        
        for (int lnCtr = 0; lnCtr <= getDetailCount()- 1; lnCtr++) {
            if (lnCtr != row) {
                //Check Existing Stock ID
                if(!"".equals(stockId)){
                    if(stockId.equals(Detail(lnCtr).getStockId())){
                        poJSON.put("result", "error");
                        poJSON.put("message", "Item Description already exists in the transaction detail at row "+(lnCtr+1)+".");
                        poJSON.put("row", lnCtr);
                        return poJSON;
                    }
                }
            }
        }
        
        return poJSON;
    }

    /*Convert Date to String*/
    private static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    private LocalDate strToDate(String val) {
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(val, date_formatter);
        return localDate;
    }

    public void setIndustryId(String industryId) {
        psIndustryId = industryId;
    }

    public void setCompanyId(String companyId) {
        psCompanyId = companyId;
    }

    public void setCategoryId(String categoryId) {
        psCategorCd = categoryId;
    }

    @Override
    public String getSourceCode() {
        return SOURCE_CODE;
    }

    @Override
    public Model_Sales_Giveaways_Master Master() {
        return (Model_Sales_Giveaways_Master) poMaster;
    }

    @Override
    public Model_Sales_Giveaways_Item Detail(int row) {
        return (Model_Sales_Giveaways_Item) paDetail.get(row);
    }

    public Model_Sales_Giveaways_Item getDetail() {
        return (Model_Sales_Giveaways_Item) poDetail;
    }

    @Override
    public int getDetailCount() {
        if (paDetail == null) {
            paDetail = new ArrayList<>();
        }

        return paDetail.size();
    }

    public JSONObject AddDetail()
            throws CloneNotSupportedException {
        poJSON = new JSONObject();

        if (getDetailCount() > 0) {
            if (Detail(getDetailCount() - 1).getStockId() != null) {
                if (Detail(getDetailCount() - 1).getStockId().isEmpty()) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Last row has empty item.");
                    return poJSON;
                }
            }
        }

        return addDetail();
    }

    public void resetMaster() {
        poMaster = new SalesModels(poGRider).SalesGiveawaysMaster();
    }
    /**
     * Reload Detail for adding or deleting detail
     * @throws CloneNotSupportedException
     */
    public void ReloadDetail() throws CloneNotSupportedException, SQLException{
        int lnCtr = getDetailCount() - 1;
        while (lnCtr >= 0) {
            if ((Detail(lnCtr).getStockId() == null || "".equals(Detail(lnCtr).getStockId()))) {
                Detail().remove(lnCtr);
            }
            lnCtr--;
        }

        if ((getDetailCount() - 1) >= 0) {
            if (Detail(getDetailCount() - 1).getStockId() != null && !"".equals(Detail(getDetailCount() - 1).getStockId())) {
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

        if(Master().getEditMode() == EditMode.ADDNEW){
            System.out.println("Will Save : " + Master().getNextCode());
            Master().setGiveawayCode(Master().getNextCode());
            Master().setEntryBy(poGRider.Encrypt(poGRider.getUserID()));
            Master().setEntryDate(poGRider.getServerDate());
        }

        Master().setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        Master().setModifiedDate(poGRider.getServerDate());

        Iterator<Model> detail = Detail().iterator();
        while (detail.hasNext()) {
            Model item = detail.next();
            if (item.getValue("sStockIDx") == null || "".equals(item.getValue("sStockIDx"))) {
                detail.remove();
            }
        }

        //Validate detail after removing all zero qty and empty stock Id
        if (getDetailCount() <= 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction detail to be save.");
            return poJSON;
        }
        
        for (int lnCtr = 0; lnCtr <= getDetailCount() - 1; lnCtr++) {
            Detail(lnCtr).setGiveawayCode(Master().getGiveawayCode());
            Detail(lnCtr).setEntryNo(lnCtr + 1);
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public JSONObject save() {
        /*Put saving business rules here*/
        return isEntryOkay(SalesGiveawaysStatus.OPEN);
    }
    
    @Override
    public void saveComplete() {
        /*This procedure was called when saving was complete*/
        System.out.println("Record saved successfully.");
    }

    @Override
    public JSONObject initFields() {
        try {
            /*Put initial model values here*/
            poJSON = new JSONObject();
            System.out.println("Dept ID : " + poGRider.getDepartment());
            System.out.println("Current User : " + poGRider.getUserID());
            Master().setIndustryId(psIndustryId);
            Master().setCategoryCode(psCategorCd);
            Master().setTransactionStatus(SalesGiveawaysStatus.OPEN);
            
            LocalDate currentDate = strToDate(xsDateShort(poGRider.getServerDate())).plusMonths(1);
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
            Master().setFromDate(SQLUtil.toDate(formattedDate, SQLUtil.FORMAT_SHORT_DATE));
            Master().setThruDate(SQLUtil.toDate(formattedDate, SQLUtil.FORMAT_SHORT_DATE));
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON.put("result", "error");
            poJSON.put("message", MiscUtil.getException(ex));
            return poJSON;
        }

        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    protected JSONObject isEntryOkay(String status) {
        poJSON = new JSONObject();
        if (Master().getGiveawayCode() == null || "".equals(Master().getGiveawayCode())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Giveaway code must not be empty.");
            return poJSON;
        }

        if (Master().getDescription() == null || "".equals(Master().getDescription())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Description must not be empty.");
            return poJSON;
        }
        if (Master().getIndustryId() == null || "".equals(Master().getIndustryId())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Industry must not be empty.");
            return poJSON;
        }
        if (Master().getCategoryCode() == null || "".equals(Master().getCategoryCode())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Category must not be empty.");
            return poJSON;
        }
        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public void initSQL() {
        SQL_BROWSE =  "SELECT " +
                    "  a.sGAWayCde, " +
                    "  a.sGAWayDsc, " +
                    "  a.sIndstCdx, " +
                    "  a.sCategrCd, " +
                    "  a.sRemarksx, " +
                    "  a.dFromDate, " +
                    "  a.dThruDate, " +
                    "  a.cTranStat, " +
                    "  a.sEntryByx, " +
                    "  a.dEntryDte, " +
                    "  a.sModified, " +
                    "  a.dModified, " +
                    "  b.sDescript as Industry, " +
                    "  c.sDescript AS Category " +
                    "FROM Sales_Giveaways_Master a " +
                    "LEFT JOIN Industry b ON b.sIndstCdx = a.sIndstCdx " +
                    "LEFT JOIN Category c ON c.sCategrCd = a.sCategrCd ";
        
    }

    /**
     * Displays the status history of a Cash Advance transaction.
     *
     * Retrieves status records, maps status codes to readable text, and
     * shows them in the UI along with entry details.
     *
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if application-specific error occurs
     * @throws Exception for other unexpected errors
     */
    public void ShowStatusHistory() throws SQLException, GuanzonException, Exception {
        CachedRowSet crs = getStatusHistory();

        crs.beforeFirst();

        while(crs.next()){
            switch (crs.getString("cRefrStat")){
                case "":
                    crs.updateString("cRefrStat", "-");
                    break;
                case SalesGiveawaysStatus.OPEN:
                    crs.updateString("cRefrStat", "OPEN");
                    break;
                case SalesGiveawaysStatus.DISAPPROVE:
                    crs.updateString("cRefrStat", "DISAPPROVED");
                    break;
                case SalesGiveawaysStatus.ACTIVE:
                    crs.updateString("cRefrStat", "ACTIVE");
                    break;
                case SalesGiveawaysStatus.DEACTIVATE:
                    crs.updateString("cRefrStat", "INACTIVE");
                    break;
                default:
                    char ch = crs.getString("cRefrStat").charAt(0);
                    String stat = String.valueOf((int) ch - 64);

                    switch (stat){
                        case SalesGiveawaysStatus.OPEN:
                            crs.updateString("cRefrStat", "OPEN");
                            break;
                        case SalesGiveawaysStatus.DISAPPROVE:
                            crs.updateString("cRefrStat", "DISAPPROVED");
                            break;
                        case SalesGiveawaysStatus.ACTIVE:
                            crs.updateString("cRefrStat", "ACTIVE");
                            break;
                        case SalesGiveawaysStatus.DEACTIVATE:
                            crs.updateString("cRefrStat", "INACTIVE");
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

        showStatusHistoryUI("Giveaways", (String) poMaster.getValue("sTransNox"), entryBy, entryDate, crs);
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
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox =  " + SQLUtil.toSQL(Master().getGiveawayCode()));
        lsSQL = lsSQL + " ORDER BY b.dModified DESC ";
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    if (loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))) {
                        if (loRS.getString("sModified").length() > 10) {
                            lsEntry = getSysUser(poGRider.Decrypt(loRS.getString("sModified")));
                        } else {
                            lsEntry = getSysUser(loRS.getString("sModified"));
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
    /**
     * Retrieves the company name of a system user based on user ID.
     *
     * @param fsId User ID to lookup
     * @return Company name of the user
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if application-specific error occurs
     */
    public String getSysUser(String fsId) throws SQLException, GuanzonException {
        String lsEntry = "";
        String lsSQL = " SELECT b.sCompnyNm from xxxSysUser a "
                + " LEFT JOIN Client_Master b ON b.sClientID = a.sEmployNo ";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sUserIDxx =  " + SQLUtil.toSQL(fsId));
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            if (MiscUtil.RecordCount(loRS) > 0L) {
                if (loRS.next()) {
                    lsEntry = loRS.getString("sCompnyNm");
                }
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON = setJSON("error", e.getMessage());
        }
        return lsEntry;
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
