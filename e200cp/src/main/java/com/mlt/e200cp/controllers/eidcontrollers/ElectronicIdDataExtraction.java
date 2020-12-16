package com.mlt.e200cp.controllers.eidcontrollers;

import android.util.Base64;
import android.util.Log;

import com.mlt.e200cp.utilities.helper.util.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class ElectronicIdDataExtraction{

    static HashMap<String, String> information;
    public static ElectronicIdDataExtraction newInstance(){
        information = new HashMap<>();

        return new ElectronicIdDataExtraction();
    }

    /*
     * Converting hexadecimal into image*/
    public String convertHexStringToBase64(StringBuffer passData){
        byte[] convertedBytes = Utility.hex2Byte(passData.toString());
        String convertedString = Base64.encodeToString( convertedBytes, Base64.DEFAULT );
        return convertedString;
    }

    /*
     * Changing the date format of the read date*/
    public static String changeDate(String date){
        String dateOfBirth = null;
        try {
            Date prevDOB = new SimpleDateFormat("yyyyMMdd").parse(date);
            dateOfBirth  = new SimpleDateFormat("dd-MM-yyyy").format(prevDOB);
        } catch (ParseException e) {
            e.printStackTrace();
            appendLog(e.getLocalizedMessage());
        }
        return dateOfBirth;
    }

    /*
     * Extracting data from file basing on tags*/
    public HashMap<String, String> decodeElectronicId(String hexadecimalData){
        if( hexadecimalData.contains("E10100") ){
            readDataIntoHashmap(hexadecimalData, "E10100", "CardNumber");
        }
        if( hexadecimalData.contains("E10200") ){
            readDataIntoHashmap(hexadecimalData, "E10200", "IdentityNumber");
        }
        if( hexadecimalData.contains("430F00") ){
            readDateIntoHashmap(hexadecimalData, "430F00", "DateOfBirth");
        }
        if( hexadecimalData.contains("430600") ){
            readDateIntoHashmap(hexadecimalData, "430600", "CardIssuanceDate");
        }
        if( hexadecimalData.contains("430700") ){
            readDateIntoHashmap(hexadecimalData, "430700", "CardExpiryDate");
        }
        if( hexadecimalData.contains("A30900") ){
            readDataIntoHashmap(hexadecimalData, "A30900", "ArabicFullName");
        }
        if( hexadecimalData.contains("E30B00") ){
            readDataIntoHashmap(hexadecimalData, "E30B00", "EnglishFullName");
        }
        if( hexadecimalData.contains("E30C00") ){
            readDataIntoHashmap(hexadecimalData, "E30C00", "Sex");
        }
        if( hexadecimalData.contains("A30D00") ){
            readDataIntoHashmap(hexadecimalData, "A30D00", "ArabicNationality");
        }
        if( hexadecimalData.contains("E33600") ){
            readDataIntoHashmap(hexadecimalData, "E33600", "EnglishNationality");
        }
        if( hexadecimalData.contains("E30E00") ){
            readDataIntoHashmap(hexadecimalData, "E30E00", "Nation");
        }
        if( hexadecimalData.contains("A33700") ){
            readDataIntoHashmap(hexadecimalData, "A33700", "City");
        }
        if( hexadecimalData.contains("E33800") ){
            readDataIntoHashmap(hexadecimalData, "E33800", "ResidenceType");
        }
        if( hexadecimalData.contains("E52200") ){
            readDataIntoHashmap(hexadecimalData, "E52200", "EnglishOccupation");
        }
        if( hexadecimalData.contains("A52100") ){
            readDataIntoHashmap(hexadecimalData, "A52100", "ArabicOccupation");
        }
        if( hexadecimalData.contains("A52400") ){
            readDataIntoHashmap(hexadecimalData, "A52400", "CompanyArabicTitle");
        }
        if( hexadecimalData.contains("E52500") ){
            readDataIntoHashmap(hexadecimalData, "E52500", "CompanyEnglishTitle");
        }
        if( hexadecimalData.contains("E51C00") ){
            readDataIntoHashmap(hexadecimalData, "E51C00", "ResidenceNumber");
        }
        if( hexadecimalData.contains("E52600") ){
            readDataIntoHashmap(hexadecimalData, "E52600", "PassportNumber");
        }
        if( hexadecimalData.contains("451D00") ){
            readDateIntoHashmap(hexadecimalData, "451D00", "ResidenceExpiryDate");
        }
        if( hexadecimalData.contains("E52800") ){
            readDataIntoHashmap(hexadecimalData, "E52800", "CountryOfOrigin");
        }
        if( hexadecimalData.contains("A53B00") ){
            readDataIntoHashmap(hexadecimalData, "A53B00", "NationalityArabicFullTitle");
        }
        if( hexadecimalData.contains("E53C00") ){
            readDataIntoHashmap(hexadecimalData, "E53C00", "NationalityEnglishFullTitle");
        }
        if( hexadecimalData.contains("452900") ){
            readDateIntoHashmap(hexadecimalData, "452900", "PassportIssuanceDate");
        }
        if( hexadecimalData.contains("452A00") ){
            readDateIntoHashmap(hexadecimalData, "452A00", "PassportExpiryDate");
        }
//        Log.d("TTTT", EmvTransactionPresenter.hex2string(hexadecimalData));
        return  information;
    }

    private void readDataIntoHashmap(String hexadecimalData, String tag, String tagTitle){
        int startingIndex = hexadecimalData.indexOf(tag);
        int length = Integer.parseInt(hexadecimalData.substring(startingIndex+6, startingIndex+8), 16) * 2;
        String readData = ( length>0 ) ? hexadecimalData.substring(startingIndex+8, startingIndex+8+length) : "";
        Log.d("TTT", readData+"");
        readData = ( readData.length() > 0 ) ? Utility.hex2string(readData) : "N/A";
        Log.e("TTT", readData+"");
        information.put(tagTitle, readData);
    }

    private void readDateIntoHashmap(String hexadecimalData, String tag, String tagTitle){
        int startingIndex = hexadecimalData.indexOf(tag);
        int length = Integer.parseInt(hexadecimalData.substring(startingIndex+6, startingIndex+8), 16) * 2;
        String issuanceDate = hexadecimalData.substring(startingIndex+8, startingIndex+8+length);
        issuanceDate = changeDate(issuanceDate);
        information.put(tagTitle, issuanceDate);
    }
}
