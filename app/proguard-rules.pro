# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
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

# Stripe Android SDK
-keep class com.stripe.** { *; }
#-keepclassmembers class * {
#    @com.stripe.android.paymentsheet.injection.Injectable <fields>;
#}

# Kotlin metadata
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Parcelize annotations
-keep class kotlinx.parcelize.Parcelize
-keep @kotlinx.parcelize.Parcelize class * { *; }

# Prevent obfuscation of classes used in reflection
-keepnames class com.stripe.** { *; }
-dontwarn com.stripe.**

# JSON parsing with Gson/Moshi
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

# For Java Time (if used by Stripe or in your app)
-dontwarn java.time.**
