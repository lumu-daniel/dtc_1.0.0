package com.mlt.dtc.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.mlt.dtc.R;
import java.util.ArrayList;
import java.util.List;

public
class WifiFragment extends Fragment {
    ListView listView;
    Button btn_back,btnrefresh;
    ArrayAdapter<String> arrayAdapter;
    List<String> listOfNetworks;
    WifiManager wifiManager;
    Context context;
    List<ScanResult> scanResultList;
    Dialog dialog;
    boolean booleanPassword = true;
    public ProgressDialog pd;
    String connectedWifi = "";
    public WifiFragment() {
    }

    public static WifiFragment newInstance() {
        return new WifiFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.wifilist, container, false);

        context=container.getContext();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        scanResultList =  new ArrayList<>();

        listView = view.findViewById(R.id.listOfNetworks);

        btn_back=view.findViewById(R.id.btnback);

        btnrefresh=view.findViewById(R.id.btnrefresh);

        pd = new ProgressDialog(context);
        pd.setMessage("loading");
        pd.show();

        listOfNetworks = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(context,R.layout.row,R.id.list_value,listOfNetworks);
        listView.setAdapter(arrayAdapter);


        wifiManager = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);


        connectedWifi = getCurrentSsid(getContext());
        if(connectedWifi!=null) { connectedWifi = connectedWifi.substring(1, connectedWifi.length()-1); }

        boolean success = wifiManager.startScan();


//        MyBroadcastReceiver wifiScanReceiver =new  MyBroadcastReceiver();
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new AdminFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(WifiFragment.this).attach(WifiFragment.this).commit();
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        context.registerReceiver(wifiScanReceiver, intentFilter);






        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ScanResult scanResult = scanResultList.get(position);


                if(!scanResult.SSID.equals(connectedWifi)){

                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_ssid_password);

                    TextView tvSSID = dialog.findViewById(R.id.tvSSID);
                    tvSSID.setText(scanResult.SSID);
                    Button btnConnect = dialog.findViewById(R.id.btnConnect);
                    Button btncancel = dialog.findViewById(R.id.btnCancel);
                    final EditText ePassword = dialog.findViewById(R.id.ePassword);
                    ePassword.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {


                            if(event.getAction() == MotionEvent.ACTION_UP) {
                                if(event.getRawX() >= ePassword.getRight() - ePassword.getTotalPaddingRight()) {
                                    // your action for drawable click event
                                    if(booleanPassword){

                                        ePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visible, 0);
                                        ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                        booleanPassword = false;

                                    }else{

                                        ePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_invisible, 0);
                                        ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        booleanPassword = true;
                                    }

                                    return true;
                                }
                            }

                            return false;
                        }
                    });
                    btnConnect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ConnectedWifi(scanResult.SSID,ePassword.getText().toString().trim(),dialog);
                        }
                    });
                    btncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();


                        }
                    });
//                dialog.getWindow().setLayout(100,100);

                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.CENTER);
                    dialog.getWindow().setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.show();

                }
                else{
                    Toast.makeText(getContext(),"already connected",Toast.LENGTH_LONG).show();
                }


            }
        });




        return view;
    }


    private void scanSuccess() {
        pd.dismiss();
        List<ScanResult> results = wifiManager.getScanResults();
        scanResultList = results;
        listOfNetworks.clear();
        for(int i = 0; i<results.size();i++){
            if(results.get(i).SSID.equals(connectedWifi)){
                listOfNetworks.add(results.get(i).SSID + " - Connected");
            }else{
                listOfNetworks.add(results.get(i).SSID);
            }


        }
        arrayAdapter.notifyDataSetChanged();
    }

    private void scanFailure() {
        pd.dismiss();
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!

        List<ScanResult> results = wifiManager.getScanResults();
    }

    private void ConnectedWifi(String networkSSID, String networkPass, Dialog dialog){

        //String networkSSID = "Ebone talreja";
        //String networkPass = "pass";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.preSharedKey = "\""+ networkPass +"\"";
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        int networkId =  wifiManager.addNetwork(conf);
        boolean ConnectionValue = false;
        if(networkId==-1){
            @SuppressLint("MissingPermission") List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
            for(WifiConfiguration c : networks) {
                if (conf.SSID.equals(c.SSID)) {
                    wifiManager.enableNetwork(c.networkId, true);
                    return;
                }
            }
        }else{

            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.enableNetwork(networkId, true);
            wifiManager.reconnect();

            ConnectionValue = checkWifiNegotiation(wifiManager,networkId);

        }

        TextView tvErrorMessage = dialog.findViewById(R.id.tvErrorMessage);
        if(ConnectionValue){

            dialog.dismiss();
            Toast.makeText(getContext(),"Connected",Toast.LENGTH_LONG).show();

        }else{

            wifiManager.removeNetwork(networkId);
            tvErrorMessage.setText("Password is incorrect");

        }


    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }

        return ssid;
    }

    private static boolean checkWifiNegotiation(WifiManager wifiManager, int netId) {
        boolean startedHandshake = false;
        boolean successful = false;

        for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            SupplicantState currentState = wifiManager.getConnectionInfo().getSupplicantState();
            if (!startedHandshake && currentState.equals(SupplicantState.FOUR_WAY_HANDSHAKE)) {
                startedHandshake = true;
            } else if (startedHandshake) {
                if (currentState.equals(SupplicantState.DISCONNECTED)) {
                    break;
                } else if (currentState.equals(SupplicantState.COMPLETED)) {
                    successful = true;
                    break;
                }
            }
            wifiManager.enableNetwork(netId, true);
        }

        // no matter what happened above, if COMPLETED then we have the correct pw
        if (!successful && wifiManager.getConnectionInfo().getSupplicantState().equals(SupplicantState.COMPLETED)) {
            successful = true;
        }

        return successful;
    }

    @Override
    public void onStop() {
        super.onStop();
        scanResultList.clear();
        listView = null;
        arrayAdapter.clear();
        context = null;
    }
}
