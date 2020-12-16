package com.mlt.e200cp.utilities.helper.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import com.mlt.e200cp.utilities.helper.listener.ConditionCheck;

public class Dialog {

    int selectedIndex = -1;
    String text = "";
    boolean canceled = false;
    OPTION option = OPTION.CANCEL;

    private void checkToRunOnUiThread(Context ctx, Runnable codeInUiThread) {

        if (Looper.myLooper() == Looper.getMainLooper()){
            codeInUiThread.run();
        } else {
            Handler handler = new Handler(ctx.getMainLooper());
            handler.post(codeInUiThread);
        }
    }

    public int selection(Context ctx, String title, String[] items, int timeout) throws InterruptedException {

        selectedIndex = -1;

        final Object syncObj = new Object();

        checkToRunOnUiThread(ctx, () -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setTitle(title);
            builder.setItems(items, (dialog, which) -> {
                selectedIndex = which;
                synchronized (syncObj) { syncObj.notify(); }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
                synchronized (syncObj) { syncObj.notify(); }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();

            setOnDismiss(dialog, syncObj, timeout);
        });

        synchronized (syncObj) { syncObj.wait(timeout); }

        return selectedIndex;
    }


    public void info(Context ctx, String title, String msg, int timeout) throws InterruptedException {

        final Object syncObj = new Object();

        text = "";
        canceled = false;

        checkToRunOnUiThread(ctx, () -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setTitle(title);

            final EditText input = new EditText(ctx);
            input.setEnabled(false);
            input.setText(msg);
            input.setTypeface(Typeface.MONOSPACE);
            input.setTextColor(Color.BLUE);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                text = input.getText().toString();
                synchronized (syncObj) { syncObj.notify(); }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
            setOnDismiss(dialog, syncObj, timeout);
        });

        synchronized (syncObj) { syncObj.wait(timeout); }

    }

    public int inputNumber(Context ctx, String title, int defaultValue, int timeout, int min, int max) throws InterruptedException {

        String ret = input(ctx, title, Integer.toString(defaultValue), InputType.TYPE_CLASS_NUMBER, timeout, (t) -> {
            try {

                int value = Integer.parseInt(t);
                return (value >= min && value <= max);

            } catch (NumberFormatException ex) { return false; }
        });

        if (!canceled) return Integer.parseInt(ret);
        return -1;
    }

    public String input(Context ctx, String title, String defaultValue, int inputType, int timeout, ConditionCheck conditionCheck) throws InterruptedException {

        final Object syncObj = new Object();

        text = "";
        canceled = false;

        while (true) {

            checkToRunOnUiThread(ctx, () -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setCancelable(false);
                builder.setTitle(title);

                final EditText input = new EditText(ctx);
                input.setInputType(inputType);
                input.setText(defaultValue);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    text = input.getText().toString();
                    synchronized (syncObj) { syncObj.notify(); }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    canceled = true;
                    synchronized (syncObj) { syncObj.notify(); }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                setOnDismiss(dialog, syncObj, timeout);
            });

            synchronized (syncObj) { syncObj.wait(timeout); }

            if (canceled || conditionCheck == null) break;

            if (conditionCheck.run(text))
                return text;

        }

        return text;
    }

    public enum OPTION { CANCEL, YES, NO }

    public OPTION answer(Context ctx, String title, String msg, int timeout) throws InterruptedException {

        final Object syncObj = new Object();

        option = OPTION.CANCEL;

        checkToRunOnUiThread(ctx, () -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setTitle(title);

            final TextView text = new TextView(ctx);
            text.setText(msg);
            builder.setView(text);

            builder.setPositiveButton("YES", (dialog, which) -> {
                option = OPTION.YES;
                canceled = false;
                synchronized (syncObj) { syncObj.notify(); }
            });

            builder.setNegativeButton("NO", (dialog, which) -> {
                dialog.cancel();
                option = OPTION.NO;
                synchronized (syncObj) { syncObj.notify(); }
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
            setOnDismiss(dialog, syncObj, timeout);
        });

        synchronized (syncObj) { syncObj.wait(timeout); }


        return option;
    }

    private void setOnDismiss(AlertDialog dialog, Object syncObj, int timeout) {

        final Handler handler  = new Handler();
        final Runnable runnable = () -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            synchronized (syncObj) { syncObj.notify(); }
        };

        dialog.setOnDismissListener(dialog1 -> handler.removeCallbacks(runnable));

        handler.postDelayed(runnable, timeout);
    }
}
