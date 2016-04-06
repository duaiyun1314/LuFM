-target 1.7                              #jdk版本
-optimizationpasses 5                   #压缩级别
-dontusemixedcaseclassnames            #是否使用大小写混淆
-dontskipnonpubliclibraryclasses       #是否混淆第三方库
-dontpreverify                           #是否做预校验
#---------------Shrink Options----------------
-dontshrink

#--------------------Optimization Options--------------------
-dontoptimize
-optimizations !code/simplification/arithmetic #混淆所采用的算法
-allowaccessmodification

#----------------Obfuscate Options---------------
#-dontobfuscate
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-repackageclasses ''


#----------------Print Options---------------
-verbose
-dump /build/outputs/class_files.txt
-printseeds /build/outputs/seeds.txt
-printusage /build/outputs/unused.txt
-printmapping /build/outputs/mapping.txt

#六大控件
-keep public class * extends android.app.Activity 
-keep public class * extends android.app.Application 
-keep public class * extends android.app.Service 
-keep public class * extends android.content.BroadcastReceiver 
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Fragment
#自定义控件
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#myclass
#-keepclasseswithmembers class com.andy.LuFM.fragments.DiscoverFragment {*;}

#R
-keepclassmembers class **.R$* {
    public static <fields>;
}

#枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#序列化
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

#native
-keepclasseswithmembernames class * {
    native <methods>;
}

#反射，依赖注入，注解
-keep class com.google.inject.Binder
-keepclassmembers class * {
    @com.google.inject.Inject <init>(...);
}

-dontwarn java.lang.invoke.*
-dontwarn javax.annotation.*
-dontwarn javax.annotation.**

#系统接口回调，如onEvent
-keepclassmembers class * {
    void *(**On*Event);
}
-keepclassmembers,includedescriptorclasses class ** { public void onEvent*(**); }

#android support library
-dontwarn android.support.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn  java.lang.reflect.*
-dontwarn org.**
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }


#roboguice
-dontwarn roboguice.**
-keep public class roboguice.**
-dontwarn  com.actionbarsherlock.*

# RxJava
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
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

# ----------------------------------------
# Retrofit and OkHttp
# ----------------------------------------
-dontwarn com.squareup.okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
# vitamio
-dontwarn io.vov.vitamio.**
-keep class io.vov.vitamio.** {*;}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.andy.LuFM.model.** { *; }

##---------------End: proguard configuration for Gson  ----------





