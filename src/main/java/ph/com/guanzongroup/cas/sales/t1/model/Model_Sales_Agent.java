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
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.client.model.Model_Client_Master;
import org.guanzon.cas.client.services.ClientModels;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Model_Sales_Agent extends Model {
    
    //reference objects
    Model_Client_Master poClient;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("dModified", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sClientID";

            //initialize reference objects
            ClientModels clientModel = new ClientModels(poGRider);
            poClient = clientModel.ClientMaster();
            
//            end - initialize reference objects

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setClientId(String clientId) {
        return setValue("sClientID", clientId);
    }

    public String getClientId() {
        return (String) getValue("sClientID");
    }

    public JSONObject setProfession(String profession) {
        return setValue("sProfessn", profession);
    }

    public String getProfession() {
        return (String) getValue("sProfessn");
    }
    
    public JSONObject setCompany(String company) {
        return setValue("sCompanyx", company);
    }

    public String getCompany() {
        return (String) getValue("sCompanyx");
    }

    public JSONObject setPosition(String position) {
        return setValue("sPosition", position);
    }

    public String getPosition() {
        return (String) getValue("sPosition");
    }

    public JSONObject setRecordStatus(boolean recordStatus) {
        return setValue("cRecdStat", recordStatus ? "1" : "0");
    }

    public boolean getRecordStatus() {
        return ((String) getValue("cRecdStat")).equals("1");
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
        return "";
    }

    //reference object models
    public Model_Client_Master Client() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sClientID"))) {
            if (poClient.getEditMode() == EditMode.READY
                    && poClient.getClientId().equals((String) getValue("sClientID"))) {
                return poClient;
            } else {
                poJSON = poClient.openRecord((String) getValue("sClientID"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poClient;
                } else {
                    poClient.initialize();
                    return poClient;
                }
            }
        } else {
            poClient.initialize();
            return poClient;
        }
    }
    
    //end - reference object models

}
