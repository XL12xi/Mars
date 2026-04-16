# ProGuard/R8 配置文件

# 保持我们的代码不被混淆
-keep class com.mars.colony.** { *; }
-keep class com.mars.colony.ui.** { *; }
-keep class com.mars.colony.model.** { *; }
-keep class com.mars.colony.ability.** { *; }
-keep class com.mars.colony.game.** { *; }
-keep class com.mars.colony.upgrade.** { *; }

# 保持Activity
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity

# 保持View的子类
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持枚举
-keep class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持资源类中的静态字段
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保持原生方法
-keepclasseswithmembernames class * {
    native <methods>;
}
