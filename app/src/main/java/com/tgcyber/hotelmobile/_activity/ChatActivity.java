package com.tgcyber.hotelmobile._activity;

import android.text.TextUtils;

import com.hyphenate.easeui.ui.EaseChatFragment;
import com.tgcyber.hotelmobile.R;


public class ChatActivity extends BaseActionBarActivity {
    private EaseChatFragment chatFragment;
//    String toChatUsername;

    @Override
    int getLayoutId() {
        return R.layout.em_activity_chat;
    }

    @Override
    public void initView() {
        super.initView();

        String title = getIntent().getExtras().getString("nick_name");
        if(TextUtils.isEmpty(title)){
            title = "群聊";
        }

        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle(title);

        //get user id or group id
//        toChatUsername = getIntent().getExtras().getString("userId");

        //use EaseChatFratFragment
        chatFragment = new EaseChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }
}
