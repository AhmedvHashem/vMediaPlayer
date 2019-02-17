package com.vivantor.mediaplayer;

import com.vivantor.mediaplayer.Interfaces.VPlayer;
import com.vivantor.mediaplayer.UI.VAudioPlayer;

/**
 * Created by AhmedNTS on 2016-06-18.
 */
public class MediaPlayerManager
{
	private String TAG = MediaPlayerManager.class.getSimpleName();

	private static MediaPlayerManager instance;

	private VPlayer currentPlayer;

	private MediaPlayerManager()
	{
	}

	public static MediaPlayerManager getInstance()
	{
		if (instance == null)
			instance = new MediaPlayerManager();

		return instance;
	}

	public void StartPlayer(VPlayer player)
	{
		if (player == null) return;

		if (currentPlayer == player)
			player.Start();
		else
		{
			if (currentPlayer != null)
				currentPlayer.Pause();

			currentPlayer = player;
			currentPlayer.Start();
		}
	}

	public void PausePlayer(VPlayer player)
	{
		player.Pause();
	}
}
