package com.oilyliving.tips;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class MainActivity extends Activity
{
	EditText quote;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		

		Button doneButton = (Button) findViewById(R.id.done);
		doneButton.setOnClickListener(mAddListener);
    }

	private OnClickListener mAddListener = new OnClickListener()
	{
		public void onClick(View view)
		{
			finish();
		}
	};
}
