package com.mlt.dtc.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.mlt.dtc.R;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.interfaces.MainVideoBannerListener;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.AUDIO_SERVICE;
import static com.mlt.dtc.utility.Constant.TAG;
import static com.mlt.dtc.utility.Constant.multimediaPath;

public
class MainBannerVideoFragment extends Fragment {

    Fragment mFragment;
    SeekBar volumeBar, sb_Brightness;
    int volumeLevel;
    AudioManager audioManager;
    ImageView volumnBtn, iv_fullScreen, iv_brightness_Button;
    VideoView video_view;
    WindowManager mWindowManager;
    RelativeLayout relativeLayout, rl_Video_Brightness_Box;
    TextView tv_Video_Brightness;
    static private MainVideoBannerListener mMethodCallBack;
    private String brightness = "-1";
    int int_brightness;
    public int percent;
    private Runnable runnable = new Runnable(){
        public void run() {
            try {
                volumeBar.setVisibility(View.GONE);
                sb_Brightness.setVisibility(View.GONE);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    WindowManager.LayoutParams layoutpars;
    //Window object, that will store a reference to the current window
    private Window window;
    int j = 0; //for video loop usage


    public MainBannerVideoFragment() {
        // Required empty public constructor
    }

    public static MainBannerVideoFragment newInstance() {
        return new MainBannerVideoFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mainbannervideofragment, container, false);

        video_view = view.findViewById(R.id.videoviewmainbanner);
        relativeLayout = view.findViewById(R.id.rl_videoviewmainbanner);
        rl_Video_Brightness_Box = view.findViewById(R.id.rl_video_brightness_box);
        tv_Video_Brightness = view.findViewById(R.id.tv_video_brightness);

        //For volume
        volumeBar = view.findViewById(R.id.sbVolume);
        sb_Brightness = view.findViewById(R.id.sbBrightness);
        audioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumnBtn = view.findViewById(R.id.volumnBtn);
        iv_brightness_Button = view.findViewById(R.id.iv_brightness_button);
        iv_fullScreen = view.findViewById(R.id.iv_fullscreen);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);


        try {

            //Set Volume and brightness
            VolumeControl();
            BrightnessControl();

            volumeBar.setVisibility(View.GONE);
            sb_Brightness.setVisibility(View.GONE);
        } catch (Exception e) {

        }

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        try {

            if (multimediaPath!=null){
                File directory = new File(multimediaPath+"/mainvideo/");

                File[] files = directory.listFiles();
                ArrayList<File> mainVideoList=new ArrayList<>();
                if(files!=null){
                    for (File mainVideoFile :files){
                        mainVideoList.add(mainVideoFile);
                    }

                }
                Log.d(TAG, "mainVideoList: "+mainVideoList);

                if (files!=null){


                    try{
                        video_view.setVideoURI(Uri.fromFile(mainVideoList.get(0)));
                        video_view.requestFocus();
                        video_view.start();
                        video_view.setMediaController(null);


                        video_view.setOnCompletionListener(mp -> {
                            if (files.length > 1) {
                                j++;
                                if(j>mainVideoList.size()-1){
                                    j = 0;
                                }
                                video_view.setVideoURI(Uri.fromFile(mainVideoList.get(j)));
                                if (j == (files.length - 1)) {
                                    j = -1;
                                }
                                video_view.requestFocus();
                                video_view.start();

                            } else {
                                video_view.requestFocus();
                                video_view.start();

                            }
                        });
                        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                Log.d("video", "setOnErrorListener ");
                                return true;
                            }
                        });
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        /*Video view full screen and send the views as parameters to contorl the brightness when its full screen*/
        iv_fullScreen.setOnClickListener(v -> {
            if (mMethodCallBack != null) {
                mMethodCallBack.MainVideoBannerallBackMethod(iv_fullScreen, rl_Video_Brightness_Box, tv_Video_Brightness, R.id.rl_videoviewmainbanner, video_view);
            }
        });

//        iv_Plus.setOnClickListener(v -> volumeBar.setProgress(volumeBar.getProgress() + 4));

//        iv_Minus.setOnClickListener(v -> volumeBar.setProgress(volumeBar.getProgress() - 4));

//        iv_brightness_Plus.setOnClickListener(v -> {
//            sb_Brightness.setProgress(sb_Brightness.getProgress() + 20);
//            updateBrightness();
//        });

//        iv_brightness_Minus.setOnClickListener(v -> {
//            sb_Brightness.setProgress(sb_Brightness.getProgress() - 20);
//            updateBrightness();
//        });


    }





    public void addFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, mFragment)
                .addToBackStack(null)
                .commit();
    }


    public void setMethodCallBack(MainVideoBannerListener CallBack) {
        mMethodCallBack = CallBack;
    }


    private void VolumeControl() {
        //set volume default to 50
        volumeBar.setProgress(volumeBar.getProgress() + 40);


        if (volumeLevel == 0) {
            volumnBtn.setBackgroundResource(R.drawable.ic_volume_off_black_36dp);
        } else {
            volumnBtn.setBackgroundResource(R.drawable.ic_volume_up_black_36dp);
        }

        volumnBtn.setOnClickListener(view -> {
            volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volumeBar.setProgress(volumeLevel);
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 4000);
            if (volumeBar.getVisibility() == View.GONE) {
                volumeBar.setVisibility(View.VISIBLE);
            } else {
                volumeBar.setVisibility(View.GONE);
            }
        });

