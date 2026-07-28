/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.validator;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.iface.GValidator;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Commitment_Master;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;

/**
 *
 * @author Arsiela 
 */
public class SalesCommitmentValidator implements GValidator{
    GRiderCAS poGRider;
    String psTranStat;
    JSONObject poJSON;
    
    Model_Sales_Commitment_Master poMaster;

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
        poMaster = (Model_Sales_Commitment_Master) value;
    }

    @Override
    public void setDetail(ArrayList<Object> value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOthers(ArrayList<Object> value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject validate() {
        try {
            switch (psTranStat){
                case BankApplicationStatus.OPEN:
                    return validateNew();
                case BankApplicationStatus.APPROVED:
                    return validateApproved();
                case BankApplicationStatus.DISAPPROVED:
                    return validateDisApproved();
                case BankApplicationStatus.CANCELLED:
                    return validateCancelled();
                default:
                    poJSON = new JSONObject();
                    poJSON.put("result", "success");
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        
        return poJSON;
    }
    
    private JSONObject validateNew() throws SQLException{
        poJSON = new JSONObject();
        Date loAppliedDate = poMaster.getAppliedDate();
        Date loApprovedDate = poMaster.getApprovedDate();
        LocalDate serverDate = strToDate(xsDateShort(poGRider.getServerDate()));
        LocalDate oneYearAgo = serverDate.minusYears(1);
        
        if (loAppliedDate == null) {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid Applied Date.");
            return poJSON;
        }

        if ("1900-01-01".equals(xsDateShort(loAppliedDate))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid Applied Date.");
            return poJSON;
        }
        
//        if (loApprovedDate == null) {
//            poJSON.put("message", "Invalid Approved Date.");
//            return poJSON;
//        }
        
        if (poMaster.getTransactionNo()== null) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction no is not set.");
            return poJSON;
        }
        if (poMaster.getPONumber() == null || "".equals(poMaster.getPONumber())) {
            poJSON.put("result", "error");
            poJSON.put("message", "PO Number is not set.");
            return poJSON;
        }
//        if (poMaster.getATDNumber() == null || "".equals(poMaster.getATDNumber())) {
//            poJSON.put("result", "error");
//            poJSON.put("message", "ATD Number is not set.");
//            return poJSON;
//        }
        if (poMaster.getPaymentMode()== null || "".equals(poMaster.getPaymentMode())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Payment Mode is not set.");
            return poJSON;
        }
        if (poMaster.getBankId()== null || "".equals(poMaster.getBankId())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Bank is not set.");
            return poJSON;
        }
        if (poMaster.getSourceCode()== null || "".equals(poMaster.getSourceCode())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source Code is not set.");
            return poJSON;
        }
        if (poMaster.getSourceNo() == null || "".equals(poMaster.getSourceNo())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source No is not set.");
            return poJSON;
        }
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateApproved()throws SQLException{
        poJSON = new JSONObject();
        Date ldAppliedDate = poMaster.getAppliedDate();
        Date ldApprovedDate = poMaster.getApprovedDate();
        LocalDate loAppliedDate = strToDate(xsDateShort(poMaster.getAppliedDate()));
        
        if (ldApprovedDate == null) {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid Approved Date.");
            return poJSON;
        }

        if ("1900-01-01".equals(xsDateShort(ldApprovedDate))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid Approved Date.");
            return poJSON;
        }
        
        LocalDate loApprovedDate = strToDate(xsDateShort(poMaster.getApprovedDate()));
        if (loApprovedDate.isBefore(loAppliedDate)) {
            poJSON.put("result", "error");
            poJSON.put("message", "Approve date cannot be before the applied date.");
            return poJSON;
        }
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateDisApproved(){
        poJSON = new JSONObject();
                
        poJSON.put("result", "success");
        return poJSON;
    }
    
    private JSONObject validateCancelled(){
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
