package com.mlt.e200cp.models.repository.response.MerchantDetailsService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MerchantDetail {

    @SerializedName("MerchantMultipleDetails")
    @Expose
    private MerchantDetails merchantMultipleDetails = null;

    public MerchantDetails getMerchantMultipleDetails() {
        return merchantMultipleDetails;
    }

    public void setMerchantMultipleDetails(MerchantDetails merchantMultipleDetails) {
        this.merchantMultipleDetails = merchantMultipleDetails;
    }

}