package com.oilyliving.tips;

import android.app.Activity;
import android.os.Bundle;
import android.text.style.QuoteSpan;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;

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
