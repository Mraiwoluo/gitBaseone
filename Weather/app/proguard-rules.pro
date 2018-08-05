# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\zeu\sdk\android/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
#屏蔽警告
-ignorewarnings
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
# 保持AIDL不被混淆
-keep public class * extends android.os.IInterface{*;}
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#不混淆jar包
#-libraryjars libs/crashLog.jar
-dontwarn hrs.crash.log.**
-keep class hrs.crash.log.** { *; }
#-libraryjars libs/fastjson.jar
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
#-libraryjars libs/glide.jar
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *; }
#-libraryjars libs/litepal.jar
-dontwarn org.litepal.**
-keep class org.litepal.** { *; }
#-libraryjars libs/okhttp.jar
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
#-libraryjars libs/okio.jar
-dontwarn okio.**
-keep class okio.** { *; }

#搜索
-dontwarn com.amap.api.services.**
-keep class com.amap.api.services.**{*;}
#定位
-dontwarn com.amap.api**
-keep class com.amap.api**{*;}
-dontwarn com.autonavi.aps.amapapi.model.**
-keep class com.autonavi.aps.amapapi.model.**{*;}
#2D地图
-dontwarn com.amap.api.**
-keep class com.amap.api.**{*;}
-dontwarn com.amap.api.mapcore2d.**
-keep class com.amap.api.mapcore2d.**{*;}
-keep class android.support.** {*;}

