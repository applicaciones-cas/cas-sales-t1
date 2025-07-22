/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.services;

import org.guanzon.appdriver.base.GRiderCAS;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Detail;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;

/**
 *
 * @author MIS-PC
 */
public class SalesModels {
    
    public SalesModels(GRiderCAS applicationDriver){
        poGRider = applicationDriver;
    }
    
    public Model_Sales_Inquiry_Master SalesInquiryMaster(){
        if (poGRider == null){
            System.err.println("SalesModels.SalesInquiryMaster: Application driver is not set.");
            return null;
        }
        
        if (poSalesInquiryMaster == null){
            poSalesInquiryMaster = new Model_Sales_Inquiry_Master();
            poSalesInquiryMaster.setApplicationDriver(poGRider);
            poSalesInquiryMaster.setXML("Model_Sales_Inquiry_Master");
            poSalesInquiryMaster.setTableName("Sales_Inquiry_Master");
            poSalesInquiryMaster.initialize();
        }

        return poSalesInquiryMaster;
    }
    
    public Model_Sales_Inquiry_Detail SalesInquiryDetails(){
        if (poGRider == null){
            System.err.println("SalesModels.SalesInquiryDetails: Application driver is not set.");
            return null;
        }
        
        if (poSalesInquiryDetail == null){
            poSalesInquiryDetail = new Model_Sales_Inquiry_Detail();
            poSalesInquiryDetail.setApplicationDriver(poGRider);
            poSalesInquiryDetail.setXML("Model_Sales_Inquiry_Detail");
            poSalesInquiryDetail.setTableName("Sales_Inquiry_Detail");
            poSalesInquiryDetail.initialize();
        }

        return poSalesInquiryDetail;
    }
    
    
    @Override
    protected void finalize() throws Throwable {
        try {                    
            poSalesInquiryMaster = null;
            poSalesInquiryDetail = null;

            poGRider = null;
        } finally {
            super.finalize();
        }
    }
    
    private GRiderCAS poGRider;

    private Model_Sales_Inquiry_Master poSalesInquiryMaster;
    private Model_Sales_Inquiry_Detail poSalesInquiryDetail;
}