        volumeBar.setProgress(volumeLevel);

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volumeLevel = i;
                if (volumeLevel == 0) {
                    volumnBtn.setBackgroundResource(R.drawable.ic_volume_off_black_36dp);
                } else {
                    volumnBtn.setBackgroundResource(R.drawable.ic_volume_up_black_36dp);
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeLevel, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                volumeBar.setVisibility(View.GONE);

                if (volumeLevel == 0) {
                    volumnBtn.setBackgroundResource(R.drawable.ic_volume_off_black_36dp);
                } else {
                    volumnBtn.setBackgroundResource(R.drawable.ic_volume_up_black_36dp);
                }
            }
        });
    }

    private void BrightnessControl() {
        //set brightness default to 50
        sb_Brightness.setProgress(sb_Brightness.getProgress() + 100);
        float f_brightness = Float.parseFloat(brightness);
        if (f_brightness < 0) {
            f_brightness = getActivity().getWindow().getAttributes().screenBrightness;
            if (f_brightness <= 0.00f) {
                f_brightness = 0.50f;
            } else if (f_brightness < 0.01f) {
                f_brightness = 0.01f;
            }
        }
        if (percent == 0) {
            iv_brightness_Button.setBackgroundResource(R.drawable.ic_brightness_low_black_36dp);
        } else {
            iv_brightness_Button.setBackgroundResource(R.drawable.ic_brightness_high_black_36dp);
        }

        //Get the content resolver
        //Content resolver used as a handle to the system's settings
        ContentResolver cResolver = getActivity().getContentResolver();

        //Get the current window
        window = getActivity().getWindow();

        //Set the seekbar range between 0 and 255
        sb_Brightness.setMax(255);
        //Set the seek bar progress to 1
        sb_Brightness.setKeyProgressIncrement(1);

        //Set the progress of the seek bar based on the system's brightness
        brightness = PreferenceConnector.readString(getContext(), "progresscolor", "-1");
        sb_Brightness.setProgress(Integer.parseInt(brightness));

        iv_brightness_Button.setOnClickListener(view -> {
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 4000);
            if (sb_Brightness.getVisibility() == View.GONE) {
                sb_Brightness.setVisibility(View.VISIBLE);
            } else {
                sb_Brightness.setVisibility(View.GONE);
            }
        });

        //Set the screen brightness
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        //set the brightness by changing the floating value
        String br = PreferenceConnector.readString(getContext(), "br", "");
        if (br != null || !br.equals(null))
            lp.screenBrightness = Float.parseFloat(br);
        else
            lp.screenBrightness = Float.parseFloat(br);
        getActivity().getWindow().setAttributes(lp);

        //sb_Brightness.setProgress(volumeLevel);
        sb_Brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {


                int_brightness = Integer.parseInt(brightness);

                //Set the minimal brightness level
                //if seek bar is 20 or any value below
                if (progress <= 20) {
                    //Set the brightness to 20
                    int_brightness = 20;
                } else //brightness is greater than 20
                {
                    //Set brightness variable based on the progress bar
                    int_brightness = progress;
                }
                //Calculate the brightness percentage
                float perc = (int_brightness / (float) 255) * 100;
                updateBrightness();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //Update the brightness
                updateBrightness();

                sb_Brightness.setVisibility(View.GONE);

                if (percent == 0) {
                    iv_brightness_Button.setBackgroundResource(R.drawable.ic_brightness_low_black_36dp);
                } else {
                    iv_brightness_Button.setBackgroundResource(R.drawable.ic_brightness_high_black_36dp);
                }

            }
        });
    }

    private void updateBrightness() {
        try {
            //Set the system brightness using the brightness variable value
            layoutpars = window.getAttributes();
            //Set the brightness of this window
            layoutpars.screenBrightness = int_brightness / (float) 255;
            getActivity().getWindow().setAttributes(layoutpars);

            PreferenceConnector.writeString(getContext(), "br", String.valueOf(layoutpars.screenBrightness));
            PreferenceConnector.writeString(getContext(), "progresscolor", String.valueOf(int_brightness));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        video_view.pause();
        int progress = video_view.getCurrentPosition();

        if(progress!=0){
            PreferenceConnector.writeInteger(getContext(),"PauseTime",progress);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int progress = PreferenceConnector.readInteger(getContext(),"PauseTime",0);
        video_view.seekTo(progress);
    }



    @Override
    public void onStop() {
        super.onStop();
        relativeLayout = null;
        rl_Video_Brightness_Box = null;
        layoutpars = null;
        audioManager = null;

        video_view = null;
        iv_fullScreen.setImageDrawable(null);
        iv_fullScreen.setOnClickListener(null);
        iv_brightness_Button.setOnClickListener(null);
        tv_Video_Brightness.setText(null);
        tv_Video_Brightness = null;
        iv_fullScreen = null;
        iv_brightness_Button = null;
        volumnBtn = null;
        sb_Brightness = null;

        volumeBar = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            relativeLayout = null;
            rl_Video_Brightness_Box = null;
            layoutpars = null;
            audioManager = null;

            video_view = null;
            iv_fullScreen.setImageDrawable(null);
            iv_fullScreen.setOnClickListener(null);
            iv_brightness_Button.setOnClickListener(null);
            tv_Video_Brightness.setText(null);
            tv_Video_Brightness = null;
            iv_fullScreen = null;
            iv_brightness_Button = null;
            volumnBtn = null;
            sb_Brightness = null;

            volumeBar = null;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
