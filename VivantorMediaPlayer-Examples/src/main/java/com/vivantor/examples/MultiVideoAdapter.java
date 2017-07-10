package com.vivantor.examples;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vivantor.mediaplayer.MediaFile;
import com.vivantor.mediaplayer.MediaPlayerManager;
import com.vivantor.mediaplayer.Interfaces.VMediaPlayerEvents;
import com.vivantor.mediaplayer.UI.VAudioPlayer;

import java.util.List;

/**
 * Created by AhmedNTS on 2016-06-07.
 */
public class MultiVideoAdapter extends ArrayAdapter<MediaFile>
{
	private Context context;

	public MultiVideoAdapter(Context context, List<MediaFile> items)
	{
		super(context, -1, items);
		this.context = context;
	}

	@NonNull
	public View getView(final int position, View view, @NonNull ViewGroup parent)
	{
		final MediaFile mediaFile = getItem(position);

		final ViewHolder holder;

		if (view == null)
		{
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.item_list, null);

			holder = new ViewHolder();
			holder.player = (VAudioPlayer) view.findViewById(R.id.audioPlayer);
			holder.mediaURL = (TextView) view.findViewById(R.id.mediaURL);

			view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) view.getTag();
		}

		if (mediaFile != null)
		{
			holder.mediaURL.setText(mediaFile.filePath);

			holder.player.setMediaFile(mediaFile);
			holder.player.setOnVPlayerListener(new VMediaPlayerEvents()
			{
				@Override
				public void OnPlayClicked()
				{
					MediaPlayerManager.getInstance().StartPlayer(holder.player);
				}

				@Override
				public void OnPauseClicked()
				{
					MediaPlayerManager.getInstance().PausePlayer(holder.player);
				}
			});
		}

		return view;
	}

	private static class ViewHolder
	{
		TextView mediaURL;
		VAudioPlayer player;
	}
}

