package com.vivantor.mediaplayer.UI;

/**
 * Created by AhmedNTS on 2016-02-16.
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vivantor.mediaplayer.Interfaces.VMediaPlayerEvents;
import com.vivantor.mediaplayer.Interfaces.VPlayer;
import com.vivantor.mediaplayer.MediaFile;
import com.vivantor.mediaplayer.R;
import com.vivantor.mediaplayer.Utils;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class VAudioPlayer
        extends
        LinearLayout
        implements
        VPlayer,
        MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {
    private static final String TAG = VAudioPlayer.class.getSimpleName();

    private Handler handler;
    private MediaPlayer mediaPlayer;

    private ToggleButton mediaToggle;
    private SeekBar mediaSeekBar;
    private TextView mediaRemainTime;

    private MediaFile mediaFile;

    VMediaPlayerEvents listener;

    public VAudioPlayer(Context context) {
        super(context);
        init();
    }

    public VAudioPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VAudioPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VAudioPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.view_player_audio, this);

        mediaToggle = (ToggleButton) findViewById(R.id.mediaToggle);
        mediaSeekBar = (SeekBar) findViewById(R.id.mediaSeekBar);
        mediaSeekBar.setMax(100);
        mediaSeekBar.setProgress(0);
        mediaRemainTime = (TextView) findViewById(R.id.mediaRemainTime);
        mediaRemainTime.setText("00:00");

        mediaToggle.setOnCheckedChangeListener(this);
        mediaSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (listener != null)
                listener.OnPlayClicked();
            else
                Start();
        } else {
            if (listener != null)
                listener.OnPauseClicked();
            else
                Pause();
        }
    }

    public void setMediaFile(MediaFile mediaFile) {
        if (mediaFile == null) return;

        if (mediaPlayer != null && isPlaying())
            Stop();

        this.mediaFile = mediaFile;

        mediaToggle.setChecked(false);
        mediaSeekBar.setProgress(Utils.getProgressPercentage(this.mediaFile.currentDuration, this.mediaFile.totalDuration));
        mediaRemainTime.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) this.mediaFile.currentDuration),
                TimeUnit.MILLISECONDS.toSeconds((long) this.mediaFile.currentDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) this.mediaFile.currentDuration))));
    }

    @Override
    public void Start() {
        if (this.mediaFile == null) return;

//		this.mediaFile = mediaFile;

        AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();

            mediaPlayer.reset();
            mediaPlayer.setDataSource(this.mediaFile.filePath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(this);

        if (!isPlaying()) {
            mediaPlayer.seekTo((int) this.mediaFile.currentDuration);
            mediaPlayer.start();
        }

        if (handler == null)
            handler = new Handler();
        handler.postDelayed(UpdateSongTime, 100);
    }

    @Override
    public void Pause() {
        if (mediaPlayer == null) return;

        mediaPlayer.pause();

        mediaToggle.setChecked(false);

        if (handler != null)
            handler.removeCallbacks(UpdateSongTime);

        AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    @Override
    public void Stop() {
        if (mediaPlayer == null) return;

        mediaPlayer.stop();
        mediaPlayer.reset();

        mediaToggle.setChecked(false);
        mediaSeekBar.setProgress(0);
        mediaRemainTime.setText("00:00");

        if (handler != null)
            handler.removeCallbacks(UpdateSongTime);

        AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }

    public boolean isPlaying() {
        if (mediaPlayer != null)
            return mediaPlayer.isPlaying();
        else return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (handler != null)
            handler.removeCallbacks(UpdateSongTime);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mediaPlayer == null) return;

        if (handler != null)
            handler.removeCallbacks(UpdateSongTime);

        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        handler.postDelayed(UpdateSongTime, 100);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
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

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            try {
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
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    };

    public void setOnVPlayerListener(VMediaPlayerEvents listener) {
        this.listener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Dispose();
    }

    public void Dispose() {
        if (handler != null)
            handler.removeCallbacks(UpdateSongTime);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaFile = null;
        listener = null;

        AudioManager am = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);
    }
}
