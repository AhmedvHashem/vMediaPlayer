package com.vivantor.examples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.vivantor.mediaplayer.MediaFile;

import java.util.ArrayList;
import java.util.List;

public class MultiVideoActivity extends AppCompatActivity {
    ListView listView;
    MultiVideoAdapter adapter;
    List<MediaFile> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MultiVideoAdapter(this, getSampleData());
        listView.setAdapter(adapter);
    }

    List<MediaFile> getSampleData() {
        items = new ArrayList<>();

        items.add(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));
        items.add(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));
        items.add(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));
        items.add(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));
        items.add(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));
        items.add(new MediaFile("android.resource://" + getPackageName() + "/" + R.raw.video));

//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/1.mp3"));
//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/2.mp3"));
//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/3.mp3"));
//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/4.mp3"));
//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/5.mp3"));
//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/6.mp3"));
//		items.add(new MediaFile("/storage/emulated/0/Download/MediaFiles/7.mp3"));

        return items;
    }
}
