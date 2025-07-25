/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.validator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.iface.GValidator;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Detail;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela 03-12-2025
 */
public class SalesInquiry_Monarch implements GValidator{
    GRiderCAS poGRider;
    String psTranStat;
    JSONObject poJSON;
    
    Model_Sales_Inquiry_Master poMaster;
    ArrayList<Model_Sales_Inquiry_Detail> poDetail;

    @Override
    public void setApplicationDriver(Object applicationDriver) {
        poGRider = (GRiderCAS) applicationDriver;
    }

    @Override
    public void setTransactionStatus(String transactionStatus) {
        psTranStat = transactionStatus;
    }

    @Override
    public void setMaster(Object value) {
        poMaster = (Model_Sales_Inquiry_Master) value;
    }

    @Override
    public void setDetail(ArrayList<Object> value) {
        poDetail.clear();
        for(int lnCtr = 0; lnCtr <= value.size() - 1; lnCtr++){
            poDetail.add((Model_Sales_Inquiry_Detail) value.get(lnCtr));
        }
    }

    @Override
    public void setOthers(ArrayList<Object> value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject validate() {
        try {
            switch (psTranStat){
                case SalesInquiryStatic.OPEN:
                    return validateNew();
                case SalesInquiryStatic.CONFIRMED:
                    return validateConfirmed();
                case SalesInquiryStatic.POSTED:
                    return validatePosted();
                case SalesInquiryStatic.PAID:
                    return validatePaid();
                case SalesInquiryStatic.CANCELLED:
                    return validateCancelled();
                case SalesInquiryStatic.VOID:
                    return validateVoid();
                default:
                    poJSON = new JSONObject();
                    poJSON.put("result", "success");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SalesInquiry_Monarch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return poJSON;
    }
    
    private JSONObject validateNew() throws SQLException{
        poJSON = new JSONObject();
        Date loTransactionDate = poMaster.getTransactionDate();
        LocalDate serverDate = strToDate(xsDateShort(poGRider.getServerDate()));
        LocalDate oneYearAgo = serverDate.minusYears(1);
        
        if (loTransactionDate == null) {
            poJSON.put("message", "Invalid Transaction Date.");
            return poJSON;
        }

        if ("1900-01-01".equals(xsDateShort(loTransactionDate))) {
            poJSON.put("message", "Invalid Transaction Date.");
            return poJSON;
        }
        if (poMaster.getIndustryId() == null) {
            poJSON.put("message", "Industry is not set.");
            return poJSON;
        }
        if (poMaster.getCompanyId() == null || poMaster.getCompanyId().isEmpty()) {
            poJSON.put("message", "Company is not set.");
            return poJSON;
        }
        if (poMaster.getCategoryCode()== null || poMaster.getCategoryCode().isEmpty()) {
            poJSON.put("message", "Category is not set.");
            return poJSON;
        }
        if (poMaster.getBranchCode()== null || poMaster.getBranchCode().isEmpty()) {
            poJSON.put("message", "Branch is not set.");
            return poJSON;
        }
        if (poMaster.getPurchaseType()== null || poMaster.getPurchaseType().isEmpty()) {
            poJSON.put("message", "Purchase Type is not set.");
            return poJSON;
        }
        if (poMaster.getCategoryType()== null || poMaster.getCategoryType().isEmpty()) {
            poJSON.put("message", "Category Type is not set.");
            return poJSON;
        }
        if (poMaster.getSourceCode()== null || poMaster.getSourceCode().isEmpty()) {
            poJSON.put("message", "Source Code is not set.");
            return poJSON;
        }
        if (poMaster.getClientId() == null || poMaster.getClientId().isEmpty()) {
            poJSON.put("message", "Client is not set.");
            return poJSON;
        }
        if (poMaster.getAddressId()== null || poMaster.getAddressId().isEmpty()) {
            poJSON.put("message", "Address is not set.");
            return poJSON;
        }
        if (poMaster.getContactId()== null || poMaster.getContactId().isEmpty()) {
            poJSON.put("message", "Contact Number is not set.");
            return poJSON;
        }
        if(poMaster.getSourceCode().equals("")){
            if (poMaster.getAgentId()== null || poMaster.getAgentId().isEmpty()) {
                poJSON.put("message", "Referral is not set.");
                return poJSON;
            }
        }
        if (poMaster.getInquiryStatus()== null || poMaster.getInquiryStatus().isEmpty()) {
            poJSON.put("message", "Inquiry Status is not set.");
            return poJSON;
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateConfirmed()throws SQLException{
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateApproved(){
        poJSON = new JSONObject();
                
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateProcessed(){
        poJSON = new JSONObject();
                
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validatePosted(){
        poJSON = new JSONObject();
                
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validatePaid(){
        poJSON = new JSONObject();
                
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateCancelled() throws SQLException{
        poJSON = new JSONObject();
        
        //TODO
//        poJSON = checkPOReturn();
//        if("error".equals((String) poJSON.get("result"))){
//            poJSON.put("message", "Found existing Purchase Order Return <" + (String) poJSON.get("sTransNox") + ">. Cancellation of transaction aborted.");
//            return poJSON;
//        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateVoid() throws SQLException{
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateReturned() throws SQLException{
        poJSON = new JSONObject();
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
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
    
    
}
