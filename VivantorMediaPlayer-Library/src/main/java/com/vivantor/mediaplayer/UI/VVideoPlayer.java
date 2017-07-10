package com.vivantor.mediaplayer.UI;

/**
 * Created by AhmedNTS on 2016-02-16.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vivantor.mediaplayer.Interfaces.VMediaPlayerEvents;
import com.vivantor.mediaplayer.MediaFile;
import com.vivantor.mediaplayer.R;
import com.vivantor.mediaplayer.Utils;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class VVideoPlayer extends FrameLayout implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener
{
	private static final String TAG = VVideoPlayer.class.getSimpleName();

	private Handler handler;
	private VVideoView mediaPlayer;

	private ToggleButton mediaToggle;
	private View mediaControllers;
	private SeekBar mediaSeekBar;
	private TextView mediaRemainTime;

	private MediaFile mediaFile;

	VMediaPlayerEvents listener;

	//Style
	private int bgColor = Color.parseColor("#80ffffff");
	private int timeColor = Color.WHITE;
	private int timelineProgressColor = Color.WHITE;
	private int timelineIndicatorColor = Color.WHITE;
	private Drawable toggleDrawable;

	public VVideoPlayer(Context context)
	{
		super(context);
		init(null, 0);
	}

	public VVideoPlayer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public VVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(attrs, defStyleAttr);
	}

	public VVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(attrs, defStyleAttr);
	}

	private void init(AttributeSet attrs, int defStyle)
	{
		inflate(getContext(), R.layout.view_player_video, this);

		if (isInEditMode()) return;

		applyAttr(attrs, defStyle);

		mediaPlayer = (VVideoView) findViewById(R.id.vVideoView);

		mediaControllers = (View) findViewById(R.id.mediaControllers);
		mediaControllers.setBackgroundColor(bgColor);

		mediaToggle = (ToggleButton) findViewById(R.id.mediaToggle);
		if (toggleDrawable != null)
			mediaToggle.setBackground(toggleDrawable);

		mediaSeekBar = (SeekBar) findViewById(R.id.mediaSeekBar);
		mediaSeekBar.setMax(100);
		mediaSeekBar.setProgress(0);
		mediaSeekBar.getProgressDrawable().setColorFilter(timelineProgressColor, PorterDuff.Mode.SRC_IN);
		mediaSeekBar.getThumb().setColorFilter(timelineIndicatorColor, PorterDuff.Mode.SRC_IN);

		mediaRemainTime = (TextView) findViewById(R.id.mediaRemainTime);
		mediaRemainTime.setText("00:00");
		mediaRemainTime.setTextColor(timeColor);


		mediaToggle.setOnCheckedChangeListener(this);
		mediaSeekBar.setOnSeekBarChangeListener(this);
		setOnClickListener(null);
//		mediaPlayer.setOnMediaPlayerReleasedRelease(new VVideoView.OnMediaPlayerReleased()
//		{
//			@Override
//			public void onMediaPlayerReleasedRelease()
//			{
//				handler.removeCallbacks(UpdateSongTime);
//
//				mediaToggle.setChecked(false);
//				mediaControllers.setVisibility(View.VISIBLE);
//			}
//
//			@Override
//			public void onClick()
//			{
//				if (isControlsShown)
//				{
//					mediaControllers.setVisibility(View.GONE);
//					isControlsShown = false;
//				}
//				else
//				{
//					mediaControllers.setVisibility(View.VISIBLE);
//					isControlsShown = true;
//				}
//			}
//		});
	}

	private void applyAttr(AttributeSet attrs, int defStyle)
	{
		final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VVideoPlayer, defStyle, 0);

		bgColor = a.getColor(R.styleable.VVideoPlayer_bgColor, bgColor);
		timeColor = a.getColor(R.styleable.VVideoPlayer_timeColor, timeColor);
		timelineIndicatorColor = a.getColor(R.styleable.VVideoPlayer_timelineIndicatorColor, timelineIndicatorColor);
		timelineProgressColor = a.getColor(R.styleable.VVideoPlayer_timelineProgressColor, timelineProgressColor);

		if (a.hasValue(R.styleable.VVideoPlayer_toggleDrawable))
		{
			toggleDrawable = a.getDrawable(R.styleable.VVideoPlayer_toggleDrawable);
//			toggleDrawable.setCallback(mediaToggle);
		}

		a.recycle();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (isChecked)
		{
			mediaToggle.setVisibility(GONE);
			mediaControllers.setVisibility(GONE);

			if (listener != null)
				listener.OnPlayClicked();
			else
				Start();
		}
		else
		{
			if (listener != null)
				listener.OnPauseClicked();
			else
				Pause();
		}
	}

	@Override
	public void setOnClickListener(OnClickListener l)
	{
		if (l == null)
			l = new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mediaToggle.getVisibility() == VISIBLE)
						mediaToggle.setVisibility(GONE);
					else
						mediaToggle.setVisibility(VISIBLE);

					if (mediaControllers.getVisibility() == VISIBLE)
						mediaControllers.setVisibility(GONE);
					else
						mediaControllers.setVisibility(VISIBLE);
				}
			};

		super.setOnClickListener(l);
	}

	public void setMediaFile(MediaFile mediaFile)
	{
		if (mediaFile == null) return;

		if (mediaPlayer != null && isPlaying())
			Stop();

		this.mediaFile = mediaFile;
		mediaPlayer.setVideoPath(this.mediaFile.filePath);

		mediaToggle.setChecked(false);
		mediaSeekBar.setProgress(Utils.getProgressPercentage(this.mediaFile.currentDuration, this.mediaFile.totalDuration));
		mediaRemainTime.setText(String.format("%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes((long) this.mediaFile.currentDuration),
				TimeUnit.MILLISECONDS.toSeconds((long) this.mediaFile.currentDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) this.mediaFile.currentDuration))));
	}

	void Start()
	{
		if (this.mediaFile == null) return;

		mediaPlayer.setOnCompletionListener(this);

		if (!isPlaying())
		{
			mediaPlayer.seekTo((int) this.mediaFile.currentDuration);
			mediaPlayer.start();
		}

		if (handler == null)
			handler = new Handler();
		handler.postDelayed(UpdateSongTime, 100);
	}

	void Pause()
	{
		if (mediaPlayer == null) return;

		mediaPlayer.pause();

		mediaToggle.setChecked(false);

		if (handler != null)
			handler.removeCallbacks(UpdateSongTime);
	}

	void Stop()
	{
		if (mediaPlayer == null) return;

		mediaPlayer.stopPlayback();

		mediaToggle.setChecked(false);
		mediaSeekBar.setProgress(0);
		mediaRemainTime.setText("00:00");

		if (handler != null)
			handler.removeCallbacks(UpdateSongTime);
	}

	public boolean isPlaying()
	{
		if (mediaPlayer != null)
			return mediaPlayer.isPlaying();
		else return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
		if (handler != null)
			handler.removeCallbacks(UpdateSongTime);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		if (mediaPlayer == null) return;

		if (handler != null)
			handler.removeCallbacks(UpdateSongTime);

		int totalDuration = mediaPlayer.getDuration();
		int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		mediaPlayer.seekTo(currentPosition);

		// update timer progress again
		if (handler != null)
			handler.postDelayed(UpdateSongTime, 100);
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		if (handler != null)
			handler.removeCallbacks(UpdateSongTime);

		if (mediaToggle != null)
			mediaToggle.setChecked(false);
		if (mediaSeekBar != null)
			mediaSeekBar.setProgress(0);
		if (mediaRemainTime != null)
			mediaRemainTime.setText("00:00");

		mediaFile.currentDuration = 0;
	}

	private Runnable UpdateSongTime = new Runnable()
	{
		public void run()
		{
			try
			{
//				if (mediaPlayer.isPlaying())
				{
					mediaFile.totalDuration = mediaPlayer.getDuration();
					mediaFile.currentDuration = mediaPlayer.getCurrentPosition();

					if (mediaRemainTime != null)
						mediaRemainTime.setText(String.format("%02d:%02d",
								TimeUnit.MILLISECONDS.toMinutes((long) mediaFile.currentDuration),
								TimeUnit.MILLISECONDS.toSeconds((long) mediaFile.currentDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mediaFile.currentDuration))
						));

					// return percentage
					int progress = Utils.getProgressPercentage(mediaFile.currentDuration, mediaFile.totalDuration);
					if (mediaSeekBar != null)
						mediaSeekBar.setProgress(progress);

					if (handler != null)
						handler.postDelayed(this, 100);
				}
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			}
		}
	};

	public void setOnVPlayerListener(VMediaPlayerEvents listener)
	{
		this.listener = listener;
	}

//	@Override
//	protected void onDetachedFromWindow()
//	{
//		super.onDetachedFromWindow();
//
//		Dispose();
//	}
//
//	public void Dispose()
//	{
//		if (handler != null)
//			handler.removeCallbacks(UpdateSongTime);
//
//		if (mediaPlayer != null)
//		{
//			mediaPlayer.release(true);
//			mediaPlayer = null;
//		}
//
//		mediaFile = null;
//		listener = null;
//	}
}
