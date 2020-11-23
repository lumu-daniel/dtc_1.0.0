package com.mlt.dtc.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public
interface IncidentManagement {
    @POST("Create/")
    @FormUrlEncoded
    Call<Object> postIncident(@Field("CreatedBy") String CreatedBy, @Field("EmailId") String EmailId, @Field("ReferenceID") String ReferenceID, @Field("CreatedDate") String CreatedDate, @Field("IncidentTitle") String IncidentTitle, @Field("Requester") String Requester, @Field("Severity") String Severity, @Field("PhoneNumber") String PhoneNumber, @Field("Description") String Description, @Field("ServiceProviderId") String ServiceProviderId, @Field("RequestId") String RequestId, @Field("RequestNumber") String RequestNumber, @Field("DeviceNumber") String DeviceNumber, @Field("ServiceId") String ServiceId, @Field("PaymentType") String PaymentType, @Field("TotalFineAmount") String TotalFineAmount, @Field("IsPublic") String IsPublic, @Field("IssueType") String IssueType, @Field("ResolverGroup") String ResolverGroup, @Field("MyResolverGroup") String MyResolverGroup, @Field("LocationId") String LocationId, @Field("DevicesId") String DevicesId, @Field("OrganizationDepartmentId") String OrganizationDepartmentId, @Field("BackendApplicationId") String BackendApplicationId, @Field("ForMerchant") String ForMerchant);

}
