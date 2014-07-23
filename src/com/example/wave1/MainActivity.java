package com.example.wave1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View view = new WaveView(this);
		FrameLayout layout = (FrameLayout)findViewById(R.id.container);
		layout.addView(view);
	}

}
