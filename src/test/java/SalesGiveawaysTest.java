import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.h2.tools.RunScript;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ph.com.guanzongroup.cas.sales.t1.SalesGiveaways;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.status.SalesGiveawaysStatus;
import org.guanzon.appdriver.agent.services.Transaction;
import org.guanzon.appdriver.constant.EditMode;
import java.lang.reflect.Field;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SalesGiveawaysTest {
    static GRiderCAS instance;
    static SalesGiveaways poController;
    static Connection conn;

    static String testIndustryId = "05";
    static String testCategoryId = "0008";
    static String testStockId = "P0W125000002";
    static String sampleGiveawayCode;

    @BeforeClass
    public static void setUpClass() throws GuanzonException, SQLException, IOException {
        instance = new GRiderCAS();

        if (!instance.loadEnv("gRider")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        if (!instance.logUser("gRider", "M001250015")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        loadCorePrimary();
        sampleGiveawayCode = resolveSampleGiveawayCode();

        String path;
        String tempPath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
            tempPath = "D:/temp";
        } else {
            path = "/srv/GGC_Maven_Systems";
            tempPath = "/srv/temp";
        }

        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.metadata", path + "/config/metadata/new/");
        System.setProperty("sys.default.path.temp", tempPath);

        if (!loadProperties()) {
            System.err.println("Unable to load config.");
            System.exit(1);
        }

        resetController();
    }

    @AfterClass
    public static void tearDownClass() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
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

    @Test
    public void test01InitTransaction() {
        JSONObject loJSON = poController.InitTransaction();
        Assert.assertEquals("success", loJSON.get("result"));
        Assert.assertEquals("PARM", poController.getSourceCode());
        Assert.assertNotNull(poController.Master());
        Assert.assertNotNull(poController.getDetail());
    }

    @Test
    public void test02NewTransaction() throws CloneNotSupportedException {
        if (poController == null) {
            resetController();
        }
        Assert.assertNotNull(poController);

        resetController();

        JSONObject loJSON = poController.InitTransaction();
        Assert.assertEquals("success", loJSON.get("result"));

        poController.setIndustryId(testIndustryId);
        poController.setCategoryId(testCategoryId);

        loJSON = poController.NewTransaction();
        Assert.assertEquals("success", loJSON.get("result"));
        Assert.assertEquals(testIndustryId, poController.Master().getIndustryId());
        Assert.assertEquals(testCategoryId, poController.Master().getCategoryCode());
        Assert.assertEquals(SalesGiveawaysStatus.OPEN, poController.Master().getTransactionStatus());
    }

    @Test
    public void test03AddDetailValidationLastRowEmpty() throws CloneNotSupportedException {
        startNewTransaction();

        if (poController.getDetailCount() == 0) {
            JSONObject loJSON = poController.AddDetail();
            Assert.assertEquals("success", loJSON.get("result"));
        }

        int lastRow = poController.getDetailCount() - 1;
        poController.Detail(lastRow).setStockId("");

        JSONObject loJSON = poController.AddDetail();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Last row has empty item.", loJSON.get("message"));
    }

    @Test
    public void test04SaveValidationNoDetail() throws Exception {
        startNewTransaction();

        for (int lnCtr = 0; lnCtr < poController.getDetailCount(); lnCtr++) {
            poController.Detail(lnCtr).setStockId("");
        }

        JSONObject loJSON = poController.SaveTransaction();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("No transaction detail to be save.", loJSON.get("message"));
    }

    @Test
    public void test05SaveValidationMissingGiveawayCode() throws Exception {
        startNewTransaction();

        poController.Master().setDescription("UT Giveaway");
        poController.Master().setGiveawayCode("");
        poController.Master().setIndustryId(testIndustryId);
        poController.Master().setCategoryCode(testCategoryId);

        if (poController.getDetailCount() == 0) {
            JSONObject loJSON = poController.AddDetail();
            Assert.assertEquals("success", loJSON.get("result"));
        }

        poController.Detail(0).setStockId(testStockId);
        poController.Detail(0).setQuantity(1);

        // Use business-rule validation directly to avoid hitting DB constraints first.
        JSONObject loJSON = poController.save();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Giveaway code must not be empty.", loJSON.get("message"));
    }

    @Test
    public void test06StatusMethodsRequireLoadedTransaction() throws Exception {
        Assert.assertNotNull("No giveaways sample transaction available.", sampleGiveawayCode);

        resetController();
        JSONObject loJSON = poController.InitTransaction();
        Assert.assertEquals("success", loJSON.get("result"));

        // Load existing giveaways master directly, then force transaction into READY mode.
        loJSON = poController.Master().openRecord(sampleGiveawayCode);
        Assert.assertEquals("success", loJSON.get("result"));
        setTransactionEditMode(poController);

        // Trigger early status validation branches after loading an existing record.
        poController.Master().setTransactionStatus(SalesGiveawaysStatus.ACTIVE);
        loJSON = poController.ActivateTransaction("confirm");
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Record was already active.", loJSON.get("message"));

        poController.Master().setTransactionStatus(SalesGiveawaysStatus.DEACTIVATE);
        loJSON = poController.DeactiveTransaction("deactivate");
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Record was already deactivated.", loJSON.get("message"));

        poController.Master().setTransactionStatus(SalesGiveawaysStatus.DISAPPROVE);
        loJSON = poController.DisapproveTransaction("disapprove");
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Record was already voided.", loJSON.get("message"));
    }

    @Test
    public void test07GetStatusMappings() {
        Assert.assertEquals("OPEN", poController.getStatus(SalesGiveawaysStatus.OPEN));
        Assert.assertEquals("ACTIVE", poController.getStatus(SalesGiveawaysStatus.ACTIVE));
        Assert.assertEquals("INACTIVE", poController.getStatus(SalesGiveawaysStatus.DEACTIVATE));
        Assert.assertEquals("DISAPPROVE", poController.getStatus(SalesGiveawaysStatus.DISAPPROVE));
        Assert.assertEquals("UNKNOWN", poController.getStatus("9"));
    }

    @Test
    public void test08InitFieldsSetsDefaultValues() {
        resetController();
        JSONObject loJSON = poController.InitTransaction();
        Assert.assertEquals("success", loJSON.get("result"));

        poController.setIndustryId(testIndustryId);
        poController.setCategoryId(testCategoryId);

        loJSON = poController.initFields();
        Assert.assertEquals("success", loJSON.get("result"));
        Assert.assertEquals(testIndustryId, poController.Master().getIndustryId());
        Assert.assertEquals(testCategoryId, poController.Master().getCategoryCode());
        Assert.assertEquals(SalesGiveawaysStatus.OPEN, poController.Master().getTransactionStatus());
        Assert.assertNotNull(poController.Master().getFromDate());
        Assert.assertNotNull(poController.Master().getThruDate());
    }

    @Test
    public void test09ReloadDetailKeepsSingleBlankRow() throws Exception {
        startNewTransaction();
        Assert.assertEquals(1, poController.getDetailCount());

        poController.ReloadDetail();
        Assert.assertEquals(1, poController.getDetailCount());
        Assert.assertTrue(poController.Detail(0).getStockId() == null || "".equals(poController.Detail(0).getStockId()));
    }

    @Test
    public void test10WillSaveAssignsDetailLinkage() throws Exception {
        startNewTransaction();

        poController.Master().setDescription("UT Save");
        poController.Master().setGiveawayCode("UTGWAY000001");
        poController.Master().setIndustryId(testIndustryId);
        poController.Master().setCategoryCode(testCategoryId);

        poController.Detail(0).setStockId(testStockId);
        poController.Detail(0).setQuantity(1);

        JSONObject loJSON = poController.willSave();
        Assert.assertEquals("success", loJSON.get("result"));
        Assert.assertEquals(poController.Master().getGiveawayCode(), poController.Detail(0).getGiveawayCode());
        Assert.assertEquals(1, poController.Detail(0).getEntryNo());
    }

    @Test
    public void test11WillSaveInvalidQuantity() throws Exception {
        startNewTransaction();

        poController.Master().setDescription("UT Save Invalid Qty");
        poController.Master().setGiveawayCode("UTGWAY000005");
        poController.Master().setIndustryId(testIndustryId);
        poController.Master().setCategoryCode(testCategoryId);

        poController.Detail(0).setStockId(testStockId);
        poController.Detail(0).setQuantity(0);

        JSONObject loJSON = poController.willSave();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Invalid quantity at row 1.", loJSON.get("message"));
    }

    @Test
    public void test12SaveValidationMissingDescription() throws Exception {
        startNewTransaction();

        poController.Master().setGiveawayCode("UTGWAY000002");
        poController.Master().setDescription("");
        poController.Master().setIndustryId(testIndustryId);
        poController.Master().setCategoryCode(testCategoryId);

        poController.Detail(0).setStockId(testStockId);
        poController.Detail(0).setQuantity(1);

        JSONObject loJSON = poController.save();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Description must not be empty.", loJSON.get("message"));
    }

    @Test
    public void test13SaveValidationMissingIndustry() throws Exception {
        startNewTransaction();

        poController.Master().setGiveawayCode("UTGWAY000003");
        poController.Master().setDescription("UT Giveaway");
        poController.Master().setIndustryId("");
        poController.Master().setCategoryCode(testCategoryId);

        poController.Detail(0).setStockId(testStockId);
        poController.Detail(0).setQuantity(1);

        JSONObject loJSON = poController.save();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Industry must not be empty.", loJSON.get("message"));
    }

    @Test
    public void test14SaveValidationMissingCategory() throws Exception {
        startNewTransaction();

        poController.Master().setGiveawayCode("UTGWAY000004");
        poController.Master().setDescription("UT Giveaway");
        poController.Master().setIndustryId(testIndustryId);
        poController.Master().setCategoryCode("");

        poController.Detail(0).setStockId(testStockId);
        poController.Detail(0).setQuantity(1);

        JSONObject loJSON = poController.save();
        Assert.assertEquals("error", loJSON.get("result"));
        Assert.assertEquals("Category must not be empty.", loJSON.get("message"));
    }

    @Test
    public void test15IsJSONSuccess() {
        JSONObject loJSON = new JSONObject();
        loJSON.put("result", "success");
        Assert.assertTrue(poController.isJSONSuccess(loJSON));

        loJSON.put("result", "error");
        Assert.assertFalse(poController.isJSONSuccess(loJSON));

        loJSON.put("result", "warning");
        Assert.assertTrue(poController.isJSONSuccess(loJSON));
    }

    @Test
    public void test16OpenTransactionFromSampleData() throws Exception {
        Assert.assertNotNull("No giveaways sample transaction available.", sampleGiveawayCode);

        resetController();
        JSONObject loJSON = poController.InitTransaction();
        Assert.assertEquals("success", loJSON.get("result"));

        loJSON = poController.OpenTransaction(sampleGiveawayCode);
        Assert.assertEquals("success", loJSON.get("result"));
        Assert.assertEquals(sampleGiveawayCode, poController.Master().getGiveawayCode());
    }

    private static void resetController() {
        poController = new SalesControllers(instance, null).SalesGiveaways();
        Assert.assertNotNull(poController);
    }

    private static void startNewTransaction() throws CloneNotSupportedException {
        if (poController == null) {
            resetController();
        }

        JSONObject loJSON = poController.InitTransaction();
        Assert.assertEquals("success", loJSON.get("result"));

        poController.setIndustryId(testIndustryId);
        poController.setCategoryId(testCategoryId);

        loJSON = poController.NewTransaction();
        Assert.assertEquals("success", loJSON.get("result"));
    }

    private static boolean loadProperties() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(System.getProperty("sys.default.path.config") + "/config/cas.properties"));

            System.setProperty("sys.main.industry", props.getProperty("sys.main.industry"));
            System.setProperty("sys.general.industry", props.getProperty("sys.general.industry"));
            System.setProperty("sys.dept.finance", props.getProperty("sys.dept.finance"));
            System.setProperty("sys.dept.procurement", props.getProperty("sys.dept.procurement"));
            System.setProperty("user.selected.industry", props.getProperty("user.selected.industry"));
            System.setProperty("user.selected.category", props.getProperty("user.selected.category"));
            System.setProperty("user.selected.company", props.getProperty("user.selected.company"));
            System.setProperty("sys.default.client.token", System.getProperty("sys.default.path.config") + "/client.token");
            System.setProperty("sys.default.access.token", System.getProperty("sys.default.path.config") + "/access.token");
            System.setProperty("sys.default.path.temp.attachments", props.getProperty("sys.default.path.temp.attachments"));
            System.setProperty("allowed.department", props.getProperty("allowed.department"));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private static void loadCorePrimary() throws IOException, SQLException {
        conn = instance.getGConnection().getConnection();

        List<String> schemaScripts = new ArrayList<>();
        List<String> dataScripts = new ArrayList<>();

        schemaScripts.add("sales_giveaways_master_schema");
        schemaScripts.add("sales_giveaways_item_schema");
        schemaScripts.add("inventory_schema");
        schemaScripts.add("industry_schema");
        schemaScripts.add("parameter_status_history_schema");

        dataScripts.add("sales_giveaways_master_data");
        dataScripts.add("sales_giveaways_item_data");
        dataScripts.add("inventory_data");
        dataScripts.add("industry_data");
        dataScripts.add("parameter_status_history_data");

        for (String schema : schemaScripts) {
            try (FileReader schemaReader = new FileReader("test-data/" + schema + ".sql")) {
                RunScript.execute(conn, schemaReader);
            }
        }

        for (String data : dataScripts) {
            try (FileReader dataReader = new FileReader("test-data/" + data + ".sql")) {
                RunScript.execute(conn, dataReader);
            }
        }

        // Metadata includes nEntryNox on master; keep test schema compatible.
        ensureMasterSchemaCompatibility();
    }

    private static void ensureMasterSchemaCompatibility() throws SQLException {
        String alterSql = "ALTER TABLE sales_giveaways_master ADD COLUMN nEntryNox SMALLINT DEFAULT 0";
        try (PreparedStatement ps = conn.prepareStatement(alterSql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            String message = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (!message.contains("duplicate") && !message.contains("already exists")) {
                throw ex;
            }
        }
    }

    private static String resolveSampleGiveawayCode() throws SQLException {
        String giveawayCode = getFirstGiveawayCode();
        if (giveawayCode != null && !giveawayCode.isEmpty()) {
            return giveawayCode;
        }

        // Fallback when sample data files are empty.
        String fallbackCode = "GCO126000001";
        String insertMaster = "INSERT INTO sales_giveaways_master("
                + "sGAWayCde, sGAWayDsc, sIndstCdx, sCategrCd, sRemarksx, dFromDate, dThruDate, cTranStat, sEntryByx, dEntryDte, sModified, dModified"
                + ") VALUES (?, ?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE, '0', ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(insertMaster)) {
            ps.setString(1, fallbackCode);
            ps.setString(2, "Sample Giveaways");
            ps.setString(3, testIndustryId);
            ps.setString(4, testCategoryId);
            ps.setString(5, "fallback sample");
            ps.setString(6, "M001250015");
            ps.setString(7, "M001250015");
            ps.executeUpdate();
        }

        String insertItem = "INSERT INTO sales_giveaways_item(sGAWayCde, nEntryNox, sStockIDx, nQuantity, cReversex) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
            ps.setString(1, fallbackCode);
            ps.setInt(2, 1);
            ps.setString(3, testStockId);
            ps.setInt(4, 1);
            ps.setString(5, "0");
            ps.executeUpdate();
        }

        return fallbackCode;
    }

    private static String getFirstGiveawayCode() throws SQLException {
        String sql = "SELECT sGAWayCde FROM sales_giveaways_master ORDER BY sGAWayCde LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("sGAWayCde");
            }
        }
        return null;
    }

    private static void setTransactionEditMode(SalesGiveaways controller) throws Exception {
        Field field = Transaction.class.getDeclaredField("pnEditMode");
        field.setAccessible(true);
        field.setInt(controller, EditMode.READY);
    }
}
