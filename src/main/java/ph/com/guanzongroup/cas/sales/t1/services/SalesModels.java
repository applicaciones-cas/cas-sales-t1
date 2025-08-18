/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.services;

import org.guanzon.appdriver.base.GRiderCAS;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Bank_Application;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Requirement_Source;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Requirement_Source_PerGroup;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Agent;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Detail;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Requirements;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Sources;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Salesman;

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
    
    public Model_Sales_Inquiry_Sources SalesInquirySources(){
        if (poGRider == null){
            System.err.println("SalesModels.SalesInquirySources: Application driver is not set.");
            return null;
        }
        
        if (poSalesInquirySources == null){
            poSalesInquirySources = new Model_Sales_Inquiry_Sources();
            poSalesInquirySources.setApplicationDriver(poGRider);
            poSalesInquirySources.setXML("Model_Sales_Inquiry_Sources");
            poSalesInquirySources.setTableName("Sales_Inquiry_Sources");
            poSalesInquirySources.initialize();
        }

        return poSalesInquirySources;
    }
    
    public Model_Salesman Salesman(){
        if (poGRider == null){
            System.err.println("SalesModels.Salesman: Application driver is not set.");
            return null;
        }
        
        if (poSalesman == null){
            poSalesman = new Model_Salesman();
            poSalesman.setApplicationDriver(poGRider);
            poSalesman.setXML("Model_Salesman");
            poSalesman.setTableName("Salesman");
            poSalesman.initialize();
        }

        return poSalesman;
    }
    
    public Model_Sales_Agent SalesAgent(){
        if (poGRider == null){
            System.err.println("SalesModels.SalesAgent: Application driver is not set.");
            return null;
        }
        
        if (poSalesAgent == null){
            poSalesAgent = new Model_Sales_Agent();
            poSalesAgent.setApplicationDriver(poGRider);
            poSalesAgent.setXML("Model_Sales_Agent");
            poSalesAgent.setTableName("Sales_Agent");
            poSalesAgent.initialize();
        }

        return poSalesAgent;
    }
    
    public Model_Sales_Inquiry_Requirements SalesInquiryRequirements(){
        if (poGRider == null){
            System.err.println("SalesModels.SalesInquiryRequirements: Application driver is not set.");
            return null;
        }
        
        if (poSalesInquiryRequirements == null){
            poSalesInquiryRequirements = new Model_Sales_Inquiry_Requirements();
            poSalesInquiryRequirements.setApplicationDriver(poGRider);
            poSalesInquiryRequirements.setXML("Model_Sales_Inquiry_Requirements");
            poSalesInquiryRequirements.setTableName("Sales_Inquiry_Requirements");
            poSalesInquiryRequirements.initialize();
        }

        return poSalesInquiryRequirements;
    }
    
    public Model_Bank_Application BankApplication(){
        if (poGRider == null){
            System.err.println("SalesModels.BankApplication: Application driver is not set.");
            return null;
        }
        
        if (poBankApplication == null){
            poBankApplication = new Model_Bank_Application();
            poBankApplication.setApplicationDriver(poGRider);
            poBankApplication.setXML("Model_Bank_Application");
            poBankApplication.setTableName("Bank_Application");
            poBankApplication.initialize();
        }

        return poBankApplication;
    }
    
    public Model_Requirement_Source RequirementSource(){
        if (poGRider == null){
            System.err.println("SalesModels.RequirementSource: Application driver is not set.");
            return null;
        }
        
        if (poRequirementSource == null){
            poRequirementSource = new Model_Requirement_Source();
            poRequirementSource.setApplicationDriver(poGRider);
            poRequirementSource.setXML("Model_Requirement_Source");
            poRequirementSource.setTableName("Requirement_Source");
            poRequirementSource.initialize();
        }

        return poRequirementSource;
    }
    
    public Model_Requirement_Source_PerGroup RequirementSourcePerGroup(){
        if (poGRider == null){
            System.err.println("SalesModels.RequirementSourcePerGroup: Application driver is not set.");
            return null;
        }
        
        if (poRequirementSourcePerGroup == null){
            poRequirementSourcePerGroup = new Model_Requirement_Source_PerGroup();
            poRequirementSourcePerGroup.setApplicationDriver(poGRider);
            poRequirementSourcePerGroup.setXML("Model_Requirement_Source_PerGroup");
            poRequirementSourcePerGroup.setTableName("Requirement_Source_PerGroup");
            poRequirementSourcePerGroup.initialize();
        }

        return poRequirementSourcePerGroup;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {                    
            poSalesInquiryMaster = null;
            poSalesInquiryDetail = null;
            poSalesInquirySources = null;
            poSalesman = null;
            poSalesAgent = null;
            poSalesInquiryRequirements = null;
            poRequirementSource = null;
            poRequirementSourcePerGroup = null;

            poGRider = null;
        } finally {
            super.finalize();
        }
    }
    
    private GRiderCAS poGRider;

    private Model_Sales_Inquiry_Master poSalesInquiryMaster;
    private Model_Sales_Inquiry_Detail poSalesInquiryDetail;
    private Model_Sales_Inquiry_Sources poSalesInquirySources;
    private Model_Salesman poSalesman;
    private Model_Sales_Agent poSalesAgent;
    private Model_Sales_Inquiry_Requirements poSalesInquiryRequirements;
    private Model_Bank_Application poBankApplication;
    private Model_Requirement_Source poRequirementSource;
    private Model_Requirement_Source_PerGroup poRequirementSourcePerGroup;
}
