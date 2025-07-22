/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.validator;

import org.guanzon.appdriver.iface.GValidator;

/**
 *
 * @author Arsiela 
 */
public class SalesInquiryValidatorFactory {
    public static GValidator make(String industryId){
        switch (industryId) {
            case "01": //Mobile Phone
                return new SalesInquiry_MP();
            case "02": //Motorcycle
                return new SalesInquiry_MC();
            case "03": //Vehicle
                return new SalesInquiry_Vehicle();
            case "04": //Hospitality
//                return new SalesInquiry_Hospitality();
            case "05": //Los Pedritos
//                return new SalesInquiry_LP();
            case "07": //Appliances
                return new SalesInquiry_Appliances();
            default:
                return null;
        }
    }
    
}
