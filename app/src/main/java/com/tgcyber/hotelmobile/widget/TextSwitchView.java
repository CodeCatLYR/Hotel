package com.tgcyber.hotelmobile.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/22.
 */
public class TextSwitchView extends TextSwitcher {
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;
    private Timer timer;
    private TimerTask timerTask;
    private int index= -1;
    private Context context;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    index = next(); //取得下标值
                    updateText();  //更新TextSwitcherd显示内容;
                    break;
            }
        };
    };

    private List<String> resources;

    public TextSwitchView(Context context) {
        super(context);
        this.context = context;
        init();
    }
    public TextSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    private void init() {
        if(timer==null)
            timer = new Timer();

        mInUp = createAnim(-90, 0 , true, true);
        mOutUp = createAnim(0, 90, false, true);

//        Rotate3dAnimation mInDown = createAnim(90, 0 , true , false);
//        Rotate3dAnimation mOutDown = createAnim(0, -90, false, false);

        setInAnimation(mInUp);
        setOutAnimation(mOutUp);
    }
    public void setResources(List<String> res){
        this.resources = res;
    }
    public void setTextStillTime(long time){
        if (timer == null) {
            timer = new Timer();
        }
        if (time != 0) {
            if (timerTask != null) {
                timerTask.cancel();  //将原任务从队列中移除
            }
            if (getInAnimation() == null) {
                setInAnimation(mInUp);
            }
            if (getOutAnimation() == null) {
                setOutAnimation(mOutUp);
            }
            timerTask = new MyTask();
            timer.scheduleAtFixedRate(timerTask, 1, time);//每3秒更新
        } else {
            if (timerTask != null) {
                timerTask.cancel();  //将原任务从队列中移除
            }
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            if (getInAnimation() != null) {
                this.setInAnimation(null);
            }
            if (getOutAnimation() != null) {
                this.setOutAnimation(null);
            }
            if (resources != null && resources.get(0) != null) {
                index = 0;
                this.setText(resources.get(0));
            }
        }
    }
    private class MyTask extends TimerTask {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    }
    private int next(){
        int flag = index+1;
        if(flag>resources.size()-1){
            flag=flag-resources.size();
        }
        return flag;
    }
    private void updateText(){
        this.setText(resources.get(index));
    }

    public int getIndex(){
        return index;
    }

    private Rotate3dAnimation createAnim(float start, float end, boolean turnIn, boolean turnUp){
        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, turnIn, turnUp);
        rotation.setDuration(800);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new AccelerateInterpolator());
        return rotation;
    }

    private class Rotate3dAnimation extends Animation {
        private final float mFromDegrees;
        private final float mToDegrees;
        private float mCenterX;
        private float mCenterY;
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private Camera mCamera;

        public Rotate3dAnimation(float fromDegrees, float toDegrees, boolean turnIn, boolean turnUp) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight() / 2;
            mCenterX = getWidth() / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

            final float centerX = mCenterX ;
            final float centerY = mCenterY ;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1: -1;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime), 0.0f);
            }
            camera.rotateX(degrees);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
}