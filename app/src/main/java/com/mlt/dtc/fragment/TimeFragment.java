package com.mlt.dtc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mlt.dtc.R;
import com.mlt.dtc.activity.MainActivity;
import com.mlt.dtc.common.PreferenceConnector;
import com.mlt.dtc.utility.Constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.mlt.dtc.common.Common.getDateHome;

public
class TimeFragment extends DialogFragment {

    private AlertDialog dialog;
    Context context;
    TextView tv_currentTime, tv_currentDate;
    String ClassName;
    ImageView iv_close;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder customDialogMain = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.CustomDialog);
        context = getContext();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.timefragment, null);
        iv_close = view.findViewById(R.id.iv_closefarefragment);
        tv_currentTime = view.findViewById(R.id.currenttime_timefrg);
        tv_currentDate = view.findViewById(R.id.currentdate_timefrg);

        PreferenceConnector.RemoveItem(getContext(), Constant.Language);



        //get Class Name
        ClassName = getClass().getCanonicalName();

        customDialogMain.setView(view);
        dialog = customDialogMain.show();
        dialog.setCanceledOnTouchOutside(false);



        return dialog;
    }

    public void DateTimeRunning(TextView tv_Time) {
        final Handler someHandler = new Handler(getActivity().getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("hh.mm:ss aa");
                String dateString = dateFormat.format(new Date()).toString();
                tv_Time.setText(dateString);
//                tv_Time.setText(new SimpleDateFormat("HH:mm:ss a", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            DateTimeRunning(tv_currentTime);
            tv_currentDate.setText(getDateHome());
        } catch (Exception e) {

        }
        iv_close.setOnClickListener(v -> {
            MainActivity.mainActivity.onFinished("finish");
            try {
                dialog.dismiss();
            } catch (Exception e) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Window window = getDialog().getWindow();
        window.setLayout(1000, 700);
        window.setGravity(Gravity.CENTER);
    }
}
