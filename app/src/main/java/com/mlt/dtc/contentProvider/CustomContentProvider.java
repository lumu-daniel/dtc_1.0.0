package com.mlt.dtc.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mlt.dtc.Db.DeviceDao;
import com.mlt.dtc.Db.DeviceMngtDB;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CustomContentProvider extends ContentProvider {

    private DeviceMngtDB db;
    private DeviceDao deviceDao;
    private static final String DBNAME = "DeviceMngtDb";
    public static final String AUTHORITY = "com.mlt.dtc.contentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_DETAILS = "device_details";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //database.execSQL();
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    @Override
    public boolean onCreate() {
        db = Room.databaseBuilder(getContext(), DeviceMngtDB.class,DBNAME)
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                .build();

        deviceDao = db.deviceDao();
        return true;
    }

    static {

        uriMatcher.addURI(AUTHORITY, PATH_DETAILS,1);

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = uriMatcher.match(uri);
        if (code == 1){

            final Context context = getContext();
            if (context == null) {
                return null;
            }

            final Cursor cursor = deviceDao.getAll();

            cursor.setNotificationUri(context.getContentResolver(), uri);

            return cursor;

        }else {
            Toast.makeText(getContext(), "UnKnown URI", Toast.LENGTH_SHORT).show();

            return null;
        }

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int code = uriMatcher.match(uri);
        if (code == 1){

            final Context context = getContext();
            if (context == null) {
                return -1;
            }

            if (values!=null) {

                Set<Map.Entry<String, Object>> s = values.valueSet();

                for (Map.Entry<String, Object> stringObjectEntry : s) {
                    Map.Entry me = (Map.Entry) stringObjectEntry;
                    String key = me.getKey().toString();
                    String value = (String) me.getValue();
                    switch (key) {
                        case "Adv":
                            return deviceDao.upadateAdv(value);
                        case "apk":
                            return deviceDao.upadateApkV(value);
                        case "mainVer":
                            return deviceDao.upadateMainV(value);
                        case "topVer":
                            return deviceDao.upadateTop(value);
                        case "mTopVer":
                            return deviceDao.upadateMainTop(value);
                        case "mSBVer":
                            return deviceDao.upadateSBMain(value);
                        default:
                            return -1;
                    }


                    //Log.d("DatabaseSync", "Key:" + key + ", values:" + (String) (value == null ? null : value.toString()));
                }



            }
            else {
                return -1;
            }

        }
        else {
            Toast.makeText(getContext(), "UnKnown URI", Toast.LENGTH_SHORT).show();

            return -1;
        }

        return -1;
    }

    public static class TaskEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAILS).build();
        public static final String TABLE_NAME = "device_details";
    }
}
