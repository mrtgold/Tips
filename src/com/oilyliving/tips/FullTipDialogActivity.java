package com.oilyliving.tips;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.widget.*;
import com.oilyliving.tips.data.*;


public class FullTipDialogActivity extends Activity
{
	private static final String TAG = "FullTipDialogActivity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_relative);

		Bundle extras = getIntent().getExtras();

		Tip tip = extras.getParcelable(WidgetProvider.EXTRA_TIP);				

		String tipText = tip.getTipText();
		String notes = "";
		String referenceUrl = tip.getWebReference();
		if (referenceUrl != null && !referenceUrl.trim().isEmpty())
		{
			Log.d(TAG, "reference='" + referenceUrl + "'");
			String link = " <sup><a href=\"" + referenceUrl + "\">[1]</a></sup>";
			tipText = tipText + link;
		}			

		int eopr = tip.getEoprPage();
		if (eopr > 0)
		{
			String link= " <sup>[PR:" + eopr + "]</sup>";
			tipText = tipText + link;
			notes = notes + "PR = Essential Oils Pocket Reference\n";
		}			

		int rgeo = tip.getRgeoPage();
		if (rgeo > 0)
		{
			String link= " <sup>[RG:" + rgeo + "]</sup>";
			tipText = tipText + link;
			notes = notes + "RG = Reference Guide for Essential Oils\n";
		}			

		TextView tipTextView = (TextView)findViewById(R.id.dialogTipText);
		tipTextView.setText(Html.fromHtml(tipText));
		tipTextView.setMovementMethod(LinkMovementMethod.getInstance());


		TextView link = (TextView)findViewById(R.id.dialogLink);
		link.setMovementMethod(LinkMovementMethod.getInstance());
		Log.d(TAG, "link=" + link.getText());

		Icon icon = getIconFromDb(getApplicationContext(), tip);

		ImageView iconView = (ImageView)findViewById(R.id.dialogIcon);
		if (icon == null)
		{
			Log.d(TAG,"iconName is null");
			iconView.setImageResource(R.drawable.yllogo1);
		}
		else if (icon.getIconAsBitmap() == null)
		{
			Log.d(TAG,"icon bitmap is null");
			iconView.setImageResource(R.drawable.yllogo1);
		}else
			iconView.setImageBitmap(icon.getIconAsBitmap());


		TextView refTextView = (TextView)findViewById(R.id.references);
		refTextView.setText(notes);

	}

	private Icon getIconFromDb(Context context, Tip tip)
	{
        String iconName = tip.getIconName();
		Log.d(TAG, "iconName=" + iconName);
        IconDbAdapter db = new IconDbAdapter(context);
        db.open();
		Icon icon = db.getIconByName(iconName);
        db.close();

		return icon;
	}	

}
	
