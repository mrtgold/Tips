package com.oilyliving.tips.data;

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
    private final String iconName;
	private Icon icon;

    public Tip(String tipText, String iconName)
	{
        this.tipText = tipText;
		this.iconName = iconName;
        this.icon = null;
    }

    public Tip(String tipText, Icon icon)
	{
        this.tipText = tipText;
        this.icon = icon;
        this.iconName = icon.getName();
    }

    public String getTipText()
	{
        return this.tipText;
    }

    public String getIconName()
	{
        return this.iconName;
    }

    public Icon getIcon()
	{
        return this.icon;
    }
	
	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

    @Override
    public String toString()
	{
        return this.tipText;
    }


	public int describeContents()
	{ return 0; }

	public void writeToParcel(Parcel out, int flags)
	{
		Log.i(TAG, "writing to parcel:"+tipText);
		out.writeString(tipText);
		out.writeString(iconName);
		icon.writeToParcel(out, 0);
		
		Log.i(TAG, "parcel.dataSize:"+out.dataSize());		
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
		iconName= in.readString();
		icon = Icon.CREATOR.createFromParcel(in);
		Log.i(TAG, "tipText=" + tipText);
		
	}
}
