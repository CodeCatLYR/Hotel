package com.tgcyber.hotelmobile._activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.HttpResult;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.constants.UrlConstant;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.HotelResponseListener;
import com.tgcyber.hotelmobile.utils.MD5Utils;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lyr on 2017/6/30.
 */

public class RegisterActivity extends BaseActionBarActivity implements View.OnClickListener {

    private EditText mUsernameEt;
    private EditText mNicknameEt;
    private EditText mPwdEt;

    private boolean mIsFollowUsername;

    @Override
    int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        super.initView();

        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle(getResources().getString(R.string.string_register));

        mUsernameEt = findEditTextById(R.id.register_username_et);
        mNicknameEt = findEditTextById(R.id.register_nickname_et);
        mPwdEt = findEditTextById(R.id.register_password_et);
        findViewById(R.id.register_submit_btn).setOnClickListener(this);


        mUsernameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mIsFollowUsername = mNicknameEt.getText().toString().equals(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mIsFollowUsername){
                    mNicknameEt.setText(s);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_submit_btn:
                register();
                break;
        }
    }

    private void register() {

        String username = mUsernameEt.getText().toString();
        String nickname = mNicknameEt.getText().toString();
        String password = mPwdEt.getText().toString();

        if (StringUtil.isBlank(username)) {
            showToast("请输入 用户名/手机号");
            return;
        } else if (StringUtil.isBlank(nickname)) {
            showToast("请输入昵称");
            return;
        } else if (password.length() < 6) {
            showToast("请输入6位及以上的密码");
            return;
        }

        RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("nickname", nickname);
        params.add("password", MD5Utils.md5(password));


        String url = Constants.HOTEL_BASE_URL + UrlConstant.REGISTER_URL;
        HotelClient.get(url, params, new HotelResponseListener() {
            @Override
            public void onSuccess(HttpResult result) {
                showToast("注册成功");
                finish();
            }

            @Override
            public void onFailed(int status, HttpResult result) {
                showToast(result.info);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showToast(R.string.network_error);
            }
        });
    }
}
