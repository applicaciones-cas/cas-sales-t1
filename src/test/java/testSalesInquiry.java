/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testSalesInquiry {
    
    static GRiderCAS instance;
    static SalesInquiry poSalesInquiryController;
    @BeforeClass
    public static void setUpClass() {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();

        poSalesInquiryController = new SalesControllers(instance, null).SalesInquiry();
    }

//    @Test
    public void testNewTransaction() {
        String branchCd = instance.getBranchCode();
        String industryId = "01";
        String companyId = "0002";
        String categoryId = "0005";
        String remarks = "this is a test Class 4.";

        String stockId = "C0W125000001";
        int quantity = 110;

        JSONObject loJSON;

        try {

            loJSON = poSalesInquiryController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poSalesInquiryController.NewTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            try {
                poSalesInquiryController.setIndustryId(industryId);
                poSalesInquiryController.setCompanyId(companyId);
                poSalesInquiryController.setCategoryId(categoryId);

                poSalesInquiryController.initFields();
                poSalesInquiryController.Master().setIndustryId(industryId); 
                Assert.assertEquals(poSalesInquiryController.Master().getIndustryId(), industryId);
                poSalesInquiryController.Master().setCompanyId(companyId); 
                Assert.assertEquals(poSalesInquiryController.Master().getCompanyId(), companyId);
                poSalesInquiryController.Master().setCategoryCode(categoryId); 
                Assert.assertEquals(poSalesInquiryController.Master().getCategoryCode(), categoryId);
                poSalesInquiryController.Master().setTransactionDate(instance.getServerDate()); 
                Assert.assertEquals(poSalesInquiryController.Master().getTransactionDate(), instance.getServerDate());
                poSalesInquiryController.Master().setBranchCode(branchCd); 
                Assert.assertEquals(poSalesInquiryController.Master().getBranchCode(), branchCd);
                poSalesInquiryController.Master().setClientId("C00124000020"); 
                Assert.assertEquals(poSalesInquiryController.Master().getClientId(), "C00124000020");

                poSalesInquiryController.Master().setRemarks(remarks);
                Assert.assertEquals(poSalesInquiryController.Master().getRemarks(), remarks);

                poSalesInquiryController.Detail(0).setModelId("V0001");
                poSalesInquiryController.Detail(0).setColorId("00001");
                poSalesInquiryController.AddDetail();
                poSalesInquiryController.Detail(1).setModelId("V0002");
                poSalesInquiryController.Detail(1).setColorId("00001");

                System.out.println("Industry ID : " + instance.getIndustry());
                System.out.println("Industry : " + poSalesInquiryController.Master().Industry().getDescription());
                System.out.println("Category Code : " + poSalesInquiryController.Master().getCategoryCode());
                System.out.println("TransNox : " + poSalesInquiryController.Master().getTransactionNo());

                loJSON = poSalesInquiryController.SaveTransaction();
                if (!"success".equals((String) loJSON.get("result"))) {
                    System.err.println((String) loJSON.get("message"));
                    Assert.fail();
                }

            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ExceptionInInitializerError e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Test
    public void testUpdateTransaction() {
        JSONObject loJSON;

        try {
            loJSON = poSalesInquiryController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poSalesInquiryController.OpenTransaction("P0w125000001");
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poSalesInquiryController.UpdateTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poSalesInquiryController.Detail(0).setModelId("V0003");
            poSalesInquiryController.Detail(0).setColorId("00001");
            
            loJSON = poSalesInquiryController.SaveTransaction();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
        } catch (CloneNotSupportedException | SQLException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (GuanzonException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }
    
//    @Test
    public void testLoadSalesInquiry() {
        String industryId = "01";
        String companyId = "";
        JSONObject loJSON;
        loJSON = poSalesInquiryController.InitTransaction();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        poSalesInquiryController.setIndustryId(industryId);
        loJSON = poSalesInquiryController.loadSalesInquiry(industryId,"","");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        //retreiving using column index
        for (int lnCtr = 0; lnCtr <= poSalesInquiryController.getSalesInquiryCount()- 1; lnCtr++) {
            try {
                System.out.println("Row No ->> " + lnCtr);
                System.out.println("Transaction No ->> " + poSalesInquiryController.SalesInquiryList(lnCtr).getTransactionNo());
                System.out.println("Transaction Date ->> " + poSalesInquiryController.SalesInquiryList(lnCtr).getTransactionDate());
                System.out.println("Client ->> " + poSalesInquiryController.SalesInquiryList(lnCtr).Client().getCompanyName());
                System.out.println("----------------------------------------------------------------------------------");
            } catch (SQLException ex) {
                Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GuanzonException ex) {
                Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
     @Test
    public void testOpenTransaction() {
        JSONObject loJSON;
        
        try {
            loJSON = poSalesInquiryController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            loJSON = poSalesInquiryController.OpenTransaction("M00125000001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesInquiryController.Master().getColumnCount(); lnCol++){
                System.out.println(poSalesInquiryController.Master().getColumn(lnCol) + " ->> " + poSalesInquiryController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poSalesInquiryController.Master().Branch().getBranchName());
            System.out.println(poSalesInquiryController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poSalesInquiryController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poSalesInquiryController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poSalesInquiryController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poSalesInquiryController.Detail(lnCtr).getValue(lnCol));
                }
            }
        } catch (CloneNotSupportedException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }   
    
    @Test
    public void testConfirmTransaction() {
        JSONObject loJSON;
        
        try {
            loJSON = poSalesInquiryController.InitTransaction();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            loJSON = poSalesInquiryController.OpenTransaction("M00125000001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesInquiryController.Master().getColumnCount(); lnCol++){
                System.out.println(poSalesInquiryController.Master().getColumn(lnCol) + " ->> " + poSalesInquiryController.Master().getValue(lnCol));
            }
            //retreiving using field descriptions
            System.out.println(poSalesInquiryController.Master().Branch().getBranchName());
            System.out.println(poSalesInquiryController.Master().Industry().getDescription());

            //retreiving using column index
            for (int lnCtr = 0; lnCtr <= poSalesInquiryController.Detail().size() - 1; lnCtr++){
                for (int lnCol = 1; lnCol <= poSalesInquiryController.Detail(lnCtr).getColumnCount(); lnCol++){
                    System.out.println(poSalesInquiryController.Detail(lnCtr).getColumn(lnCol) + " ->> " + poSalesInquiryController.Detail(lnCtr).getValue(lnCol));
                }
            }
            
            loJSON = poSalesInquiryController.ConfirmTransaction("test confirm");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 
            
            System.out.println((String) loJSON.get("message"));
        } catch (CloneNotSupportedException |ParseException e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
