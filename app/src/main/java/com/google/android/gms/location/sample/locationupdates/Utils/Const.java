package com.google.android.gms.location.sample.locationupdates.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by Grishma on 17/5/16.
 */
public class Const {

    public static final String DATABASE_NAME = "Location_master.sqlite"; //Change it
    public static final String DATABASE_PATH = "/data/data/com.google.android.gms.location.sample.locationupdates/databases/"; //Change it
    public static final int DATABASE_VERSION = 1;

    /**
     * Method to get address from latitude and longitude.
     *
     * @param m_context
     * @param LATITUDE
     * @param LONGITUDE
     * @return
     */
    public static String getCompleteAddressString(Context m_context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(m_context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.v("My Current location add", "" + strAdd.toString());
            }
//            else {
//                Log.v("My Current location address", "No Address returned!");
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(m_context, "Sorry, Your location cannot be retrieved !" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.v("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }

    public static void ExportDatabase(Context mcont) {
        File f = new File(DATABASE_PATH + DATABASE_NAME);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(f);
            fos = new FileOutputStream("/mnt/sdcard/" + DATABASE_NAME);
            while (true) {
                int i = fis.read();
                if (i != -1) {
                    fos.write(i);
                } else {
                    break;
                }
            }
            fos.flush();
            Toast.makeText(mcont, "Database Export Successfully", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mcont, "DB dump ERROR", Toast.LENGTH_LONG).show();
        }
    }
}
