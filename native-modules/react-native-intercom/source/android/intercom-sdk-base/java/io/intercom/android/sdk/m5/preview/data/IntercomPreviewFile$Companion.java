package io.intercom.android.sdk.m5.preview.data;

import android.net.Uri;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007J\u0010\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$Companion;",
         "",
         "<init>",
         "()V",
         "network",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$NetworkFile;",
         "url",
         "",
         "mimeType",
         "local",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$LocalFile;",
         "uri",
         "Landroid/net/Uri;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomPreviewArgs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPreviewArgs.kt\nio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$Companion\n+ 2 Uri.kt\nandroidx/core/net/UriKt\n*L\n1#1,142:1\n29#2:143\n*S KotlinDebug\n*F\n+ 1 IntercomPreviewArgs.kt\nio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$Companion\n*L\n63#1:143\n*E\n"
)
public final class IntercomPreviewFile$Companion {
   private IntercomPreviewFile$Companion() {
   }

   @Nullable
   public final IntercomPreviewFile$NetworkFile network(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "url");
      Intrinsics.checkNotNullParameter(var2, "mimeType");
      String var4 = var1;
      int var5 = 0;
      Uri var3 = Uri.parse(var4);
      IntercomPreviewFile$NetworkFile var10000;
      if (IntercomPreviewArgsKt.isSafeNetworkUrl(var3)) {
         var10000 = new IntercomPreviewFile$NetworkFile(var1, var2);
      } else {
         IntercomPreviewArgsKt.access$getTWIG$p().w("Rejected non-https preview URL (scheme=" + var3.getScheme() + ')');
         var10000 = null;
      }

      return var10000;
   }

   @Nullable
   public final IntercomPreviewFile$LocalFile local(@NotNull Uri var1) {
      Intrinsics.checkNotNullParameter(var1, "uri");
      IntercomPreviewFile$LocalFile var10000;
      if (IntercomPreviewArgsKt.isLocalContentUri(var1)) {
         var10000 = new IntercomPreviewFile$LocalFile(var1);
      } else {
         IntercomPreviewArgsKt.access$getTWIG$p().w("Rejected non-content preview URI (scheme=" + var1.getScheme() + ')');
         var10000 = null;
      }

      return var10000;
   }

   // $VF: synthetic method
   public IntercomPreviewFile$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
