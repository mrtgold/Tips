package com.oilyliving.tips;
import android.graphics.*;
import android.net.*;
import android.content.res.*;
import java.net.*;

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
		this.tipText=tipText;
		this.icon=icon;
		this.localUri=null;
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
}
