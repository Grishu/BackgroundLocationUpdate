package com.google.android.gms.location.sample.locationupdates.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.location.sample.locationupdates.Utils.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SQLiteDBProvider {
    /**
     * Context of the application using the database.
     */
    private Context m_context;

    private static final String SELECT_STATEMENT = "SELECT * from ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    /**
     * Holds the database instance.
     */
    static SQLiteDatabase m_database = null;
    /**
     * Database open/upgrade helper.
     */
    DBHelperClass m_dbhelper = null;

    private static SQLiteDBProvider m_DBinstance;

    public static SQLiteDBProvider getInstance(Context context) {
        if (m_DBinstance == null) {
            System.err.println("DatabaseSingleton Instance");
            try {
                m_DBinstance = new SQLiteDBProvider(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return m_DBinstance;
    }

    public SQLiteDBProvider(Context p_context) throws IOException {
        super();
        this.m_context = p_context;

        m_dbhelper = new DBHelperClass(p_context, Const.DATABASE_NAME, null,
                  Const.DATABASE_VERSION);
        System.err.println("Database Created.");
    }


    public synchronized SQLiteDatabase openToRead() throws SQLiteException {
        if (this.m_database != null) {
            try {
                return this.m_database = this.m_dbhelper.getReadableDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return m_database;
    }

    /**
     * Open database for read and write operations
     *
     * @return true/false
     */
    public synchronized SQLiteDatabase openToWrite() throws SQLiteException {
        if (m_database != null) {
            try {
                return m_database = this.m_dbhelper.getWritableDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return m_database;
    }

    public synchronized void close() {
        if (m_database != null) {
            m_database.close();
        }
        m_database = null;
    }

    public class DBHelperClass extends SQLiteOpenHelper {
        public DBHelperClass(Context context, String name,
                             SQLiteDatabase.CursorFactory factory, int version) throws IOException {
            super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
            boolean dbexist = checkdatabase();
            if (dbexist) {
                System.out.println("Database exists");
                opendatabase();
            } else {
                System.out.println("Database doesn't exist");
                createdatabase();
                opendatabase();
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("SqliteOpenHelper", "onUpgrade " + oldVersion + "=>" + newVersion);

            db.execSQL(DROP_TABLE + LocationDBHelper.LocationMaster.getName() + ";");

            onCreate(db);
        }

        public void createdatabase() throws IOException {
            boolean dbexist = checkdatabase();
            if (dbexist) {
                System.out.println(" Database exists.");
            } else {
                this.getReadableDatabase();
                try {
                    copydatabase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        }

        private boolean checkdatabase() {
            boolean checkdb = false;
            try {
                String myPath = Const.DATABASE_PATH + Const.DATABASE_NAME;
                File dbfile = new File(myPath);
                checkdb = dbfile.exists();
            } catch (SQLiteException e) {
                System.out.println("Database doesn't exist");
            }
            return checkdb;
        }

        private void copydatabase() throws IOException {
            //Open your local db as the input stream
            InputStream myinput = m_context.getAssets().open(Const.DATABASE_NAME);
            // Path to the just created empty db
            String outfilename = Const.DATABASE_PATH + Const.DATABASE_NAME;
            //Open the empty db as the output stream
            OutputStream myoutput = new FileOutputStream(Const.DATABASE_PATH + Const.DATABASE_NAME);
            // transfer byte to inputfile to outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myinput.read(buffer)) > 0) {
                myoutput.write(buffer, 0, length);
            }
            //Close the streams
            myoutput.flush();
            myoutput.close();
            myinput.close();
        }

        public void opendatabase() throws SQLException {
            //Open the database
            m_database = SQLiteDatabase.openDatabase(Const.DATABASE_PATH + Const.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }
}
