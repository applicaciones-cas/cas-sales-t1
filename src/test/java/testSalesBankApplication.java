/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ph.com.guanzongroup.cas.sales.t1.SalesBankApplication;
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Bank_Application;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testSalesBankApplication {
    
    static GRiderCAS instance;
    static SalesBankApplication poController;
    private String psIndustryId = "01";
    private String psCompanyId = "M001";
    private String psCategorCd = "00006";
    private String psTransNo = "A00125000055";
    @BeforeClass
    public static void setUpClass() {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();

        poController = new SalesControllers(instance, null).SalesBankApplication();
    }

    @Test
    public void testUpdateTransaction() {
        JSONObject loJSON;

        try {
            loJSON = poController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poController.OpenTransaction(psTransNo);
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poController.UpdateTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            loJSON = poController.SaveTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
        } catch (CloneNotSupportedException | SQLException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (GuanzonException ex) {
            Logger.getLogger(testSalesBankApplication.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }
    
//    @Test
    public void testLoadSalesInquiry() {
        JSONObject loJSON;
        loJSON = poController.InitTransaction();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        poController.setIndustryId(psIndustryId);
        poController.setCategoryId(psCategorCd);
        poController.setCompanyId(psCompanyId);
        loJSON = poController.loadTransactionList("","");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        //retreiving using column index
        for (int lnCtr = 0; lnCtr <= poController.getSalesInquiryCount()- 1; lnCtr++) {
            try {
                System.out.println("Row No ->> " + lnCtr);
                System.out.println("Transaction No ->> " + poController.SalesInquiryList(lnCtr).getTransactionNo());
                System.out.println("Transaction Date ->> " + poController.SalesInquiryList(lnCtr).getTransactionDate());
                System.out.println("Client ->> " + poController.SalesInquiryList(lnCtr).Client().getCompanyName());
                System.out.println("----------------------------------------------------------------------------------");
            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(testSalesBankApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
//    @Test
    public void testOpenTransaction() {
        JSONObject loJSON;
        
        try {
            loJSON = poController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            loJSON = poController.OpenTransaction(psTransNo);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.Master().getColumnCount(); lnCol++){
                System.out.println(poController.Master().getColumn(lnCol) + " ->> " + poController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poController.Master().Branch().getBranchName());
            System.out.println(poController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poController.Detail(lnCtr).getValue(lnCol));
                }
                System.out.println("Bank " +  poController.Detail(lnCtr).Bank().getBankName());
            }

            System.out.println("Bank : " + (String) loJSON.get("message"));
            
        } catch (CloneNotSupportedException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesBankApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }      
    
//    @Test
    public void testApproveTransaction() {
        JSONObject loJSON;
        
        try {
            loJSON = poController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            loJSON = poController.OpenTransaction(psTransNo);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.Master().getColumnCount(); lnCol++){
                System.out.println(poController.Master().getColumn(lnCol) + " ->> " + poController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poController.Master().Branch().getBranchName());
            System.out.println(poController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poController.Detail(lnCtr).getValue(lnCol));
                }
            }

            List<Model_Bank_Application> faModel = new ArrayList<>();
            poController.Detail(0).setApprovedDate(instance.getServerDate());
            faModel.add(poController.Detail(0));
            loJSON = poController.ApproveBankApplication("",faModel);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 
            
            System.out.println((String) loJSON.get("message"));
        } catch (CloneNotSupportedException | ParseException | SQLException | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        }
    }

    //    @Test
    public void testDisapproveTransaction() {
        JSONObject loJSON;

        try {
            loJSON = poController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poController.OpenTransaction(psTransNo);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.Master().getColumnCount(); lnCol++){
                System.out.println(poController.Master().getColumn(lnCol) + " ->> " + poController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poController.Master().Branch().getBranchName());
            System.out.println(poController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poController.Detail(lnCtr).getValue(lnCol));
                }
            }

            List<Model_Bank_Application> faModel = new ArrayList<>();
            faModel.add(poController.Detail(0));
            loJSON = poController.DisapproveBankApplication("",faModel);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            System.out.println((String) loJSON.get("message"));
        } catch (CloneNotSupportedException | ParseException | SQLException | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        }
    }

    //    @Test
    public void testVoidTransaction() {
        JSONObject loJSON;

        try {
            loJSON = poController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poController.OpenTransaction(psTransNo);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.Master().getColumnCount(); lnCol++){
                System.out.println(poController.Master().getColumn(lnCol) + " ->> " + poController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poController.Master().Branch().getBranchName());
            System.out.println(poController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poController.Detail(lnCtr).getValue(lnCol));
                }
            }

            List<Model_Bank_Application> faModel = new ArrayList<>();
            faModel.add(poController.Detail(0));
            loJSON = poController.VoidBankApplication("",faModel);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            System.out.println((String) loJSON.get("message"));
        } catch (CloneNotSupportedException | ParseException | SQLException | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        }
    }

    //    @Test
    public void testCancelTransaction() {
        JSONObject loJSON;

        try {
            loJSON = poController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poController.OpenTransaction(psTransNo);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.Master().getColumnCount(); lnCol++){
                System.out.println(poController.Master().getColumn(lnCol) + " ->> " + poController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poController.Master().Branch().getBranchName());
            System.out.println(poController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poController.Detail(lnCtr).getValue(lnCol));
                }
            }

            List<Model_Bank_Application> faModel = new ArrayList<>();
            faModel.add(poController.Detail(0));
            loJSON = poController.CancelBankApplication("",faModel);
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            System.out.println((String) loJSON.get("message"));
        } catch (CloneNotSupportedException | ParseException | SQLException | GuanzonException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        }
    }
}
