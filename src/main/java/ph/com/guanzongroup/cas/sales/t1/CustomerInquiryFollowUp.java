/*
* -----------------------------------------------------------------------------
* Project       : CAS Sales
* Module        : Customer Inquiry Follow Up
* Class Name    : CustomerInquiryFollowUp
*
* Description   :
* Handles the business logic for Customer Inquiry Follow Up transactions,
* including record validation, searching, retrieval of sales inquiries,
* salesperson lookup, and management of customer follow-up records.
* This class also provides filtering and retrieval of inquiries based on
* inquiry type, follow-up schedule, customer, salesperson, and transaction
* date.
*
* Author        : TEEJEI DE CELIS
* Date Created  : July 25, 2026
* -----------------------------------------------------------------------------
*/
package ph.com.guanzongroup.cas.sales.t1;

import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.agent.systables.SysTableContollers;
import org.guanzon.appdriver.agent.systables.TransactionAttachment;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.base.WebFile;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Customer_Inquiry_FollowUp;
import ph.com.guanzongroup.cas.sales.t1.model.Model_Sales_Inquiry_Master;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.services.SalesModels;
import ph.com.guanzongroup.cas.sales.t1.status.CustomerInquiryFollowUpStatic;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
* Initializes the Customer Inquiry Follow Up transaction.
*
* <p>
* Sets the default record status to ACTIVE and initializes the
* Customer Inquiry Follow Up model.
* </p>
*
* @author TEEJEI DE CELIS
* @date July 25, 2026
* @module Customer Inquiry Follow Up
* @throws SQLException if a database access error occurs
* @throws GuanzonException if initialization fails
*/

