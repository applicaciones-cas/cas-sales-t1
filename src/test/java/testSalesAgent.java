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
import ph.com.guanzongroup.cas.sales.t1.SalesAgent;
import ph.com.guanzongroup.cas.sales.t1.SalesInquirySources;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testSalesAgent {
    static GRiderCAS instance;
    static SalesAgent poSalesAgentController;
    @BeforeClass
    public static void setUpClass() throws SQLException, GuanzonException {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();
        poSalesAgentController = new SalesControllers(instance, null).SalesAgent();
    }

//    @Test
    public void testNewRecord() {
        String actual = "M00125000005";
        String actual2 = "Manager";
        String actual3 = "Accountant";
        String actual4 = "BDO";
        JSONObject loJSON;

        try {

            poSalesAgentController.initialize();
            loJSON = poSalesAgentController.newRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poSalesAgentController.getModel().setClientId(actual);
            Assert.assertEquals(poSalesAgentController.getModel().getClientId(), actual);
            poSalesAgentController.getModel().setPosition(actual2);
            Assert.assertEquals(poSalesAgentController.getModel().getPosition(), actual2);
            poSalesAgentController.getModel().setProfession(actual3);
            Assert.assertEquals(poSalesAgentController.getModel().getProfession(), actual3);
            poSalesAgentController.getModel().setCompany(actual4);
            Assert.assertEquals(poSalesAgentController.getModel().getCompany(), actual4);

            loJSON = poSalesAgentController.saveRecord();
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
    
//    @Test
    public void testUpdateTransaction() {
        String actual = "M00125000005";
        String actual2 = "Manager";
        String actual3 = "Accountant";
        String actual4 = "Banco De Oro";
        JSONObject loJSON;

        try { 
            poSalesAgentController.initialize();
            loJSON = poSalesAgentController.openRecord(actual);
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poSalesAgentController.updateRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poSalesAgentController.getModel().setPosition(actual2);
            Assert.assertEquals(poSalesAgentController.getModel().getPosition(), actual2);
            poSalesAgentController.getModel().setProfession(actual3);
            Assert.assertEquals(poSalesAgentController.getModel().getProfession(), actual3);
            poSalesAgentController.getModel().setCompany(actual4);
            Assert.assertEquals(poSalesAgentController.getModel().getCompany(), actual4);
            
            loJSON = poSalesAgentController.saveRecord();
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
            poSalesAgentController.initialize();
            loJSON = poSalesAgentController.openRecord("M00125000005");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesAgentController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesAgentController.getModel().getColumn(lnCol) + " ->> " + poSalesAgentController.getModel().getValue(lnCol));
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
            
            poSalesAgentController.initialize();
            loJSON = poSalesAgentController.openRecord("M00125000005");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesAgentController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesAgentController.getModel().getColumn(lnCol) + " ->> " + poSalesAgentController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poSalesAgentController.activateRecord();
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
            
            poSalesAgentController.initialize();
            loJSON = poSalesAgentController.openRecord("M00125000005");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesAgentController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesAgentController.getModel().getColumn(lnCol) + " ->> " + poSalesAgentController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poSalesAgentController.deactivateRecord();
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
