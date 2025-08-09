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
import ph.com.guanzongroup.cas.sales.t1.SalesInquirySources;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testSalesInquirySources {
    static GRiderCAS instance;
    static SalesInquirySources poSalesInquirySourcesController;
    @BeforeClass
    public static void setUpClass() throws SQLException, GuanzonException {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();
        poSalesInquirySourcesController = new SalesControllers(instance, null).SalesInquirySources();
    }

//    @Test
    public void testNewRecord() {
        String actual = "Test Source";
        JSONObject loJSON;

        try {

            poSalesInquirySourcesController.initialize();
            loJSON = poSalesInquirySourcesController.newRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poSalesInquirySourcesController.getModel().setDescription(actual);
            Assert.assertEquals(poSalesInquirySourcesController.getModel().getDescription(), actual);

            loJSON = poSalesInquirySourcesController.saveRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
        } catch (ExceptionInInitializerError e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesInquirySources.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }
    
    @Test
    public void testUpdateTransaction() {
        JSONObject loJSON;

        try { 
            poSalesInquirySourcesController.initialize();
            loJSON = poSalesInquirySourcesController.openRecord("003");
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poSalesInquirySourcesController.updateRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poSalesInquirySourcesController.getModel().setDescription("Facebook");
            
            loJSON = poSalesInquirySourcesController.saveRecord();
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
    public void testOpenTransaction() {
        JSONObject loJSON;
        
        try {
            poSalesInquirySourcesController.initialize();
            loJSON = poSalesInquirySourcesController.openRecord("001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesInquirySourcesController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesInquirySourcesController.getModel().getColumn(lnCol) + " ->> " + poSalesInquirySourcesController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }   
    
//    @Test
    public void testActivate() {
        JSONObject loJSON;
        
        try {
            
            poSalesInquirySourcesController.initialize();
            loJSON = poSalesInquirySourcesController.openRecord("001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesInquirySourcesController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesInquirySourcesController.getModel().getColumn(lnCol) + " ->> " + poSalesInquirySourcesController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poSalesInquirySourcesController.activateRecord();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 
            
            System.out.println((String) loJSON.get("message"));
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
    
//    @Test
    public void testDeActivate() {
        JSONObject loJSON;
        
        try {
            
            poSalesInquirySourcesController.initialize();
            loJSON = poSalesInquirySourcesController.openRecord("001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesInquirySourcesController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesInquirySourcesController.getModel().getColumn(lnCol) + " ->> " + poSalesInquirySourcesController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poSalesInquirySourcesController.deactivateRecord();
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 
            
            System.out.println((String) loJSON.get("message"));
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testSalesInquiry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
