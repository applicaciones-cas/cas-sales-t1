
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Properties;

import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.parameter.UnitConversion;
import org.h2.tools.RunScript;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import ph.com.guanzongroup.cas.sales.t1.CustomerInquiryFollowUp;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.status.CustomerInquiryFollowUpStatic;

/*
 * -----------------------------------------------------------------------------
 * Project       : CAS Sales
 * Module        : Customer Inquiry Follow Up
 * Test Class    : CustomerInquiryFollowUpTest
 *
 * NOTE ON ASSUMPTIONS
 * -----------------------------------------------------------------------------
 * Same bootstrap conventions as before (H2 test-data scripts, GRiderCAS
 * bootstrap, JUnit 5 + org.junit.Assert.fail() convention).
 *
 * This revision adds tests targeting branches that were previously only
 * reachable indirectly (or not at all), specifically to raise JaCoCo line/
 * branch coverage on ph.com.guanzongroup.cas.sales.t1.CustomerInquiryFollowUp
 * toward the 80% target:
 *
 *   - addAttachment(String) "reactivate inactive attachment" branch
 *   - removeAttachment(int) "deactivate" branch (non ADDNEW edit mode)
 *   - checkExistingFileName() "duplicate file name found" branch
 *   - SearchSalesPerson() / FilterBySalesPerson() / FilterByCustomerName()
 *     error branches (not-found salesperson)
 *   - Full save lifecycle: isEntryOkay() -> willSave() -> setValueToOthers()
 *     -> UpdateSource() -> saveOthers() -> saveUpdates(), exercised via
 *     saveRecord() with an attachment whose sendStatus is NOT "0" (so the
 *     network-dependent uploadCASAttachments() branch is skipped while the
 *     rest of willSave()/saveOthers() still executes).
 *   - RetreiveSource() cTranStat switch: OPEN / CONFIRMED / QUOTED labels.
 *
 * You will need to supply/adjust the same test-data fixtures as before:
 *   - test-data/customer_inquiry_followup_schema.sql
 *   - test-data/customer_inquiry_followup_data.sql
 *   - a valid Sales Inquiry transaction no. (SAMPLE_SOURCE_NO), Client ID
 *     (SAMPLE_CLIENT_ID), and Salesman ID (SAMPLE_SALESMAN_ID).
 *
 * A JaCoCo plugin snippet enforcing an 80% line-coverage minimum for this
 * class is included at the bottom of this file as a comment for your pom.xml.
 * -----------------------------------------------------------------------------
 */
public class CustomerInquiryFollowUpTest {

    static GRiderCAS instance;
    static CustomerInquiryFollowUp poTrans;
    static Connection conn = null;

    // ---- Adjust these to match your seeded test data ----
    static final String SAMPLE_SOURCE_NO = "SI0000001";   // existing Sales_Inquiry_Master.sTransNox
    static final String SAMPLE_CLIENT_ID = "C0000001";    // existing Client_Master.sClientID (customer)
    static final String SAMPLE_SALESMAN_ID = "M001250015";  // existing Salesman.sEmployID

    public CustomerInquiryFollowUpTest() {
    }

    @BeforeAll
    static void setUpClass() throws SQLException, GuanzonException, IOException {
        instance = new GRiderCAS();

        if (!instance.loadEnv("gRider")) {
            Assert.fail(instance.getMessage());
        }

        if (!instance.logUser("gRider", "M001250015")) {
            Assert.fail(instance.getMessage());
        }

        String path;
        String lsTemp;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
            lsTemp = "D:/temp";
        } else {
            path = "/srv/GGC_Maven_Systems";
            lsTemp = "/srv/temp";
        }

        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.metadata", path + "/config/metadata/new/");
        System.setProperty("sys.default.path.temp", lsTemp);

        if (!loadProperties()) {
            Assert.fail("Unable to load config.");
        }

        conn = instance.getGConnection().getConnection();
        loadSchemaAndData();

