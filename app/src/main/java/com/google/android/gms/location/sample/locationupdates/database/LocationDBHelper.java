package com.google.android.gms.location.sample.locationupdates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.location.sample.locationupdates.Utils.LocationVo;

import java.util.ArrayList;

/**
 * Created by Grishma on 17/5/16.
 */
public class LocationDBHelper {

    //objects
    private Context mContext;
    private static LocationDBHelper helper;


    private LocationDBHelper(Context pContxt) {
        mContext = pContxt;
    }

    public synchronized static LocationDBHelper getInstance(Context pcontxt) {
        if (helper != null) return helper;

        return helper = new LocationDBHelper(pcontxt);
    }


    /**
     * Insert records of contacts into database.
     */
    public boolean insertLocationDetails(ArrayList<LocationVo> mContactsList) {
        if (mContactsList == null || mContactsList.size() < 0)
            return true;
        SQLiteDatabase m_provider = null;
        try {
            m_provider = SQLiteDBProvider.getInstance(mContext).openToWrite();
            m_provider.beginTransaction();

            for (final LocationVo mcontacts : mContactsList) {

                ContentValues m_conVal = new ContentValues();
                m_conVal.put(LocationMaster.mloc_latitude.name(), mcontacts.getmLatitude());
                m_conVal.put(LocationMaster.mloc_longitude.name(), mcontacts.getmLongitude());
                m_conVal.put(LocationMaster.mloc_address.name(), mcontacts.getmLocAddress());

                m_provider.replace(LocationMaster.getName(), null, m_conVal);
                System.err.println("Records Inserted.");
            }

            m_provider.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m_provider != null)
                m_provider.endTransaction();
        }

        return false;
    }

    /**
     * Get all the contact list from the database.
     *
     * @return-Arraylist of contacts
     */
    public ArrayList<LocationVo> getAllLocationLatLongDetails() {

        ArrayList<LocationVo> m_arryContVo = new ArrayList<LocationVo>();
        SQLiteDatabase m_provider = SQLiteDBProvider.getInstance(mContext).openToRead();

        Cursor m_contCursor = m_provider.query(
                  LocationMaster.getName(),
                  null,
                  null,
                  null,
                  null,
                  null,
                  null, null);

        if (m_contCursor.getCount() > 0) {
            m_contCursor.moveToFirst();
            do {
                LocationVo m_conVo = new LocationVo();
                m_conVo.setmLatitude(m_contCursor.getDouble(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_latitude.name())));
                m_conVo.setmLongitude(m_contCursor.getDouble(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_longitude.name())));
                m_conVo.setmLocId(m_contCursor.getInt(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_id.name())));

                m_conVo.setmLocAddress(m_contCursor.getString(m_contCursor
                          .getColumnIndex(LocationMaster.mloc_address.name())));

                m_arryContVo.add(m_conVo);

            } while (m_contCursor.moveToNext());
            m_contCursor.close();
        }

        return m_arryContVo;

    }

    public enum LocationMaster {
        mloc_id, mloc_latitude, mloc_longitude, mloc_address;

        public static String getName() {
            return "location_table";
        }
    }
}
