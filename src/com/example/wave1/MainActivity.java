package com.example.wave1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends Activity {

	MediaPlayer player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		player = MediaPlayer.create(this, Uri.parse("/mnt/sdcard/music/「ぶちぬけ！２００８！」　オリジナル曲　vo.初音ミク.mp3"));

		View view = new WaveView(this, player);
		FrameLayout layout = (FrameLayout)findViewById(R.id.container);
		layout.addView(view);
		
	}
	
	protected void onStart(){
		super.onStart();
		
		player.start();
	}
	
	protected void onStop(){
		super.onDestroy();
		
		player.pause();
	}

}
