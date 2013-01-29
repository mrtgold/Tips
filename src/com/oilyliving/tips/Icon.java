package com.oilyliving.tips;
import android.graphics.*;
import android.net.*;
import android.content.res.*;
import java.net.*;
import java.io.*;
import android.text.TextUtils;

public class Icon
{
	private final String name;
	private String[] tags;
	private Bitmap icon;
	private Uri localUri;
	private final URL serverUrl;

	public Icon(String name, Bitmap icon)
	{
		this.name = name;
		this.icon = icon;
		this.tags = null;
		this.localUri = null;
		this.serverUrl = null;
	}

	public Icon(String name, Bitmap icon, String[] tags)
	{
		this.name = name;
		this.icon = icon;
		this.tags = tags;
		this.localUri = null;
		this.serverUrl = null;
	}

	public Icon(String name, byte[] iconBytes, String tagString)
	{
		this.name = name;
		this.icon = convertBytesToBitmap(iconBytes);
		this.tags = null;
		this.localUri = null;
		this.serverUrl = null;

		setTagsFromString(tagString);
	}

	public String getName()
	{
		return this.name;
	}

	public String[] getTags()
	{
		return this.tags;
	}

	public String getTagsString()
	{
		return TextUtils.join(",",this.tags);
	}

	public void setTagsFromString(String str)
	{
		this.tags = str.split(",");
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

	public void setIconFromBytes(byte[] iconBytes)
	{
		this.icon = convertBytesToBitmap(iconBytes);

	}
	@Override
	public String toString()
	{
		return this.name;
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
