package com.oilyliving.tips.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.util.*;
import com.oilyliving.tips.*;

public class TipsDbAdapter
{     
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIP_TEXT = "TipText";
    public static final String KEY_ICON_NAME = "IconName";
    public static final String KEY_ICON_TAGS = "IconTags";
    private static final String TAG = "TipsDbAdapter";

    private static final String DATABASE_NAME = "OilyTips";
    private static final String DATABASE_TABLE = "tblTips";
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE +
	" (" +
	KEY_ROWID + " integer primary key autoincrement, " +
	KEY_TIP_TEXT + " text not null, " +
	KEY_ICON_NAME + " text null, " +
	KEY_ICON_TAGS + " text null " +
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

    public void deleteAll()
	{
        long count = db.delete(DATABASE_TABLE, "1", null);
        Log.w(TAG, "Deleted " + count + "rows from database");
    }

    public long insertTip(Tip tip)
	{
        ContentValues values = new ContentValues();
        values.put(KEY_TIP_TEXT, tip.getTipText());
        values.put(KEY_ICON_NAME, tip.getIconName());
        return db.insert(DATABASE_TABLE, null, values);
    }

    public int getCount()
	{
        Cursor cursor = db.rawQuery(
			"SELECT count(" + KEY_TIP_TEXT + ") from " + DATABASE_TABLE, null);

        cursor.moveToFirst();
        int tipCount = cursor.getInt(0);
        Log.i(TAG, "getCount=" + tipCount);
        return tipCount;
    }

    public Tip getRandomTip()
	{

        Cursor cursor = db.rawQuery(
			"SELECT " +
			KEY_ROWID + ", " +
			KEY_TIP_TEXT + "," +
			KEY_ICON_NAME +
			" from " + DATABASE_TABLE +
			" ORDER BY RANDOM() LIMIT 1", null);
        //TODO: this could be cleaned up
        if (cursor.moveToFirst())
		{
            String tipText = cursor.getString(1) + " (Tip #" + cursor.getInt(0) + ")";
            String iconName = cursor.getString(2);

            return new Tip(tipText, iconName);
        }
        return null;
    }

	public void InitTips(Context context)
	{
		this.deleteAll();
		this.insertTip(new Tip(context.getString(R.string.tip1), "peppermint"));
		this.insertTip(new Tip(context.getString(R.string.tip2), "ylIcon"));
		this.insertTip(new Tip(context.getString(R.string.tip3), "rc"));
		this.insertTip(new Tip(context.getString(R.string.tip4), "rc"));
		this.insertTip(new Tip(context.getString(R.string.tip5), "ylIcon"));
		this.insertTip(new Tip(context.getString(R.string.tip6), "thievesIcon"));
		this.insertTip(new Tip(context.getString(R.string.tip7), "frankincense"));
		this.insertTip(new Tip(context.getString(R.string.tip8), "lavenderIcon"));
		this.insertTip(new Tip(context.getString(R.string.tip9), "kidScentsIcon"));
		this.insertTip(new Tip(context.getString(R.string.tip10), "thievesSpray"));
	}

}
