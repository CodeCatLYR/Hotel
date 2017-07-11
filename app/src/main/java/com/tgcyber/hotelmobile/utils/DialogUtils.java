package com.tgcyber.hotelmobile.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tgcyber.hotelmobile.R;


public class DialogUtils {

    public static View showProgressDialog(Context context, String msg,
                                          View dialogView) {
        if (context == null) {
            return null;
        }
        boolean addViewFlag = false;
        if (null == dialogView) {
            addViewFlag = true;
            dialogView = LayoutInflater.from(context).inflate(
                    R.layout.dialog_layout, null);
            LinearLayout parentLayout = (LinearLayout) dialogView
                    .findViewById(R.id.parentLayout);
            parentLayout.getBackground().setAlpha(200);
        } else {
            dialogView.setVisibility(View.VISIBLE);
        }

        TextView msgTextView = (TextView) dialogView
                .findViewById(R.id.msgTextView);
        ProgressBar progressBar = (ProgressBar) dialogView
                .findViewById(R.id.progressBar);
        ImageView done = (ImageView) dialogView.findViewById(R.id.done);
        msgTextView.setText(msg);
        progressBar.setVisibility(View.VISIBLE);
        done.setVisibility(View.GONE);
        LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        if (addViewFlag) {
            ((Activity) context).addContentView(dialogView, mLayoutParams);
        }

        return dialogView;
    }

    public static View showProgressDialog(Context context, int res,
                                          View dialogView) {
        if (context == null) {
            return null;
        }
        String msg = context.getString(res);
        return showProgressDialog(context, msg, dialogView);
    }

    public static void closeProgressDialog(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }

    }
}
