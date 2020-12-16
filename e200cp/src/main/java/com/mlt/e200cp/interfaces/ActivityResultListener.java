package com.mlt.e200cp.interfaces;

import android.content.Intent;

public interface ActivityResultListener {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}