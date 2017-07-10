package com.vivantor.examples;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import uk.co.senab.photoview.PhotoView;

public class SingleImageActivity extends AppCompatActivity implements MediaScannerConnection.MediaScannerConnectionClient
{
	public static final String IMAGE_URL_EXTRA = "IMAGE_URL_EXTRA";
	PhotoView photoView;
	String image_url;

	MediaScannerConnection mediaScannerConnection;
	String filePathOnDisk;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_image);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LOW_PROFILE
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}

		photoView = (PhotoView) findViewById(R.id.your_photo_view);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			image_url = bundle.getString(IMAGE_URL_EXTRA, "");
			LoadImage();
			mediaScannerConnection = new MediaScannerConnection(this, this);
		}
		else
		{
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_save, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.saveAction:
				saveImageToDisk();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void LoadImage()
	{
		Glide.with(this)
				.load(image_url)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.listener(new RequestListener<String, GlideDrawable>()
				{
					@Override
					public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
					{
						Log.d("glide", "Glide failed loading image ");

						return false;
					}

					@Override
					public boolean onResourceReady(final GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
					{
						photoView.setImageDrawable(resource);

						Log.d("glide", "Glide success loading image ");
						return false;
					}
				})
				.into(photoView);
	}

	void saveImageToDisk()
	{
		String[] permissions = new String[]
				{
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				};

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
		{
			Save();
		}
		else
		{
			ActivityCompat.requestPermissions(this, permissions, 5000);
		}
	}

	void Save()
	{
		new AsyncTask<Drawable, Void, Void>()
		{
			@Override
			protected Void doInBackground(Drawable... params)
			{
				filePathOnDisk = Utils.saveBitmapToFile("VivantorMediaFiles", Utils.drawableToBitmap(params[0]));

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid)
			{
				Toast.makeText(SingleImageActivity.this, "Photo saved!", Toast.LENGTH_SHORT).show();
				if (!mediaScannerConnection.isConnected())
					mediaScannerConnection.connect();
			}
		}.execute(photoView.getDrawable());
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			if (requestCode == 5000)
			{
				Save();
			}
		}
	}

	@Override
	public void onMediaScannerConnected()
	{
		if (filePathOnDisk != null && !filePathOnDisk.isEmpty())
			mediaScannerConnection.scanFile(filePathOnDisk, "image/*");
	}

	@Override
	public void onScanCompleted(String path, Uri uri)
	{
//		Toast.makeText(FullScreenImageViewActivity.this, "Photo added!", Toast.LENGTH_SHORT).show();
		mediaScannerConnection.disconnect();
	}
}
