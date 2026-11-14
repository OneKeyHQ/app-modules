package io.intercom.android.sdk.m5.preview.util;

import android.webkit.MimeTypeMap;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\u001a\u0018\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\b\b\u0002\u0010\u0006\u001a\u00020\u0001\u001a\u0018\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\b\b\u0002\u0010\u0006\u001a\u00020\u0001\u001a\u0016\u0010\b\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0001\u001a \u0010\t\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u0001H\u0002\u001a\f\u0010\u000b\u001a\u00020\f*\u00020\u0001H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\r",
   d2 = {
         "IMAGE_FALLBACK",
         "",
         "VIDEO_FALLBACK",
         "FILE_FALLBACK",
         "resolveImageMimeType",
         "url",
         "mimeType",
         "resolveVideoMimeType",
         "resolveFileMimeType",
         "resolveMimeType",
         "fallback",
         "isConcreteMimeType",
         "",
         "intercom-sdk-base_release"
   }
)
public final class MimeTypeResolverKt {
   @NotNull
   private static final String IMAGE_FALLBACK = "image/png";
   @NotNull
   private static final String VIDEO_FALLBACK = "video/mp4";
   @NotNull
   private static final String FILE_FALLBACK = "application/octet-stream";

   @NotNull
   public static final String resolveImageMimeType(@NotNull String var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      return resolveMimeType(var0, var1, "image/png");
   }

   // $VF: synthetic method
   public static String resolveImageMimeType$default(String var0, String var1, int var2, Object var3) {
      if ((var2 & 2) != 0) {
         var1 = "image/*";
      }

      return resolveImageMimeType(var0, var1);
   }

   @NotNull
   public static final String resolveVideoMimeType(@NotNull String var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      return resolveMimeType(var0, var1, "video/mp4");
   }

   // $VF: synthetic method
   public static String resolveVideoMimeType$default(String var0, String var1, int var2, Object var3) {
      if ((var2 & 2) != 0) {
         var1 = "video/*";
      }

      return resolveVideoMimeType(var0, var1);
   }

   @NotNull
   public static final String resolveFileMimeType(@NotNull String var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "mimeType");
      return resolveMimeType(var0, var1, "application/octet-stream");
   }

   private static final String resolveMimeType(String var0, String var1, String var2) {
      if (isConcreteMimeType(var1)) {
         return var1;
      }

      String var3 = MimeTypeMap.getFileExtensionFromUrl(var0);
      CharSequence var5 = var3;
      String var4 = var5 == null || var5.length() == 0 ? null : MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
      String var10000 = var4;
      if (var4 == null) {
         var10000 = var2;
      }

      return var10000;
   }

   private static final boolean isConcreteMimeType(String var0) {
      return var0.length() > 0 && !StringsKt.contains$default(var0, "*", false, 2, null) && StringsKt.contains$default(var0, "/", false, 2, null);
   }
}
