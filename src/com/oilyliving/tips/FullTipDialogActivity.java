package com.oilyliving.tips;


import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.*;


public class FullTipDialogActivity extends Activity
{
	private static final String TAG = "FullTipDialogActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);

		Bundle extras = getIntent().getExtras();
		Log.i(TAG, "Extras null:" + (extras == null));
		Log.i(TAG, "Extras.keys.count:" + extras.keySet().toArray().length);
		for (String key : extras.keySet())
		{
			Log.i(TAG, "Key:" + key);
		}		
		
		Tip tip = extras.getParcelable(WidgetProvider.EXTRA_TIP);
		String tipText = tip.getTipText();
		
		if (tipText == null)
			tipText = "ARGHHH! no tip text!";


		TextView tipTextView = (TextView)findViewById(R.id.dialogTipText);
		ImageView iconView = (ImageView)findViewById(R.id.dialogIcon);
		tipTextView.setText(tipText);
		iconView.setImageBitmap(tip.getIconAsBitmap());
	}
}
	
