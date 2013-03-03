package com.oilyliving.tips.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.util.*;
import com.oilyliving.tips.*;
import java.util.*;

public class IconDbAdapter
{
    private static final String TAG = "IconDbAdapter";

    public static final String COL_ROWID = "_id";
    public static final String COL_ICON_NAME = "IconName";
    public static final String COL_LOCAL_URI = "LocalUri";
    public static final String COL_SERVER_URL = "ServerUrl";
    public static final String COL_BITMAP_BYTES = "BitmapBytes";
    public static final String COL_TAGS_STRING = "TagsString";
	public static final String COL_LAST_DL_ATTEMPT = "LastDownloadAttemp";


    private static final String DATABASE_NAME = "OilyTipsIcons";
    private static final String DATABASE_TABLE = "tblIcons";
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE +
	" (" +
	COL_ROWID + " integer primary key autoincrement, " +
	COL_ICON_NAME + " text not null, " +
	COL_LOCAL_URI + " text null, " +
	COL_SERVER_URL + " text null, " +
	COL_BITMAP_BYTES + " blob null, " +
	COL_TAGS_STRING + " text null, "  +
	COL_LAST_DL_ATTEMPT + " long null " +
	" );";

    private static final String SELECT_ALL_COLUMNS =
	"SELECT " +
	COL_ROWID + ", " +
	COL_ICON_NAME + "," +
	COL_LOCAL_URI + "," +
	COL_SERVER_URL + "," +
	COL_BITMAP_BYTES + "," +
	COL_TAGS_STRING + "," +
	COL_LAST_DL_ATTEMPT +
	" from " + DATABASE_TABLE;

    private final Context appContext;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public IconDbAdapter(Context ctx)
	{
        appContext = ctx.getApplicationContext();
        DBHelper = new DatabaseHelper(appContext);
    }

	public void tryUpdateIcons(List<Icon> icons)
	{
		for (Icon downloadedIcon:icons)
		{
			insertOrUpdate(downloadedIcon);
		}

	}

	public void insertOrUpdate(Icon icon)
	{
		Log.d(TAG, "insertOrUpdate icon:" + icon);

		Icon iconFromDb = getIconByName(icon.getName());
		if (iconFromDb == null)
		{
			Log.d(TAG, icon.getName() + " does not exist, inserting");
			insertIcon(icon);
		}
		else
			Log.d(TAG, icon.getName() + " exists, skipping");

	}

	public long updateLastDownloadAttempt(Icon icon, long lastDownloadAttempt)
	{
        ContentValues values = new ContentValues();
		values.put(COL_LAST_DL_ATTEMPT, lastDownloadAttempt);

		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		int countUpdated= db.update(DATABASE_TABLE, values, COL_ICON_NAME + "=?", new String[]{icon.getName()});
		Log.d(TAG, "numRowsUpdated:" + countUpdated);
		db.setTransactionSuccessful();
		db.endTransaction();

		return countUpdated;
    }

