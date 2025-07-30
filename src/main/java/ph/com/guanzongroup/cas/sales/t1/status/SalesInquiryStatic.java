/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.status;

/**
 *
 * @author Arsiela
 */
public class SalesInquiryStatic {
    public static final String OPEN = "0"; //Open/default 
    public static final  String CONFIRMED = "1"; //contacted
    public static final  String QUOTED = "2"; //quoted
    public static final  String CANCELLED = "3"; //Converted to Sale
    public static final  String VOID = "5"; //Lost/Declined
    public static final  String SALE = "4"; //Converted TO Sale
    public static final  String LOST = "6"; //Lost/Declined
    
    public static final  String CATEGORY_APPLIANCES = "0002"; 
    public static final  String CATEGORY_MC = "0003";
    public static final  String CATEGORY_CAR = "0005";
}
