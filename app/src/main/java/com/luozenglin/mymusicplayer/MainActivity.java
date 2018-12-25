package com.luozenglin.mymusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.luozenglin.common.MusicItem;
import com.luozenglin.common.MusicListAdapter;
import com.luozenglin.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView musicListView;
    private List<MusicItem> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicListView = (ListView) findViewById(R.id.music_listView);
        musicList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            initListView();
        }
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusic(position);
            }
        });
    }

    protected void initListView(){
        musicList = Utils.getMusicList(this);
        MusicListAdapter adapter = new MusicListAdapter(MainActivity.this,
                R.layout.music_item, musicList);
        musicListView.setAdapter(adapter);
    }

    protected void playMusic(int position){
        Intent intent = new Intent(this,MusicPlayingActivity.class);
        MusicItem item = musicList.get(position);
        intent.putExtra("musicName",item.getTitle());
        intent.putExtra("singer",item.getSinger());
        intent.putExtra("length",String.valueOf(item.getLength()));
        intent.putExtra("path",item.getPath());
        intent.putExtra("position",String.valueOf(position));
        Log.i("MainActivity","start to playing music: "+item.getTitle());
        startActivityForResult(intent,position);
    }

}
