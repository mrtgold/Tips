package com.oilyliving.tips.data;

import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import com.oilyliving.tips.data.*;
import java.io.*;
import java.net.*;
import android.webkit.*;

public class Icon implements Parcelable
{
	private static final String TAG = "Icon";

    private final String name;
    private String[] tags = null;
    private Bitmap icon = null;
    private String serverUrl = null;

    public Icon(String name, Bitmap icon)
	{
        this.name = name;
        this.icon = icon;
    }

    public Icon(String name, String url) 
	{
        this.name = name;
        this.serverUrl = url;
    }

    public Icon(String name, Bitmap icon, String[] tags, String serverUrl)
	{
        this.name = name;
        this.icon = icon;
        this.tags = tags;
        this.serverUrl = serverUrl;
    }

    public Icon(String name, byte[] iconBytes, String tagString)
	{
        this.name = name;
        this.icon = convertBytesToBitmap(iconBytes);

        setTagsFromString(tagString);
    }

	public void setServerUrl(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}

	public String getServerUrl()
	{
		return serverUrl;
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
		if (tags == null || tags.length < 1)
			return "";
        return TextUtils.join(",", this.tags);
    }

    public void setTagsFromString(String str)
	{
		if (str !=  null)
			this.tags = str.split(",");
    }

    public Bitmap getIconAsBitmap()
	{
        return this.icon;
    }

    public byte[] getIconAsBytes()
	{
		if (icon == null)
			return null;
        return convertBitmapToBytes(this.icon);
    }

    public void setIconFromBytes(byte[] iconBytes)
	{
        this.icon = convertBytesToBitmap(iconBytes);

    }

    public void setIcon(Bitmap bitmap)
	{
        this.icon = bitmap;
    }

    @Override
    public String toString()
	{
        return this.name + "(" + this.serverUrl + ")";
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
	public int describeContents()
	{ return 0; }

	public void writeToParcel(Parcel out, int flags)
	{
		Log.d(TAG, "writing to parcel:" + name);
		out.writeString(name);
		out.writeString(serverUrl);

		out.writeValue(icon);
	 
		Log.d(TAG, "parcel.dataSize:" + out.dataSize());		
	}

	private Icon(Parcel in)
	{ 
		Log.d(TAG, "Reading from parcel");
		name = in.readString();
		serverUrl = in.readString();
		icon= in.readParcelable(Bitmap.class.getClassLoader());

		Log.d(TAG, "name=" + name);

	}

	public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() 
	{ 
		public Icon createFromParcel(Parcel in) 
		{ return new Icon(in); }

		public Icon[] newArray(int size)
		{ return new Icon[size]; }
	};


}
