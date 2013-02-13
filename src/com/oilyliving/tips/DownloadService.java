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
import android.provider.Settings.Secure;

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
				alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + failureTimeoutMsec, pIntent); 
				result = Activity.RESULT_OK;
			}
			else
			{		
				String androidId = Secure.getString(appContext.getContentResolver(),Secure.ANDROID_ID);
				String urlPath = "http://oilytipsupdate.appspot.com/csv1?" + androidId;//context.getString(R.string.downloadUrl);
				Log.i(TAG, "Starting download: " + urlPath);

				List<String> lines = download(urlPath);
				List<Tip> tips = new ArrayList<Tip>();
				List<Icon> icons = new ArrayList<Icon>();

				CsvParser.Parse(lines, tips, icons);

				for (Tip tip:tips)
				{
					Log.d(TAG, "Got tip:" + tip);
				}

				for (Icon icon:icons)
				{
					Log.d(TAG, "Got icon:" + icon);
				}


				// Sucessful finished
				result = Activity.RESULT_OK;

				Log.i(TAG, "Download succeeded - setting interval to " + successTimeoutMsec + "msec");
				alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + successTimeoutMsec, pIntent); 
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.i(TAG, "Download failed - setting interval to " + failureTimeoutMsec + "msec");
			alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + failureTimeoutMsec, pIntent); 


		}
	}


	private List<String> download(String urlPath)
	{	
		List<String> lines = new ArrayList<String>();
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
					lines.add(line);
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

		return lines;
	}

	public boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}


}
