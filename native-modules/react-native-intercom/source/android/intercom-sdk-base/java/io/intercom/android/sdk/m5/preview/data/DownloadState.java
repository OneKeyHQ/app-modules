package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J;\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u0005HÇ\u0001J\b\u0010\u0018\u001a\u00020\u0019H\u0007J\u0013\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH×\u0003J\t\u0010\u001d\u001a\u00020\u0019H×\u0001J\t\u0010\u001e\u001a\u00020\u0005H×\u0001J\u0018\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0019H\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000e¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/DownloadState;",
         "Landroid/os/Parcelable;",
         "showDownloadAction",
         "",
         "fileSavingText",
         "",
         "fileSavedText",
         "saveFailedText",
         "permissionDeniedText",
         "<init>",
         "(ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getShowDownloadAction",
         "()Z",
         "getFileSavingText",
         "()Ljava/lang/String;",
         "getFileSavedText",
         "getSaveFailedText",
         "getPermissionDeniedText",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "describeContents",
         "",
         "equals",
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
@Parcelize
@StabilityInferred(parameters = 1)
public final class DownloadState implements Parcelable {
   private final boolean showDownloadAction;
   @NotNull
   private final String fileSavingText;
   @NotNull
   private final String fileSavedText;
   @NotNull
   private final String saveFailedText;
   @NotNull
   private final String permissionDeniedText;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new DownloadState$Creator();

   public DownloadState(boolean var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5) {
      super();
      Intrinsics.checkNotNullParameter(var2, "fileSavingText");
      Intrinsics.checkNotNullParameter(var3, "fileSavedText");
      Intrinsics.checkNotNullParameter(var4, "saveFailedText");
      Intrinsics.checkNotNullParameter(var5, "permissionDeniedText");
      this.showDownloadAction = var1;
      this.fileSavingText = var2;
      this.fileSavedText = var3;
      this.saveFailedText = var4;
      this.permissionDeniedText = var5;
   }

   public final boolean getShowDownloadAction() {
      return this.showDownloadAction;
   }

   @NotNull
   public final String getFileSavingText() {
      return this.fileSavingText;
   }

   @NotNull
   public final String getFileSavedText() {
      return this.fileSavedText;
   }

   @NotNull
   public final String getSaveFailedText() {
      return this.saveFailedText;
   }

   @NotNull
   public final String getPermissionDeniedText() {
      return this.permissionDeniedText;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeInt(this.showDownloadAction ? 1 : 0);
      var1.writeString(this.fileSavingText);
      var1.writeString(this.fileSavedText);
      var1.writeString(this.saveFailedText);
      var1.writeString(this.permissionDeniedText);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   public final boolean component1() {
      return this.showDownloadAction;
   }

   @NotNull
   public final String component2() {
      return this.fileSavingText;
   }

   @NotNull
   public final String component3() {
      return this.fileSavedText;
   }

   @NotNull
   public final String component4() {
      return this.saveFailedText;
   }

   @NotNull
   public final String component5() {
      return this.permissionDeniedText;
   }

   @NotNull
   public final DownloadState copy(boolean var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5) {
      Intrinsics.checkNotNullParameter(var2, "fileSavingText");
      Intrinsics.checkNotNullParameter(var3, "fileSavedText");
      Intrinsics.checkNotNullParameter(var4, "saveFailedText");
      Intrinsics.checkNotNullParameter(var5, "permissionDeniedText");
      return new DownloadState(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static DownloadState copy$default(DownloadState var0, boolean var1, String var2, String var3, String var4, String var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.showDownloadAction;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.fileSavingText;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.fileSavedText;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.saveFailedText;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.permissionDeniedText;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "DownloadState(showDownloadAction="
         + this.showDownloadAction
         + ", fileSavingText="
         + this.fileSavingText
         + ", fileSavedText="
         + this.fileSavedText
         + ", saveFailedText="
         + this.saveFailedText
         + ", permissionDeniedText="
         + this.permissionDeniedText
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.showDownloadAction);
      var1 = var1 * 31 + this.fileSavingText.hashCode();
      var1 = var1 * 31 + this.fileSavedText.hashCode();
      var1 = var1 * 31 + this.saveFailedText.hashCode();
      return var1 * 31 + this.permissionDeniedText.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DownloadState)) {
         return false;
      } else {
         DownloadState var2 = (DownloadState)var1;
         if (this.showDownloadAction != var2.showDownloadAction) {
            return false;
         } else if (!Intrinsics.areEqual(this.fileSavingText, var2.fileSavingText)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fileSavedText, var2.fileSavedText)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.saveFailedText, var2.saveFailedText)
               ? false
               : Intrinsics.areEqual(this.permissionDeniedText, var2.permissionDeniedText);
         }
      }
   }
}
