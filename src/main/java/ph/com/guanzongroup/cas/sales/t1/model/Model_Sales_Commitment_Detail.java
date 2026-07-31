/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.inv.model.Model_Inventory;
import org.guanzon.cas.inv.services.InvModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.status.BankApplicationStatus;

/**
 *
 * @author Arsiela
 */
public class Model_Sales_Commitment_Detail extends Model {

    Model_Inventory poInventory;
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            // assign default values
            poEntity.updateObject("nEntryNox", 0);
            poEntity.updateObject("nQuantity", 0);
            poEntity.updateObject("nQtyRelsd", 0);
            poEntity.updateObject("nUnitPrce", 0.0000);
            poEntity.updateString("cReversex", BankApplicationStatus.Reverse.INCLUDE);
            // end - assign default values
            InvModels invModel = new InvModels(poGRider);
            poInventory = invModel.Inventory();

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sTransNox";
            ID2 = "nEntryNox";

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setTransactionNo(String transactionNo) {
        return setValue("sTransNox", transactionNo);
    }

    public String getTransactionNo() {
        return (String) getValue("sTransNox");
    }

    public JSONObject setEntryNo(int entryNo) {
        return setValue("nEntryNox", entryNo);
    }

    public int getEntryNo() {
        if (getValue("nEntryNox") == null || "".equals(getValue("nEntryNox"))) {
            return 0;
        }
        return (int) getValue("nEntryNox");
    }

    public JSONObject setStockId(String stockId) {
        return setValue("sStockIDx", stockId);
    }

    public String getStockId() {
        return (String) getValue("sStockIDx");
    }

    public JSONObject setQuantity(int quantity) {
        return setValue("nQuantity", quantity);
    }

    public int getQuantity() {
        if (getValue("nQuantity") == null || "".equals(getValue("nQuantity"))) {
            return 0;
        }
        return (int) getValue("nQuantity");
    }

    public JSONObject setQuantityReleased(int quantityReleased) {
        return setValue("nQtyRelsd", quantityReleased);
    }

    public int getQuantityReleased() {
        if (getValue("nQtyRelsd") == null || "".equals(getValue("nQtyRelsd"))) {
            return 0;
        }
        return (int) getValue("nQtyRelsd");
    }
    
    public JSONObject setUnitPrice(double unitPrice) {
        return setValue("nUnitPrce", unitPrice);
    }

    public double getUnitPrice() {
        return Double.parseDouble(String.valueOf(getValue("nUnitPrce")));
    }


    public JSONObject isReversed(boolean reversed) {
        return setValue("cReversex", reversed ? "+" : "-");
    }

    public boolean isReversed() {
        return "+".equals(getValue("cReversex"));
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return "";
    }

    public Model_Inventory Inventory() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sStockIDx"))) {
            if (poInventory.getEditMode() == EditMode.READY
                    && poInventory.getStockId().equals((String) getValue("sStockIDx"))) {
                return poInventory;
            } else {
                poJSON = poInventory.openRecord((String) getValue("sStockIDx"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poInventory;
                } else {
                    poInventory.initialize();
                    return poInventory;
                }
            }
        } else {
            poInventory.initialize();
            return poInventory;
        }
    }

}


