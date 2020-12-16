package com.mlt.e200cp.interfaces;

import android.content.Context;

/**
 * Created by simon_chen on 2018/2/14.
 */

public interface PresenterInterface {
    void initPresenter(Context context, ViewInterface viewInterface);
    void onDestroy();
    void onAction(String action);
}
