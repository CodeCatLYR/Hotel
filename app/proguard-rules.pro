# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontshrink
-dontskipnonpubliclibraryclasses
-verbose

-keep class com.umeng.socialize.** { *; }
-dontwarn com.umeng.socialize.**
# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).


-dontwarn c.**
-dontwarn com.vpadn.**
-dontwarn vpadn.**
-dontoptimize
-dontpreverify
#过滤警告
-ignorewarnings
#不过滤警告


#支付宝混淆
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}


#mframework
-keep class m.mframework.** {*;}

#mta-sdk混淆
-keep class com.tencent.stat.** {*;}

#nineoldandroids混淆
-keep class com.nineoldandroids.** {*;}

#org.apache.http混淆
-keep class android.net.** {*;}
-keep class com.android.internal.http.multipart.** {*;}
-keep class org.apache.** {*;}

#open_sdk混淆
-keep class com.tencent.connect.** {*;}
-keep class com.tencent.map.** {*;}
-keep class com.tencent.open.** {*;}
-keep class com.tencent.qqconnect.** {*;}
-keep class com.tencent.tauth.** {*;}

-keep public interface com.tencent.**
-keep public class com.tencent.** {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

#tbs_sdk混淆
-keep class com.tencent.smtt.** {*;}
-keep class com.tencent.tbs.** {*;}

#umeng混淆
-dontoptimize
-dontpreverify
-dontwarn com.umeng.comm.**
-dontwarn com.umeng.commm.**
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-keep class  activeandroid.** {*;}
-keep class com.umeng.** {*;}
-keep class android.** {*;}
-keepattributes *Annotation*

-keep class org.apache.http.** {*;}
-dontwarn  org.apache.http.**
-keep class org.apache.http.* {*;}
-dontwarn  org.apache.http.*
-keep,allowshrinking class org.android.agoo.service.* {
    public <fields>;
    public <methods>;
}
-keep class android.support.v4.** {*;}
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable
-keep public class javax.**
-keep public class android.webkit.**
# adding push

-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}


#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }
#如果compileSdkVersion为23，请添加以下混淆代码



#WebViewActivity$的js交互不混淆
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepclassmembers class com.tgcyber.hotelmobile._activity.WebViewActivity$* {
  public *;
}
-keep  class com.tgcyber.hotelmobile._activity.WebViewActivity {
       public void checkSelfPermission(**);
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep class * extends android.webkit.WebViewClient
-keep class c.**{ *; }
-keep class com.vpadn.** { *; }
-keep class vpadn.** { *; }
-keep class android.webkit.** {*;}
-keep class android.net.http.** {*;}
-keep class com.tgcyber.hotelmobile.bean.** {*;}
-keep class com.tgcyber.hotelmobile._bean.** {*;}
-keep class java.io.** {*;}
-keep class java.lang.Class.** {*;}
-keep class org.greenrobot.eventbus.** {*;}
-keepclassmembers class * extends android.webkit.WebChromeClient{
   		public void openFileChooser(...);
}

-keep public class com.android.vending.licensing.ILicensingService
# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembers,includedescriptorclasses,allowshrinking class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# 编译时候无必要的提示错误屏蔽
-dontnote java.io.**
-dontnote android.webkit.**
-dontnote android.media.**
-dontnote android.util.**
-dontnote android.hardware.**
-dontnote javax.**

#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
-keep   class com.amap.api.services.**{*;}

#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

#讯飞混淆
-keep class com.iflytek.**{*;}
-keepattributes Exceptions,InnerClasses,Signature