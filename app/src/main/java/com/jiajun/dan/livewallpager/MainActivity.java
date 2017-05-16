package com.jiajun.dan.livewallpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    private CheckBox mCbVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCbVoice = (CheckBox) findViewById(R.id.id_cb_voice);

        mCbVoice.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            // 静音
                            LiveWallpaper.voiceSilence(getApplicationContext());
                        } else {
                            LiveWallpaper.voiceNormal(getApplicationContext());
                        }
                    }
                });
    }

   public  void setVideoToWallPaper(View view) {
        LiveWallpaper.setToWallPaper(this);
    }
}
