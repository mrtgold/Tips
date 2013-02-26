package com.oilyliving.tips;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.net.*;
import android.net.http.*;
import android.provider.Settings.*;
import android.util.*;
import com.oilyliving.tips.data.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

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
		Context appContext = getApplicationContext();			
		PendingIntent pIntent = PendingIntent.getService(appContext, 0, intent, 0);
		AlarmManager alarm = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();

		//reset alarm to twice a day (prod) or 30 sec(testing)
		Resources resources = appContext.getResources();
		int successTimeoutMsec = resources.getInteger(R.integer.DownloadSuccessIntervalMin) * 60 * 1000;  //30 * 1000; //AlarmManager.INTERVAL_HALF_DAY;
		int failureTimeoutMsec = resources.getInteger(R.integer.DownloadFailureIntervalMin) * 60 * 1000;  //35 * 1000;  //AlarmManager.INTERVAL_HALF_HOUR;
		boolean success = false;

		try
		{
			success = downloadAndImportTips(appContext, success);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (success)
			{
				result = Activity.RESULT_OK;

				Log.i(TAG, "Download succeeded - setting interval to " + successTimeoutMsec + "msec");
				alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + successTimeoutMsec, pIntent); 
			}
			else
			{
				Log.i(TAG, "Download failed - setting interval to " + failureTimeoutMsec + "msec");
				alarm.set(AlarmManager.RTC, cal.getTimeInMillis() + failureTimeoutMsec, pIntent); 
			}
		}
	}

	private boolean downloadAndImportTips(Context appContext, boolean success)
	{
		Log.i(TAG, "Checking internet access");

//		if (!isOnline()) return false;

		String androidId = Secure.getString(appContext.getContentResolver(), Secure.ANDROID_ID);
		String urlPath = "http://oilytipsupdate.appspot.com/csv1?" + androidId;
		Log.i(TAG, "Starting download: " + urlPath);

		List<String> lines = new ArrayList<String>();
		if (!download(urlPath, lines)) return false;

		List<Tip> tips = new ArrayList<Tip>();
		List<Icon> icons = new ArrayList<Icon>();

		if (!CsvParser.Parse(lines, tips, icons)) return false;

		if (!updateTips(tips, appContext)) return false;				
		if (!updateIcons(icons, appContext)) return false;

		return true;
	}

	private boolean updateIcons(List<Icon> icons, Context appContext)
	{
		try
		{
			IconDbAdapter db;
			db = new IconDbAdapter(appContext);
			db.open();
			db.tryUpdateIcons(icons);
			db.close();
		}
		catch (SQLiteException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean updateTips(List<Tip> tips, Context appContext)// throws SQLiteException
	{
		try
		{
			TipsDbAdapter db;
			db = new TipsDbAdapter(appContext);
			db.open();
			db.tryUpdateTips(tips);
			db.close();
		}
		catch (SQLiteException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}


	private boolean download(String urlPath, List<String> lines)
	{	
		boolean success = false;
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
					Log.d(TAG, "added line: " + line);
				}
				success = true;
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
		catch (UnknownHostException ex)
		{	
			Log.i(TAG, "Can't connect; will try again later");
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

		return success;
	}
	
	Bitmap downloadBitmap(String url) {
//        final int IO_BUFFER_SIZE = 4 * 1024;

        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = /*(mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :*/
            AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
					  " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    return BitmapFactory.decodeStream(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }
    

	public boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean oneIsConnected = false;
		for (NetworkInfo info :cm.getAllNetworkInfo())
		{
			Log.d(TAG, info.toString() + " is active:" + info.isConnected());
			if (info.isConnected())
				oneIsConnected = true;
		}
//		NetworkInfo netInfo = cm.getActiveNetworkInfo();
//		return (netInfo != null && netInfo.isConnected());

		return oneIsConnected;
	}


}
