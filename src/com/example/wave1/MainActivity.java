package com.example.wave1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
		

//		Notification notif = new Notification();
//		notif.ledARGB = 0xffffffff;
//		notif.ledOnMS = 300;
//		notif.ledOffMS = 1000;
//		notif.flags |= Notification.FLAG_SHOW_LIGHTS;
//		notif.defaults |= Notification.DEFAULT_LIGHTS;
//		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//		nm.notify(R.string.app_name, notif);
	}

}
