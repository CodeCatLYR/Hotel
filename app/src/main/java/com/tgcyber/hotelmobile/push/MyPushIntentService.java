package com.tgcyber.hotelmobile.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._activity.ContentActivity;
import com.tgcyber.hotelmobile._activity.MainActivity;
import com.tgcyber.hotelmobile._activity.WebViewActivity;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;
import com.tgcyber.hotelmobile._utils.SharedPreferencesUtils;
import com.tgcyber.hotelmobile._utils.StringUtil;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * Developer defined push intent service.
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}.
 *
 * @author lucas
 */
//完全自定义处理类
//参考文档的1.6.5
//http://dev.umeng.com/push/android/integration#1_6_5
public class MyPushIntentService extends UmengMessageService {
    private static final String TAG = MyPushIntentService.class.getName();

    @Override

    public void onMessage(Context context, Intent intent) {
        // 需要调用父类的函数，否则无法统计到消息送达
        //super.onMessage(context, intent);
        try {
            /*String var3 = intent.getStringExtra("body");
			String var4 = intent.getStringExtra("id");
			String var5 = intent.getStringExtra("task_id");
			Intent var6 = new Intent();
			var6.setPackage(context.getPackageName());
			var6.setAction("com.umeng.message.message.handler.action");
			var6.putExtra("body", var3);
			var6.putExtra("id", var4);
			var6.putExtra("task_id", var5);
			context.startService(var6);*/

            //可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            //type=跳转类型　content 内容终端页contentActivity, web外部浏览器，webview,内部编写的浏览器webviewActivity name=banner标题
            String title = null, value = null, summary = null, type=null,name=null;
            if (msg.extra != null) {
                LogCat.i(TAG, "msg.extra!-=null");    //消息体
                title = msg.extra.get("title");
                value = msg.extra.get("value");
                summary = msg.extra.get("summary");
                type = msg.extra.get("type");
                name=msg.extra.get("name");
            }
            if (title == null) {
                LogCat.i(TAG, "msg.extra!-=null");    //消息体
                title = msg.title;
                summary = msg.text;
            }

            LogCat.i(TAG, "message=" + message);    //消息体
            //Log.d(TAG, "custom="+msg.custom);    //自定义消息的内容
            //Log.d(TAG, "extra="+msg.extra);    //自定义消息的内容
            LogCat.i(TAG, "title=" + title);    //通知标题
            LogCat.i(TAG, "text=" + summary);    //通知内容
            // code  to handle message here
            // ...

            // 对完全自定义消息的处理方式，点击或者忽略
            boolean isClickOrDismissed = true;
         /*   if (isClickOrDismissed) {
                //完全自定义消息的点击统计
                UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
            } else {
                //完全自定义消息的忽略统计
                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
            }*/
            if (title != null && summary != null)
                showNotification(type,value, title, summary, msg.ticker, msg,name);

        } catch (Exception e) {
            e.printStackTrace();
            LogCat.i(TAG, e.getMessage());
        }
    }

    // 强行限制２分钟内只能接收３条
    private static long firstTime;
    private static int msgCount;
    private final int WAITTIME = 120000;

    private void showNotification(String type,String value, String title, String summary, String tiker, UMessage msg,String name) {
        try {
            NotificationManager mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //text = new String(android.util.Base64.decode(text,android.util.Base64.DEFAULT));

			/*PushBean data = PushBean.getBean(text);
			LogCat.i("PushService", data + "------------text:" + text);
			if (null == data) {
				return;
			}*/
            LogCat.i(TAG, "showNotification title=" + title);    //消息体
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            RemoteViews myNotificationView = new RemoteViews(getBaseContext().getPackageName(), R.layout.notification_view);
            myNotificationView.setTextViewText(R.id.notification_title, title);
            myNotificationView.setTextViewText(R.id.notification_text, summary);
            //myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(getBaseContext(), msg));
            //myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
            builder.setContentTitle(title).setContentText(summary).setSmallIcon(R.drawable.ic_launcher).setTicker(tiker != null ? tiker : title).setAutoCancel(true).setDefaults(Notification.DEFAULT_VIBRATE);
//.setContent(myNotificationView)


            Intent intent = null;
            int id = 0;
            try {
                id = (int) (System.currentTimeMillis() % 1000);
            } catch (Exception e) {
                // TODO: handle exception

            }

            if (type != null&&type.equals("webview")) {
                intent = new Intent(this, WebViewActivity.class);
                //intent.putExtra("type", data.type);
                intent.putExtra("src", "push");
                intent.putExtra("url", value);
                intent.putExtra("name",name);
              /*  int count = SharedPreferencesUtils.getInt(this,
                        com.tgcyber.hotelmobile.constants.Constants.SP_KEY_PUSH_COUNT);
                count++;
                SharedPreferencesUtils.saveInt(this,
                        Constants.SP_KEY_PUSH_COUNT, count);
                intent.setAction("" + System.currentTimeMillis());*/
            }else    if (type != null&&type.equals("web")) {
                    LogCat.i("MyPushIntentService", "start url = " + value);
                //intent.setAction(Intent.ACTION_MAIN);
               // intent.addCategory(Intent.CATEGORY_APP_BROWSER);
                    if (!StringUtil.isBlank(value)) {
                        if (!value.startsWith("http://") && !value.startsWith("https://")) {
                            value = "http://" + value;
                        }
                        intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(value);
                            intent.setData(content_url);
                    }else {
                        intent = new Intent(this, MainActivity.class);
                        intent.putExtra("src", "push");
                    }
            }else    if (type != null&&type.equals("content")) {
                LogCat.i("MyPushIntentService", "start url = " + value);
                intent = new Intent(this, ContentActivity.class);
                intent.putExtra("url", value);
                intent.putExtra("name", name);
                intent.putExtra("src", "push");
            } else {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("src", "push");
                int count = SharedPreferencesUtils.getInt(this,
                        Constants.SP_KEY_MSG_COUNT);
                count++;
                SharedPreferencesUtils.saveInt(this, Constants.SP_KEY_MSG_COUNT, count);
            }
            {
                // 强行限制２分钟内只能接收３条
                long now = System.currentTimeMillis();
                if (firstTime == 0 || now - firstTime > WAITTIME) {
                    firstTime = now;
                    msgCount = 1;
                } else {
                    msgCount++;
                }
                if (msgCount > 5)
                    return;

                //PendingIntent pi = PendingIntent.getActivity(this, id, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                //n.setLatestEventInfo(this, title, summary, pi);
                //mNotifMan.notify(id, n);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                if (intent != null) {
                    stackBuilder.addNextIntent(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(resultPendingIntent);
                }
                Notification n = builder.build();
                if (msg.play_lights) n.flags |= Notification.FLAG_SHOW_LIGHTS;
                n.flags |= Notification.FLAG_AUTO_CANCEL;
                n.defaults = Notification.DEFAULT_ALL;
                n.icon = R.drawable.ic_launcher;
                n.when = System.currentTimeMillis();
                mNotifMan.notify(id, n);
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction(Constants.ACTION_PUSH_COUNT_UPDATE); // ˵������
                sendBroadcast(intentBroadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
