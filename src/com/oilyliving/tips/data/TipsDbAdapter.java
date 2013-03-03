package com.oilyliving.tips.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.oilyliving.tips.R;

import java.util.Date;
import java.util.List;

public class TipsDbAdapter
{     
    public static final String COL_ROWID = "_id";
    public static final String COL_TIP_ID = "TipID";
    public static final String COL_TIP_TEXT = "TipText";
    public static final String COL_REFERNCE = "RefUrl";
    public static final String COL_ICON_NAME = "IconName";
    public static final String COL_ICON_TAGS = "IconTags";
    public static final String COL_EOPR_PG = "EOPocketRefPg";
    public static final String COL_RGEO_PG = "RefGuideEoPg";
	public static final String COL_LAST_MOD_MSEC_EOPOCH= "LastModifiedMSecEpoch";
    private static final String TAG = "TipsDbAdapter";

    private static final String DATABASE_NAME = "OilyTips";
    private static final String DATABASE_TABLE = "tblTips";
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE +
	" (" +
	COL_ROWID + " integer primary key autoincrement, " +
	COL_TIP_ID + " integer not null, " +
	COL_TIP_TEXT + " text not null, " +
	COL_REFERNCE + " text null, " +
	COL_ICON_NAME + " text null, " +
	COL_ICON_TAGS + " text null, " +
	COL_EOPR_PG+ " int null, " +
	COL_RGEO_PG + " int null, " +
	COL_LAST_MOD_MSEC_EOPOCH + " long null " +
	" );";

    private final Context appContext;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

	public TipsDbAdapter(Context ctx)
	{
        appContext = ctx.getApplicationContext();
        DBHelper = new DatabaseHelper(appContext);
    }

	public void tryUpdateTips(List<Tip> tips)
	{

		for (Tip downloadedTip : tips)
		{
			insertOrUpdate(downloadedTip);
		}
	}

	public void insertOrUpdate(Tip tip)
	{
		Log.d(TAG, "Try update tip:" + tip);

		Tip tipFromDb = getTipById(tip.getTipId());
		if (tipFromDb == null)
		{
			Log.d(TAG, "Tip does not exist, inserting");
			insertTip(tip);
		}
		else if (tipFromDb.getLastModifiedDate().before(tip.getLastModifiedDate()))
		{
			Log.d(TAG, "Existing tip is older, updating");
			Log.d(TAG, "from db:" + tipFromDb);
			updateTip(tip);
			Tip confirm = getTipById(tip.getTipId());
			Log.d(TAG, "confirm updated:" + confirm);
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
//			//API level 11:db.enableWriteAheadLogging();
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

    public TipsDbAdapter open() throws SQLiteException
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
		db.beginTransaction();
        long count = db.delete(DATABASE_TABLE, "1", null);
        db.setTransactionSuccessful();
		db.endTransaction();

		Log.w(TAG, "Deleted " + count + "rows from database");
    }

    public long insertTip(Tip tip)
	{
        ContentValues values = new ContentValues();
		values.put(COL_TIP_ID, tip.getTipId());
        values.put(COL_TIP_TEXT, tip.getTipText());
        values.put(COL_ICON_NAME, tip.getIconName());
		values.put(COL_REFERNCE, tip.getWebReference());
		values.put(COL_LAST_MOD_MSEC_EOPOCH, tip.getLastModifiedDate().getTime());
        values.put(COL_EOPR_PG, tip.getEoprPage());
		values.put(COL_RGEO_PG, tip.getRgeoPage());
		
		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		long insertedRowId = db.insert(DATABASE_TABLE, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();

		return insertedRowId;
    }

    public long updateTip(Tip tip)
	{
        ContentValues values = new ContentValues();
        values.put(COL_TIP_TEXT, tip.getTipText());
        values.put(COL_ICON_NAME, tip.getIconName());
		values.put(COL_REFERNCE, tip.getWebReference());
		values.put(COL_LAST_MOD_MSEC_EOPOCH, tip.getLastModifiedDate().getTime());
        values.put(COL_EOPR_PG, tip.getEoprPage());
		values.put(COL_RGEO_PG, tip.getRgeoPage());
		
		//API level 11:db.beginTransactionNonExclusive();
        db.beginTransaction();
		int countUpdated= db.update(DATABASE_TABLE, values, COL_TIP_ID + "=?", new String[]{"" + tip.getTipId()});
		Log.d(TAG, "numRowsUpdated:" + countUpdated);
		db.setTransactionSuccessful();
		db.endTransaction();

		return countUpdated;
    }

	private Tip getTip(String whereClause, String orderBy)
	{

		String select = "SELECT " +
			COL_ROWID + ", " +
			COL_TIP_ID + ", " +
			COL_TIP_TEXT + "," +
			COL_ICON_NAME + "," +
			COL_REFERNCE + "," +
			COL_EOPR_PG+ "," +
			COL_RGEO_PG + "," +
			COL_LAST_MOD_MSEC_EOPOCH +
			" from "  + DATABASE_TABLE + " " +
			whereClause +  " " +
			orderBy +  " " +
			" LIMIT 1";
		Tip tip = null;
		Cursor cursor = db.rawQuery(select, null);
        //TODO: this could be cleaned up
        if (cursor.moveToFirst())
		{
//			int rowId = cursor.getInt(0);
			int tipId = cursor.getInt(1);
            String tipText = cursor.getString(2);// + " (Tip #" + rowId + ")";
            String iconName = cursor.getString(3);
			String ref = cursor.getString(4);
			int eopr= cursor.getInt(5);
			int rgeo = cursor.getInt(6);
			long lastModMsecEpoch = cursor.getLong(7);
			Date lastMod = new Date(lastModMsecEpoch);
            tip = new Tip(tipId, tipText, iconName, ref, lastMod);
			tip.setEoprPage(eopr);
			tip.setRgeoPage(rgeo);
		}
		cursor.close();
        return tip;
    }


    public int getCount()
	{
        Cursor cursor = db.rawQuery(
			"SELECT count(" + COL_ROWID + ") from " + DATABASE_TABLE, null);

        cursor.moveToFirst();
        int tipCount = cursor.getInt(0);
		cursor.close();
        Log.d(TAG, "getCount=" + tipCount);
        return tipCount;
    }

    public Tip getRandomTip()
	{
		String orderBy = " ORDER BY RANDOM() ";
		return getTip("", orderBy);
	}

    public Tip getTipById(int id)
	{
		String whereClause = " WHERE " + COL_TIP_ID + "=" + id;
		return getTip(whereClause, "");
	}

	public void InitTips(Context context)
	{
		TipsBootstrap.initTips(db, this, context);
	}


}
