package com.oilyliving.tips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipsDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIP_TEXT = "TipText";
    public static final String KEY_LOCAL_URI = "LocalUri";
    public static final String KEY_SERVER_URL = "ServerUrl";
    public static final String KEY_BITMAP_BYTES = "BitmapBytes";
    private static final String TAG = "TipsDbAdapter";

    private static final String DATABASE_NAME = "Random";
    private static final String DATABASE_TABLE = "tblTips";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE +
                    " (" +
                    KEY_ROWID + " integer primary key autoincrement, " +
                    KEY_TIP_TEXT + " text not null, " +
                    KEY_LOCAL_URI + " text null, " +
                    KEY_SERVER_URL + " text null, " +
                    KEY_BITMAP_BYTES + " blob null " +
                    " );";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public TipsDbAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion +
                    " to " + newVersion + "; All data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }

    }

    public TipsDbAdapter open() throws SQLiteException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public void deleteAll() {
        long count = db.delete(DATABASE_TABLE, "1", null);
        Log.w(TAG, "Deleted " + count + "rows from database");
    }

    public long insertTip(Tip tip) {
        ContentValues values = new ContentValues();
        values.put(KEY_TIP_TEXT, tip.getTipText());
        values.put(KEY_BITMAP_BYTES, tip.getIconAsBytes());
        return db.insert(DATABASE_TABLE, null, values);
    }

    public int getCount() {
        Cursor cursor = db.rawQuery(
                "SELECT count(" + KEY_TIP_TEXT + ") from " + DATABASE_TABLE, null);

        cursor.moveToFirst();
        int tipCount = cursor.getInt(0);
        Log.i(TAG, "getCount=" + tipCount);
        return tipCount;
    }

    public Tip getRandomTip() {

        Cursor cursor = db.rawQuery(
                "SELECT " +
                        KEY_ROWID + ", " +
                        KEY_TIP_TEXT + "," +
                        KEY_LOCAL_URI + "," +
                        KEY_SERVER_URL + "," +
                        KEY_BITMAP_BYTES +
                        " from " + DATABASE_TABLE +
                        " ORDER BY RANDOM() LIMIT 1", null);
        //TODO: this could be cleaned up
        if (cursor.moveToFirst()) {
            String tipText = cursor.getString(1) + " (Tip #" + cursor.getInt(0) + ")";
            byte[] iconBytes = cursor.getBlob(4);

            return new Tip(tipText, iconBytes);
        }
        return null;
    }
}
