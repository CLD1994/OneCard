#优化次数
-optimizationpasses 5
#混淆时不会产生形形色色的类名，不使用大小写混合
-dontusemixedcaseclassnames
#混淆jars中的非public classes
-dontskipnonpubliclibraryclasses
#不预校验
-dontpreverify
#混淆时记录日志
-verbose
#忽略警告，避免打包时某些警告出现
-ignorewarnings
#将包里的类混淆成n个再重新打包到一个统一的package中，后跟一个目录名
-repackageclasses ''
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod,Deprecated,Exceptions

#保持R文件不被混淆，否则，你的反射是获取不到资源id的
-keep class **.R
-keep class **.R$* {
    <fields>;
}

-dontwarn android.support.**
-dontwarn org.apache.commons.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.support.v4.widget
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class codetail.graphics.drawables.** { *; }

#v4包混淆
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

#Gson混淆
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}

#javaBean不能被混淆
-keep class com.zhuoli.onecard.data.model.** { *;}
-keep class com.zhuoli.onecard.data.source.remote.response.** { *;}

#Parcelable混淆规则
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#Serializable混淆规则
-keep public class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclasseswithmembers class * {
    void onClick*(...);
}
-keepclasseswithmembers class * {
    *** *Callback(...);
}


#enum混淆规则
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# we need line numbers in our stack traces otherwise they are pretty useless
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

#Butter Knife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okhttp3.**
-dontwarn okio.**

## Nineolddroid related classes to ignore
-keep class com.nineoldandroids.animation.** { *; }
-keep interface com.nineoldandroids.animation.** { *; }
-keep class com.nineoldandroids.view.** { *; }
-keep interface com.nineoldandroids.view.** { *; }

#rx
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#Fresco
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-keep class com.facebook.imagepipeline.animated.factory.AnimatedFactoryImpl {
    public AnimatedFactoryImpl(com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory,com.facebook.imagepipeline.core.ExecutorSupplier);
}

#android-saripaar
-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}
-keep class com.vvelink.yiqilai.validator.**{*;}

#微信
-dontwarn com.tencent.mm.**
-dontwarn com.tencent.wxop.stat.**
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.wxop.stat.**{*;}

#百度
#-libraryjars libs/baidumapapi_v2_1_0.jar 替换成自己所用版本的jar包
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

#zxing
-dontwarn com.google.zxing.**
-keep  class com.google.zxing.**{*;}

#gzu-liyujiang/AndroidPicker
-keep class cn.qqtheme.framework.entity.** { *;}
-keep class cn.qqtheme.framework.picker.AddressPicker$* { *;}

#uCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }