package com.oilyliving.tips;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import com.oilyliving.tips.*;
import com.oilyliving.tips.data.*;

public class WidgetProvider extends AppWidgetProvider
{
    private static final String TAG = "TipsWidget";
	public static final String EXTRA_TIP = "com.oilyliving.tips.WidgetProvider.EXTRA_TIP";
	public static final String EXTRA_TIP_TEXT = "com.oilyliving.tips.WidgetProvider.EXTRA_TIP_TEXT";

    private boolean firstTimeTips = true;
	private boolean firstTimeImages = true;

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
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
												  R.layout.widget_layout);

		remoteViews.setTextViewText(R.id.tipText, tip.getTipText());


		if (tip.getIcon() == null || tip.getIcon().getIconAsBitmap() == null)
			remoteViews.setImageViewResource(R.id.icon, R.drawable.yllogo1);
		else
		{
			Log.i(TAG, "Yea! this one has an icon!");
			remoteViews.setImageViewBitmap(R.id.icon, tip.getIcon().getIconAsBitmap());
		}

		return remoteViews;
	}

    private Tip getTipFromDb(Context context)
	{
        TipsDbAdapter db = InitTipsDb(context);
        Tip tip = db.getRandomTip();
        db.close();

		Icon icon = getIconFromDb(context, tip);
		tip.setIcon(icon);

        Log.i(TAG, "got tip: " + tip);
        return tip;
    }

    private Icon getIconFromDb(Context context, Tip tip)
	{
        IconDbAdapter db = InitIconsDb(context);
        Icon icon = db.getIconByName(tip.getIconName());
        db.close();
        Log.i(TAG, "got icon: " + icon.getName());

		return icon;
	}

    private TipsDbAdapter InitTipsDb(Context context)
	{
        TipsDbAdapter db;
        db = new TipsDbAdapter(context);
        db.open();

        if (firstTimeTips)
		{
            firstTimeTips = false;
            db.InitTips(context);
        }
        return db;
    }


	private IconDbAdapter InitIconsDb(Context context)
	{
        IconDbAdapter db;
        db = new IconDbAdapter(context);
        db.open();

        if (firstTimeImages)
		{
            firstTimeImages= false;
            db.InitIcons(context);
        }
        return db;
    }


} 
