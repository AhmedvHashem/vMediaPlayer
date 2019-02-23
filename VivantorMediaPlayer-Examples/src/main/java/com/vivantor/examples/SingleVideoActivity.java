package com.vivantor.examples;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.vivantor.mediaplayer.MediaFile;
import com.vivantor.mediaplayer.UI.VVideoPlayer;
import com.vivantor.mediaplayer.Utils;

public class SingleVideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video);
        ImageView videoThumbnail = findViewById(R.id.videoThumbnail);
        VVideoPlayer videoPlayer = findViewById(R.id.videoPlayer);

//        String url = "android.resource://" + getPackageName() + "/" + R.raw.video;
        String url = "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4";

//        Bitmap bitmap = null;
//        try {
//            bitmap = Utils.retriveVideoFrameFromVideo(url);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        if (bitmap != null) {
//            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false);
//            videoThumbnail.setImageBitmap(bitmap);
//        }

        videoPlayer.setMediaFile(new MediaFile(url));
    }
}
