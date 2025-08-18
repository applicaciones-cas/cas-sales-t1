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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ph.com.guanzongroup.cas.sales.t1.RequirementsSource;
import ph.com.guanzongroup.cas.sales.t1.RequirementsSourcePerGroup;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testRequirementSourcePerGroup {
    static GRiderCAS instance;
    static RequirementsSourcePerGroup poController;
    @BeforeClass
    public static void setUpClass() throws SQLException, GuanzonException {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();
        poController = new SalesControllers(instance, null).RequirementsSourcePerGroup();
    }

    @Test
    public void testNewRecord() {
        String actual = "0001";
        String actual2 = "2";
        String actual3 = "0";
        JSONObject loJSON;

        try {

            poController.initialize();
            loJSON = poController.newRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poController.getModel().setRequirementCode(actual);
            Assert.assertEquals(poController.getModel().getRequirementCode(), actual);
            poController.getModel().setCustomerGroup(actual2);
            Assert.assertEquals(poController.getModel().getCustomerGroup(), actual2);
            poController.getModel().setPaymentMode(actual3);
            Assert.assertEquals(poController.getModel().getPaymentMode(), actual3);
            poController.getModel().isRequired(true);
            Assert.assertEquals(poController.getModel().isRequired(), true);

            loJSON = poController.saveRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
        } catch (ExceptionInInitializerError e) {
            System.err.println(MiscUtil.getException(e));
            Assert.fail();
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }
    
//    @Test
    public void testUpdateTransaction() {
        JSONObject loJSON;

        try { 
            poController.initialize();
            loJSON = poController.openRecord("0001");
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poController.updateRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poController.getModel().setCustomerGroup("PRC");
            
            loJSON = poController.saveRecord();
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
            poController.initialize();
            loJSON = poController.openRecord("001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.getModel().getColumnCount(); lnCol++){
                System.out.println(poController.getModel().getColumn(lnCol) + " ->> " + poController.getModel().getValue(lnCol));
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
            
            poController.initialize();
            loJSON = poController.openRecord("001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.getModel().getColumnCount(); lnCol++){
                System.out.println(poController.getModel().getColumn(lnCol) + " ->> " + poController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poController.activateRecord();
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
            
            poController.initialize();
            loJSON = poController.openRecord("001");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poController.getModel().getColumnCount(); lnCol++){
                System.out.println(poController.getModel().getColumn(lnCol) + " ->> " + poController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poController.deactivateRecord();
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
