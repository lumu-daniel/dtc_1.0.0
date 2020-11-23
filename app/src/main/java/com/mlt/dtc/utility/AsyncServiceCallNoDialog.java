package com.mlt.dtc.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.mlt.dtc.interfaces.AsyncResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;


/**
 * Created by raheel on 3/4/2018.
 */


public class AsyncServiceCallNoDialog extends AsyncTask<Object, Object, Object> {

    //Call back interface
    public AsyncResponse delegate;
    Handler mHandler;
    String ClassName;

    // Call back assigning to local delegate
    public AsyncServiceCallNoDialog(AsyncResponse asyncResponse, Context context) {
        //Assigning call back interface through constructor
        delegate = asyncResponse;

    }

    @Override
    protected Object doInBackground(Object... params) {

        HttpPost httppost = new HttpPost((String) params[1]);

        StringEntity se;
        String SOAPRequestXML;
        String response = null;

        try {
            // Pass the Object Here and Get the XML
            SOAPRequestXML = (String) params[0];

            se = new StringEntity(SOAPRequestXML, "UTF-8");
            se.setContentType("text/xml");
            httppost.setHeader("Accept-Charset", "utf-8");
            httppost.setHeader("soapaction", (String) params[2]);
            httppost.setEntity(se);

            HttpClient httpclient = new DefaultHttpClient();
            BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
            HttpEntity resEntity = httpResponse.getEntity();
            response = EntityUtils.toString(resEntity);

//            Log.e("RESPONSE: ", response);
        } catch (Exception e) {
            //ExceptionService.ExceptionLogService(mContext, e.getMessage(), ClassName, AndroidSerialNo);
        }

        return response;

    }

    @Override
    protected void onPostExecute(Object result) {
       /* if (dialog.isShowing()) {
            dialog.dismiss();
        }*/
        delegate.processFinish(result);
    }
}
