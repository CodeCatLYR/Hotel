package com.tgcyber.hotelmobile._activity;

import com.tgcyber.hotelmobile.R;

/**
 * Created by lyr on 2017/7/11.
 */

public class TranslationActivity extends BaseActionBarActivity {
    @Override
    int getLayoutId() {
        return R.layout.activity_translation;
    }

    @Override
    protected void initView() {
        super.initView();
        setActionbarLeftDrawable(R.drawable.back);
        setActionbarTitle("翻译");


    }
}
