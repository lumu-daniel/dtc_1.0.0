package com.mlt.dtc.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import com.mlt.dtc.R;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.AsyncResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

public
class AsyncServiceCall extends AsyncTask<Object, Object, Object> {

    //Call back interface
    public AsyncResponse delegate;
    private ProgressDialog dialog;
    private Context mContext;
    Resources resources;
    Handler mHandler;

    // Call back assigning to local delegate
    public AsyncServiceCall(AsyncResponse asyncResponse, Context context) {
        //Assigning call back interface through constructor
        delegate = asyncResponse;
        mContext = context;
        resources = context.getResources();
        String Language = PreferenceConnector.readString(mContext, Constant.Language, "");
        mContext = LocaleHelper.setLocale(mContext, Language);
        try {
            dialog = ProgressDialog.show(context, resources.getString(R.string.progress_title),
                    resources.getString(R.string.page_is_loading));
            dialog.setCancelable(false);
        }catch (Exception e){
            e.printStackTrace();
        }

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
            e.printStackTrace();
        }

        return response;

    }

    @Override
    protected void onPostExecute(Object result) {
        if(dialog!=null){
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        delegate.processFinish(result);
    }

    public void stopDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }

        Common.TimeoutAlertDialog(mContext);
    }
}