public class CustomerInquiryFollowUp extends Parameter {
    Model_Customer_Inquiry_FollowUp poModel;
    Model_Sales_Inquiry_Master poSalesMaster;
    List<TransactionAttachment> paAttachments;
    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = RecordStatus.ACTIVE;
        poModel = new SalesModels(poGRider).CustomerInquiryFollowUp();
        poSalesMaster = new SalesModels(poGRider).SalesInquiryMaster();
        paAttachments = new ArrayList<>();
        super.initialize();

    }

    private TransactionAttachment TransactionAttachment() throws SQLException, GuanzonException {
        return new SysTableContollers(poGRider, null).TransactionAttachment();
    }

    public TransactionAttachment TransactionAttachmentList(int row) {
        return (TransactionAttachment) paAttachments.get(row);
    }

    public int getTransactionAttachmentCount() {
        if (paAttachments == null) {
            paAttachments = new ArrayList<>();
        }

        return paAttachments.size();
    }
    public JSONObject addAttachment()
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        if (paAttachments.isEmpty()) {
            paAttachments.add(TransactionAttachment());
            poJSON = paAttachments.get(getTransactionAttachmentCount() - 1).newRecord();
        } else {
            if (!paAttachments.get(paAttachments.size() - 1).getModel().getTransactionNo().isEmpty()) {
                paAttachments.add(TransactionAttachment());
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "Unable to add transaction attachment.");
                return poJSON;
            }
        }
        poJSON.put("result", "success");
        return poJSON;
    }
    public JSONObject removeAttachment(int fnRow) throws GuanzonException, SQLException{
        poJSON = new JSONObject();
        if(getTransactionAttachmentCount() <= 0){
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction attachment to be removed.");
            return poJSON;
        }

        if(paAttachments.get(fnRow).getEditMode() == EditMode.ADDNEW){
            paAttachments.remove(fnRow);
            System.out.println("Attachment :"+ fnRow+" Removed");
        } else {
            paAttachments.get(fnRow).getModel().setRecordStatus(RecordStatus.INACTIVE);
            System.out.println("Attachment :"+ fnRow+" Deactivate");
        }

        poJSON.put("result", "success");
        return poJSON;
    }

    public int addAttachment(String fFileName) throws SQLException, GuanzonException{
        for(int lnCtr = 0;lnCtr <= getTransactionAttachmentCount() - 1;lnCtr++){
            if(fFileName.equals(paAttachments.get(lnCtr).getModel().getFileName())
                    && RecordStatus.INACTIVE.equals(paAttachments.get(lnCtr).getModel().getRecordStatus())){
                paAttachments.get(lnCtr).getModel().setRecordStatus(RecordStatus.ACTIVE);
                System.out.println("Attachment :"+ lnCtr+" Activate");
                return lnCtr;
            }
        }

        addAttachment();
        paAttachments.get(getTransactionAttachmentCount() - 1).getModel().setFileName(fFileName);
        paAttachments.get(getTransactionAttachmentCount() - 1).getModel().setSourceNo(getModel().getTransactionNo());
        paAttachments.get(getTransactionAttachmentCount() - 1).getModel().setRecordStatus(RecordStatus.ACTIVE);
        return getTransactionAttachmentCount() - 1;
    }
    public void copyFile(String fsPath){
        Path source = Paths.get(fsPath);
        Path targetDir = Paths.get(System.getProperty("sys.default.path.temp.attachments"));

        try {
            // Ensure target directory exists
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Copy file into the target directory
            Files.copy(source, targetDir.resolve(source.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File copied successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadAttachments()
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();
        paAttachments = new ArrayList<>();

        TransactionAttachment loAttachment = new SysTableContollers(poGRider, null).TransactionAttachment();
        List loList = loAttachment.getAttachments(SOURCE_CODE, getModel().getTransactionNo());
        for (int lnCtr = 0; lnCtr <= loList.size() - 1; lnCtr++) {
            paAttachments.add(TransactionAttachment());
            poJSON = paAttachments.get(getTransactionAttachmentCount() - 1).openRecord((String) loList.get(lnCtr));
            if ("success".equals((String) poJSON.get("result"))) {
                if(getModel().getEditMode() == EditMode.UPDATE){
                    poJSON = paAttachments.get(getTransactionAttachmentCount() - 1).updateRecord();
                }
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getTransactionNo());
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getSourceNo());
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getSourceCode());
                System.out.println(paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getFileName());
            }

            //Download Attachments
            poJSON = WebFile.DownloadFile(WebFile.getAccessToken(System.getProperty("sys.default.access.token"))
                    , "0032" //Constant
                    , "" //Empty
                    , paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getFileName()
                    , SOURCE_CODE
                    , paAttachments.get(getTransactionAttachmentCount() - 1).getModel().getSourceNo()
                    , "");
            if ("success".equals((String) poJSON.get("result"))) {

                poJSON = (JSONObject) poJSON.get("payload");
                if(WebFile.Base64ToFile((String) poJSON.get("data")
                        , (String) poJSON.get("hash")
                        , System.getProperty("sys.default.path.temp.attachments") + "/"
                        , (String) poJSON.get("filename"))){
                    System.out.println("poJSON success: " +  poJSON.toJSONString());
                    System.out.println("File downloaded succesfully.");
                } else {
                    poJSON = (JSONObject) poJSON.get("error");
                    poJSON.put("result", "error");
                    System.out.println("ERROR WebFile.DownloadFile: " + poJSON.get("message"));
                    System.out.println("poJSON error WebFile.DownloadFile: " + poJSON.toJSONString());
                }

            } else {
                System.out.println("poJSON error WebFile.DownloadFile: " + poJSON.toJSONString());
            }
        }
        return poJSON;
    }
    /**
     * Returns the active Customer Inquiry Follow Up model.
     *
     * <p>
     * Provides access to the transaction model currently being managed
     * by this controller.
     * </p>
     *
     * @return the {@link Model_Customer_Inquiry_FollowUp} instance.
     *
     * @author TEEJEI DE CELIS
     * @module Customer Inquiry Follow Up
     * @date July 25, 2026
     */

    @Override
    public Model_Customer_Inquiry_FollowUp getModel() {
        return poModel;
    }

    /**
     * Validates the current Customer Inquiry Follow Up transaction.
     *
     * <p>
     * Performs all required business rule validations before allowing
     * the transaction to be saved. Validation includes:
     * <ul>
     *     <li>Transaction information</li>
     *     <li>Reference information</li>
     *     <li>Follow-up details</li>
     *     <li>Communication method</li>
     *     <li>Customer response</li>
     *     <li>Competitor information (when applicable)</li>
     *     <li>Audit information</li>
     * </ul>
     *
     * Upon successful validation, the system automatically assigns the
     * current user as the entry user and records the server date.
     * </p>
     *
     * @return a {@link JSONObject} containing the validation result and message.
     *
     * @throws SQLException if a database access error occurs.
     *
     * @author TEEJEI DE CELIS
     * @module Customer Inquiry Follow Up
     * @date July 25, 2026
     */
    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();
        if (poModel.getTransactionNo() == null || poModel.getTransactionNo().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction No. must not be empty.");
            return poJSON;
        }

        if (poModel.getSourceCode() == null || poModel.getSourceCode().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source Code must not be empty.");
            return poJSON;
        }

        if (poModel.getSourceNo() == null || poModel.getSourceNo().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source No. must not be empty.");
            return poJSON;
        }

        if (poModel.getRemarks() == null || poModel.getRemarks().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Remarks must not be empty.");
            return poJSON;
        }

        if (poModel.getMessage() == null || poModel.getMessage().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Message must not be empty.");
            return poJSON;
        }

        if (poModel.getMethodCode() == null || poModel.getMethodCode().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Method Code must not be empty.");
            return poJSON;
        }

        /*
         * Required only when Method Code is SOCIAL MEDIA.
         */
        if ("SOC".equalsIgnoreCase(poModel.getMethodCode())
                && (poModel.getSocialMediaCode() == null || poModel.getSocialMediaCode().trim().isEmpty())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Social Media must not be empty.");
            return poJSON;
        }

        if (poModel.getFollowUpDate() == null) {
            poJSON.put("result", "error");
            poJSON.put("message", "Follow-up Date must not be empty.");
            return poJSON;
        }

        if (poModel.getFollowUpTime() == null) {
            poJSON.put("result", "error");
            poJSON.put("message", "Follow-up Time must not be empty.");
            return poJSON;
        }



        /*
         * Competitor information is required only if the response is Lost Sale.
         * Replace "LOST" with your actual response code.
         */
