package com.oilyliving.tips.data;

import android.content.*;
import android.database.sqlite.*;
import com.oilyliving.tips.*;
import java.util.*;

public final class TipsBootstrap
{
	public static void initTips(SQLiteDatabase db, TipsDbAdapter tips, Context context)
	{
		db.beginTransaction();
		tips.deleteAll();
		Tip tip1001 = new Tip(1001, context.getString(R.string.tip1001), "rc", context.getString(R.string.ref1001), new Date(0));
		tips.insertTip(tip1001);

		Tip tip2001 = new Tip(2001, context.getString(R.string.tip2001), "purification");
		tip2001.setEoprPage(149);
		tip2001.setRgeoPage(187);
		tips.insertTip(tip2001);

		tips.insertTip(new Tip(10001, context.getString(R.string.tip10001), "rc"));
		tips.insertTip(new Tip(4001, context.getString(R.string.tip4001), "ylIcon"));
		tips.insertTip(new Tip(5001, context.getString(R.string.tip5001), "thievesIcon"));
		tips.insertTip(new Tip(6001, context.getString(R.string.tip6001), "frankincense"));
		tips.insertTip(new Tip(7001, context.getString(R.string.tip7001), "lavenderIcon"));
		tips.insertTip(new Tip(8001, context.getString(R.string.tip8001), "kidScentsIcon"));
		tips.insertTip(new Tip(9001, context.getString(R.string.tip9001), "thievesSpray"));
        tips.insertTip(new Tip(10001, context.getString(R.string.tip10001), "rc"));
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
}