	public long updateBitmapBytes(Icon icon)
	{
        ContentValues values = new ContentValues();
		byte[] iconBytes = icon.getIconAsBytes();
		if (iconBytes != null)
			values.put(COL_BITMAP_BYTES, iconBytes);

		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		int countUpdated= db.update(DATABASE_TABLE, values, COL_ICON_NAME + "=?", new String[]{icon.getName()});
		Log.d(TAG, "numRowsUpdated:" + countUpdated);
		db.setTransactionSuccessful();
		db.endTransaction();

		return countUpdated;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
	{
        DatabaseHelper(Context context)
		{
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

//		@Override
//		public void onConfigure(SQLiteDatabase db)
//		{
//           //API level 11: db.enableWriteAheadLogging();
//        }

        @Override
        public void onCreate(SQLiteDatabase db)
		{
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
            Log.w(TAG, "Upgrading database from version " + oldVersion +
				  " to " + newVersion + "; All data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }

    }

    public IconDbAdapter open() throws SQLiteException
	{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
	{
        DBHelper.close();
    }

    public void deleteAll()
	{
		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		long count = db.delete(DATABASE_TABLE, "1", null);
		db.setTransactionSuccessful();
		db.endTransaction();
        Log.w(TAG, "Deleted " + count + "rows from " + DATABASE_TABLE);
    }

    public long insertIcon(Icon icon)
	{
        ContentValues values = new ContentValues();
        values.put(COL_ICON_NAME, icon.getName());
		values.put(COL_SERVER_URL, icon.getServerUrl());

		byte[] iconBytes = icon.getIconAsBytes();
		if (iconBytes != null)
			values.put(COL_BITMAP_BYTES, iconBytes);

		values.put(COL_TAGS_STRING, icon.getTagsString());
		values.put(COL_LAST_DL_ATTEMPT, 0);
		
		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		long insertedRowId = db.insert(DATABASE_TABLE, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();

		return insertedRowId;
    }

    public int getCount(String whereClause)
	{
		if (whereClause == null)
			whereClause = "";

        Cursor cursor = db.rawQuery(
			"SELECT count(" + COL_ICON_NAME + ") from " + DATABASE_TABLE + " " + whereClause, null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);
		cursor.close();

        Log.d(TAG, "getCount=" + count);
        return count;
    }

    public Icon getBestIcon(String name, String tagsString)
	{
        return getIconByName(name);
	}

    public Icon getIconByName(String name)
	{
        String whereClause = " WHERE " + COL_ICON_NAME + " ='" + name + "'";
		return getIcon(whereClause, "");
	}

    public Icon getIconToDownload(long usecDownloadStartTime)
	{
        String whereClause = " WHERE " + 
			COL_LAST_DL_ATTEMPT + " < " + usecDownloadStartTime + 
			" AND " + COL_BITMAP_BYTES + " IS NULL " + 
			" AND " + COL_SERVER_URL + " IS NOT NULL" +
			" AND length(" + COL_SERVER_URL + ") > 10 "
			;
		int numToDownload = getCount(whereClause);
		Log.d(TAG, "need to download:" + numToDownload);

		String orderBy = " ORDER BY RANDOM() ";
		Icon icon = getIcon(whereClause, orderBy);

		if (icon != null)
			Log.d(TAG, "going to download:" + icon.getName() + " from " + icon.getServerUrl());
		else
			Log.d(TAG, "no more missing icons left");
		return icon;
	}

    private Icon getIcon(String whereClause, String orderByClause)
	{
		if (whereClause == null) whereClause = "";
		if (orderByClause == null) orderByClause = "";

		Icon iconFromCursor =null;
		String sqlStatement = SELECT_ALL_COLUMNS + whereClause + orderByClause + " LIMIT 1";
		Cursor cursor = db.rawQuery(sqlStatement, null);
        if (cursor.moveToFirst())
		{
            iconFromCursor = getIconFromCursor(cursor);
        }
		cursor.close();
        return iconFromCursor;
    }

//	public Icon getIconByTags(String[] tags)
//	{
//		Cursor cursor = db.rawQuery(
//			SELECT_ALL_COLUMNS + 
//			" WHERE " + KEY_ICON_NAME + " ='" + name + "'"
//			, null);
//
//		if (cursor.moveToFirst())
//		{
//			Icon icon = getIconFromCursor(cursor);
//			return icon;
//		}
//		return null;
//	}

    private Icon getIconFromCursor(Cursor cursor)
	{
        String iconName = cursor.getString(1);
        byte[] iconBytes = cursor.getBlob(4);
        String tagsString = cursor.getString(5);
		String url  = cursor.getString(3);

        Icon icon = new Icon(iconName, iconBytes, tagsString);
		icon.setServerUrl(url);
		return icon;
    }

	public void InitIcons(Context context)
	{
		Bootstrap.initIcons(context, db, this);
	}

	public void debugPrintAllIcons()
	{
		Cursor cursor = db.rawQuery(SELECT_ALL_COLUMNS, null);
        if (cursor.moveToFirst())
		{
			Log.d(TAG, "debugPrintAllIcons");
			do{
				Log.d(TAG, "rowNum:" + cursor.getPosition());
				Log.d(TAG, "iconName:" + cursor.getString(1));
				Log.d(TAG, "localUri:" + cursor.getString(2));
				Log.d(TAG, "url:" + cursor.getString(3));
				byte[] blob = cursor.getBlob(4);
				if (blob == null)
					Log.d(TAG, "blobSize:<null>");
				else
					Log.d(TAG, "blobSize:" + blob.length);
				Log.d(TAG, "tags:" + cursor.getString(5));
				Log.d(TAG, "lastUpdate:" + cursor.getLong(6));
			}while(cursor.moveToNext());
		}
		cursor.close();

	}
}
