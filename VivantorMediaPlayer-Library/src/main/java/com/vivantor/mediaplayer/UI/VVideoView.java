package com.vivantor.mediaplayer.UI;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import java.io.IOException;

@SuppressWarnings("all")
class VVideoView extends TextureView
{
	private static final String TAG = "VVideoView";

	private Uri mUri;

	// all possible internal states
	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;

	// currentState is a VideoView object's current state.
	// targetState is the state that a method caller intends to reach.
	// For instance, regardless the VideoView object's current state,
	// calling pause() intends to bring the object to a target state
	// of STATE_PAUSED.
	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private Surface mSurface;
	private MediaPlayer mMediaPlayer;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mAudioSession;
	private int mSeekWhenPrepared; // recording the seek position while
	private int mCurrentBufferPercentage;

	private OnCompletionListener mOnCompletionListener;
	private MediaPlayer.OnPreparedListener mOnPreparedListener;
	private OnErrorListener mOnErrorListener;
	private OnInfoListener mOnInfoListener;
	private OnMediaPlayerReleased mOnMediaPlayerReleased;

	public VVideoView(final Context context)
	{
		super(context);
		initVideoView();
	}

	public VVideoView(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
		initVideoView();
	}

	public VVideoView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initVideoView();
	}

	public void initVideoView()
	{
		mVideoHeight = 0;
		mVideoWidth = 0;

		setSurfaceTextureListener(mSurfaceTextureListener);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

//		setBackgroundColor(getResources().getColor(android.R.color.transparent));
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//	{
//		//Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
//		//        + MeasureSpec.toString(heightMeasureSpec) + ")");
//
//		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
//		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
//		if (mVideoWidth > 0 && mVideoHeight > 0)
//		{
//
//			int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
//			int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//			int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
//			int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//
//			if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY)
//			{
//				// the size is fixed
//				width = widthSpecSize;
//				height = heightSpecSize;
//
//				// for compatibility, we adjust size based on aspect ratio
//				if (mVideoWidth * height < width * mVideoHeight)
//				{
//					//Log.i("@@@", "image too wide, correcting");
//					width = height * mVideoWidth / mVideoHeight;
//				}
//				else if (mVideoWidth * height > width * mVideoHeight)
//				{
//					//Log.i("@@@", "image too tall, correcting");
//					height = width * mVideoHeight / mVideoWidth;
//				}
//			}
//			else if (widthSpecMode == MeasureSpec.EXACTLY)
//			{
//				// only the width is fixed, adjust the height to match aspect ratio if possible
//				width = widthSpecSize;
//				height = width * mVideoHeight / mVideoWidth;
//				if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize)
//				{
//					// couldn't match aspect ratio within the constraints
//					height = heightSpecSize;
//				}
//			}
//			else if (heightSpecMode == MeasureSpec.EXACTLY)
//			{
//				// only the height is fixed, adjust the width to match aspect ratio if possible
//				height = heightSpecSize;
//				width = height * mVideoWidth / mVideoHeight;
//				if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize)
//				{
//					// couldn't match aspect ratio within the constraints
//					width = widthSpecSize;
//				}
//			}
//			else
//			{
//				// neither the width nor the height are fixed, try to use actual video size
//				width = mVideoWidth;
//				height = mVideoHeight;
//				if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize)
//				{
//					// too tall, decrease both width and height
//					height = heightSpecSize;
//					width = height * mVideoWidth / mVideoHeight;
//				}
//				if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize)
//				{
//					// too wide, decrease both width and height
//					width = widthSpecSize;
//					height = width * mVideoHeight / mVideoWidth;
//				}
//			}
//		}
//		else
//		{
//			// no size yet, just adopt the given spec sizes
//		}
//		setMeasuredDimension(width, height);
//	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
	{
		// Will resize the view if the video dimensions have been found.
		// video dimensions are found after onPrepared has been called by
		// MediaPlayer
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		if ((mVideoWidth > 0) && (mVideoHeight > 0))
		{
			if ((mVideoWidth * height) > (width * mVideoHeight))
			{
				Log.d(TAG, "Video too tall, change size.");
				height = (width * mVideoHeight) / mVideoWidth;
			}
			else if ((mVideoWidth * height) < (width * mVideoHeight))
			{
				Log.d(TAG, "Video too wide, change size.");
				width = (height * mVideoWidth) / mVideoHeight;
			}
			else
			{
				Log.d(TAG, "Aspect ratio is correct.");
			}
		}
		setMeasuredDimension(width, height);
	}

	//	private void updateTextureViewSize()
//	{
//		float viewWidth = getWidth();
//		float viewHeight = getHeight();
//
//		float scaleX = 1.0f;
//		float scaleY = 1.0f;
//
//		if (mVideoWidth > viewWidth && mVideoHeight > viewHeight)
//		{
//			scaleX = mVideoWidth / viewWidth;
//			scaleY = mVideoHeight / viewHeight;
//		}
//		else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight)
//		{
//			scaleY = viewWidth / mVideoWidth;
//			scaleX = viewHeight / mVideoHeight;
//		}
//		else if (viewWidth > mVideoWidth)
//		{
//			scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
//		}
//		else if (viewHeight > mVideoHeight)
//		{
//			scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
//		}
//
//		// Calculate pivot points, in our case crop from center
//		int pivotPointX;
//		int pivotPointY;
//
//		pivotPointX = (int) (viewWidth / 2);
//		pivotPointY = (int) (viewHeight / 2);
//
//		Matrix matrix = new Matrix();
//		matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);
//
//		setTransform(matrix);
//	}

	public void setVideoPath(String path)
	{
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri)
	{
		mUri = uri;
		mSeekWhenPrepared = 0;

		openVideo();

		requestLayout();
		invalidate();
	}

	public void openVideo()
	{
		if ((mUri == null) || (mSurface == null))
		{
			Log.d(TAG, "Cannot open video, uri or surface texture is null.");
			return;
		}

		release(false);

//		Intent i = new Intent("com.android.music.musicservicecommand");
//		i.putExtra("command", "pause");
//		getContext().sendBroadcast(i);

		AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		try
		{
			mMediaPlayer = new MediaPlayer();
			if (mAudioSession != 0)
			{
				mMediaPlayer.setAudioSessionId(mAudioSession);
			}
			else
			{
				mAudioSession = mMediaPlayer.getAudioSessionId();
			}
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnCompletionListener(mCompleteListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

			mMediaPlayer.setDataSource(getContext(), mUri);
			mMediaPlayer.setSurface(mSurface);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();

			mCurrentBufferPercentage = 0;
			mCurrentState = STATE_PREPARING;
		}
		catch (IllegalStateException e)
		{
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
		}
		catch (IOException e)
		{
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
		}
	}

	SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener()
	{
		@Override
		public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height)
		{
			Log.d(TAG, "onSurfaceTextureAvailable.");
			mSurface = new Surface(surface);
			openVideo();
		}

		@Override
		public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height)
		{
			Log.d(TAG, "onSurfaceTextureSizeChanged: " + width + '/' + height);
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = (mVideoWidth == width && mVideoHeight == height);
			if (mMediaPlayer != null && isValidState && hasValidSize)
			{
				if (mSeekWhenPrepared != 0)
				{
					seekTo(mSeekWhenPrepared);
				}
				start();
			}
		}

		@Override
		public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface)
		{
			if (mSurface != null)
			{
				mSurface.release();
				mSurface = null;
			}

			if (mOnMediaPlayerReleased != null)
				mOnMediaPlayerReleased.onMediaPlayerReleasedRelease();

			release(true);

			return true;
		}

		@Override
		public void onSurfaceTextureUpdated(final SurfaceTexture surface)
		{

		}
	};

	private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener()
	{
		@Override
		public void onPrepared(final MediaPlayer mp)
		{
			mCurrentState = STATE_PREPARED;

			if (mOnPreparedListener != null)
			{
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}

			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

//			invalidate();

			if ((mVideoWidth != 0) && (mVideoHeight != 0))
			{
				getSurfaceTexture().setDefaultBufferSize(mVideoWidth, mVideoHeight);
				requestLayout();

				if (mTargetState == STATE_PLAYING)
				{
					start();
				}
			}
			else
			{
				if (mTargetState == STATE_PLAYING)
				{
					start();
				}
			}
		}
	};
	private OnCompletionListener mCompleteListener = new OnCompletionListener()
	{
		@Override
		public void onCompletion(final MediaPlayer mp)
		{
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;

//			mSurface.release();

			if (mOnCompletionListener != null)
			{
				mOnCompletionListener.onCompletion(mp);
			}
		}
	};
	private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener()
	{
		public boolean onInfo(MediaPlayer mp, int arg1, int arg2)
		{
			if (mOnInfoListener != null)
			{
				mOnInfoListener.onInfo(mp, arg1, arg2);
			}
			return true;
		}
	};
	private OnErrorListener mErrorListener = new OnErrorListener()
	{
		@Override
		public boolean onError(final MediaPlayer mp, final int framework_err, final int impl_err)
		{
			Log.d(TAG, "Error: " + framework_err + "," + impl_err);
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;

			if (mOnErrorListener != null)
			{
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
				{
					return true;
				}
			}

			/*
			 * Otherwise, pop up an error dialog so the user knows that
			 * something bad has happened. Only try and pop up the dialog if
			 * we're attached to a window. When we're going away and no longer
			 * have a window, don't bother showing the user an error.
			 */
//			if (getWindowToken() != null)
//			{
//				new AlertDialog.Builder(mContext).setMessage("Error: " + what + "," + extra).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						/*
//						 * If we get here, there is no onError listener, so at
//						 * least inform them that the video is over.
//						 */
//						if (mOnCompletionListener != null) {
//							mOnCompletionListener.onCompletion(mMediaPlayer);
//						}
//					}
//				}).setCancelable(false).show();
//			}

			return true;
		}
	};
	private MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener()
	{
		@Override
		public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height)
		{
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mVideoWidth != 0 && mVideoHeight != 0)
			{
				getSurfaceTexture().setDefaultBufferSize(mVideoWidth, mVideoHeight);
				requestLayout();
			}
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener()
	{
		@Override
		public void onBufferingUpdate(final MediaPlayer mp, final int percent)
		{
			mCurrentBufferPercentage = percent;
		}
	};

	void release(boolean cleartargetstate)
	{
		Log.d(TAG, "Releasing media player.");
		if (mMediaPlayer != null)
		{
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;

			mCurrentState = STATE_IDLE;

			if (cleartargetstate)
			{
				mTargetState = STATE_IDLE;
			}

			AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			am.abandonAudioFocus(null);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (mOnMediaPlayerReleased != null)
		{
			mOnMediaPlayerReleased.onClick();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
				&& keyCode != KeyEvent.KEYCODE_VOLUME_MUTE
				&& keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL
				&& keyCode != KeyEvent.KEYCODE_ENDCALL;

		if (isInPlaybackState() && isKeyCodeSupported)
		{
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
			{
				if (mMediaPlayer.isPlaying())
				{
					pause();
				}
				else
				{
					start();
				}
				return true;
			}
			else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY)
			{
				if (!mMediaPlayer.isPlaying())
				{
					start();
				}
				return true;
			}
			else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE)
			{
				if (mMediaPlayer.isPlaying())
				{
					pause();
				}
				return true;
			}
			else
			{
//				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	public void start()
	{
		// This can potentially be called at several points, it will go through
		// when all conditions are ready
		// 1. When setting the video URI
		// 2. When the surface becomes available
		// 3. From the activity
		if (isInPlaybackState())
		{
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		else
		{
			Log.d(TAG, "Could not start. Current state " + mCurrentState);
		}
		mTargetState = STATE_PLAYING;
	}

	public void resume()
	{
		openVideo();
	}

	public void pause()
	{
		if (isInPlaybackState())
		{
			if (mMediaPlayer.isPlaying())
			{
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	public void stopPlayback()
	{
		if (mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;

			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;

			AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			am.abandonAudioFocus(null);
		}
	}

	public int getDuration()
	{
		if (isInPlaybackState())
		{
			return mMediaPlayer.getDuration();
		}

		return -1;
	}

	public int getCurrentPosition()
	{
		if (isInPlaybackState())
		{
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public void seekTo(int msec)
	{
		if (isInPlaybackState())
		{
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		}
		else
		{
			mSeekWhenPrepared = msec;
		}
	}

	public boolean isPlaying()
	{
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	public int getBufferPercentage()
	{
		if (mMediaPlayer != null)
		{
			return mCurrentBufferPercentage;
		}
		return 0;
	}

	private boolean isInPlaybackState()
	{
		return ((mMediaPlayer != null) && (mCurrentState != STATE_ERROR) && (mCurrentState != STATE_IDLE) && (mCurrentState != STATE_PREPARING));
	}

	/**
	 * Register a callback to be invoked when the media file is loaded and ready
	 * to go.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
	{
		mOnPreparedListener = l;
	}

	/**
	 * Register a callback to be invoked when the end of a media file has been
	 * reached during playback.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnCompletionListener(OnCompletionListener l)
	{
		mOnCompletionListener = l;
	}

	/**
	 * Register a callback to be invoked when an informational event occurs
	 * during playback or setup.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnInfoListener(OnInfoListener l)
	{
		mOnInfoListener = l;
	}

	/**
	 * Register a callback to be invoked when an error occurs during playback or
	 * setup. If no listener is specified, or if the listener returned false,
	 * VideoView will inform the user of any errors.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnErrorListener(OnErrorListener l)
	{
		mOnErrorListener = l;
	}

	/**
	 * Register a callback to be invoked when
	 *
	 * @param l The callback that will be run
	 */
	public void setOnMediaPlayerReleasedRelease(OnMediaPlayerReleased l)
	{
		mOnMediaPlayerReleased = l;
	}

	public static interface OnMediaPlayerReleased
	{
		public void onMediaPlayerReleasedRelease();

		public void onClick();
	}
}
