package io.intercom.android.sdk.ui.extension;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\u0010\u000e\n\u0002\b\u0007\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0003\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0004\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0005\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0006\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0007\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\b\u001a\u00020\u0001*\u00020\u0002¨\u0006\t",
   d2 = {"isVideo", "", "", "isImage", "isAudio", "isTxt", "isPdf", "isZip", "isCsv", "intercom-sdk-ui_release"}
)
public final class ContentTypeExtensionKt {
   public static final boolean isVideo(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "video", false, 2, null);
   }

   public static final boolean isImage(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "image", false, 2, null);
   }

   public static final boolean isAudio(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "audio", false, 2, null);
   }

   public static final boolean isTxt(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "text/plain", false, 2, null);
   }

   public static final boolean isPdf(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "application/pdf", false, 2, null);
   }

   public static final boolean isZip(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "application/zip", false, 2, null);
   }

   public static final boolean isCsv(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.contains$default(var0, "text/csv", false, 2, null);
   }
}
