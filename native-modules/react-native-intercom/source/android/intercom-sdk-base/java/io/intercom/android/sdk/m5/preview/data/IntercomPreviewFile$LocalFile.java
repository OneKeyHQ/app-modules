package io.intercom.android.sdk.m5.preview.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0018\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÁ\u0001¢\u0006\u0002\b\nJ\b\u0010\u000b\u001a\u00020\fH\u0007J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H×\u0003J\t\u0010\u0011\u001a\u00020\fH×\u0001J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\fH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$LocalFile;",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "fileUri",
         "Landroid/net/Uri;",
         "<init>",
         "(Landroid/net/Uri;)V",
         "getFileUri",
         "()Landroid/net/Uri;",
         "component1",
         "copy",
         "copy$intercom_sdk_base_release",
         "describeContents",
         "",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPreviewFile$LocalFile extends IntercomPreviewFile {
   @NotNull
   private final Uri fileUri;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new IntercomPreviewFile$LocalFile$Creator();

   public IntercomPreviewFile$LocalFile(@NotNull Uri var1) {
      Intrinsics.checkNotNullParameter(var1, "fileUri");
      super(var1, null);
      this.fileUri = var1;
   }

   @NotNull
   public final Uri getFileUri() {
      return this.fileUri;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeParcelable(this.fileUri, var2);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final Uri component1() {
      return this.fileUri;
   }

   @NotNull
   public final IntercomPreviewFile$LocalFile copy$intercom_sdk_base_release(@NotNull Uri var1) {
      Intrinsics.checkNotNullParameter(var1, "fileUri");
      return new IntercomPreviewFile$LocalFile(var1);
   }

   // $VF: synthetic method
   public static IntercomPreviewFile$LocalFile copy$intercom_sdk_base_release$default(IntercomPreviewFile$LocalFile var0, Uri var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.fileUri;
      }

      return var0.copy$intercom_sdk_base_release(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "LocalFile(fileUri=" + this.fileUri + ')';
   }

   @Override
   public int hashCode() {
      return this.fileUri.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomPreviewFile$LocalFile)) {
         return false;
      }

      IntercomPreviewFile$LocalFile var2 = (IntercomPreviewFile$LocalFile)var1;
      return Intrinsics.areEqual(this.fileUri, var2.fileUri);
   }
}
