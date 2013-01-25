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

public class WidgetProvider extends AppWidgetProvider
{

	private static final String ACTION_CLICK = "ACTION_CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
						 int[] appWidgetIds)
	{

		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
													 WidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds)
		{

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
													  R.layout.widget_layout);
			// Set the text
			String[] tipAndUri = getTipAndUri();
			String tipText = tipAndUri[0];
			Uri tipUri = new Uri.Builder().appendPath(tipAndUri[1]).build();
			remoteViews.setTextViewText(R.id.update, tipText);
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

	private String[] getTipAndUri()
	{
		String[] tips = new String[]{
			"The Young Living blend Purification takes the sting out of fire ant bites. Use every 4-6 hours for 3 days to completely avoid the blistering and pain.",
			"Peppermint is great for relieving headaches. Rub it accross the forehead and on the temples.",
			"Use the Young Living blend R.C. for respiritory problems. Rub topically on chest & diffuse."
		};
		
		String[] uris = new String[]{
			"@drawable/ic_launcher",
			"@drawable/ic_launcher",
			"@drawable/ic_launcher"
		};
		// Pick one
		int number = (new Random().nextInt(tips.length));
		String tipText = tips[number] +
			" Get some today! (Tip #" + String.valueOf(number) + ")";
			String uri = uris[number];

		Log.w("WidgetExample", "tipText=" + tipText);
		return new String[]{ tipText, uri};
	}
} 
