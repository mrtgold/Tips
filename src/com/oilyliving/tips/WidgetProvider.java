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
import java.util.*;

public class WidgetProvider extends AppWidgetProvider
{
    private static final String TAG = "TipsWidget";
	public static final String EXTRA_TIP = "com.oilyliving.tips.WidgetProvider.EXTRA_TIP";
	public static final String EXTRA_TIP_TEXT = "com.oilyliving.tips.WidgetProvider.EXTRA_TIP_TEXT";

    private boolean firstTimeTips = true;
	private boolean firstTimeImages = true;

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
	
		
		Intent intent = new Intent(context, DownloadService.class);
		Context appContext = context.getApplicationContext();			
		PendingIntent pIntent = PendingIntent.getService(appContext, 0, intent, 0);
		AlarmManager alarm = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		
		// Start now
		alarm.set(AlarmManager.RTC, cal.getTimeInMillis(), pIntent); 
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
			Log.d(TAG, "Yea! this one has an icon!");
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

        Log.d(TAG, "got tip: " + tip);
        return tip;
    }

    private Icon getIconFromDb(Context context, Tip tip)
	{
        IconDbAdapter db = InitIconsDb(context);
        Icon icon = db.getIconByName(tip.getIconName());
        db.close();
        Log.d(TAG, "got icon: " + icon.getName());

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
