# ProGuard / R8 Keep Rules — Distinct Routes

# Moshi JSON Adapters & Models
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class com.distinctroutes.app.data.model.** { *; }
-keep class com.distinctroutes.app.domain.model.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers enum * { *; }

# osmdroid
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# Distinct Routes UI Composables
-keep class com.distinctroutes.app.ui.** { *; }
-keepclassmembers class com.distinctroutes.app.ui.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Hilt & Dagger
-keep class dagger.hilt.** { *; }
-keepclassmembers class * {
    @javax.inject.Inject *;
}