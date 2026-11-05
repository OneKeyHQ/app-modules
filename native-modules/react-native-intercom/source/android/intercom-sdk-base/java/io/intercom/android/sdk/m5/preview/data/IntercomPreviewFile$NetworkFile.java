package io.intercom.android.sdk.m5.preview.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0019\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\"\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÁ\u0001¢\u0006\u0002\b\rJ\b\u0010\u000e\u001a\u00020\u000fH\u0007J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H×\u0003J\t\u0010\u0014\u001a\u00020\u000fH×\u0001J\t\u0010\u0015\u001a\u00020\u0003H×\u0001J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000fH\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$NetworkFile;",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "url",
         "",
         "mimeType",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getUrl",
         "()Ljava/lang/String;",
         "getMimeType",
         "component1",
         "component2",
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
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nIntercomPreviewArgs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPreviewArgs.kt\nio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$NetworkFile\n+ 2 Uri.kt\nandroidx/core/net/UriKt\n*L\n1#1,142:1\n29#2:143\n*S KotlinDebug\n*F\n+ 1 IntercomPreviewArgs.kt\nio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$NetworkFile\n*L\n45#1:143\n*E\n"
)
public final class IntercomPreviewFile$NetworkFile extends IntercomPreviewFile {
   @NotNull
   private final String url;
   @NotNull
   private final String mimeType;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new IntercomPreviewFile$NetworkFile$Creator();

   public IntercomPreviewFile$NetworkFile(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "url");
      Intrinsics.checkNotNullParameter(var2, "mimeType");
      String var3 = var1;
      int var4 = 0;
      super(Uri.parse(var3), null);
      this.url = var1;
      this.mimeType = var2;
   }

   @NotNull
   public final String getUrl() {
      return this.url;
   }

   @NotNull
   public final String getMimeType() {
      return this.mimeType;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.url);
      var1.writeString(this.mimeType);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.url;
   }

   @NotNull
   public final String component2() {
      return this.mimeType;
   }

   @NotNull
   public final IntercomPreviewFile$NetworkFile copy$intercom_sdk_base_release(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "url");
      Intrinsics.checkNotNullParameter(var2, "mimeType");
      return new IntercomPreviewFile$NetworkFile(var1, var2);
   }

   // $VF: synthetic method
   public static IntercomPreviewFile$NetworkFile copy$intercom_sdk_base_release$default(
      IntercomPreviewFile$NetworkFile var0, String var1, String var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.url;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.mimeType;
      }

      return var0.copy$intercom_sdk_base_release(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "NetworkFile(url=" + this.url + ", mimeType=" + this.mimeType + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.url.hashCode();
      return var1 * 31 + this.mimeType.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomPreviewFile$NetworkFile)) {
         return false;
      }

      IntercomPreviewFile$NetworkFile var2 = (IntercomPreviewFile$NetworkFile)var1;
      return !Intrinsics.areEqual(this.url, var2.url) ? false : Intrinsics.areEqual(this.mimeType, var2.mimeType);
   }
}
