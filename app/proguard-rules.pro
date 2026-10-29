# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class com.shubdeep.calculatorpro.data.remote.** { *; }
-keep class com.shubdeep.calculatorpro.data.local.** { *; }
