/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.validator;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.iface.GValidator;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Requirements;

/**
 *
 * @author Arsiela 03-12-2025
 */
public class SalesInquiryRequirements implements GValidator{
    GRiderCAS poGRider;
    String psTranStat;
    JSONObject poJSON;
    
    Model_Sales_Inquiry_Requirements poMaster;

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
        poMaster = (Model_Sales_Inquiry_Requirements) value;
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
        poJSON = new JSONObject();
        Date loReceiveDate = poMaster.getReceivedDate();
        
        if (poMaster.getTransactionNo()== null) {
            poJSON.put("message", "Transaction no is not set.");
            return poJSON;
        }
        if (poMaster.getRequirementCode()== null || "".equals(poMaster.getRequirementCode())) {
            poJSON.put("message", "Requirements code is not set.");
            return poJSON;
        }
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
