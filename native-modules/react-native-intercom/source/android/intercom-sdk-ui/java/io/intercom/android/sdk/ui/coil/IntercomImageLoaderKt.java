package io.intercom.android.sdk.ui.coil;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import coil.ImageLoader;
import coil.ImageLoader.Builder;
import coil.decode.ImageDecoderDecoder.Factory;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u000e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006\"\u0010\u0010\u0000\u001a\u0004\u0018\u00010\u0001X\u0082\u000e¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007",
   d2 = {"imageLoader", "Lcoil/ImageLoader;", "imageLoaderLock", "", "getImageLoader", "context", "Landroid/content/Context;", "intercom-sdk-ui_release"}
)
@SourceDebugExtension(
   "SMAP\nIntercomImageLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomImageLoader.kt\nio/intercom/android/sdk/ui/coil/IntercomImageLoaderKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ImageLoader.kt\ncoil/ImageLoader$Builder\n*L\n1#1,39:1\n1#2:40\n192#3:41\n*S KotlinDebug\n*F\n+ 1 IntercomImageLoader.kt\nio/intercom/android/sdk/ui/coil/IntercomImageLoaderKt\n*L\n23#1:41\n*E\n"
)
public final class IntercomImageLoaderKt {
   @Nullable
   private static volatile ImageLoader imageLoader;
   @NotNull
   private static final Object imageLoaderLock = new Object();

   @NotNull
   public static final ImageLoader getImageLoader(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      ImageLoader var1 = imageLoader;
      if (var1 != null) {
         ImageLoader var14 = var1;
         int var17 = 0;
         return var14;
      }

      synchronized (imageLoaderLock) {
         int var3 = 0;
         if (imageLoader != null) {
            ImageLoader var16 = imageLoader;
            int var18 = 0;
            return var16;
         }

         Context var10002 = var0.getApplicationContext();
         Intrinsics.checkNotNullExpressionValue(var10002, "getApplicationContext(...)");
         Builder var6 = new Builder(var10002).bitmapConfig(Config.ARGB_8888);
         int var7 = 0;
         coil.ComponentRegistry.Builder var4 = new coil.ComponentRegistry.Builder();
         coil.ComponentRegistry.Builder var5 = var4;
         Builder var8 = var6;
         int var9 = 0;
         if (VERSION.SDK_INT >= 28) {
            var5.add(new Factory(false, 1, null));
         } else {
            var5.add(new coil.decode.GifDecoder.Factory(false, 1, null));
         }

         var5.add(new coil.decode.VideoFrameDecoder.Factory());
         var5.add(new PdfDecoder$Factory());
         ImageLoader var19 = var8.components(var4.build()).build();
         ImageLoader var20 = var19;
         int var15 = 0;
         imageLoader = var20;
         return var19;
      }
   }
}
