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
	public static final String COL_DL_ATTEMPTS = "DownloadAttemps";
	

    private static final String DATABASE_NAME = "OilyTipsIcons";
    private static final String DATABASE_TABLE = "tblIcons";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE +
	" (" +
	COL_ROWID + " integer primary key autoincrement, " +
	COL_ICON_NAME + " text not null, " +
	COL_LOCAL_URI + " text null, " +
	COL_SERVER_URL + " text null, " +
	COL_BITMAP_BYTES + " blob null, " +
	COL_TAGS_STRING + " text null, "  +
	COL_DL_ATTEMPTS + " int null " +
	" );";

    private static final String SELECT_ALL_COLUMNS =
	"SELECT " +
	COL_ROWID + ", " +
	COL_ICON_NAME + "," +
	COL_LOCAL_URI + "," +
	COL_SERVER_URL + "," +
	COL_BITMAP_BYTES + "," +
	COL_TAGS_STRING +
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
			Log.d(TAG, "Try update icon:" + downloadedIcon);

			Icon iconFromDb = getIconByName(downloadedIcon.getName());
			if (iconFromDb == null)
			{
				Log.d(TAG, "Icon does not exist, inserting");
				insertIcon(downloadedIcon);
			}
		}

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

		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		long insertedRowId = db.insert(DATABASE_TABLE, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();

		return insertedRowId;
    }

    public int getCount()
	{
        Cursor cursor = db.rawQuery(
			"SELECT count(" + COL_ICON_NAME + ") from " + DATABASE_TABLE, null);

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
		return getIcon(whereClause);
	}

    private Icon getIcon(String whereClause)
	{
		Icon iconFromCursor =null;
		String sqlStatement = SELECT_ALL_COLUMNS + whereClause + " LIMIT 1";
		Cursor cursor = db.rawQuery(sqlStatement, null);
        if (cursor.moveToFirst())
		{
            iconFromCursor= getIconFromCursor(cursor);
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
		IconsBootstrap.initIcons(context,db,this);
	}



}
