package com.mlt.e200cp.models.repository.response.MerchantDetailsService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MerchantDetailArr {

    @SerializedName("MerchantMultipleDetails")
    @Expose
    private List<MerchantDetails> merchantMultipleDetails = null;

    public List<MerchantDetails> getMerchantMultipleDetails() {
        return merchantMultipleDetails;
    }

    public void setMerchantMultipleDetails(List<MerchantDetails> merchantMultipleDetails) {
        this.merchantMultipleDetails = merchantMultipleDetails;
    }

}