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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
import org.guanzon.cas.client.Client;
import org.guanzon.cas.client.services.ClientControllers;
import org.guanzon.cas.inv.Inventory;
import org.guanzon.cas.inv.services.InvControllers;
import org.guanzon.cas.parameter.Brand;
import org.guanzon.cas.parameter.Color;
//import org.guanzon.cas.parameter.Model;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Detail;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;
import ph.com.guanzongroup.cas.sales.t1.validator.SalesInquiryValidatorFactory;

/**
 *
 * @author Arsiela
 */
public class SalesInquiry extends Transaction {
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategorCd = "";
    
    List<Model_Sales_Inquiry_Master> paMasterList;
    List<Model> paDetailRemoved;
    
    public JSONObject InitTransaction() {
        SOURCE_CODE = "SInq";

        poMaster = new SalesModels(poGRider).SalesInquiryMaster();
        poDetail = new SalesModels(poGRider).SalesInquiryDetails();

        paMasterList = new ArrayList<>();
        paDetail = new ArrayList<>();
        paDetailRemoved = new ArrayList<>();

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

    public JSONObject ConfirmTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesInquiryStatic.CONFIRMED;
        boolean lbConfirm = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already confirmed.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(lsStatus);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);
            if (!"success".equals((String) poJSON.get("result"))) {
                return poJSON;
            } else {
                if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                    poJSON.put("result", "error");
                    poJSON.put("message", "User is not an authorized approving officer.");
                    return poJSON;
                }
            }
        }
        
        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, !lbConfirm);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        if (lbConfirm) {
            poJSON.put("message", "Transaction confirmed successfully.");
        } else {
            poJSON.put("message", "Transaction confirmation request submitted successfully.");
        }

        return poJSON;
    }

    public JSONObject PaidTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesInquiryStatic.PAID;
        boolean lbPaid = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already paid.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(lsStatus);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, !lbPaid);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        if (lbPaid) {
            poJSON.put("message", "Transaction paid successfully.");
        } else {
            poJSON.put("message", "Transaction paid request submitted successfully.");
        }

        return poJSON;
    }

    public JSONObject ProcessTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesInquiryStatic.PROCESSED;
        boolean lbProcess = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already processed.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(SalesInquiryStatic.PROCESSED);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, !lbProcess);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        if (lbProcess) {
            poJSON.put("message", "Transaction processed successfully.");
        } else {
            poJSON.put("message", "Transaction process request submitted successfully.");
        }

        return poJSON;
    }

    public JSONObject CancelTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesInquiryStatic.CANCELLED;
        boolean lbCancelled = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already cancelled.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(SalesInquiryStatic.CANCELLED);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        if (SalesInquiryStatic.CONFIRMED.equals(Master().getTransactionStatus())) {
            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if (!"success".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                        poJSON.put("result", "error");
                        poJSON.put("message", "User is not an authorized approving officer.");
                        return poJSON;
                    }
                }
            }
        }

        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, !lbCancelled);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        if (lbCancelled) {
            poJSON.put("message", "Transaction cancelled successfully.");
        } else {
            poJSON.put("message", "Transaction cancellation request submitted successfully.");
        }

        return poJSON;
    }

    public JSONObject VoidTransaction(String remarks)
            throws ParseException,
            SQLException,
            GuanzonException,
            CloneNotSupportedException {
        poJSON = new JSONObject();

        String lsStatus = SalesInquiryStatic.VOID;
        boolean lbVoid = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transacton was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already voided.");
            return poJSON;
        }

        //validator
        poJSON = isEntryOkay(SalesInquiryStatic.VOID);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        if (SalesInquiryStatic.CONFIRMED.equals(Master().getTransactionStatus())) {
            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if (!"success".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                        poJSON.put("result", "error");
                        poJSON.put("message", "User is not an authorized approving officer.");
                        return poJSON;
                    }
                }
            }
        }
        
        //change status
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sTransNox"), remarks, lsStatus, !lbVoid);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        if (lbVoid) {
            poJSON.put("message", "Transaction voided successfully.");
        } else {
            poJSON.put("message", "Transaction voiding request submitted successfully.");
        }

        return poJSON;
    }
    
    public JSONObject searchTransaction()
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
        String lsSQL = MiscUtil.addCondition(SQL_BROWSE, " a.sIndstCdx = " + SQLUtil.toSQL(psIndustryId));
        //If current user is an ordinary user load only its inquiries
