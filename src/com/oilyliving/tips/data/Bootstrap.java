package com.oilyliving.tips.data;

import android.content.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.util.*;
import com.oilyliving.tips.*;
import java.util.*;


public final class Bootstrap
{
	private static final String TAG = "Bootstrap";

	public static void initTips(SQLiteDatabase db, TipsDbAdapter tips, Context context)
	{
//		Context appContext = context.getApplicationContext();
//		String androidId = Secure.getString(appContext.getContentResolver(), Secure.ANDROID_ID);
//		Log.d(TAG,"androidId =" + androidId);

		Log.d(TAG, "starting InitTips");

		db.beginTransaction();
//		tips.deleteAll();
		Tip tip1001 = new Tip(1001, context.getString(R.string.tip1001), "rc", context.getString(R.string.ref1001), new Date(0));
		tips.insertOrUpdate(tip1001);

		Tip tip2001 = new Tip(2001, context.getString(R.string.tip2001), "purification");
		tip2001.setEoprPage(149);
		tip2001.setRgeoPage(187);
		tips.insertOrUpdate(tip2001);

		tips.insertOrUpdate(new Tip(10001, context.getString(R.string.tip10001), "rc"));
		tips.insertOrUpdate(new Tip(4001, context.getString(R.string.tip4001), "ylIcon"));
		tips.insertOrUpdate(new Tip(5001, context.getString(R.string.tip5001), "thievesIcon"));
		tips.insertOrUpdate(new Tip(6001, context.getString(R.string.tip6001), "frankincense"));
		tips.insertOrUpdate(new Tip(7001, context.getString(R.string.tip7001), "lavenderIcon"));
		tips.insertOrUpdate(new Tip(8001, context.getString(R.string.tip8001), "kidScentsIcon"));
		tips.insertOrUpdate(new Tip(9001, context.getString(R.string.tip9001), "thievesSpray"));
        tips.insertOrUpdate(new Tip(10001, context.getString(R.string.tip10001), "rc"));
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public static void initIcons(Context context, SQLiteDatabase db, IconDbAdapter icons)
	{
		Log.d(TAG, "starting InitIcons");

		db.beginTransaction();
//		icons.deleteAll();

		Bitmap ylIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.yllogo1);
		Bitmap thievesIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves1);
		Bitmap lavenderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.lavender_field);
		Bitmap kidScentsIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.kidscents1);
		Bitmap frankincense = BitmapFactory.decodeResource(context.getResources(), R.drawable.frankincense);
		Bitmap thievesSpray = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves_spray);
		Bitmap peppermint = BitmapFactory.decodeResource(context.getResources(), R.drawable.peppermint);
		Bitmap rc = BitmapFactory.decodeResource(context.getResources(), R.drawable.rc);
		Bitmap purification = BitmapFactory.decodeResource(context.getResources(), R.drawable.purification);
		icons.insertOrUpdate(new Icon("peppermint", peppermint));
		icons.insertOrUpdate(new Icon("ylIcon", ylIcon));
		icons.insertOrUpdate(new Icon("rc", rc));
		icons.insertOrUpdate(new Icon("thievesIcon", thievesIcon));
		icons.insertOrUpdate(new Icon("frankincense", frankincense));
		icons.insertOrUpdate(new Icon("lavenderIcon", lavenderIcon));
		icons.insertOrUpdate(new Icon("kidScentsIcon", kidScentsIcon));
		icons.insertOrUpdate(new Icon("thievesSpray", thievesSpray));
		icons.insertOrUpdate(new Icon("purification", purification));

		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
}
