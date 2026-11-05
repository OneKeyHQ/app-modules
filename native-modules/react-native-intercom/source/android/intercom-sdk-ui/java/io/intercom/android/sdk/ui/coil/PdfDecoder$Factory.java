package io.intercom.android.sdk.ui.coil;

import androidx.compose.runtime.internal.StabilityInferred;
import coil.ImageLoader;
import coil.decode.Decoder;
import coil.decode.Decoder.Factory;
import coil.fetch.SourceResult;
import coil.request.Options;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0012\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u0013\u0010\u0010\u001a\u00020\r2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0096\u0002J\b\u0010\u0013\u001a\u00020\u0014H\u0016¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/ui/coil/PdfDecoder$Factory;",
         "Lcoil/decode/Decoder$Factory;",
         "<init>",
         "()V",
         "create",
         "Lcoil/decode/Decoder;",
         "result",
         "Lcoil/fetch/SourceResult;",
         "options",
         "Lcoil/request/Options;",
         "imageLoader",
         "Lcoil/ImageLoader;",
         "isApplicable",
         "",
         "mimeType",
         "",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 1)
public final class PdfDecoder$Factory implements Factory {
   public static int $stable;

   static {}

   @Nullable
   @Override
   public Decoder create(@NotNull SourceResult var1, @NotNull Options var2, @NotNull ImageLoader var3) {
      Intrinsics.checkNotNullParameter(var1, "result");
      Intrinsics.checkNotNullParameter(var2, "options");
      Intrinsics.checkNotNullParameter(var3, "imageLoader");
      return !this.isApplicable(var1.getMimeType()) ? null : new PdfDecoder(var1.getSource(), var2);
   }

   private final boolean isApplicable(String var1) {
      return var1 != null ? ContentTypeExtensionKt.isPdf(var1) : false;
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      return var1 instanceof PdfDecoder$Factory;
   }

   @Override
   public int hashCode() {
      return this.getClass().hashCode();
   }
}
