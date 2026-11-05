package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\tJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\u001fH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R+\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\t8B@BX\u0082\u008e\u0002¢\u0006\u0012\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001b\u0010\u0011\u001a\u00020\t8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0012\u0010\f¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "",
         "data",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;)V",
         "getData",
         "()Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "<set-?>",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus;",
         "_uploadStatus",
         "get_uploadStatus",
         "()Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus;",
         "set_uploadStatus",
         "(Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus;)V",
         "_uploadStatus$delegate",
         "Landroidx/compose/runtime/MutableState;",
         "uploadStatus",
         "getUploadStatus",
         "uploadStatus$delegate",
         "Landroidx/compose/runtime/State;",
         "setUploadStatus",
         "",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nAnswer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Answer.kt\nio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem\n+ 2 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,143:1\n85#2:144\n117#2,2:145\n85#2:147\n*S KotlinDebug\n*F\n+ 1 Answer.kt\nio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem\n*L\n106#1:144\n106#1:145,2\n107#1:147\n*E\n"
)
public final class Answer$MediaAnswer$MediaItem {
   @NotNull
   private final MediaData$Media data;
   @NotNull
   private final MutableState _uploadStatus$delegate;
   @NotNull
   private final State uploadStatus$delegate;
   public static final int $stable;

   static { $stable = 8; }

   public Answer$MediaAnswer$MediaItem(@NotNull MediaData$Media var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "data");
      this.data = var1;
      this._uploadStatus$delegate = SnapshotStateKt.mutableStateOf$default(Answer$MediaAnswer$FileUploadStatus$None.INSTANCE, null, 2, null);
      final io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem recoveredExpressionCapture_1370_0 = this;
      this.uploadStatus$delegate = SnapshotStateKt.derivedStateOf(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem.uploadStatus_delegate$lambda$0(recoveredExpressionCapture_1370_0)));
   }

   @NotNull
   public final MediaData$Media getData() {
      return this.data;
   }

   private final Answer$MediaAnswer$FileUploadStatus get_uploadStatus() {
      State var1 = this._uploadStatus$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Answer$MediaAnswer$FileUploadStatus)var1.getValue();
   }

   private final void set_uploadStatus(Answer$MediaAnswer$FileUploadStatus var1) {
      MutableState var2 = this._uploadStatus$delegate;
      KProperty var3 = null;
      int var4 = 0;
      var2.setValue(var1);
   }

   @NotNull
   public final Answer$MediaAnswer$FileUploadStatus getUploadStatus() {
      State var1 = this.uploadStatus$delegate;
      KProperty var2 = null;
      int var3 = 0;
      return (Answer$MediaAnswer$FileUploadStatus)var1.getValue();
   }

   public final void setUploadStatus(@NotNull Answer$MediaAnswer$FileUploadStatus var1) {
      Intrinsics.checkNotNullParameter(var1, "uploadStatus");
      this.set_uploadStatus(var1);
   }

   @NotNull
   public final MediaData$Media component1() {
      return this.data;
   }

   @NotNull
   public final Answer$MediaAnswer$MediaItem copy(@NotNull MediaData$Media var1) {
      Intrinsics.checkNotNullParameter(var1, "data");
      return new Answer$MediaAnswer$MediaItem(var1);
   }

   // $VF: synthetic method
   public static Answer$MediaAnswer$MediaItem copy$default(Answer$MediaAnswer$MediaItem var0, MediaData$Media var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.data;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "MediaItem(data=" + this.data + ')';
   }

   @Override
   public int hashCode() {
      return this.data.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Answer$MediaAnswer$MediaItem)) {
         return false;
      }

      Answer$MediaAnswer$MediaItem var2 = (Answer$MediaAnswer$MediaItem)var1;
      return Intrinsics.areEqual(this.data, var2.data);
   }

   private static final Answer$MediaAnswer$FileUploadStatus uploadStatus_delegate$lambda$0(Answer$MediaAnswer$MediaItem var0) {
      return var0.get_uploadStatus();
   }
}
