package com.mlt.e200cp.controllers.eidcontrollers;

import android.util.Log;

import com.google.gson.JsonObject;
import com.mlt.e200cp.controllers.mainlogiccontrollers.EmvTransactionPresenter;
import com.mlt.e200cp.interfaces.ChipCardCallBack;
import com.mlt.e200cp.utilities.helper.protocol.VNG;
import com.mlt.e200cp.utilities.helper.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mlt.e200cp.utilities.helper.util.Utility.CHIPTYPE;
import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class ReadChipCards {

    private static VNG vng_response3;
    private static String response3;
    private static String j3;
    private static VNG vng_response6;
    private static String response6;
    private static VNG vng_response7;
    private static String response7;
    private static String res2;
    private static String recordno;
    private static String result;
    private static int num;
    private static String[] applicationIds = { "A0000000031010", "A0000000041010", "A00000002501", "A0000000032010", "A0000000032020" };
    private static String[] eID = { "3B6A00008065A20131013D72D641", "3B7A9500008065A20131013D72D641" };

    private static ChipCardCallBack callback;

    /*
     * Invoke this method if e chip card has been inserted*/
    public void onChipCardInserted(String responseAPDU, ChipCardCallBack callback){
        this.callback = callback;
        //Electronic Ids have two application id options which are in the string array above
        switch (CHIPTYPE){
            case "EID":
                if (responseAPDU.equals(eID[0]) || responseAPDU.equals(eID[1])) {
                    onElectronicIdInserted(); //If application id shows that it is an electronic Id.
                }/*else if(responseAPDU != null){
                    onTransactionCardInserted();// Check if transaction id was inserted.
                }*/ else {
                    Log.d("TTT","card is not Supported here");
                    callback.onResponseFailure("Please enter valid Emirates ID");
                }
                break;
            case "Transaction":
                if (responseAPDU.equals(eID[0]) || responseAPDU.equals(eID[1])) {
                    /*onElectronicIdInserted();*/ //If application id shows that it is an electronic Id.
                    callback.onResponseFailure("transaction card required");
                }else if(responseAPDU != null){
                    onTransactionCardInserted();// Check if transaction id was inserted.
                } else {
                    Log.d("TTT","card is not Supported here");
                    callback.onResponseFailure("transaction card required");
                }
                break;
        }
        if (responseAPDU.equals(eID[0]) || responseAPDU.equals(eID[1])) {
            onElectronicIdInserted(); //If application id shows that it is an electronic Id.
        }else if(responseAPDU != null){
            onTransactionCardInserted();// Check if transaction id was inserted.
        } else {
            Log.d("TTT","card is not Supported here");
        }
    }

    /*
     * Invoke electronic Id commands*/
    public void onElectronicIdInserted(){

        //access the payment system environment
        //select the Master File
        VNG k = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 00 A4 00 00 023F0000")));
        String k1 = Utility.bytes2Hex(k.parseBytes(k.size));

        //Check if the status word is ok.
        if ((k1.substring(k1.length()-4)).equals("9000"))
        {
            //Select the Dedicated file
            VNG z = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 00 A4 01 00 02020000")));
            String z1 = Utility.bytes2Hex(z.parseBytes(z.size));

            //Check to see if the dedicated file was selected
            if ((z1.substring(z1.length()-4)).equals("9000"))
            {
                String imageBase64 = null;
                HashMap<String, String> electronicIdData = new HashMap<>();
                for (int i =0; i<=10; i++){

                    String index = Integer.toHexString( i );
                    String index2 = ( i < 16 ) ? "0"+index : index;

                    if( i == 2 ) {
                        StringBuffer hexadecimalData = readHolderPhoto();
                        ElectronicIdDataExtraction dataExtraction = ElectronicIdDataExtraction.newInstance();
                        imageBase64 = dataExtraction.convertHexStringToBase64(hexadecimalData);
                    }else{
                        String hexadecimalData = readFileInfo(index2);
                        Log.d("TTT", hexadecimalData + "");
                        ElectronicIdDataExtraction dataExtraction = ElectronicIdDataExtraction.newInstance();
                        HashMap<String, String> data = dataExtraction.decodeElectronicId( hexadecimalData );
                        electronicIdData.putAll(data);
                    }
                }
                if(electronicIdData.containsKey("PassportExpiryDate"))
                    startAnotherActivity(electronicIdData, imageBase64);

            } else {
                //code to add the lenght that came with 61
            }
        }else {
            //code to add the lenght that came with 61
        }
    }

    private static StringBuffer readHolderPhoto(){
        String status = "9000";
        String imageHex = "";
        String imageHex1 = "";
        int increment = 0;
        //int offset = 0;
        StringBuffer imageBuffer = new StringBuffer();
        while ( status.equals("9000") ) {
            String imageString;
            try {
                VNG y = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 00A4020002020200")));
                String y1 = Utility.bytes2Hex(y.parseBytes(y.size));

                String index = Integer.toHexString(increment);
                String index2 = (increment < 16) ? "0" + index : index;

                if ((y1.substring(y1.length() - 4)).equals("9000")) {
                    VNG imageCommand;

                    imageCommand = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 05 00B0" + index2 + "0000")));
                    imageHex = Utility.bytes2Hex(imageCommand.parseBytes(imageCommand.size));
                }

                VNG yy = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 00A4020002020200")));
                String yy1 = Utility.bytes2Hex(yy.parseBytes(yy.size));

                if ((yy1.substring(yy1.length() - 4)).equals("9000")) {
                    VNG imageCommand1;

                    imageCommand1 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 05 00B0" + index2 + "0100")));
                    imageHex1 = Utility.bytes2Hex(imageCommand1.parseBytes(imageCommand1.size));
                    imageHex1 = imageHex1.substring(imageHex.length() - 6).substring(0, 2);

                }

                status = imageHex.substring(imageHex.length() - 4);
                if (imageHex.contains("FFD8")) {
                    imageString = imageHex.substring(imageHex.indexOf("FFD8"), imageHex.length() - 4);
                } else if (imageHex.contains("FFD9")) {
                    imageString = imageHex.substring(14, imageHex.indexOf("FFD9") + 4);
                    //Log.d("TTT", imageString + "");
                    imageBuffer.append(imageString);
                    break;
                } else {
                    imageString = (imageHex.length() > 20) ? imageHex.substring(14, imageHex.length() - 4) : "";
                }
                //Log.d("TTT", imageString + "");
                imageBuffer.append(imageString + imageHex1);
                increment++;
            }catch (Exception ex){
                EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("00 00 00 00")));
                appendLog(ex.getLocalizedMessage());
                Log.e("TTT", ex.getMessage());
                break;
            }
        }
        return imageBuffer;
    }

    public static String readFileInfo(String fileIndex){
        String defaultStatus = "9000";
        String status = defaultStatus;
        int increment = 0;
        String dataHex = "", dataHex1 = "";
        StringBuffer infoBuffer = new StringBuffer();

        while ( status.equals("9000") ) {
            try {
                VNG y = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 00A402000202" + fileIndex + "00")));
                String y1 = Utility.bytes2Hex(y.parseBytes(y.size));

                String index = Integer.toHexString(increment);
                String index2 = (increment < 16) ? "0" + index : index;

                if ((y1.substring(y1.length() - 4)).equals("9000")) {
                    VNG imageCommand;

                    imageCommand = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 05 00B0" + index2 + "0000")));
                    dataHex = Utility.bytes2Hex(imageCommand.parseBytes(imageCommand.size));
                    if (!(dataHex.substring(dataHex.length() - 4)).equals("9000"))
                        break;
                    dataHex = dataHex.substring(14, dataHex.length() - 4);
                } else {
                    break;
                }

                VNG yy = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 00A4020002020500")));
                String yy1 = Utility.bytes2Hex(yy.parseBytes(yy.size));

                if ((yy1.substring(yy1.length() - 4)).equals("9000")) {
                    VNG imageCommand1;

                    imageCommand1 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 05 00B0" + index2 + "0100")));
                    dataHex1 = Utility.bytes2Hex(imageCommand1.parseBytes(imageCommand1.size));
                    dataHex1 = dataHex1.substring(dataHex1.length() - 6).substring(0, 2);

                }

                if ((y1.substring(y1.length() - 4)).equals("9000")) {

                    if (dataHex.startsWith("0000000000")) {
                        break;
                    } else if (dataHex.endsWith("0000")) {
                        dataHex = dataHex.substring(0, dataHex.indexOf("0000"));
                        infoBuffer.append(dataHex);
                        break;
                    }
                    Log.d("TTT", dataHex + dataHex1);
                }

                //Log.d("TTT", imageString + "");
                infoBuffer.append(dataHex + dataHex1);
                increment++;
            }catch (Exception ex){
                EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("00 00 00 00")));
                Log.e("TTT", ex.getMessage());
                appendLog(ex.getLocalizedMessage());
                break;
                //byte[] arData = Utility.hex2Byte("00 00 00 00");
                //req.addRSLenData(arData);

                //rsp = exchangeData(req);
            }
        }
        return infoBuffer.toString();
    }

    private static void startAnotherActivity(HashMap<String, String> cardInformation, String cardHolderImage){
        JsonObject object = new JsonObject();
        object.addProperty("cardInformation",new JSONObject(cardInformation).toString());
        object.addProperty("cardHolderImage",cardHolderImage);
        callback.onResponseSuccess(object.toString());

    }








    /*
     * These methods are for reading transactional chip cards*/

    public static void onTransactionCardInserted(){
        // Declare a vng obect to use to access the data
        VNG vng_response;
        //access the payment system environment
        vng_response = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 14 00A404000E315041592E5359532E444446303100")));
        //From the response get the excessive bytes with get response
        String response = Utility.bytes2Hex(vng_response.parseBytes(vng_response.size));
        //Log.d("TTT", "Response 1: "+response);

        String subResponse = response.substring(response.length() - 4);
        subResponse = subResponse.replace(" ", "");
        //Check if the returned string is correct or not
        if (subResponse.equals("9000")) {
            //If the SW is 9000, then we have accessed the payment environment file
//                EmvTransactionPresenter.callAddLog("your card accessed payment system environment file");
            //read the response
            VNG vng_response2 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 05 00B2010C00")));
            //convert it to a string
            String response2 = Utility.bytes2Hex(vng_response2.parseBytes(vng_response2.size));
            //Log.d("TTT", "Response 2: "+response2);
            //check the status word
            String k = response2.substring(response2.length() - 4);

            if (k.equals("9000")) {
                //get the application id from the returned string
                String applicationId = response2.substring(26, 40);


                String wlk;
                if (applicationId.equals("A0000000041010")) {
//                        EmvTransactionPresenter.callAddLog("MasterCard payment environment");
                    vng_response3 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 0D 00A4040007"+applicationId+"00")));
                    response3 = Utility.bytes2Hex(vng_response3.parseBytes(vng_response3.size));
                    //Log.d("TTT", "Response 3: "+response3);

                    //CHeck the status word
                    if (response3.substring(response3.length() - 4).equals("9000")) {
                        readTransactionCardInformation();
                    }
                }
                else if (!applicationId.equals(null) || applicationId.equals("A0000000031010")) {
                    //for visa cards, we have AID = A0000000031010, hence we select it.
                    vng_response3 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 0D 00A4040007"+applicationId+"00")));
                    response3 = Utility.bytes2Hex(vng_response3.parseBytes(vng_response3.size));
                    //Log.d("TTT", "Response 3: "+response3);
                    if (response3.substring(response3.length() - 4).equals("9000")) {
                        readTransactionCardInformation();
                    }
                }else{
                    invokeApplicationById();
                }
            }
        } else if(subResponse.equals("6A82") || subResponse.equals("6A83")){
            invokeApplicationById();
        }
    }

    public static void invokeApplicationById(){
        for (int index=0; index<applicationIds.length; index++) {
            int applicationIdLength = applicationIds[index].length()/2;
            String apduCommandLength = Integer.toHexString( applicationIdLength+6 );
            apduCommandLength = ( apduCommandLength.length() < 2) ? "0"+apduCommandLength : apduCommandLength;

            vng_response3 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 "+apduCommandLength+" 00A404000"+applicationIdLength+applicationIds[index]+"00")));
            response3 = Utility.bytes2Hex(vng_response3.parseBytes(vng_response3.size));
            if ( response3.substring( response3.length()-4 ).equals("9000") ) {
                String applicationName = Utility.hex2string(response3);
                if(applicationName.contains("MASTER") || applicationName.contains("Master")) {
                    readTransactionCardInformation();
                    break;
                }else if(applicationName.contains("VISA") || applicationName.contains("Visa")) {
                    readTransactionCardInformation();
                    break;
                }
            }
        }
    }


    private static void readTransactionCardInformation(){
        String cardName = null;
        String cardNumber = null;
        String expiryDate = null;
        if (!response3.contains("9F38")) {
            VNG vng_response4 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 08 80A8000002830000")));
            String response4 = Utility.bytes2Hex(vng_response4.parseBytes(vng_response4.size));
            //Log.d("TTT", "Response 4: "+response4);
            String q = response4.substring(response4.length() - 4);
            if (q.equals("9000")) {
                response4 = response4.substring(response4.indexOf("94"), response4.length()-4);

                int applicationDataLength = Integer.parseInt( response4.substring(2, 4), 16);

                String aip = response4.substring( 4, ( applicationDataLength*2 ) + 4 );

                ArrayList<String> AFLs = new ArrayList<>();

                int numFiles = aip.length()/8;
                int firstIndex = 0;
                for(int value = 0; value<numFiles; value++) {
                    int lastIndex = firstIndex + 8;
                    AFLs.add(aip.substring(firstIndex, lastIndex));
                    firstIndex = lastIndex;
                }


                for ( int index = 0; index < AFLs.size(); index ++ ){
                    String AFL = AFLs.get(index);
                    try {
                        int SFIValue = Integer.parseInt( AFL.substring(0, 2), 16 ) + 4;
                        String SFI = Integer.toHexString(SFIValue);
                        SFI = (SFIValue<16) ? "0"+SFI : SFI;
                        for(int value = 1; value<= Integer.parseInt(AFL.substring(5, 6)); value++){
                            VNG vng_response5 = EmvTransactionPresenter.callExchangeData(new VNG(Utility.hex2Byte("51 53 45 1E 00 05 00B20" + value + SFI + "00")));
                            String response5 = Utility.bytes2Hex(vng_response5.parseBytes(vng_response5.size));

                            String data = Utility.hex2string(response5);
                            //Log.d("TTT", "Expiry Date: "+data);

                            if(response5.contains("5F24")) {
                                expiryDate = TransactionCardDataExtraction.getExpiryDate(response5);
                                Log.d("TTT", "Expiry Date: "+expiryDate);
                            }

                            if(response5.contains("5F34") && response5.contains("5A") && response5.indexOf("5A")< response5.indexOf("5F34") ) {
                                cardNumber = response5.substring( response5.indexOf("5A")+4, response5.indexOf("5F34") );
                                Log.d("TTT", "Card Number: "+cardNumber);
                            }

                            if(response5.contains("5F20") ) {
                                String namePart = response5.substring(response5.indexOf("5F20")+4);
                                String nameLength = namePart.substring( 0, 2 );
                                int length = Integer.parseInt(nameLength, 16)*2;
                                cardName = namePart.substring(2, length+2);

                                cardName = Utility.hex2string(cardName);
                                Log.d("TTT", "Card Name : "+cardName );
                            }

                            callback.onResponseSuccess(cardName+" "+expiryDate+" "+cardNumber);

                        }

                    }catch(Exception nfe){
                        callback.onResponseFailure(nfe.getLocalizedMessage());
                    }
                }
            }
        }
    }
}
