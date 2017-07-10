package com.vivantor.examples;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vivantor.mediaplayer.MediaFile;
import com.vivantor.mediaplayer.UI.VVideoPlayer;

public class SingleVideoActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_video);

		setTitle("");

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		VVideoPlayer videoPlayer = (VVideoPlayer)findViewById(R.id.videoPlayer);
		videoPlayer.setMediaFile(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
//		getMenuInflater().inflate(R.menu.menu_save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
//				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
