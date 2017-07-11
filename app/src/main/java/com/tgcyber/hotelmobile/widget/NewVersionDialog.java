package com.tgcyber.hotelmobile.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.tgcyber.hotelmobile.R;


public class NewVersionDialog extends Dialog {

    private View view;
    private TextView tv_title;
    private TextView tv_msg;
    private Button btn_cancel;
    private Button btn_confirm;
    private android.view.View.OnClickListener clickListener;

    public NewVersionDialog(Context context, int theme, int Layout) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.show_version_window, null);

        initDialog();
    }

    public void setSubViewOnClickListener(
            android.view.View.OnClickListener clickListener) {
        this.clickListener = clickListener;
        btn_cancel.setOnClickListener(clickListener);
        btn_confirm.setOnClickListener(clickListener);
    }

    public void initDialog() {
        // 设置在父控件的底部弹出
        Window win_dialog = this.getWindow();
        win_dialog.getDecorView().setPadding(0, 0, 0, 0);
        win_dialog.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win_dialog.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win_dialog.setAttributes(lp);

        LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        this.addContentView(view, params1);

        // 点击对话框外部可以消失
        this.setCanceledOnTouchOutside(true);
        initSubView(view);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setMessage(String message) {
        tv_msg.setText(message);
    }

    public void initSubView(View view) {
        tv_title = (TextView) view.findViewById(R.id.show_version_tv_title);
        tv_msg = (TextView) view.findViewById(R.id.show_version_tv_msg);

        btn_cancel = (Button) view.findViewById(R.id.show_version_btn_cancel);
        btn_confirm = (Button) view.findViewById(R.id.show_version_btn_confirm);

    }
}