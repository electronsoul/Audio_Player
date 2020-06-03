package com.example.audioplayer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ImageView record;
    MediaPlayer song;
    Anim_Thread A;
    SeekBar seekBar;
    TextView duration;

    //For Audio
    //AudioManager audioManager;
    //protected int maxVolume = 0, currentVolume = 0;

    //Play Button On onClick
    public void play(View view) {
        song.start();
        A.start();
        setDuration();
    }

    //Pause Button on onClick
    public void pause(View view) {
        song.pause();
    }

    //Stop Button onClick
    @SuppressLint("SetTextI18n")
    public void stop(View view) {
        song.stop();
        song.release();
        song = null;
        duration.setText("00:00");
    }

    //onConfigurationChange Method
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressLint("SetTextI18n")
    protected void setDuration(){
        //To Display Song Duration
        if ((float) song.getDuration() / 3600000 >= 1) {


            duration.setText(
                    Integer.toString((int) ((float) song.getDuration() / 3600000))
                            + ":" +
                            Integer.toString((int) ((float) song.getDuration() / 60000))
                            + ":" +
                            Integer.toString((int) ((float) song.getDuration() / 1000))
            );
        }
        else {
            duration.setText(
                    Integer.toString((int) ((float) song.getDuration() / 60000))
                            + ":" +
                            Integer.toString((int) ((float) song.getDuration() / 1000))
            );
        }
    }

    //onCreate Method
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams.FLAG_FULLSCREEN); //For Full Screen And Navigation Bar Translucent
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); == To Clear Flags
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.activity_color));

        //Instantiations
        record = (ImageView) findViewById(R.id.record); //Record Is Rotating Image
        song = MediaPlayer.create(this, R.raw.song); //Current Song
        duration = (TextView) findViewById(R.id.duration); //TextView To Show Duration Of Song
        seekBar = (SeekBar) findViewById(R.id.sweep); //SeekBar To Show Progress of Song

        //For Audio
        //audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        seekBar.setMax(song.getDuration()); //Setting SeekBar Max Value
        A = new Anim_Thread("RecordRotate",record); //Instantiate Animation Thread
        A.start();

        //For Volume Change
        //maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //seekBar.setMax(maxVolume);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                song.seekTo(progress, MediaPlayer.SEEK_CLOSEST_SYNC);

                //For Changing Volume
                //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //For Cleaner Seek
                //song.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //For Cleaner Seek
                //song.start();
            }
        });
    }
}

class Anim_Thread extends Thread{

    //Variables
    private Thread t;
    private String threadname;
    ImageView img;

    Anim_Thread(String name, ImageView i) //Class Constructor
    {
        threadname = name;
        img = i;
            }

    public void run() //Start Thread
    {
        try
        {
            for (float angle = 0; ; angle++) {
                if (angle == 360) {
                    angle = 0;
                }
                img.animate().rotation(angle);
            TimeUnit.MILLISECONDS.sleep(1000);
            Thread.sleep(1);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void start() //Set Thread
    {
        if( t == null)
        {
            t = new Thread(this,threadname);
            t.start();
        }
    }
}
