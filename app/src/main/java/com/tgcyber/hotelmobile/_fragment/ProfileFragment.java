package com.tgcyber.hotelmobile._fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.AppCenterActivity;
import com.tgcyber.hotelmobile._activity.ChatActivity;
import com.tgcyber.hotelmobile._activity.HotelApplication;
import com.tgcyber.hotelmobile._activity.WebViewActivity;
import com.tgcyber.hotelmobile._event.HXEvent;
import com.tgcyber.hotelmobile.constants.HXConstant;
import com.tgcyber.hotelmobile.constants.KeyConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by lyr on 2017/7/6.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private final String COUPONS_URL = "http://api.hotelmobile.top/hmstore";
    private final String MY_FAVOURABLE_URL = "http://api.hotelmobile.top/hmstore/myorder";

    private TextView mUserNameTv;
    private TextView mMobileTv;
    private View mLogoutView;
    private TextView mBadgerTv;

    public static ProfileFragment newInstance(){
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initView(view);
        bindPhoneNum();
        refreshBadgerCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindUserData();
    }

    private void refreshBadgerCount() {
        int unreadCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        refreshMsgCount(new HXEvent(HXEvent.MSG_COUNT, unreadCount));
    }

    private void bindUserData() {
        if(HotelApplication.getInstance().hasLogin()){
            mUserNameTv.setText(HotelApplication.getInstance().getUser().getUsername());
            mLogoutView.setVisibility(View.VISIBLE);
        }else{
            mUserNameTv.setText("登陆/注册");
            mLogoutView.setVisibility(View.GONE);
        }
    }

    private void bindPhoneNum() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String phone = telephonyManager.getLine1Number();
        String phoneNumStr = "本机号码：+853 ";
        if(!TextUtils.isEmpty(phone)){
            phoneNumStr += phone;
        }
        mMobileTv.setText(phoneNumStr);
    }

    private void initView(View view) {
        mBadgerTv = (TextView) view.findViewById(R.id.profile_badger_tv);
        view.findViewById(R.id.profile_avatar_iv).setOnClickListener(this);
        mUserNameTv = (TextView) view.findViewById(R.id.profile_username_tv);
        mUserNameTv.setOnClickListener(this);
        mMobileTv = (TextView) view.findViewById(R.id.profile_mobile_tv);
        mLogoutView = view.findViewById(R.id.profile_logout_ll);
        mLogoutView.setOnClickListener(this);
        view.findViewById(R.id.profile_message_ll).setOnClickListener(this);
        view.findViewById(R.id.profile_appcenter_ll).setOnClickListener(this);
        view.findViewById(R.id.profile_coupons_ll).setOnClickListener(this);
        view.findViewById(R.id.profile_favourable_ll).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_avatar_iv:
            case R.id.profile_username_tv:
                HotelApplication.getInstance().checkLogin(getActivity());
                break;

            case R.id.profile_logout_ll:
                HotelApplication.getInstance().logout();
                bindUserData();
                break;

            case R.id.profile_message_ll:
                startChatActivity();
                break;

            case R.id.profile_appcenter_ll:
                startAppCenterActivity();
                break;

            case R.id.profile_coupons_ll:
                startWebViewActivity(COUPONS_URL);
                break;

            case R.id.profile_favourable_ll:
                startWebViewActivity(MY_FAVOURABLE_URL);
                break;
        }
    }

    private void startAppCenterActivity() {
        Intent intent = new Intent(getActivity(), AppCenterActivity.class);
        startActivity(intent);
    }

    private void startWebViewActivity(String url){
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra(KeyConstant.TYPE, WebViewActivity.TYPE_ORIGINAL_URL);
        startActivity(intent);
    }

    private void startChatActivity() {
        if(HotelApplication.getInstance().checkLogin(getActivity())){
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
            intent.putExtra(EaseConstant.EXTRA_USER_ID, HXConstant.GROUP_ID);
            startActivity(intent);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMsgCount(HXEvent event){
        if(event.type == HXEvent.MSG_COUNT){
            Log.i("lyr", "message unread count ----   " + event.unreadMsgCount);
            if(event.unreadMsgCount > 0){
                mBadgerTv.setVisibility(View.VISIBLE);
                mBadgerTv.setText(String.valueOf(event.unreadMsgCount));
            }else{
                mBadgerTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
