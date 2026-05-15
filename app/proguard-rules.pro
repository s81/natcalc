-keepattributes Signature
-keepattributes *Annotation*

# Retrofit & Gson
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.natcalc.data.remote.dto.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
