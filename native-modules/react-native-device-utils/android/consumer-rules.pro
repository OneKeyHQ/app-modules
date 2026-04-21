# Keep host app's BuildConfig class name AND its ANDROID_CHANNEL field so
# getAndroidChannel() can read it via `Class.forName(...).getField("ANDROID_CHANNEL")`
# when R8/Proguard is enabled in the consumer app.
#
# `-keep class` (not `-keepclassmembers`) is required because the class is
# only referenced as a string literal in reflection, so R8 cannot detect the
# usage and would otherwise rename/strip the class entirely.
-keep class **.BuildConfig {
    public static final java.lang.String ANDROID_CHANNEL;
}
