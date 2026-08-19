/*
 * -----------------------------------------------------------------------------
 * Project       : CAS Sales
 * Module        : Customer Inquiry Follow Up
 * Class Name    : CustomerInquiryFollowUpStatic
 *
 * Description   :
 * Contains static constants used by the Customer Inquiry Follow Up module,
 * including follow-up status and inquiry type identifiers.
 *
 * Author        : TEEJEI DE CELIS
 * Date Created  : July 25, 2026
 * -----------------------------------------------------------------------------
 */
package org.guanzon.cas.sales.status;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Static constants for the Customer Inquiry Follow Up module.
 *
 * <p>
 * This class contains predefined constant values used throughout the
 * Customer Inquiry Follow Up transactions to avoid the use of hard-coded
 * values in the application.
 * </p>
 *
 * <h3>Follow-Up Status</h3>
 * <ul>
 *     <li>{@link #FOLLOWED_UP} - Customer has already been followed up.</li>
 *     <li>{@link #PENDING} - Customer is still pending for follow-up.</li>
 * </ul>
 *
 * <h3>Inquiry Types</h3>
 * <ul>
 *     <li>{@link InquiryType#ALL} - Retrieve all inquiries.</li>
 *     <li>{@link InquiryType#NEW_SALES_INQUIRY} - Newly created sales inquiries.</li>
 *     <li>{@link InquiryType#SCHEDULED_TODAY} - Follow-ups scheduled for today.</li>
 *     <li>{@link InquiryType#OVER_DUE_SCHEDULED} - Overdue scheduled follow-ups.</li>
 * </ul>
 *
 * @author TEEJEI DE CELIS
 * @date July 25, 2026
 * @module Customer Inquiry Follow Up
 * @since 1.0
 */
public final class CustomerInquiryFollowUpStatic {

    /**
     * Prevents instantiation of this utility class.
     */
    private CustomerInquiryFollowUpStatic() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    // -------------------------------------------------------------------------
    // Follow-Up Status
    // -------------------------------------------------------------------------

    /**
     * Indicates that the customer inquiry has already been followed up.
     */
    public static final String FOLLOWED_UP = "0";

    /**
     * Indicates that the customer inquiry is still pending for follow-up.
     */
    public static final String PENDING = "1";

    // -------------------------------------------------------------------------
    // Inquiry Types
    // -------------------------------------------------------------------------

    /**
     * Inquiry type identifiers used by the Customer Inquiry Follow Up module.
     */
    public static final class InquiryType {

        /**
         * Prevents instantiation.
         */
        private InquiryType() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated.");
        }

        /**
         * Retrieve all customer inquiries.
         */
        public static final String ALL = "0";

        /**
         * Retrieve newly created sales inquiries.
         */
        public static final String NEW_SALES_INQUIRY = "1";

        /**
         * Retrieve customer inquiries scheduled for follow-up today.
         */
        public static final String SCHEDULED_TODAY = "2";

        /**
         * Retrieve customer inquiries with overdue follow-up schedules.
         */
        public static final String OVER_DUE_SCHEDULED = "3";
    }
    public static final ObservableList<String> INQUIRY_FILTER =
            FXCollections.observableArrayList(
                    "All",
                    "New Sales Inquiry",
                    "Scheduled Today",
                    "Over Due Scheduled"
            );
    public static final String[] INQUIRY_FILTER_CODE = {
            "0",
            "1",
            "2",
            "3"
    };


    /**
     * Inquiry status descriptions.
     * <p>
     * The list index corresponds to the values in
     * {@link #INQUIRY_STATUS_CODE}.
     * </p>
     *
     * <pre>
     * FOLLOWED-UP - Customer inquiry has already been followed up.
     * PENDING     - Customer inquiry is still pending for follow-up.
     * </pre>
     */
    public static final ObservableList<String> INQUIRY_STATUS =
            FXCollections.observableArrayList(
                    "FOLLOWED-UP",
                    "PENDING",
                    ""
            );

    /**
     * Inquiry status codes.
     * <p>
     * The array index corresponds to the values in
     * {@link #INQUIRY_STATUS}.
     * </p>
     *
     * <pre>
     * 0 - FOLLOWED-UP
     * 1 - PENDING
     * </pre>
     */
    public static final String[] INQUIRY_STATUS_CODE = {
            "0",
            "1",
            null
    };
    /**
     * Returns the inquiry status description based on its code.
     *
     * @param code Inquiry status code.
     * @return Inquiry status description.
     */
    public static String getInquiryStatusDescription(String code) {
        if (code == null) {
            return "";
        }

        for (int i = 0; i < INQUIRY_STATUS_CODE.length; i++) {
            if (INQUIRY_STATUS_CODE[i].equalsIgnoreCase(code)) {
                return INQUIRY_STATUS.get(i);
            }
        }

        return code;
    }

    /**
     * Returns the inquiry status code based on its description.
     *
     * @param description Inquiry status description.
     * @return Inquiry status code.
     */
    public static String getInquiryStatusCode(String description) {
        if (description == null) {
            return "";
        }

        for (int i = 0; i < INQUIRY_STATUS.size(); i++) {
            if (INQUIRY_STATUS.get(i).equalsIgnoreCase(description)) {
                return INQUIRY_STATUS_CODE[i];
            }
        }

        return "";
    }

    /**
     * Supported Social Media platforms.
     * <p>
     * Used to populate the Social Media ComboBox in the Customer Inquiry
     * Follow-Up module.
     */
    public static final ObservableList<String> SOCIAL_MEDIA =
            FXCollections.observableArrayList(
                    "Facebook",
                    "Messenger",
                    "Instagram",
                    "TikTok",
                    "Viber",
                    "WhatsApp",
                    ""
            );

    /**
     * Social Media platform codes.
     * <p>
     * The array index corresponds to the {@link #SOCIAL_MEDIA} list.
     *
     * <pre>
     * FBK - Facebook
     * MSG - Messenger
     * INS - Instagram
     * TTK - TikTok
     * VBR - Viber
     * WAP - WhatsApp
     * </pre>
     */
    public static final String[] SOCIAL_MEDIA_CODE = {
            "FBK",
            "MSG",
            "INS",
            "TTK",
            "VBR",
            "WAP",
            null
    };

    /**
     * Supported communication methods.
     * <p>
     * Used to populate the Communication Method ComboBox.
     */
    public static final ObservableList<String> COMMUNICATION_METHOD =
            FXCollections.observableArrayList(
                    "SMS",
                    "CALL",
                    "SOCIAL MEDIA",
                    "EMAIL",
                    "VIBER",
                    ""
            );

    /**
     * Communication method codes.
     * <p>
     * The array index corresponds to the {@link #COMMUNICATION_METHOD} list.
     *
     * <pre>
     * SMS - SMS
     * CAL - Call
     * SOC - Social Media
     * EML - Email
     * VBR - Viber
     * </pre>
     */
    public static final String[] COMM_METHOD_CODE = {
            "SMS",
            "CAL",
            "SOC",
            "EML",
            "VBR",
            null
    };

    /**
     * Supported customer responses during follow-up.
     * <p>
     * Used to populate the Customer Response ComboBox.
     */
    public static final String RESPONSE_POSITIVE = "POS";
    public static final String RESPONSE_NEGATIVE = "NEG";
    public static final String RESPONSE_LOST_SALE = "LOS";
    public static final String RESPONSE_UNDECIDED = "UND";
    public static final String RESPONSE_NO_RESPONSE = "NOR";
    public static final String RESPONSE_HANGED_UP = "HNG";

    public static final ObservableList<String> CUSTOMER_RESPONSE =
            FXCollections.observableArrayList(
                    "Positive Response",
                    "Negative Response",
                    "Negative Response - Lost Sale",
                    "Undecided Response",
                    "No Response",
                    "Hanged Up",
                    ""
            );

    /**
     * Customer response codes.
     * <p>
     * The array index corresponds to the {@link #CUSTOMER_RESPONSE} list.
     *
     * <pre>
     * POS - Positive Response
     * NEG - Negative Response
     * LOS - Negative Response - Lost Sale
     * UND - Undecided Response
     * NOR - No Response
     * HNG - Hanged Up
     * </pre>
     */
    public static final String[] CUSTOMER_RESPONSE_CODE = {
            "POS",
            "NEG",
            "LOS",
            "UND",
            "NOR",
            "HNG",
            null
    };
    /**
     * Returns the customer response description based on the response code.
     *
     * @param code Customer response code.
     * @return Customer response description.
     */
    public static String getCustomerResponseDescription(String code) {
        if (code == null) {
            return "";
        }

        for (int i = 0; i < CUSTOMER_RESPONSE_CODE.length; i++) {
            if (CUSTOMER_RESPONSE_CODE[i].equalsIgnoreCase(code)) {
                return CUSTOMER_RESPONSE.get(i);
            }
        }

        return code;
    }

    /**
     * Returns the customer response code based on the description.
     *
     * @param description Customer response description.
     * @return Customer response code.
     */
    public static String getCustomerResponseCode(String description) {
        if (description == null) {
            return "";
        }

        for (int i = 0; i < CUSTOMER_RESPONSE.size(); i++) {
            if (CUSTOMER_RESPONSE.get(i).equalsIgnoreCase(description)) {
                return CUSTOMER_RESPONSE_CODE[i];
            }
        }

        return "";
    }

    public static final ObservableList<String> COM_VHICLE_COND =
            FXCollections.observableArrayList(
                    "Brand New",
                    "Pre-Owned",
                    ""
            );
    public static final String[] COM_VHICLE_COND_CODE = {
            "0",
            "1",
            null
    };
}
