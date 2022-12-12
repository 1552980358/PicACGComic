# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# For serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations
-keep,includedescriptorclasses class kotlinx.serialization.json.**$$serializer { *; }
-keep,includedescriptorclasses class kotlinx.serialization.json.**Serializer { *; }
-keep,includedescriptorclasses class projekt.cloud.piece.pic.**$$serializer { *; }
-keepclassmembers class projekt.cloud.piece.pic.** {
    *** Companion;
}
-keepclasseswithmembers class projekt.cloud.piece.pic.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# For reflection of Toolbar
-keepclassmembernames class androidx.appcompat.widget.Toolbar {
    private void ensureLogoView();
    private android.widget.ImageView mLogoView;
}