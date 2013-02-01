package com.oilyliving.tips;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.oilyliving.tips.*;

public class WidgetProvider extends AppWidgetProvider
{
    private static final String TAG = "TipsWidget";
	public static final String EXTRA_TIP = "com.oilyliving.tips.WidgetProvider.EXTRA_TIP";
	public static final String EXTRA_TIP_TEXT = "com.oilyliving.tips.WidgetProvider.EXTRA_TIP_TEXT";

    private boolean firstTime = true;

	/*	
	 public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	 final int N = appWidgetIds.length;

	 // Perform this loop procedure for each App Widget that belongs to this provider 
	 for (int i=0; i<N; i++) {
	 int appWidgetId = appWidgetIds[i];

	 // Create an Intent to launch ExampleActivity 
	 Intent intent = new Intent(context, FullTipDialogActivity.class); 
	 PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

	 // Get the layout for the App Widget and attach an on-click listener 
	 // to the button 
	 RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
	 views.setTextViewText(R.id.tipText,"Tip Text has been updated");
	 views.setOnClickPendingIntent(R.id.next, pendingIntent);

	 // Tell the AppWidgetManager to perform an update on the current app widget 
	 appWidgetManager.updateAppWidget(appWidgetId, views);
	 }
	 }
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
						 int[] appWidgetIds)
	{
		// Get all ids
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		for (int widgetId : allWidgetIds)
		{			
			appWidgetManager.updateAppWidget(widgetId, buildUpdate(context, appWidgetIds));
		}


	}

	private RemoteViews buildUpdate(Context context, int[] appWidgetIds)
	{
		Tip tip = getTipFromDb(context);
		RemoteViews remoteViews = setupRemoteViewWithTip(context, tip);

		// Create an Intent to launch ExampleActivity 
		Intent intent = new Intent(context, FullTipDialogActivity.class); 
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra(WidgetProvider.EXTRA_TIP, tip);
		intent.putExtra(WidgetProvider.EXTRA_TIP_TEXT, tip.getTipText());

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		remoteViews.setOnClickPendingIntent(R.id.tipText, pendingIntent);
		remoteViews.setOnClickPendingIntent(R.id.icon, pendingIntent);

		Intent getNextIntent = new Intent(context, WidgetProvider.class);

		getNextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		getNextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		PendingIntent getNextPI = PendingIntent.getBroadcast(context,
															 0, getNextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.next, getNextPI);
		return remoteViews;
	}	

	private RemoteViews setupRemoteViewWithTip(Context context, Tip tip)
	{
		//Tip tip = getTipFromDb(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
												  R.layout.widget_layout);

		//Uri tipUri = new Uri.Builder().appendPath(tipAndUri[1]).build();
		remoteViews.setTextViewText(R.id.tipText, tip.getTipText());
		if (tip.getIconAsBitmap() == null)
			remoteViews.setImageViewResource(R.id.icon, R.drawable.yllogo1);
		else
		{
			Log.i(TAG, "Yea! this one has an icon!");
			remoteViews.setImageViewBitmap(R.id.icon, tip.getIconAsBitmap());
		}

		//remoteViews.setImageViewUri( R.id.icon, tipUri);

		return remoteViews;
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

        if (firstTime)
		{
            firstTime = false;
            db.deleteAll();

            Bitmap ylIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.yllogo1);
            Bitmap thievesIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves1);
            Bitmap lavenderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.lavender_field);
            Bitmap kidScentsIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.kidscents1);
            Bitmap frankincense = BitmapFactory.decodeResource(context.getResources(), R.drawable.frankincense);
            Bitmap thievesSpray = BitmapFactory.decodeResource(context.getResources(), R.drawable.thieves_spray);
            Bitmap peppermint = BitmapFactory.decodeResource(context.getResources(), R.drawable.peppermint);
            Bitmap rc = BitmapFactory.decodeResource(context.getResources(), R.drawable.rc);
            db.insertTip(new Tip(context.getString(R.string.tip1), peppermint));
            db.insertTip(new Tip(context.getString(R.string.tip2), ylIcon));
            db.insertTip(new Tip(context.getString(R.string.tip3), rc));
            db.insertTip(new Tip(context.getString(R.string.tip4), rc));
            db.insertTip(new Tip(context.getString(R.string.tip5), ylIcon));
            db.insertTip(new Tip(context.getString(R.string.tip6), thievesIcon));
            db.insertTip(new Tip(context.getString(R.string.tip7), frankincense));
            db.insertTip(new Tip(context.getString(R.string.tip8), lavenderIcon));
            db.insertTip(new Tip(context.getString(R.string.tip9), kidScentsIcon));
            db.insertTip(new Tip(context.getString(R.string.tip10), thievesSpray));

        }
        return db;
    }

} 