//        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
//            if(psCategorCd.equals(SalesInquiryStatic.CATEGORY_CAR)){
//                lsSQL = MiscUtil.addCondition(lsSQL, 
//                        " a.sSalesman = " + SQLUtil.toSQL(poGRider.getUserID()));
//            }
//        }
        
        if (lsTransStat != null && !"".equals(lsTransStat)) {
            lsSQL = lsSQL + lsTransStat;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Transaction Date»Transaction No»Client",
                "dTransact»sTransNox»sClientNm",
                "a.dTransact»a.sTransNox»b.sCompnyNm",
                1);

        if (poJSON != null) {
            return OpenTransaction((String) poJSON.get("sTransNox"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject searchTransaction(String fsClient, String fsTransNo)
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
        String lsSQL = MiscUtil.addCondition(SQL_BROWSE, 
                   " a.sIndstCdx = " + SQLUtil.toSQL(psIndustryId)
                 + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL("%"+fsClient)
                 + " AND a.sTransNox LIKE " + SQLUtil.toSQL("%"+fsTransNo));
        
        //If current user is an ordinary user load only its inquiries
//        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
//            if(psCategorCd.equals(SalesInquiryStatic.CATEGORY_CAR)){
//                lsSQL = MiscUtil.addCondition(lsSQL, 
//                        " a.sSalesman = " + SQLUtil.toSQL(poGRider.getUserID()));
//            }
//        }
        
        if (lsTransStat != null && !"".equals(lsTransStat)) {
            lsSQL = lsSQL + lsTransStat;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Transaction Date»Transaction No»Client",
                "dTransact»sTransNox»sClientNm",
                "a.dTransact»a.sTransNox»b.sCompnyNm",
                1);

        if (poJSON != null) {
            return OpenTransaction((String) poJSON.get("sTransNox"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    public JSONObject searchTransaction(String fsIndustry, String fsCompany, String fsCategory, String fsClient, String fsTransNo)
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
        String lsSQL = MiscUtil.addCondition(SQL_BROWSE, 
                   " a.sIndstCdx = " + SQLUtil.toSQL(fsIndustry)
                 + " AND a.sCompnyID = " + SQLUtil.toSQL(fsCompany)
                 + " AND a.sCategrCd = " + SQLUtil.toSQL(fsCategory)
                 + " AND a.sBranchCd = " + SQLUtil.toSQL(poGRider.getBranchCode())
                 + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL("%"+fsClient)
                 + " AND a.sTransNox LIKE " + SQLUtil.toSQL("%"+fsTransNo));
        
        //If current user is an ordinary user load only its inquiries
//        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
//            if(psCategorCd.equals(SalesInquiryStatic.CATEGORY_CAR)){
//                lsSQL = MiscUtil.addCondition(lsSQL, 
//                        " a.sSalesman = " + SQLUtil.toSQL(poGRider.getUserID()));
//            }
//        }
        
        if (lsTransStat != null && !"".equals(lsTransStat)) {
            lsSQL = lsSQL + lsTransStat;
        }

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                "",
                "Transaction Date»Transaction No»Client",
                "dTransact»sTransNox»sClientNm",
                "a.dTransact»a.sTransNox»b.sCompnyNm",
                1);

        if (poJSON != null) {
            return OpenTransaction((String) poJSON.get("sTransNox"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    
    /*Search Master References*/
    public JSONObject SearchClient(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Client object = new ClientControllers(poGRider, logwrapr).Client();
        object.Master().setRecordStatus(RecordStatus.ACTIVE);
        object.Master().setClientType(Master().getClientType());
        poJSON = object.Master().searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            Master().setClientId(object.Master().getModel().getClientId());
            Master().setAddressId("address"); //TODO
            Master().setContactId("contact"); //TODO
        }

        return poJSON;
    }
    
    public JSONObject SearchReferralAgent(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Client object = new ClientControllers(poGRider, logwrapr).Client();
        object.Master().setRecordStatus(RecordStatus.ACTIVE);
//        object.Master().setClientType(Master().getClientType());
        poJSON = object.Master().searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            Master().setAgentId(object.Master().getModel().getClientId());
        }

        return poJSON;
    }
    
    public JSONObject SearchSalesPerson(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Client object = new ClientControllers(poGRider, logwrapr).Client();
        object.Master().setRecordStatus(RecordStatus.ACTIVE);
//        object.Master().setClientType(Master().getClientType());
        poJSON = object.Master().searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            Master().setSalesMan(object.Master().getModel().getClientId());
        }

        return poJSON;
    }
    
    public JSONObject SearchBrand(String value, boolean byCode, int row)
            throws ExceptionInInitializerError,
            SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        if(Master().getClientId()== null || "".equals(Master().getClientId())){
            poJSON.put("result", "error");
            poJSON.put("message", "Client is not set.");
            return poJSON;
        }
        
        poJSON = checkMaximumInqDetail();
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;
        }
        
        Brand object = new ParamControllers(poGRider, logwrapr).Brand();
        object.setRecordStatus(RecordStatus.ACTIVE);

        poJSON = object.searchRecord(value, byCode, Master().getIndustryId());
        if ("success".equals((String) poJSON.get("result"))) {
            if (!object.getModel().getBrandId().equals(Detail(row).getBrandId())) {
                Detail(row).setStockId("");
            }

            Detail(row).setBrandId(object.getModel().getBrandId());
            Detail(row).setModelId("");
            Detail(row).setColorId("");
        }
        return poJSON;
    }

    public JSONObject SearchModel(String value, boolean byCode, int row)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        poJSON.put("row", row);

        if(Detail(row).getBrandId() == null || "".equals(Detail(row).getBrandId())){
            poJSON.put("result", "error");
            poJSON.put("message", "Brand is not set.");
            return poJSON;
        }
        
        Inventory object = new InvControllers(poGRider, logwrapr).Inventory();
        object.setRecordStatus(RecordStatus.ACTIVE);
        System.out.println("Brand ID : "  + Detail(row).getBrandId());;
        poJSON = object.searchRecordOfVariants(value, byCode);
        poJSON.put("row", row);
        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = checkExistingDetail(row,object.getModel().getModelId(),"",object.getModel().getStockId() );
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            
            Detail(row).setModelId(object.getModel().getModelId());
            Detail(row).setColorId("");
            Detail(row).setColorId(object.getModel().getStockId());
            
            System.out.println("StockID : " + Detail(row).Inventory().getStockId());
            System.out.println("Model  : " + Detail(row).Inventory().Model().getDescription());
        }

        return poJSON;
    }
    
    
    public JSONObject SearchColor(String value, boolean byCode, int row)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        poJSON.put("row", row);

        if(Detail(row).getModelId()== null || "".equals(Detail(row).getModelId())){
            poJSON.put("result", "error");
            poJSON.put("message", "Model is not set.");
            return poJSON;
        }
        
        Color object = new ParamControllers(poGRider, logwrapr).Color();
        object.setRecordStatus(RecordStatus.ACTIVE);
        System.out.println("Model ID : "  + Detail(row).getModelId());
        poJSON = object.searchRecord(value, byCode);
        poJSON.put("row", row);
        if ("success".equals((String) poJSON.get("result"))) {
           
            poJSON = checkExistingDetail(row,Detail(row).getModelId(),object.getModel().getColorId(),Detail(row).getStockId() );
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            Detail(row).setColorId(object.getModel().getColorId());
            
            //Check for existing stock ID
            
            System.out.println("StockID : " + Detail(row).Inventory().getStockId());
            System.out.println("Color  : " + Detail(row).Color().getDescription());
        }

        return poJSON;
    }
    
    public JSONObject SearchInventory(String value, boolean byCode, int row)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        poJSON.put("row", row);
        Inventory object = new InvControllers(poGRider, logwrapr).Inventory();
        object.setRecordStatus(RecordStatus.ACTIVE);
        
        poJSON = object.searchRecord(value, byCode, 
                null,
                null, 
                Master().getIndustryId(),  
                Master().getCategoryCode());
        
        poJSON.put("row", row);
        System.out.println("result" + (String) poJSON.get("result"));
        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = checkExistingDetail(row,object.getModel().getModelId(),object.getModel().getColorId(),object.getModel().getStockId() );
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            Detail(row).setStockId(object.getModel().getStockId());
            Detail(row).setModelId(object.getModel().getModelId());
            Detail(row).setColorId(object.getModel().getColorId());
        }
        
        System.out.println("StockID : " + Detail(row).Inventory().getStockId());
        System.out.println("Description  : " + Detail(row).Inventory().getDescription());
        return poJSON;
    }
    
    private JSONObject checkExistingDetail(int row,String modelId, String colorId, String stockId){
        poJSON = new JSONObject();
        poJSON.put("row", row);
        
        for (int lnCtr = 0; lnCtr <= getDetailCount()- 1; lnCtr++) {
            if (lnCtr != row) {
                //Check Existing Stock ID
                if(stockId != null && !"".equals(stockId)){
                    if(stockId.equals(Detail(lnCtr).getStockId())){
                        poJSON.put("result", "error");
                        poJSON.put("message", "Unit Description already exists in the transaction detail.");
                        poJSON.put("row", lnCtr);
                        return poJSON;
                    }
                }
                
                //Check Existing Brand and Model
                if(Detail(lnCtr).getBrandId().equals(Detail(row).getBrandId())
                    && Detail(lnCtr).getModelId().equals(modelId)){
                    
                    //Check if there is brand and model without color Id
                    if(Detail(lnCtr).getColorId() == null || "".equals(Detail(lnCtr).getColorId())){
                        poJSON.put("result", "error");
                        poJSON.put("message", "Brand and model already exists without color.");
                        poJSON.put("row", lnCtr);
                        return poJSON;
                    }
                    
                    //Check if brand, model and color already exists in the transaction detail
                    if(Detail(lnCtr).getBrandId().equals(Detail(row).getBrandId())
                        && Detail(lnCtr).getModelId().equals(modelId)
                        && Detail(lnCtr).getColorId().equals(colorId)){
                        poJSON.put("result", "error");
                        poJSON.put("message", "Unit Description already exists in the transaction detail.");
                        poJSON.put("row", lnCtr);
                        return poJSON;
                    }
                }
                
            }
        }
        
        return poJSON;
    }
    
    public JSONObject checkMaximumInqDetail(){
        poJSON = new JSONObject();
        //Check if client type is corporate allow only 5 inquiry detail
        if(Master().getCategoryCode().equals(SalesInquiryStatic.CATEGORY_CAR)
                || Master().getCategoryCode().equals(SalesInquiryStatic.CATEGORY_MC)
                || Master().getCategoryCode().equals(SalesInquiryStatic.CATEGORY_APPLIANCES)){
            
            //TODO Corporate
            if(Master().getClientType().equals("1")){
                if(getDetailCount() > 5){
                    poJSON.put("result", "error");
                    poJSON.put("message", "You can only inquire up to 5 items for corporate client.");
                    return poJSON;
                }
            }
        }
        return poJSON;
    }
    
    public JSONObject loadSalesInquiry(String industryId, String client, String referenceNo) {
        try {
            if (industryId == null) {
                industryId = psIndustryId;
            }
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
            String lsSQL = MiscUtil.addCondition(SQL_BROWSE, " a.sIndstCdx = " + SQLUtil.toSQL(industryId)
                    + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL("%" + client)
                    + " AND a.sTransNox LIKE " + SQLUtil.toSQL("%" + referenceNo)
                    + " AND a.cProcessd = '0' "
            );
            
            //If current user is an ordinary user load only its inquiries
//            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
//                if(psCategorCd.equals(SalesInquiryStatic.CATEGORY_CAR)){
//                    lsSQL = MiscUtil.addCondition(lsSQL, 
//                            " a.sSalesman = " + SQLUtil.toSQL(poGRider.getUserID()));
//                }
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
            Logger.getLogger(SalesInquiry.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
    
    public JSONObject removeSalesInquiryDetails() {
        poJSON = new JSONObject();
        Iterator<Model> detail = Detail().iterator();
        while (detail.hasNext()) {
            Model item = detail.next();
            detail.remove();
        }

        poJSON.put("result", "success");
        poJSON.put("message", "success");
        return poJSON;
    }
    
    public void sortPriority(){
        Detail().sort((item1, item2) -> {
            Integer lnPriority1 = (Integer) item1.getValue("nPriority");
            Integer lnPriority2 = (Integer) item2.getValue("nPriority");

            if (lnPriority1 == 0 && lnPriority2 != 0) {
                return 1; 
            } else if (lnPriority2 == 0 && lnPriority1 != 0) {
                return -1; 
            } else {
                return lnPriority1.compareTo(lnPriority2); 
            }
        });

        //Update priority no
        for(int lnCtr = 0;lnCtr <= getDetailCount()-1;lnCtr++){
            Detail(lnCtr).setPriority(lnCtr+1);
        }
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
    public Model_Sales_Inquiry_Master Master() {
        return (Model_Sales_Inquiry_Master) poMaster;
    }

    @Override
    public Model_Sales_Inquiry_Detail Detail(int row) {
        return (Model_Sales_Inquiry_Detail) paDetail.get(row);
    }

    public Model_Sales_Inquiry_Detail getDetail() {
        return (Model_Sales_Inquiry_Detail) poDetail;
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
            if (Detail(getDetailCount() - 1).getModelId() != null) {
                if (Detail(getDetailCount() - 1).getModelId().isEmpty()) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Last row has empty item.");
                    return poJSON;
                }
            }
        }

        return addDetail();
    }
    
    public void resetMaster() {
        poMaster = new SalesModels(poGRider).SalesInquiryMaster();
    }

    public JSONObject removeDetails() {
        poJSON = new JSONObject();
        Iterator<Model> detail = Detail().iterator();
        while (detail.hasNext()) {
            Model item = detail.next();
            detail.remove();
        }

        poJSON.put("result", "success");
        poJSON.put("message", "success");
        return poJSON;
    }
    
    public int getDetailRemovedCount() {
        if (paDetailRemoved == null) {
            paDetailRemoved = new ArrayList<>();
        }

        return paDetailRemoved.size();
    }

    public Model_Sales_Inquiry_Detail DetailRemove(int row) {
        return (Model_Sales_Inquiry_Detail) paDetailRemoved.get(row);
    }

    public void removeDetail(Model_Sales_Inquiry_Detail item) {
        if (paDetailRemoved == null) {
            paDetailRemoved = new ArrayList<>();
        }
        
        paDetailRemoved.add(item);
    }

    @Override
    public JSONObject willSave()
            throws SQLException,
            GuanzonException,
            CloneNotSupportedException {
        /*Put system validations and other assignments here*/
        poJSON = new JSONObject();
        
        if (SalesInquiryStatic.CONFIRMED.equals(Master().getTransactionStatus())) {
            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if (!"success".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                        poJSON.put("result", "error");
                        poJSON.put("message", "User is not an authorized approving officer.");
                        return poJSON;
                    }
                }
            }
        }

        if (paDetailRemoved == null) {
            paDetailRemoved = new ArrayList<>();
        }

        Master().setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        Master().setModifiedDate(poGRider.getServerDate());

        Iterator<Model> detail = Detail().iterator();
        while (detail.hasNext()) {
            Model item = detail.next();
            if (item.getValue("sModelIDx") == null || "".equals(item.getValue("sModelIDx"))) {
                detail.remove();

                if (item.getEditMode() == EditMode.UPDATE) {
                    paDetailRemoved.add(item);
                }

            }
        }

        //Validate detail after removing all zero qty and empty stock Id
        if (getDetailCount() <= 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction detail to be save.");
            return poJSON;
        }

        //assign other info on detail
        for (int lnCtr = 0; lnCtr <= getDetailCount() - 1; lnCtr++) {
            Detail(lnCtr).setTransactionNo(Master().getTransactionNo());
            Detail(lnCtr).setEntryNo(lnCtr + 1);
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public JSONObject save() {
        /*Put saving business rules here*/
        return isEntryOkay(SalesInquiryStatic.OPEN);
    }
    
    @Override
    public void saveComplete() {
        /*This procedure was called when saving was complete*/
        System.out.println("Transaction saved successfully.");
    }

    @Override
    public JSONObject initFields() {
        try {
            /*Put initial model values here*/
            poJSON = new JSONObject();
            System.out.println("Dept ID : " + poGRider.getDepartment());
            Master().setBranchCode(poGRider.getBranchCode());
            Master().setIndustryId(psIndustryId);
            Master().setCompanyId(psCompanyId);
            Master().setCategoryCode(psCategorCd);
            Master().setTransactionDate(poGRider.getServerDate());
//            Master().setTargetDate(poGRider.getServerDate());
            Master().setTransactionStatus(SalesInquiryStatic.OPEN);
            Master().setInquiryStatus(SalesInquiryStatic.OPEN);
            Master().setSourceCode("0"); //TODO
            Master().setPurchaseType("0");
            Master().setCategoryType("0");
            Master().setClientType("0");

        } catch (SQLException ex) {
            Logger.getLogger(SalesInquiry.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
            poJSON.put("result", "error");
            poJSON.put("message", MiscUtil.getException(ex));
            return poJSON;
        }

        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public void initSQL() {
        SQL_BROWSE =  " SELECT "
                    + " a.sTransNox "
                    + " , a.dTransact "
                    + " , b.sCompnyNm AS sClientNm "
                    + " , c.sCompnyNm AS sSalePrsn "
                    + " , d.sCompnyNm AS sAgentNme "
                    + " , e.sBranchNm "
                    + " , f.sCompnyNm "
                    + " , g.sDescript "
                    + " FROM sales_inquiry_master a "
                    + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "
                    + " LEFT JOIN client_master c ON c.sClientID = a.sSalesman "
                    + " LEFT JOIN client_master d ON d.sClientID = a.sAgentIDx "
                    + " LEFT JOIN branch e ON e.sBranchCd = a.sBranchCd "
                    + " LEFT JOIN company f ON f.sCompnyID = a.sCompnyID "
                    + " LEFT JOIN industry g ON g.sIndstCdx = a.sIndstCdx " ;
        
    }
    
    @Override
    protected JSONObject isEntryOkay(String status) {
        GValidator loValidator = SalesInquiryValidatorFactory.make(Master().getIndustryId());

        loValidator.setApplicationDriver(poGRider);
        loValidator.setTransactionStatus(status);
        loValidator.setMaster(poMaster);
//        loValidator.setDetail(paDetail);

        poJSON = loValidator.validate();

        return poJSON;
    }
    
}
