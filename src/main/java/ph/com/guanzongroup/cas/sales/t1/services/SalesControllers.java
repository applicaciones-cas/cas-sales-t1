/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.services;

import java.sql.SQLException;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import ph.com.guanzongroup.cas.sales.t1.SalesAgent;
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;
import ph.com.guanzongroup.cas.sales.t1.SalesInquirySources;
import ph.com.guanzongroup.cas.sales.t1.Salesman;

/**
 *
 * @author MIS-PC
 */
public class SalesControllers {
    
    public SalesControllers(GRiderCAS applicationDriver, LogWrapper logWrapper) {
        poGRider = applicationDriver;
        poLogWrapper = logWrapper;
    }
    
    public SalesInquiry SalesInquiry() {
        if (poGRider == null) {
            poLogWrapper.severe("SalesControllers.SalesInquiry: Application driver is not set.");
            return null;
        }

        if (poSalesInquiry != null) {
            return poSalesInquiry;
        }

        poSalesInquiry = new SalesInquiry();
        poSalesInquiry.setApplicationDriver(poGRider);
        poSalesInquiry.setBranchCode(poGRider.getBranchCode());
        poSalesInquiry.setVerifyEntryNo(true);
        poSalesInquiry.setWithParent(false);
        poSalesInquiry.setLogWrapper(poLogWrapper);
        return poSalesInquiry;
    }
    
    public SalesInquirySources SalesInquirySources() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("SalesControllers.SalesInquirySources: Application driver is not set.");
            return null;
        }

        if (poSalesInquirySources != null) {
            return poSalesInquirySources;
        }

        poSalesInquirySources = new SalesInquirySources();
        poSalesInquirySources.setApplicationDriver(poGRider);
        poSalesInquirySources.setWithParentClass(false);
        poSalesInquirySources.setLogWrapper(poLogWrapper);
        poSalesInquirySources.initialize();
        return poSalesInquirySources;
    }
    
    public Salesman Salesman() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("SalesControllers.Salesman: Application driver is not set.");
            return null;
        } 
        if (poSalesman != null){
            return poSalesman; 
        }
        poSalesman = new Salesman();
        poSalesman.setApplicationDriver(poGRider);
        poSalesman.setWithParentClass(false);
        poSalesman.setLogWrapper(poLogWrapper);
        poSalesman.initialize();
        return poSalesman;
    }
    
    public SalesAgent SalesAgent() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("SalesControllers.SalesAgent: Application driver is not set.");
            return null;
        } 
        if (poSalesAgent != null){
            return poSalesAgent; 
        }
        poSalesAgent = new SalesAgent();
        poSalesAgent.setApplicationDriver(poGRider);
        poSalesAgent.setWithParentClass(false);
        poSalesAgent.setLogWrapper(poLogWrapper);
        poSalesAgent.initialize();
        return poSalesAgent;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            poSalesInquiry = null;
            poSalesInquirySources = null;
            poSalesman = null;
            poSalesAgent = null;
            poLogWrapper = null;
            poGRider = null;
        } finally {
            super.finalize();
        }
    }

    private GRiderCAS poGRider;
    private LogWrapper poLogWrapper;

    private SalesInquiry poSalesInquiry;
    private SalesInquirySources poSalesInquirySources;
    private Salesman poSalesman;
    private SalesAgent poSalesAgent;
}
