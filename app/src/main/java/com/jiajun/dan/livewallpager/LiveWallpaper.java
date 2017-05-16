package com.jiajun.dan.livewallpager;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

/**
 * Created by danjj on 2017/5/16.
 */
public class LiveWallpaper extends WallpaperService {

    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.jiajun.dan.livewallpaper";
    public static final String KEY_ACTION = "action";
    public static final int ACTION_VOICE_SILENCE = 110;
    public static final int ACTION_VOICE_NORMAL = 111;

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    public static void voiceSilence(Context context) {
        Intent intent = new Intent(LiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(LiveWallpaper.KEY_ACTION, LiveWallpaper.ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }

    public static void voiceNormal(Context context) {
        Intent intent = new Intent(LiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(LiveWallpaper.KEY_ACTION, LiveWallpaper.ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    public static void setToWallPaper(Context context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, LiveWallpaper.class));
        context.startActivity(intent);
    }

    class LiveEngine extends Engine {
        private MediaPlayer mediaPlayer;
        private BroadcastReceiver broadcastReceiver;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            L.d("VideoEngine#onCreate");

            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);
            registerReceiver(broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    L.d("onReceive");

                    int action = intent.getIntExtra(KEY_ACTION, -1);
                    switch (action) {
                        case ACTION_VOICE_NORMAL:
                            mediaPlayer.setVolume(1.0f, 1.0f);
                            break;
                        case ACTION_VOICE_SILENCE:
                            mediaPlayer.setVolume(0, 0);
                            break;
                    }
                }
            }, intentFilter);
        }

        @Override
        public void onDestroy() {
            L.d("VideoEngine#onDestroy");
            unregisterReceiver(broadcastReceiver);
            super.onDestroy();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            L.d("VideoEngine#onSurfaceCreated ");
            super.onSurfaceCreated(holder);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(holder.getSurface());
            try {
                AssetManager assetManager = getApplicationContext().getAssets();
                AssetFileDescriptor fileDescriptor = assetManager.openFd("test1.mp4");
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            L.d("VideoEngine#onVisibilityChanged visible = " + visible);
            if (visible) {
                mediaPlayer.start();
            } else {
                mediaPlayer.pause();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            L.d("VideoEngine#onSurfaceDestroyed ");
            super.onSurfaceDestroyed(holder);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
