package com.mlt.e200cp.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Merchantdetailsarray implements Serializable {


        @SerializedName("DeviceSerialNumber")
        @Expose
        private String deviceSerialNumber;
        @SerializedName("ReasonCode")
        @Expose
        private String reasonCode;
        @SerializedName("xmlns")
        @Expose
        private String xmlns;
        @SerializedName("MerchantDetail")
        @Expose
        private MerchantDetail merchantDetail;
        @SerializedName("Message")
        @Expose
        private String message;
        @SerializedName("RequestId")
        @Expose
        private String requestId;
        @SerializedName("SecureHash")
        @Expose
        private String secureHash;
        @SerializedName("TimeStamp")
        @Expose
        private String timeStamp;
        @SerializedName("SignatureField")
        @Expose
        private String signatureField;

        public String getDeviceSerialNumber() {
            return deviceSerialNumber;
        }

        public void setDeviceSerialNumber(String deviceSerialNumber) {
            this.deviceSerialNumber = deviceSerialNumber;
        }

        public String getReasonCode() {
            return reasonCode;
        }

        public void setReasonCode(String reasonCode) {
            this.reasonCode = reasonCode;
        }

        public String getXmlns() {
            return xmlns;
        }

        public void setXmlns(String xmlns) {
            this.xmlns = xmlns;
        }

        public MerchantDetail getMerchantDetail() {
            return merchantDetail;
        }

        public void setMerchantDetail(MerchantDetail merchantDetail) {
            this.merchantDetail = merchantDetail;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getSecureHash() {
            return secureHash;
        }

        public void setSecureHash(String secureHash) {
            this.secureHash = secureHash;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getSignatureField() {
            return signatureField;
        }

        public void setSignatureField(String signatureField) {
            this.signatureField = signatureField;
        }




    public static class ItemDetails {
        @SerializedName("Field0")
        @Expose
        private String field0;
        @SerializedName("Field7")
        @Expose
        private String field7;
        @SerializedName("Field17")
        @Expose
        private String field17;
        @SerializedName("Field12")
        @Expose
        private String field12;
        @SerializedName("Field4")
        @Expose
        private String field4;
        @SerializedName("Field2")
        @Expose
        private String field2;
        @SerializedName("Field30")
        @Expose
        private String field30;
        @SerializedName("Field3")
        @Expose
        private String field3;
        @SerializedName("Field18")
        @Expose
        private String field18;
        @SerializedName("Field1")
        @Expose
        private String field1;
        @SerializedName("Field9")
        @Expose
        private String field9;
        @SerializedName("Field28")
        @Expose
        private String field28;
        @SerializedName("Field8")
        @Expose
        private String field8;
        @SerializedName("Field15")
        @Expose
        private String field15;
        @SerializedName("Field10")
        @Expose
        private String field10;
        @SerializedName("Field13")
        @Expose
        private String field13;
        @SerializedName("Field16")
        @Expose
        private String field16;
        @SerializedName("Field14")
        @Expose
        private String field14;
        @SerializedName("Field11")
        @Expose
        private String field11;
        @SerializedName("Field29")
        @Expose
        private String field29;
        @SerializedName("Field5")
        @Expose
        private String field5;
        @SerializedName("Field6")
        @Expose
        private String field6;

        public String getField7() {
            return field7;
        }

        public void setField7(String field7) {
            this.field7 = field7;
        }

        public String getField17() {
            return field17;
        }

        public void setField17(String field17) {
            this.field17 = field17;
        }

        public String getField12() {
            return field12;
        }

        public void setField12(String field12) {
            this.field12 = field12;
        }

        public String getField4() {
            return field4;
        }

        public void setField4(String field4) {
            this.field4 = field4;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField30() {
            return field30;
        }

        public void setField30(String field30) {
            this.field30 = field30;
        }

        public String getField3() {
            return field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }

        public String getField18() {
            return field18;
        }

        public void setField18(String field18) {
            this.field18 = field18;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField9() {
            return field9;
        }

        public void setField9(String field9) {
            this.field9 = field9;
        }

        public String getField28() {
            return field28;
        }

        public void setField28(String field28) {
            this.field28 = field28;
        }

        public String getField8() {
            return field8;
        }

        public void setField8(String field8) {
            this.field8 = field8;
        }

        public String getField15() {
            return field15;
        }

        public void setField15(String field15) {
            this.field15 = field15;
        }

        public String getField10() {
            return field10;
        }

        public void setField10(String field10) {
            this.field10 = field10;
        }

        public String getField13() {
            return field13;
        }

        public void setField13(String field13) {
            this.field13 = field13;
        }

        public String getField16() {
            return field16;
        }

        public void setField16(String field16) {
            this.field16 = field16;
        }

        public String getField14() {
            return field14;
        }

        public void setField14(String field14) {
            this.field14 = field14;
        }

        public String getField11() {
            return field11;
        }

        public void setField11(String field11) {
            this.field11 = field11;
        }

        public String getField29() {
            return field29;
        }

        public void setField29(String field29) {
            this.field29 = field29;
        }

        public String getField5() {
            return field5;
        }

        public void setField5(String field5) {
            this.field5 = field5;
        }

        public String getField6() {
            return field6;
        }

        public void setField6(String field6) {
            this.field6 = field6;
        }

        public String getField0() {
            return field0;
        }

        public void setField0(String field0) {
            this.field0 = field0;
        }
    }


    public class MerchantDetail {

        @SerializedName("MerchantMultipleDetails")
        @Expose
        private List<MerchantMultipleDetail> merchantMultipleDetails = null;

        public List<MerchantMultipleDetail> getMerchantMultipleDetails() {
            return merchantMultipleDetails;
        }

        public void setMerchantMultipleDetails(List<MerchantMultipleDetail> merchantMultipleDetails) {
            this.merchantMultipleDetails = merchantMultipleDetails;
        }

    }


    public static class MerchantMultipleDetail {

        @SerializedName("MerchantServiceType")
        @Expose
        private String merchantServiceType;
        @SerializedName("ItemDetails")
        @Expose
        private ItemDetails itemDetails;

        public String getMerchantServiceType() {
            return merchantServiceType;
        }

        public void setMerchantServiceType(String merchantServiceType) {
            this.merchantServiceType = merchantServiceType;
        }

        public ItemDetails getItemDetails() {
            return itemDetails;
        }

        public void setItemDetails(ItemDetails itemDetails) {
            this.itemDetails = itemDetails;
        }

    }
}