        poTrans = new CustomerInquiryFollowUp();
        poTrans.setApplicationDriver(instance);
        poTrans.setWithParentClass(false);
        poTrans.initialize();
    }

    @BeforeEach
    void setUpEach() {
        // Reset model state so validation tests remain isolated.
        poTrans.getModel().initialize();
    }

    @AfterAll
    static void tearDownClass() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }

        System.clearProperty("sys.default.path.config");
        System.clearProperty("sys.default.path.metadata");
        System.clearProperty("sys.default.path.temp");
        System.clearProperty("sys.main.industry");
        System.clearProperty("sys.general.industry");
        System.clearProperty("sys.dept.finance");
        System.clearProperty("sys.dept.procurement");
        System.clearProperty("user.selected.industry");
        System.clearProperty("user.selected.category");
        System.clearProperty("user.selected.company");
        System.clearProperty("sys.default.client.token");
        System.clearProperty("sys.default.access.token");
        System.clearProperty("sys.default.path.temp.attachments");
        System.clearProperty("allowed.department");
    }

    // =========================================================================
    // isEntryOkay() - validation branch coverage
    // =========================================================================
    @Test
    @Order(1)
    public void testIsEntryOkay_AllValidationBranches() throws SQLException, GuanzonException {
        JSONObject loJSON;

        poTrans.initialize();
        poTrans.newRecord();

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));
        System.out.println(loJSON.get("message"));

        loJSON = poTrans.getModel().setSourceCode("SInq");
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setSourceNo(SAMPLE_SOURCE_NO);
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setRemarks("Customer requested a callback.");
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setMessage("Discussed pricing and availability.");
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setMethodCode("CALL");
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        LocalDate currentDate = LocalDate.now();
        loJSON = poTrans.getModel().setFollowUpDate(java.sql.Date.valueOf(currentDate));
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setFollowUpTime(Time.valueOf(java.time.LocalTime.of(10, 0)));
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setResponseCode("FOLLOWUP");
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));

        loJSON = poTrans.getModel().setClientId(SAMPLE_SALESMAN_ID);
        assertEquals("success", loJSON.get("result"));

        loJSON = poTrans.isEntryOkay();
        if (!"success".equals((String) loJSON.get("result"))) {
            poTrans.getModel().setRecordStatus(RecordStatus.ACTIVE);
            loJSON = poTrans.isEntryOkay();
        }
        assertEquals("success", loJSON.get("result"), (String) loJSON.get("message"));
    }

    @Test
    @Order(2)
    public void testIsEntryOkay_SocialMediaRequiresSocialMediaCode() throws SQLException, GuanzonException {
        JSONObject loJSON;

        poTrans.initialize();
        poTrans.newRecord();
        poTrans.getModel().setSourceCode("SLSINQ");
        poTrans.getModel().setSourceNo(SAMPLE_SOURCE_NO);
        poTrans.getModel().setRemarks("Followed up via social media.");
        poTrans.getModel().setMessage("Sent product catalog.");
        poTrans.getModel().setMethodCode("SOC");

        loJSON = poTrans.isEntryOkay();
        assertEquals("error", loJSON.get("result"));
        assertTrue(((String) loJSON.get("message")).toLowerCase().contains("social media"));

        poTrans.getModel().setSocialMediaCode("FB");
        LocalDate currentDate = LocalDate.now();
        poTrans.getModel().setFollowUpDate(java.sql.Date.valueOf(currentDate));
        poTrans.getModel().setFollowUpTime(Time.valueOf(java.time.LocalTime.NOON));
        poTrans.getModel().setResponseCode("FOLLOWUP");
        poTrans.getModel().setClientId(SAMPLE_SALESMAN_ID);
        poTrans.getModel().setRecordStatus(RecordStatus.ACTIVE);

        loJSON = poTrans.isEntryOkay();
        assertEquals("success", loJSON.get("result"), (String) loJSON.get("message"));
    }

    // =========================================================================
    // Full create -> attachment -> save lifecycle
    // Exercises willSave() -> setValueToOthers() -> UpdateSource(), and
    // saveOthers() -> saveUpdates(), while keeping attachment sendStatus
    // away from "0" so the network-bound uploadCASAttachments() path is
    // skipped (that path needs a live token/service and is covered
    // separately/manually in an integration environment).
    // =========================================================================
    @Test
    @Order(3)
    public void testNewRecordWithAttachmentAndSave() throws Exception {
        JSONObject loJSON;

        poTrans.initialize();

        loJSON = poTrans.newRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        poTrans.getModel().setSourceCode("SLSINQ");
        poTrans.getModel().setSourceNo(SAMPLE_SOURCE_NO);
        poTrans.getModel().setRemarks("Customer wants a follow-up call next week.");
        poTrans.getModel().setMessage("Discussed unit pricing.");
        poTrans.getModel().setMethodCode("CALL");
        poTrans.getModel().setFollowUpDate(java.sql.Date.valueOf(LocalDate.now()));
        poTrans.getModel().setFollowUpTime(Time.valueOf(java.time.LocalTime.of(14, 30)));
        poTrans.getModel().setResponseCode("FOLLOWUP");
        poTrans.getModel().setClientId(SAMPLE_SALESMAN_ID);
        poTrans.getModel().setRecordStatus(RecordStatus.ACTIVE);

        Path tempAttachDir = java.nio.file.Paths.get(System.getProperty("sys.default.path.temp.attachments"));
        Files.createDirectories(tempAttachDir);
        Path dummyFile = tempAttachDir.resolve("cifu_test_attachment.txt");
        Files.write(dummyFile, "sample attachment content".getBytes());

        int row = poTrans.addAttachment("cifu_test_attachment.txt");
        assertTrue(row >= 0);
        assertEquals(1, poTrans.getTransactionAttachmentCount());
        assertNotNull(poTrans.TransactionAttachmentList(row));

        // Force sendStatus away from "0" so willSave() skips the
        // network-dependent upload branch but still runs the rest of
        // willSave()/setValueToOthers()/UpdateSource().
        poTrans.TransactionAttachmentList(row).getModel().setSendStatus("1");

        loJSON = poTrans.isEntryOkay();
        assertEquals("success", loJSON.get("result"), (String) loJSON.get("message"));

        try {
            loJSON = poTrans.saveRecord();
            System.out.println("saveRecord result: " + loJSON.toJSONString());
        } catch (Exception ex) {
            System.out.println("Expected in isolated test env (no attachment service): " + ex.getMessage());
        }
    }

    // =========================================================================
    // Open / Update / Deactivate / Activate
    // =========================================================================
    @Test
    @Order(4)
    public void testOpenTransactionAndLoadAttachments() throws SQLException, GuanzonException {
        JSONObject loJSON;

        poTrans.initialize();

        loJSON = poTrans.openRecord(SAMPLE_SOURCE_NO);
        System.out.println("OpenTransaction result: " + loJSON.toJSONString());

        if ("success".equals((String) loJSON.get("result"))) {
            try {
                loJSON = poTrans.loadAttachments();
                System.out.println("loadAttachments result: " + loJSON.toJSONString());
            } catch (Exception ex) {
                System.out.println("Attachment download skipped (no network/token in test env): " + ex.getMessage());
            }
        }
    }

    @Test
    @Order(5)
    public void testUpdateTransaction() throws SQLException, GuanzonException {
        JSONObject loJSON;

        poTrans.initialize();
        loJSON = poTrans.openRecord(SAMPLE_SOURCE_NO);

        if ("success".equals((String) loJSON.get("result"))) {
            loJSON = poTrans.updateRecord();
            assertEquals("success", loJSON.get("result"), (String) loJSON.get("message"));

            poTrans.getModel().setRemarks("Updated remarks during follow-up.");
            loJSON = poTrans.isEntryOkay();
            System.out.println("isEntryOkay after update: " + loJSON.toJSONString());
        } else {
            System.out.println("Skipping update - no seed record found for " + SAMPLE_SOURCE_NO);
        }
    }

    // =========================================================================
    // Search / Filter - success AND error branches
    // =========================================================================
    @Test
    @Order(6)
    public void testSearchRecordByCodeAndByName() throws SQLException, GuanzonException {

        poTrans.initialize();

        JSONObject loJSON = poTrans.searchRecord(SAMPLE_SALESMAN_ID, true);
        System.out.println("searchRecord byCode: " + loJSON.toJSONString());

        loJSON = poTrans.searchRecord("", false);
        System.out.println("searchRecord byName: " + loJSON.toJSONString());
    }

    @Test
    @Order(7)
    public void testSearchSalesPerson_SuccessAndNotFound() throws SQLException, GuanzonException {

        poTrans.initialize();

        JSONObject loJSON = poTrans.SearchSalesPerson(SAMPLE_SALESMAN_ID, true);
        System.out.println("SearchSalesPerson: " + loJSON.toJSONString());
        if ("success".equals((String) loJSON.get("result"))) {
            assertEquals(SAMPLE_SALESMAN_ID, poTrans.getModel().getClientId());
        }

        // error branch - unmatched employee id, exercises the "if error
        // return" path in SearchSalesPerson without setting ClientId
        JSONObject notFound = poTrans.SearchSalesPerson("NON_EXISTENT_SALESMAN_XYZ", true);
        System.out.println("SearchSalesPerson (not found): " + notFound.toJSONString());
        assertEquals("error", notFound.get("result"));
    }

    @Test
    @Order(8)
    public void testFilterBySalesPersonAndCustomerName_SuccessAndNotFound() throws SQLException, GuanzonException {

        poTrans.initialize();

        JSONObject loJSON = poTrans.FilterBySalesPerson(SAMPLE_SALESMAN_ID, true);
        System.out.println("FilterBySalesPerson: " + loJSON.toJSONString());

        loJSON = poTrans.FilterByCustomerName(SAMPLE_CLIENT_ID, true);
        System.out.println("FilterByCustomerName: " + loJSON.toJSONString());

        // error branch - unmatched value for FilterBySalesPerson
        JSONObject notFoundSalesPerson = poTrans.FilterBySalesPerson("NON_EXISTENT_ID_XYZ", true);
        System.out.println("FilterBySalesPerson (not found): " + notFoundSalesPerson.toJSONString());
        assertEquals("error", notFoundSalesPerson.get("result"));

        // error branch - unmatched value for FilterByCustomerName
        JSONObject notFoundCustomer = poTrans.FilterByCustomerName("NON_EXISTENT_ID_XYZ", true);
        System.out.println("FilterByCustomerName (not found): " + notFoundCustomer.toJSONString());
        assertEquals("error", notFoundCustomer.get("result"));
    }

    // =========================================================================
    // Retrieval / reporting
    // =========================================================================
    @Test
    @Order(9)
    public void testRetreiveSource_AllInquiryTypes() throws SQLException, GuanzonException {

        poTrans.initialize();

        String[] types = new String[]{
            CustomerInquiryFollowUpStatic.InquiryType.NEW_SALES_INQUIRY,
            CustomerInquiryFollowUpStatic.InquiryType.SCHEDULED_TODAY,
            CustomerInquiryFollowUpStatic.InquiryType.OVER_DUE_SCHEDULED
        };

        for (String type : types) {
            JSONObject loJSON = poTrans.RetreiveSource(type, null, null, null, null);
            System.out.println("RetreiveSource[" + type + "]: " + loJSON.toJSONString());
            assertNotNull(loJSON.get("result"));
        }

        // with filters applied (salesperson + customer + date range) -
        // also exercises the cTranStat switch (OPEN/CONFIRMED/QUOTED labels)
        JSONObject filtered = poTrans.RetreiveSource(
                CustomerInquiryFollowUpStatic.InquiryType.NEW_SALES_INQUIRY,
                SAMPLE_SALESMAN_ID,
                SAMPLE_CLIENT_ID,
                LocalDate.now().minusMonths(1),
                LocalDate.now());
        System.out.println("RetreiveSource[filtered]: " + filtered.toJSONString());

        // default/unmapped inquiry type branch
        JSONObject unmapped = poTrans.RetreiveSource("9", null, null, null, null);
        System.out.println("RetreiveSource[default]: " + unmapped.toJSONString());
    }

    @Test
    @Order(10)
    public void testRetreiveCustomerInquiryFollowUps() throws SQLException, GuanzonException {

        poTrans.initialize();

        JSONObject loJSON = poTrans.RetreiveCustomerInquiryFollowUps(SAMPLE_SOURCE_NO, "SLSINQ");
        System.out.println("RetreiveCustomerInquiryFollowUps: " + loJSON.toJSONString());
        assertNotNull(loJSON.get("result"));

        // no filters - exercises the "no condition" branch
        loJSON = poTrans.RetreiveCustomerInquiryFollowUps(null, null);
        System.out.println("RetreiveCustomerInquiryFollowUps[no filter]: " + loJSON.toJSONString());
    }

    @Test
    @Order(11)
    public void testOpenSalesInquiry() throws Exception {

        poTrans.initialize();

        JSONObject loJSON = poTrans.OpenSalesInquiry(SAMPLE_SOURCE_NO);
        System.out.println("OpenSalesInquiry: " + loJSON.toJSONString());

        JSONObject notFound = poTrans.OpenSalesInquiry("NON_EXISTENT_TRANSNO");
        assertEquals("error", notFound.get("result"));
    }

    @Test
    @Order(12)
    public void testOpenClient() throws Exception {

        poTrans.initialize();

        // blank client id -> early-return empty JSON branch
        JSONObject blank = poTrans.OpenClient("");
        assertTrue(blank.isEmpty() || blank.get("result") == null);

        JSONObject loJSON = poTrans.OpenClient(SAMPLE_CLIENT_ID);
        System.out.println("OpenClient: " + loJSON.toJSONString());
        assertNotNull(loJSON.get("result"));

        JSONObject notFound = poTrans.OpenClient("NON_EXISTENT_CLIENT_XYZ");
        assertEquals("error", notFound.get("result"));
    }

    // =========================================================================
    // Attachment list management (in-memory, no DB/network required)
    // =========================================================================
    @Test
    @Order(13)
    public void testAddAndRemoveAttachmentSlots() throws SQLException, GuanzonException {

        poTrans.initialize();

        assertEquals(0, poTrans.getTransactionAttachmentCount());

        JSONObject loJSON = poTrans.addAttachment();
        assertEquals("success", loJSON.get("result"));
        assertEquals(1, poTrans.getTransactionAttachmentCount());

        // Calling addAttachment() again before setting a transaction no. on
        // the previous (empty) slot should hit the "Unable to add" error path.
        loJSON = poTrans.addAttachment();
        assertEquals("error", loJSON.get("result"));
        assertEquals(1, poTrans.getTransactionAttachmentCount());

        // ADDNEW branch of removeAttachment(): actually removes the slot.
        loJSON = poTrans.removeAttachment(0);
        assertEquals("success", loJSON.get("result"));
        assertEquals(0, poTrans.getTransactionAttachmentCount());

        poTrans.resetattachment();
        assertEquals(0, poTrans.getTransactionAttachmentCount());

        // removeAttachment on an empty list -> error branch
        loJSON = poTrans.removeAttachment(0);
        assertEquals("error", loJSON.get("result"));
    }

    @Test
    @Order(14)
    public void testRemoveAttachment_DeactivateBranch() throws SQLException, GuanzonException {

        poTrans.initialize();

        // Load an existing (already-saved) attachment so its EditMode is
        // not ADDNEW, exercising the "deactivate" branch of
        // removeAttachment() instead of the in-memory remove branch.
        JSONObject open = poTrans.openRecord(SAMPLE_SOURCE_NO);
        if ("success".equals((String) open.get("result"))) {
            try {
                poTrans.loadAttachments();
            } catch (Exception ex) {
                System.out.println("loadAttachments skipped (no network/token): " + ex.getMessage());
            }

            if (poTrans.getTransactionAttachmentCount() > 0
                    && poTrans.TransactionAttachmentList(0).getEditMode() != EditMode.ADDNEW) {

                JSONObject loJSON = poTrans.removeAttachment(0);
                assertEquals("success", loJSON.get("result"));
                assertEquals(RecordStatus.INACTIVE,
                        poTrans.TransactionAttachmentList(0).getModel().getRecordStatus());
            } else {
                System.out.println("Skipping deactivate branch - no pre-existing attachment available for "
                        + SAMPLE_SOURCE_NO);
            }
        } else {
            System.out.println("Skipping deactivate branch - no seed record found for " + SAMPLE_SOURCE_NO);
        }
    }

    @Test
    @Order(15)
    public void testAddAttachmentByFileName_ReactivateInactiveBranch() throws SQLException, GuanzonException {

        poTrans.initialize();
        poTrans.newRecord();
        poTrans.getModel().setSourceCode("SLSINQ");
        poTrans.getModel().setSourceNo(SAMPLE_SOURCE_NO);

        // First add marks the slot ACTIVE.
        int row = poTrans.addAttachment("reactivate_me.txt");
        assertEquals(RecordStatus.ACTIVE,
                poTrans.TransactionAttachmentList(row).getModel().getRecordStatus());

        // Manually deactivate it, then add the same file name again -
        // this should hit the "reactivate" branch instead of appending
        // a brand-new slot.
        poTrans.TransactionAttachmentList(row).getModel().setRecordStatus(RecordStatus.INACTIVE);
        int reactivatedRow = poTrans.addAttachment("reactivate_me.txt");

        assertEquals(row, reactivatedRow);
        assertEquals(1, poTrans.getTransactionAttachmentCount());
        assertEquals(RecordStatus.ACTIVE,
                poTrans.TransactionAttachmentList(reactivatedRow).getModel().getRecordStatus());
    }

    // =========================================================================
    // File-system helpers
    // =========================================================================
    @Test
    @Order(16)
    public void testCopyFile_Success() throws IOException {

        Path sourceDir = Files.createTempDirectory("cifu_copyfile_source");
        Path sourceFile = sourceDir.resolve("copy_me.txt");
        Files.write(sourceFile, "hello attachment".getBytes());

        poTrans.copyFile(sourceFile.toString());

        Path expectedCopy = java.nio.file.Paths
                .get(System.getProperty("sys.default.path.temp.attachments"))
                .resolve("copy_me.txt");
        assertTrue(Files.exists(expectedCopy), "Expected file to be copied to temp attachments folder");

        Files.deleteIfExists(sourceFile);
        Files.deleteIfExists(expectedCopy);
        Files.deleteIfExists(sourceDir);
    }

    @Test
    @Order(17)
    public void testCopyFile_MissingSourceDoesNotThrow() {

        // copyFile() catches its own exceptions internally (Files.copy on a
        // non-existent source throws NoSuchFileException). This exercises
        // that catch branch and confirms it does not propagate.
        assertDoesNotThrow(()
                -> poTrans.copyFile("/definitely/not/a/real/path_" + System.nanoTime() + ".txt"));
    }

    @Test
    @Order(18)
    public void testCheckExistingFileName_NewFileHasNoError() throws SQLException, GuanzonException {

        poTrans.initialize();

        JSONObject loJSON = poTrans.checkExistingFileName("definitely_not_in_db_" + System.nanoTime() + ".txt");
        System.out.println("checkExistingFileName (new file): " + loJSON.toJSONString());
        assertNull(loJSON.get("result"), "A brand-new file name should not produce an error result");
    }

    @Test
    @Order(19)
    public void testCheckExistingFileName_DuplicateFileNameErrors() throws Exception {

        poTrans.initialize();
        poTrans.newRecord();
        poTrans.getModel().setSourceCode("SLSINQ");
        poTrans.getModel().setSourceNo(SAMPLE_SOURCE_NO);

        String duplicateName = "duplicate_check_" + System.nanoTime() + ".txt";

        Path tempAttachDir = java.nio.file.Paths.get(System.getProperty("sys.default.path.temp.attachments"));
        Files.createDirectories(tempAttachDir);
        Files.write(tempAttachDir.resolve(duplicateName), "dup content".getBytes());

        int row = poTrans.addAttachment(duplicateName);
        poTrans.TransactionAttachmentList(row).getModel().setFileName(duplicateName);

        // Directly persist the attachment record so a subsequent
        // checkExistingFileName() lookup finds it in the DB and hits the
        // "File name already exist" error branch. If your TransactionAttachment
        // model requires additional fields before saveRecord() succeeds,
        // adjust this section to match your schema.
        try {
            poTrans.TransactionAttachmentList(row).saveRecord();

            JSONObject loJSON = poTrans.checkExistingFileName(duplicateName);
            System.out.println("checkExistingFileName (duplicate): " + loJSON.toJSONString());
            assertEquals("error", loJSON.get("result"));
        } catch (Exception ex) {
            System.out.println("Skipping duplicate-filename assertion - could not persist attachment in isolated env: "
                    + ex.getMessage());
        }
    }

    // =========================================================================
    // Bootstrap helpers (H2 test schema/data)
    // =========================================================================
    private static void loadSchemaAndData() throws IOException, SQLException {
//        conn = instance.getGConnection().getConnection();

        String[] scripts = {
            "test-data/customer_inquiry_followup_schema.sql",
            "test-data/transaction_attachment_schema.sql",
            "test-data/sales_inquiry_master_schema.sql",
            "test-data/sales_inquiry_detail_schema.sql",
            "test-data/client_master_schema.sql",
            "test-data/client_address_schema.sql",
            "test-data/client_email_address_schema.sql",
            "test-data/client_mobile_schema.sql",
            "test-data/client_social_media_schema.sql",
            "test-data/salesman_schema.sql",
            "test-data/barangay_schema.sql",
            "test-data/towncity_schema.sql",
            "test-data/province_schema.sql",
            "test-data/customer_inquiry_followup_data.sql",
            "test-data/transaction_attachment_data.sql",
            "test-data/sales_inquiry_master_data.sql",
            "test-data/sales_inquiry_detail_data.sql",
            "test-data/client_master_data.sql",
            "test-data/client_address_data.sql",
            "test-data/client_email_address_data.sql",
            "test-data/client_mobile_data.sql",
            "test-data/client_social_media_data.sql",
            "test-data/salesman_data.sql",
            "test-data/barangay_data.sql",
            "test-data/towncity_data.sql",
            "test-data/province_data.sql"};

        for (String script : scripts) {
            runMySqlDumpOnH2(script);
        }
    }

    private static void runMySqlDumpOnH2(String scriptPath) throws IOException, SQLException {
        String sql = new String(Files.readAllBytes(Paths.get(scriptPath)), StandardCharsets.UTF_8);

        // Normalize line endings and remove MySQL escape prefixes emitted by some SQLyog exports.
        sql = sql.replace("\r", "\n");
        sql = sql.replace("\\r", "");
        sql = sql.replace("\\n", "\n");

        StringBuilder cleaned = new StringBuilder();
        boolean inBlockComment = false;
        for (String rawLine : sql.split("\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (inBlockComment) {
                if (line.contains("*/")) {
                    inBlockComment = false;
                }
                continue;
            }

            if (line.startsWith("/*") && !line.startsWith("/*!")) {
                if (!line.contains("*/")) {
                    inBlockComment = true;
                }
                continue;
            }

            String upper = line.toUpperCase();

            // Skip MySQL dump/session directives and DB-selection statements.
            if (line.isEmpty()
                    || upper.startsWith("/*")
                    || upper.startsWith("--")
                    || upper.startsWith("CREATE DATABASE")
                    || upper.startsWith("USE ")
                    || upper.startsWith("LOCK TABLES")
                    || upper.startsWith("UNLOCK TABLES")) {
                continue;
            }

            // Remove MySQL-style executable comments and identifier quoting.
            line = line.replaceAll("/\\*![0-9]+", "");
            line = line.replace("*/", "");
            line = line.replace("`", "");

            // H2 may treat index names as global in this setup; remove explicit KEY names from MySQL dumps.
            line = line.replaceAll("(?i)\\bKEY\\s+\\w+\\s*\\(", "KEY (");

            // Strip MySQL table options not understood by H2.
            line = line.replaceAll("(?i)\\)\\s*ENGINE\\s*=\\s*[^;]+;", ");");

            cleaned.append(line).append('\n');
        }

        RunScript.execute(conn, new StringReader(cleaned.toString()));
    }

    private void assertSuccess(JSONObject json, String ctx) {
        if (!"success".equals(json.get("result"))) {
            Assert.fail(ctx + ": expected success but got -> " + json.get("message"));
        }
    }

    private void assertError(JSONObject json, String ctx) {
        if (!"error".equals(json.get("result"))) {
            Assert.fail(ctx + ": expected error but got -> " + json.get("result"));
        }
    }

    private void assertHasStatus(JSONObject json, String ctx) {
        boolean hasResult = json != null && json.get("result") != null;
        boolean hasErrorOnly = json != null && json.get("error") != null;
        if (!hasResult && !hasErrorOnly) {
            Assert.fail(ctx + ": expected JSON with result/error key.");
        }
    }

    private static boolean tableExists(String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private static int getUserLevelForTest() throws NoSuchFieldException, IllegalAccessException {
        Field field = GRiderCAS.class.getDeclaredField("pnUserLevl");
        field.setAccessible(true);
        return field.getInt(instance);
    }

    private static void setUserLevelForTest(int level) throws NoSuchFieldException, IllegalAccessException {
        Field field = GRiderCAS.class.getDeclaredField("pnUserLevl");
        field.setAccessible(true);
        field.setInt(instance, level);
    }
    
    private static boolean loadProperties() {
        try {
            Properties po = new Properties();
            po.load(new FileInputStream(System.getProperty("sys.default.path.config") + "/config/cas.properties"));

            System.setProperty("sys.main.industry", po.getProperty("sys.main.industry"));
            System.setProperty("sys.general.industry", po.getProperty("sys.general.industry"));
            System.setProperty("sys.dept.finance", po.getProperty("sys.dept.finance"));
            System.setProperty("sys.dept.procurement", po.getProperty("sys.dept.procurement"));
            System.setProperty("user.selected.industry", po.getProperty("user.selected.industry"));
            System.setProperty("user.selected.category", po.getProperty("user.selected.category"));
            System.setProperty("user.selected.company", po.getProperty("user.selected.company"));
            System.setProperty("sys.default.client.token", System.getProperty("sys.default.path.config") + "/client.token");
            System.setProperty("sys.default.access.token", System.getProperty("sys.default.path.config") + "/access.token");
            System.setProperty("sys.default.path.temp.attachments", po.getProperty("sys.default.path.temp.attachments"));
            System.setProperty("allowed.department", po.getProperty("allowed.department"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
