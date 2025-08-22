/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.services;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import ph.com.guanzongroup.cas.sales.t1.RequirementsSource;
import ph.com.guanzongroup.cas.sales.t1.RequirementsSourcePerGroup;
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
    
    public Salesman Salesman() {
        try {
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
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
        
        return poSalesman;
    }
    
    public SalesAgent SalesAgent(){
        try {
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
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
        
        return poSalesAgent;
    }
    
    public RequirementsSource RequirementsSource(){
        try {
            if (poGRider == null) {
                poLogWrapper.severe("SalesControllers.RequirementsSource: Application driver is not set.");
                return null;
            }
            if (poRequirementSource != null){ 
                return poRequirementSource;
            }
            poRequirementSource = new RequirementsSource();
            poRequirementSource.setApplicationDriver(poGRider);
            poRequirementSource.setWithParentClass(false);
            poRequirementSource.setLogWrapper(poLogWrapper);
            poRequirementSource.initialize();
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
        
        return poRequirementSource;
    }
    
    public RequirementsSourcePerGroup RequirementsSourcePerGroup(){
        try {
            if (poGRider == null) {
                poLogWrapper.severe("SalesControllers.RequirementsSourcePerGroup: Application driver is not set.");
                return null;
            }
            if (poRequirementSourcePerGroup != null){ 
                return poRequirementSourcePerGroup;
            }
            poRequirementSourcePerGroup = new RequirementsSourcePerGroup();
            poRequirementSourcePerGroup.setApplicationDriver(poGRider);
            poRequirementSourcePerGroup.setWithParentClass(false);
            poRequirementSourcePerGroup.setLogWrapper(poLogWrapper);
            poRequirementSourcePerGroup.initialize();
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
        
        return poRequirementSourcePerGroup;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            poSalesInquiry = null;
            poSalesInquirySources = null;
            poSalesman = null;
            poSalesAgent = null;
            poRequirementSource = null;
            poRequirementSourcePerGroup = null;
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
    private RequirementsSource poRequirementSource;
    private RequirementsSourcePerGroup poRequirementSourcePerGroup;
}
