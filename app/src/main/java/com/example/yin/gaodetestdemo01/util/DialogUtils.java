package com.example.yin.gaodetestdemo01.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Yin on 2016/4/12.
 */
public class DialogUtils {

    private static ProgressDialog progDialog;


    /**
     * 显示进度框
     */
    public static void showProgressDialog(Context context, String message, String keyWord) {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage(message + "\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    public static void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
