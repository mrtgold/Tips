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

public class TipsDbAdapter
{
	int id=0;
	public static final String KEY_ROWID="_id";
	public static final String KEY_TIP_TEXT="TipText";
	public static final String KEY_LOCAL_URI="LocalUri";
	public static final String KEY_SERVER_URL="ServerUrl";
	public static final String KEY_BITMAP_BYTES="BitmapBytes";
	private static final String TAG="TipsDbAdapter";

	private static final String DATABASE_NAME="Random";
	private static final String DATABASE_TABLE="tblTips";
	private static final int DATABASE_VERSION=2;

	private static final String DATABASE_CREATE = 
	"create table " + DATABASE_TABLE + 
	" (" + 
	KEY_ROWID + " integer primary key autoincrement, " +
	KEY_TIP_TEXT + " text not null, " +
	KEY_LOCAL_URI+ " text null, " +
	KEY_SERVER_URL + " text null, " +
	KEY_BITMAP_BYTES + " blob null " +
	" );";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public TipsDbAdapter(Context ctx)
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

	public TipsDbAdapter open() throws SQLiteException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		DBHelper.close();
	}

	public long insertTip(Tip tip)
	{
		ContentValues values=new ContentValues();
		values.put(KEY_TIP_TEXT, tip.getTipText());
		return db.insert(DATABASE_TABLE, null, values);
	}

	public int getTipCount()
	{
		Cursor cursor = db.rawQuery(
			"SELECT count(" + KEY_TIP_TEXT + ") from " + DATABASE_TABLE, null);
		//TODO: this could be cleaned up	
		if (cursor.moveToFirst())
		{
			return cursor.getInt(0);
		}
		return cursor.getInt(0);
	}


	//TODO: refactor this per comments
	public Tip getRandomTip()
	{
		//id = getAllEntries();
		int rand  =0;
		Random random = new Random();
		try
		{
			rand  = random.nextInt(getTipCount());
		}
		catch (Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		if (rand == 0)
			++rand;

		Cursor cursor = db.rawQuery(
			"SELECT " + 
			KEY_TIP_TEXT + "," +  
			KEY_LOCAL_URI+ "," +  
			KEY_SERVER_URL + "," +  
			KEY_BITMAP_BYTES +  
			" from " + DATABASE_TABLE + 
			" WHERE " + KEY_ROWID + " = " + rand, null);
		//TODO: this could be cleaned up	
		if (cursor.moveToFirst())
		{
			String tipText = cursor.getString(0) + " Get some today! (Tip #" + String.valueOf(rand) + ")";
			
			Tip tip = new Tip(tipText);
			return tip;
		}
		return null;
	}
}
