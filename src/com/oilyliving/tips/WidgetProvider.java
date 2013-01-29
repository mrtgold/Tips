package com.oilyliving.tips;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.net.*;
import android.graphics.*;

public class WidgetProvider extends AppWidgetProvider
{
	private static final String TAG="TipsWidget";

//	@Override
//	public void onEnabled(Context context)
//	{
//		InitDb(context);
//	}
//
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) 
	{
		// Get all ids
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds)
		{

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
													  R.layout.widget_layout);
            Tip tip = getTipFromDb(context);

            //Uri tipUri = new Uri.Builder().appendPath(tipAndUri[1]).build();
            remoteViews.setTextViewText(R.id.update, tip.getTipText());
			if (tip.getIconAsBitmap() == null)
				remoteViews.setImageViewResource(R.id.icon, R.drawable.yllogo1);
			else
			{Log.i(TAG, "Yea! this one has an icon!");
				remoteViews.setImageViewBitmap(R.id.icon, tip.getIconAsBitmap());
			}		
			//remoteViews.setImageViewUri( R.id.icon, tipUri);

            // Register an onClickListener
            Intent intent = new Intent(context, WidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
																	 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

	private Tip getTipFromDb(Context context)
	{
		TipsDbAdapter db = InitDb(context);
		Tip randomTip = db.getRandomTip();
		db.close();
		Log.i(TAG, "got tip: " + randomTip);
		return randomTip;
	}

	private TipsDbAdapter InitDb(Context context)
	{
		TipsDbAdapter db;
		db = new TipsDbAdapter(context);
		db.open();

		if (db.getCount() < 10)
		{
			db.deleteAll();

			Bitmap ylIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.yllogo1);
			Bitmap theivesIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves1);
			Bitmap lavenderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.lavender_field);
			Bitmap kidScentsIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.kidscents1);
			Bitmap frankincense= BitmapFactory.decodeResource(context.getResources(), R.drawable.frankincense);
			Bitmap thievesSpray= BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves_spray);
			Bitmap peppermint= BitmapFactory.decodeResource(context.getResources(), R.drawable.peppermint);
			Bitmap rc= BitmapFactory.decodeResource(context.getResources(), R.drawable.rc);
			db.insertTip(new Tip(context.getString(R.string.tip1), peppermint));
			db.insertTip(new Tip(context.getString(R.string.tip2), ylIcon));
			db.insertTip(new Tip(context.getString(R.string.tip3), rc));
			db.insertTip(new Tip(context.getString(R.string.tip4), rc));
			db.insertTip(new Tip(context.getString(R.string.tip5), ylIcon));
			db.insertTip(new Tip(context.getString(R.string.tip6), theivesIcon));
			db.insertTip(new Tip(context.getString(R.string.tip7), frankincense));
			db.insertTip(new Tip(context.getString(R.string.tip8), lavenderIcon));
			db.insertTip(new Tip(context.getString(R.string.tip9), kidScentsIcon));
			db.insertTip(new Tip(context.getString(R.string.tip10), thievesSpray));
			
		}
		return db;
	}

} 
