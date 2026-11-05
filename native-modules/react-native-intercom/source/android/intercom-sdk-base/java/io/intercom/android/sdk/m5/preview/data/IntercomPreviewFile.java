package io.intercom.android.sdk.m5.preview.data;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u0000 \u00112\u00020\u0001:\u0003\u000f\u0010\u0011B\u0011\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\u000e\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0002\u0012\u0013¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "Landroid/os/Parcelable;",
         "uri",
         "Landroid/net/Uri;",
         "<init>",
         "(Landroid/net/Uri;)V",
         "getUri",
         "()Landroid/net/Uri;",
         "getMimeType",
         "",
         "context",
         "Landroid/content/Context;",
         "isImage",
         "",
         "isVideo",
         "LocalFile",
         "NetworkFile",
         "Companion",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$LocalFile;",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$NetworkFile;",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 0)
public abstract class IntercomPreviewFile implements Parcelable {
   @NotNull
   public static final IntercomPreviewFile$Companion Companion = new IntercomPreviewFile$Companion(null);
   @NotNull
   private final Uri uri;
   public static final int $stable;

   static { $stable = 8; }

   private IntercomPreviewFile(Uri var1) {
      this.uri = var1;
   }

   @NotNull
   public final Uri getUri() {
      return this.uri;
   }

   @NotNull
   public final String getMimeType(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      IntercomPreviewFile var2 = this;
      String var10000;
      if (var2 instanceof IntercomPreviewFile$LocalFile) {
         var10000 = var1.getContentResolver().getType(this.uri);
         if (var10000 == null) {
            var10000 = "application/*";
         }
      } else {
         if (!(var2 instanceof IntercomPreviewFile$NetworkFile)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = ((IntercomPreviewFile$NetworkFile)this).getMimeType();
      }

      return var10000;
   }

   public final boolean isImage(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return StringsKt.contains$default(this.getMimeType(var1), "image", false, 2, null);
   }

   public final boolean isVideo(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return StringsKt.contains$default(this.getMimeType(var1), "video", false, 2, null);
   }

   // $VF: synthetic method
   public IntercomPreviewFile(Uri var1, DefaultConstructorMarker var2) {
      this(var1);
   }
}
