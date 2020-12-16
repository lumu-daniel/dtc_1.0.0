package com.mlt.dtc.location;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;


public class getReverseGeoCoding {
    public static final String API_KEY = "AIzaSyB0ZCLT-QKhe5S4gdIP5h9MAE9x6w33WuY";
    private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "";
    String Language = "ar";

    public void getAddress(double latitude, double longitude) {
        Address1 = "";
        Address2 = "";
        City = "";
        State = "";
        Country = "";
        County = "";
        PIN = "";

        try {

            JSONObject myJsonObj = parser_Json.getJSONfromURL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + API_KEY);


            JSONArray Results = myJsonObj.getJSONArray("results");
            JSONObject zero = Results.getJSONObject(0);
            //String myAddress = zero.getString("formatted_address");
            //Address1 = myAddress;
            JSONArray address_components = zero.getJSONArray("address_components");

            Log.d(TAG, "getAddress: " + address_components);

            for (int i = 0; i < address_components.length(); i++) {
                JSONObject zero2 = address_components.getJSONObject(i);
                String long_name = zero2.getString("long_name");
                JSONArray mtypes = zero2.getJSONArray("types");
                String Type = mtypes.getString(0);

                if (!TextUtils.isEmpty(long_name) || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                    //Address1 = "Dummy Address";
                    if (Type.equalsIgnoreCase("premise")) {
                        Address1 = long_name + " ";
                    } else if (Type.equalsIgnoreCase("street_number")) {
                        Address1 = Address1 + long_name;
                    } else if (Type.equalsIgnoreCase("route")) {
                        Address1 = Address1 + long_name;
                    } else if (Type.equalsIgnoreCase("sublocality")) {
                        Address2 = long_name;
                    } else if (Type.equalsIgnoreCase("locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        City = long_name;
                    } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                        County = long_name;
                    } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                        State = long_name;
                    } else if (Type.equalsIgnoreCase("country")) {
                        Country = long_name;
                    } else if (Type.equalsIgnoreCase("postal_code")) {
                        PIN = long_name;
                    }
                }

                // JSONArray mtypes = zero2.getJSONArray("types");
                // String Type = mtypes.getString(0);
                // Log.e(Type,long_name);
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String getAddress1() {
        return Address1;

    }

    public String getAddress2() {
        return Address2;

    }

    public String getCity() {
        return City;

    }

    public String getState() {
        return State;

    }

    public String getCountry() {
        return Country;

    }

    public String getCounty() {
        return County;

    }

    public String getPIN() {
        return PIN;

    }

}
