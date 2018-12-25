package com.luozenglin.playmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.luozenglin.common.MusicItem;
import com.luozenglin.common.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayMusicService extends Service {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private PlayMusicBinder playMusicBinder = new PlayMusicBinder();
    private List<MusicItem> musicList;
    private List<Integer> playedMusicIndex;
    private String playingMusicPath;
    private int playingMusicPosition;
    private Utils.PlayMode playMode = Utils.PlayMode.ORDER_PLAY;

    public class PlayMusicBinder extends Binder {

        public int getProgress() {
            return mediaPlayer.getCurrentPosition();
        }

        public int getMusicLength() {
            return mediaPlayer.getDuration();
        }

        public String getMusicName() {
            return musicList.get(playingMusicPosition).getTitle();
        }

        public String getSinger() {
            return musicList.get(playingMusicPosition).getSinger();
        }

        public void setProgress(int time) {
            mediaPlayer.seekTo(time);
        }

        public Utils.PlayMode getPlayMode() {
            return playMode;
        }

        public Utils.PlayMode setPlayMode() {
            if (playMode == Utils.PlayMode.SINGE_LOOP) {
                playMode = Utils.PlayMode.ORDER_PLAY;
            } else if (playMode == Utils.PlayMode.ORDER_PLAY) {
                playMode = Utils.PlayMode.RANDOM_PLAY;
            } else {
                playMode = Utils.PlayMode.SINGE_LOOP;
            }
            return playMode;
        }

        public void startOrPause(String path, int position) {
            Log.i("PlayMusicService", "playingMusicPath:" + playingMusicPath);
            if (playingMusicPath == null || (playingMusicPath != null && !playingMusicPath.equals(path))) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    playingMusicPosition = position;
                    playingMusicPath = path;
                    playedMusicIndex.add(position);
                    Log.i("PlayMusicService", "play a new music." + "playingMusicPath:");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Log.i("PlayMusicService", "paused music.");
            } else {
                mediaPlayer.start();
                Log.i("PlayMusicService", "start music from paused.");
            }
        }

        public void playLastMusic() {
            Log.i("PlayMusicService","play last music."+" list:"+playedMusicIndex.toString());
            if (playedMusicIndex.size() > 0) {
                playingMusicPosition = playedMusicIndex.remove(playedMusicIndex.size() - 1);
                play();
                Log.i("PlayMusicService","play last music:"+playingMusicPosition);
            } else if (playingMusicPosition == 0) {
                playingMusicPosition = musicList.size() - 1;
            } else {
                playingMusicPosition--;
            }
            play();
        }

        public void playNextMusic() {
            Log.i("PlayMusicService","play next music.");
            if (playMode == Utils.PlayMode.SINGE_LOOP) {
                play();
            } else if (playMode == Utils.PlayMode.ORDER_PLAY) {
                if (playingMusicPosition == musicList.size() - 1) {
                    playingMusicPosition = 0;
                } else {
                    playingMusicPosition++;
                }
                play();
            } else {
                playingMusicPosition = ((int) (Math.random() * musicList.size()));
                play();
            }
            playedMusicIndex.add(playingMusicPosition);
        }

        private void play() {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(musicList.get(playingMusicPosition).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public PlayMusicService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return playMusicBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicList = Utils.getMusicList(this);
        playedMusicIndex = new ArrayList<>();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playMusicBinder.playNextMusic();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
