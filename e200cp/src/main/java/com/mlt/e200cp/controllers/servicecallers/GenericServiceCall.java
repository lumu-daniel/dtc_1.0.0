package com.mlt.e200cp.controllers.servicecallers;

import android.util.Log;

import com.mlt.e200cp.interfaces.ClientApi;
import com.mlt.e200cp.interfaces.GeneralServiceCallback;
import com.mlt.e200cp.interfaces.ResultsCallback;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.mlt.e200cp.utilities.helper.util.Utility.appendLog;

public class GenericServiceCall {

    private Retrofit retrofit;
    private static GenericServiceCall genericServiceCall;
    private String BaseURL, SOAPRequestXML, Endpoint, soapAction;

    public GenericServiceCall(String baseURL, String SOAPRequestXML, String endpoint, String soapAction) {
        BaseURL = baseURL;
        this.SOAPRequestXML = SOAPRequestXML;
        Endpoint = endpoint;
        this.soapAction = soapAction;
    }

    public static GenericServiceCall getInstance(String baseURL, String SOAPRequestXML, String endpoint, String soapAction){
        return (genericServiceCall!=null)?genericServiceCall:new GenericServiceCall(baseURL,SOAPRequestXML,endpoint,soapAction);
    }

    private Call<String> getResponse(RequestBody body, String endpoint) {
        ClientApi clientApi = retrofit.create(ClientApi.class);
        Call<String> call = clientApi.getSoapClient(/*"AdjdRMSService.svc"*/endpoint, "wsdl", body);
        return call;
    }

    Request request;
    private OkHttpClient getOkHttpClient(final String SOAPAction){
        return new OkHttpClient.Builder()
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(210, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        try {
                            request = chain.request();
                            request = request.newBuilder()
                                    .addHeader("soapaction", SOAPAction)
                                    .build();

                        } catch (Exception e) {
                            appendLog(e.getLocalizedMessage());
                            e.getMessage();
                        }
                        return chain.proceed(request);
                    }
                }).build();
    }


    public synchronized void callService(final GeneralServiceCallback callback){
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), SOAPRequestXML);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURL+"/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(getOkHttpClient(soapAction))
                    .addConverterFactory(SimpleXmlConverterFactory.create(new Persister(new AnnotationStrategy())))
                    .build();

            getResponse(requestBody,Endpoint).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    callback.onResponseSuccess(response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    appendLog(t.getLocalizedMessage());
                    Log.e("TAG",t.getMessage());
                    callback.onResponseFailure(t.getMessage());
                }
            });

        } catch (Exception e) {
            callback.onResponseFailure(e.getMessage());
            e.printStackTrace();
            callback.onResponseFailure(e.getMessage());
            appendLog(e.getLocalizedMessage());
        }

    }
}
