/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.status.SalesGiveawaysStatus;

/**
 *
 * @author Arsiela
 */
public class Model_Sales_Giveaways_Master extends Model {

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            // assign default values
            poEntity.updateNull("dFromDate");
            poEntity.updateNull("dThruDate");
            poEntity.updateNull("dEntryDte");
            poEntity.updateNull("dModified");
            poEntity.updateString("cTranStat", SalesGiveawaysStatus.OPEN);
            // end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sGAWayCde";

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setGiveawayCode(String giveawayCode) {
        return setValue("sGAWayCde", giveawayCode);
    }

    public String getGiveawayCode() {
        return (String) getValue("sGAWayCde");
    }

    public JSONObject setDescription(String description) {
        return setValue("sGAWayDsc", description);
    }

    public String getDescription() {
        return (String) getValue("sGAWayDsc");
    }

    public JSONObject setIndustryId(String industryId) {
        return setValue("sIndstCdx", industryId);
    }

    public String getIndustryId() {
        return (String) getValue("sIndstCdx");
    }

    public JSONObject setCategoryCode(String categoryCode) {
        return setValue("sCategrCd", categoryCode);
    }

    public String getCategoryCode() {
        return (String) getValue("sCategrCd");
    }

    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks);
    }

    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }

    public JSONObject setFromDate(Date fromDate) {
        return setValue("dFromDate", fromDate);
    }

    public Date getFromDate() {
        return (Date) getValue("dFromDate");
    }

    public JSONObject setThruDate(Date thruDate) {
        return setValue("dThruDate", thruDate);
    }

    public Date getThruDate() {
        return (Date) getValue("dThruDate");
    }

    public JSONObject setTransactionStatus(String transactionStatus) {
        return setValue("cTranStat", transactionStatus);
    }

    public String getTransactionStatus() {
        return (String) getValue("cTranStat");
    }

    public JSONObject setEntryBy(String entryBy) {
        return setValue("sEntryByx", entryBy);
    }

    public String getEntryBy() {
        return (String) getValue("sEntryByx");
    }

    public JSONObject setEntryDate(Date entryDate) {
        return setValue("dEntryDte", entryDate);
    }

    public Date getEntryDate() {
        return (Date) getValue("dEntryDte");
    }

    public JSONObject setModifyingId(String modifiedBy) {
        return setValue("sModified", modifiedBy);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }
}

