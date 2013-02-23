package com.oilyliving.tips;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.*;
import android.text.method.*;
import com.oilyliving.tips.data.*;
import android.text.util.*;
import android.text.*;


public class FullTipDialogActivity extends Activity
{
	private static final String TAG = "FullTipDialogActivity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);

		Bundle extras = getIntent().getExtras();

		Tip tip = extras.getParcelable(WidgetProvider.EXTRA_TIP);				

		String tipText = tip.getTipText();
		String notes = "";
		String referenceUrl = tip.getWebReference();
		if (referenceUrl != null && referenceUrl != "")
		{
			Log.d(TAG, "reference='" + referenceUrl +"'");
			String link = " <sup><a href=\"" + referenceUrl + "\">[1]</a></sup>";
			tipText = tipText + link;
		}			

		int eopr = tip.getEoprPage();
		if (eopr > 0)
		{
			String link= " <sup>[EPRO:" + eopr + "]</sup>";
			tipText = tipText + link;
			notes = notes + "ERPO = Essential Oils Pocket Reference\n";
		}			

		int rgeo = tip.getRgeoPage();
		if (rgeo > 0)
		{
			String link= " <sup>[RGEO:" + rgeo + "]</sup>";
			tipText = tipText + link;
			notes = notes + "RGEO = Reference Guide for Essential Oils\n";
		}			

		TextView tipTextView = (TextView)findViewById(R.id.dialogTipText);
		tipTextView.setText(Html.fromHtml(tipText));
		tipTextView.setMovementMethod(LinkMovementMethod.getInstance());


		TextView link = (TextView)findViewById(R.id.dialogLink);
		link.setMovementMethod(LinkMovementMethod.getInstance());
		Log.d(TAG, "link=" + link.getText());

		ImageView iconView = (ImageView)findViewById(R.id.dialogIcon);
		iconView.setImageBitmap(tip.getIcon().getIconAsBitmap());

		TextView refTextView = (TextView)findViewById(R.id.references);
		refTextView.setText(notes);

	}
}
	
