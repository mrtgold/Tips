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

    private static final String DATABASE_NAME = "OilyTipsIcons";
    private static final String DATABASE_TABLE = "tblIcons";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
	"create table " + DATABASE_TABLE +
	" (" +
	COL_ROWID + " integer primary key autoincrement, " +
	COL_ICON_NAME + " text not null, " +
	COL_LOCAL_URI + " text null, " +
	COL_SERVER_URL + " text null, " +
	COL_BITMAP_BYTES + " blob null, " +
	COL_TAGS_STRING + " text null " +
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

//			Tip tipFromDb = getTipById(downloadedTip.getTipId());
//			if (tipFromDb == null)
//			{
//				Log.d(TAG, "Tip does not exist, inserting");
//				insertTip(downloadedTip);
//			}
//			else if (tipFromDb.getLastModifiedDate().before(downloadedTip.getLastModifiedDate()))
//			{
//				Log.d(TAG, "Existing tip is older, updating");
//				Log.d(TAG, "from db:"+tipFromDb);
//				updateTip(downloadedTip);
//			}
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
//      values.put(KEY_SERVER_URL, icon.getServerUrl().toString());
        values.put(COL_BITMAP_BYTES, icon.getIconAsBytes());
//      values.put(KEY_TAGS_STRING, icon.getTagsString());

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
        Log.d(TAG, "getCount=" + count);
        return count;
    }

    public Icon getBestIcon(String name, String tagsString)
	{
        Icon icon = getIconByName(name);
        return icon;
	}

    public Icon getIconByName(String name)
	{
        String whereClause = " WHERE " + COL_ICON_NAME + " ='" + name + "'";
		return getIcon(whereClause);
	}

    private Icon getIcon(String whereClause)
	{
		String sqlStatement = SELECT_ALL_COLUMNS + whereClause + " LIMIT 1";
		Cursor cursor = db.rawQuery(sqlStatement, null);
        if (cursor.moveToFirst())
		{
            return getIconFromCursor(cursor);
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

    private Icon getIconFromCursor(Cursor cursor)
	{
        String iconName = cursor.getString(1);
        byte[] iconBytes = cursor.getBlob(4);
        String tagsString = cursor.getString(5);

        return new Icon(iconName, iconBytes, tagsString);
    }

	public void InitIcons(Context context)
	{
		db.beginTransaction();
		deleteAll();

		Bitmap ylIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.yllogo1);
		Bitmap thievesIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves1);
		Bitmap lavenderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.lavender_field);
		Bitmap kidScentsIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.kidscents1);
		Bitmap frankincense = BitmapFactory.decodeResource(context.getResources(), R.drawable.frankincense);
		Bitmap thievesSpray = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves_spray);
		Bitmap peppermint = BitmapFactory.decodeResource(context.getResources(), R.drawable.peppermint);
		Bitmap rc = BitmapFactory.decodeResource(context.getResources(), R.drawable.rc);
		insertIcon(new Icon("peppermint", peppermint));
		insertIcon(new Icon("ylIcon", ylIcon));
		insertIcon(new Icon("rc", rc));
		insertIcon(new Icon("thievesIcon", thievesIcon));
		insertIcon(new Icon("frankincense", frankincense));
		insertIcon(new Icon("lavenderIcon", lavenderIcon));
		insertIcon(new Icon("kidScentsIcon", kidScentsIcon));
		insertIcon(new Icon("thievesSpray", thievesSpray));

		db.setTransactionSuccessful();
		db.endTransaction();
	}


}
