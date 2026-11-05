package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BK\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\bHÆ\u0003J\t\u0010 \u001a\u00020\nHÆ\u0003J\t\u0010!\u001a\u00020\fHÆ\u0003J\t\u0010\"\u001a\u00020\u000eHÆ\u0003JM\u0010#\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eHÇ\u0001J\b\u0010$\u001a\u00020\fH\u0007J\u0013\u0010%\u001a\u00020\n2\b\u0010&\u001a\u0004\u0018\u00010'H×\u0003J\t\u0010(\u001a\u00020\fH×\u0001J\t\u0010)\u001a\u00020\bH×\u0001J\u0018\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020\fH\u0007R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001c¨\u0006/",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "Landroid/os/Parcelable;",
         "uris",
         "",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "deleteType",
         "Lio/intercom/android/sdk/m5/preview/data/DeleteType;",
         "ctaText",
         "",
         "showSendCTA",
         "",
         "initialIndex",
         "",
         "downloadState",
         "Lio/intercom/android/sdk/m5/preview/data/DownloadState;",
         "<init>",
         "(Ljava/util/List;Lio/intercom/android/sdk/m5/preview/data/DeleteType;Ljava/lang/String;ZILio/intercom/android/sdk/m5/preview/data/DownloadState;)V",
         "getUris",
         "()Ljava/util/List;",
         "getDeleteType",
         "()Lio/intercom/android/sdk/m5/preview/data/DeleteType;",
         "getCtaText",
         "()Ljava/lang/String;",
         "getShowSendCTA",
         "()Z",
         "getInitialIndex",
         "()I",
         "getDownloadState",
         "()Lio/intercom/android/sdk/m5/preview/data/DownloadState;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "describeContents",
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
@StabilityInferred(parameters = 0)
public final class IntercomPreviewArgs implements Parcelable {
   @NotNull
   private final List uris;
   @NotNull
   private final DeleteType deleteType;
   @Nullable
   private final String ctaText;
   private final boolean showSendCTA;
   private final int initialIndex;
   @NotNull
   private final DownloadState downloadState;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new IntercomPreviewArgs$Creator();

   public IntercomPreviewArgs(@NotNull List var1, @NotNull DeleteType var2, @Nullable String var3, boolean var4, int var5, @NotNull DownloadState var6) {
      super();
      Intrinsics.checkNotNullParameter(var1, "uris");
      Intrinsics.checkNotNullParameter(var2, "deleteType");
      Intrinsics.checkNotNullParameter(var6, "downloadState");
      this.uris = var1;
      this.deleteType = var2;
      this.ctaText = var3;
      this.showSendCTA = var4;
      this.initialIndex = var5;
      this.downloadState = var6;
   }

   // $VF: synthetic method
   public IntercomPreviewArgs(List var1, DeleteType var2, String var3, boolean var4, int var5, DownloadState var6, int var7, DefaultConstructorMarker var8) {
      this((((var7 & 1) != 0) ? (List) (CollectionsKt.emptyList()) : var1), (((var7 & 2) != 0) ? (DeleteType) (DeleteType$None.INSTANCE) : var2), (((var7 & 4) != 0) ? (String) (null) : var3), (((var7 & 8) != 0) ? (boolean) (false) : var4), (((var7 & 16) != 0) ? (int) (0) : var5), (((var7 & 32) != 0) ? (DownloadState) (new DownloadState(false, "Saving...", "File saved", "Something went wrong", "Permission denied")) : var6));
   }

   @NotNull
   public final List getUris() {
      return this.uris;
   }

   @NotNull
   public final DeleteType getDeleteType() {
      return this.deleteType;
   }

   @Nullable
   public final String getCtaText() {
      return this.ctaText;
   }

   public final boolean getShowSendCTA() {
      return this.showSendCTA;
   }

   public final int getInitialIndex() {
      return this.initialIndex;
   }

   @NotNull
   public final DownloadState getDownloadState() {
      return this.downloadState;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      List var3 = this.uris;
      var1.writeInt(var3.size());
      Iterator var4 = var3.iterator();

      while (var4.hasNext()) {
         var1.writeParcelable((Parcelable)var4.next(), var2);
      }

      var1.writeParcelable(this.deleteType, var2);
      var1.writeString(this.ctaText);
      var1.writeInt(this.showSendCTA ? 1 : 0);
      var1.writeInt(this.initialIndex);
      this.downloadState.writeToParcel(var1, var2);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final List component1() {
      return this.uris;
   }

   @NotNull
   public final DeleteType component2() {
      return this.deleteType;
   }

   @Nullable
   public final String component3() {
      return this.ctaText;
   }

   public final boolean component4() {
      return this.showSendCTA;
   }

   public final int component5() {
      return this.initialIndex;
   }

   @NotNull
   public final DownloadState component6() {
      return this.downloadState;
   }

   @NotNull
   public final IntercomPreviewArgs copy(
      @NotNull List var1, @NotNull DeleteType var2, @Nullable String var3, boolean var4, int var5, @NotNull DownloadState var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "uris");
      Intrinsics.checkNotNullParameter(var2, "deleteType");
      Intrinsics.checkNotNullParameter(var6, "downloadState");
      return new IntercomPreviewArgs(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static IntercomPreviewArgs copy$default(
      IntercomPreviewArgs var0, List var1, DeleteType var2, String var3, boolean var4, int var5, DownloadState var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.uris;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.deleteType;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.ctaText;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.showSendCTA;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.initialIndex;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.downloadState;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "IntercomPreviewArgs(uris="
         + this.uris
         + ", deleteType="
         + this.deleteType
         + ", ctaText="
         + this.ctaText
         + ", showSendCTA="
         + this.showSendCTA
         + ", initialIndex="
         + this.initialIndex
         + ", downloadState="
         + this.downloadState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.uris.hashCode();
      var1 = var1 * 31 + this.deleteType.hashCode();
      var1 = var1 * 31 + (this.ctaText == null ? 0 : this.ctaText.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.showSendCTA);
      var1 = var1 * 31 + Integer.hashCode(this.initialIndex);
      return var1 * 31 + this.downloadState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomPreviewArgs)) {
         return false;
      } else {
         IntercomPreviewArgs var2 = (IntercomPreviewArgs)var1;
         if (!Intrinsics.areEqual(this.uris, var2.uris)) {
            return false;
         } else if (!Intrinsics.areEqual(this.deleteType, var2.deleteType)) {
            return false;
         } else if (!Intrinsics.areEqual(this.ctaText, var2.ctaText)) {
            return false;
         } else if (this.showSendCTA != var2.showSendCTA) {
            return false;
         } else {
            return this.initialIndex != var2.initialIndex ? false : Intrinsics.areEqual(this.downloadState, var2.downloadState);
         }
      }
   }

   public IntercomPreviewArgs() {
      this(null, null, null, false, 0, null, 63, null);
   }
}
