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
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.client.model.Model_Client_Master;
import org.guanzon.cas.client.model.Model_Client_Mobile;
import org.guanzon.cas.client.services.ClientModels;
import org.guanzon.cas.parameter.model.Model_Banks;
import org.guanzon.cas.parameter.model.Model_Branch;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.model.Model_Industry;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class Model_Requirement_Source_PerGroup extends Model {
    
    //reference objects
    Model_Requirement_Source poRequirementSource;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateObject("dModified", SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));
            poEntity.updateObject("cRecdStat", RecordStatus.ACTIVE);
            //end - assign default values

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sRqrmtIDx";

            //initialize reference objects
            SalesModels salesModel = new SalesModels(poGRider); 
            poRequirementSource = salesModel.RequirementSource();
            
//            end - initialize reference objects

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setRequirementId(String requirementId) {
        return setValue("sRqrmtIDx", requirementId);
    }

    public String getRequirementId() {
        return (String) getValue("sRqrmtIDx");
    }

    public JSONObject setPaymentMode(String paymentMode) {
        return setValue("cPayModex", paymentMode);
    }

    public String getPaymentMode() {
        return (String) getValue("cPayModex");
    }

    public JSONObject setCustomerGroup(String customerGroup) {
        return setValue("cCustGrpx", customerGroup);
    }

    public String getCustomerGroup() {
        return (String) getValue("cCustGrpx");
    }

    public JSONObject setRequirementCode(String requirementCode) {
        return setValue("sRqrmtCde", requirementCode);
    }

    public String getRequirementCode() {
        return (String) getValue("sRqrmtCde");
    }
    
    public JSONObject isRequired(boolean isRequired) {
        return setValue("cRequired", isRequired ? "1" : "0");
    }

    public boolean isRequired() {
        return ((String) getValue("cRequired")).equals("1");
    }
    
    public JSONObject isActive(boolean isRequired) {
        return setValue("cRecdStat", isRequired ? "1" : "0");
    }

    public boolean isActive() {
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
        return MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    //reference object models
    public Model_Requirement_Source RequirementSource() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sRqrmtCde"))) {
            if (poRequirementSource.getEditMode() == EditMode.READY
                    && poRequirementSource.getRequirementCode().equals((String) getValue("sRqrmtCde"))) {
                return poRequirementSource;
            } else {
                poJSON = poRequirementSource.openRecord((String) getValue("sRqrmtCde"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poRequirementSource;
                } else {
                    poRequirementSource.initialize();
                    return poRequirementSource;
                }
            }
        } else {
            poRequirementSource.initialize();
            return poRequirementSource;
        }
    }
    //end - reference object models

}