//        if ("LOST".equalsIgnoreCase(poModel.getResponseCode())) {
//
//            if (poModel.getCompetitorGoods() == null || poModel.getCompetitorGoods().trim().isEmpty()) {
//                poJSON.put("result", "error");
//                poJSON.put("message", "Competitor Goods must not be empty.");
//                return poJSON;
//            }
//
//            if (poModel.getCompetitorMake() == null || poModel.getCompetitorMake().trim().isEmpty()) {
//                poJSON.put("result", "error");
//                poJSON.put("message", "Competitor Make must not be empty.");
//                return poJSON;
//            }
//
//            if (poModel.getCompetitorDealer() == null || poModel.getCompetitorDealer().trim().isEmpty()) {
//                poJSON.put("result", "error");
//                poJSON.put("message", "Competitor Dealer must not be empty.");
//                return poJSON;
//            }
//        }

        if (poModel.getResponseCode() == null || poModel.getResponseCode().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Response Code must not be empty.");
            return poJSON;
        }

        if (poModel.getClientId() == null || poModel.getClientId().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Client ID must not be empty.");
            return poJSON;
        }

        if (poModel.getEntryBy() == null || poModel.getEntryBy().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Entry By must not be empty.");
            return poJSON;
        }



        if (poModel.getRecordStatus() == null || poModel.getRecordStatus().trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record Status must not be empty.");
            return poJSON;
        }


        poModel.setEntryBy(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setEntryDate(poGRider.getServerDate());
        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public JSONObject willSave() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        try {

                poJSON = setValueToOthers();
                if (!"success".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        poJSON.put("result", "success");
        return this.poJSON;
    }

    private JSONObject setValueToOthers()
            throws CloneNotSupportedException, SQLException, GuanzonException {

        poJSON = new JSONObject();
            UpdateSource(getModel().getSourceNo(),getModel().getResponseCode());
        poJSON.put("result", "success");
        return poJSON;
    }

    private void UpdateSource(String fsSourceNo,String fsResponseCode)
            throws GuanzonException, SQLException, CloneNotSupportedException {

        poSalesMaster.openRecord(fsSourceNo);
        poSalesMaster.updateRecord();
        poSalesMaster.setFollowUpDate(poGRider.getServerDate());
        if(fsResponseCode.equals(CustomerInquiryFollowUpStatic.RESPONSE_LOST_SALE)){
            poSalesMaster.setTransactionStatus(SalesInquiryStatic.LOST);
        }
        poSalesMaster.setTransactionStatus(SalesInquiryStatic.LOST);
        poSalesMaster.setModifyingId(poGRider.getUserID());
        poSalesMaster.setModifiedDate(poGRider.getServerDate());
    }

    private JSONObject saveUpdates()
            throws CloneNotSupportedException, SQLException, GuanzonException {
        poJSON = new JSONObject();
        poSalesMaster.saveRecord();
        poJSON.put("result", "success");
        return poJSON;
    }
    @Override
    protected JSONObject saveOthers() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        try {
            poJSON = saveUpdates();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Searches for a Customer Inquiry Follow Up record.
     *
     * <p>
     * Displays a browse dialog allowing the user to search records either
     * by employee code or employee name depending on the value of
     * {@code byCode}. Once selected, the corresponding record is loaded
     * into the current transaction model.
     * </p>
     *
     * @param value the search value.
     * @param byCode {@code true} to search by employee code;
     *               {@code false} to search by employee name.
     *
     * @return a {@link JSONObject} containing the search result.
     *
     * @throws SQLException if a database access error occurs.
     * @throws GuanzonException if the browse operation fails.
     *
     * @author TEEJEI DE CELIS
     * @module Customer Inquiry Follow Up
     * @date July 25, 2026
     */

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsCondition = "";
        if (psRecdStat != null) {
            if (psRecdStat.length() > 1) {
                for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                    lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
                }
                lsCondition = " a.cRecdStat IN (" + lsCondition.substring(2) + ")";
            } else {
                lsCondition = " a.cRecdStat = " + SQLUtil.toSQL(psRecdStat);
            }
        }

        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);

        System.out.println("Executing SQL: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Employee ID»Salesman",
                "sEmployID»sFullName",
                "a.sEmployID»concat(a.sLastName,', ',a.sFrstName, ' ',a.sMiddName)",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sEmployID"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    /**
     * Searches for a salesperson and assigns the selected employee
     * to the current follow-up transaction.
     *
     * <p>
     * Only active salespersons are included in the search.
     * When a salesperson is successfully selected, the employee ID
     * is stored as the Client ID of the current transaction.
     * </p>
     *
     * @param value the search value.
     * @param byCode {@code true} to search using employee ID;
     *               {@code false} to search using employee name.
     *
     * @return a {@link JSONObject} containing the search result.
     *
     * @throws SQLException if a database access error occurs.
     * @throws GuanzonException if the search operation fails.
     *
     * @author TEEJEI DE CELIS
     * @module Customer Inquiry Follow Up
     * @date July 25, 2026
     */

    public JSONObject SearchSalesPerson(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Salesman object = new SalesControllers(poGRider, logwrapr).Salesman();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            poModel.setClientId(object.getModel().getEmployeeId());
        }

        return poJSON;
    }
    /**
     * Retrieves salesperson information to be used as a search filter.
     *
     * <p>
     * Searches only active salespersons and returns the selected
     * employee's name and employee ID without modifying the current
     * transaction.
     * </p>
     *
     * @param value the search value.
     * @param byCode {@code true} to search using employee ID;
     *               {@code false} to search using employee name.
     *
     * @return a {@link JSONObject} containing the salesperson information.
     *
     * @throws SQLException if a database access error occurs.
     * @throws GuanzonException if the search operation fails.
     *
     * @author TEEJEI DE CELIS
     * @module Customer Inquiry Follow Up
     * @date July 25, 2026
     */
    public JSONObject FilterBySalesPerson(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Salesman object = new SalesControllers(poGRider, logwrapr).Salesman();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;

        }

        poJSON.put("salesman",object.getModel().Client().getCompanyName());
        poJSON.put("salesmanID",object.getModel().getEmployeeId());
        poJSON.put("result", "success");
        return poJSON;
    }

    public JSONObject FilterByCustomerName(String value, boolean byCode)
            throws SQLException,
            GuanzonException {
        poJSON = new JSONObject();

        Salesman object = new SalesControllers(poGRider, logwrapr).Salesman();
        object.setRecordStatus(RecordStatus.ACTIVE);
        poJSON = object.searchRecord(value, byCode);
        if ("error".equals((String) poJSON.get("result"))) {
            return poJSON;

        }

        poJSON.put("customerNme",object.getModel().Client().getCompanyName());
        poJSON.put("clientID",object.getModel().getEmployeeId());
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Retrieves Sales Inquiry records based on the selected inquiry type.
     *
     * <p>
     * This method dynamically builds the SQL query according to the
     * specified inquiry type and applies optional filters such as
     * salesperson, customer, and date range.
     * </p>
     *
     * <p>
     * Supported inquiry types:
     * <ul>
     *     <li><b>0</b> - All Sales Inquiries</li>
     *     <li><b>1</b> - New Sales Inquiries without follow-up</li>
     *     <li><b>2</b> - Follow-ups scheduled today</li>
     *     <li><b>3</b> - Overdue follow-ups</li>
     * </ul>
     *
     * Depending on the inquiry type, the method returns either the
     * transaction date or follow-up date for each inquiry.
     * </p>
     *
     * @param InquiryType identifies which inquiry records to retrieve.
     * @param Salesperson optional salesperson employee ID filter.
     * @param CustomerName optional customer ID filter.
     * @param dateFrom optional starting date of the search range.
     * @param dateThru optional ending date of the search range.
     *
     * @return a {@link JSONObject} containing the query result and payload.
     *
     * @throws SQLException if a database access error occurs.
     * @throws GuanzonException if an application error occurs while retrieving
     *                          inquiry records.
     *
     * @author TEEJEI DE CELIS
     * @module Customer Inquiry Follow Up
     * @date July 25, 2026
     */
    public JSONObject RetreiveSource(String InquiryType,
                                     String Salesperson,
                                     String CustomerName,
                                     LocalDate dateFrom,
                                     LocalDate dateThru) throws SQLException, GuanzonException {

        JSONObject loJSON = new JSONObject();
        JSONArray loArray = new JSONArray();

        LocalDate today = LocalDate.now();

        String lsSQL = " SELECT "
                + " a.sTransNox, "
                + " a.sClientID, "
                + " c.sCompnyNm AS CustomerName, "
                + " a.sSalesman, "
                + " d.sCompnyNm AS SalesPerson, "
                + " a.dTransact, "
                + " a.cTranStat "
                + " FROM Sales_Inquiry_Master a ";

        List<String> loCondition = new ArrayList<>();

        switch (InquiryType) {

            case CustomerInquiryFollowUpStatic.InquiryType.NEW_SALES_INQUIRY: // Unfollowed-up New Sales Inquiries

                lsSQL += " LEFT JOIN ( "
                        + "     SELECT DISTINCT sSourceNo "
                        + "     FROM Customer_Inquiry_FollowUp "
                        + " ) f ON f.sSourceNo = a.sTransNox ";

                loCondition.add("a.dTransact < " + SQLUtil.toSQL(today));
                loCondition.add("f.sSourceNo IS NULL");
                break;

            case CustomerInquiryFollowUpStatic.InquiryType.SCHEDULED_TODAY: // Scheduled Follow-ups Today

                lsSQL += " INNER JOIN ( "
                        + "     SELECT DISTINCT sSourceNo "
                        + "     FROM Customer_Inquiry_FollowUp "
                        + "     WHERE dFollowUp = " + SQLUtil.toSQL(today)
                        + " ) f ON f.sSourceNo = a.sTransNox ";
                break;

            case CustomerInquiryFollowUpStatic.InquiryType.OVER_DUE_SCHEDULED: // Overdue Follow-ups

                lsSQL += " INNER JOIN ( "
                        + "     SELECT DISTINCT sSourceNo "
                        + "     FROM Customer_Inquiry_FollowUp "
                        + "     WHERE dFollowUp < " + SQLUtil.toSQL(today)
                        + " ) f ON f.sSourceNo = a.sTransNox ";
                break;

            default:
                break;
        }

        lsSQL += " LEFT JOIN Salesman b "
                + "     ON a.sSalesman = b.sEmployID "
                + " LEFT JOIN Client_Master c "
                + "     ON a.sClientID = c.sClientID "
                + " LEFT JOIN Client_Master d "
                + "     ON b.sEmployID = d.sClientID ";

        // Active inquiries only
        loCondition.add("a.cTranStat NOT IN ("
                + SQLUtil.toSQL(SalesInquiryStatic.SALE) + ","
                + SQLUtil.toSQL(SalesInquiryStatic.LOST) + ","
                + SQLUtil.toSQL(SalesInquiryStatic.CANCELLED) + ","
                + SQLUtil.toSQL(SalesInquiryStatic.VOID)
                + ")");

        if (Salesperson != null && !Salesperson.trim().isEmpty()) {
            loCondition.add("a.sSalesman = " + SQLUtil.toSQL(Salesperson));
        }

        if (CustomerName != null && !CustomerName.trim().isEmpty()) {
            loCondition.add("a.sClientID = " + SQLUtil.toSQL(CustomerName));
        }

        String lsDateField;

        if (CustomerInquiryFollowUpStatic.InquiryType.SCHEDULED_TODAY.equals(InquiryType)
                || CustomerInquiryFollowUpStatic.InquiryType.OVER_DUE_SCHEDULED.equals(InquiryType)) {
            lsDateField = "f.dFollowUp";
        } else {
            lsDateField = "a.dTransact";
        }

        if (dateFrom != null) {
            loCondition.add(lsDateField + " >= " + SQLUtil.toSQL(dateFrom));
        }

        if (dateThru != null) {
            loCondition.add(lsDateField + " <= " + SQLUtil.toSQL(dateThru));
        }

        lsSQL = MiscUtil.addCondition(lsSQL, String.join(" AND ", loCondition))
                + " ORDER BY a.dTransact ASC";

        System.out.println("Executing SQL:");
        System.out.println(lsSQL);

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {

            while (loRS.next()) {

                JSONObject loData = new JSONObject();

                loData.put("sTransNox", loRS.getString("sTransNox"));
                loData.put("sClientID", loRS.getString("sClientID"));
                loData.put("CustomerName", loRS.getString("CustomerName"));
                loData.put("sSalesman", loRS.getString("sSalesman"));
                loData.put("SalesPerson", loRS.getString("SalesPerson"));
                switch (InquiryType) {
                    case CustomerInquiryFollowUpStatic.InquiryType.SCHEDULED_TODAY:
                    case CustomerInquiryFollowUpStatic.InquiryType.OVER_DUE_SCHEDULED:
                        loData.put("dFollowUp", loRS.getString("dFollowUp"));
                        break;
                    default:
                        loData.put("dTransact", loRS.getString("dTransact"));
                        break;
                }

                String TranStat = loRS.getString( "cTranStat");
                switch (TranStat) {
                    case SalesInquiryStatic.OPEN:
                        loData.put("cTranStat", "OPEN");
                        break;
                    case SalesInquiryStatic.CONFIRMED:
                        loData.put("cTranStat", "CONFIRMED");
                        break;
                    case SalesInquiryStatic.QUOTED:
                        loData.put("cTranStat", "QUOTED");
                        break;
                }
                loArray.add(loData);
            }

        } finally {
            MiscUtil.close(loRS);
        }

        if (!loArray.isEmpty()) {
            loJSON.put("result", "success");
            loJSON.put("message", "Record loaded successfully.");
            loJSON.put("payload", loArray);
        } else {
            loJSON.put("result", "error");
            loJSON.put("continue", true);
            loJSON.put("message", "No record found.");
            loJSON.put("payload", new JSONArray());
        }

        return loJSON;
    }

    public JSONObject RetreiveCustomerInquiryFollowUps(String SourceNo,
                                                       String sourceCode) throws SQLException, GuanzonException {

        JSONObject loJSON = new JSONObject();
        JSONArray loArray = new JSONArray();

        String lsSQL = " SELECT "
                + " a.sTransNox, "
                + " b.sClientID AS Customer_Client_ID, "
                + " d.sCompnyNm AS Customer_Name, "
                + " a.sClientID AS Sales_Client_ID, "
                + " e.sCompnyNm AS Sales_Name, "
                + " a.sRspnseCd, "
                + " a.dFollowUp, "
                + " a.cRecdStat, "
                + " a.sSourceCd, "
                + " a.sSourceNo"
                + " FROM Customer_Inquiry_FollowUp a "
                + " LEFT JOIN Sales_Inquiry_Master b ON a.sSourceNo = b.sTransNox "
                + " LEFT JOIN Salesman c ON a.sClientID = c.sEmployID "
                + " LEFT JOIN Client_Master d ON b.sClientID = d.sClientID "
                + " LEFT JOIN Client_Master e ON a.sClientID = e.sClientID ";

        List<String> loCondition = new ArrayList<>();

        if (SourceNo != null && !SourceNo.trim().isEmpty()) {
            loCondition.add("a.sSourceNo = " + SQLUtil.toSQL(SourceNo.trim()));
        }

        if (sourceCode != null && !sourceCode.trim().isEmpty()) {
            loCondition.add("a.sSourceCd = " + SQLUtil.toSQL(sourceCode.trim()));
        }

        lsSQL = MiscUtil.addCondition(lsSQL, String.join(" AND ", loCondition))
                + " ORDER BY a.dFollowUp ASC";

        System.out.println("Executing SQL:");
        System.out.println(lsSQL);

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {

            while (loRS.next()) {

                JSONObject loData = new JSONObject();

                loData.put("sTransNox", loRS.getString("sTransNox"));
                loData.put("sClientID", loRS.getString("Customer_Client_ID"));
                loData.put("CustomerName", loRS.getString("Customer_Name"));
                loData.put("sSalesman", loRS.getString("Sales_Client_ID"));
                loData.put("SalesPerson", loRS.getString("Sales_Name"));
                loData.put("dFollowUp", loRS.getString("dFollowUp"));

                loData.put("sRspnseCd",
                        CustomerInquiryFollowUpStatic.getCustomerResponseDescription(
                                loRS.getString("sRspnseCd")));

                loData.put("cTranStat",
                        CustomerInquiryFollowUpStatic.getInquiryStatusDescription(
                                loRS.getString("cRecdStat")));
                loArray.add(loData);
            }

        } finally {
            MiscUtil.close(loRS);
        }

        if (!loArray.isEmpty()) {
            loJSON.put("result", "success");
            loJSON.put("message", "Record loaded successfully.");
            loJSON.put("payload", loArray);
        } else {
            loJSON.put("result", "error");
            loJSON.put("continue", true);
            loJSON.put("message", "No record found.");
            loJSON.put("payload", new JSONArray());
        }

        return loJSON;
    }

    public JSONObject OpenSalesInquiry(String fsTransNox) throws CloneNotSupportedException, SQLException, GuanzonException {
        poJSON = new JSONObject();

        SalesInquiry poSalesInquiry;
        poSalesInquiry = new SalesControllers(poGRider, logwrapr).SalesInquiry();
        poJSON = poSalesInquiry.InitTransaction();
        poJSON = poSalesInquiry.OpenTransaction(fsTransNox);
        if ("error".equals(poJSON.get("result"))) {
            poJSON.put("result", "error");
            poJSON.put("message", (String) poJSON.get("message"));
            return poJSON;
        }
        poJSON = poModel.setSourceCode(poSalesInquiry.sourceCode);
        poJSON = poModel.setSourceNo(poSalesInquiry.Master().getTransactionNo());
        poJSON = poModel.setClientId(poSalesInquiry.Master().getSalesMan());
        poJSON.put("result", "success");
        // Return success
        return poJSON;
    }

    public JSONObject OpenClient(String fsClientID) throws CloneNotSupportedException, SQLException, GuanzonException {

        JSONObject poJSON = new JSONObject();
        if(fsClientID == null || fsClientID.trim().isEmpty()){
            return poJSON;
        }
        String lsSQL = "SELECT "
                + " a.sClientID, "
                + " a.sCompnyNm, "
                + " CONCAT("
                + "     b.sAddressx, ', ', "
                + "     e.sBrgyName, ', ', "
                + "     f.sTownName, ', ', "
                + "     g.sDescript, ', ', "
                + "     f.sZippCode"
                + " ) AS address, "
                + " c.sMobileNo, "
                + " c.cPrimaryx, "
                + " d.cSocialTp, "
                + " d.sAccountx "
                + "FROM Client_Master a "
                + "LEFT JOIN Client_Address b ON a.sClientID = b.sClientID "
                + "LEFT JOIN Client_Mobile c ON a.sClientID = c.sClientID "
                + "LEFT JOIN Client_Social_Media d ON a.sClientID = d.sClientID "
                + "LEFT JOIN Barangay e ON b.sBrgyIDxx = e.sBrgyIDxx "
                + "LEFT JOIN TownCity f ON b.sTownIDxx = f.sTownIDxx "
                + "LEFT JOIN Province g ON f.sProvIDxx = g.sProvIDxx ";

        List<String> loCondition = new ArrayList<>();

        if (fsClientID != null && !fsClientID.trim().isEmpty()) {
            loCondition.add("a.sClientID = " + SQLUtil.toSQL(fsClientID.trim()));
        }

        loCondition.add("c.cPrimaryx = '1'");

        lsSQL = MiscUtil.addCondition(lsSQL, String.join(" AND ", loCondition))
                + " ORDER BY a.sClientID ASC";

        System.out.println("Executing SQL:");
        System.out.println(lsSQL);

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {

                poJSON.put("address", loRS.getString("address"));
                poJSON.put("contact", loRS.getString("sMobileNo"));
                poJSON.put("SocMed", loRS.getString("cSocialTp"));
                poJSON.put("SocMedAcct", loRS.getString("sAccountx"));
                poJSON.put("result", "success");

            } else {

                poJSON.put("address", "");
                poJSON.put("contact", "");
                poJSON.put("SocMed", "");
                poJSON.put("SocMedAcct", "");
                poJSON.put("result", "error");
                poJSON.put("message", "No record found.");
            }
        } finally {
            MiscUtil.close(loRS);
        }

        return poJSON;
    }

}
