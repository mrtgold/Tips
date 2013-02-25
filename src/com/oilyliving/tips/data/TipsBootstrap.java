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
		Tip tip1001 = new Tip(1001, context.getString(R.string.rcMrsa), "rc", context.getString(R.string.rcMrsaRef), new Date(0));
		tips.insertTip(tip1001);

		Tip tip2001 = new Tip(2001, context.getString(R.string.tip1), "purification");
		tip2001.setEoprPage(149);
		tip2001.setRgeoPage(187);
		tips.insertTip(tip2001);

		tips.insertTip(new Tip(3, context.getString(R.string.rcResp), "rc"));
		tips.insertTip(new Tip(5, context.getString(R.string.tip5), "ylIcon"));
		tips.insertTip(new Tip(6, context.getString(R.string.tip6), "thievesIcon"));
		tips.insertTip(new Tip(7, context.getString(R.string.tip7), "frankincense"));
		tips.insertTip(new Tip(8, context.getString(R.string.tip8), "lavenderIcon"));
		tips.insertTip(new Tip(9, context.getString(R.string.tip9), "kidScentsIcon"));
		tips.insertTip(new Tip(10, context.getString(R.string.tip10), "thievesSpray"));
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
}
