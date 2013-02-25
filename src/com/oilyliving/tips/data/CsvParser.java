package com.oilyliving.tips.data;

import android.util.*;
import java.net.*;
import java.util.*;
import java.text.*;
import android.webkit.*;

public final class CsvParser
{
	private static final String TAG = "CsvParser";
	public static boolean Parse(List<String> lines, List<Tip> tips, List<Icon> icons)
	{
		if (lines == null)
		{
			Log.d(TAG, "No lines given (null)");
			return false;
		}
		if (lines.size() == 0)
		{
			Log.d(TAG, "No lines given (empty)");
			return false;
		}
		for (String line:lines)
		{
			parseLine(line, tips, icons);
		}

		return true;
	}


	private static void parseLine(String line, List<Tip> tips, List<Icon> icons) 
	{
		Log.d(TAG, "Got line:" + line);
		String[] RowData = line.split("[|]");
//		for (String part:RowData)
//		{
//			Log.d(TAG, part);
//		}
		String tipIdString = RowData[0].replaceAll("\"", "");
		int tipId = Integer.parseInt(tipIdString);
		String tipText = RowData[1].replaceAll("\"", "");
		String iconUrlString = RowData[2].replaceAll("\"", "");
		String referenceUrlString = RowData[3].replaceAll("\"", "");
		String lastModifiedString = RowData[4].replaceAll("\"", "");


		Date lastModified = new Date(0);
		try
		{
			lastModified = new Date(Long.parseLong(lastModifiedString));
		}
		catch (NumberFormatException e)
		{
			Log.i(TAG, "LastModifiedDate in wrong format:" + lastModifiedString + "; using default date:" + lastModified);
		}

		String iconName ="";
		parseIcon(iconUrlString, icons);

		Tip tip = new Tip(tipId, tipText, iconName, "", lastModified);
		parseRefData(tip, referenceUrlString);
		tips.add(tip);		


	}

	private static void parseRefData(Tip tip, String referenceUrlString)
	{
		String[] parts= referenceUrlString.split("[;]");
		for (String part:parts)
		{
			Log.d(TAG, part);
			if (part.startsWith("http") || part.startsWith("www"))
				tip.setWebReference(part);
			else if (part.startsWith("EOPR"))
			{
				tip.setEoprPage(0);
			}
			else if (part.startsWith("RGEO"))
			{		
				tip.setRgeoPage(0);
			}	
		}
	}

	private static String parseIcon(String iconUrlString, List<Icon> icons)
	{
		String iconName = "";
		String[] split = iconUrlString.split("/");

		iconName = split[split.length - 1];
		Log.d(TAG, "UrlUtil.guessFileName=" + URLUtil.guessFileName(iconUrlString, null, null));

		Icon icon = new Icon(iconName, iconUrlString);
		icons.add(icon);

		return iconName;
	}

}
