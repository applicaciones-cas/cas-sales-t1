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
    
    //Category
    public static final String MOBILEPHONE = "0001";   //Cellphone    
    public static final String APPLIANCES  = "0002";   //Appliances   
    public static final String MOTORCYCLE  = "0003";   //Motorcycle   
    public static final String SPMC        = "0004";   //Motorcycle SP
    public static final String CAR         = "0005";   //CAR          
    public static final String SPCAR       = "0006";   //CAR SP       
    public static final String GENERAL     = "0007";   //General      
    public static final String FOOD        = "0008";   //Food         
    public static final String HOSPITALITY = "0009";   //Hospitality 
    
    //Client Type
    public static final  String INDIVIDUAL = "0"; //Individual
    public static final  String CORPORATE = "1";  //Corporate
}
