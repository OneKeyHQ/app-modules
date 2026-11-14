package io.intercom.android.sdk.m5.preview.data;

import android.net.Uri;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0004H\u0000\u001a\f\u0010\u0005\u001a\u00020\u0003*\u00020\u0004H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0003*\u00020\u0007H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b",
   d2 = {
         "TWIG",
         "Lcom/intercom/twig/Twig;",
         "isSafeNetworkUrl",
         "",
         "Landroid/net/Uri;",
         "isLocalContentUri",
         "hasSafeScheme",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPreviewArgsKt {
   @NotNull
   private static final Twig TWIG;

   public static final boolean isSafeNetworkUrl(@NotNull Uri var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      if (StringsKt.equals("https", var0.getScheme(), true)) {
         CharSequence var1 = var0.getHost();
         if (var1 != null && !StringsKt.isBlank(var1)) {
            return true;
         }
      }

      return false;
   }

   public static final boolean isLocalContentUri(@NotNull Uri var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return StringsKt.equals("content", var0.getScheme(), true);
   }

   public static final boolean hasSafeScheme(@NotNull IntercomPreviewFile var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      IntercomPreviewFile var1 = var0;
      boolean var10000;
      if (var1 instanceof IntercomPreviewFile$LocalFile) {
         var10000 = isLocalContentUri(var0.getUri());
      } else {
         if (!(var1 instanceof IntercomPreviewFile$NetworkFile)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = isSafeNetworkUrl(var0.getUri());
      }

      return var10000;
   }

   // $VF: synthetic method
   public static final Twig access$getTWIG$p() {
      return TWIG;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      TWIG = var10000;
   }
}
