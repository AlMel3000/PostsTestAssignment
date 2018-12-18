package com.live.almel.poststestassignment.utils;


import android.view.View;

import com.google.android.material.snackbar.Snackbar;


public class CustomSnackBar {

    public static void showCustomSnackbar(View view, CharSequence message, String actionName, final View.OnClickListener listener) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(actionName, listener)
                .show();

    }
}