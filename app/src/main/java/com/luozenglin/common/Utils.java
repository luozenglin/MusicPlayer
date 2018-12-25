package com.luozenglin.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public static List<MusicItem> getMusicList(Context context){
        List<MusicItem> musicList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                MusicItem musicItem = new MusicItem();
                musicItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                musicItem.setSinger(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                musicItem.setLength(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                musicItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                if(musicItem.getLength()>60000){
                    musicList.add(musicItem);
                }
            }while (cursor.moveToNext());
            cursor.close();
        }
        return musicList;
    }


    public static String parseTime(int oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }

    public enum PlayMode {
        SINGE_LOOP,
        ORDER_PLAY,
        RANDOM_PLAY
    }
}
