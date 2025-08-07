/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.services;

import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;

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
    
    @Override
    protected void finalize() throws Throwable {
        try {
            poSalesInquiry = null;
            poLogWrapper = null;
            poGRider = null;
        } finally {
            super.finalize();
        }
    }

    private GRiderCAS poGRider;
    private LogWrapper poLogWrapper;

    private SalesInquiry poSalesInquiry;
}
