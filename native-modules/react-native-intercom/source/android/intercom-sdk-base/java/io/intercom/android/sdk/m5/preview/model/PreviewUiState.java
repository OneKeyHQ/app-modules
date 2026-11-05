package io.intercom.android.sdk.m5.preview.model;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\"\b\u0081\b\u0018\u00002\u00020\u0001Bo\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\b\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000b\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000b\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0006HÆ\u0003J\t\u0010!\u001a\u00020\bHÆ\u0003J\t\u0010\"\u001a\u00020\bHÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\t\u0010$\u001a\u00020\bHÆ\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\u000b\u0010'\u001a\u0004\u0018\u00010\u000bHÆ\u0003Jq\u0010(\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\f\u001a\u00020\b2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000bHÇ\u0001J\u0013\u0010)\u001a\u00020\b2\b\u0010*\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010+\u001a\u00020\u0006H×\u0001J\t\u0010,\u001a\u00020\u000bH×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\f\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0017R\u0013\u0010\r\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001aR\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001a¨\u0006-",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/model/PreviewUiState;",
         "",
         "files",
         "",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "currentPage",
         "",
         "showDeleteAction",
         "",
         "showSendAction",
         "confirmationText",
         "",
         "showDownloadAction",
         "fileSavingText",
         "fileSavedText",
         "saveFailedText",
         "<init>",
         "(Ljava/util/List;IZZLjava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getFiles",
         "()Ljava/util/List;",
         "getCurrentPage",
         "()I",
         "getShowDeleteAction",
         "()Z",
         "getShowSendAction",
         "getConfirmationText",
         "()Ljava/lang/String;",
         "getShowDownloadAction",
         "getFileSavingText",
         "getFileSavedText",
         "getSaveFailedText",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class PreviewUiState {
   @NotNull
   private final List files;
   private final int currentPage;
   private final boolean showDeleteAction;
   private final boolean showSendAction;
   @Nullable
   private final String confirmationText;
   private final boolean showDownloadAction;
   @Nullable
   private final String fileSavingText;
   @Nullable
   private final String fileSavedText;
   @Nullable
   private final String saveFailedText;
   public static final int $stable;

   static { $stable = 8; }

   public PreviewUiState(
      @NotNull List var1,
      int var2,
      boolean var3,
      boolean var4,
      @Nullable String var5,
      boolean var6,
      @Nullable String var7,
      @Nullable String var8,
      @Nullable String var9
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "files");
      this.files = var1;
      this.currentPage = var2;
      this.showDeleteAction = var3;
      this.showSendAction = var4;
      this.confirmationText = var5;
      this.showDownloadAction = var6;
      this.fileSavingText = var7;
      this.fileSavedText = var8;
      this.saveFailedText = var9;
   }

   // $VF: synthetic method
   public PreviewUiState(
      List var1,
      int var2,
      boolean var3,
      boolean var4,
      String var5,
      boolean var6,
      String var7,
      String var8,
      String var9,
      int var10,
      DefaultConstructorMarker var11
   ) {
      this((((var10 & 1) != 0) ? (List) (CollectionsKt.emptyList()) : var1), (((var10 & 2) != 0) ? (int) (0) : var2), (((var10 & 4) != 0) ? (boolean) (false) : var3), (((var10 & 8) != 0) ? (boolean) (false) : var4), (((var10 & 16) != 0) ? (String) (null) : var5), (((var10 & 32) != 0) ? (boolean) (false) : var6), (((var10 & 64) != 0) ? (String) (null) : var7), (((var10 & 128) != 0) ? (String) (null) : var8), (((var10 & 256) != 0) ? (String) (null) : var9));
   }

   @NotNull
   public final List getFiles() {
      return this.files;
   }

   public final int getCurrentPage() {
      return this.currentPage;
   }

   public final boolean getShowDeleteAction() {
      return this.showDeleteAction;
   }

   public final boolean getShowSendAction() {
      return this.showSendAction;
   }

   @Nullable
   public final String getConfirmationText() {
      return this.confirmationText;
   }

   public final boolean getShowDownloadAction() {
      return this.showDownloadAction;
   }

   @Nullable
   public final String getFileSavingText() {
      return this.fileSavingText;
   }

   @Nullable
   public final String getFileSavedText() {
      return this.fileSavedText;
   }

   @Nullable
   public final String getSaveFailedText() {
      return this.saveFailedText;
   }

   @NotNull
   public final List component1() {
      return this.files;
   }

   public final int component2() {
      return this.currentPage;
   }

   public final boolean component3() {
      return this.showDeleteAction;
   }

   public final boolean component4() {
      return this.showSendAction;
   }

   @Nullable
   public final String component5() {
      return this.confirmationText;
   }

   public final boolean component6() {
      return this.showDownloadAction;
   }

   @Nullable
   public final String component7() {
      return this.fileSavingText;
   }

   @Nullable
   public final String component8() {
      return this.fileSavedText;
   }

   @Nullable
   public final String component9() {
      return this.saveFailedText;
   }

   @NotNull
   public final PreviewUiState copy(
      @NotNull List var1,
      int var2,
      boolean var3,
      boolean var4,
      @Nullable String var5,
      boolean var6,
      @Nullable String var7,
      @Nullable String var8,
      @Nullable String var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "files");
      return new PreviewUiState(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   // $VF: synthetic method
   public static PreviewUiState copy$default(
      PreviewUiState var0,
      List var1,
      int var2,
      boolean var3,
      boolean var4,
      String var5,
      boolean var6,
      String var7,
      String var8,
      String var9,
      int var10,
      Object var11
   ) {
      if ((var10 & 1) != 0) {
         var1 = var0.files;
      }

      if ((var10 & 2) != 0) {
         var2 = var0.currentPage;
      }

      if ((var10 & 4) != 0) {
         var3 = var0.showDeleteAction;
      }

      if ((var10 & 8) != 0) {
         var4 = var0.showSendAction;
      }

      if ((var10 & 16) != 0) {
         var5 = var0.confirmationText;
      }

      if ((var10 & 32) != 0) {
         var6 = var0.showDownloadAction;
      }

      if ((var10 & 64) != 0) {
         var7 = var0.fileSavingText;
      }

      if ((var10 & 128) != 0) {
         var8 = var0.fileSavedText;
      }

      if ((var10 & 256) != 0) {
         var9 = var0.saveFailedText;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @NotNull
   @Override
   public String toString() {
      return "PreviewUiState(files="
         + this.files
         + ", currentPage="
         + this.currentPage
         + ", showDeleteAction="
         + this.showDeleteAction
         + ", showSendAction="
         + this.showSendAction
         + ", confirmationText="
         + this.confirmationText
         + ", showDownloadAction="
         + this.showDownloadAction
         + ", fileSavingText="
         + this.fileSavingText
         + ", fileSavedText="
         + this.fileSavedText
         + ", saveFailedText="
         + this.saveFailedText
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.files.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.currentPage);
      var1 = var1 * 31 + Boolean.hashCode(this.showDeleteAction);
      var1 = var1 * 31 + Boolean.hashCode(this.showSendAction);
      var1 = var1 * 31 + (this.confirmationText == null ? 0 : this.confirmationText.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.showDownloadAction);
      var1 = var1 * 31 + (this.fileSavingText == null ? 0 : this.fileSavingText.hashCode());
      var1 = var1 * 31 + (this.fileSavedText == null ? 0 : this.fileSavedText.hashCode());
      return var1 * 31 + (this.saveFailedText == null ? 0 : this.saveFailedText.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PreviewUiState)) {
         return false;
      } else {
         PreviewUiState var2 = (PreviewUiState)var1;
         if (!Intrinsics.areEqual(this.files, var2.files)) {
            return false;
         } else if (this.currentPage != var2.currentPage) {
            return false;
         } else if (this.showDeleteAction != var2.showDeleteAction) {
            return false;
         } else if (this.showSendAction != var2.showSendAction) {
            return false;
         } else if (!Intrinsics.areEqual(this.confirmationText, var2.confirmationText)) {
            return false;
         } else if (this.showDownloadAction != var2.showDownloadAction) {
            return false;
         } else if (!Intrinsics.areEqual(this.fileSavingText, var2.fileSavingText)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.fileSavedText, var2.fileSavedText) ? false : Intrinsics.areEqual(this.saveFailedText, var2.saveFailedText);
         }
      }
   }

   public PreviewUiState() {
      this(null, 0, false, false, null, false, null, null, null, 511, null);
   }
}
