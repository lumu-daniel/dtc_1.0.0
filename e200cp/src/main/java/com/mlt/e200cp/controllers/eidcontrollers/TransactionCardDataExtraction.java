package com.mlt.e200cp.controllers.eidcontrollers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionCardDataExtraction {
    
    public static String getExpiryDate(String cardData){
        int expiryLength = Integer.parseInt(cardData.substring(cardData.indexOf("5F24") + 5, cardData.indexOf("5F24") + 6));
        //int expiryOffset = cardData.indexOf("5F24")+6
        String expiryDate = cardData.substring(cardData.indexOf("5F24") + 6, (cardData.indexOf("5F24") + 6) + (expiryLength * 2));
        try {
            Date prevED = ( expiryLength == 3 ) ? new SimpleDateFormat("yyMMdd").parse(expiryDate) : new SimpleDateFormat("yyyyMMdd").parse(expiryDate);
            expiryDate  = new SimpleDateFormat("dd-MM-yyyy").format(prevED);
        } catch (ParseException e) {
            Log.e("CARDDE", "onSuccess: "+e.getLocalizedMessage() );
        }
        return expiryDate;
    }
    
}
