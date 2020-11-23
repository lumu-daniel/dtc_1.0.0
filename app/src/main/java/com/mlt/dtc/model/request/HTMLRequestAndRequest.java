package com.mlt.dtc.model.request;

import android.util.Log;

public class HTMLRequestAndRequest {

    public String paymentRequest(PaymentRequest request ) {

        String htmlRequest = null;

        try {
            htmlRequest = String.format("<!DOCTYPE html>\n" +
                            "\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "    <meta name=\"viewport\" content=\"width=device-width\" />\n" +
                            "    <title>Test</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "\n" +
                            "\n" +
                            "    <form id=\"frmToPost\" method=\"POST\" action=\"%s\">\n" +
                            "        <div id=\"divRequestKioskForm\">\n" +
                            "            <input type=\"hidden\" name=\"RequestId\" id=\"RequestId\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"RequestCategory\" id=\"RequestCategory\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"SourceApplication\" id=\"SourceApplication\n" +
                            "\t\t\t\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"DeviceFingerPrint\" id=\"DeviceFingerPrint\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"MerchantId\" id=\"MerchantId\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"UserId\" id=\"UserId\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"Password\" id=\"Password\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"IpAssigned\" id=\"IpAssigned\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"Version\" id=\"Version\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"ServiceId\" id=\"ServiceId\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"ServiceName\" id=\"ServiceName\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"TimeStamp\" id=\"TimeStamp\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"TransactionReferenceNumber\" id=\"TransactionReferenceNumber\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"ReferenceNumber\" id=\"ReferenceNumber\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"Currency\" id=\"Currency\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"Amount\" id=\"Amount\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"FirstName\" id=\"FirstName\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"LastName\" id=\"LastName\" value=\"%s\" />\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"CustomerAddress\" id=\"CustomerAddress\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CustomerCity\" value=\"%s\" id=\"CustomerCity\" />\n" +
                            "            <input type=\"hidden\" name=\"CustomerPostalCode\" id=\"CustomerPostalCode\"  value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CustomerState\" id=\"CustomerState\"  value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CustomerCountry\" id=\"CustomerCountry\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CustomerEmail\" id=\"CustomerEmail\" value=\"%s\" />\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"CustomerContactNumber\" id=\"CustomerContactNumber\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CallBackUrl\" id=\"CallBackUrl\" value=\"%s\" />\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"PaymentChannel\" id=\"PaymentChannel\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"Language\" id=\"Language\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"SubMerchantID\" id=\"SubMerchantID\"  value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"SignatureFields\" id=\"SignatureFields\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"SecureHash\" id=\"SecureHash\" value=\"%s\" />\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"CardNumber\" id=\"CardNumber\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CardType\" id=\"CardType\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CVV\" id=\"CVV\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CardExpiry\" id=\"CardExpiry\" value=\"%s\" />\n" +
                            "            <input type=\"hidden\" name=\"CustomerIPAddress\" id=\"CustomerIPAddress\" value=\"%s\" />\n" +
                            "\n" +
                            "            <hr />\n" +
                            "            <button id=\"btn_submit\" hidden=\"hidden\" type=\"submit\" />\n" +
                            "        </div>\n" +
                            "        </form>\n" +
                            "\n" +
                            "\n" +
                            "    </body>\n" +
                            "    </html>\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "<script type=\"text/javascript\">\n" +
                            "\n" +
                            "    window.onload = function () {\n" +
                            "        var hangoutButton = document.getElementById(\"btn_submit\");\n" +
                            "        hangoutButton.click();\n" +
                            "    };\n" +
                            "\n" +
                            "</script>\n",
                    request.getAction(),
                    request.getRequestId(),
                    request.getRequestCategory(),
                    request.getSourceApplication(),
                    request.getDeviceFingerPrint(),
                    request.getMerchantId(),
                    request.getUserId(),
                    request.getPassword(),
                    request.getIpAssigned(),
                    request.getVersion(),
                    request.getServiceId(),
                    request.getServiceName(),
                    request.getTimeStamp(),
                    request.getTransactionReferenceNumber(),
                    request.getReferenceNumber(),
                    request.getCurrency(),
                    request.getAmount(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getCustomerAddress(),
                    request.getCustomerCity(),
                    request.getCustomerPostalCode(),
                    request.getCustomerState(),
                    request.getCustomerCountry(),
                    request.getCustomerEmail(),
                    request.getCustomerContactNumber(),
                    request.getCallBackUrl(),
                    request.getPaymentChannel(),
                    request.getLanguage(),
                    request.getSubMerchantID(),
                    request.getSignatureFields(),
                    request.getSecureHash(),
                    request.getCardNumber(),
                    request.getCardType(),
                    request.getCVV(),
                    request.getCardExpiry(),
                    request.getCustomerIPAddress());

        } catch (Exception ex) {
            Log.d("The Exception is",ex.getLocalizedMessage());
        }
        return htmlRequest;
    }
}
