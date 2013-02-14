package com.oilyliving.tips.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import android.os.*;
import java.net.*;
import android.util.*;
import java.util.*;

public class Tip implements Parcelable
{
	private static final String TAG = "Tip";
	private final int tipId;
    private final String tipText;
    private final String iconName;
	private final String reference;
	private final Date lastModified;
	private Icon icon;

    public Tip(int tipId, String tipText, String iconName, String reference, Date lastUpdated)
	{
		this.tipId = tipId;
        this.tipText = tipText;
		this.iconName = iconName;
        this.icon = null;
		this.reference = reference;
		this.lastModified = lastUpdated;
    }

    public Tip(int tipId, String tipText, String iconName)
	{
		this.tipId = tipId;
        this.tipText = tipText;
		this.iconName = iconName;
        this.icon = null;
		this.reference = "";
		this.lastModified = new Date(0);
    }

    public Tip(int tipId, String tipText, Icon icon)
	{
		this.tipId = tipId;
        this.tipText = tipText;
        this.icon = icon;
        this.iconName = icon.getName();
		this.reference = "";
		this.lastModified = new Date(0);
	}

	public String getTipTextAndId()
	{
		return getTipText() + " (Tip #" + getTipId() + ")";
	}

	public int getTipId()
	{
		return tipId;
	}
    public String getTipText()
	{
        return this.tipText;
    }

    public String getIconName()
	{
        return this.iconName;
    }

	public Date getLastModifiedDate()
	{
		return this.lastModified;
	}

	public String getReferenceUrl()
	{
		return this.reference;
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
		Log.d(TAG, "writing to parcel:" + tipText);
		out.writeInt(tipId);
		out.writeString(tipText);
		out.writeString(iconName);
		out.writeString(reference);
		out.writeLong(lastModified.getTime());
		icon.writeToParcel(out, 0);		
		Log.d(TAG, "parcel.dataSize:" + out.dataSize());		
	}


	private Tip(Parcel in)
	{ 
		Log.d(TAG, "Reading from parcel");
		tipId = in.readInt();
		tipText = in.readString();
		iconName = in.readString();
		reference = in.readString();
		lastModified = new Date(in.readLong());
		icon = Icon.CREATOR.createFromParcel(in);

		Log.d(TAG, "tipText=" + tipText);		
	}

	public static final Parcelable.Creator<Tip> CREATOR = new Parcelable.Creator<Tip>() 
	{ 
		public Tip createFromParcel(Parcel in) 
		{ return new Tip(in); }

		public Tip[] newArray(int size)
		{ return new Tip[size]; }
	};
}
