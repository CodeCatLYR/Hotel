package com.tgcyber.hotelmobile._activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photoview.PhotoView;
import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;

/**
 * Created by Administrator on 2016/8/30.22
 */
public class APhotoActivity extends BaseActionBarActivity{

    private String imgurl,shareContent,imgpath,qrcode_url;
    private PhotoView photoView;
    private LinearLayout PhotoLayoutFoot;
    private TextView photoContent;

    @Override
    int getLayoutId() {
        return R.layout.activity_aphoto;
    }

    @Override
    protected void initView() {
        super.initView();
        getWindow().setBackgroundDrawableResource(R.color.tou_ming);
       // setActionbarRightText(-1);
        setActionbarTitle(R.string.detail,"");
        setActionbarRightText("二维码");
        imgurl = getIntent().getStringExtra("imgurl");
        imgpath = getIntent().getStringExtra("imgpath");
        shareContent=getIntent().getStringExtra("sharecontent");
        qrcode_url=getIntent().getStringExtra("qrcode_url");
        LogCat.i("APhotoActivity", imgurl);

        photoView = (PhotoView) findViewById(R.id.pv_big);
        photoContent = (TextView) this.findViewById(R.id.PhotoContent);
        PhotoLayoutFoot = (LinearLayout) findViewById(R.id.PhotoLayoutFoot);
        setActionbarLeftDrawable(R.drawable.icon50_back);

        if (imgurl != null){
            imageLoader.displayImage(imgurl, photoView, menuImgOptions);
        } else if (imgpath != null){
            photoView.setImageBitmap(BitmapFactory.decodeFile(imgpath));
            PhotoLayoutFoot.setVisibility(View.GONE);
        }
        if (shareContent != null &&shareContent.length() > 0) {

            photoContent.setText(shareContent);
            photoContent.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onActionbarRightClick(View v) {
        super.onActionbarRightClick(v);
        LogCat.i("ContentActivity", "onActionbarRightClick:" );
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", qrcode_url);
        intent.putExtra("name", "二维码");
        startActivity(intent);
    }

}
