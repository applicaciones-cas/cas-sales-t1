/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.guanzongroup.cas.sales.t1.validator;

import org.guanzon.appdriver.iface.GValidator;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela 
 */
public class SalesInquiryValidatorFactory {
    public static GValidator make(String industryId){
        switch (industryId) {
            case SalesInquiryStatic.Industry.MOBILEPHONE: //Mobile Phone
                return new SalesInquiry_MP();
            case SalesInquiryStatic.Industry.MOTORCYCLE: //Motorcycle
                return new SalesInquiry_MC();
            case SalesInquiryStatic.Industry.CAR: //Vehicle
                return new SalesInquiry_Vehicle();
            case SalesInquiryStatic.Industry.HOSPITALITY: //Monarch Food
                return new SalesInquiry_Monarch();
            case SalesInquiryStatic.Industry.LPFOOD: //Los Pedritos
                return new SalesInquiry_LP();
            case SalesInquiryStatic.Industry.APPLIANCES: //Appliances
                return new SalesInquiry_Appliances();
            default:
                return null;
        }
    }
    
}
