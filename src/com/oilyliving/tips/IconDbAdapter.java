package com.oilyliving.tips;

import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.sqlite.*;

public class IconDbAdapter
{
	public static final String KEY_ROWID="_id";
	public static final String KEY_ICON_NAME="IconName";
	public static final String KEY_LOCAL_URI="LocalUri";
	public static final String KEY_SERVER_URL="ServerUrl";
	public static final String KEY_BITMAP_BYTES="BitmapBytes";
	public static final String KEY_TAGS_STRING="TagsString";
	private static final String TAG="IconDbAdapter";

	private static final String DATABASE_NAME="Random";
	private static final String DATABASE_TABLE="tblIcons";
	private static final int DATABASE_VERSION=1;

	private static final String DATABASE_CREATE = 
	"create table " + DATABASE_TABLE + 
	" (" + 
	KEY_ROWID + " integer primary key autoincrement, " +
	KEY_ICON_NAME + " text not null, " +
	KEY_LOCAL_URI + " text null, " +
	KEY_SERVER_URL + " text null, " +
	KEY_BITMAP_BYTES + " blob null " +
	KEY_TAGS_STRING + " text null, " +
	" );";

	private static final String SELECT_ALL_COLUMNS = 
	"SELECT " + 
	KEY_ROWID + ", " +
	KEY_ICON_NAME + "," +  
	KEY_LOCAL_URI + "," +  
	KEY_SERVER_URL + "," +  
	KEY_BITMAP_BYTES + "," +  
	KEY_TAGS_STRING +  
	" from " + DATABASE_TABLE;

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public IconDbAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

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
		long count = db.delete(DATABASE_TABLE, "1", null);
		Log.w(TAG, "Deleted " + count + "rows from " + DATABASE_TABLE);
	}

	public long insertIcon(Icon icon)
	{
		ContentValues values=new ContentValues();
		values.put(KEY_ICON_NAME, icon.getName());
		values.put(KEY_LOCAL_URI, icon.getLocalUri().toString());
		values.put(KEY_SERVER_URL, icon.getServerUrl().toString());
		values.put(KEY_BITMAP_BYTES, icon.getIconAsBytes());
		values.put(KEY_TAGS_STRING, icon.getTagsString());
		return db.insert(DATABASE_TABLE, null, values);
	}

	public int getCount()
	{
		Cursor cursor = db.rawQuery(
			"SELECT count(" + KEY_ICON_NAME + ") from " + DATABASE_TABLE, null);

		cursor.moveToFirst();
		int count = cursor.getInt(0);
		Log.i(TAG, "getCount=" + count);
		return count;
	}

	public Icon getIcon(String name, String tagsString)
	{
		Icon icon = getIconByName(name);
		if (icon != null)
			return icon;

//		icon = getIconByTags(tagsString);
//		if (icon != null)
//			return icon;
//
		icon = getRandomIcon();
		return icon;
	}

	public Icon getIconByName(String name)
	{
		Cursor cursor = db.rawQuery(
			SELECT_ALL_COLUMNS + 
			" WHERE " + KEY_ICON_NAME + " ='" + name + "'"
			, null);

		if (cursor.moveToFirst())
		{
			Icon icon = getIconFromCursor(cursor);
			return icon;
		}
		return null;
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

	public Icon getRandomIcon()
	{
		Cursor cursor = db.rawQuery(
			SELECT_ALL_COLUMNS + 
			" ORDER BY RANDOM() LIMIT 1", null);

		if (cursor.moveToFirst())
		{
			Icon icon = getIconFromCursor(cursor);
			return icon;
		}
		return null;
	}

	private Icon getIconFromCursor(Cursor cursor)
	{
		String iconName = cursor.getString(1);
		byte[] iconBytes = cursor.getBlob(4);
		String tagsString = cursor.getString(5);

		Icon icon= new Icon(iconName, iconBytes, tagsString);
		return icon;
	}
}
