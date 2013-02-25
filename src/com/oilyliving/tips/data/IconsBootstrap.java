package com.oilyliving.tips.data;

import android.content.*;
import android.database.sqlite.*;
import android.graphics.*;
import com.oilyliving.tips.*;

public final class IconsBootstrap
{
	public static void initIcons(Context context, SQLiteDatabase db, IconDbAdapter icons)
	{
		db.beginTransaction();
		icons.deleteAll();

		Bitmap ylIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.yllogo1);
		Bitmap thievesIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves1);
		Bitmap lavenderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.lavender_field);
		Bitmap kidScentsIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.kidscents1);
		Bitmap frankincense = BitmapFactory.decodeResource(context.getResources(), R.drawable.frankincense);
		Bitmap thievesSpray = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves_spray);
		Bitmap peppermint = BitmapFactory.decodeResource(context.getResources(), R.drawable.peppermint);
		Bitmap rc = BitmapFactory.decodeResource(context.getResources(), R.drawable.rc);
		Bitmap purification = BitmapFactory.decodeResource(context.getResources(), R.drawable.purification);
		icons.insertIcon(new Icon("peppermint", peppermint));
		icons.insertIcon(new Icon("ylIcon", ylIcon));
		icons.insertIcon(new Icon("rc", rc));
		icons.insertIcon(new Icon("thievesIcon", thievesIcon));
		icons.insertIcon(new Icon("frankincense", frankincense));
		icons.insertIcon(new Icon("lavenderIcon", lavenderIcon));
		icons.insertIcon(new Icon("kidScentsIcon", kidScentsIcon));
		icons.insertIcon(new Icon("thievesSpray", thievesSpray));
		icons.insertIcon(new Icon("purification", purification));

		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
}
