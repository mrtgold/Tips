package com.oilyliving.tips;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import android.os.*;
import java.net.*;
import android.util.*;

public class Tip implements Parcelable
{
	private static final String TAG = "Tip";
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
	public int describeContents()
	{ return 0; }

	public void writeToParcel(Parcel out, int flags)
	{
		Log.i(TAG, "writing to parcel:"+tipText);
		out.writeString(tipText);
		icon.writeToParcel(out, 0);
		
		Log.i(TAG, "parcel.dataSize:"+out.dataSize());
		
		//out.writeString(localUri.toString());
		//out.writeString(serverUrl.toString());
	}
	

	public static final Parcelable.Creator<Tip> CREATOR = new Parcelable.Creator<Tip>() 
	{ 
		public Tip createFromParcel(Parcel in) 
		{ return new Tip(in); }

		public Tip[] newArray(int size)
		{ return new Tip[size]; }
	};

	private Tip(Parcel in)
	{ 
		Log.i(TAG, "Reading from parcel");
		tipText = in.readString();
		Log.i(TAG, "tipText=" + tipText);

		icon = Bitmap.CREATOR.createFromParcel(in);

		//localUri = Uri.parse(in.readString());
		localUri = null;
		//serverUrl = new URL(in.readString());
		serverUrl = null;
	}
}
