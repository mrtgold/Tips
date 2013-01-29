package com.oilyliving.tips;
import android.graphics.*;
import android.net.*;
import android.content.res.*;
import java.net.*;
import java.io.*;

public class Tip
{
	private final String tipText;
	private final Bitmap icon;
	private final Uri localUri;
	private final URL serverUrl;

	public Tip(String tipText)
	{
		this.tipText = tipText;
		this.icon = null;//BitmapFactory.decodeResource(getResources(), R.drawable.yllogo1);
		this.localUri = null;
		this.serverUrl = null;
	}

	public Tip(String tipText, Bitmap icon)
	{
		this.tipText = tipText;
		this.icon = icon;
		this.localUri = null;
		this.serverUrl = null;
	}

	public Tip(String tipText, byte[] iconBytes)
	{
		this.tipText = tipText;
		this.icon = convertBytesToBitmap(iconBytes);
		this.localUri = null;
		this.serverUrl = null;
	}

	public String getTipText()
	{
		return this.tipText;
	}

	public Uri getLocalUri()
	{
		return this.localUri;
	}

	public URL getServerUrl()
	{
		return this.serverUrl;
	}

	public Bitmap getIconAsBitmap()
	{
		return this.icon;
	}

	public byte[] getIconAsBytes()
	{
		return convertBitmapToBytes(this.icon);
	}

	@Override
	public String toString()
	{
		return this.tipText;
	}
	
	
	private static Bitmap convertBytesToBitmap(byte[] bytes)
	{
		if (bytes == null) 
			return null;
		
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);		
	}

	private static byte[] convertBitmapToBytes(Bitmap bitmap)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
		return outputStream.toByteArray();

	}
	
}
