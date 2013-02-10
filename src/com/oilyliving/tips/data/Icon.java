package com.oilyliving.tips.data;

import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import com.oilyliving.tips.data.*;
import java.io.*;
import java.net.*;

public class Icon implements Parcelable
{
	private static final String TAG = "Icon";

    private final String name;
    private String[] tags;
    private Bitmap icon;
    private final URL serverUrl;

    public Icon(String name, Bitmap icon)
	{
        this.name = name;
        this.icon = icon;
        this.tags = null;
        this.serverUrl = null;
    }

    public Icon(String name, URL url) 
	{
        this.name = name;
        this.icon = null;
        this.tags = null;
        this.serverUrl = url;
    }

    public Icon(String name, Bitmap icon, String[] tags)
	{
        this.name = name;
        this.icon = icon;
        this.tags = tags;
        this.serverUrl = null;
    }

    public Icon(String name, byte[] iconBytes, String tagString)
	{
        this.name = name;
        this.icon = convertBytesToBitmap(iconBytes);
        this.tags = null;
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
        return TextUtils.join(",", this.tags);
    }

    public void setTagsFromString(String str)
	{
		if (str !=  null)
			this.tags = str.split(",");
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
	public int describeContents()
	{ return 0; }

	public void writeToParcel(Parcel out, int flags)
	{
		Log.i(TAG, "writing to parcel:" + name);
		out.writeString(name);
		icon.writeToParcel(out, 0);

		Log.i(TAG, "parcel.dataSize:" + out.dataSize());		
	}


	public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() 
	{ 
		public Icon createFromParcel(Parcel in) 
		{ return new Icon(in); }

		public Icon[] newArray(int size)
		{ return new Icon[size]; }
	};

	private Icon(Parcel in)
	{ 
		Log.i(TAG, "Reading from parcel");
		name = in.readString();
		Log.i(TAG, "name=" + name);
		serverUrl = null;
		icon = Bitmap.CREATOR.createFromParcel(in);

	}

}
