# Glide and optional image decoders are reached through the registry.
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# @onekeyfe/react-native-native-overlay draws toast icons through this registry
# by reflection (Class.forName + method name and arity), so R8 must keep the
# class name, the singleton field and these members unrenamed in app builds.
-keep class com.margelo.nitro.onekeyimage.OneKeyNamedSVG {
    public static final com.margelo.nitro.onekeyimage.OneKeyNamedSVG INSTANCE;
    public final void request(java.lang.Object, android.content.res.AssetManager, java.lang.String, java.lang.String, int, int, float, kotlin.jvm.functions.Function1);
    public final void cancel(java.lang.Object);
}
