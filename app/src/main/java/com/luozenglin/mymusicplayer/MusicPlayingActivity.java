package com.luozenglin.mymusicplayer;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.luozenglin.common.Utils;
import com.luozenglin.playmusic.PlayMusicService;

public class MusicPlayingActivity extends AppCompatActivity {
    private PlayMusicService.PlayMusicBinder playMusicBinder;

    private ServiceConnection connection;
    private SeekBar seekBar;
    private TextView musicNameTV;
    private TextView singerTV;
    private Button backBtn;
    private ImageView lastMusicIV;
    private ImageView startOrPauseIV;
    private ImageView nextMusicIV;
    private ImageView image;
    private ImageView playLoopImg;
    private TextView musicCurrentTime;
    private TextView musicMaxTime;
    private int musicLength;
    private String path;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initLayout();
        if(connection==null){
            bindService();
        }
    }

    public void initLayout() {
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);
        backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicPlayingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        lastMusicIV = (ImageView) findViewById(R.id.music_prev_imgv);
        startOrPauseIV = (ImageView) findViewById(R.id.music_pause_imgv);
        nextMusicIV = (ImageView) findViewById(R.id.music_next_imgv);
        musicNameTV = (TextView) findViewById(R.id.music_title_tv);
        singerTV = (TextView) findViewById(R.id.music_artist_tv);
        musicCurrentTime = (TextView) findViewById(R.id.music_current_tv);
        musicMaxTime = (TextView) findViewById(R.id.music_total_tv);
        musicMaxTime.setText(Utils.parseTime(Integer.parseInt(getIntent().getStringExtra("length"))));
        musicNameTV.setText(getIntent().getStringExtra("musicName"));
        singerTV.setText(getIntent().getStringExtra("singer"));
        path = getIntent().getStringExtra("path");
        position = Integer.parseInt(getIntent().getStringExtra("position"));
        image = (ImageView) findViewById(R.id.music_disc_imagv);
        playLoopImg = (ImageView) findViewById(R.id.music_play_btn_loop_img);
        playLoopImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicBinder.setPlayMode();
                if(playMusicBinder.getPlayMode()==Utils.PlayMode.SINGE_LOOP){
                    playLoopImg.setImageResource(R.drawable.ic_play_btn_one);
                }else if(playMusicBinder.getPlayMode() == Utils.PlayMode.RANDOM_PLAY){
                    playLoopImg.setImageResource(R.drawable.ic_play_btn_shuffle);
                }else{
                    playLoopImg.setImageResource(R.drawable.ic_play_btn_loop);
                }
            }
        });
        seekBar.setMax(Integer.parseInt(getIntent().getStringExtra("length")));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playMusicBinder.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        lastMusicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicBinder.playLastMusic();
                refreshLayout(playMusicBinder.getMusicName(), playMusicBinder.getSinger(),
                        playMusicBinder.getMusicLength());
            }
        });
        nextMusicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicBinder.playNextMusic();
                refreshLayout(playMusicBinder.getMusicName(), playMusicBinder.getSinger(),
                        playMusicBinder.getMusicLength());
            }
        });
        startOrPauseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicBinder.startOrPause(path, position);
                updateProgress();

            }
        });
    }

    protected void bindService(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                playMusicBinder = (PlayMusicService.PlayMusicBinder) service;
                Log.i("MusicPlayingActivity", "playMusicBinder end:" + playMusicBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(this, PlayMusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        Log.i("MusicPlayingActivity", "bind service successfully." + connection);

    }

    public void refreshLayout(String musicName, String singer, int musicLength) {
        updateProgress();
        musicNameTV.setText(musicName);
        singerTV.setText(singer);
        this.musicLength = musicLength;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(!musicNameTV.getText().equals(playMusicBinder.getMusicName())){
                startToPlay();
            }
            int progress = playMusicBinder.getProgress();
            seekBar.setProgress(progress);
            musicCurrentTime.setText(Utils.parseTime(progress));
            updateProgress();
            return true;
        }
    });


    private void updateProgress() {
        Message msg = Message.obtain();
        handler.sendMessageDelayed(msg, 1000);
    }

    private void startToPlay() {
        musicNameTV.setText(playMusicBinder.getMusicName());
        singerTV.setText(playMusicBinder.getSinger());
        musicCurrentTime.setText(Utils.parseTime(0));
        musicMaxTime.setText(Utils.parseTime(playMusicBinder.getMusicLength()));
        seekBar.setProgress(0);
        seekBar.setMax(playMusicBinder.getMusicLength());
        updateProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
