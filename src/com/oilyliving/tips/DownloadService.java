package com.oilyliving.tips;

import android.app.*;
import android.content.*;
import android.net.*;
import android.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.oilyliving.tips.data.*;
import android.content.res.*;

public class DownloadService extends IntentService
{

	private static final String TAG = "DownloadService";
	private int result = Activity.RESULT_CANCELED;

	public DownloadService()
	{
		super("DownloadService");
	}

	// called asynchronously 
	@Override
	protected void onHandleIntent(Intent intent)
	{
//		Uri data = intent.getData();
//		String urlPath = intent.getStringExtra("urlpath");

		Context appContext = getApplicationContext();			
		PendingIntent pIntent = PendingIntent.getService(appContext, 0, intent, 0);
		AlarmManager alarm = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();

		//reset alarm to twice a day (prod) or 30 sec(testing)
		Resources resources = appContext.getResources();
		int successTimeoutMsec = resources.getInteger(R.integer.DownloadSuccessIntervalMin) * 60 * 1000;  //30 * 1000; //AlarmManager.INTERVAL_HALF_DAY;
		int failureTimeoutMsec = resources.getInteger(R.integer.DownloadFailureIntervalMin) * 60 * 1000;  //35 * 1000;  //AlarmManager.INTERVAL_HALF_HOUR;
		try
		{
			Log.i(TAG, "Checking internet access");
			if (!isOnline())
			{
				Log.i(TAG, "Not online");//, will try again in " + failureTimeoutMsec / 1000 + " sec");
				Log.i(TAG, "Download failed - setting interval to " + failureTimeoutMsec + "msec");
//			alarm.cancel(pIntent);
//			alarm.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), failureTimeoutMsec, pIntent); 
				alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + failureTimeoutMsec, pIntent); 
				result = Activity.RESULT_OK;
//			return;
			}
			else
			{		String urlPath = "http://tgoldingtutorial1.appspot.com/csv";//context.getString(R.string.downloadUrl);
				Log.i(TAG, "Starting download: " + urlPath);

				List<Tip> tips = new ArrayList<Tip>();
				List<Icon> icons = new ArrayList<Icon>();

				downloadAndParse(urlPath, tips, icons);

				for (Tip tip:tips)
				{
					Log.i(TAG, "Got tip:" + tip);
				}

				for (Icon icon:icons)
				{
					Log.i(TAG, "Got icon:" + icon);
				}


				// Sucessful finished
				result = Activity.RESULT_OK;

				Log.i(TAG, "Download succeeded - setting interval to " + successTimeoutMsec + "msec");
				alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + successTimeoutMsec, pIntent); 
//			alarm.cancel(pIntent);
//			alarm.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), successTimeoutMsec, pIntent); 
//
			}
		}
		catch (Exception e)
		{
			Log.i(TAG, "Download failed - setting interval to " + failureTimeoutMsec + "msec");
			alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + failureTimeoutMsec, pIntent); 


		}
	}


	private void downloadAndParse(String urlPath, List<Tip> tips, List<Icon> icons)
	{
		InputStream stream = null;
		try
		{
			URL url = new URL(urlPath);
			stream = url.openConnection().getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			try
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					parseLine(line, tips, icons);
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void parseLine(String line, List<Tip> tips, List<Icon> icons) throws MalformedURLException
	{
		Log.i(TAG, "Got line:" + line);
		String[] RowData = line.split(",");
		String tipText = RowData[1].replaceAll("\"", "");
		String iconUrlString = RowData[2].replaceAll("\"", "");
		String referenceUrlString = RowData[3].replaceAll("\"", "");

		URL iconURL = new URL(iconUrlString);
		String iconName = iconURL.getFile();
		Icon icon = new Icon(iconName, iconURL);
		icons.add(icon);

		URL referenceUrl= new URL(referenceUrlString);
		Tip tip = new Tip(tipText, iconName, referenceUrl);
		tips.add(tip);		

	}
	public boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}


}
