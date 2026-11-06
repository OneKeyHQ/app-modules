# The native diagnostics call this existing logger entry point through JNI.
-keep class com.margelo.nitro.nativelogger.OneKeyLog {
    public static void info(java.lang.String, java.lang.String);
}
