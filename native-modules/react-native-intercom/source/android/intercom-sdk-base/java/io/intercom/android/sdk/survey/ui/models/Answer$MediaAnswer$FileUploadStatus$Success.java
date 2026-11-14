package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH×\u0003J\t\u0010\u000e\u001a\u00020\u000fH×\u0001J\t\u0010\u0010\u001a\u00020\u0011H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus$Success;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus;",
         "id",
         "",
         "<init>",
         "(J)V",
         "getId",
         "()J",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Answer$MediaAnswer$FileUploadStatus$Success extends Answer$MediaAnswer$FileUploadStatus {
   private final long id;
   public static int $stable;

   static {}

   public Answer$MediaAnswer$FileUploadStatus$Success(long var1) {
      super(null);
      this.id = var1;
   }

   public final long getId() {
      return this.id;
   }

   public final long component1() {
      return this.id;
   }

   @NotNull
   public final Answer$MediaAnswer$FileUploadStatus$Success copy(long var1) {
      return new Answer$MediaAnswer$FileUploadStatus$Success(var1);
   }

   // $VF: synthetic method
   public static Answer$MediaAnswer$FileUploadStatus$Success copy$default(Answer$MediaAnswer$FileUploadStatus$Success var0, long var1, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.id;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Success(id=" + this.id + ')';
   }

   @Override
   public int hashCode() {
      return Long.hashCode(this.id);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Answer$MediaAnswer$FileUploadStatus$Success)) {
         return false;
      }

      Answer$MediaAnswer$FileUploadStatus$Success var2 = (Answer$MediaAnswer$FileUploadStatus$Success)var1;
      return this.id == var2.id;
   }
}
