package com.oilyliving.tips.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.util.*;
import com.oilyliving.tips.*;
import java.util.*;

public class TipsDbAdapter
{     
    public static final String COL_ROWID = "_id";
    public static final String COL_TIP_ID = "TipID";
    public static final String COL_TIP_TEXT = "TipText";
    public static final String COL_REFERNCE = "RefUrl";
    public static final String COL_ICON_NAME = "IconName";
    public static final String COL_ICON_TAGS = "IconTags";
	public static final String COL_LAST_MOD_MSEC_EOPOCH= "LastModifiedMSecEpoch";
    private static final String TAG = "TipsDbAdapter";

    private static final String DATABASE_NAME = "OilyTips";
    private static final String DATABASE_TABLE = "tblTips";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE +
	" (" +
	COL_ROWID + " integer primary key autoincrement, " +
	COL_TIP_ID + " integer not null, " +
	COL_TIP_TEXT + " text not null, " +
	COL_REFERNCE + " text null, " +
	COL_ICON_NAME + " text null, " +
	COL_ICON_TAGS + " text null, " +
	COL_LAST_MOD_MSEC_EOPOCH + " long null " +
	" );";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

	public TipsDbAdapter(Context ctx)
	{
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

	public void tryUpdateTips(List<Tip> tips)
	{
		
		for (Tip downloadedTip : tips)
		{
			Log.d(TAG, "Try update tip:" + downloadedTip);

			Tip tipFromDb = getTipById(downloadedTip.getTipId());
			if (tipFromDb == null)
			{
				Log.d(TAG, "Tip does not exist, inserting");
				insertTip(downloadedTip);
			}
			else if (tipFromDb.getLastModifiedDate().before(downloadedTip.getLastModifiedDate()))
			{
				Log.d(TAG, "Existing tip is older, updating");
				Log.d(TAG, "from db:"+tipFromDb);
				updateTip(downloadedTip);
			}
		}
	}

    private static class DatabaseHelper extends SQLiteOpenHelper
	{
        DatabaseHelper(Context context)
		{
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onConfigure(SQLiteDatabase db)
		{
            db.enableWriteAheadLogging();
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

    public void deleteAll()
	{
		db.beginTransactionNonExclusive();
        long count = db.delete(DATABASE_TABLE, "1", null);
        db.endTransaction();
		
		Log.w(TAG, "Deleted " + count + "rows from database");
    }

    public long insertTip(Tip tip)
	{
        ContentValues values = new ContentValues();
		values.put(COL_TIP_ID, tip.getTipId());
        values.put(COL_TIP_TEXT, tip.getTipText());
        values.put(COL_ICON_NAME, tip.getIconName());
		values.put(COL_REFERNCE, tip.getReferenceUrl());
		values.put(COL_LAST_MOD_MSEC_EOPOCH, tip.getLastModifiedDate().getTime());
        
		db.beginTransactionNonExclusive();
		long insertedRowId = db.insert(DATABASE_TABLE, null, values);
		db.endTransaction();
		
		return insertedRowId;
    }

    public long updateTip(Tip tip)
	{
        ContentValues values = new ContentValues();
        values.put(COL_TIP_TEXT, tip.getTipText());
        values.put(COL_ICON_NAME, tip.getIconName());
		values.put(COL_REFERNCE, tip.getReferenceUrl());
		values.put(COL_LAST_MOD_MSEC_EOPOCH, tip.getLastModifiedDate().getTime());
        
		db.beginTransactionNonExclusive();
		int countUpdated= db.update(DATABASE_TABLE, values, COL_ROWID + "=?", new String[]{"" + tip.getTipId()});
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
			COL_LAST_MOD_MSEC_EOPOCH +
			" from "  + DATABASE_TABLE + " " +
			whereClause +  " " +
			orderBy +  " " +
			" LIMIT 1";

		Cursor cursor = db.rawQuery(select, null);
        //TODO: this could be cleaned up
        if (cursor.moveToFirst())
		{
//			int rowId = cursor.getInt(0);
			int tipId = cursor.getInt(1);
            String tipText = cursor.getString(2);// + " (Tip #" + rowId + ")";
            String iconName = cursor.getString(3);
			String ref = cursor.getString(4);
			long lastModMsecEpoch = cursor.getLong(5);
			Date lastMod = new Date(lastModMsecEpoch);
            return new Tip(tipId, tipText, iconName, ref, lastMod);
        }
        return null;
    }


    public int getCount()
	{
        Cursor cursor = db.rawQuery(
			"SELECT count(" + COL_ROWID + ") from " + DATABASE_TABLE, null);

        cursor.moveToFirst();
        int tipCount = cursor.getInt(0);
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
		this.deleteAll();
		this.insertTip(new Tip(2001, context.getString(R.string.tip1), "ylIcon"));
		this.insertTip(new Tip(1, context.getString(R.string.tip2), "peppermint"));
		this.insertTip(new Tip(3, context.getString(R.string.tip3), "rc"));
		this.insertTip(new Tip(1001, context.getString(R.string.tip4), "rc"));
		this.insertTip(new Tip(5, context.getString(R.string.tip5), "ylIcon"));
		this.insertTip(new Tip(6, context.getString(R.string.tip6), "thievesIcon"));
		this.insertTip(new Tip(7, context.getString(R.string.tip7), "frankincense"));
		this.insertTip(new Tip(8, context.getString(R.string.tip8), "lavenderIcon"));
		this.insertTip(new Tip(9, context.getString(R.string.tip9), "kidScentsIcon"));
		this.insertTip(new Tip(10, context.getString(R.string.tip10), "thievesSpray"));
	}

}
