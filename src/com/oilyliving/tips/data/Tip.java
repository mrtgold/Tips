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
	private String webReference = "";
	private int eoprPage = 0;
	private int rgeoPage = 0;
	private Date lastModified = new Date(0);
//	private Icon icon = null;

    public Tip(int tipId, String tipText, String iconName, String reference, Date lastUpdated)
	{
		this.tipId = tipId;
        this.tipText = tipText;
		this.iconName = iconName;
		this.webReference = reference;
		this.lastModified = lastUpdated;
    }

    public Tip(int tipId, String tipText, String iconName)
	{
		this.tipId = tipId;
        this.tipText = tipText;
		this.iconName = iconName;
    }

//    public Tip(int tipId, String tipText, Icon icon)
//	{
//		this.tipId = tipId;
//        this.tipText = tipText;
//        this.icon = icon;
//        this.iconName = icon.getName();
//	}
//
	public void setWebReference(String webReference)
	{
		this.webReference = webReference;
	}

	public String getWebReference()
	{
		return webReference;
	}

	public void setRgeoPage(int rgeoPage)
	{
		this.rgeoPage = rgeoPage;
	}

	public int getRgeoPage()
	{
		return rgeoPage;
	}

	public void setEoprPage(int eoprPage)
	{
		this.eoprPage = eoprPage;
	}

	public int getEoprPage()
	{
		return eoprPage;
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

//    public Icon getIcon()
//	{
//        return this.icon;
//    }


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
		out.writeString(webReference);
		out.writeInt(eoprPage);
		out.writeInt(rgeoPage);
		out.writeLong(lastModified.getTime());
//		out.writeValue(icon);		
		Log.d(TAG, "parcel.dataSize:" + out.dataSize());		
	}


	private Tip(Parcel in)
	{ 
		Log.d(TAG, "Reading from parcel");
		tipId = in.readInt();
		tipText = in.readString();
		iconName = in.readString();
		webReference = in.readString();
		eoprPage = in.readInt();
		rgeoPage = in.readInt();
		lastModified = new Date(in.readLong());
//		icon= in.readParcelable(Icon.class.getClassLoader());
//		icon = Icon.CREATOR.createFromParcel(in);

		Log.d(TAG, "tipText=" + tipText);		
		Log.d(TAG, "reference=" + webReference);		
	}

	public static final Parcelable.Creator<Tip> CREATOR = new Parcelable.Creator<Tip>() 
	{ 
		public Tip createFromParcel(Parcel in) 
		{ return new Tip(in); }

		public Tip[] newArray(int size)
		{ return new Tip[size]; }
	};
}
