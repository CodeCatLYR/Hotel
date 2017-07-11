package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.loopj.android.http.RequestParams;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._event.HXEvent;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.tgcyber.hotelmobile.bean.HomeItem;
import com.tgcyber.hotelmobile.bean.HttpResult;
import com.tgcyber.hotelmobile.bean.UserInfo;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile.constants.HXConstant;
import com.tgcyber.hotelmobile.constants.KeyConstant;
import com.tgcyber.hotelmobile.constants.UrlConstant;
import com.tgcyber.hotelmobile.utils.HotelClient;
import com.tgcyber.hotelmobile.utils.HotelResponseListener;
import com.tgcyber.hotelmobile.utils.MD5Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lyr on 2017/6/30.
 */

public class LoginActivity extends BaseActionBarActivity implements View.OnClickListener {

    public static final int TYPE_WEB = 6;

    private EditText mUsernameEt;
    private EditText mPasswordEt;

    @Override
    int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        setActionbarTitle(getResources().getString(R.string.string_login));
        setActionbarLeftDrawable(R.drawable.back);
        mUsernameEt = findEditTextById(R.id.login_username_et);
        mPasswordEt = findEditTextById(R.id.login_pwd_et);
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.login_register_tv).setOnClickListener(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                login();
                break;

            case R.id.login_register_tv:
                startRegisteActivity();
                break;
        }
    }

    private void startRegisteActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void login() {
        final String username = mUsernameEt.getText().toString();
        String password = mPasswordEt.getText().toString();

        if (StringUtil.isBlank(username)) {
            showToast(R.string.string_input_username_phone);
            return;
        } else if (StringUtil.isBlank(password)) {
            showToast(R.string.string_input_pwd);
            return;
        }
        showProgressDialog("正在登陆中");

        final String pwdMd5 = MD5Utils.md5(password);
        RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("password", pwdMd5);

        String url = Constants.HOTEL_BASE_URL + UrlConstant.LOGIN_URL;
        HotelClient.get(url, params, new HotelResponseListener() {
            @Override
            public void onSuccess(HttpResult result) {
                UserInfo user = new UserInfo();
                user.setUsername(username);
                user.setPassword(pwdMd5);
                user.setGuid(result.info);

                loginHx(user);
            }

            @Override
            public void onFailed(int status, HttpResult result) {
                closeProgressDialog();
                showToast(result.info);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                closeProgressDialog();
                showToast(R.string.network_error);
            }
        });
    }

    private void loginHx(final UserInfo user) {
        EMClient.getInstance().login(user.getHxId(), user.getHxPwd(), new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                checkJoinGroupChat();
                EventBus.getDefault().post(new HXEvent(HXEvent.LOGIN_SUCCESS, user));
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                Log.d("lyr", "登录聊天服务器失败！  reason --- " + message);
                EventBus.getDefault().post(new HXEvent(HXEvent.LOGIN_FAIL, user));
            }
        });
    }

    /**
     * 检查是否加入群聊， 没加入则加入群组
     */
    private void checkJoinGroupChat() {
        try {
            if(!hasJoinHXGroup()){
                EMClient.getInstance().groupManager().joinGroup(HXConstant.GROUP_ID);
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    private boolean hasJoinHXGroup() throws HyphenateException {
        List<EMGroup> grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

        for (EMGroup emGroup : grouplist) {
            if(emGroup.getGroupId().equals(HXConstant.GROUP_ID)){
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hxLoginCallBack(HXEvent hxEvent){
        switch (hxEvent.type) {
            case HXEvent.LOGIN_SUCCESS:
            case HXEvent.LOGIN_FAIL:
                closeProgressDialog();
                HotelApplication.getInstance().saveUser(hxEvent.user);
                finish();
                jumpNextActivity();
                break;
        }
    }

    private void jumpNextActivity() {
        Intent dataIntent = getIntent();
        int type = dataIntent.getIntExtra(KeyConstant.TYPE, -1);
        switch (type) {
            case TYPE_WEB:
                startWebActivity(dataIntent);
                break;
        }
    }

    @NonNull
    public void startWebActivity(Intent data) {
        HomeItem item = (HomeItem) data.getSerializableExtra(KeyConstant.DATA);

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", item.value);
        intent.putExtra("name", item.name);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
