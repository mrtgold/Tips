package com.oilyliving.tips.data;

import android.util.*;
import java.net.*;
import java.util.*;
import java.text.*;

public final class CsvParser
{
	private static final String TAG = "CsvParser";
	public static void Parse(List<String> lines, List<Tip> tips, List<Icon> icons)
	{
		for (String line:lines)
		{
			parseLine(line, tips, icons);
		}
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

		Tip tip = new Tip(tipId, tipText, iconName, referenceUrlString, lastModified);
		tips.add(tip);		


	}

	private static String parseIcon(String iconUrlString, List<Icon> icons)
	{
		String iconName = "";
		String[] split = iconUrlString.split("/");
//		for (String part : split)
//		{
//			Log.d(TAG, part);
//		}

		iconName = split[split.length - 1];

		try
		{
			URL iconURL = new URL(iconUrlString);
			Icon icon = new Icon(iconName, iconURL);
			icons.add(icon);
		}
		catch (MalformedURLException e)
		{
			Log.i(TAG, "Bad icon path:" + iconUrlString);
		}

		return iconName;
	}

}
