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
import ph.com.guanzongroup.cas.sales.t1.Salesman;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testSalesman {
    static GRiderCAS instance;
    static Salesman poSalesmanController;
    @BeforeClass
    public static void setUpClass() throws SQLException, GuanzonException {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();
        poSalesmanController = new SalesControllers(instance, null).Salesman();
    }

    @Test
    public void testNewRecord() {
        String actual = "M001000001";
        String actual2 = "Michael";
        String actual3 = "Torres";
        String actual4 = "Cuison";
        String actual5 = instance.getBranchCode();
        JSONObject loJSON;

        try {

            poSalesmanController.initialize();
            loJSON = poSalesmanController.newRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            
            poSalesmanController.getModel().setEmployeeId(actual);
            Assert.assertEquals(poSalesmanController.getModel().getEmployeeId(), actual);
            poSalesmanController.getModel().setFirstName(actual2);
            Assert.assertEquals(poSalesmanController.getModel().getFirstName(), actual2);
            poSalesmanController.getModel().setMiddleName(actual3);
            Assert.assertEquals(poSalesmanController.getModel().getMiddleName(), actual3);
            poSalesmanController.getModel().setLastName(actual4);
            Assert.assertEquals(poSalesmanController.getModel().getLastName(), actual4);
            poSalesmanController.getModel().setBranchCode(actual5);
            Assert.assertEquals(poSalesmanController.getModel().getBranchCode(), actual5);

            loJSON = poSalesmanController.saveRecord();
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
        String actual = "A00120000016";
        String actual2 = "Juana";
        String actual3 = "Abalos";
        String actual4 = "Dela Cruz";
        String actual5 = instance.getBranchCode();
        JSONObject loJSON;

        try { 
            poSalesmanController.initialize();
            loJSON = poSalesmanController.openRecord(actual);
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }

            loJSON = poSalesmanController.updateRecord();
            if (!"success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            
            poSalesmanController.getModel().setEmployeeId(actual);
            Assert.assertEquals(poSalesmanController.getModel().getEmployeeId(), actual);
            poSalesmanController.getModel().setFirstName(actual2);
            Assert.assertEquals(poSalesmanController.getModel().getFirstName(), actual2);
            poSalesmanController.getModel().setMiddleName(actual3);
            Assert.assertEquals(poSalesmanController.getModel().getMiddleName(), actual3);
            poSalesmanController.getModel().setLastName(actual4);
            Assert.assertEquals(poSalesmanController.getModel().getLastName(), actual4);
            poSalesmanController.getModel().setBranchCode(actual5);
            Assert.assertEquals(poSalesmanController.getModel().getBranchCode(), actual5);
            
            loJSON = poSalesmanController.saveRecord();
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
            poSalesmanController.initialize();
            loJSON = poSalesmanController.openRecord("A00120000016");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesmanController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesmanController.getModel().getColumn(lnCol) + " ->> " + poSalesmanController.getModel().getValue(lnCol));
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
            
            poSalesmanController.initialize();
            loJSON = poSalesmanController.openRecord("A00120000016");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesmanController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesmanController.getModel().getColumn(lnCol) + " ->> " + poSalesmanController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poSalesmanController.activateRecord();
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
            
            poSalesmanController.initialize();
            loJSON = poSalesmanController.openRecord("A00120000016");
            if (!"success".equals((String) loJSON.get("result"))){
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            } 

            //retreiving using column index
            for (int lnCol = 1; lnCol <= poSalesmanController.getModel().getColumnCount(); lnCol++){
                System.out.println(poSalesmanController.getModel().getColumn(lnCol) + " ->> " + poSalesmanController.getModel().getValue(lnCol));
            }
            //retreiving using field descriptions
            
            loJSON = poSalesmanController.deactivateRecord();
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
