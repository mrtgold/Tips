package com.oilyliving.tips;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.*;
import android.text.method.*;
import com.oilyliving.tips.data.*;


public class FullTipDialogActivity extends Activity
{
//	private static final String TAG = "FullTipDialogActivity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);

		Bundle extras = getIntent().getExtras();

		Tip tip = extras.getParcelable(WidgetProvider.EXTRA_TIP);				

		TextView tipTextView = (TextView)findViewById(R.id.dialogTipText);
		tipTextView.setText(tip.getTipTextAndId());

		TextView link = (TextView)findViewById(R.id.dialogLink);
		link.setMovementMethod(LinkMovementMethod.getInstance());

		ImageView iconView = (ImageView)findViewById(R.id.dialogIcon);
		iconView.setImageBitmap(tip.getIcon().getIconAsBitmap());

		String referenceUrl = tip.getReferenceUrl();
		if (referenceUrl != null && referenceUrl != "")
		{
			TextView ref = (TextView)findViewById(R.id.references);
			String refLink = "<a href=\"" + referenceUrl + "\">[1]</a>";
			ref.setText(refLink);
			ref.setMovementMethod(LinkMovementMethod.getInstance());
		}			

	}
}
	
