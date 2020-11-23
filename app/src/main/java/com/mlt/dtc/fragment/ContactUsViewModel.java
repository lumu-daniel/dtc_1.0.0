package com.mlt.dtc.fragment;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.Configuration;
import com.mlt.dtc.interfaces.IncidentManagement;
import com.mlt.dtc.model.IncedentRequest;
import com.mlt.dtc.utility.Constant;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.mlt.dtc.common.Configuration.NOT_AVAILABLE;

public
class ContactUsViewModel extends AndroidViewModel implements LifecycleObserver, ViewModelProvider.Factory{

    private final MutableLiveData<retrofit2.Response<Object>> incidentDataSuccess;
    private final MutableLiveData<Throwable> incidentDataFail;
    private final View view;
    private final FragmentActivity fragmentActivity;
    private final Application mApplication;
    private IncedentRequest incedentRequest;

    public ContactUsViewModel(@NonNull Application application, View classView, FragmentActivity activity) {
        super(application);
        incidentDataSuccess = new MutableLiveData<>();
        incidentDataFail = new MutableLiveData<>();
        this.view = classView;
        this.fragmentActivity = activity;
        this.mApplication = application;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onLifeCycleStop() {
        try {
            InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onLifeCyclePause() {
        try {
            InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onLifeCycleDestroy() {
        try {
            InputMethodManager imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    void doClickAction(int id) {
        switch (id){
            case R.id.btnback:
                 MainActivity.count = 0;
                addFragment(new MainBannerVideoFragment(),fragmentActivity);
                break;
            case R.id.confirmation_button:
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;
//            case R.id.adminlogin:
//                addFragment(new SettingsLoginFragment(),fragmentActivity);
//                break;
//            case R.id.confirmation_button:
//                addFragment(new ContactUsFragment(),fragmentActivity);
//                break;
            /*case R.id.iv_feedback:
                buttonPressed = Constant.FeedBack;
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;
            case R.id.iv_complaints:
                buttonPressed = Constant.Complaint;
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;
            case R.id.iv_aboutus:
                buttonPressed = Constant.Aboutus;
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;
            case R.id.iv_regulatory:
                buttonPressed = Constant.Regulatory;
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;
            case R.id.iv_admin:
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;
            case R.id.iv_contactus:
                buttonPressed = Constant.Contactus;
                addFragment(new ContactUsFragment(),fragmentActivity);
                break;*/
        }
    }

//    void setImageResources(int id){
//        switch (id){
//            case R.id.iv_regulatory:
//                Glide.with(fragmentActivity).asBitmap().load(R.drawable.regulatory).into((ImageView) view.findViewById(R.id.iv_regulatory));
//                break;
//            case R.id.iv_admin:
//                Glide.with(fragmentActivity).asBitmap().load(R.drawable.adminbutton).into((ImageView) view.findViewById(R.id.iv_admin));
//                break;
//            case R.id.iv_complaints:
//                Glide.with(fragmentActivity).asBitmap().load(R.drawable.complaint).into((ImageView) view.findViewById(R.id.iv_complaints));
//                break;
//
//        }
//    }

    private void addFragment(Fragment fragment, FragmentActivity activity) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }



    void sendIncident(String [] args) {

        String severity = (args[6].equalsIgnoreCase(Configuration.FEEDBACK)) ? Configuration.LOW : Configuration.HIGH;

        incedentRequest = new IncedentRequest();
        incedentRequest.setCreatedBy(Configuration.CREATEDBY_VALUE);
        incedentRequest.setEmailId(args[1]);
        incedentRequest.setReferenceID("REQ-" + args[4]);
        incedentRequest.setCreatedDate(Common.getDate());
        incedentRequest.setIncidentTitle(args[4]);
        incedentRequest.setRequester(args[0]);
        incedentRequest.setSeverity(severity);
        incedentRequest.setPhoneNumber(args[2]);
        incedentRequest.setDescription(args[3]);
        incedentRequest.setServiceProviderId(Configuration.SERVICEPROVIDERID_VALUE);
        incedentRequest.setRequestId(NOT_AVAILABLE);
        incedentRequest.setRequestNumber(NOT_AVAILABLE);
        incedentRequest.setDeviceNumber(NOT_AVAILABLE);
        incedentRequest.setServiceId(Configuration.SERVICEID_VALUE);
        incedentRequest.setPaymentType("na");
        incedentRequest.setTotalFineAmount(Configuration.TOTALFINEAMOUNT_VALUE);
        incedentRequest.setIsPublic(Configuration.ISPUBLIC_VALUE);
        incedentRequest.setIssueType(args[6]);
        incedentRequest.setResolverGroup(Configuration.RESOLVERGROUP_VALUE);
        incedentRequest.setMyResolverGroup(Configuration.MYRESOLVERGROUP_VALUE);
        incedentRequest.setLocationId(null);
        incedentRequest.setDevicesId(null);
        incedentRequest.setOrganizationDepartmentId(Configuration.ORGANIZATIONDEPARTMENTID_VALUE);
        //incedentRequest.setIsBranchUser("NULL");
        incedentRequest.setBackendApplicationId(Configuration.BACKENDAPPLICATIONID_VALUE);
        incedentRequest.setForMerchant(Configuration.FORMERCHANT_VALUE);

        //IncidentMenegementServiceCall();
        postIncident();
        /*}else {
            Toast.makeText(getContext(), "Wrong email address please", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void postIncident() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.serviceBaseUrl)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        IncidentManagement incidentManagement = retrofit.create(IncidentManagement.class);
        Call<Object> call = incidentManagement.postIncident( incedentRequest.getCreatedBy(), incedentRequest.getEmailId(), incedentRequest.getReferenceID(), incedentRequest.getCreatedDate(), incedentRequest.getIncidentTitle(), incedentRequest.getRequester(), incedentRequest.getSeverity(), incedentRequest.getPhoneNumber(), incedentRequest.getDescription(), incedentRequest.getServiceProviderId(), incedentRequest.getRequestId(), incedentRequest.getRequestNumber(), incedentRequest.getDeviceNumber(), incedentRequest.getServiceId(), incedentRequest.getPaymentType(), incedentRequest.getTotalFineAmount(), incedentRequest.getIsPublic(), incedentRequest.getIssueType(), incedentRequest.getResolverGroup(), incedentRequest.getMyResolverGroup(), incedentRequest.getLocationId(), incedentRequest.getDevicesId(), incedentRequest.getOrganizationDepartmentId(), incedentRequest.getBackendApplicationId(), incedentRequest.getForMerchant() );
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                incidentDataSuccess.setValue(response);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                incidentDataFail.setValue(t);
            }
        });
    }

    LiveData<retrofit2.Response<Object>> postIncidentSuccess(){
        return incidentDataSuccess;
    }

    LiveData<Throwable> postIncidentFail(){
        return incidentDataFail;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new ContactUsViewModel(mApplication,view,fragmentActivity);
    }
}
